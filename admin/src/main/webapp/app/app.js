'use strict';
/**
 * @ngdoc overview
 * @name sbAdminApp
 * @description
 * # sbAdminApp
 *
 * Main module of the application.
 */
angular
    .module('sbAdminApp.services', ['ngResource', 'ui.bootstrap', 'ui.bootstrap.datetimepicker', 'ngSanitize', 'wt.responsive', 'ui.tree'])

    .factory('UserService', ['$resource', function ($resource) {
        return $resource('/admin/api/admin-user/me', {}, {
            'profile': {
                method: 'GET'
            }
        });
    }])
    .factory('AlertService', function ($rootScope) {
        var alertService = {};

        //创建一个全局的alert数组
        $rootScope.alerts = [];

        alertService.add = function (type, msg) {
            $rootScope.alerts.push({
                'type': type, 'msg': msg, 'close': function () {
                    alertService.closeAlert(this);
                }
            });
        };

        alertService.closeAlert = function (alert) {
            alertService.closeAlertIndex($rootScope.alerts.indexOf(alert));
        };

        alertService.closeAlertIndex = function (index) {
            $rootScope.alerts.splice(index, 1);
        };

        return alertService;
    }).factory('AlertErrorMessage',function($window){
        var alertS={};
        alertS.alert=function(data,defaultMsg){
            if(data!=null){
                if(data.errmsg!=null && $.trim(data.errmsg).length!=0){
                    $window.alert(data.errmsg);
                    return ;
                }
            }
            $window.alert(data.errmsg);
        }
        return alertS;
    });

angular
    .module('sbAdminApp', [
        'sbAdminApp.services',
        'oc.lazyLoad',
        'ui.router',
        'ui.bootstrap',
        'angular-loading-bar',
        'checklist-model',
        'angularFileUpload',
        'ui.select',
        'xeditable',
        'ui.map',
        'ngMessages',
        'ngJsTree',
        'wt.responsive',
        'templatesCache',
        'ngclipboard',
        'chart.js'
    ])
    .config(['$stateProvider', '$urlRouterProvider', '$ocLazyLoadProvider', '$locationProvider', '$httpProvider', '$provide', 'ChartJsProvider',
        function ($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, $locationProvider, $httpProvider, $provide, ChartJsProvider) {

            // Configure all charts
            ChartJsProvider.setOptions({
                colours: ['#FF5252'],
                responsive: false,
                animation: true,
                animationSteps: 60
            });
            // Configure all line charts
            ChartJsProvider.setOptions('Line', {
                datasetFill: false,
                datasetStroke : true,
                datasetStrokeWidth : 2,
            });

            $ocLazyLoadProvider.config({
                debug: false,
                events: true
            });

            $urlRouterProvider.otherwise('/oam/home');

            /* Register error provider that shows message on failed requests or redirects to login page on
             * unauthenticated requests */
            $httpProvider.interceptors.push(function ($q, $rootScope, $location) {

                var numLoadings = 0;
                return {
                    'request': function (config) {
                        var showLoader = true;
                        if (config.method == 'GET') {
                            if (config.params && config.params.showLoader == false) {
                                showLoader = false;
                            }
                        }

                        numLoadings++;
                        if (showLoader == true) {
                            $rootScope.$broadcast("loader_show");
                        }
                        return config;
                    },

                    'response': function (response) {
                        if ((--numLoadings) === 0) {
                            $rootScope.$broadcast("loader_hide");
                        }
                        return response;
                    },

                    'responseError': function (rejection) {
                        if (!(--numLoadings)) {
                            $rootScope.$broadcast("loader_hide");
                        }
                        var status = rejection.status;
                        var config = rejection.config;
                        var method = config.method;
                        var url = config.url;

                        if (status == 401) {
                            $location.path("/login");
                        } else {
                            $rootScope.error = method + " on " + url + " failed with status " + status;
                        }

                        return $q.reject(rejection);
                    }
                };
            });

            $stateProvider
                .state('oam', {
                    url: '/oam',
                    templateUrl: 'app/dashboard/main.html',
                    resolve: {
                        loadMyDirectives: function ($ocLazyLoad) {
                            return [
                                    'app/directives/header/header.js',
                                    'app/directives/header/header-notification/header-notification.js',
                                    'app/directives/sidebar/sidebar.js',
                                    'app/directives/history/back.js'
                                ],
                                $ocLazyLoad.load({
                                    name: 'toggle-switch',
                                    files: ["bower_components/angular-toggle-switch/angular-toggle-switch.min.js",
                                        "bower_components/angular-toggle-switch/angular-toggle-switch.css"
                                    ]
                                }),
                                $ocLazyLoad.load({
                                    name: 'ngAnimate',
                                    files: ['bower_components/angular-animate/angular-animate.js']
                                }),
                                $ocLazyLoad.load({
                                    name: 'ngCookies',
                                    files: ['bower_components/angular-cookies/angular-cookies.js']
                                }),
                                $ocLazyLoad.load({
                                    name: 'ngResource',
                                    files: ['bower_components/angular-animate/angular-animate.js']
                                }),
                                $ocLazyLoad.load({
                                    name: 'ngSanitize',
                                    files: ['bower_components/angular-sanitize/angular-sanitize.js']
                                })
                        }
                    }
                })
                .state('oam.home', {
                    url: '/home',
                    templateUrl: 'app/dashboard/home.html',
                    controller: 'HomeCtrl'
                })
                .state('oam.help-document', {
                    url: '/help-document',
                    templateUrl: 'app/dashboard/help-document.html'
                })
                .state('login', {
                    templateUrl: 'app/pages/login.html',
                    controller: 'LoginCtrl',
                    url: '/login',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/pages/login.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addressBook', {
                    templateUrl: 'app/admin-management/address-book-list.html',
                    url: '/addressBook?page&pageSize&username&realname&telephone',
                    controller: 'AddressBookCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/address-book-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateGlobalAdmin', {
                    templateUrl: 'app/admin-management/global-admin-detail.html',
                    url: '/update-global-admin/{id}',
                    controller: 'AddGlobalStaffCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/global-admin-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.manageRole', {
                    templateUrl: 'app/admin-management/role-detail.html',
                    url: '/role-detail',
                    controller: 'RoleCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/role-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.manageGlobalStaff', {
                    templateUrl: 'app/admin-management/global-admin-list.html',
                    url: '/global-admin-list?cityId&organizationId&page&pageSize&username&realname&telephone&isEnabled',
                    controller: 'ListAllGlobalAdminCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/global-admin-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateAdminPassword', {
                    templateUrl: 'app/admin-management/admin-updatePassword.html',
                    url: '/admin-updatePassword',
                    controller: 'updateAdminPassCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/admin-updatePassword.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.performance', {
                    templateUrl: 'app/admin-management/customer-service-performance.html',
                    url: '/performance',
                    controller: 'PerformanceCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/customer-service-performance.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.salesman', {
                    templateUrl: 'app/admin-management/salesman-statistics.html',
                    url: '/salesman/?start&end',
                    controller: 'SalesmanCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/salesman-statistics.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updatePassword', {
                    templateUrl: 'app/pages/update-password.html',
                    url: '/update-password',
                    controller: 'UpdatePwdCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/pages/update-password.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.restaurantList', {
                    templateUrl: 'app/restaurant-management/restaurant/restaurant-list.html',
                    url: '/restaurant-list/?page&pageSize&zoneId&adminUserId&telephone&blankTime&name&status&start&end&warehouseId&registPhone&id&cityId&organizationId&grade&warning&neverOrder&sortField&{asc:bool}',
                    controller: 'RestaurantManagementCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/restaurant/restaurant-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.obtainToken', {

                    templateUrl: 'app/obtainToken-management/token/token.html',
                    url: '/token?username',
                    controller: 'TokenManagementCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/obtainToken-management/token/token.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.editRestaurant', {
                    templateUrl: 'app/restaurant-management/restaurant/restaurant-detail.html',
                    url: '/edit-restaurant/?id&blockId',
                    controller: 'EditRestaurantCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/restaurant/restaurant-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.alarmRestaurant', {
                    templateUrl: 'app/restaurant-management/restaurant/restaurant-alarm-list.html',
                    url: '/alarm-restaurant/?orderDate',
                    controller: 'AlarmRestaurantCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/restaurant/restaurant-alarm-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.changeService', {
                    templateUrl: 'app/admin-management/change-service.html',
                    url: '/change-service',
                    controller: 'ChangeServiceCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/change-service.js'
                                ]
                            })
                        }
                    }
                }).state('oam.zoneList', {
                    templateUrl: 'app/restaurant-management/zone/zone-list.html',
                    url: '/zone-list',
                    controller: 'zoneListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/zone/zone-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.salesDistribute', {
                    templateUrl: 'app/restaurant-management/sales-distribute.html',
                    url: '/sales-distribute/{id}',
                    controller: 'SalesDistributeCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/sales-distribute.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateCustomerPassword', {
                    templateUrl: 'app/restaurant-management/customer-updatePassword.html',
                    url: '/customer-updatePassword/',
                    controller: 'updateCustomerPassCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/customer-updatePassword.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.orderList', {
                    templateUrl: 'app/order-management/order/order-list.html',
                    url: '/order-list/?page&pageSize&start&end&restaurantId&restaurantName&status&adminId&warehouseId&vendorName&cityId&organizationId&orderId&sortField&{asc:bool}&coordinateLabeled&refundsIsNotEmpty&orderType&spikeItemId',
                    controller: 'ListOrdersCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/order-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-detail', {
                    templateUrl: 'app/order-management/order/order-detail.html',
                    url: '/order-detail/?id',
                    controller: 'OrderDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/order-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-info', {
                    templateUrl: 'app/order-management/order/order-info.html',
                    url: '/order-info/?id',
                    controller: 'OrderInfoCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/order-info.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stop-order', {
                    templateUrl: 'app/erp/stop-order/stop-order.html',
                    url: '/stop-order?page&pageSize&start&end&restaurantId&restaurantName&status&adminId&warehouseId&vendorName&cityId&organizationId&orderId&sortField&{asc:bool}&coordinateLabeled&refundsIsNotEmpty&depotId&blockId&orderType',
                    controller: 'StopOrdersCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stop-order/stop-order.html'
                                ]
                            })
                        }
                    }
                })
                .state('oam.add-order', {
                    templateUrl: 'app/order-management/order/add-order.html',
                    url: '/add-order',
                    controller: 'AddOrderCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/add-order.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellReturn-list', {
                    templateUrl: 'app/erp/sellReturn-management/sellReturn-check.html',
                    url: '/sellReturn-list?pageType&type&cityId&organizationId&depotId&restaurantId&restaurantName&status&startDate&endDate&startReturnDate&endReturnDate&page&pageSize',
                    controller: 'SellReturnCheckListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellReturn-check.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellReturn-detail', {
                    templateUrl: 'app/erp/sellReturn-management/sellReturn-detail.html',
                    url: '/sellReturn-detail/?id',
                    controller: 'SellReturnDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellReturn-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellReturnItem-list', {
                    templateUrl: 'app/erp/sellReturn-management/sellReturnItem-list.html',
                    url: '/sellReturnItem-list?cityId&organizationId&depotId&type&status&orderId&restaurantId&restaurantName&skuId&skuName&startDate&endDate&startReturnDate&endReturnDate&page&pageSize',
                    controller: 'SellReturnItemListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellReturnItem-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellCancel-list', {
                    templateUrl: 'app/erp/sellReturn-management/sellCancel-list.html',
                    url: '/sellCancel-list?cityId&organizationId&depotId&orderId&restaurantId&restaurantName&type&startDate&endDate&startCancelDate&endCancelDate&page&pageSize',
                    controller: 'SellCancelListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellCancel-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellCancel-detail', {
                    templateUrl: 'app/erp/sellReturn-management/sellCancel-detail.html',
                    url: '/sellCancel-detail/?id',
                    controller: 'SellCancelDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellCancel-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellCancelItem-list', {
                    templateUrl: 'app/erp/sellReturn-management/sellCancelItem-list.html',
                    url: '/sellCancelItem-list?cityId&organizationId&depotId&orderId&restaurantId&restaurantName&skuId&skuName&type&startDate&endDate&startCancelDate&endCancelDate&page&pageSize',
                    controller: 'SellCancelItemListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/sellReturn-management/sellCancelItem-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.cancel-order-reason', {
                    templateUrl: 'app/order-management/cancel-order-reason/cancel-order-reason.html',
                    url: '/cancel-order-reason/{orderId}',
                    controller: 'AddcancelOrderReasonCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/cancel-order-reason/cancel-order-reason.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addNotes', {
                    templateUrl: 'app/order-management/add-memo.html',
                    url: '/add-memo/{id}',
                    controller: 'AddMemoCtrl',
                    resolve: {
                        loadMyFeiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/add-memo.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-item-list', {
                    templateUrl: 'app/order-management/order/order-item-list.html',
                    url: '/order-detail-list/?start&end&page&pageSize&startDate&endDate&restaurantId&skuId&orderStatus&productName&restaurantName&warehouseId&orderId&cityId&organizationId&orderType',
                    controller: 'OrderItemsListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/order-item-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-group-list', {
                    templateUrl: 'app/order-management/order-group/order-group-list.html',
                    url: '/order-group-list?cityId&depotId&startOrderDate&endOrderDate&trackerId',
                    controller: 'OrderGroupListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/order-group-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-group-detail', {
                    templateUrl: 'app/order-management/order-group/order-group-detail.html',
                    url: '/order-group-detail/:id?type',
                    controller: 'OrderGroupDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/order-group-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-group-view', {
                    templateUrl: 'app/order-management/order-group/order-group-view.html',
                    url: '/order-group-view/:id',
                    controller: 'OrderGroupViewCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/order-group-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('order-group-map', {
                    templateUrl: 'app/order-management/order-group/order-group-map.html',
                    url: '/order-group-map/?type&cityId&id',
                    controller: 'OrderGroupMapCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/order-group-map.js'
                                ]
                            })
                        }
                    }
                })
                .state('stop-order-map', {
                    templateUrl: 'app/order-management/order-group/order-group-map.html',
                    url: '/stop-order-map?type&start&end&restaurantId&restaurantName&status&adminId&warehouseId&vendorName&cityId&organizationId&orderId&sortField&{asc:bool}&coordinateLabeled&refundsIsNotEmpty&depotId&blockId&orderType',
                    controller: 'OrderGroupMapCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/order-group-map.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.car-list', {
                    templateUrl: 'app/order-management/order-group/car-list.html',
                    url: '/car-list/',
                    controller: 'CarListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/car-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.carRoute-list', {
                    templateUrl: 'app/carRoute-management/carRoute-list.html',
                    url: '/route-list?page&pageSize&name&price&cityId&depotId',
                    controller: 'CarRouteListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/carRoute-management/carRoute-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.carRoute-detail',{
                    templateUrl:'app/carRoute-management/carRoute-detail.html',
                    url:'/carRoute-detail/{id}',
                    controller:'CarRouteDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/carRoute-management/carRoute-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.car-detail', {
                    templateUrl: 'app/order-management/order-group/car-detail.html',
                    url: '/car-detail/?id',
                    controller: 'CarDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order-group/car-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.category-list', {
                    templateUrl: 'app/content-management/category/category-list.html',
                    url: '/category-list',
                    controller: 'CategoryListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/category/category-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.category-detail', {
                    templateUrl: 'app/content-management/category/category-detail.html',
                    url: '/category-detail/{id}',
                    controller: 'CategoryDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/category/category-detail.js'
                                ]
                            })
                        }
                    }
                })

                .state('oam.restaurant-type-list', {
                    templateUrl: 'app/restaurant-management/restaurant/restaurant-type-list.html',
                    url: '/restaurant-type-list',
                    controller: 'RestaurantTypeListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/restaurant/restaurant-type-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.restaurant-type-detail', {
                    templateUrl: 'app/restaurant-management/restaurant/restaurant-type-detail.html',
                    url: '/restaurant-type-detail/{id}',
                    controller: 'RestaurantTypeDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/restaurant/restaurant-type-detail.js'
                                ]
                            })
                        }
                    }
                })
                
                .state('oam.product-list', {
                    templateUrl: 'app/content-management/product/product-list.html',
                    url: '/product-list/?page&pageSize&productId&skuId&productName&brandId&categoryId&cityId&organizationId&status',
                    controller: 'ProductListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/product-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.product-list-temp', {
                    templateUrl: 'app/content-management/product/product-list-temp.html',
                    url: '/product-list-temp/?page&pageSize&cityId&organizationId&status&productName&submitRealName&checkRealName&submitDate&passDate',
                    controller: 'ProductListTempCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/product-list-temp.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.product-detail', {
                    templateUrl: 'app/content-management/product/product-detail.html',
                    url: '/product-detail/{id}',
                    controller: 'ProductDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/product-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.photo-upload-tool', {
                    templateUrl: 'app/content-management/product/photo-upload-tool.html',
                    url: '/photo-upload-tool',
                    controller: 'photo-upload-tool'
                })
                .state('oam.product-detail-temp', {
                    templateUrl: 'app/content-management/product/product-detail-temp.html',
                    url: '/product-detail-temp/{id}',
                    controller: 'ProductDetailTempCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/product-detail-temp.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-tag-list', {
                    templateUrl: 'app/content-management/product/sku-tag-list.html',
                    url: '/sku-tag-list/?page&pageSize&productId&skuId&productName&brandId&categoryId&skuTagCityId&organizationId&status',
                    controller: 'SkuTagListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/sku-tag-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-tag-detail', {
                    templateUrl: 'app/content-management/product/sku-tag-detail.html',
                    url: '/sku-tag-detail/?:id',
                    controller: 'SkuTagDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/product/sku-tag-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-list', {
                    templateUrl: 'app/content-management/sku/sku-list.html',
                    url: '/sku-list/?type&categoryId&cityId&organizationId&warehouseId&vendorId&status&productId&skuId&productName&page&pageSize',
                    controller: 'SkuListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/sku/sku-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-price-history-list', {
                    templateUrl: 'app/content-management/sku/sku-price-history-list.html',
                    url: '/sku-price-history-list/?page&pageSize&startDate&endDate&skuId&type&single&cityId',
                    controller: 'SkuPriceHistoryList',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/sku/sku-price-history-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.excel-export-task-list', {
                    templateUrl: 'app/export-management/excels/excel-export-task-list.html',
                    url: '/excel-export-task-list/?page&pageSize',
                    controller: 'ExcelExportTaskList',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/export-management/excels/excel-export-task-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.dynamic-price-list', {
                    templateUrl: 'app/content-management/dynamic-price/dynamic-price-list.html',
                    url: '/dynamic-price-list/?skuId&page&pageSize&productId&productName&warehouseId&brandId&cityId&organizationId&categoryId&status&singleAvailable&singleInSale&bundleAvailable&bundleInSale&skuCreateDate',
                    controller: 'DynamicPriceListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/dynamic-price/dynamic-price-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-vendor-list', {
                    templateUrl: 'app/content-management/sku/sku-vendor-list.html',
                    url: '/sku-vendor-list?page&pageSize&cityId&organizationId&vendorId&status&brandId&productName&categoryId',
                    controller: 'SkuVendorListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/sku/sku-vendor-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-vendor-detail', {
                    templateUrl: 'app/content-management/sku/sku-vendor-detail.html',
                    url: '/sku-vendor-detail/{id}?cityId&skuId',
                    controller: 'SkuVendorDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/sku/sku-vendor-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.dynamic-price-list-temp', {
                    templateUrl: 'app/content-management/dynamic-price/dynamic-price-list-temp.html',
                    url: '/dynamic-price-list-temp/?skuId&page&pageSize&cityId&warehouseId&organizationId&status&skuName&checkRealName&submitRealName&submitDate&passDate',
                    controller: 'DynamicPriceListTempCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/dynamic-price/dynamic-price-list-temp.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.dynamic-price-detail', {
                    templateUrl: 'app/content-management/dynamic-price/dynamic-price-detail.html',
                    url: '/dynamic-price-detail/:id?skuId&warehouseId&organizationId',
                    controller: 'DynamicPriceDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/content-management/dynamic-price/dynamic-price-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.brandsManagement', {
                    templateUrl: 'app/partners-management/brand/brand-list.html',
                    url: '/brand-list?brandId&brandName&status&page&pageSize',
                    controller: 'BrandListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/partners-management/brand/brand-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateBrand', {
                    templateUrl: 'app/partners-management/brand/brand-detail.html',
                    url: '/update-brand?id',
                    controller: 'UpdateBrandCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/partners-management/brand/brand-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.order-limit-list', {
                    templateUrl: 'app/organization-management/order-limit-list.html',
                    url: '/order-limit-list',
                    controller: 'OrderLimitListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/order-limit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.vendor-list', {
                    templateUrl: 'app/partners-management/vendor/vendor-list.html',
                    url: '/vendor-list?pageSize&page&cityId&organizationId&vendorId',
                    controller: 'VendorListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/partners-management/vendor/vendor-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.pwd-vendor', {
                    templateUrl: 'app/partners-management/vendor/update-vendor-password.html',
                    url: '/update-vendor-password',
                    controller: 'updateVendorPasswordCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/partners-management/vendor/update-vendor-password.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.vendor-detail', {
                    templateUrl: 'app/partners-management/vendor/vendor-detail.html',
                    url: '/vendor-detail/{id}',
                    controller: 'VendorDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/partners-management/vendor/vendor-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sku-sale-detail', {
                    templateUrl: 'app/order-management/order/sku-sale-detail.html',
                    url: '/sku-sale-detail',
                    controller: 'SkuSaleDetail',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/sku-sale-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.order-evaluate', {
                    templateUrl: 'app/order-management/order/order-evaluate.html',
                    url: '/order-evaluate?onlyNoScore&cityId&organizationId&start&end&orderId&adminName&trackerName&page&pageSize&warehouseId',
                    controller: 'OrderEvaluate',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/order-management/order/order-evaluate.js'

                                ]
                            })
                        }
                    }
                })
                .state('oam.couponManagement', {
                    templateUrl: 'app/activity-management/coupon/coupon-list.html',
                    url: '/coupon-list?page&pageSize&couponType&startDate&endDate',
                    controller: 'CouponListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/coupon/coupon-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.promotionManagement', {
                    templateUrl: 'app/activity-management/promotion/promotion-list.html',
                    url: '/promotion-list?page&pageSize&promotionType&startDate&endDate',
                    controller: 'PromotionListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/promotion/promotion-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.organization-list', {
                    templateUrl: 'app/organization-management/organization-list.html',
                    url: '/organization?cityId&organizationId&name&status',
                    controller: 'ListOrganizationCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/organization-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.bannerManagement', {
                    templateUrl: 'app/activity-management/banner/banner-list.html',
                    url: '/banner-list',
                    controller: 'BannerListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/banner/banner-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.organization-detail', {
                    templateUrl: 'app/organization-management/organization-detail.html',
                    url: '/organization-detail/{id}',
                    controller: 'AddOrganizationCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/organization-detail.js'

                                ]
                            })
                        }
                    }
                })
                .state('oam.pushManagement', {
                    templateUrl: 'app/activity-management/push/push-list.html',
                    url: '/push-list',
                    controller: 'PushListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/push-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.precisePush', {
                    templateUrl: 'app/activity-management/push/precise-push.html',
                    url: '/precise-push?page&pageSize&zoneId&adminUserId&telephone&name&status&start&end&warehouseId&registPhone&id&cityId&organizationId&grade&warning&sortField&{asc:bool}',
                    controller: 'PrecisePushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/precise-push.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.precise-batch-push', {
                    templateUrl: 'app/activity-management/push/precise-batch-push.html',
                    url: '/precise-batch-push?ids',
                    controller: 'PreciseBatchPushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/precise-batch-push.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.dailyPush', {
                    templateUrl: 'app/push-management/daily-push-list.html',
                    url: '/dailyPush-list',
                    controller: 'DailyPushListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/push-management/daily-push-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateDailyPush', {
                    templateUrl: 'app/push-management/daily-push-detail.html',
                    url: '/update-dailyPush?id',
                    controller: 'UpdateDailyPushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/push-management/daily-push-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.editCoupon', {
                    templateUrl: 'app/activity-management/coupon/coupon-detail.html',
                    url: '/coupon-detail/{id}?type&couponType',
                    controller: 'CreateCouponCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/coupon/coupon-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sendCoupon', {
                    templateUrl: 'app/activity-management/coupon/coupon-detail.html',
                    url: ' /coupon-detail/{id}?sendCoupon=1',
                    controller: 'CreateCouponCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/coupon/coupon-detail.js'
                                ]
                            })
                        }
                    }
                })

                .state('oam.addPromotion', {
                    templateUrl: 'app/activity-management/promotion/promotion-detail.html',
                    url: '/promotion-detail/{id}',
                    controller: 'CreatePromotionCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/promotion/promotion-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sendPromotion', {
                    templateUrl: 'app/activity-management/promotion/promotion-detail.html',
                    url: ' /promotion-detail/{id}?sendPromotion=1',
                    controller: 'CreatePromotionCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/promotion/promotion-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addBanner', {
                    templateUrl: 'app/activity-management/banner/banner-detail.html',
                    url: '/banner-detail',
                    controller: 'CreateBannerCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/banner/banner-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addBanners', {
                    templateUrl: 'app/activity-management/banner/banner-detail.html',
                    url: '/banner-detail/{id}',
                    controller: 'CreateBannerCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/banner/banner-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addPush', {
                    templateUrl: 'app/activity-management/push/push-detail.html',
                    url: '/push-detail',
                    controller: 'CreatePushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/push-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addWXPush', {
                    templateUrl: 'app/activity-management/push/push-wx-detail.html',
                    url: '/push-wx-detail',
                    controller: 'WXPushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/push-wx-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addPushes', {
                    templateUrl: 'app/activity-management/push/push-detail.html',
                    url: '/push-detail/{id}',
                    controller: 'CreatePushCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/push/push-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.block-list', {
                    templateUrl: 'app/organization-management/block-list.html',
                    url: '/block-list',
                    controller: 'ListBlockCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/block-list.js'

                                ]
                            })
                        }
                    }
                }).state('oam.block-detail', {
                    templateUrl: 'app/organization-management/block-detail.html',
                    url: '/block-detail/{id}',
                    controller: 'AddBlockCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/block-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.city-list', {
                    templateUrl: 'app/organization-management/city-list.html',
                    url: '/city-list',
                    controller: 'ListCityCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/city-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.city-detail', {
                    templateUrl: 'app/organization-management/city-detail.html',
                    url: '/city-detail/{id}',
                    controller: 'cityDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/city-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.warehouse-list', {
                    templateUrl: 'app/organization-management/warehouse-list.html',
                    url: '/warehouse-list',
                    controller: 'ListWarehouseCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/warehouse-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.warehouse-detail', {
                    templateUrl: 'app/organization-management/warehouse-detail.html',
                    url: '/warehouse-detail/{id}',
                    controller: 'WarehouseDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/warehouse-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.manageOrganizationStaff', {
                    templateUrl: 'app/admin-management/organization-admin-list.html',
                    url: '/organization-list-admin/{id}?cityId&organizationId&username&realname&telephone&isEnabled&page&pageSize',
                    controller: 'ListAllOrganzationAdminCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/organization-admin-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.updateOrganizationAdmin', {
                    templateUrl: 'app/admin-management/organization-admin-detail.html',
                    url: '/update-organization-admin/:id?cityId&organizationId',
                    controller: 'AddOrganizationStaffCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/organization-admin-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.saleVisit-list', {
                    templateUrl: 'app/restaurant-management/saleVisit/saleVisit-list.html',
                    url: '/saleVisit-list/?{restaurantId}&pageSize',
                    controller: 'saleVisitManagementCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/saleVisit/saleVisit-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.saleVisit-detail', {
                    templateUrl: 'app/restaurant-management/saleVisit/saleVisit-detail.html',
                    url: '/saleVisit-detail/{restaurantId}',
                    controller: 'AddsaleVisitStaffCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/restaurant-management/saleVisit/saleVisit-detail.js'
                                ]
                            })
                        }
                    }
                }).state('oam.cut-order-list', {
                    templateUrl: 'app/erp/purchase-order/cut-order-list.html',
                    url: '/cut-order-list',
                    controller: 'CutOrderListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/cut-order-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-order-list', {
                    templateUrl: 'app/erp/purchase-order/purchase-order-list.html',
                    url: '/purchase-order-list?audit&type&listType&toPrint&cityId&organizationId&depotId&vendorId&paymentVendorId&productName&productId&skuId&cutOrderId&id&status&print&startDate&endDate&page&pageSize&minAmount&maxAmount&minPaymentAmount&maxPaymentAmount',
                    controller: 'PurchaseOrderListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-order-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.return-note-list', {
                    templateUrl: 'app/erp/purchase-order/return-note-list.html',
                    url: '/return-note-list?audit&page&pageSize&cityId&organizationId&depotId&vendorId&productName&purchaseOrderId&status&startDate&endDate',
                    controller: 'ReturnNoteListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/return-note-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-order-add', {
                    templateUrl: 'app/erp/purchase-order/purchase-order-add.html',
                    url: '/purchase-order-add?audit&edit&add',
                    controller: 'PurchaseOrderAddCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-order-add.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-order-detail', {
                    templateUrl: 'app/erp/purchase-order/purchase-order-add.html',
                    url: '/purchase-order-add/{id}?audit&edit',
                    controller: 'PurchaseOrderAddCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-order-add.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-order-info', {
                    templateUrl: 'app/erp/purchase-order/purchase-order-info.html',
                    url: '/purchase-order-info?id',
                    controller: 'PurchaseOrderInfoCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-order-info.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-order-return-detail', {
                    templateUrl: 'app/erp/purchase-order/purchase-order-return.html',
                    url: '/purchase-order-return-detail/{id}?purchaseOrderId&audit&edit',
                    controller: 'PurchaseOrderReturnCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-order-return.js'
                                ]
                            })
                        }
                    }
                }).state('oam.purchase-according-result', {
                    templateUrl: 'app/erp/purchase-order/purchase-according-result.html',
                    url: '/purchase-according-result?cityId&organizationId&depotId&cutOrders&page&pageSize',
                    controller: 'PurchaseAccordingResultCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/purchase-order/purchase-according-result.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-accounting-payment-list', {
                    templateUrl: 'app/erp/accounting-payment/accounting-payment-list.html',
                    url: '/accounting-payment-list?page&pageSize&cityId&organizationId&vendorId&id&methodCode&status&startDate&endDate&minAmount&maxAmount&creator',
                    controller: 'AccountingPaymentListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-payment-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-accounting-payable-list', {
                    templateUrl: 'app/erp/accounting-payment/accounting-payable-list.html',
                    url: '/accounting-payable-list?edit&page&pageSize&cityId&organizationId&purchaseVendorId&vendorId&status&startDate&endDate&minAccountPayableAmount&maxAccountPayableAmount&purchaseOrderId&stockInId&stockOutId&accountPayableType&writeOffStartDate&writeOffEndDate',
                    controller: 'AccountingPayableListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-payable-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-accounting-payment-writeOff-list', {
                    templateUrl: 'app/erp/accounting-payment/accounting-payment-writeOff-list.html',
                    url: '/accounting-payment-writeOff-list?cancel&page&pageSize&cityId&organizationId&purchaseVendorId&vendorId&status&stockInStartDate&stockInEndDate&writeOffStartDate&writeOffEndDate&writeOffCanceledStartDate&writeOffCanceledEndDate&minAccountPayableAmount&maxAccountPayableAmount&purchaseOrderId&stockInId&stockOutId&accountPayableType&writeOffer',
                    controller: 'AccountingPaymentWriteOffListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-payment-writeOff-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-vendorAccount-list', {
                    templateUrl: 'app/erp/integrated-query/vendorAccount-list.html',
                    url: '/vendorAccount-list?page&pageSize&cityId&organizationId&vendorId&statisticalDate',
                    controller: 'VendorAccountListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/vendorAccount-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-vendorAccountDetail-list', {
                    templateUrl: 'app/erp/integrated-query/vendorAccountDetail-list.html',
                    url: '/vendorAccountDetail-list?page&pageSize&cityId&organizationId&vendorId&purchaseOrderType&accountPayableType&productName&purchaseOrderId&purchaseOrderDateStart&purchaseOrderDateEnd&operationDateStart&operationDateEnd',
                    controller: 'VendorAccountDetailListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/vendorAccountDetail-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-vendorTrading-list', {
                    templateUrl: 'app/erp/integrated-query/vendorTrading-list.html',
                    url: '/vendorTrading-list?page&pageSize&cityId&organizationId&vendorId&startDate&endDate&vendorAccountOperationType',
                    controller: 'VendorTradingListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/vendorTrading-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.erp-accounting-payment-detail', {
                    templateUrl: 'app/erp/accounting-payment/accounting-payment-detail.html',
                    url: '/accounting-payment-detail',
                    controller: 'AccountingPaymentDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-payment-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.erp-accounting-receivable-list', {
                    templateUrl: 'app/erp/accounting-payment/accounting-receivable-list.html',
                    url: '/accounting-receivable-list?type&cityId&depotId&trackerId&accountReceivableStatus&accountReceivableType&sourceId&orderId&customerName&skuId&skuName&startOrderDate&endOrderDate&startSendDate&endSendDate&startReceiveDate&endReceiveDate',
                    controller: 'AccountReceivableListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-receivable-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.erp-accounting-receivable-writeoff', {
                    templateUrl: 'app/erp/accounting-payment/accounting-receivable-writeoff.html',
                    url: '/accounting-receivable-writeoff?type&cityId&depotId&trackerId&accountReceivableWriteoffStatus&accountReceivableType&sourceId&orderId&customerName&skuId&skuName&startOrderDate&endOrderDate&startSendDate&endSendDate&startReceiveDate&endReceiveDate',
                    controller: 'AccountReceivableWriteoffCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-receivable-writeoff.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.erp-accounting-collectionPaymentMethod-list', {
                    templateUrl: 'app/erp/accounting-payment/accounting-collectionPaymentMethod-list.html',
                    url: '/accounting-collectionPaymentMethod-list?cityId',
                    controller: 'AccountCollectionPaymentMethodListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-collectionPaymentMethod-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.erp-accounting-collectionPaymentMethod-detail', {
                    templateUrl: 'app/erp/accounting-payment/accounting-collectionPaymentMethod-detail.html',
                    url: '/accounting-collectionPaymentMethod-detail/{id}',
                    controller: 'AccountCollectionPaymentMethodDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/accounting-payment/accounting-collectionPaymentMethod-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.transfer-list', {
                    templateUrl: 'app/erp/transfer/transfer-list.html',
                    url: '/transfer-list?audit',
                    controller: 'TransferListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/transfer/transfer-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.transfer-add', {
                    templateUrl: 'app/erp/transfer/transfer-add.html',
                    url: '/transfer-add?audit&edit&add',
                    controller: 'TransferAddCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/transfer/transfer-add.js'
                                ]
                            })
                        }
                    }
                }).state('oam.transfer-detail', {
                    templateUrl: 'app/erp/transfer/transfer-add.html',
                    url: '/transfer-add/{id}?audit&edit',
                    controller: 'TransferAddCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/transfer/transfer-add.js'
                                ]
                            })
                        }
                    }
                }).state('oam.stock-total-list', {
                    templateUrl: 'app/erp/stock-management/stock-total-list.html',
                    url: '/stock-total-list?cityId&skuId&skuName&categoryId&page&pageSize',
                    controller: 'StockTotalCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-total-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.stock-depot-list', {
                    templateUrl: 'app/erp/stock-management/stock-depot-list.html',
                    url: '/stock-depot-list?cityId&depotId&skuId&skuName&categoryId&page&pageSize',
                    controller: 'StockDepotListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-depot-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockIn-list', {
                    templateUrl: 'app/erp/stock-management/stockIn-list.html',
                    url: '/stockIn-list?stockInType&stockInStatus&cityId&depotId&sourceDepotId&targetDepotId&purchaseOrderType&stockInId&purchaseOrderId&vendorId&vendorName&sellReturnId&orderId&transferId&skuId&skuName&page&pageSize',
                    controller: 'StockInListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockIn-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockIn-total-list', {
                    templateUrl: 'app/erp/stock-management/stockIn-total-list.html',
                    url: '/stockIn-total-list?cityId&depotId&stockInType&stockInStatus&purchaseOrderType&sellReturnType&stockInId&sourceId&vendorId&skuId&skuName&startCreateDate&endCreateDate&startReceiveDate&endReceiveDate&outPrint&page&pageSize',
                    controller: 'StockInTotalListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockIn-total-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockIn-receive', {
                    templateUrl: 'app/erp/stock-management/stockIn-receive.html',
                    url: '/stockIn-receive?stockInId&stockInType&part',
                    controller: 'StockInReceiveCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockIn-receive.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-list.html',
                    url: '/stockOut-list?stockOutType&stockOutStatus&cityId&depotId&sourceDepotId&targetDepotId&trackerId&stockOutId&orderId&customerName&returnId&vendorId&vendorName&transferId&skuId&skuName&startOrderDate&endOrderDate&pickPrint&page&pageSize',
                    controller: 'StockOutListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-total-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-total-list.html',
                    url: '/stockOut-total-list?cityId&depotId&stockOutType&stockOutStatus&stockOutId&sourceId&warehouseId&blockId&trackerId&skuId&skuName&customerName&vendorId&vendorName&startSendDate&endSendDate&startOrderDate&endOrderDate&outPrint&page&pageSize',
                    controller: 'StockOutTotalListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-total-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-total-export', {
                    templateUrl: 'app/erp/stock-management/stockOut-total-export.html',
                    url: '/stockOut-total-export?cityId&depotId&stockOutType&stockOutStatus&stockOutId&sourceId&warehouseId&blockId&trackerId&skuId&skuName&customerName&startSendDate&endSendDate&startOrderDate&endOrderDate&outPrint&page&pageSize',
                    controller: 'StockOutTotalExportCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-total-export.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-out-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-out-list.html',
                    url: '/stockOut-out-list',
                    controller: 'StockOutOutListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-out-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-receive-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-receive-list.html',
                    url: '/stockOut-receive-list',
                    controller: 'StockOutReceiveListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-receive-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-send', {
                    templateUrl: 'app/erp/stock-management/stockOut-send.html',
                    url: '/stockOut-send?stockOutId&stockOutType',
                    controller: 'StockOutSendCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-send.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-receive', {
                    templateUrl: 'app/erp/stock-management/stockOut-receive.html',
                    url: '/stockOut-receive/?stockOutId',
                    controller: 'StockOutReceiveCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-receive.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.depot-list', {
                    templateUrl: 'app/erp/stock-management/depot-list.html',
                    url: '/depot-list',
                    controller: 'ListDepotCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/depot-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addDepot', {
                    templateUrl: 'app/erp/stock-management/depot-detail.html',
                    url: '/depot-detail/{id}',
                    controller: 'AddOrUpdateDepotCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/depot-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockIn-query', {
                    templateUrl: 'app/erp/integrated-query/stockIn-query.html',
                    url: '/stockIn-query?saleReturn&cityId&depotId&stockInType&stockInStatus&sellReturnType&stockInId&sourceId&vendorId&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'StockInQueryCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockIn-query.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockIn-detail', {
                    templateUrl: 'app/erp/integrated-query/stockIn-detail.html',
                    url: '/stockIn-detail?id',
                    controller: 'StockInDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockIn-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockInItem-query', {
                    templateUrl: 'app/erp/integrated-query/stockInItem-query.html',
                    url: '/stockInItem-query?saleReturn&cityId&depotId&stockInType&stockInStatus&sellReturnType&stockInId&sourceId&vendorId&skuId&skuName&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'StockInItemQueryCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockInItem-query.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-query', {
                    templateUrl: 'app/erp/integrated-query/stockOut-query.html',
                    url: '/stockOut-query?purchaseId&stockOutType&cityId&depotId&sourceDepotId&targetDepotId&stockOutStatus&trackerId&stockOutId&orderId&customerName&vendorId&vendorName&startSendDate&endSendDate&page&pageSize',
                    controller: 'StockOutQueryCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockOut-query.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-detail', {
                    templateUrl: 'app/erp/integrated-query/stockOut-detail.html',
                    url: '/stockOut-detail?stockOutType&id',
                    controller: 'StockOutDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockOut-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-info', {
                    templateUrl: 'app/erp/integrated-query/stockOut-info.html',
                    url: '/stockOut-info?id',
                    controller: 'StockOutInfoCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockOut-info.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOutItem-query', {
                    templateUrl: 'app/erp/integrated-query/stockOutItem-query.html',
                    url: '/stockOutItem-query?stockOutType&cityId&depotId&sourceDepotId&targetDepotId&stockOutStatus&trackerId&stockOutId&orderId&customerName&purchaseId&vendorId&vendorName&skuId&skuName&startSendDate&endSendDate&startReceiveDate&endReceiveDate&startTransferDate&endTransferDate&startAuditDate&endAuditDate&page&pageSize',
                    controller: 'StockOutItemQueryCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/integrated-query/stockOutItem-query.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-all-receive', {
                    templateUrl: 'app/erp/stock-management/stockOut-all-receive.html',
                    url: '/stockOut-all-receive',
                    params: {
                        stockOuts: null
                    },
                    controller: 'StockOutAllReceiveCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-all-receive.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-collection-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-collection-list.html',
                    url: '/stockOut-collection-list',
                    controller: 'StockOutCollectionListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-collection-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-collection', {
                    templateUrl: 'app/erp/stock-management/stockOut-collection.html',
                    url: '/stockOut-collection',
                    params: {
                        stockOuts: null
                    },
                    controller: 'StockOutCollectionCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-collection.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-barcode-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-barcode-list.html',
                    url: '/stockOut-barcode-list',
                    controller: 'StockOutBarcodeListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-barcode-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.scoreSearch-list', {
                    templateUrl: 'app/score-management/scoreSearch/score-list.html',
                    url: '/scoreSearch-list?sortField&asc&cityId&restaurantName&warehouseId&restaurantId&adminUserId&status&grade&scoreLogStatus&page&pageSize',
                    controller: 'scoreSearchList',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/score-management/scoreSearch/score-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.scoreDetailSearch-list', {
                    templateUrl: 'app/score-management/scoreSearch/scoreDetail-list.html',
                    url: '/scoreDetailSearch-list?customerId&restaurantId&scoreLogStatus&page&pageSize&orderBeginDate&orderEndDate',
                    controller: 'scoreDetailSearchList',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/score-management/scoreSearch/scoreDetail-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.exchangeScoreSearch-list', {
                    templateUrl: 'app/score-management/exchangeScoreSearch/score-list.html',
                    url: '/exchangeScoreSearch-list?cityId&restaurantName&warehouseId&restaurantId&adminUserId&status&grade&scoreLogStatus&page&pageSize',
                    controller: 'exchangeScoreSearchList',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/score-management/exchangeScoreSearch/score-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.scoreExchange', {
                    templateUrl: 'app/score-management/scoreSearch/scoreExchange.html',
                    url: '/scoreExchange?customerId',
                    controller: 'scoreExchangeCtrl'


                }).state('oam.stock-avgcost-list', {
                    templateUrl: 'app/erp/stock-management/stock-avgcost-list.html',
                    url: '/stock-avgcost-list?cityId&skuId&skuName&page&pageSize',
                    controller: 'AvgCostCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-avgcost-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stockOut-notmatch-list', {
                    templateUrl: 'app/erp/stock-management/stockOut-notmatch-list.html',
                    url: '/stockOut-notmatch-list',
                    controller: 'StockOutNotMatchListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stockOut-notmatch-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.warehouseCategoryProfit-list', {
                    templateUrl: 'app/erp/profit-management/warehouseCategoryProfit-list.html',
                    url: '/warehouseCategoryProfit-list?cityId&startReceiveDate&endReceiveDate',
                    controller: 'WarehouseCategoryProfitListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/warehouseCategoryProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.categorySellerProfit-list', {
                    templateUrl: 'app/erp/profit-management/categorySellerProfit-list.html',
                    url: '/categorySellerProfit-list?cityId&startReceiveDate&endReceiveDate',
                    controller: 'CategorySellerProfitListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/categorySellerProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.skuProfit-list', {
                    templateUrl: 'app/erp/profit-management/skuProfit-list.html',
                    url: '/skuProfit-list?cityId&warehouseId&accountReceivableType&categoryId&skuId&skuName&customerName&sellerName&startOrderDate&endOrderDate&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'SkuProfitListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/skuProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.customerSellerProfit-list', {
                    templateUrl: 'app/erp/profit-management/customerSellerProfit-list.html',
                    url: '/customerSellerProfit-list?cityId&warehouseId&accountReceivableType&restaurantStatus&restaurantId&restaurantName&sellerName&startOrderDate&endOrderDate&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'CustomerSellerProfitListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/customerSellerProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.customerSkuProfit-list', {
                    templateUrl: 'app/erp/profit-management/customerSkuProfit-list.html',
                    url: '/customerSkuProfit-list?cityId&warehouseId&accountReceivableType&skuId&skuName&customerName&sellerName&startOrderDate&endOrderDate&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'customerSkuProfitListController',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/customerSkuProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.skuSales-list', {
                    templateUrl: 'app/erp/profit-management/skuSales-list.html',
                    url: '/skuSales-list?cityId&warehouseId&accountReceivableType&categoryId&skuId&skuName&startOrderDate&endOrderDate&startReceiveDate&endReceiveDate&page&pageSize',
                    controller: 'SkuSalesListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/skuSales-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.skuSellSummeryProfit-list', {
                    templateUrl: 'app/erp/profit-management/skuSellSummeryProfit-list.html',
                    url: '/skuSellSummeryProfit-list?cityId&warehouseId&accountReceivableType &restaurantStatus &orderId&skuId&skuName&restaurantId&restaurantName&customerName&sellerName&startOrderDate&endOrderDate&startReceiveDate&endReceiveDate&orderBySeller&page&pageSize',
                    controller: 'skuSellSummeryProfitListController',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/profit-management/skuSellSummeryProfit-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.sellerSalesPerformance-list', {
                    templateUrl: 'app/erp/salesPerformance-management/sellerSalesPerformance-list.html',
                    url: '/sellerSalesPerformance-list?cityId&startDate&endDate&page&pageSize',
                    controller: 'SellerSalesPerformanceListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/salesPerformance-management/sellerSalesPerformance-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.restaurantSalesPerformance-list', {
                    templateUrl: 'app/erp/salesPerformance-management/restaurantSalesPerformance-list.html',
                    url: '/restaurantSalesPerformance-list?cityId&startDate&endDate&page&pageSize',
                    controller: 'RestaurantSalesPerformanceListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/salesPerformance-management/restaurantSalesPerformance-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.blockSalesPerformance-list', {
                    templateUrl: 'app/erp/salesPerformance-management/blockSalesPerformance-list.html',
                    url: '/blockSalesPerformance-list?cityId&startDate&endDate&page&pageSize',
                    controller: 'BlockSalesPerformanceListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/salesPerformance-management/blockSalesPerformance-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.couponStatistics-list', {
                    templateUrl: 'app/activity-management/coupon/couponStatistics-list.html',
                    url: '/couponStatistics-list?couponIdFront&couponIdBack&cityId&warehouseId&restaurantName&restaurantId&couponType&couponStatus&sendFront&sendBack&endFront&endBack&page&pageSize&startFront&startBack&useFront&useBack',
                    controller: 'couponStatisticsListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/coupon/couponStatistics-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.couponStatistics-used-list', {
                    templateUrl: 'app/activity-management/coupon/couponStatistics-used-list.html',
                    url: '/couponStatistics-used-list?stockoutDateFront&stockoutDateBack&orderDateFront&orderDateBack&orderId&listType&cityId&warehouseId&restaurantName&restaurantId&couponType&couponStatus&sendFront&sendBack&startFront&startBack&endFront&endBack&page&pageSize&sortField&asc&useFront&useBack',
                    controller: 'couponStatisticsUsedListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/activity-management/coupon/couponStatistics-used-list.js'
                                ]
                            })
                        }
                    }
                }).state('oam.promotionStatistics-fullgift-list', {
                    templateUrl: 'app/activity-management/promotion/promotionStatistics-fullgift-list.html',
                    url: '/promotionStatistics-fullgift-list?stokoutTimeFront&stokoutTimeBak&promotionType&cityId&warehouseId&orderId&orderSubmitFront&orderSubmitBack&promotionId&skuId&skuName&restaurantId&restaurantName&page&pageSize&sortField&asc',
                    controller: 'promotionStatisticsFullgiftListCtrl',

                }).state('oam.promotionStatistics-fullcut-list', {
                    templateUrl: 'app/activity-management/promotion/promotionStatistics-fullcut-list.html',
                    url: '/promotionStatistics-fullcut-list?stokoutTimeFront&stokoutTimeBak&promotionType&cityId&warehouseId&orderId&orderSubmitFront&orderSubmitBack&promotionId&skuId&skuName&restaurantId&restaurantName&page&pageSize&sortField&asc',
                    controller: 'promotionStatisticsFullCutListCtrl',

                })
                .state('oam.shelf-list', {
                    templateUrl: 'app/erp/stock-management/shelf-list.html',
                    url: '/shelf-list',
                    controller: 'ShelfListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/shelf-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addShelf', {
                    templateUrl: 'app/erp/stock-management/shelf-detail.html',
                    url: '/shelf-detail/{id}',
                    controller: 'AddOrUpdateShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/shelf-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addBatchShelf', {
                    templateUrl: 'app/erp/stock-management/shelf-batch-detail.html',
                    url: '/shelf-batch-detail',
                    controller: 'AddBatchShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/shelf-batch-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.version-list', {
                    templateUrl: 'app/version-management/version-list.html',
                    url: '/version-list?versionCode&versionName&comment&page&pageSize',
                    controller: 'VersionListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/version-management/version-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-willshelf-list', {
                    templateUrl: 'app/erp/stock-management/stock-willshelf-list.html',
                    url: '/stock-willshelf-list',
                    controller: 'StockWillShelfListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-willshelf-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.updateVersion', {
                    templateUrl: 'app/version-management/version-detail.html',
                    url: '/update-version?id',
                    controller: 'UpdateVersionCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/version-management/version-detail.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.onShelf', {
                    templateUrl: 'app/erp/stock-management/stock-onshelf.html',
                    url: '/stock-onshelf',
                    params: {
                        stock: null
                    },
                    controller: 'StockOnShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-onshelf.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-shelf-list', {
                    templateUrl: 'app/erp/stock-management/stock-shelf-list.html',
                    url: '/stock-shelf-list',
                    controller: 'StockShelfListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-shelf-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-moveshelf-list', {
                    templateUrl: 'app/erp/stock-management/stock-moveshelf-list.html',
                    url: '/stock-moveshelf-list',
                    controller: 'StockMoveShelfListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-moveshelf-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.moveShelf', {
                    templateUrl: 'app/erp/stock-management/stock-moveshelf.html',
                    url: '/stock-moveshelf',
                    params: {
                        stock: null
                    },
                    controller: 'StockMoveShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-moveshelf.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.suggestionListCtrl', {
                    templateUrl: 'app/activity-management/suggestion/suggestion-list.html',
                    url: '/suggestion-list?cityId&page&pageSize',
                    controller: 'suggestionListCtrl'
                })
                .state('oam.feedbackListCtrl', {
                    templateUrl: 'app/activity-management/feedback/feedback-list.html',
                    url: '/feedback-list?cityId&page&pageSize&sortField&asc&id&customerId&customerName&restaurantId&restaurantName&vendorId&verdorName&status&type&submitTimeFront&submitTimeBack&updateTimeFront&updateTimeBack',
                    controller: 'feedbackListCtrl'
                })
                .state('oam.feedbackDetail', {
                    templateUrl: 'app/activity-management/feedback/feedback-detail.html',
                    url: '/feedback-detail?feedbackId',
                    controller: 'feedbackDetailCtrl'
                })
                .state('oam.spike-add', {
                    templateUrl: 'app/activity-management/spike/spike-add.html',
                    url: '/spike-add',
                    controller: 'spikeAddCtrl'
                })
                .state('oam.spike-list', {
                    templateUrl: 'app/activity-management/spike/spike-list.html',
                    url: '/spike-list?page&pageSize&sortField&asc',
                    controller: 'spikeListCtrl'
                })
                .state('oam.spike-item-list', {
                    templateUrl: 'app/activity-management/spike/spike-item-list.html',
                    url: '/spike-item-list?id',
                    controller: 'spikeItemListCtrl'
                })
                .state('oam.spike-modify', {
                    templateUrl: 'app/activity-management/spike/spike-modify.html',
                    url: '/spike-modify?id',
                    controller: 'spikeItemModifyCtrl'
                })
                .state('oam.stock-adjust-list', {
                    templateUrl: 'app/erp/stock-management/stock-adjust-list.html',
                    url: '/stock-adjust-list',
                    controller: 'StockAdjustListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-adjust-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-adjust', {
                    templateUrl: 'app/erp/stock-management/stock-adjust.html',
                    url: '/stock-adjust',
                    params: {
                        stock: null
                    },
                    controller: 'StockAdjustCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-adjust.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-adjust-surplus', {
                    templateUrl: 'app/erp/stock-management/stock-adjust-surplus.html',
                    url: '/stock-adjust-surplus',
                    controller: 'StockAdjustSurplusCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-adjust-surplus.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-adjustConfirm-list', {
                    templateUrl: 'app/erp/stock-management/stock-adjustconfirm-list.html',
                    url: '/stock-adjustconfirm-list',
                    controller: 'StockAdjustConfirmListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-adjustconfirm-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-adjust-query', {
                    templateUrl: 'app/erp/stock-management/stock-adjust-query.html',
                    url: '/stock-adjust-query',
                    controller: 'StockAdjustQueryCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-adjust-query.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.batch-onShelf', {
                    templateUrl: 'app/erp/stock-management/stock-batch-onshelf.html',
                    url: '/stock-batch-onshelf',
                    params: {
                        stocks: null
                    },
                    controller: 'StockBatchOnShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-batch-onshelf.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.batch-moveShelf', {
                    templateUrl: 'app/erp/stock-management/stock-batch-moveshelf.html',
                    url: '/stock-batch-moveshelf',
                    params: {
                        stocks: null
                    },
                    controller: 'StockBatchMoveShelfCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-batch-moveshelf.js'
                                ]
                            })
                        }
                    }
                }).state('oam.stock-total-daily-list', {
                      templateUrl: 'app/erp/stock-management/stock-total-daily-list.html',
                      url: '/stock-total-daily-list?cityId&skuId&skuName&categoryId&startCreateDate&endCreateDate&page&pageSize',
                      controller: 'StockTotalDailyCtrl',
                      resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-total-daily-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-production-date-list', {
                    templateUrl: 'app/erp/stock-management/stock-production-date-list.html',
                    url: '/stock-production-date-list',
                    controller: 'StockProductionDateListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-production-date-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-production-date', {
                    templateUrl: 'app/erp/stock-management/stock-production-date.html',
                    url: '/stock-production-date',
                    params: {
                        stock: null
                    },
                    controller: 'StockProductionDateCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-production-date.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.batch-production-date', {
                    templateUrl: 'app/erp/stock-management/stock-batch-date.html',
                    url: '/stock-batch-date',
                    params: {
                        stocks: null
                    },
                    controller: 'StockBatchDateCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-batch-date.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-expiration-list', {
                    templateUrl: 'app/erp/stock-management/stock-expiration-list.html',
                    url: '/stock-expiration-list',
                    controller: 'StockExpirationListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-expiration-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-dull-list', {
                    templateUrl: 'app/erp/stock-management/stock-dullsale-list.html',
                    url: '/stock-dull-list',
                    controller: 'StockDullSaleListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-dullsale-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.stock-safety-set-list', {
                    templateUrl: 'app/erp/stock-management/stock-safety-set-list.html',
                    url: '/stock-safety-set-list',
                    controller: 'StockSafetySetListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/erp/stock-management/stock-safety-set-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.system-email-list', {
                    templateUrl: 'app/organization-management/system-email-list.html',
                    url: '/system-email-list',
                    controller: 'SystemEmailListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/system-email-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.addSystemEmail', {
                    templateUrl: 'app/organization-management/system-email-detail.html',
                    url: '/system-email-detail/{id}',
                    controller: 'AddOrUpdateSystemEmailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/organization-management/system-email-detail.js'
                                ]
                            })
                        }
                    }
                })
        }
    ]).run(function ($rootScope, $location, UserService) {
        $rootScope.hasRole = function (role) {
            var result = false;


            if ($rootScope.user === undefined) {
                result = false;
            } else {
                for (var i = 0; i <= $rootScope.user.adminRoles.length - 1; i++) {
                    var roleName = $rootScope.user.adminRoles[i].name;
                    if (roleName == role) {
                        result = true;
                    }
                }
            }

            return result;
        };

        $rootScope.hasPermission = function (permission) {
            var result = false;

            if ($rootScope.user === undefined) {
                result = false;
            } else {
                for (var i = 0; i <= $rootScope.user.adminPermissions.length - 1; i++) {
                    var permissionName = $rootScope.user.adminPermissions[i].name;
                    if (permissionName == permission) {
                        result = true;
                    }
                }
            }

            return result;
        };
        $rootScope.hasGlobalManager = function () {
            if ($rootScope.user === undefined) {
                return false;
            }
            return $rootScope.user.globalAdmin;
        };

        $rootScope.logout = function () {
            delete $rootScope.user;

            $location.path("/login");
        };

        var originalPath = $location.path();
        UserService.profile(function (user) {
            $rootScope.user = user;
            $location.path(originalPath);
        });


    });

//将后台毫秒数转化为时间
angular.module('sbAdminApp').directive('formatedDate', ['$filter', '$parse', function ($filter, $parse) {
    return {
        restrict: 'A',
        require: '^ngModel',
        link: function (scope, element, attrs, ctrl) {
            scope.$watch(attrs.ngModel, function (d) {
                if (d) {
                    if (angular.isNumber(d)) {
                        var modelGetter = $parse(attrs.ngModel);
                        var modelSetter = modelGetter.assign;
                        modelSetter(scope, new Date(d).toISOString());
                    }
                }
            });
        }
    };
}]);

//将后台毫秒数转化为时间
angular.module('sbAdminApp').directive('millSecToDate', ['$filter', '$parse', function ($filter, $parse) {
    return {
        restrict: 'A',
        require: '^ngModel',
        link: function (scope, element, attrs, ctrl) {
            scope.$watch(attrs.ngModel, function (d) {
                if (d) {
                    if (angular.isNumber(d)) {
                        var modelGetter = $parse(attrs.ngModel);
                        var modelSetter = modelGetter.assign;
                        modelSetter(scope, new Date(d));
                    }
                }
            });
        }
    };
}]);

//将时间控件的时间格式化为字符串
angular.module('sbAdminApp').directive('dateForSearch', ['$filter', '$parse', function ($filter, $parse) {
    return {
        restrict: 'A',
        require: '^ngModel',
        link: function (scope, element, attrs, ctrl) {
            scope.$watch(attrs.ngModel, function (d) {
                if (d) {
                    if (angular.isDate(d)) {
                    } else {
                        d = Date.parse(d);
                    }
                    var modelGetter = $parse(attrs.ngModel);
                    var modelSetter = modelGetter.assign;
                    if (scope.submitDateFormat) {
                        modelSetter(scope, $filter('date')(d, scope.submitDateFormat));
                    } else {
                        modelSetter(scope, $filter('date')(d, 'yyyy-MM-dd'));
                    }
                }
            });
        }
    };
}]);

//init ngModel when page refreshed with stateParams.
angular.module('sbAdminApp').directive('refreshEnabled', ['$filter', '$parse', '$stateParams', function ($filter, $parse, $stateParams) {
    return {
        restrict: 'EA',
        require: '^ngModel',
        priority: 2,
        link: function (scope, element, attrs, ctrl) {
            //ngModel name
            var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
            var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
            //stateParam of this ngModel
            var valueStr = $stateParams[ngModelName];
            if (valueStr) {
                //custom ngModel type
                var ngModelType = attrs.refreshEnabled;
                if (!ngModelType) {
                    ngModelType = attrs.type;
                }
                var modelValue;
                if (ngModelType == "Integer") {
                    var REGEX = /^\-?\d+(.\d+)?$/
                    if (REGEX.test(valueStr)) {
                        modelValue = parseInt(valueStr);
                    } else {
                        modelValue = null;
                    }
                } else if (ngModelType == "Float") {
                    modelValue = parseFloat(valueStr);
                } else if (ngModelType == "Date") {
                    modelValue = valueStr;
                } else if (ngModelType == "Boolean") {
                    if (valueStr == "true") {
                        modelValue = true;
                    } else {
                        modelValue = false;
                    }
                } else if (ngModelType == "String") {
                    modelValue = valueStr;
                }
                else {
                    modelValue = valueStr;
                }
                //set ngModel value
                var modelGetter = $parse(attrs.ngModel);
                var modelSetter = modelGetter.assign;
                modelSetter(scope, modelValue);
            }
        }
    };
}]);

angular.module('sbAdminApp').directive('validateNull', function () {
    return {
        require: ['^ngModel'],
        compile: function (tElement, tAttrs, tCtrl) {
            return {
                pre: function (scope, element, attrs, ctrl) {
                    if (attrs.validateNull != "false") {
                        var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
                        var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
                        var messageName = ngModelName + "_message_validate_null";
                        var messageDom = "<div name='" + messageName + "'><font color='red'>必填项</font></div>";
                        ctrl[0].$validators.null = function (modelValue, viewValue) {
                            if (viewValue != null && viewValue != "" && typeof(viewValue) != "undefined") {
                                angular.element("[name='" + messageName + "']").remove();
                                return true;
                            }
                            //angular.element("[name='" + messageName + "']").remove();
                            //element.parent().append(messageDom);
                            return false;
                        };
                    }
                }
            }
        }
    };
});

angular.module('sbAdminApp').directive('validateNumber', function () {
    var REGEX = /^\-?\d+(.\d+)?$/
    return {
        require: ['^ngModel'],
        compile: function (tElement, tAttrs, tCtrl) {
            return {
                pre: function (scope, element, attrs, ctrl) {
                    var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
                    var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
                    var messageName = ngModelName + "_message_validate_number";
                    var messageDom = "<div name='" + messageName + "'><font color='red'>必须是数字</font></div>";
                    ctrl[0].$validators.number = function (modelValue, viewValue) {
                        if (viewValue == "" || viewValue == undefined || REGEX.test(viewValue)) {
                            angular.element("[name='" + messageName + "']").remove();
                            return true;
                        }
                        //angular.element("[name='" + messageName + "']").remove();
                        //element.parent().append(messageDom);
                        return false;
                    };
                }
            }
        }
    };
});

angular.module('sbAdminApp').directive('validateInteger', function () {
    var REGEX = /^\-?\d+$/
    return {
        require: ['^ngModel'],
        compile: function (tElement, tAttrs, tCtrl) {

            return {
                pre: function (scope, element, attrs, ctrl) {
                    var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
                    var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
                    var messageName = ngModelName + "_message_validate_integer";
                    var messageDom = "<div name='" + messageName + "'><font color='red'>必须是整数</font></div>";
                    ctrl[0].$validators.integer = function (modelValue, viewValue) {
                        if (viewValue == "" || viewValue == undefined || REGEX.test(viewValue)) {
                            angular.element("[name='" + messageName + "']").remove();
                            return true;
                        }
                        //angular.element("[name='" + messageName + "']").remove();
                        //element.parent().append(messageDom);
                        return false;
                    };
                }
            }
        }
    };
});

angular.module('sbAdminApp').directive('validatePositive', function () {
    var REGEX = /^([1-9]\d*(.\d+)?|0.\d+)$/
    return {
        require: ['^ngModel'],
        compile: function (tElement, tAttrs, tCtrl) {
            return {
                pre: function (scope, element, attrs, ctrl) {
                    var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
                    var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
                    var messageName = ngModelName + "_message_validate_positive";
                    var messageDom = "<div name='" + messageName + "'><font color='red'>必须大于0</font></div>";
                    ctrl[0].$validators.positive = function (modelValue, viewValue) {
                        if (viewValue == "" || viewValue == undefined || REGEX.test(viewValue)) {
                            angular.element("[name='" + messageName + "']").remove();
                            return true;
                        }
                        //angular.element("[name='" + messageName + "']").remove();
                        //element.parent().append(messageDom);
                        return false;
                    };
                }
            }
        }
    };
});

angular.module('sbAdminApp').directive('validateNonNegative', function () {
    var REGEX = /^(\d+(.\d+)?)$/
    return {
        require: ['^ngModel'],
        compile: function (tElement, tAttrs, tCtrl) {
            return {
                pre: function (scope, element, attrs, ctrl) {
                    var ngModelNameIndex = attrs.ngModel.lastIndexOf(".");
                    var ngModelName = attrs.ngModel.substr(ngModelNameIndex + 1);
                    var messageName = ngModelName + "_message_validate_positive";
                    var messageDom = "<div name='" + messageName + "'><font color='red'>必须大于0</font></div>";
                    ctrl[0].$validators.positive = function (modelValue, viewValue) {
                        if (viewValue == "" || viewValue == undefined || REGEX.test(viewValue)) {
                            angular.element("[name='" + messageName + "']").remove();
                            return true;
                        }
                        //angular.element("[name='" + messageName + "']").remove();
                        //element.parent().append(messageDom);
                        return false;
                    };
                }
            }
        }
    };
});


angular.module('sbAdminApp').directive('autoGrow', function () {
    return {
        link: function ($scope, element) {
            element.bind('keyup', function ($event) {
                element.css('height', element[0].scrollHeight + 'px');
            });
        }
    };
});

//适用于click时间发送的实时请求，可防止重复点击，请求返回后，按钮恢复正常
angular.module('sbAdminApp').directive('singleClick', ['$parse', '$timeout', function ($parse, $timeout) {
    return {
        restrict: 'A',
        priority: 1,
        compile: function (tElement, tAttrs, tCtrl) {
            return {
                link: function (scope, element, attrs, ctrl) {
                    element.bind('click', function () {
                        scope.$apply(function () {
                            element.attr('disabled', true);
                        });

                        var func = $parse(attrs.singleClick);
                        scope.$apply(func);

                        scope.$apply(function () {
                            element.attr('disabled', false);
                        });
                    });
                }
            }
        }
    };
}]);

angular.module('sbAdminApp').directive('avoidMultiClick', function($timeout) {
    return {
        priority: 1,
        link: function(scope, element, attrs) {
            element.bind('click', function () {
                $timeout(function () {
                    element.attr('disabled', true);
                }, 0);
            });
        }
    }
});

angular.module('sbAdminApp').directive('ngConfirmClick', [
    function () {
        return {
            priority: 1,
            //terminal: true,
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "确定？";
                var func = attr.confirmedClick;
                element.bind('click', function (event) {
                    if (window.confirm(msg)) {
                        element.attr('disabled', true);
                        scope.$eval(func);
                    }
                });
            }
        };
    }]);

angular.module('sbAdminApp').directive("loader", function () {
        return {
            restrict: 'E',
            priority: 1,
            link: function (scope, element, attr) {
                scope.$on("loader_show", function () {
                    return element.show();
                });
                scope.$on("loader_hide", function () {
                    return element.hide();
                });
            }
        };
    }
);

angular.module('sbAdminApp').filter('percentage', ['$filter', function ($filter) {
    return function (input, decimals) {
        return $filter('number')(input * 100, decimals) + '%';
    };
}]);
