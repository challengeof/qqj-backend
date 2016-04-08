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

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddressBookCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

            $scope.page = {
                itemsPerPage : 100
            }

            $scope.adminUserForm = {
                page : $stateParams.page,
                pageSize : $stateParams.pageSize,
                username: $stateParams.username,
                realname: $stateParams.realname,
                telephone: $stateParams.telephone
            }

            if($stateParams.page) {
                $scope.adminUserForm.page = parseInt($stateParams.page);
            }

            if($stateParams.pageSize) {
                $scope.adminUserForm.pageSize = parseInt($stateParams.pageSize);
            }

            if ($stateParams.username) {
                $scope.adminUserForm.username = $stateParams.username;
            }

            if ($stateParams.realname) {
                $scope.adminUserForm.realname = $stateParams.realname;
            }

            if ($stateParams.telephone) {
                $scope.adminUserForm.telephone = $stateParams.telephone;
            }

            $http({
                url: "/admin/api/admin-user",
                method: "GET",
                params:$scope.adminUserForm
            })
            .success(function (data) {
                $scope.users = data.adminUsers;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            });
            

            $scope.resetPageAndSearchForm = function () {
                $scope.adminUserForm.page = 0;
                $scope.adminUserForm.pageSize = 100;

                $location.search($scope.adminUserForm);
            }

            $scope.pageChanged = function() {
                $scope.adminUserForm.page = $scope.page.currentPage - 1;
                $scope.adminUserForm.pageSize = $scope.page.itemsPerPage;

                $location.search($scope.adminUserForm);
            }
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:admin-updatePassword
 * @description
 * # admin-updatePassword
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('updateAdminPassCtrl', function($scope, $http, $stateParams) {

 	$scope.updateAdminPass = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/admin-user/updateAdminPassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
 			if (data) {
 				window.alert("修改成功!");
 			} else {
 				window.alert("管理员不存在!");
			}
 		})
 		.error(function() {
 			window.alert("修改失败!");
 		});
 	};
 });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ChangeServiceCtrl', function($scope, $state, $http) {

        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.oldAdminUsers = [{id:0, realname:"未分配销售"}].concat(data);
                $scope.newAdminUsers = data;
            });


        $scope.restaurantCountParam = {
            pageSize : 1
        }

        $scope.formData = {}

        $scope.getOldCount = function() {
            $http({
                'async' : false,
                url:'/admin/api/restaurant',
                method: 'GET',
                params:{pageSize:1,adminUserId:$scope.formData.oldAdminUserId}
            }).success(function (data){
                $scope.oldAdminUserCount = data.total;
            });
        }


        $scope.getNewCount = function() {
            $http({
                async : false,
                url:'/admin/api/restaurant',
                method: 'GET',
                params:{pageSize:1,adminUserId:$scope.formData.newAdminUserId}
            }).success(function (data){
                $scope.newAdminUserCount = data.total;
            });
        }

        $scope.$watch('formData.oldAdminUserId', function(newVal) {
            if(newVal || newVal == 0) {
                $scope.getOldCount();
            }
        });

        $scope.$watch('formData.newAdminUserId', function(newVal) {
            if(newVal || newVal == 0) {
                $scope.getNewCount();
            }
        });


        $scope.updateRestaurantBatch = function() {
            $http({
                url: '/admin/api/restaurant/changeAdminUserBatch',
                method: 'PUT',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                params:$scope.formData

            }).success(function() {
                alert("修改成功！");
                $scope.getOldCount();
                $scope.getNewCount();
            }).error(function(data) {
                alert("修改失败!");
            });
        }



    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('PerformanceCtrl', function ($scope, $http, $filter) {

        $scope.month = new Date();

        $scope.queryPerformance = function() {
            $http({
                url: "/admin/api/performance",
                method: "GET",
                params: {month: $filter('date')($scope.month, 'yyyy-MM-dd')}
            }).success(function (data) {
                $scope.performances = data;
            })
        }

        $scope.queryPerformance();

        $scope.$watch('month', function() {
            $scope.queryPerformance();

            var firstDate = $scope.month.setDate(1);
            var endDate = new Date(firstDate);
            endDate.setMonth($scope.month.getMonth()+1);
            endDate.setDate(0);

            $scope.firstDate = $filter('date')(firstDate,'yyyy-MM-dd');
            $scope.endtDate = $filter('date')(endDate,'yyyy-MM-dd');
        })

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddGlobalStaffCtrl', function($scope, $state, $stateParams, $http) {

        $scope.treeData = {
            url:"/admin/api/city/blocksTree"
        }
        $scope.depotData = {
            url:"/admin/api/city/depotsTree"
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }

        $scope.readyCB = function() {
            $scope.treeInstance.jstree(true).check_node($scope.cityIds);
            $scope.treeInstance.jstree(true).check_node($scope.warehouseIds);
            $scope.treeInstance.jstree(true).check_node($scope.blockIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotCityIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotIds);
        };

        $scope.repeatPassword = null;
        $scope.formData = {
            adminRoleIds: [],
            blockIds:[]
        };

        $http.get("/admin/api/admin-role")
            .success(function(data) {
                $scope.adminRoles = data;
            });
        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/admin/api/admin-user/" + $stateParams.id).success(function(data) {
                $scope.formData.username = data.username;
                $scope.formData.realname = data.realname;
                $scope.formData.telephone = data.telephone;

                if (data.adminRoles) {
                    for (var i = 0; i < data.adminRoles.length; i++) {
                        $scope.formData.adminRoleIds.push(data.adminRoles[i].id);
                    }
                }

                $scope.cityIds = data.cityIds;
                $scope.warehouseIds = data.warehouseIds;
                $scope.blockIds = data.blockIds;
                $scope.depotCityIds = data.depotCityIds;
                $scope.depotIds = data.depotIds;


                $scope.formData.enable = data.enabled;
                $scope.readyCB();

            });
        }


        $scope.createAdminUser = function() {
            if($scope.formData.password != $scope.repeatPassword){
                window.alert("请再次确认密码！");
                return;
            }

            $scope.formData.cityWarehouseBlockIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.depotIds = $scope.depotTreeInstance.jstree(true).get_top_selected();

            $scope.formData.globalAdmin = true;

            if ($stateParams.id == '') {
                $http({
                    method: 'post',
                    url: '/admin/api/admin-user',
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("保存成功!");
                    }).error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                    method: 'put',
                    url: '/admin/api/admin-user/' + $stateParams.id,
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("修改成功！");
                    }).error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

            /*表单重置*/
            $scope.resetAdminForm = function(){
                $scope.formData = {
                    adminRoleIds: []
                };
                $scope.repeatPassword = null;
                $scope.isCheckedAll = false;
            }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListAllGlobalAdminCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

            $scope.adminUserForm = {
                page : $stateParams.page,
                pageSize : $stateParams.pageSize,
                cityId : $stateParams.cityId,
                organizationId : $stateParams.organizationId,
                username: $stateParams.username,
                realname: $stateParams.realname,
                telephone: $stateParams.telephone,
                isEnabled: 'true',
                global:true
            }

            var role;
            if ($rootScope.hasRole('CustomerServiceAssistant')) {
                $scope.adminUserForm.role = "CustomerService";
            }

            $scope.page = {
                itemsPerPage : 100
            }

            if($stateParams.page) {
                $scope.adminUserForm.page = parseInt($stateParams.page);
            }

            if($stateParams.pageSize) {
                $scope.adminUserForm.pageSize = parseInt($stateParams.pageSize);
            }
            if ($stateParams.cityId) {
                $scope.adminUserForm.cityId = parseInt($stateParams.cityId);
            }
            if ($stateParams.organizationId) {
                $scope.adminUserForm.organizationId = parseInt($stateParams.organizationId);
            }
            if ($stateParams.username) {
                $scope.adminUserForm.username = $stateParams.username;
            }

            if ($stateParams.realname) {
                $scope.adminUserForm.realname = $stateParams.realname;
            }

            if ($stateParams.telephone) {
                $scope.adminUserForm.telephone = $stateParams.telephone;
            }

            if ($stateParams.isEnabled) {
                $scope.adminUserForm.isEnabled = $stateParams.isEnabled;
            }

            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if(data.cities.length == 1){
                    $scope.adminUserForm.cityId = $scope.cities[0].id;
                }
            }

            $scope.$watch('adminUserForm.cityId',function(cityId,oldVal){
                if(cityId){
                    $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                        $scope.organizations = data;
                        if ($scope.organizations && $scope.organizations.length == 1) {
                            $scope.adminUserForm.organizationId = $scope.organizations[0].id;
                        }
                    });
                    if(typeof oldVal != 'undefined' && cityId != oldVal){
                        $scope.adminUserForm.organizationId = null;
                    }
                }else{
                    $scope.organizations = [];
                    $scope.adminUserForm.organizationId = null;
                }

            });

            $http({
                url: "/admin/api/admin-user",
                method: "GET",
                params:$scope.adminUserForm
            })
            .success(function (data) {
                $scope.users = data.adminUsers;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            });


            $scope.resetPageAndSearchForm = function () {
                $scope.adminUserForm.page = 0;
                $scope.adminUserForm.pageSize = 100;

                $location.search($scope.adminUserForm);
            }

            $scope.pageChanged = function() {
                $scope.adminUserForm.page = $scope.page.currentPage - 1;
                $scope.adminUserForm.pageSize = $scope.page.itemsPerPage;

                $location.search($scope.adminUserForm);
            }
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrganizationStaffCtrl', function($scope, $state, $stateParams, $http, $rootScope) {

        $http.get("/admin/api/admin-roles/organization").success(function(data) {
                         $scope.adminRoles = data;
         });

        $scope.treeData = {
            url:"/admin/api/city/blocksTree"
        }
        $scope.depotData = {
            url:"/admin/api/city/depotsTree"
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }

        $scope.readyCB = function() {
            $scope.treeInstance.jstree(true).check_node($scope.cityIds);
            $scope.treeInstance.jstree(true).check_node($scope.warehouseIds);
            $scope.treeInstance.jstree(true).check_node($scope.blockIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotCityIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotIds);
        };


        /* 用户角色 */
        $scope.repeatPassword = null;
        $scope.formData = {
            adminRoleIds: [],
            blockIds:[],
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId
        };

        /*获取city*/
        if($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        if($stateParams.cityId) {
            $scope.formData.cityId = parseInt($stateParams.cityId);
         }

        if($stateParams.organizationId){
            $scope.formData.organizationId = parseInt($stateParams.organizationId);
        }


        /*$scope.$watch('formData.cityId',function(cityId,oldVal){
          if(cityId){
                $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                    $scope.organizations = data;
                    if ($scope.organizations && $scope.organizations.length == 1) {
                        $scope.formData.organizationId = $scope.organizations[0].id;
                    }
                    if(typeof oldVal != 'undefined' && $scope.cities.length != 1){
                        $scope.formData.organizationId = null;
                    }
                });
           }else{
                $scope.organizations = [];
                $scope.formData.organizationId = null;
            }
       });*/


        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/admin/api/admin-user/" + $stateParams.id).success(function(data) {
                $scope.formData.username = data.username;
                $scope.formData.realname = data.realname;
                $scope.formData.telephone = data.telephone;

                if (data.adminRoles) {
                    for (var i = 0; i < data.adminRoles.length; i++) {
                        $scope.formData.adminRoleIds.push(data.adminRoles[i].id);
                    }
                }

                $scope.cityIds = data.cityIds;
                $scope.warehouseIds = data.warehouseIds;
                $scope.blockIds = data.blockIds;
                $scope.depotCityIds = data.depotCityIds;
                $scope.depotIds = data.depotIds;


                $scope.formData.enable = data.enabled;
                $scope.readyCB();

            });
        }

        $scope.createAdminUser = function() {
            if($scope.formData.password != $scope.repeatPassword){
                window.alert("请再次确认密码！");
                return;
            }
            $scope.formData.globalAdmin = false;
            $scope.formData.cityWarehouseBlockIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.depotIds = $scope.depotTreeInstance.jstree(true).get_top_selected();

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/admin-user',
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("保存成功!");
                    })
                    .error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                        method: 'put',
                        url: '/admin/api/admin-user/' + $stateParams.id,
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("修改成功！");
                    })
                    .error(function(data) {
                        alert("修改失败!");
                    });
            }
        }
        $scope.blocks = [];


       $scope.$watch('formData.organizationId',function(newVal,oldVal){
             if(newVal != "" && newVal != null){
                  $http.get('/admin/api/organization/'+newVal+'/blocks').success(function(data) {
                     $scope.blocks = data;
                 });
             }else{
                 $scope.blocks = [];
             }
       });
        $scope.isCheckedAll = false;

        /*地区权限全选、反选*/
        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                angular.forEach($scope.blocks, function(value, key){
                    $scope.formData.blockIds.push(value.id);
                });
                $scope.isCheckedAll= true;
            }else{
                angular.forEach($scope.blocks, function(value, key){
                    while ($scope.formData.blockIds.indexOf(value.id) != -1) {
                        $scope.formData.blockIds.splice($scope.formData.blockIds.indexOf(value.id), 1);
                    }
                });
                $scope.isCheckedAll = false;
            }
        };


        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                adminRoleIds: [],
                blocks:[]
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = false;
        }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListAllOrganzationAdminCtrl', function($scope, $state, $stateParams, $http,$rootScope,$location) {

        var role;
        if ($rootScope.hasRole('LogisticsAssistant')) {
            role = "LogisticsStaff";
        }
        $scope.formData = {
            page : $stateParams.page,
            pageSize : $stateParams.pageSize,
			cityId : $stateParams.cityId,
			organizationId : $stateParams.organizationId,
            username: $stateParams.username,
            realname: $stateParams.realname,
            telephone: $stateParams.telephone,
			isEnabled: 'true',
			role:role
		};

		$scope.page = {
            itemsPerPage : 100
        }


        if($stateParams.page) {
            $scope.formData.page = parseInt($stateParams.page);
        }

        if($stateParams.pageSize) {
            $scope.formData.pageSize = parseInt($stateParams.pageSize);
        }

        if ($stateParams.cityId) {
            $scope.formData.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.organizationId) {
            $scope.formData.organizationId = parseInt($stateParams.organizationId);
        }
        if ($stateParams.username) {
            $scope.formData.username = $stateParams.username;
        }

        if ($stateParams.realname) {
            $scope.formData.realname = $stateParams.realname;
        }

        if ($stateParams.telephone) {
            $scope.formData.telephone = $stateParams.telephone;
        }

        if ($stateParams.isEnabled) {
            $scope.formData.isEnabled = $stateParams.isEnabled;
        }


        $scope.globalAdmin = false;
        /*获取city*/
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if(data.cities.length == 1){
                $scope.formData.cityId = $scope.cities[0].id;
            }
        }

          $scope.$watch('formData.cityId',function(cityId,oldVal){
                if(cityId){
                    $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                        $scope.organizations = data;
                        if ($scope.organizations && $scope.organizations.length == 1) {
                            $scope.formData.organizationId = $scope.organizations[0].id;
                        }
                    });
                    if(typeof oldVal != 'undefined' && cityId != oldVal){
                        $scope.formData.organizationId = null;
                    }
                }else{
                    $scope.organizations = [];
                }

           })


         $http({
              url: "/admin/api/organization/adminUsers",
              method: "GET",
              params: $scope.formData
          }).success(function (data) {
              $scope.users = data.adminUsers;
              $scope.page.itemsPerPage = data.pageSize;
             $scope.page.totalItems = data.total;
             $scope.page.currentPage = data.page + 1;
          })


           $scope.resetPageAndSearchForm = function () {
               $scope.formData.page = 0;
               $scope.formData.pageSize = 100;

               $location.search($scope.formData);
           }

           $scope.pageChanged = function() {
               $scope.formData.page = $scope.page.currentPage - 1;
               $scope.formData.pageSize = $scope.page.itemsPerPage;

               $location.search($scope.formData);
           }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RoleCtrl', function($scope, $state, $http) {
        /* 用户角色 */
        $http.get("/admin/api/admin-role")
            .success(function(data) {
                $scope.adminRoles = data;
            });


        $http.get("/admin/api/admin-permission")
            .success(function(data) {
                $scope.permissions = data;
            })

        $scope.form = {
            permissionIds : []
        };

        $scope.$watch('form.roleId', function(v) {
            if(v) {
                $http.get('/admin/api/admin-role/' + v)
                    .success(function (data) {
                        $scope.role = data;
                        $scope.form.permissionIds.splice(0, $scope.form.permissionIds.length);
                        for(var i=0;i<data.adminPermissions.length;i++) {
                            $scope.form.permissionIds.push(data.adminPermissions[i].id);
                        }
                    })
            }
        });

        $scope.updateRole = function() {
            $http({
                url: '/admin/api/admin-role/' + $scope.form.roleId,
                method: 'PUT',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({
                    permissions: $scope.form.permissionIds
                })

            }).success(function() {
                alert("修改成功！");
            }).error(function(data) {
                alert("修改失败!");
            });;
        }



    });

/**
 * Created by challenge on 15/9/17.
 */
/**
 * Created by challenge on 15/9/16.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SalesmanCtrl', function ($scope, $http, $filter,$stateParams) {


        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();


        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.adminUsers = [{id:0, realname:"未分配销售"}].concat(data);
            })

        $scope.restaurantSearchForm = {
            adminUserId : $stateParams.adminUserId,
            start : $stateParams.start,
            end : $stateParams.end
        };

        if($scope.restaurantSearchForm.start) {
            $scope.start = Date.parse($scope.restaurantSearchForm.start);
        }

        if($scope.restaurantSearchForm.end) {
            $scope.end = Date.parse($scope.restaurantSearchForm.end);
        }

        if($stateParams.adminUserId) {
            $scope.adminUserId = parseInt($stateParams.adminUserId);
        }

        $scope.$watch('start', function(d) {
            if(d){
                $scope.restaurantSearchForm.start = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('end', function(d) {
            if(d){
                $scope.restaurantSearchForm.end= $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.resetPageAndSearchOrderList = function () {

            $http({
                url: "/admin/api/salesman-statistics",
                method: "GET",
                params: $scope.restaurantSearchForm
            })
                .success(function (data, status, headers, config) {
                    $scope.salesmanStatisticses = data;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });

        }
        $scope.resetPageAndSearchOrderList();
    });

/**
 * Created by Administrator on 2016/04/07.
 */
'use strict';
angular.module('sbAdminApp')
    .controller('CarRouteDetailCtrl',function($rootScope,$scope, $http, $stateParams,$state){
        $scope.carRouteForm = {


        };

        if($rootScope.user) {
         var data = $rootScope.user;
         $scope.cities = data.depotCities;
         /*if ($scope.cities && $scope.cities.length == 1) {
         $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
         }*/
         }

        $scope.$watch('carRouteForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.carRouteForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.carRouteForm.depotId = null;
                }

            }else{
                $scope.depots = [];
                $scope.carRouteForm.depotId = null;
            }
        });

        $scope.createRoute = function(){
            $http({
                url:"/admin/api/carRoute/update",
                method:"POST",
                data:$scope.carRouteForm,
                headers:{'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function(data){
                alert("添加成功");
                $state.go("oam.carRoute-list");
            }).error(function(data){
                console.log($scope.carRouteForm);
                alert("加载失败......");
            });
        }

        if ($stateParams.id) {
            $http.get("/admin/api/carRoute/" + $stateParams.id)
                .success(function (data) {
                    $scope.carRouteForm = data;
                })
                .error(function (data, status) {
                    window.alert("获取线路信息失败...");
                });

        }

    });



/**
 * Created by Administrator on 2016/04/06.
 */
'use strict';
angular.module('sbAdminApp')
    .controller('CarRouteListCtrl',function($scope, $q, $rootScope, $http, $filter, $state, $stateParams, $location){
        $scope.queryForm = {
            page:$stateParams.page,
            pageSize:$stateParams.pageSize,
            name:$stateParams.name,
            price:$stateParams.price,
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId
        };
        $scope.page = {};

        $scope.list = function(){
            $http({
                url:"/admin/api/carRoute/list",
                method:'GET',
                params:$scope.queryForm,
                headers:{'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function(data){
                $scope.routies = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.currentPage = data.page + 1;

                $scope.idx = data.page*data.pageSize+1;
            }).error(function(data){
                alert("加载失败......");
            });
        }

        $scope.list();

        $scope.query = function(){
            $scope.queryForm.page = 0;
            $location.search($scope.queryForm);
        }


        $scope.pageChanged = function () {
            $scope.queryForm.page = $scope.page.currentPage - 1;
            $scope.queryForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.queryForm);
        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.depotCities;
            /*if ($scope.cities && $scope.cities.length == 1) {
             $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
             }*/
        }

        $scope.$watch('queryForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.queryForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.queryForm.depotId = null;
                }

            }else{
                $scope.depots = [];
                $scope.queryForm.depotId = null;
            }
        });

        $scope.clear = function(){
            $scope.queryForm.page = "",
            $scope.queryForm.pageSize = "",
            $scope.queryForm.name = "",
            $scope.queryForm.price = "",
            $scope.queryForm.cityId = "",
            $scope.queryForm.depotId = ""
        }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('HomeCtrl', function($scope,$http,$filter, $state) {

    var today = new Date().setHours(0,0,0,0);
    var yesterday = new Date(today - 24 * 60 * 60 * 1000);
    var tomorrow = new Date(today + 24 * 60 * 60 * 1000);
    $scope.dateTimeFormat = "yyyy-MM-dd";
    var todayOrderSearchForm = {
            page: 0,
            pageSize: 1,
            start: $filter('date')(today, $scope.dateTimeFormat),
            end: $filter('date')(tomorrow, $scope.dateTimeFormat)
        };

    $scope.today = $filter('date')(today, $scope.dateTimeFormat);


    $http({
        url: '/admin/api/order',
        method: "GET",
        params: todayOrderSearchForm
    }).success(function (data, status, headers, congfig) {
        $scope.todayOrderCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var todayDeliverOrderForm = {
                page: 0,
                pageSize: 1,
                start: $filter('date')(yesterday, $scope.dateTimeFormat),
                end: $filter('date')(today, $scope.dateTimeFormat)
        };

    $http({
        url: '/admin/api/order',
        method: "GET",
        params: todayDeliverOrderForm
    }).success(function (data, status, headers, congfig) {
        $scope.todayDeliverOrderCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var uncheckedRestaurantForm = {
        page : 0,
        pageSize : 1,
        status: 1
    };

    $http({
        url: '/admin/api/restaurant',
        method: "GET",
        params: uncheckedRestaurantForm
    }).success(function (data, status, headers, congfig) {
        $scope.uncheckedRestaurantCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var unassignedRestaurantForm = {
        page : 0,
        pageSize : 1,
        adminUserId: 0
    };

    $http({
        url: '/admin/api/restaurant',
        method: "GET",
        params: unassignedRestaurantForm
    }).success(function (data, status, headers, congfig) {
        $scope.unassignedRestaurantCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    $http({
        url: '/admin/api/restaurant/alarm',
        method: 'GET',
    }).success(function (data, status, headers, congfig) {
        $scope.alarmRestaurantCount = data.restaurants.length;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    $scope.viewTodayOrder = function() {
        $state.go('oam.orderList', {start: $filter('date')(today, $scope.dateTimeFormat),
                                                end: $filter('date')(tomorrow, $scope.dateTimeFormat)});
        };

    $scope.viewTodayDeliverOrder = function() {
            $state.go('oam.orderList', {start: $filter('date')(yesterday, $scope.dateTimeFormat),
                                                    end: $filter('date')(today, $scope.dateTimeFormat)});
        };

    $scope.viewUncheckedRestaurant = function() {
            $state.go('oam.restaurantList', {status: 1});
        };

    $scope.viewUnassignedRestaurant = function() {
            $state.go('oam.restaurantList', {adminUserId: 0});
        };

});


'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('MainCtrl', function($scope,$position) {
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddMemoCtrl
 * @description
 * # AddMemoCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('AddMemoCtrl',function($scope, $http, $stateParams){
		/*添加备注表单数据集*/
 		$scope.addMemoForm = {
 			id: $stateParams.id
 		};

 		/*根据订单id获取已添加的备注信息*/
 		$http.get("/admin/api/order/" + $stateParams.id)
 		.success(function(data,status,headers,config){
			$scope.addMemoForm.memo = data.memo;
 		})
 		.error(function(data,status,headers,config){
 			window.alert("获取失败...");
 		})

 		/*添加备注表单提交请求*/
 		$scope.addMemoFun = function(){
		    if($stateParams.id != ""){
				$http({
					method: 'PUT',
					url: '/admin/api/order/' + $stateParams.id,
					data: $scope.addMemoForm,
					headers: {
						'Content-Type': 'application/json;charset=UTF-8'
					}
				})
				.success(function(data,status,headers,config){
					window.alert("添加成功!");
				})
				.error(function(data,status,headers,config){
					window.alert("添加失败...");
				});
			}
		};

	});
angular.module('sbAdminApp')
    .controller('AddBlockCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }
        $scope.formData = {

        };

         $scope.$watch('formData.cityId', function(cityId) {
             if(cityId != null && cityId != '') {
                 $http.get("/admin/api/city/"+cityId+"/warehouses").success(function(data) {
                     $scope.warehouses = data;
                 });
             } else {
                 $scope.warehouses = [];
             }
         });
        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/admin/api/block/" + $stateParams.id).success(function(data) {
                $scope.formData.blockName = data.name;
                $scope.formData.cityId = data.city.id;
                $scope.formData.warehouseId = data.warehouse.id;
                $scope.formData.active = data.active;
                $scope.formData.pointStr = data.pointStr;
            });
        }

        $scope.createBlock = function() {
            if($stateParams.id == "") {
                $http({
                    method: 'post',
                    url: '/admin/api/block',
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败!");
                    });
            }else{
                $http({
                    method: 'put',
                    url: '/admin/api/block/' + $stateParams.id,
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                }).success(function(data) {
                    alert("修改成功！");
                }).error(function(data) {
                    alert("修改失败!");
                });

            }
        }
        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                blockIds: []
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = false;
        }

    });

angular.module('sbAdminApp')
    .controller('ListBlockCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location) {

        $scope.blockListSearchForm = {
                    page: $stateParams.page,
                    pageSize:$stateParams.pageSize,

                    blockId:$stateParams.blockId,
                    blockName:$stateParams.blockName,
                    warehouseId:$stateParams.warehouseId,
                    enable:$stateParams.status,
                    cityId:$stateParams.cityId
         };

       if($rootScope.user) {
            var data = $rootScope.user;
             $scope.availableCities = data.cities;  
        }

       $scope.page={
            blocksPerPage:100
       }

        $scope.resetPageAndSearchBlockList = function () {
            $scope.blockListSearchForm.page = 0;
            $scope.blockListSearchForm.pageSize = 100;
            $scope.SearchBlockList();
        }
          $scope.SearchBlockList = function () {
                     $http({
                         url: '/admin/api/block',
                         method: "GET",
                         params: $scope.blockListSearchForm
                     }).success(function (data, status, headers, config) {
                         $scope.blocks = data.blocks;
                         /*分页数据*/
                          $scope.page.blocksPerPage = data.pageSize;
                          $scope.page.totalBlocks = data.total;
                         $scope.page.currentPage = data.page + 1;
                     }).error(function (data, status, headers, config) {
                         window.alert("搜索失败...");
                     });
           }
            $scope.SearchBlockList();

                  $scope.updateBlock = function(block ,active){
                   	$http({
                           method: 'put',
                           url: '/admin/api/block/' + block.id,
                           data: 'active=' + active,
                           headers: {
                               'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                           }
                       })
                       .success(function(data) {
                       	window.alert("保存成功!");
                           block.active = data.active;
                       })
                       .error(function(data) {
                       	window.alert("保存失败!");
                       });
                   }

                   $scope.editSaveBlock = function(warehouse, block) {
                       var saveBlockWarehouse = function(){
                           $http({
                                  method: 'PUT',
                                  url: '/admin/api/block/' + block.id,
                                  data: 'warehouseId=' + warehouse.warehouseId,
                                  headers: {
                                      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                                  }
                              })
                              .success(function(data) {
                                   block.warehouse.id = data.warehouse.id;
                                   block.warehouse.name = data.warehouse.name;
                                   window.alert("修改成功!");
                              })
                              .error(function(data) {
                                   window.alert("修改失败!");
                              });
                       }

                       return saveBlockWarehouse();
                   };


        $scope.$watch('blockListSearchForm.cityId',function(newVal,oldVal){
                if(newVal != null && newVal != ""){
                    $http.get("/admin/api/city/"+newVal+"/warehouses").success(function(data) {
                        $scope.availableWarehouses = data;
                    });

                    if(typeof oldVal != 'undefined' && newVal != oldVal){
                        $scope.blockListSearchForm.warehouseId = null;
                    }
                }else{
                    $scope.availableWarehouses = [];
                    $scope.blockListSearchForm.warehouseId = null;
                }
                $scope.SearchBlockList();

        })


          /**分页数据*/
          $scope.pageChanged = function() {
                    $scope.blockListSearchForm.page = $scope.page.currentPage - 1;
                    $scope.blockListSearchForm.pageSize = $scope.page.blocksPerPage;

                    $scope.SearchBlockList();
           }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('cityDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.isEdit = false;
        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/city/" + $stateParams.id).success(function(data) {
                $scope.city = data;
            });
        }

        $scope.createCity = function() {

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/city',
                        data: "name=" + $scope.city.name,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("保存成功!");
                    })
                    .error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                        method: 'put',
                        url: '/admin/api/city/' + $stateParams.id,
                        data: "name=" + $scope.city.name,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("修改成功！");
                    })
                    .error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListCityCtrl', function ($scope, $http, $stateParams) {

        $http({
            url: '/admin/api/city',
            method: "GET",
        }).success(function (data, status, headers, congfig) {
            $scope.cities = data;
        });

    });

'use strict';

angular.module('sbAdminApp')
    .controller('OrderLimitListCtrl', function ($scope, $http, $stateParams) {
        $http({
            url: '/admin/api/conf/orderLimit/list',
            method: "GET",
        }).success(function (data, status, headers, congfig) {
            $scope.orderLimitList = data;
        });

        $scope.checkLimit = function(data) {
            var REGEX = /^\-?\d+(.\d+)?$/
            if (!data || !REGEX.test(data)) {
                return "请输入数字";
            }
        }

        $scope.saveOrderLimit = function(orderLimit) {
            var postData = {};
            postData.name = 'order_limit';
            postData.key = orderLimit.city.id;
            postData.value = orderLimit.limit;
            $http({
                url: "/admin/api/conf/save",
                method: "POST",
                data: postData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("保存成功...");
            })
            .error(function (data, status, headers, config) {
                alert("保存失败...");
            });
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrganizationCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        /*$scope.treeData = {
            url : '/admin/api/category/treeJson'
        }*/

        $scope.cityBlockData = {
            url : '/admin/api/city/blocksTree'
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }


        $scope.readyCB = function() {
//            $scope.treeInstance.jstree(true).check_node($scope.formData.categoryIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.blockIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.warehouseIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.cityIds);
        };


        $scope.formData = {
            blockIds: [],
            categoryIds:[]
        };

        $scope.$watch('formData.cityId', function(cityId) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/"+cityId+"/warehouses").success(function(data) {
                    $scope.warehouses = data;
                });
            } else {
                $scope.warehouses = [];
            }
        });




        $scope.isEdit = false;
        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/organization/" + $stateParams.id).success(function(data) {
                $scope.formData.id = data.id;
                $scope.formData.name = data.name;
//                $scope.formData.cityId = data.city.id;
                $scope.formData.telephone = data.telephone;

                $scope.cityIds = data.cityIds;
                $scope.warehouseIds = data.warehouseIds;
                $scope.blockIds = data.blockIds;

                if (data.blocks) {
                    for (var i = 0; i < data.blocks.length; i++) {
                        $scope.formData.blockIds.push(data.blocks[i].id);
                    }
                }

                $scope.formData.categoryIds = data.categoryIds;
                $scope.readyCB();
                $scope.formData.enable = data.enabled;
            });
        }


        $scope.createOrganization = function() {
//            $scope.formData.categoryIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.cityWarehouseBlockIds = $scope.cityWarehouseBlockInstance.jstree(true).get_top_selected();

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/organization',
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("保存成功!");
                    })
                    .error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                        method: 'put',
                        url: '/admin/api/organization/' + $stateParams.id,
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("修改成功！");
                    })
                    .error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

        $scope.isCheckedAll = [];

        /*地区权限全选、反选*/
        $scope.checkAll = function(warehouse) {
            if(!($scope.isCheckedAll[warehouse.id])){
                angular.forEach(warehouse.blocks, function(value, key){
                    $scope.formData.blockIds.push(value.id);
                });
                $scope.isCheckedAll[warehouse.id] = true;
            }else{
                angular.forEach(warehouse.blocks, function(value, key){
                    while ($scope.formData.blockIds.indexOf(value.id) != -1) {
                        $scope.formData.blockIds.splice($scope.formData.blockIds.indexOf(value.id), 1);
                    }
                });
                $scope.isCheckedAll[warehouse.id] = false;
            }
        };

        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                blockIds: []
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = [];
        }
        
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListOrganizationCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();

        $scope.page = {
            itemsPerPage: 100
        };

        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.organizations = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            createDate: $stateParams.createDate,
            serviceAdminId: $stateParams.serviceAdminId,
            name: $stateParams.name,
        };

        if($scope.orderListSearchForm.createDate) {
            $scope.createDate = Date.parse($scope.orderListSearchForm.createDate);
        }

        if($stateParams.status) {
            $scope.orderListSearchForm.status = parseInt($stateParams.status);
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        if($stateParams.cityId) {
            $scope.orderListSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.name) {
            $scope.orderListSearchForm.name = parseInt($stateParams.name);
        }

        $scope.$watch('createDate', function(d) {
            if(d){
                $scope.orderListSearchForm.createDate = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;

            $scope.searchOrganizationList();
        }

        $scope.searchOrganizationList = function () {
            $location.search($scope.orderListSearchForm);
            
            $http({
                url: '/admin/api/organization',
                method: "GET",
                params: $scope.orderListSearchForm
            }).success(function (data, status, headers, congfig) {
                $scope.organizations = data.organizations;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.searchOrganizationList();

        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrganizationList();
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateSystemEmailCtrl
 * @description
 * # AddOrUpdateSystemEmailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateSystemEmailCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.systemEmail = {};
        $scope.submitting = false;
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.systemEmail.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/systemEmail/type/list").success(function (data) {
            $scope.type = data;
        });

        $scope.isEdit = false;

        /*根据id获取信息*/
        if ($stateParams.id != null && $stateParams.id != '') {
            $scope.isEdit = true;
            $http.get("/admin/api/systemEmail/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.systemEmail = data;
                });
        }

        /*添加/编辑 */
        $scope.createSystemEmail = function () {

            $scope.submitting = true;
            if ($scope.isEdit) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/systemEmail/' + $stateParams.id,
                    data: $scope.systemEmail,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                    $scope.submitting = false;
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/systemEmail',
                    data: $scope.systemEmail,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                    $scope.submitting = false;
                })
            }
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SystemEmailListCtrl
 * @description
 * # SystemEmailListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('SystemEmailListCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.systemEmails = {};
	    $scope.formData = {};
	    $scope.page = {itemsPerPage : 100};
	    $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/systemEmail/type/list").success(function (data) {
            $scope.type = data;
        });

        $scope.isCheckedAll = false;
        $scope.formData.systemEmailIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.systemEmailIds = [];
                angular.forEach($scope.systemEmails, function(value, key){
                    $scope.formData.systemEmailIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.systemEmailIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.systemEmails = [];
            $scope.formData.systemEmailIds = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/systemEmail/list',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.systemEmails = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.batchDelete = function () {
            if ($scope.formData.systemEmailIds.length == 0) {
                alert("请选择要删除的设置");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/systemEmail/del",
                method: "DELETE",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("删除成功...");
                $scope.submitting = false;
                $scope.searchForm();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "删除失败...");
                $scope.submitting = false;
            });
        };

	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('WarehouseDetailCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        $scope.isEdit = false;

        $scope.warehouse = {
            cityId:undefined,
            depotId:undefined
        };

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/warehouse/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                $scope.warehouse = data;
                $scope.warehouse.cityId = data.city.id;
                if (data.depot) {
                    $scope.warehouse.depotId = data.depot.id;
                }

            });
        }

        $scope.$watch('warehouse.cityId', function(cityId, old) {
            $http({
                method:"GET",
                url:"/admin/api/depot/list",
                params:{cityId:cityId}
            })
            .success(function(data) {
                $scope.depot = data;
            });
        });

        $scope.createCity = function() {

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/warehouse',
                        data: $scope.warehouse,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("保存成功!");
                    })
                    .error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                        method: 'put',
                        url: '/admin/api/warehouse/' + $stateParams.id,
                        data: $scope.warehouse,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("修改成功！");
                    })
                    .error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListWarehouseCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.$watch('formData.cityId', function(cityId) {
            $http({
                url: '/admin/api/warehouse',
                method: "GET",
                params: {"cityId":cityId}
            }).success(function (data, status) {
                $scope.warehouses = data;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        });


        $scope.updateWarehouse = function(warehouse ,isDefault){
            $http({
                method: 'put',
                url: '/admin/api/warehouse/isDefault/' + warehouse.id,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
            .success(function(data) {
                window.alert("保存成功!");
                $scope.warehouses = data;
            })
            .error(function(data) {
                window.alert("保存失败!");
            });
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('LoginCtrl', function($scope, $position) {

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:UpdatePwdCtrl
 * @description
 * # UpdatePwdCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('UpdatePwdCtrl', function($scope, $http) {
    	$scope.reset = function(){
  			$scope.oldPassword = null;
	    	$scope.newPassword = null;
	    	$scope.repeatNewPassword = null;
  		}
  		
    	$scope.reset();

  		$scope.updatePassword = function(){
  			if($scope.newPassword != $scope.repeatNewPassword){
  				window.alert("请再次确认新密码！");
                return;
  			}else{
  				$http({
                        method: 'PUT',
                        url: '/admin/api/admin-user/me/password',
                        params: {
                        	oldPassword: $scope.oldPassword,
                        	newPassword: $scope.newPassword
                        },
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                    .success(function(data) {
                        window.alert("密码修改成功!");
                    })
                    .error(function(data) {
                        window.alert("密码修改失败!");
                    });
  			}
  		}
    });


'use strict';

angular.module('sbAdminApp')
    .controller('UpdateDailyPushCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

        $scope.dailyPushForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/push/daily/" + $stateParams.id).success(function (data) {
                $scope.dailyPushForm = data;
            })
        }
        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.dailyPushForm.tag == null) {
                $scope.dailyPushForm.tag = $scope.cities[0].name;
            }
        }
        $scope.updateDailyPush = function () {
            $http({
                method: 'POST',
                url: '/admin/api/push/daily/update',
                data: $scope.dailyPushForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
                $state.go("oam.dailyPush");
            }).error(function (data) {
                alert("操作失败..." + data.errmsg);
            })
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('DailyPushListCtrl', function ($scope, $rootScope, $http, $state) {
        $http({
            url: "/admin/api/push/daily/list",
            method: 'GET',
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.pushes = data;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.deleteDailyPush = function (id) {
            $http.delete("/admin/api/push/daily/" + id).success(function () {
                alert("操作成功...");
                $state.reload();
            }).error(function () {
                alert("操作失败...");
            })
        }
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:updateCustomerPassCtrl
 * @description
 * # updateCustomerPassCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('updateCustomerPassCtrl', function($scope, $http, $stateParams) {

 	$scope.updateCustomerPass = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/restaurant/updatePassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
 			if (data) {
 				window.alert("修改成功!");
 			} else {
 				window.alert("用户不存在!");
			}
 		})
 		.error(function(data) {
 			window.alert("修改失败!");
 		});
 	};
 });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SalesDistributeCtrl
 * @description
 * # SalesDistributeCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('SalesDistributeCtrl', function($scope, $http, $stateParams) {
 	/*分配销售表单数据集*/
 	$scope.adminUserId = 0;
 	$scope.formData = {};

 	/*获取销售注册号*/
 	if ($stateParams.id != "") {
 		$http.get("/admin/api/restaurant/" + $stateParams.id)
 		.success(function(data, status, headers, config) {
 			$scope.telephone = data.telephone;

 			if (data.customer.adminUser) {
 				$scope.formData.adminUserId = data.customer.adminUser.id;
 			}
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("获取失败！");
 		});
 	}

 	/*获取销售*/
 	$http.get("/admin/api/admin-user/global")
 	.success(function(data) {
 		$scope.adminUsers = data;
 	})

 	/*分配销售表单提交请求*/
 	$scope.assignAdminUser = function() {
 		$http({
 			method: 'PUT',
 			url: '/admin/api/restaurant/' + $stateParams.id + '/admin-user',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("分配成功!");
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("分配失败！");
 		});
 	};
 });

'use strict';

angular.module('sbAdminApp')
    .controller('UpdateVersionCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.versionForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/version/" + $stateParams.id).success(function (data) {
                $scope.versionForm = data;
            })
        }
        $scope.updateVersion = function () {
            $http({
                method: 'POST',
                url: '/admin/api/version/update',
                data: $scope.versionForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
            }).error(function () {
                alert("操作失败...");
            })
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('VersionListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.searchForm = {
            pageSize: 100
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $http({
            url: "/admin/api/version/list",
            method: 'GET',
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.versions = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.searchForm);
        };
    });
'use strict';
angular.module('sbAdminApp')
    .controller('CreateBannerCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.isEditBanner = false;

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();



        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });


        $scope.banner = {
            bannerUrl:{
                imgUrl:null,
                redirectUrl:null
            }
        }


        $scope.$watch('banner.cityId', function (newVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                    $scope.warehouses.push({
                        "id": 0,
                        "name": "全城",
                        "city": {},
                        "displayName": "全城"
                    });
                });
            } else {
                $scope.warehouses = [];
            }
        })



        if ($stateParams.id) {
            $scope.isEditBanner = true;

            $http.get("/admin/api/banner/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.banner = data;
                })
                .error(function (data, status) {
                    window.alert("获取banner信息失败...");
                });

        }


        /*添加/编辑banner*/
        $scope.createBanner = function () {


            if ($stateParams.id != '' && $stateParams.id != undefined) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/banner/' + $stateParams.id,
                    data: $scope.banner,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {

                $http({
                    method: 'POST',
                    url: '/admin/api/banner/create',
                    data: $scope.banner,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }
        //上传图片
        $scope.$watch('media', function (files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        //$scope.banner.imgUrl = data.url;
                        $scope.banner.bannerUrl.imgUrl = data.url;
                       $scope.banner.mediaFileId = data.id;
                    })
                }
            }
        })

    });
'use strict';

angular.module('sbAdminApp')
	.controller('BannerListCtrl', function($scope, $http) {
		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

		$scope.date = new Date().toLocaleDateString();

		$scope.cityId = 0;

		$scope.$watch('cityId',function(){
			$http.get("/admin/api/banners/"+$scope.cityId)
				.success(function(data){
					$scope.banners = data;
				})
				.error(function(data){

				});
		});


		//获取城市
		$http.get("/admin/api/admin-user/me")
			.success(function (data, status, headers, config) {
				$scope.cities = data.cities;
			});

		/*$http({
			method:'POST',
			url:'/admin/api/banner',
			data:$scope.banner,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}


		});*/
	});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
    .controller('CreateCouponCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $filter) {

        $scope.init = function(couponType, send) {
            $scope.showDiscount = [1,2,3,4,6,7,8].indexOf(couponType) >= 0;
            $scope.showSkuId = [5].indexOf(couponType) >= 0;
            $scope.showQuantity = [5].indexOf(couponType) >= 0;
            $scope.showSendRestrictionsTotal = [1,5,8].indexOf(couponType) >= 0;
            $scope.showSendRestrictionsCategories = [1,5,8].indexOf(couponType) >= 0;
            $scope.showUseRestrictionsCategories = [1,3,5,7,8].indexOf(couponType) >= 0;
            $scope.showDeadline = [5,8].indexOf(couponType) >= 0;
            $scope.showCityId = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showWarehouseId = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showName = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showUseRestrictionsTotal = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showStart = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showEnd = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showDescription = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showRemark = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showPeriodOfValidity = [1,3,6].indexOf(couponType) >= 0;
            $scope.showScore = [7].indexOf(couponType) >= 0;
            $scope.showBuySkuId = [8].indexOf(couponType) >=0;
            $scope.showBuySkuUnit = [8].indexOf(couponType) >=0;
            $scope.showBuyQuantity = [8].indexOf(couponType) >=0;
            $scope.showSendCouponQuantity = [8].indexOf(couponType) >=0;
            $scope.showBeginningDays = [3].indexOf(couponType) >=0;
            $scope.showBrandId = [1,2,5].indexOf(couponType) >= 0;
            $scope.showCouponRestriction = [1,5,8].indexOf(couponType) >= 0;
        }

        $scope.skuUnit = [
            {
                "name" : "请选择sku单位"
            },
            {
                "id" : true,
                "name" : "打包"
            },
            {
                "id" : false,
                "name" : "单品"
            }
        ]

        $scope.promotionPatterns = [
            {
                "name" : "请选择活动方式"
            },
            {
                "id": 1,
                "name": "满足条件就可以参加活动"
            },
            {
                "id": 2,
                "name": "今日首单"
            },
            {
                "id": 3,
                "name": "餐馆首单"
            }
        ]

        $scope.init(null, null);

        if ($stateParams.id && $stateParams.couponType == null) {
            $http.get("/admin/api/coupon/" + $stateParams.id).then(function(result) {
                $state.go($state.current, {couponType:result.data.couponType}, {reload: true});
            })
        } else {

            $scope.restaurants = [];

            $scope.candidateRestaurants = [];

            $scope.funcAsync = function (name) {
                if (name && name !== "") {
                    $scope.candidateRestaurants = [];
                    $http.get("/admin/api/restaurant/candidates?page=0&pageSize=20&name="+name).then(
                        function (data) {
                            $scope.candidateRestaurants = data.data;
                        }
                    )
                }
            }

            $scope.searchRestaurant = function(restaurant) {
                $scope.candidateRestaurants = [];

                $http.get("/admin/api/restaurant/" + restaurant.id).success(function (data, status, headers, config) {
                    if (!data) {
                        alert('sku不存在或已失效');
                        restaurant.id = '';
                        return;
                    }
                    $scope.candidateRestaurants.push(data);
                }).error(function (data, status, headers, config) {
                    alert('sku不存在或已失效');
                    restaurant.id = '';
                    return;
                });
            };

            $scope.$watch('coupon.cityId',function(newVal, oldVal){
                if(newVal){
                    $http.get("/admin/api/city/" + newVal + "/warehouses").success(function(data) {
                        $scope.warehouses = data;
                    });
                }else{
                    $scope.warehouses = [];
                }
            });

            $scope.resetCandidateRestaurants = function () {
                $scope.candidateRestaurants = [];
            }

            $scope.addItem = function() {
                $scope.inserted = {
                };
                $scope.restaurants.push($scope.inserted);
            };

            $scope.remove = function(index) {
                $scope.restaurants.splice(index, 1);
            };

            $scope.coupon = {
                categoryIds:[]
            };
            if ($stateParams.type == 1) {
                $scope.add = true;
            } else if ($stateParams.type == 2) {
                $scope.edit = true;
            } else if ($stateParams.type == 3) {
                $scope.send = true;
            } else if ($stateParams.type == 4) {
                $scope.batchSendCoupon = true;
            }
            var couponType = $stateParams.couponType;

            if (couponType != null) {
                $scope.init(parseInt(couponType), $scope.send);
            }

            $scope.categories = {
                url : '/admin/api/category/treeJson'
            }

            $scope.treeConfig = {
                'plugins': ["wholerow", "checkbox"],
            }

            $scope.changeCouponType = function() {
                $state.go($state.current, {couponType:$scope.coupon.couponType}, {reload: true});
            };

            $http.get("/admin/api/admin-user/me")
                .success(function (data, status, headers, config) {
                    $scope.cities = data.cities;
                });

            $http.get("/admin/api/brand")
                .success(function (data) {
                    $scope.brands = data;
                })

            $scope.readyCB = function() {
                if ($scope.sendRestrictionsTreeInstance) {
                    $scope.sendRestrictionsTreeInstance.jstree(true).check_node($scope.sendRestrictionsCategoryIds);
                }

                if ($scope.useRestrictionsTreeInstance) {
                    $scope.useRestrictionsTreeInstance.jstree(true).check_node($scope.useRestrictionsCategoryIds);
                }
            };

            $http.get("/admin/api/coupon/couponEnums")
                .success(function (data, status, headers, config) {
                    $scope.couponTypes = data;
                });

            $http.get("/admin/api/coupon/sendCouponReasons")
                .success(function (data, status, headers, config) {
                    $scope.sendCouponReasons = data;
                });

            if ($stateParams.id) {
                $http.get("/admin/api/coupon/" + $stateParams.id).then(function(result) {
                    $scope.coupon = result.data;
                    $scope.sendRestrictionsCategoryIds = result.data.sendRestrictionsCategoryIds;
                    $scope.useRestrictionsCategoryIds = result.data.useRestrictionsCategoryIds;
                    $scope.readyCB();
                })
            }

            $scope.createCoupon = function () {
                if ($scope.sendRestrictionsTreeInstance) {
                    $scope.coupon.sendRestrictionsCategoryIds = $scope.sendRestrictionsTreeInstance.jstree(true).get_top_selected();
                }

                if ($scope.useRestrictionsTreeInstance) {
                    $scope.coupon.useRestrictionsCategoryIds = $scope.useRestrictionsTreeInstance.jstree(true).get_top_selected();
                }

                if ($stateParams.id != '') {
                    $http({
                        method: 'PUT',
                        url: '/admin/api/coupon/edit/' + $stateParams.id,
                        data: $scope.coupon,
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                        .success(function (data, status, headers, config) {
                            alert("修改成功!");
                            $state.go("oam.couponManagement");
                        })
                        .error(function (data, status, headers, config) {
                            alert("修改失败!");
                        })
                } else {
                    $http({
                        method: 'POST',
                        url: '/admin/api/coupon/create',
                        data: $scope.coupon,
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                        .success(function (data, status, headers, config) {
                            alert("添加成功!");
                            $state.go("oam.couponManagement");
                        })
                        .error(function (data, status, headers, config) {
                            alert("添加失败!");
                        })
                }
            }

            $scope.sendCouponRequest = {};

            $scope.sending = false;
            $scope.sendCouponToCustomers = function () {
                $scope.sending = true;

                if ($scope.send) {
                    $scope.sendCouponRequest.restaurantIds = [];
                    angular.forEach($scope.restaurants, function(item, key) {
                        $scope.sendCouponRequest.restaurantIds.push(item.id);
                    });
                } else if ($scope.batchSendCoupon) {
                    $scope.sendCouponRequest.restaurantIds = $scope.sendCouponRequest.restaurantIds.split('\n');
                }

                $scope.sendCouponRequest.couponId = $stateParams.id;

                $http({
                    method: 'PUT',
                    url: '/admin/api/coupon/send',
                    data: $scope.sendCouponRequest,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    $scope.restaurants = [];
                    $scope.sendCouponRequest.restaurantIds = [];
                    alert("发送成功!");
                    $scope.sending = false;
                })
                .error(function (data, status, headers, config) {
                    alert("发送失败!");
                    $scope.sending = false;
                })

            }

            $scope.changeSendCouponReason = function() {
                if ($scope.sendCouponRequest.reason == 10) {
                    $scope.sendCouponRequest.remark = '';
                } else {
                    var reason = $filter('filter')($scope.sendCouponReasons, {value:  $scope.sendCouponRequest.reason});
                    $scope.sendCouponRequest.remark = reason.length ? reason[0].name : '';
                }
            }
        }
    });

'use strict';

angular.module('sbAdminApp')
	.controller('CouponListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
				$scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$http.get("/admin/api/coupon/couponEnums")
			.success(function (data, status, headers, config) {
				$scope.couponTypes = data;
			});

		$http.get("/admin/api/purchase/order/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$http({
			url: "/admin/api/coupon",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.coupons = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.couponTypeFilter = function(item) {
			if($rootScope.hasPermission('coupon-list')) {
				return true;
			} else if ($rootScope.hasPermission('custom-service-coupon-list')) {
				return item.type == 6;
			} else {
				return false;
			}
		}

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}
	});
'use strict';

angular.module('sbAdminApp')
    .controller('couponStatisticsListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        //加载优惠券类别
        $http.get("/admin/api/coupon/couponEnums").success(function (data, status, headers, config) {
            $scope.couponTypes = data;
        });
        //加载优惠券状态
        $http.get("/admin/api/coupon/couponStatus").success(function (data, status, headers, config) {
            $scope.couponStatus = data;
        });
        //alert(222);
        //获取数据
        console.log($scope.searchForm);
        $http({
            method: 'GET',
            url: '/admin/api/coupon/statistics/provide',
            params: $scope.searchForm
        })
        .success(function (data, status, headers, config) {
            $scope.listData = data.content;
            $scope.lineTotal = data.lineTotal;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        })
        .error(function (data, status, headers, config) {
            alert("查询失败");
        })

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/coupon/statistics/search/export?" + str.join("&"));
        };

        $scope.setCanceled=function(couponStatistics){
            if(!confirm("确认作废吗？（优惠券id:"+couponStatistics.couponId+", 餐馆id:"+couponStatistics.restaurantId+"）")){
                return ;
            }
            var ccid= couponStatistics.customerCouponId;
            $http.get("/admin/api/coupon/customer/cancelled/"+ccid).success(function (data) {
                console.log(data);
                console.log(couponStatistics);
                couponStatistics.couponStatus = data.status.value;
                couponStatistics.couponStatusDesc = data.status.name;
                couponStatistics.operater = data.operater;
                couponStatistics.operateTime =data.operateTime;

            }).error(function (data, status, headers, config) {
                alert("操作失败");
            });

        }

    });
'use strict';

angular.module('sbAdminApp')
.controller('couponStatisticsUsedListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            pageSize: 20,
            listType: $stateParams.listType == null?  "TJ" : $stateParams.listType
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        //$scope.$watch("searchForm.listType",function(newVal, oldVal){
        //    alert(newVal);
        //});

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });

        $scope.listTypeChange=function () {
            $scope.searchForm.orderId = null;
            $scope.searchForm.sendFront = null;
            $scope.searchForm.sendBack = null;

            $scope.searchForm.orderDateFront = null;
            $scope.searchForm.orderDateBack = null;
            $scope.searchForm.stockoutDateFront = null;
            $scope.searchForm.stockoutDateBack = null;

            if ($scope.searchForm.listType != listTypeContent.SYMX.key && $scope.searchForm.listType != listTypeContent.TJ.key) {
                $scope.searchForm.useFront = null;
                $scope.searchForm.useBack = null;
            }


        };

        //加载优惠券类别
        $http.get("/admin/api/coupon/couponEnums").success(function (data, status, headers, config) {
            $scope.couponTypes = data;
        });
        //加载优惠券状态
        $http.get("/admin/api/coupon/couponStatus").success(function (data, status, headers, config) {
            $scope.couponStatus = data;
        });
        $scope.listTypeContent={
            "TJ":{
                key:"TJ",
                url:"/admin/api/coupon/statistics/used",
                exportUrl:"/admin/api/coupon/statistics/used/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            },
            "FFMX":{
                key:"FFMX",
                url:"/admin/api/coupon/statistics/provide",
                exportUrl:"/admin/api/coupon/statistics/provide/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            },
            "SYMX":{
                key:"SYMX",
                url:"/admin/api/coupon/statistics/usedDetail",
                exportUrl:"/admin/api/coupon/statistics/usedDetail/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            }
        };
        $scope.listType=$scope.searchForm.listType;
        $scope.loadData=function(){
            var cTypeUrl=$scope.listTypeContent[$scope.searchForm.listType];
            if(cTypeUrl!=null) {
                $http({
                    method: 'GET',
                    url: cTypeUrl.url,
                    params: $scope.searchForm
                }).success(cTypeUrl.success).error(function (data, status, headers, config) {
                        alert("查询失败");
                 })
            }
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            var cTypeUrl=$scope.listTypeContent[$scope.searchForm.listType];
            $window.open(cTypeUrl.exportUrl+"?" + str.join("&"));
        };

        $scope.loadData();
        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('feedbackDetailCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

    $http({
        method: 'GET',
        url: "/admin/api/feedback/"+$stateParams.feedbackId
    }).success(function (data, status, headers, config) {
        $scope.data = data;
    }).error(function (data, status, headers, config) {
        alert("查询失败");
    })



});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('feedbackListCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $location, $window) {

        $scope.searchForm = {
            pageSize: 20
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }
        if ($stateParams.pageSize) {
            $scope.searchForm.pageSize = parseInt($stateParams.pageSize);
        }

        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http.get("/admin/api/feedback/status")
            .success(function (data, status, headers, config) {
                $scope.feedbackStatus = data;
            });

        $http.get("/admin/api/feedback/type")
            .success(function (data, status, headers, config) {
                $scope.feedbackTypes = data;
            });

        $http({
            method: 'GET',
            url: "/admin/api/feedback/list",
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.listData = data.content;
            $scope.lineTotal = data.lineTotal;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("查询失败");
        })

        $scope.search = function () {
            $scope.searchForm.page = 0;
            console.log($scope.searchForm);
            $location.search($scope.searchForm);
        }
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/feedback/export?" + str.join("&"));
        };




});
/**
 * Created by challenge on 15/10/26.
 */
'use strict';
//var sbAdminAppModel = angular.module('sbAdminApp', ['ngMessages','ui.map']);
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
    .controller('CreatePromotionCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        $http.get("/admin/api/brand")
            .success(function (data) {
                $scope.brands = data;
            })

        $scope.categories = {
            url: '/admin/api/category/treeJson'
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }

        $scope.promotion = {
            categoryIds: []
        };

        $scope.promotionPatterns = [
            {
                "name" : "请选择活动方式"
            },
            {
                "id": 1,
                "name": "满足条件就可以参加活动"
            },
            {
                "id": 2,
                "name": "今日首单"
            },
            {
                "id": 3,
                "name": "餐馆首单"
            }
        ]

        $scope.skuUnit = [
            {
                "name" : "请选择sku单位"
            },
            {
                "id" : true,
                "name" : "打包"
            },
            {
                "id" : false,
                "name" : "单品"
            }
        ]

        $scope.promotionTypeValidation = [

            {
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": true,
                "skuId": false,
                "quantity": false,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : false,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : false
            },

            {
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : true,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : false
            },
            {//买一赠一
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : true,
                "buySkuUnit" : true,
                "buyQuantity" : true,
                "buySkuId" : true,
                "limited" : false,
                "brandId" : false
            }
        ];

        $scope.promotionTypeShowArray = [
            {//满减活动
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": true,
                "skuId": false,
                "quantity": false,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : false,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : true
            },
            {//满赠活动(赠物品)
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : true,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : true,
                "brandId" : true
            },
            {//买一赠一
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : true,
                "buySkuUnit" : true,
                "buyQuantity" : true,
                "buySkuId" : true,
                "limited" : true,
                "brandId" : false
            }
        ];

        $scope.changePromotionType = function () {
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeShow = $scope.promotionTypeShowArray[promotionType - 1];
            for (var key in promotionTypeShow) {
                if (promotionTypeShow[key]) {
                    angular.element("[name=" + key + "]").parent().removeClass('ng-hide');
                } else {
                    angular.element("[name=" + key + "]").parent().addClass('ng-hide');
                }
            }
        }

        $scope.disableByName = function (name) {
            angular.element("[name=" + name + "]").attr("disabled", "disabled");
        }

        $scope.tableInvalid = function () {
            if (!$scope.promotion) {
                return true;
            }
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeValidation = $scope.promotionTypeValidation[promotionType - 1];
            for (var key in promotionTypeValidation) {
                if (promotionTypeValidation[key] && $scope.addPromotionForm[key].$invalid) {
                    return true;
                }
            }

            return false;
        }

        $scope.sendInvalid = function () {
            if (!$scope.promotion) {
                return true;
            }
            return $scope.addPromotionForm["restaurants"].$invalid;
        }

        $scope.disabledPromotion = function () {
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeValidation = $scope.promotionTypeValidation[promotionType - 1];
            for (var key in promotionTypeValidation) {
                angular.element("[name=" + key + "]").attr("disabled", "disabled");
            }
            angular.element("[type=submit]").attr("disabled", "disabled");
        }

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });

        $scope.$watch('promotion.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                });
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                });

            } else {
                $scope.warehouses = [];
                $scope.organizations = [];
            }
        });

        $scope.readyCB = function () {
            $scope.useRestrictionsTreeInstance.jstree(true).check_node($scope.useRestrictionsCategoryIds);
        };

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
            });

        if ($stateParams.id) {
            $http.get("/admin/api/promotion/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.promotion = data;
                    //$scope.promotion.start = new Date(data.start).toISOString();
                    //$scope.promotion.end = new Date(data.end).toISOString();
                    $scope.useRestrictionsCategoryIds = data.useRestrictionsCategoryIds;
                    $scope.promotion.promotionPattern = data.promotionPattern;
                    $scope.readyCB();
                    $scope.changePromotionType();
                })
                .error(function (data, status) {
                    window.alert("获取无忧券信息失败...");
                    return;
                });
        } else {
            $scope.addPromotion = true;
        }

        $scope.createPromotion = function () {
            $scope.promotion.useRestrictionsCategoryIds = $scope.useRestrictionsTreeInstance.jstree(true).get_top_selected();
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/promotion/edit/' + $stateParams.id,
                    data: $scope.promotion,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                        $state.go("oam.promotionManagement");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/promotion/create',
                    data: $scope.promotion,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                        $state.go("oam.promotionManagement");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }

    });
/**
 * Created by challenge on 15/10/26.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('PromotionListCtrl', function($scope, $http,$stateParams,$location,$filter,$rootScope) {

        $scope.page = {
            itemsPerPage: 20
        };

        $scope.searchForm = {pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        //if($rootScope.user) {
        //    var data = $rootScope.user;
        //    $scope.cities = data.cities;
        //    if ($scope.cities && $scope.cities.length == 1) {
        //        $scope.searchForm.cityId = $scope.cities[0].id;
        //    }
        //}

        //$http.get("/admin/api/promotion")

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
            });

        $http({
            url: "/admin/api/promotion",
            method: "GET",
            params: $scope.searchForm
        }).success(function(data){
                $scope.promotions = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function(data){
                alert("加载失败...");
            });

        $scope.pageChanged = function() {
            $scope.searchForm.page = $scope.page.currentPage - 1;

            $location.search($scope.searchForm);
        }

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('promotionStatisticsFullCutListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            pageSize: 20,
            promotionType:1 //类型为满减活动
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
        });

        //获取数据
        console.log($scope.searchForm);
        $scope.loadData = function(){

            $http({
                method: 'GET',
                url: '/admin/api/promotion/fullcut',
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.listData = data.content;
                $scope.lineTotal = data.lineTotal;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })
        }
        $scope.loadData();

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/promotion/fullcut/export?" + str.join("&"));
        };


    });
'use strict';

angular.module('sbAdminApp')
    .controller('promotionStatisticsFullgiftListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            pageSize: 20,
            promotionType: $stateParams.promotionType
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
                //if ($scope.searchForm.promotionType==null) {
                //    $scope.searchForm.promotionType = $scope.promotionTypes[0].type;
                //}
        });

        //获取数据
        console.log($scope.searchForm);
        $scope.loadData = function(){
            console.log($scope.searchForm);
            var type = $scope.searchForm.promotionType;
            if(type==null ){
                return ;
            }
            console.log($scope.searchForm);
            $http({
                method: 'GET',
                url: '/admin/api/promotion/fullgift',
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.listData = data.content;
                $scope.lineTotal = data.lineTotal;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })
        }
        $scope.loadData();

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/promotion/fullgift/export?" + str.join("&"));
        };


    });
'use strict';

angular.module('sbAdminApp')
    .controller('PreciseBatchPushCtrl', function ($scope, $http, $stateParams) {

        $scope.pushForm = {};
        $scope.pushForm.restaurantIds = [];

        if ($stateParams.ids) {
            $scope.pushForm.ids = $stateParams.ids;
        }

        $scope.createPush = function () {
            if (!new RegExp("^[0-9,]+$").test($scope.pushForm.ids)) {
                alert("餐馆ID包含非法字符");
                return;
            }
            if (window.confirm("本次推送共选择" + $scope.pushForm.ids.trim().split(',').length + "个餐馆,推送不可撤销,是否继续?") == true) {
                angular.forEach($scope.pushForm.ids.trim().split(','), function (value) {
                    $scope.pushForm.restaurantIds.push(value);
                });
                $http({
                    method: 'POST',
                    url: '/admin/api/push/precise/create',
                    data: $scope.pushForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function () {
                    alert("推送成功!");
                }).error(function () {
                    alert("推送失败!");
                })
            }
        };

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('PrecisePushCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, $state) {

        /*搜索表单数据*/
        $scope.searchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            adminUserId: $stateParams.adminUserId,
            name: $stateParams.name,
            telephone: $stateParams.telephone,
            start: $stateParams.start,
            end: $stateParams.end,
            warehouseId: $stateParams.warehouseId,
            id: $stateParams.id,
            registPhone: $stateParams.registPhone,
            cityId: $stateParams.cityId,
            grade: $stateParams.grade,
            warning: $stateParams.warning
        };

        /*获取可选状态*/
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
            if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                $scope.searchForm.warehouseId = $scope.availableWarehouses[0].id;
            }
        }

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.adminUsers = [{id: 0, realname: "未分配销售"}].concat(data);
            })

        /*状态combobox*/
        $http.get("/admin/api/restaurant/status")
            .success(function (data) {
                $scope.availableStatus = data;
            })

        $http.get("/admin/api/restaurant/grades")
            .success(function (data) {
                $scope.grades = data;
            })

        $scope.warnings = [{key: 1, value: "预警状态-是"}, {key: 0, value: "预警状态-否"}];

        $scope.$watch('searchForm.cityId', function (cityId, old) {
            if (cityId) {
                $http.get("/admin/api/city/" + cityId + "/warehouses")
                    .success(function (data, status, headers, config) {
                        $scope.availableWarehouses = data;
                        if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                            $scope.searchForm.warehouseId = $scope.availableWarehouses[0].id;
                        }
                    });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });

        $scope.restaurants = [];
        $scope.page = {
            itemsPerPage: 100
        };


        if ($stateParams.sortField) {
            $scope.searchForm.sortField = $stateParams.sortField;
        } else {
            $scope.searchForm.sortField = "id";
        }

        if ($stateParams.asc) {
            $scope.searchForm.asc = true;
        } else {
            $scope.searchForm.asc = false;
        }

        $scope.date = new Date().toLocaleDateString();

        $scope.$watch('startDate', function (newVal) {
            $scope.searchForm.start = $filter('date')(newVal, 'yyyy-MM-dd');
        });

        $scope.$watch('endDate', function (newVal) {
            $scope.searchForm.end = $filter('date')(newVal, 'yyyy-MM-dd');
        });

        if ($stateParams.status) {
            $scope.searchForm.status = parseInt($stateParams.status);
        }

        if ($scope.searchForm.start) {
            $scope.startDate = Date.parse($scope.searchForm.start);
        }

        if ($scope.searchForm.end) {
            $scope.endDate = Date.parse($scope.searchForm.end);
        }

        if ($stateParams.warehouseId) {
            $scope.searchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.cityId) {
            $scope.searchForm.cityId = parseInt($stateParams.cityId);
        }

        if ($stateParams.grade) {
            $scope.searchForm.grade = parseInt($stateParams.grade);
        }

        if ($stateParams.warning) {
            $scope.searchForm.warning = parseInt($stateParams.warning);
        }

        $scope.resetPageAndSearchRestaurant = function () {
            $scope.searchForm.page = 0;
            $scope.searchForm.pageSize = 100;

            $location.search($scope.searchForm);
        }


        $http({
            url: "/admin/api/restaurant",
            method: "GET",
            params: $scope.searchForm
        })
            .success(function (data, status, headers, config) {
                $scope.restaurants = data.restaurants;
                $scope.consumption = data.consumption;
                $scope.restaurantSummary = data.restaurantSummary;
                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function (data, status, headers, config) {
                alert("加载失败...");
            });


        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.searchForm);
        }

        $scope.checkPass = function (restaurant) {

            $http({
                method: 'PUT',
                url: '/admin/api/restaurant/' + restaurant.id + '/status',
                params: {status: 2},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
                .success(function () {
                    restaurant.status.value = 2;
                    window.alert("审核成功!");
                })
                .error(function () {
                    window.alert("审核失败！");
                });
        }

        $scope.sort = function (field) {
            if (field && field == $scope.searchForm.sortField) {
                $scope.searchForm.asc = !$scope.searchForm.asc;
            } else {
                $scope.searchForm.sortField = field;
                $scope.searchForm.asc = false;
            }

            $scope.searchForm.page = 0;

            $location.search($scope.searchForm);
        }

        $scope.filterTelephone = function (telephone) {
            if (telephone) {
                $location.search({telephone: telephone});
            }
        }

        $scope.filterAdminUser = function (adminUserId) {
            if (adminUserId) {
                $location.search({adminUserId: adminUserId});
            }
        }

        $scope.isCheckedAll = false;
        $scope.batchForm = {};
        $scope.batchForm.ids = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.batchForm.ids = [];
                angular.forEach($scope.restaurants, function (value, key) {
                    $scope.batchForm.ids.push(value.customer.id);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.batchForm.ids = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.precisePush = function (id) {
            $scope.batchForm.ids = [id];
            $scope.batchPush();
        };
        $scope.batchPush = function () {
            $state.go('oam.precise-batch-push', {ids: $scope.batchForm.ids.join(',')});
        };
    });

'use strict';
angular.module('sbAdminApp')
    .controller('CreatePushCtrl', function ($scope, $http, $stateParams, $upload) {



        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';



        $scope.date = new Date().toLocaleDateString();

        $scope.push = {

        }

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });


        $scope.$watch('push.cityId', function (newVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                    $scope.warehouses.push({
                        "id": 0,
                        "name": "全城",
                        "city": {},
                        "displayName": "全城"
                    });
                });
            } else {
                $scope.warehouses = [];
            }
        })


        if ($stateParams.id) {
            $scope.isEditPush = true;

            $http.get("/admin/api/push/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.push = data;

                })
                .error(function (data, status) {
                    window.alert("获取push信息失败...");
                });

        }

        $scope.createPush = function () {


            if ($stateParams.id != '' && $stateParams.id != undefined) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/push/' + $stateParams.id,
                    data: $scope.push,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {


                $http({
                    method: 'POST',
                    url: '/admin/api/push/create',
                    data: $scope.push,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }

    })

'use strict';

angular.module('sbAdminApp')
	.controller('PushListCtrl', function($scope, $http) {

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

		$scope.date = new Date().toLocaleDateString();

		$http.get("/admin/api/pushes")
		.success(function(data){
			$scope.pushes = data;
		})
		.error(function(data){

		});
	});
'use strict';
angular.module('sbAdminApp')
    .controller('WXPushCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.push = {
            openid : "0",
            mediaid : "0",
            cityid: 0
        };
        $scope.pushMediaArray = [];

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });

        $scope.createWXPush = function () {
            if($scope.push.mediaid == "0"){
                alert("请填多媒体ID");
                return;
            }

            var mIsPreview = false;
            if($scope.pushTest == true){
                mIsPreview = true;
                $scope.pushTest = false;
            }

            $scope.push.isPreview = mIsPreview;
            $scope.push.openid = $scope.push.openid;

            $http({
                method: 'POST',
                url: '/admin/api/push/wxPushCreate/'+$scope.push.cityid,
                params: $scope.push,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                if(data == true)
                    alert("发送成功!");
                else
                    alert("发送失败");
            })
            .error(function (data, status, headers, config) {
                alert("发送失败!");
            })
        }

        $scope.createWXPushTest = function(){
            $scope.pushTest = true;
            $scope.createWXPush();
        }

        $scope.getWXPushMediaList = function (){
            $scope.pushMediaArray = [];
            $http({
                method: 'GET',
                url: '/admin/api/push/wxPushMediaList'
            })
            .success(function (data, status, headers, config) {
                if(data == null || data == "null"){
                    alert("获取微信多媒体资源失败");
                    return;
                }
                for(var key in data){
                    var pushMedia = {key : "",title : ""};
                    pushMedia.key = key;
                    pushMedia.title = data[key];
                    $scope.pushMediaArray.push(pushMedia);
                }
            })
            .error(function (data, status, headers, config) {
                alert("获取微信多媒体资源失败");
            })

        }
        $scope.getWXPushMediaList();
    })

'use strict';
angular.module('sbAdminApp')
.controller('spikeAddCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {


        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.addForm={
            cityId: null,
            endTime: null,
            startTime: null,
            warehouseId:$stateParams.warehouseId,
            items:[]
        };

        //增加一项商品
        $scope.appendItem=function(){
            var item={
                skuId:null,
                name:null,
                price:null,
                num:null,
                bundle:false
            };
            $scope.addForm.items.push(item);
        }
        $scope.removeItem = function(index) {
            $scope.addForm.items.splice(index, 1);
        }

        $scope.funcAsync = function (name) {
            if (name && name !== "") {
                $scope.searchSkus = [];
                $http({
                    url: "/admin/api/dynamic-price/candidatesPlus",
                    method: 'GET',
                    params: {warehouse: $scope.addForm.warehouseId, name: name, showLoader: false}
                }).then(
                    //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        $scope.searchSkus=data.data;
                    }
                )
            }
        }

        //$scope.checkPerMaxNum=function(item) {
        //    var evt = window.event;
        //    var element=evt.srcElement || evt.target;
        //    if (element.checkValidity()) {
        //        if (item.num!=null && item.perMaxNum > item.num) {
        //            element.setCustomValidity("此值不能大于活动数量");
        //            element.validity.valid=true;
        //        }
        //    }
        //}


        $scope.submitForm=function(){

            if($scope.addForm.startTime>= $scope.addForm.endTime){
                alert("起始时间需要小于截止时间");
                return ;
            }
            if($scope.addForm.items.length==0){
                alert("请至少添加一项商品");
                return;
            }
            for(var i=0;i<$scope.addForm.items.length;i++){
                if($scope.addForm.items[i].skuId==null || $scope.addForm.items[i].skuId.length==0){
                    alert("请选择第"+(i+1)+"项商品的sku！");
                    return;
                }
                if($scope.addForm.items[i].num!=null && $scope.addForm.items[i].perMaxNum > $scope.addForm.items[i].num){
                    alert("第"+(i+1)+"项商品的用户限购数量请小于活动数量！");
                    return;
                }
            }

            $http({
                url:"/admin/api/spike/add",
                method:"POST",
                headers: {'Content-type': 'application/json;charset=UTF-8'},
                data:$scope.addForm
            }).success(function(data) {
                alert("创建成功!")
            }).error(function(data) {
                alert("创建失败!")
            })

        }


        $scope.skuSetting = function(item,selectedSku) {
            console.log(item);
            console.log(selectedSku);
            item.skuId=selectedSku.id;
            item.name=selectedSku.name;
        };


        //$scope.$watch('addForm.cityId', function (newVal, oldVal) {
        //    if (newVal != null && newVal != "") {
        //        $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
        //            $scope.warehouses = data;
        //            if ($scope.warehouses && $scope.warehouses.length == 1) {
        //                $scope.addForm.warehouseId = $scope.warehouses[0].id;
        //            }
        //        });
        //        if (typeof oldVal != "undefined" && newVal != oldVal) {
        //            $scope.addForm.warehouseId = null;
        //        }
        //    } else {
        //        $scope.warehouses = [];
        //        $scope.addForm.warehouseId = null;
        //    }
        //});
        //加载城市
        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.addForm.cityId = $scope.cities[0].id;
            }
        }





});
'use strict';
angular.module('sbAdminApp')
    .controller('spikeItemListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            spikeId : $stateParams.id
        };

        if($scope.searchForm.spikeId!=null) {
            $http({
                method: 'GET',
                url: '/admin/api/spike/query/'+$scope.searchForm.spikeId
                //params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.data = data;

            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })
        }

    });
'use strict';
angular.module('sbAdminApp')
    .controller('spikeListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {
        $scope.page = {};
        $scope.searchForm = {
            pageSize: 20,
            page: $stateParams.page!=null ? parseInt($stateParams.page):0
        };

        $scope.showActiveState=function(state){
            if($scope.activeStates==null){
                return;
            }
            for(var i=0;i<$scope.activeStates.length ; i++){
                if($scope.activeStates[i].val == state){
                    return $scope.activeStates[i];
                }
            }
        }


        $http({
            method: 'GET',
            url: '/admin/api/spike/query',
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.listData = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("查询失败");
        })

        $http({
            method: 'GET',
            url: '/admin/api/spike/activeState/query'
        }).success(function (data, status, headers, config) {
            $scope.activeStates = data;


        }).error(function (data, status, headers, config) {
            alert("查询失败");
        })

        //设置状态
        $scope.stateSet=function(index,line,state){
            if(!confirm("确认失效吗？秒杀id:"+ line.id )){
                return ;
            }
            $http({
                method: 'POST',
                url: '/admin/api/spike/state/change',
                params: { id:line.id, state:state }
            }).success(function (data, status, headers, config) {
                $scope.listData[index]=data;
            }).error(function (data, status, headers, config) {
                alert("操作失败");
            })
        }
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
});
'use strict';
angular.module('sbAdminApp')
    .controller('spikeItemModifyCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window, AlertErrorMessage) {

        $scope.searchForm = {
            spikeId : $stateParams.id
        };

        $scope.bundleOptions =[{val:true,name:'打包'},{val:false,name:'单品'}];

        if($scope.searchForm.spikeId!=null) {
            $http({
                method: 'GET',
                url: '/admin/api/spike/query/'+$scope.searchForm.spikeId
                //params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.data = data;
                $scope.initSku(data);
            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })

        }

        $scope.initSku=function(data){
            $scope.searchSkus=[];
            $.each(data.spikeItems, function(i,vo){
                vo.skuId = vo.sku.id;
                $scope.searchSkus.push(vo.sku);
            });
        }

        $scope.removeItem = function(index) {
            $scope.data.spikeItems.splice(index, 1);
        }

        $scope.funcAsync = function (name) {

            if (name && name !== "") {
                $scope.searchSkus = [];
                $http({
                    url: "/admin/api/dynamic-price/candidatesPlus",
                    method: 'GET',
                    params: { name: name, showLoader: false}
                }).then(
                    //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        $scope.searchSkus=data.data;
                    }
                )
            }
        }

        //增加一项商品
        $scope.appendItem=function(){
            var item={
                skuId:null,
                name:null,
                price:null,
                num:null,
                bundle:false
            };
            $scope.data.spikeItems.push(item);
        }



        //$scope.skuSetting = function(item,selectedSku) {
        //    console.log(item);
        //    console.log(selectedSku);
        //    item.skuId=selectedSku.id;
        //    item.name=selectedSku.name;
        //};

        $scope.submit=function(){

            if($scope.data.spikeItems.length==0){
                alert("请至少添加一项商品");
                return;
            }

            for(var i=0;i<$scope.data.spikeItems.length;i++){
                if($scope.data.spikeItems[i].skuId==null || $scope.data.spikeItems[i].skuId.length==0){
                    alert("请选择第"+(i+1)+"项商品的sku！");
                    return;
                }
                if($scope.data.spikeItems[i].num!=null && $scope.data.spikeItems[i].perMaxNum > $scope.data.spikeItems[i].num){
                    alert("第"+(i+1)+"项商品的用户限购数量请小于活动数量！");
                    return;
                }
            }

            var param = {
                spikeId: $scope.data.id,
                description: $scope.data.description,
                items: $scope.data.spikeItems
            };
            //提交修改
            $http({
                method: 'PUT',
                url: '/admin/api/spike/modify',
                data: param,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
            .success(function(data,status,headers,config){
                window.alert("修改成功!");
                //$window.history.back();
            })
            .error(function(data,status,headers,config){
                AlertErrorMessage.alert(data);
            });
        }


});
'use strict';
angular.module('sbAdminApp')
    .controller('suggestionListCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $location, $window) {

        $scope.searchForm = {
            pageSize: 20
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }
        if ($stateParams.pageSize) {
            $scope.searchForm.pageSize = parseInt($stateParams.pageSize);
        }

        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $http({
            method: 'GET',
            url: "/admin/api/suggestion/list",
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.suggestions = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("查询失败");
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        //$scope.export = function () {
        //    var str = [];
        //    for (var p in $scope.searchForm) {
        //        if ($scope.searchForm[p]) {
        //            str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
        //        }
        //    }
        //    $window.open("/admin/api/suggestion/export?" + str.join("&"));
        //};

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('CategoryDetailCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {
            showSecond : false
        };

        /*分类状态list*/
        $http.get("/admin/api/category/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })



        $http.get("/admin/api/category")
            .success(function (data, status, headers, config) {
                $scope.availableParentCategories = [];

                angular.forEach(data, function (value, key) {
                    if (!$stateParams.id || $stateParams.id != value.id) {
                        this.push(value);
                    }
                }, $scope.availableParentCategories);
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })

        if ($stateParams.id) {
            $http.get("/admin/api/category/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                $scope.formData.status = data.status.value;
                $scope.formData.showSecond = data.showSecond;
                if(data.parentCategoryId) {
                    $scope.formData.parentCategoryId = data.parentCategoryId;
                }

                $scope.formData.displayOrder = data.displayOrder;
                if(data.mediaFile) {
                    $scope.mediaUrl = data.mediaFile.url;
                    $scope.formData.mediaFileId = data.mediaFile.id;
                }

            });
        }

        $scope.$watch('media', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        $scope.mediaUrl = data.url;
                        $scope.formData.mediaFileId = data.id;
                    })
                }
            }
        });

        $scope.saveCategory = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/category/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("修改成功!");
                    })
                    .error(function (data) {
                        alert("修改失败!");
                    });
            } else {
                $http({
                    url: "/admin/api/category",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败!");
                    });
            }
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('CategoryListCtrl', function($scope, $http, $stateParams, $location, $rootScope) {

    	if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }


        $scope.treeOptions = {
            dropped : function(event) {
                console.log(event);
                var destParentCategoryId = 0;
                var destChildren = [];
                if(event.dest.nodesScope.$parent.$modelValue) {
                    destParentCategoryId = event.dest.nodesScope.$parent.$modelValue.id;

                    event.dest.nodesScope.$parent.$modelValue.children.forEach(function(child) {destChildren.push(child.id);});
                }

                var sourceParentCategoryId = 0;
                var sourceChildren = [];
                if(event.source.nodesScope.$parent.$modelValue) {
                    sourceParentCategoryId = event.source.nodesScope.$parent.$modelValue.id;

                    event.source.nodesScope.$parent.$modelValue.children.forEach(function(child) {sourceChildren.push
                    (child.id);});
                }

                $http({
                    url: "/admin/api/category/" + destParentCategoryId + "/children",
                    params: {children: destChildren},
                    method: 'PUT'
                })
                .then(function (data) {
                    $http({
                        url: "/admin/api/category/" + sourceParentCategoryId + "/children",
                        params: {children: sourceChildren},
                        method: 'PUT'
                    })
                    .success(function (data) {
                        alert("修改成功!");
                    })
                    .error(function (data) {
                        alert("修改失败!");
                    });
                })



            }
        }

        $scope.form = {
            status : [1,2,3]
        }

        $scope.visible = function (item) {
            return $scope.form.status.indexOf(item.status) >= 0;
        };

        $http({
            url:"/admin/api/category/treeJson",
            method:'GET',
            params: {'cityId' :$stateParams.cityId}
        })
        .success(function(data){
            $scope.nodes = data;
        })
        .error(function(data){

        });

        $scope.setCategoryCity = function(node, cityId, active) {
            $http({
                url:"/admin/api/category/"+ node.id +"/changeCity",
                method:'PUT',
                params: {'cityId' :cityId, 'active': active}
            })
            .success(function(data){
                if (active === true) {
                    if (node.cityIds.indexOf(cityId) == -1) {
                        node.cityIds.push(cityId);
                    }
                } else {
                    if (node.cityIds.indexOf(cityId) != -1) {
                        node.cityIds.splice(node.cityIds.indexOf(cityId), 1);
                    }
                }
            })
            .error(function(data){
                alert("失败");
            });
        }
	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceDetailCtrl
 * @description
 * # DynamicPriceDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceDetailCtrl', function ($scope, $http, $stateParams) {

        $scope.formData = {};

        $scope.format = 'yyyy-MM-dd';

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            startingDay: 1
        };

        $http.get("/admin/api/city/warehouses/" + $stateParams.warehouseId)
            .success(function (data, status, headers, config) {
                $scope.warehouses = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.formData.skuId = $stateParams.skuId;

        $http.get("/admin/api/sku/" + $stateParams.skuId).success(function (data) {
            $scope.sku = data;
        });

        if($stateParams.warehouseId) {
            $scope.formData.warehouseId = parseInt($stateParams.warehouseId, 10); ;
        }

        $scope.$watch(function () {
            return $scope.formData.warehouseId
        }, function () {
            if ($scope.formData.warehouseId) {
                $http({
                    method: 'GET',
                    url: "/admin/api/dynamic-price/unique",
                    params: {
                        skuId: $stateParams.skuId,
                        warehouseId: $scope.formData.warehouseId
                    }
                }).success(function (data) {
                    if (data) {
                        if (data.singleDynamicSkuPriceStatus) {
                            $scope.formData.singleSalePrice = data.singleDynamicSkuPriceStatus.singleSalePrice;
                            $scope.formData.singleAvailable = data.singleDynamicSkuPriceStatus.singleAvailable;
                            $scope.formData.singleInSale = data.singleDynamicSkuPriceStatus.singleInSale;
                        }
                        if (data.bundleDynamicSkuPriceStatus) {
                            $scope.formData.bundleSalePrice = data.bundleDynamicSkuPriceStatus.bundleSalePrice;
                            $scope.formData.bundleAvailable = data.bundleDynamicSkuPriceStatus.bundleAvailable;
                            $scope.formData.bundleInSale = data.bundleDynamicSkuPriceStatus.bundleInSale;
                        }
                        $scope.formData.fixedPrice = data.fixedPrice;
                    }
                });
            }
        });


        $scope.saveDynamicPrice = function () {
            $http({
                method: 'post',
                url: '/admin/api/dynamic-price-temp',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
                .success(function (data) {
                    alert("保存成功!");
                })
                .error(function (data) {
                    alert("保存失败!");
                });
        }

        $scope.disableForm = function () {
            if ($scope.formData.effectType != null) {
                if ($scope.formData.effectType) {
                    return false;
                } else {
                    if ($scope.formData.effectTime != null) {
                        return false;
                    }
                }
            }

            return true;
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceListTempCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state,
    editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.changeDetailResponses = [];

        $scope.page = {
            itemsPerPage: 100
        };

        $scope.dynamicPriceSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            status:$stateParams.status,
            skuName:$stateParams.skuName
        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.dynamicPriceSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('dynamicPriceSearchForm.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.dynamicPriceSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.dynamicPriceSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.dynamicPriceSearchForm.organizationId = null;
                   $scope.dynamicPriceSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.availableWarehouses = [];
               $scope.dynamicPriceSearchForm.organizationId = null;
               $scope.dynamicPriceSearchForm.warehouseId = null;
           }
        });


        $scope.resetPageAndSearchDynamicPrice = function () {
            $scope.dynamicPriceSearchForm.page = 0;
            $scope.dynamicPriceSearchForm.pageSize = 100;

            $state.go($state.current, $scope.dynamicPriceSearchForm, {reload: true});
        }
        if($stateParams.status) {
            $scope.dynamicPriceSearchForm.status = parseInt($stateParams.status);
        }
        if($stateParams.organizationId){
            $scope.dynamicPriceSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.cityId){
            $scope.dynamicPriceSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if($stateParams.warehouseId){
            $scope.dynamicPriceSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }
        if($stateParams.skuName){
            $scope.dynamicPriceSearchForm.skuName = $stateParams.skuName;
        }
        if($stateParams.submitRealName){
            $scope.dynamicPriceSearchForm.submitRealName = $stateParams.submitRealName;
        }
        if($stateParams.checkRealName){
            $scope.dynamicPriceSearchForm.checkRealName = $stateParams.checkRealName;
        }

        $scope.searchDynamicPrice = function () {
            $location.search($scope.dynamicPriceSearchForm);
        }
        $http({
            url: "/admin/api/dynamic-price-temp",
            method: "GET",
            params: $scope.dynamicPriceSearchForm
        }).success(function (data) {
            $scope.changeDetailResponses = data.dynamicPriceTempResponses;


            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });
        /*$scope.searchDynamicPrice();*/
        $http.get("/admin/api/check/status")
            .success(function (data) {
                $scope.checkStatuss = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.checkThrough = function (changeDetailResponse, status) {

            $http({
                method: 'POST',
                url: '/admin/api/dynamic-price/' + changeDetailResponse.id,
                params: {status: status},
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
                .success(function () {
                    changeDetailResponse.status = 2;
                    window.alert("审核成功!");
                })
                .error(function () {
                    window.alert("审核失败！");
                });
        }

 /*全选、反选*/
    $scope.formData = {
            
            changeDetailIds:[],
            status:2
            
        };

        $scope.isCheckedAll = false;
        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                angular.forEach($scope.changeDetailResponses, function(value, key){
                    if(value.status == 0){
                        $scope.formData.changeDetailIds.push(value.id);
                    }
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.changeDetailIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.batchUpdate = function(status){
             $scope.formData.status = status;
            $http({
               
                method:'POST',
                url:'/admin/api/dynamic-price/batchUpdate',
                data:$scope.formData
                
            })
                .success(function() {
                    angular.forEach($scope.changeDetailResponses, function(value, key){
                          for(var i = 0;i < $scope.formData.changeDetailIds.length;i++){
                            if((value.id == $scope.formData.changeDetailIds[i])){
                                value.status = status;
                            }
                          }
                    });
                    $scope.formData.changeDetailIds = [];
                    window.alert("审核成功!");
                })
                .error(function() {
                $scope.formData.status = 0;
                    window.alert("审核失败！");
                });
        };



        $scope.pageChanged = function () {
            $scope.dynamicPriceSearchForm.page = $scope.page.currentPage - 1;
            $scope.dynamicPriceSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchDynamicPrice();
        }


        $scope.checkAvailable = function(){
            return $scope.formData.changeDetailIds.length == 0;
        }

        $scope.checkVendorDiff = function(changeDetailResponse){
            if(changeDetailResponse.originDynamicSkuPriceWrapper.vendor != null && changeDetailResponse.dynamicSkuPriceWrapper.vendor == null )
                return true;
            if(changeDetailResponse.originDynamicSkuPriceWrapper.vendor == null && changeDetailResponse.dynamicSkuPriceWrapper.vendor != null )
                return true;
//            return changeDetailResponse.originDynamicSkuPriceWrapper.vendor.name != changeDetailResponse.dynamicSkuPriceWrapper.vendor.name;
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, editableOptions,
    $upload, $window) {
//        editableOptions.theme = 'bs3';

        $scope.dynamicPrices = [];

        $scope.page = {
            itemsPerPage : 100
        };


        $scope.dynamicPriceSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            productId: $stateParams.productId,
            productName: $stateParams.productName,
            skuId : $stateParams.skuId,
            cityId : $stateParams.cityId,
            organizationId : $stateParams.organizationId,
            categoryId : $stateParams.categoryId,
            status:$stateParams.status,
            singleAvailable:$stateParams.singleAvailable,
            singleInSale:$stateParams.singleInSale,
            bundleAvailable:$stateParams.bundleAvailable,
            bundleInSale:$stateParams.bundleInSale,
            pageType:$stateParams.pageType,
            skuCreateDate:$stateParams.skuCreateDate
        };

        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.skuStatus = data;
            })
            .error(function(data, status) {
                alert("数据加载失败");
            });

        if($stateParams.warehouseId) {
            $scope.dynamicPriceSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if($stateParams.brandId) {
            $scope.dynamicPriceSearchForm.brandId = parseInt($stateParams.brandId);
        }

        if($stateParams.cityId) {
            $scope.dynamicPriceSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.organizationId) {
            $scope.dynamicPriceSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.skuId) {
            $scope.dynamicPriceSearchForm.skuId = parseInt($stateParams.skuId);
        }

        if ($stateParams.categoryId) {
            $scope.dynamicPriceSearchForm.categoryId = parseInt($stateParams.categoryId);
        }

        if ($stateParams.status) {
            $scope.dynamicPriceSearchForm.status = parseInt($stateParams.status);
        } else {
            $scope.dynamicPriceSearchForm.status = 2;
        }

        if ($stateParams.singleAvailable) {
            $scope.dynamicPriceSearchForm.singleAvailable = eval($stateParams.singleAvailable);
        }

        if ($stateParams.singleInSale) {
            $scope.dynamicPriceSearchForm.singleInSale = eval($stateParams.singleInSale);
        }

        if ($stateParams.bundleAvailable) {
            $scope.dynamicPriceSearchForm.bundleAvailable = eval($stateParams.bundleAvailable);
        }

        if ($stateParams.bundleInSale) {
            $scope.dynamicPriceSearchForm.bundleInSale = eval($stateParams.bundleInSale);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.searchDynamicPrice = function() {
            if($scope.dynamicPriceSearchForm.warehouseId) {

                $location.search($scope.dynamicPriceSearchForm);

                $http({
                    url: "/admin/api/dynamic-price",
                    method: "GET",
                    params: $scope.dynamicPriceSearchForm
                }).success(function (data) {
                    $scope.dynamicPrices = data.content;

                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                });
            }
        }



        /*获取可选状态*/
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.dynamicPriceSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('dynamicPriceSearchForm.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.dynamicPriceSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.dynamicPriceSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.dynamicPriceSearchForm.organizationId = null;
                   $scope.dynamicPriceSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.availableWarehouses = [];
               $scope.dynamicPriceSearchForm.organizationId = null;
               $scope.dynamicPriceSearchForm.warehouseId = null;
           }
        });


        $scope.$watch('dynamicPriceSearchForm.organizationId', function(organizationId) {
           if (organizationId) {
               $http.get("/admin/api/category")
                   .success(function (data, status, headers, config) {
                       $scope.categories = data;
                   });

               if (typeof old != 'undefined' && cityId != old) {
                   $scope.dynamicPriceSearchForm.categoryId = null;
               }
           } else {
               $scope.dynamicPriceSearchForm.categoryId = null;
           }
        });


        $scope.resetPageAndSearchDynamicPrice = function () {
            $scope.dynamicPriceSearchForm.page = 0;
            $scope.dynamicPriceSearchForm.pageSize = 100;

            $scope.searchDynamicPrice();
        }

        $scope.searchDynamicPrice();

        $scope.pageChanged = function() {
            $scope.dynamicPriceSearchForm.page = $scope.page.currentPage - 1;
            $scope.dynamicPriceSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchDynamicPrice();
        }

        /*保存修改商品进价、售价*/
        $scope.savePrice = function(data, skuId, warehouseId) {
            angular.extend(data, {skuId: skuId,warehouseId:warehouseId});
            return $http.post('/admin/api/dynamic-price', data);
        };
        
        $scope.fastSavePrice = function(data,skuId,warehouseId) {
            angular.extend(data, {skuId: skuId,warehouseId:warehouseId});
            return $http.post('/admin/api/dynamic-price-temp/fast', data);
        };

        $scope.$watch('dynamicMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/dynamic-price/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.dynamicPriceSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.priceListSize) {
                    		successSize = data.priceListSize;
                    	}
                    	if(data.exceptionMsg) {
                    		exceptionMsgSize = data.exceptionMsg.length;
                    	}
                    	var msg = "成功导入"+ successSize +"件商品， 失败了"+ exceptionMsgSize +"件商品\n";
                    	if(data.headMsg) {
                    		msg += data.headMsg + "\n";
                    	}
                    	if(data.errorFileName) {
                    		msg += "是否下载错误文件?";

                    		if(confirm(msg)){
                    			$window.open("/admin/api/dynamic-price/errorFile?errorFileName=" + data.errorFileName);
                    		}
                    	} else {
                    		alert(msg);
                    	}
                        
                    }).error(function (data) {
                        alert("导入失败，excel格式错误");
                    })
                }
            }
        });
        
        $scope.downloadErrorFile = function(fileName) {
        	$window.open("/admin/api/dynamic-price/errorFile/" + fileName);
        }
        
        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.dynamicPriceSearchForm) {
                if($scope.dynamicPriceSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.dynamicPriceSearchForm[p]));
                }
            }

        	$window.open("/admin/api/dynamic-price/excelExport?" + str.join("&"));
        };
        
        $scope.synchronizeToEdb = function(id) {
        	$http({
                url: "/admin/api/dynamic-price-edb/" + id,
                method: "PUT",
            }).success(function (data) {
            	if (data != "") {
            		alert(data);
            	} else {
            		alert("同步成功!");
            	}
            });
        };

        $scope.showVendor = function(vendor, candidateVendors) {
            if (!vendor) {
                return '';
            }

            if (!candidateVendors) {
                return vendor.name;
            }

            var name = '';
            angular.forEach(candidateVendors, function(item, key){
                if (item.id==vendor.id) {
                    name = item.name;
                }
            });

            return name;
        };
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('photo-upload-tool', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            mediaFileIds:[],
            mediaFiles:[]
        }

        $scope.$watch('media', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        if($scope.formData.mediaFileIds.indexOf(data.id) == -1) {
                            $scope.formData.mediaFiles.push(data);
                            $scope.formData.mediaFileIds.push(data.id);
                        }
                        if ($scope.formData.mediaFileIds.length > 1) {
                            var defaultImg = $scope.formData.mediaFileIds.indexOf(7713);
                            if (defaultImg != -1) {
                                $scope.formData.mediaFileIds.splice(defaultImg, 1);
                                $scope.formData.mediaFiles.splice(defaultImg, 1);
                            }
                        }
                    })
                }
            }
        });

        $scope.deleteImg = function(id) {
            var index = $scope.formData.mediaFileIds.indexOf(id);
            if (index != -1) {
                $scope.formData.mediaFileIds.splice(index, 1);
                $scope.formData.mediaFiles.splice(index, 1);
            }
            if ($scope.formData.mediaFileIds.length < 1) {
                var defaultImg = 7713;
                var defaultImgUrl = "http://7xijms.com1.z0.glb.clouddn.com/default";
                $scope.formData.mediaFileIds.push(defaultImg);
                $scope.formData.mediaFiles.push({id:defaultImg,url:defaultImgUrl});
            }
        };
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductDetailTempCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {
        };

        if ($stateParams.id) {
            $http.get("/admin/api/product-temp/" + $stateParams.id).success(function (data) {
                $scope.formData.productVo = data.productVo;
                $scope.formData.details = data.productVo.details;
                $scope.formData.name = data.productVo.name;
                $scope.formData.discrete = data.productVo.discrete;
                $scope.formData.properties = data.productVo.properties;
                $scope.formData.categoryName = data.productVo.category.hierarchyName;
                if (data.productVo.brand) {
                    $scope.formData.brandName = data.productVo.brand.brandName;
                }
                if (data.productVo.mediaFiles) {
                    $scope.formData.mediaFiles = data.productVo.mediaFiles;
                }

                $scope.formData.capacityInBundle = data.productVo.capacityInBundle;
                $scope.formData.barCode = data.productVo.barCode;


                $scope.formData.organization = data.organization;

                var bundleFound = false;
                var unitFound = false;


                if(data.organization) {
                    $scope.formData.organizationId = data.organization.id;
                }

                if(data.productVo.skus) {
                    $scope.formData.skuRequest = data.productVo.skus[0];
                }
            });
        }

    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductDetailCtrl', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            skuRequests: [],
            organization : [],
            mediaFiles:[],
            mediaFileIds:[]
        };

        $http.get("/admin/api/organization?enable=true")
        .success(function (data, status, headers, config) {
            $scope.organizations = data.organizations;
            if ($scope.organizations && $scope.organizations.length == 1) {
                $scope.formData.organizationId = $scope.organizations[0].id;
            }
        })
        .error(function (data, status) {
            alert("数据加载失败！");
        });

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http.get("/admin/api/sku/skuSingleUnit")
            .success(function (data, status, headers, config) {
                $scope.skuSingleUnit = data;
            })

        $http.get("/admin/api/sku/skuBundleUnit")
            .success(function (data, status, headers, config) {
                $scope.skuBundleUnit = data;
            })

        $http.get("/admin/api/sku/rateValues")
            .success(function (data, status, headers, config) {
                $scope.rateValues = data;
            })




        /*品牌*/
        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.$watch('formData.cityId', function(cityId, old) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;
                });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.formData.organizationId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.formData.organizationId = null;
            }
        });

        $scope.$watch('formData.organizationId', function(newValue, oldValue) {
            if(newValue != null && newValue != '') {
                $http.get("/admin/api/category")
                    .success(function (data, status, headers, config) {
                        $scope.categories = data;
                    });
                if (typeof oldValue != 'undefined' && oldValue != null && newValue != oldValue) {
                    $scope.formData.categoryId = null;
                }
            } else {
                $scope.categories = [];
                $scope.formData.categoryId = null;

            }
        });



        if ($stateParams.id) {
            $http.get("/admin/api/product/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                if (data.category) {
                    $scope.formData.categoryId = data.category.id;
                }
                $scope.formData.properties=data.properties;
                $scope.formData.details=data.details;

                if (data.brand) {
                    $scope.formData.brandId = data.brand.id;
                }
                if(data.mediaFiles) {
                    $scope.formData.mediaFiles = data.mediaFiles;
                    angular.forEach(data.mediaFiles, function(value) {
                        $scope.formData.mediaFileIds.push(value.id);
                    });
                }

                $scope.formData.capacityInBundle = data.capacityInBundle;
                $scope.formData.barCode = data.barCode;
                $scope.formData.discrete = data.discrete;
                $scope.formData.specification = data.specification;
                $scope.formData.shelfLife = data.shelfLife;

                if(data.skus) {
                    $scope.formData.skuRequest = data.skus[0];
                    $scope.formData.skuRequest.status = $scope.formData.skuRequest.status.value;
                }
            });
        }else{

        }

        $scope.$watch('media', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        if($scope.formData.mediaFileIds.indexOf(data.id) == -1) {
                            $scope.formData.mediaFiles.push(data);
                            $scope.formData.mediaFileIds.push(data.id);
                        }
                        if ($scope.formData.mediaFileIds.length > 1) {
                            var defaultImg = $scope.formData.mediaFileIds.indexOf(7713);
                            if (defaultImg != -1) {
                                $scope.formData.mediaFileIds.splice(defaultImg, 1);
                                $scope.formData.mediaFiles.splice(defaultImg, 1);
                            }
                        }
                    })
                }
            }
        });

        $scope.deleteImg = function(id) {
            var index = $scope.formData.mediaFileIds.indexOf(id);
            if (index != -1) {
                $scope.formData.mediaFileIds.splice(index, 1);
                $scope.formData.mediaFiles.splice(index, 1);
            }
            if ($scope.formData.mediaFileIds.length < 1) {
                var defaultImg = 7713;
                var defaultImgUrl = "http://7xijms.com1.z0.glb.clouddn.com/default";
                $scope.formData.mediaFileIds.push(defaultImg);
                $scope.formData.mediaFiles.push({id:defaultImg,url:defaultImgUrl});
            }
        };

        /*提交保存*/
        $scope.saveProduct = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/productTemp/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function () {
                        alert("保存失败!");
                    });
            } else {
                $http({
                    url: "/admin/api/product-temp",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function () {
                        alert("保存失败!");
                    });
            }
        };
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListTempCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductListTempCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window, $state) {
        editableOptions.theme = 'bs3';

        $scope.productSearchForm = {
            productName: $stateParams.productName,
            organizationId:1
        }

        $scope.changeDetailResponses = [];

        $scope.page = {
            itemsPerPage: 100
        };
        if($stateParams.status) {
            $scope.productSearchForm.status = parseInt($stateParams.status);
        }
        if($stateParams.organizationId){
            $scope.productSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.cityId){
            $scope.productSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if($stateParams.productName){
            $scope.productSearchForm.productName = $stateParams.productName;
        }
        if($stateParams.pageSize) {
            $scope.productSearchForm.pageSize = $stateParams.pageSize;
        }
        if($stateParams.page) {
            $scope.productSearchForm.page = $stateParams.page;
        }
        if($stateParams.submitRealName) {
            $scope.productSearchForm.submitRealName = $stateParams.submitRealName;
        }
        if($stateParams.checkRealName) {
            $scope.productSearchForm.checkRealName = $stateParams.checkRealName;
        }

        $http.get("/admin/api/check/status")
            .success(function (data) {
                $scope.checkStatuss = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http({
            url: "/admin/api/product-temp",
            method: "GET",
            params: $scope.productSearchForm
        })
            .success(function (data, status, headers, config) {
                $scope.changeDetailResponses = data.changeDetailResponses;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function (data, status, headers, config) {
                alert("加载失败...");
            });

        $scope.searchProduct = function () {
            $location.search($scope.productSearchForm);
        }


        $scope.checkThrough = function(changeDetailResponse,status) {

            $http({
                method: 'POST',
                url: '/admin/api/product/' + changeDetailResponse.id ,
                params:{status:status},
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
                .success(function() {
                    changeDetailResponse.status = 2;
                    window.alert("审核成功!");
                })
                .error(function() {
                    window.alert("审核失败！");
                });
        }


    /*全选、反选*/
    $scope.formData = {
            
            changeDetailIds:[],
            status:2
            
        };

        $scope.isCheckedAll = false;
        $scope.checkAll = function() {        
            if(!($scope.isCheckedAll)){
                angular.forEach($scope.changeDetailResponses, function(value, key){
                    if(value.status == 0){
                        $scope.formData.changeDetailIds.push(value.id);
                    }
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.changeDetailIds = [];
                $scope.isCheckedAll = false;
            }
        };
       

        $scope.batchUpdate = function(status){
            $scope.formData.status = status;
            $http({
                method: 'POST',
                url:'/admin/api/product/batchUpdate',
                data: $scope.formData
            })
                .success(function(data) {
                     angular.forEach($scope.changeDetailResponses, function(value, key){
                          for(var i = 0;i < $scope.formData.changeDetailIds.length;i++){
                            if((value.id == $scope.formData.changeDetailIds[i])){
                                value.status = status;
                            }
                          }
                    });
                     $scope.formData.changeDetailIds = [];
                    if(data.errorNum == 0){
                        alert("全部审核成功");
                    }else{
                        window.alert("审核失败"+data.errorNum+"件")
                    }
                })
                .error(function() {
                    window.alert("审核失败！");
                });
        }
        /*$scope.searchProduct();*/

        $scope.pageChanged = function () {
            $scope.productSearchForm.page = $scope.page.currentPage - 1;
            $scope.productSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchProduct();
        }

        $scope.resetPageAndSearchProduct = function () {
            $scope.productSearchForm.page = 0;
            $scope.productSearchForm.pageSize = 100;
            $state.go($state.current, $scope.productSearchForm, {reload: true});
        }

        $scope.checkAvailable = function(){
            return $scope.formData.changeDetailIds.length == 0;
        }
         $scope.checkCategoryDiff = function(changeDetailResponse){
            if (changeDetailResponse.originProductWrapper) {
                if(changeDetailResponse.originProductWrapper.category != null && changeDetailResponse.productWrapper.category == null )
                    return true;
                if(changeDetailResponse.originProductWrapper.category == null && changeDetailResponse.productWrapper.category != null )
                    return true;
                return changeDetailResponse.originProductWrapper.category.hierarchyName !=
                changeDetailResponse.productWrapper.category.hierarchyName;
            }

        }
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.products = [];
        $scope.edit = false;

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.productSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            skuId: $stateParams.skuId,
            productName: $stateParams.productName,
            cityId : $stateParams.cityId,
            organizationId : $stateParams.organizationId
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.submitDateFormat = "yyyy-MM-dd";

        $http.get("/admin/api/organization?enable=true")
        .success(function (data, status, headers, config) {
            $scope.organizations = data.organizations;
            if ($scope.organizations && $scope.organizations.length == 1) {
                $scope.productSearchForm.organizationId = $scope.organizations[0].id;
            }
        })
        .error(function (data, status) {
            alert("数据加载失败！");
        });

/*
        $scope.$watch('productSearchForm.cityId', function(cityId, old) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;
                });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.productSearchForm.organizationId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.productSearchForm.organizationId = null;
            }
        });*/

        $scope.$watch('productSearchForm.organizationId', function(oldValue, newValue) {
            if(oldValue) {
                $http.get("/admin/api/category")
                    .success(function (data, status, headers, config) {
                        $scope.categories = data;
                    });
                if (typeof old != 'undefined' && cityId != old) {
                    $scope.productSearchForm.categoryId = null;
                }
            } else {
                $scope.categories = [];
                $scope.productSearchForm.categoryId = null;

            }
        });

        if($stateParams.productId) {
            $scope.productSearchForm.productId = parseInt($stateParams.productId);
        }

        if($stateParams.skuId) {
            $scope.productSearchForm.skuId = parseInt($stateParams.skuId);
        }

        if ($stateParams.productName) {
            $scope.productSearchForm.productName = $stateParams.productName;
        }

        if($stateParams.brandId) {
            $scope.productSearchForm.brandId = parseInt($stateParams.brandId);
        }

        if($stateParams.categoryId) {
            $scope.productSearchForm.categoryId = parseInt($stateParams.categoryId);
        }

        if($stateParams.cityId) {
            $scope.productSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.organizationId) {
            $scope.productSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.status) {
            $scope.productSearchForm.status = parseInt($stateParams.status);
        }

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.status = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        /*获取品牌*/
        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        /*获取餐馆列表信息*/
        $scope.searchProduct = function () {
            $location.search($scope.productSearchForm);

            $http({
                url: "/admin/api/sku",
                method: "GET",
                params: $scope.productSearchForm
            })
                .success(function (data, status, headers, config) {
                    $scope.skus = data.skus;

                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });
        }

        $scope.searchProduct();

        $scope.resetPageAndSearchProduct = function(){
            $scope.productSearchForm.page = 0;
            $scope.productSearchForm.pageSize = 100;

            $scope.searchProduct();
        }


        $scope.pageChanged = function() {
            $scope.productSearchForm.page = $scope.page.currentPage - 1;
            $scope.productSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchProduct();
        }

        /*编辑保存商品名称、大箱包装数量*/
        $scope.saveProduct = function(data, productId) {
            return $http.put("/admin/api/product/" + productId, data);
        };

        $scope.$watch('excelMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.productListSize) {
                    		successSize = data.productListSize;
                    	}
                    	if(data.exceptionMsg) {
                    		exceptionMsgSize = data.exceptionMsg.length;
                    	}
                    	var msg = "成功导入"+ successSize +"件商品， 失败了"+ exceptionMsgSize +"件商品\n";
                    	if(data.headMsg) {
                    		msg += data.headMsg + "\n";
                    	}
                    	if(data.errorFileName) {
                    		console.log(data.errorFileName);
                    		msg += "是否下载错误文件?";

                    		if(confirm(msg)){
                    			$window.open("/admin/api/dynamic-price/errorFile?errorFileName=" + data.errorFileName);
                    		}
                    	} else {
                    		alert(msg);
                    	}
                    	
                    }).error(function (data) {
                        alert("导入失败，excel格式错误");
                    })
                }
            }
        });
        
        $scope.$watch('photoMediaName', function(files) {
        	if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/photoImportByName',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.productListSize) {
                    		successSize = data.productListSize;
                    	}
                    	if(data.errorSize) {
                    		exceptionMsgSize = data.errorSize;
                    	}
                    	var msg = "成功导入"+ successSize +"张图片， 失败了"+ exceptionMsgSize +"张图片\n";
                    	if(data.errorMsg) {
                    		msg = data.errorMsg;
                    	}
                    	if(data.errorFileName) {
                    		console.log(data.errorFileName);
                    		msg += "是否下载错误文件?";

                    		if(confirm(msg)){
                    			$window.open("/admin/api/product/errorFile?fileName=" + data.errorFileName);
                    		}
                    	} else {
                    		alert(msg);
                    	}
                    	
                    }).error(function (data) {
                        alert("导入失败");
                    })
                }
            }
        });

        $scope.$watch('photoMediaId', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/photoImportById',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                        var successSize = 0;
                        var exceptionMsgSize = 0;
                        if(data.productListSize) {
                            successSize = data.productListSize;
                        }
                        if(data.errorSize) {
                            exceptionMsgSize = data.errorSize;
                        }
                        var msg = "成功导入"+ successSize +"张图片， 失败了"+ exceptionMsgSize +"张图片\n";
                        if(data.errorMsg) {
                            msg = data.errorMsg;
                        }
                        if(data.errorFileName) {
                            console.log(data.errorFileName);
                            msg += "是否下载错误文件?";

                            if(confirm(msg)){
                                $window.open("/admin/api/product/errorFile?fileName=" + data.errorFileName);
                            }
                        } else {
                            alert(msg);
                        }

                    }).error(function (data) {
                        alert("导入失败");
                    })
                }
            }
        });

        $scope.updateCapacityInBundle = function(sku) {
            $http({
                url: "/admin/api/sku/capacityInBundle/" + sku.id,
                method: "PUT",
                params: {capacityInBundle:sku.capacityInBundle},
                headers:{'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("修改成功");
            })
            .error(function (data) {
                alert("修改失败\n" + data.errmsg);
            });
        }
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuTagDetailCtrl', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            skuRequests: [],
            organization : [],
            cityIds:[],
            limitedCityIds:[],
            skuId:[]
        };

        $http.get("/admin/api/city")
        .success(function (data) {
            $scope.cities = data;
        });


        if ($stateParams.id) {
            $http.get("/admin/api/product/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                if (data.category) {
                    $scope.formData.categoryName = data.category.hierarchyName;
                }

                if(data.skus) {
                    $scope.formData.skuRequest = data.skus[0];
                    $scope.formData.skuRequest.status = $scope.formData.skuRequest.status.name;
                    $scope.formData.skuId = $scope.formData.skuRequest.id;
                    if ($scope.formData.skuRequest.skuTags) {
                        angular.forEach($scope.formData.skuRequest.skuTags, function(value, key) {
                            $scope.formData.cityIds.push(value.cityId);
                            if(value.limitedQuantity) {
                                $scope.formData.limitedCityIds.push(value.cityId);
                                $scope.formData.limitedQuantity = value.limitedQuantity;
                            }
                        })
                    }
                }
            });
        }else{

        }

        /*提交保存*/
        $scope.saveProduct = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/skuTag/sku/" + $scope.formData.skuId,
                    method: 'PUT',
                    params: {"cityIds":$scope.formData.cityIds,"limitedQuantity" : $scope.formData.limitedQuantity, "limitedCityIds" : $scope.formData.limitedCityIds},
                    contentType:"application/x-www-form-urlencoded"
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function () {
                        alert("保存失败!");
                    });
            }
        };
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuTagListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.products = [];

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.productSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            skuId: $stateParams.skuId,
            productName: $stateParams.productName,
            skuTagCityId : $stateParams.skuTagCityId,
            organizationId : $stateParams.organizationId
        };

        $http.get("/admin/api/organization?enable=true")
        .success(function (data, status, headers, config) {
            $scope.organizations = data.organizations;
            if ($scope.organizations && $scope.organizations.length == 1) {
                $scope.productSearchForm.organizationId = $scope.organizations[0].id;
            }
        })
        .error(function (data, status) {
            alert("数据加载失败！");
        });

        $http.get("/admin/api/city")
            .success(function (data) {
                $scope.cities = data;
            });

        $scope.$watch('productSearchForm.organizationId', function(oldValue, newValue) {
            if(oldValue) {
                $http.get("/admin/api/category")
                    .success(function (data, status, headers, config) {
                        $scope.categories = data;
                    });
                if (typeof old != 'undefined' && cityId != old) {
                    $scope.productSearchForm.categoryId = null;
                }
            } else {
                $scope.categories = [];
                $scope.productSearchForm.categoryId = null;

            }
        });

        if($stateParams.productId) {
            $scope.productSearchForm.productId = parseInt($stateParams.productId);
        }

        if($stateParams.skuId) {
            $scope.productSearchForm.skuId = parseInt($stateParams.skuId);
        }

        if ($stateParams.productName) {
            $scope.productSearchForm.productName = $stateParams.productName;
        }

        if($stateParams.brandId) {
            $scope.productSearchForm.brandId = parseInt($stateParams.brandId);
        }

        if($stateParams.categoryId) {
            $scope.productSearchForm.categoryId = parseInt($stateParams.categoryId);
        }

        if($stateParams.skuTagCityId) {
            $scope.productSearchForm.skuTagCityId = parseInt($stateParams.skuTagCityId);
        }

        if($stateParams.organizationId) {
            $scope.productSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.status) {
            $scope.productSearchForm.status = parseInt($stateParams.status);
        }

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.status = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        /*获取餐馆列表信息*/
        $scope.searchProduct = function () {
            $location.search($scope.productSearchForm);

            $http({
                url: "/admin/api/sku",
                method: "GET",
                params: $scope.productSearchForm
            })
                .success(function (data, status, headers, config) {
                    $scope.skus = data.skus;

                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });
        }

        $scope.searchProduct();

        $scope.resetPageAndSearchProduct = function(){
            $scope.productSearchForm.page = 0;
            $scope.productSearchForm.pageSize = 100;

            $scope.searchProduct();
        }


        $scope.pageChanged = function() {
            $scope.productSearchForm.page = $scope.page.currentPage - 1;
            $scope.productSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchProduct();
        }
    });
'use strict';

angular.module('sbAdminApp')
	.controller('SkuListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 50
		};

		$scope.searchForm = {status : 2, pageSize : $scope.page.itemsPerPage, type : parseInt($stateParams.type)};

		$scope.type = $stateParams.type;
		if ($stateParams.type == 0) {
			$scope.showPrimary = true;
			$scope.title = '商品信息';
		} else if ($stateParams.type == 2) {
			$scope.showFixedPrice = true;
			$scope.title = '商品定价历史信息';
		} else if ($stateParams.type == 1) {
			$scope.showPriceLimit = true;
			$scope.title = '商品限价历史信息';
		} else if ($stateParams.type == 4) {
			$scope.showPurchasePrice = true;
			$scope.title = '商品采购价历史信息';
		} else if ($stateParams.type == 3) {
			$scope.showSalePrice = true;
			$scope.title = '商品售价历史信息';
		}

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$http.get("/admin/api/category")
			.success(function (data, status, headers, config) {
				$scope.categories = data;
			});

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
				//$http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
				//	$scope.availableWarehouses = data;
				//	if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
				//		$scope.searchForm.warehouseId = $scope.availableWarehouses[0].id;
				//	}
				//});
				$http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
				});
				if(typeof oldVal != 'undefined' && newVal != oldVal){
					//$scope.searchForm.warehouseId = null;
					$scope.searchForm.organizationId = null;
				}
			}else{
				$scope.organizations = [];
				$scope.availableWarehouses = [];
				$scope.searchForm.organizationId = null;
				//$scope.searchForm.warehouseId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/sku/status")
		.success(function (data, status, headers, config) {
			$scope.status = data;
		})
		.error(function (data, status) {
			alert("数据加载失败！");
		});

		$http({
			url: "/admin/api/skuPrice/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.skuPriceList = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		$scope.export = function () {
			var str = [];
			for (var p in $scope.searchForm) {
				if ($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}
			$http.get("/admin/api/skuPrice/list/export?" + str.join("&"))
				.success(function (data) {
					alert("任务创建成功,请到 excel导出任务-我的任务 中下载");
				})
				.error(function (data) {
					alert("任务创建失败");
				})
		};
	});
'use strict';

angular.module('sbAdminApp')
	.controller('SkuPriceHistoryList', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		$scope.page = {
			itemsPerPage: 50
		};

		$scope.type = parseInt($stateParams.type);
		$scope.searchForm = {type: parseInt($stateParams.type), single: $stateParams.single, skuId: parseInt($stateParams.skuId), pageSize: parseInt($scope.page.itemsPerPage)};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$http({
			url:"/admin/api/purchase/order/sku",
			method:'GET',
			params:{cityId:parseInt($stateParams.cityId), skuId:$scope.searchForm.skuId}
		}).success(function (data, status, headers, config) {
			var singleOrBundle = '';
			if ($stateParams.single=='true') {
				singleOrBundle = '(单品)';
			} else if ($stateParams.single=='false') {
				singleOrBundle = '(打包)';
			}
			$scope.title = data.sku.id + '-' + data.sku.name + singleOrBundle;
		});

		$http({
			url: "/admin/api/skuPriceHistory/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.skuPriceHistoryList = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;

			$scope.showChart = data.labels && data.data && data.labels.length > 0 && data.data.length > 0;
			if ($scope.showChart) {
				$scope.labels = data.labels;
				$scope.series = ['价格历史'];
				$scope.data = [
					data.data
				];
			}
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}
	});
'use strict';

angular.module('sbAdminApp')
	.controller('SkuVendorDetailCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.iForm = {"fixedPrice":0, "salePriceLimit":0, changeFixedPriceReason:"新增", skuId:parseInt($stateParams.skuId)};

		$scope.showReason = false;

		$scope.originFixedPrice = 0;
		$scope.originSalePriceLimitPrice = 0;

		if($rootScope.user) {
			$scope.cities = $rootScope.user.cities;
		}

		if ($stateParams.id) {
			$scope.iForm.skuVendorId = parseInt($stateParams.id);
		}

		if ($stateParams.id) {
			$http.get("/admin/api/skuVendor/" + $stateParams.id)
			.success(function (data, status) {
				$scope.iForm.vendorId = data.vendor.id;
				$scope.iForm.organizationId = data.vendor.organization.id;
				$scope.iForm.fixedPrice = data.fixedPrice;
				$scope.iForm.singleSalePriceLimit = data.singleSalePriceLimit;
				$scope.iForm.bundleSalePriceLimit = data.bundleSalePriceLimit;
				$scope.originFixedPrice = data.fixedPrice;
				$scope.originSingleSalePriceLimitPrice = data.singleSalePriceLimit;
				$scope.originBundleSalePriceLimitPrice = data.bundleSalePriceLimit;
			})
			.error(function (data, status) {
				alert("获取sku信息失败...");
			});
		} else {
			$scope.showChangeFixedPriceReason = true;
			$scope.showChangeSalePriceLimitReason = true;
			$scope.iForm.changeSalePriceLimitReason = "新增";
		}

		$scope.$watch('iForm.cityId', function(newVal, oldVal) {
			if(newVal){
				$http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
					$scope.organizations = data;
					if ($scope.organizations && $scope.organizations.length == 1) {
						$scope.iForm.organizationId = $scope.organizations[0].id;
					}
				});
			}else{
				$scope.organizations = [];
			}
		});

		$scope.$watch('iForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.iForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.iForm.vendorId = null;
				$scope.vendors = [];
			}
		});

		$scope.$watch('iForm.fixedPrice', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originFixedPrice && $stateParams.id){
				$scope.iForm.changeFixedPriceReason = null;
				$scope.showChangeFixedPriceReason = true;
			}
		});

		$scope.$watch('iForm.singleSalePriceLimit', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originSingleSalePriceLimitPrice && $stateParams.id){
				//$scope.iForm.changeSalePriceLimitReason = null;
				$scope.showChangeSalePriceLimitReason = true;
			}
		});

		$scope.$watch('iForm.bundleSalePriceLimit', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originBundleSalePriceLimitPrice && $stateParams.id){
				//$scope.iForm.changeSalePriceLimitReason = null;
				$scope.showChangeSalePriceLimitReason = true;
			}
		});

		$scope.saveSkuVendor = function() {
			$http({
				url: "/admin/api/sku/updateSkuVendor",
				method: "POST",
				data: $scope.iForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("保存成功...");
				})
				.error(function (data, status, headers, config) {
					alert("保存失败...");
				});
		};

	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuVendorListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, editableOptions,
    $upload, $window) {

        $scope.page = {
            itemsPerPage: 100
        };

        $scope.searchForm = {status:2, pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $http.get("/admin/api/category")
            .success(function (data, status, headers, config) {
                $scope.categories = data;
            })

        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            });

        $http({
            url: "/admin/api/skuVendor/list",
            method: "GET",
            params: $scope.searchForm
        }).success(function (data) {
            $scope.skuVendorList = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('searchForm.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.searchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.searchForm.organizationId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.searchForm.organizationId = null;
           }
        });

        $http.get("/admin/api/sku/status").success(function(data) {
            $scope.skuStatuses = data;
        });

        $scope.$watch('searchForm.organizationId', function(organizationId) {
            if(organizationId) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId:$scope.searchForm.cityId,organizationId:organizationId}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        $scope.search = function() {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        }

        $scope.pageChanged = function() {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        }

        $scope.$watch('dynamicMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/sku-price/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.searchForm
                    })
                    .success(function (data) {
                        alert(data.message);
                    })
                    .error(function (data) {
                        alert("任务创建失败");
                    })
                }
            }
        });

        $scope.downloadTemplate = function(){
            $window.open("/admin/api/sku-price/downloadTemplate");
        }
    });
'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('sbAdminApp')
	.directive('header',function(){
		return {
        templateUrl:'app/directives/header/header.html',
        restrict: 'E',
        replace: true,
    	}
	});



'use strict';

angular.module('sbAdminApp')
    .directive('back', ['$window', function ($window) {
        return {
            restrict: 'A',

            link: function (scope, element, attrs) {
                element.bind('click', function () {
                    $window.history.back();
                });
            }
        };
    }]);
'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */

angular.module('sbAdminApp')
    .directive('sidebar', ['$location', function () {
        return {
            templateUrl: 'app/directives/sidebar/sidebar.html',
            restrict: 'E',
            replace: true,
            scope: {},
            controller: function ($scope, $state, $rootScope, $filter) {
                $scope.selectedMenu = 'dashboard';
                $scope.collapseVar = 0;
                $scope.multiCollapseVar = 0;

                $scope.check = function (x) {

                    if (x == $scope.collapseVar)
                        $scope.collapseVar = 0;
                    else
                        $scope.collapseVar = x;
                };

                $scope.multiCheck = function (y) {

                    if (y == $scope.multiCollapseVar)
                        $scope.multiCollapseVar = 0;
                    else
                        $scope.multiCollapseVar = y;
                };

                $scope.goTicket = function () {
                    // $state.go("https://cgwy.avosapps.com/tickets");
                    var uId = $rootScope.user.realname;
                    window.open("http://bm.canguanwuyou.cn/ticket/login&" + uId);
                }

                $scope.newTicket = function () {
                    var uId = $rootScope.user.realname;
                    var arr = {
                        "username": uId
                    };
                    arr = JSON.stringify(arr);
                    // console.log(arr);
                    arr = encodeURIComponent(arr);
                    window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data=" + arr);
                }

                $scope.hasPermission = $rootScope.hasPermission;
                $scope.hasRole = $rootScope.hasRole;
                $scope.hasGlobalManager = $rootScope.hasGlobalManager;

                var newDate = new Date();
                newDate.setDate(newDate.getDate() + 1);
                $scope.startDate = $filter('date')(new Date(), 'yyyy-MM-dd');
                $scope.startTime = $filter('date')(new Date(), 'yyyy-MM-dd 00:00');
                $scope.endDate = $filter('date')(newDate, 'yyyy-MM-dd');
                $scope.endTime = $filter('date')(newDate, 'yyyy-MM-dd 00:00');

                var now = new Date();

                $scope.today = $filter('date')(now, 'yyyy-MM-dd');
                $scope.yesterday = $filter('date')(new Date().setDate(now.getDate() - 1), 'yyyy-MM-dd');
                $scope.tomorrow = $filter('date')(new Date().setDate(now.getDate() + 1), 'yyyy-MM-dd');
                $scope.firstDayOfMonth = $filter('date')(new Date(now.getFullYear(), now.getMonth(), 1), 'yyyy-MM-dd');
                $scope.lastDayOfMonth = $filter('date')(new Date(now.getFullYear(), now.getMonth() + 1, 0), 'yyyy-MM-dd');


                var now2 = new Date();
                $scope.firstDateTimeOfMonth = $filter('date')(new Date(now2.getFullYear(), now2.getMonth(),1), 'yyyy-MM-dd')+ " 00:00";
                $scope.firstDateTimeOfNextMonth = $filter('date')(new Date(now2.getFullYear(), now2.getMonth()+1,1), 'yyyy-MM-dd')+" 00:00";


                $scope.yesterdayDatetime = $filter('date')(new Date().setDate(now.getDate() - 1), 'yyyy-MM-dd')+ " 00:00";
                $scope.tomorrowDatetime = $filter('date')(new Date().setDate(now.getDate() + 1), 'yyyy-MM-dd')+ " 00:00";

            }
        }
    }]);
'use strict';
angular.module('sbAdminApp')
    .controller('AccountCollectionPaymentMethodDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

        /*收付款方式*/
        $scope.accountCollectionPaymentMethod = {
            cash : false,
            valid : true
        };
        $scope.edit = false;

        /*根据id获取信息*/
        if ($stateParams.id) {
            $scope.edit = true;
            $http.get("/admin/api/account/collectionPaymentMethod/" + $stateParams.id).success(function (data, status) {
                $scope.accountCollectionPaymentMethod = data;
                $scope.accountCollectionPaymentMethod.cityId = data.cityId;
            }).error(function (data, status) {
                window.alert("获取信息失败...");
            });
        }

        /*添加/编辑收付款方式*/
        $scope.create = function () {
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/account/collectionPaymentMethod/' + $stateParams.id,
                    data: $scope.accountCollectionPaymentMethod,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("修改成功!");
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/account/collectionPaymentMethod',
                    data: $scope.accountCollectionPaymentMethod,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("添加成功!");
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                })
            }
        }
    });
'use strict';
angular.module('sbAdminApp')
    .controller('AccountCollectionPaymentMethodListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.formData = {};
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }
        $http({
            url: "/admin/api/account/collectionPaymentMethod/list",
            method: 'GET',
            params: $scope.formData,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.collectionPaymentMethods = data;
        }).error(function (data) {
            window.alert("加载失败...");
        });
        $scope.search = function () {
            $location.search($scope.formData);
        };
    });
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('AccountingPayableListCtrl', ['$scope', '$rootScope', '$http', '$filter', '$stateParams', '$state', '$compile', '$window', '$location', function($scope, $rootScope, $http, $filter, $stateParams, $state, $compile, $window, $location) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.edit) {
			$scope.searchForm.edit = parseInt($stateParams.edit);
		}

		if ($stateParams.edit == 1) {
			$scope.writeOffPayment = true;
			$scope.searchForm.includeWriteOff = false;
		} else {
			$scope.searchPayable = true;
			$scope.searchForm.includeWriteOff = true;
		}

		$scope.submitting = false;

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
						if ($scope.depots && $scope.depots.length == 1) {
						   $scope.searchForm.depotId = $scope.depots[0].id;
					   }
					});
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				$http.get("/admin/api/accounting/payment/methods/" + newVal)
					.success(function (data) {
						$scope.methods = data;
					});

				if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
				 }
			} else {
				$scope.methods = [];
				$scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/payable/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		$scope.accountPayablesToWriteOff = [];
		$scope.searchForm.checkedItemIds = [];
		$scope.isCheckedAll = false;
		$http({
			url: "/admin/api/accounting/payable/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalAmount = data.totalAmount;
			$scope.totalWriteOffAmount = data.totalWriteOffAmount;
			$scope.totalUnWriteOffAmount = data.totalUnWriteOffAmount;
			$scope.accountPayables = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.writeOff = function () {

			var invalid = false;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						var writeOffAmount = accountPayable.currentWriteOffAmount;
						if (Math.abs(writeOffAmount) > Math.abs(accountPayable.unWriteOffAmount)) {
							alert('本次销账金额大于未销金额');
							invalid = true;
						}

						if (writeOffAmount>=0 && writeOffAmount > accountPayable.balance) {
							alert('本次销账金额大于供应商账户余额');
							invalid = true;
						}
					}
				});
			});

			if (invalid) {
				return;
			}

		    $scope.submitting = true;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						$scope.accountPayablesToWriteOff.push(accountPayable);
					}
				});
			});
			$http({
				url: "/admin/api/accounting/payable/writeOff",
				method: "POST",
				data: $scope.accountPayablesToWriteOff,
			})
			.success(function (data, status, headers, config) {
				alert("核销成功...");
				$scope.submitting = false;
				//$location.search($scope.searchForm);
					$state.reload();
			})
			.error(function (data, status, headers, config) {
				alert("核销失败...");
				$scope.submitting = false;
			});
		};

		$scope.checkAll = function() {
			if(!$scope.isCheckedAll){
				$scope.searchForm.checkedItemIds = [];
				angular.forEach($scope.accountPayables, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.tableInvalid = function() {

			if (!$scope.searchForm.checkedItemIds || $scope.searchForm.checkedItemIds.length==0 || $scope.submitting) {
				return true;
			}

			var invalid = false;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						var writeOffAmount = accountPayable.currentWriteOffAmount;
						var writeOffDate = accountPayable.writeOffDate;
						var REGEX = /^\-?\d+(.\d+)?$/
						if (!writeOffDate || writeOffDate == "" || !angular.isDate(writeOffDate) ||!writeOffAmount || writeOffAmount == "" || !REGEX.test(writeOffAmount)) {
							invalid = true;
						}
					}
				});
			});

			return invalid;
		}

		$scope.exportAccountPayables = function(){
			if ($scope.writeOffPayment) {
				$scope.searchForm.includeWriteOff = false;
			} else {
				$scope.searchForm.includeWriteOff = true;
			}
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			if (str.length == 0) {
				$window.open("/admin/api/accounting/payable/exportAccountPayables");
			} else {
				$window.open("/admin/api/accounting/payable/exportAccountPayables?" + str.join("&"));
			}
		}

		$http.get("/admin/api/accounting/payable/types")
			.success(function(data) {
				$scope.accountPayableTypes = data;
			});

	}]);
'use strict';

angular.module('sbAdminApp')
    .controller('AccountingPaymentDetailCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        $scope.payment = {
        };

        $scope.submitting = false;

        $http.get("/admin/api/accounting/payment/types")
            .success(function (data) {
                $scope.types = data;
            })

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

        $scope.$watch('payment.cityId', function(newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations")
                    .success(function(data) {
                        $scope.organizations = data;
                    });
                $http.get("/admin/api/accounting/payment/methods/" + newVal)
                    .success(function (data) {
                        $scope.methods = data;
                    })
            } else {
                $scope.organizations = [];
                $scope.methods = [];
            }
        });

        $scope.$watch('payment.organizationId', function(newVal, oldVal) {
            if(newVal) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId:$scope.payment.cityId,organizationId:newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        if ($stateParams.id) {
            $http.get("/admin/api/coupon/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.coupon = data;
                    $scope.coupon.start = new Date(data.start).toISOString();
                    $scope.coupon.end = new Date(data.end).toISOString();
                })
                .error(function (data, status) {
                    window.alert("获取无忧券信息失败...");
                    return;
                });
        } else {
            $scope.addCoupon=true;
        }

        $scope.create = function () {
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/accounting/payment/add',
                data: $scope.payment,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("添加成功!");
                $scope.submitting = false;
                $state.go("oam.erp-accounting-payment-list");
            })
            .error(function (data, status, headers, config) {
                alert("添加失败!");
                $scope.submitting = false;
            })
        };
    });

'use strict';

angular.module('sbAdminApp')
	.controller('AccountingPaymentListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};
		$scope.searchForm.cancel = $stateParams.cancel;

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}


		$scope.openStart = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedStart = true;
		};

		$scope.openEnd = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedEnd = true;
		};

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';
		$scope.date = new Date().toLocaleDateString();

		$scope.$watch('searchForm.startDate', function(d) {
			if(d){
				$scope.searchForm.startDate = $filter('date')(d, 'yyyy-MM-dd');
			}
		});

		$scope.$watch('searchForm.endDate', function(d) {
			if(d){
				$scope.searchForm.endDate= $filter('date')(d, 'yyyy-MM-dd');
			}
		});

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
						if ($scope.depots && $scope.depots.length == 1) {
						   $scope.searchForm.depotId = $scope.depots[0].id;
						}
					});
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					    }
					});

				$http.get("/admin/api/accounting/payment/methods/" + newVal)
					.success(function (data) {
						$scope.methods = data;
					});
				if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			    }
			} else {
				$scope.methods = [];
				$scope.organizations = [];
			   $scope.depots = [];
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/payment/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/payment/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalAmount = data.totalAmount;
			$scope.payments = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.cancelPayment = function (payment) {
			$http({
				url: "/admin/api/accounting/payment/cancel",
				method: "POST",
				params: {"id":payment.id},
				headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("付款录入取消成功...");
				payment.hideCancel = true;
			})
			.error(function (data, status, headers, config) {
				alert("付款录入取消失败...");
			});
		};

		$scope.exportPayment = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			if (str.length == 0) {
				$window.open("/admin/api/accounting/payment/export");
			} else {
				$window.open("/admin/api/accounting/payment/export?" + str.join("&"));
			}
		}
	});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('AccountingPaymentWriteOffListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};
		$scope.searchForm.cancel = $stateParams.cancel;
		$scope.submitting = false;

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.cancel = $stateParams.cancel == "true";

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
						if ($scope.depots && $scope.depots.length == 1) {
						   $scope.searchForm.depotId = $scope.depots[0].id;
					   }
					});
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				$http.get("/admin/api/accounting/payment/methods/" + newVal)
					.success(function (data) {
						$scope.methods = data;
					})

				if(typeof oldVal != 'undefined' && newVal != oldVal){
					   $scope.searchForm.organizationId = null;
					   $scope.searchForm.depotId = null;
				   }
			} else {
				$scope.methods = [];
				$scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/payable/writeOff/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.cancel) {
			$scope.searchForm.status = 1;
		}

		$http({
			url: "/admin/api/accounting/payable/writeOff/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalWriteOffAmount = data.totalWriteOffAmount;
			$scope.accountPayableWriteOffs = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.disableCancelWriteOff = function (cancelDate) {
			if (!cancelDate || !angular.isDate(cancelDate)) {
				return true;
			} else {
				return false;
			}
		}
		$scope.cancelWriteOff = function (id, cancelDate) {

			if (!cancelDate || !angular.isDate(cancelDate)) {
				alter();
			}

		    $scope.submitting = true;
			$http({
				url: "/admin/api/accounting/payable/writeOff/cancel",
				method: "POST",
				params: {"writeOffId":id, "cancelDate" : cancelDate},
				headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("取消核销成功...");
				$scope.submitting = false;
				$state.go($state.current, $scope.searchForm, {reload: true});
			})
			.error(function (data, status, headers, config) {
				alert("取消核销失败...");
				$scope.submitting = false;
			});
		};

		$scope.exportAccountPayableWriteOffs = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/payable/writeOff/exportAccountPayableWriteOffs?" + str.join("&"));
		}

		$http.get("/admin/api/accounting/payable/types")
			.success(function(data) {
				$scope.accountPayableTypes = data;
			});
	});
'use strict';

angular.module('sbAdminApp')
    .controller('AccountReceivableListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $window) {

        $scope.type = $stateParams.type;
        $scope.accountReceivableForm = {
            type: $scope.type,
            startOrderDate: $filter('date')(new Date().setDate(new Date().getDate() - 2), 'yyyy-MM-dd'),
            endOrderDate: $filter('date')(new Date().setDate(new Date().getDate() - 1), 'yyyy-MM-dd')
        };
        if ($scope.type == "writeoff") {
            $scope.accountReceivableForm.accountReceivableStatus = 0;
        }
        $scope.trackers = [];
        $scope.page = {itemsPerPage: 100};
        $scope.totalAmount = [0, 0, 0];

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.format = 'yyyy-MM-dd';
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.accountReceivableForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('accountReceivableForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.accountReceivableForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.accountReceivableForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.accountReceivableForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.accountReceivableForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('accountReceivableForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.accountReceivableForm.cityId, newVal);
            }
        });
        if ($scope.type == "list") {
            $http({
                url: '/admin/api/accountReceivable/status/list',
                method: "GET",
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivableStatus = data;
            });
        }
        $http({
            url: '/admin/api/accountReceivable/type/list',
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.accountReceivableType = data;
        });

        $scope.SearchAccountReceivable = function (page) {
            $scope.accountReceivableForm.accountReceivableIds = [];
            $scope.accountReceivables = [];
            $scope.accountReceivableForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/accounting/receivable/list',
                method: "GET",
                params: $scope.accountReceivableForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivables = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount;
            });

        };

        $scope.pageChanged = function () {
            $scope.SearchAccountReceivable($scope.page.currentPage - 1);
        };

        $scope.isCheckedAll = false;
        $scope.accountReceivableForm.accountReceivableIds = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.accountReceivableForm.accountReceivableIds = [];
                angular.forEach($scope.accountReceivables, function (value) {
                    $scope.accountReceivableForm.accountReceivableIds.push(value.accountReceivableId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.accountReceivableForm.accountReceivableIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.writeoff = function (accountReceivableId) {
            $scope.accountReceivableForm.accountReceivableIds = [accountReceivableId];
            $scope.batchWriteoff();
        };
        $scope.batchWriteoff = function () {
            if ($scope.accountReceivableForm.accountReceivableIds.length == 0) {
                alert("请选择应收单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/accounting/receivable/writeoff",
                method: "PUT",
                data: $scope.accountReceivableForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("核销成功...");
                $scope.submitting = false;
                $scope.SearchAccountReceivable();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "核销失败...");
                $scope.submitting = false;
            });
        };

        $scope.accountReceivableForm.pageSize = $scope.page.itemsPerPage;
        $scope.SearchAccountReceivable();

        $scope.export = function () {
            var str = [];
            for (var p in $scope.accountReceivableForm) {
                if ($scope.accountReceivableForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.accountReceivableForm[p]));
                }
            }
            $window.open("/admin/api/accounting/receivable/export?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('AccountReceivableWriteoffCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $window) {

        $scope.type = $stateParams.type;
        $scope.accountReceivableForm = {
            type: $scope.type,
            startWriteoffDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            endWriteoffDate: $filter('date')(new Date(), 'yyyy-MM-dd')
        };
        if ($scope.type == "cancel") {
            $scope.accountReceivableForm.accountReceivableWriteoffStatus = 1;
        }
        $scope.trackers = [];
        $scope.page = {itemsPerPage: 100};
        $scope.totalAmount = [0, 0, 0];

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.format = 'yyyy-MM-dd';
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.accountReceivableForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('accountReceivableForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.accountReceivableForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.accountReceivableForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.accountReceivableForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.accountReceivableForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('accountReceivableForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.accountReceivableForm.cityId, newVal);
            }
        });
        if ($scope.type == "list") {
            $http({
                url: '/admin/api/accountReceivableWriteoff/status/list',
                method: "GET",
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivableWriteoffStatus = data;
            });
        }
        $http({
            url: '/admin/api/accountReceivable/type/list',
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.accountReceivableType = data;
        });

        $scope.SearchAccountReceivable = function (page) {
            $scope.accountReceivables = [];
            $scope.accountReceivableForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/accounting/receivableWriteoff/list',
                method: "GET",
                params: $scope.accountReceivableForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivables = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount;
            });
        };
        $scope.pageChanged = function () {
            $scope.SearchAccountReceivable($scope.page.currentPage - 1);
        };

        $scope.writeoffCancel = function (accountReceivableWriteoffId, cancelDate) {
            if (cancelDate == null) {
                alert("请输入核销取消时间!");
                return;
            }
            if (window.confirm("确认取消核销?") == true) {
                $scope.submitting = true;
                $scope.accountReceivableForm.accountReceivableWriteoffId = accountReceivableWriteoffId;
                $scope.accountReceivableForm.cancelDate = cancelDate;
                $http({
                    url: "/admin/api/accounting/receivableWriteoff/cancel",
                    method: "PUT",
                    data: $scope.accountReceivableForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("核销取消成功...");
                    $scope.submitting = false;
                    $scope.SearchAccountReceivable();
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "核销取消失败...");
                    $scope.submitting = false;
                });
            }
        };

        $scope.accountReceivableForm.pageSize = $scope.page.itemsPerPage;
        $scope.SearchAccountReceivable();

        $scope.export = function () {
            var str = [];
            for (var p in $scope.accountReceivableForm) {
                if ($scope.accountReceivableForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.accountReceivableForm[p]));
                }
            }
            $window.open("/admin/api/accounting/receivableWriteoff/export?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {
        $scope.id = $stateParams.id;
        $http({
            url: '/admin/api/stockIn/' + $scope.id,
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIn = data;
            $scope.stockInItems = data.stockInItems;
        });
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockInForm = {};
        $scope.stockInForm.saleReturn = $stateParams.saleReturn;
        if ($scope.stockInForm.saleReturn == 1) {
            $scope.stockInForm.stockInType = 2;
        }
        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockInForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockInForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInForm.depotId = null;
            }
            if (newVal) {
                $http({
                    url: "/admin/api/vendor",
                    method: 'GET',
                    params: {cityId: newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });
        $http.get("/admin/api/stockIn/type/list").success(function (data) {
            $scope.type = data;
        });
        $scope.filterSellReturn = function(t) {
            if ($scope.stockInForm.saleReturn == 1) {
                return t.value == 2;
            } else {
                return t.value != 2;
            }
        }
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $scope.$watch('stockInForm.stockInType', function (newVal, oldVal) {
            if (newVal == 2) {
                $http.get("/admin/api/stockIn/sellReturnType/list").success(function (data) {
                    $scope.returnType = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInForm.sellReturnType = null;
                }
            } else {
                $scope.returnType = [];
                $scope.stockInForm.sellReturnType = null;
            }
        });
        $http({
            url: '/admin/api/stockIn/query',
            method: "GET",
            params: $scope.stockInForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIns = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.searchStockIn = function () {
            $location.search($scope.stockInForm);
        };
        $scope.resetForm = function () {
            $scope.stockInForm = {
                saleReturn:$scope.stockInForm.saleReturn
            };
            if ($scope.stockInForm.saleReturn == 1) {
                $scope.stockInForm.stockInType = 2;
            }
        };
        $scope.pageChanged = function () {
            $scope.stockInForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockInForm) {
                if ($scope.stockInForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockInForm[p]));
                }
            }
            $window.open("/admin/api/stockIn/export/list?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInItemQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockInItemForm = {};
        $scope.stockInItemForm.saleReturn = $stateParams.saleReturn;
        if ($scope.stockInItemForm.saleReturn == 1) {
            $scope.stockInItemForm.stockInType = 2;
        }
        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInItemForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockInItemForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('stockInItemForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockInItemForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInItemForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInItemForm.depotId = null;
            }
            if (newVal) {
                $http({
                    url: "/admin/api/vendor",
                    method: 'GET',
                    params: {cityId: newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });
        $http.get("/admin/api/stockIn/type/list").success(function (data) {
            $scope.type = data;
        });
        $scope.filterSellReturn = function(t) {
            if ($scope.stockInItemForm.saleReturn == 1) {
                return t.value == 2;
            } else {
                return t.value != 2;
            }
        }
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $scope.$watch('stockInItemForm.stockInType', function (newVal, oldVal) {
            if (newVal == 2) {
                $http.get("/admin/api/stockIn/sellReturnType/list").success(function (data) {
                    $scope.returnType = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInItemForm.sellReturnType = null;
                }
            } else {
                $scope.returnType = [];
                $scope.stockInItemForm.sellReturnType = null;
            }
        });
        $http({
            url: '/admin/api/stockInItem/query',
            method: "GET",
            params: $scope.stockInItemForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockInItems = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.totalCost = data.amount[0];
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.searchStockInItem = function () {
            $location.search($scope.stockInItemForm);
        };
        $scope.resetForm = function () {
            $scope.stockInItemForm = {
                saleReturn:$scope.stockInItemForm.saleReturn
            };
            if ($scope.stockInItemForm.saleReturn == 1) {
                $scope.stockInItemForm.stockInType = 2;
            }
        };
        $scope.pageChanged = function () {
            $scope.stockInItemForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInItemForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockInItemForm) {
                if ($scope.stockInItemForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockInItemForm[p]));
                }
            }
            $window.open("/admin/api/stockInItem/export/list?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {
        $scope.stockOutType = $stateParams.stockOutType;
        $scope.id = $stateParams.id;
        $http({
            url: '/admin/api/stockOut/' + $scope.id,
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOut = data;
            $scope.stockOutItems = data.stockOutItems;
        });
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/stockOut/" + $stateParams.id).success(function (data) {
            $scope.stockOut = data;
        }).error(function () {
            alert("加载失败...");
        });
    });


'use strict';

angular.module('sbAdminApp')
    .controller('StockOutQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockOutForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockOutForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                    $scope.stockOutForm.sourceDepotId = null;
                    $scope.stockOutForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockOutForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockOutForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }
        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutForm.cityId, newVal);
                }
            });
        }
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.searchStockOut = function () {
            $location.search($scope.stockOutForm);
        };
        $scope.resetForm = function () {
            $scope.stockOutForm = {
                stockOutType: $stateParams.stockOutType
            };
        };
        $scope.pageChanged = function () {
            $scope.stockOutForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/export/list?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutItemQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutItemForm = {
            stockOutType: $scope.stockOutType
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutItemForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockOutItemForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutItemForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutItemForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockOutItemForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutItemForm.depotId = null;
                    $scope.stockOutItemForm.sourceDepotId = null;
                    $scope.stockOutItemForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutItemForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockOutItemForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockOutItemForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutItemForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }
        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutItemForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutItemForm.cityId, newVal);
                }
            });
        }
        $http({
            url: '/admin/api/stockOutItem/query',
            method: "GET",
            params: $scope.stockOutItemForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOutItems = data.content;
            $scope.totalAmount = data.amount;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.searchStockOutItem = function () {
            $location.search($scope.stockOutItemForm);
        };
        $scope.resetForm = function () {
            $scope.stockOutItemForm = {
                stockOutType: $stateParams.stockOutType
            };
        };
        $scope.pageChanged = function () {
            $scope.stockOutItemForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutItemForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockOutItemForm) {
                if ($scope.stockOutItemForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutItemForm[p]));
                }
            }
            $window.open("/admin/api/stockOutItem/export/list?" + str.join("&"));
        };

    });
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('VendorAccountListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				if(typeof oldVal != 'undefined' && newVal != oldVal){
					   $scope.searchForm.organizationId = null;
				   }
			} else {
				$scope.organizations = [];
				$scope.searchForm.organizationId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/vendorAccount/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.vendorAccountBalances = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");

		});

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/vendorAccount/list/export?" + str.join("&"));
		}
	});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('VendorAccountDetailListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
			   }
			} else {
				$scope.organizations = [];
				$scope.searchForm.organizationId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/purchase/order/types")
			.success(function(data) {
				$scope.purchaseOrderTypes = data;
			});

		$http.get("/admin/api/accounting/payable/types")
			.success(function(data) {
				$scope.accountPayableTypes = data;
			});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/payableItem/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.accountPayableItems = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");

		});

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/payableItem/list/export?" + str.join("&"));
		}
	});
'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('VendorTradingListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});
				if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
			   }
			} else {
				$scope.organizations = [];
				$scope.searchForm.organizationId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/vendorAccountOperation/types").success(function(data) {
			$scope.vendorAccountOperationTypes = data;
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/vendorAccountHistory/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.vendorAccountHistories = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/vendorAccountHistory/list/export?" + str.join("&"));
		}

	});
'use strict';

angular.module('sbAdminApp')
    .controller('CategorySellerProfitListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http({
            url: '/admin/api/profit/categorySellerProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.sellers = data.sellers;
            $scope.categories = data.categories;
            $scope.profits = data.profits;
        }).error(function () {
            alert("加载失败...");
        });
        $scope.search = function () {
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $http.get("/admin/api/profit/categorySellerProfit/export?" + str.join("&"))
                .success(function (data) {
                    alert("任务创建成功,请到 excel导出任务-我的任务 中下载");
                })
                .error(function (data) {
                    alert("任务创建失败");
                })
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('CustomerSellerProfitListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        $http.get("/admin/api/accountReceivable/type/list").success(function (data) {
            $scope.accountReceivableTypes = data;
        });
        $http.get("/admin/api/restaurant/status").success(function (data) {
            $scope.restaurantStatuses = data;
        });
        $http({
            url: '/admin/api/profit/customerSellerProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.sellerNames = data.sellerNames;
            $scope.warehouseNames = data.warehouseNames;
            $scope.restaurantIds = data.restaurantIds;
            $scope.restaurantNames = data.restaurantNames;
            $scope.receiverNames = data.receiverNames;
            $scope.telephones = data.telephones;
            $scope.categories = data.categories;
            $scope.profits = data.profits;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $http.get("/admin/api/profit/customerSellerProfit/export?" + str.join("&"))
                .success(function (data) {
                    alert("任务创建成功,请到 excel导出任务-我的任务 中下载");
                })
                .error(function (data) {
                    alert("任务创建失败");
                })
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('customerSkuProfitListController', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        $http.get("/admin/api/accountReceivable/type/list").success(function (data) {
            $scope.accountReceivableTypes = data;
        });
        $http({
            url: '/admin/api/profit/customerSkuProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.skuProfits = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/profit/customerSkuProfit/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('SkuProfitListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        $http.get("/admin/api/accountReceivable/type/list").success(function (data) {
            $scope.accountReceivableTypes = data;
        });
        $http.get("/admin/api/category").success(function (data) {
            $scope.categories = data;
        });
        $http({
            url: '/admin/api/profit/skuProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.profits = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/profit/skuProfit/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('SkuSalesListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        $http.get("/admin/api/accountReceivable/type/list").success(function (data) {
            $scope.accountReceivableTypes = data;
        });
        $http.get("/admin/api/category").success(function (data) {
            $scope.categories = data;
        });
        $http({
            url: '/admin/api/profit/skuSales/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.skuSales = data.content;
            $scope.salesAmountSummation = data.amount[0];
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/profit/skuSales/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('skuSellSummeryProfitListController', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });
        $http.get("/admin/api/accountReceivable/type/list").success(function (data) {
            $scope.accountReceivableTypes = data;
        });
        $http({
            url: '/admin/api/profit/skuSellSummeryProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.skuProfits = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/profit/skuSellSummeryProfit/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('WarehouseCategoryProfitListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http({
            url: '/admin/api/profit/warehouseCategoryProfit/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.warehouses = data.warehouses;
            $scope.categories = data.categories;
            $scope.profits = data.profits;
        }).error(function () {
            alert("加载失败...");
        });
        $scope.search = function () {
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        //$scope.export = function () {
        //    var str = [];
        //    for (var p in $scope.searchForm) {
        //        if ($scope.searchForm[p]) {
        //            str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
        //        }
        //    }
        //    $window.open("/admin/api/grossProfit/export?" + str.join("&"));
        //};

    });
'use strict';

angular.module('sbAdminApp')
	.controller('CutOrderListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $state) {

		$scope.searchForm = {};

		$scope.canCheck = false;

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm.pageSize = $scope.page.itemsPerPage;

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
		   }
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search($scope.searchForm);
		}

		$scope.search = function() {
			$scope.searchForm.checkedItemIds = [];
			$scope.isCheckedAll = false;
			$scope.canCheck = false;
			$http({
				url: "/admin/api/purchase/order/cut-order-list",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.cutOrders = data.content;
				$scope.page.totalItems = data.total;
				$scope.page.currentPage = data.page + 1;
				angular.forEach($scope.cutOrders, function(cutOrder, key){
					if (cutOrder.status.value == 1) {
						$scope.searchForm.checkedItemIds.push(cutOrder.id);
					}

					if ($scope.searchForm.checkedItemIds.length == 0) {
						$scope.canCheck = true;
					}
				});
			})
			.error(function (data, status, headers, config) {
				alert("加载失败...");
			});;
		}

		$scope.checkAll = function() {
			if(!($scope.isCheckedAll)){
				angular.forEach($scope.cutOrders, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.purchaseResult = function() {
			$http({
				url: "/admin/api/purchase/order/createAccordingResult",
				method: "POST",
				data: $scope.searchForm.checkedItemIds,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				$state.go("oam.purchase-according-result", {cityId:$scope.searchForm.cityId,organizationId:$scope.searchForm.organizationId,depotId:$scope.searchForm.depotId,cutOrders:$scope.searchForm.checkedItemIds});
			})
			.error(function (data, status, headers, config) {
				alert("请求失败...");
			});
		};

	});
'use strict';

var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('PurchaseAccordingResultCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $state, $interval, $timeout) {

	$scope.page = {
		itemsPerPage: 40
	};

	$scope.searchForm = {};

	$scope.searchForm.cityId = parseInt($stateParams.cityId);

	$scope.searchForm.organizationId = parseInt($stateParams.organizationId);

	$scope.searchForm.depotId = parseInt($stateParams.depotId);

	$scope.total = parseFloat(0);

	$scope.searchForm.skuIds = [];

	$scope.searchForm.checkedItemSignIds = [];

	$scope.isCheckedAll = false;

	if (!angular.isArray($stateParams.cutOrders)) {
		$scope.searchForm.cutOrders = [];
		$scope.searchForm.cutOrders.push($stateParams.cutOrders);
	} else {
		$scope.searchForm.cutOrders = $stateParams.cutOrders;
	}

	$scope.searchForm.type = 2;
	$scope.submitting = false;

	$scope.searchForm.pageSize = $scope.page.itemsPerPage;

	if($rootScope.user) {
		var data = $rootScope.user;
		$scope.cities = data.cities;
	}

	$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
		if (newVal) {
			$http.get("/admin/api/depot/list/" + newVal)
				.success(function (data, status, headers, config) {
					$scope.depots = data;
				});
			$http.get("/admin/api/city/" + newVal + "/organizations").success(function(data) {
				$scope.organizations = data;
			});
		} else {
			$scope.depots = [];
			$scope.organizations = [];
		}
	});

	$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
		if(newVal) {
			$http({
				url:"/admin/api/vendor",
				method:'GET',
				params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
			}).success(function (data) {
				$scope.vendors = data.vendors;
			});
		} else {
			$scope.vendors = [];
		}
	});

	$http.get("/admin/api/purchase/order/statuses")
		.success(function (data) {
			$scope.statuses = data;
		})

	$http.get("/admin/api/purchase/order/item/signList")
		.success(function (data) {
			$scope.signList = data;
		})

	$scope.pageChanged = function() {
		if ($scope.tableform.$invalid||$scope.submitting) {
			alert('保存失败...');
			return;
		}

		$scope.savePurchaseAccordingResult(function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search();
		});
	}

	$scope.deleteCalculateWatchers = function() {
		var watchers = [];
		var REGEX = /^purchaseOrderItems\[\d+\]\.purchaseTotalPrice$/
		angular.forEach($scope.$$watchers, function(watcher, key) {
			if (!REGEX.test(watcher.exp)) {
				watchers.push(watcher);
			}
		});
		$scope.$$watchers = watchers;
	}

	$scope.search = function () {
		delete $scope.searchForm.purchaseOrderItems;
		$http({
			url: "/admin/api/purchase/order/items",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.searchForm.skuIds = [];
			$scope.searchForm.checkedItemSignIds = [];
			$scope.deleteCalculateWatchers();
			$scope.total = data.totalAmount;
			$scope.purchaseOrderItems = data.content;
			angular.forEach($scope.purchaseOrderItems, function(item, key){
				item.vendorId = item.purchaseOrder.vendor.id;
				$scope.searchForm.skuIds.push(item.sku.id);
				if (item.sign == 1) {
					$scope.searchForm.checkedItemSignIds.push(item.sku.id);
				}
			});

			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
			if ($scope.purchaseOrderItems.length != 0 && $scope.purchaseOrderItems[0].purchaseOrder.status.value == 1) {
				$scope.canEdit = true;
			}

		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});
	}

	$scope.resetPageAndSearchProduct = function(){
		$scope.searchForm.page = 0;
		$scope.search();
	}

	$scope.search();

	$scope.savePurchaseAccordingResult = function (callback) {
	    $scope.submitting = true;
		$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;

		$http({
			url: "/admin/api/purchase/order/accordingResult",
			method: "POST",
			data: $scope.searchForm,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
			$scope.submitting = false;
			alert("保存成功...");
			if (callback) {
				callback();
			}
		})
		.error(function (data, status, headers, config) {
			alert("保存失败...");
			$scope.submitting = false;
		});
	}

	$scope.submitPurchaseAccordingResult = function () {
		$scope.savePurchaseAccordingResult(function() {
			$scope.showProgress = true;
			$scope.random();
			$scope.submitting = true;
			$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;
			$http({
				url: "/admin/api/purchase/order/submitAccordingResult",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				if (data.success == false) {
					alert(data.msg);
					$scope.progress = 0;
					$scope.showProgress = false;
					$scope.submitting = false;
				} else {
					$scope.progress = 100;
                    $timeout(function () {
						$scope.showProgress = false;
						$scope.canEdit = false;
						$scope.submitting = false;
						$state.go("oam.cut-order-list");
                	}, 1000);
				}
			})
			.error(function (data, status, headers, config) {
				$scope.progress = 0;
				$scope.showProgress = false;
				alert("提交失败...");
				$scope.submitting = false;
			});
		});
	}

	$scope.showVendor = function(vendorId) {
		var v = $filter('filter')($scope.vendors, {id: vendorId});
		return $filter('filter')($scope.vendors, {id: vendorId})[0].name;
	};

	$scope.purchaseOrderItems = [
	];

	$scope.savePurchaseQuantity = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseQuantity || !REGEX.test(purchaseOrderItem.purchaseQuantity)) {
			purchaseOrderItem.purchaseQuantity = 0;
		}
		purchaseOrderItem.purchaseBundleQuantity = (purchaseOrderItem.purchaseQuantity / purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity).toFixed(6);
	};

	$scope.savePurchaseBundleQuantity = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseBundleQuantity || !REGEX.test(purchaseOrderItem.purchaseBundleQuantity)) {
			purchaseOrderItem.purchaseBundleQuantity = 0;
		}
		purchaseOrderItem.purchaseQuantity = Math.round(purchaseOrderItem.purchaseBundleQuantity * purchaseOrderItem.capacityInBundle);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchaseBundlePrice * purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
	};

	$scope.savePurchasePrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchasePrice || !REGEX.test(purchaseOrderItem.purchasePrice)) {
			purchaseOrderItem.purchasePrice = 0;
		}
		purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity).toFixed(6);
	};

	$scope.savePurchaseBundlePrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseBundlePrice || !REGEX.test(purchaseOrderItem.purchaseBundlePrice)) {
			purchaseOrderItem.purchaseBundlePrice = 0;
		}
		purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseBundlePrice / purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchaseBundlePrice * purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
	};

	$scope.savePurchaseTotalPrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseTotalPrice || !REGEX.test(purchaseOrderItem.purchaseTotalPrice)) {
			purchaseOrderItem.purchaseTotalPrice = 0;
		}

		if (purchaseOrderItem.purchaseQuantity != 0) {
			purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseQuantity).toFixed(6);
			purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
		}
	};

	$scope.$watchCollection('searchForm.checkedItemIds', function(newVal, oldVal) {
		if (newVal && newVal.length > 0) {
			$scope.batchUpdate = true;
		} else {
			$scope.batchUpdate = false;
		}
	});

	$scope.searchForm.checkedItemIds = [];

	$scope.checkAll = function() {
		if(!($scope.isCheckedAll)){
			angular.forEach($scope.purchaseOrderItems, function(value, key){
				$scope.searchForm.checkedItemIds.push(value.id);
			});
			$scope.isCheckedAll = true;
		}else{
			$scope.searchForm.checkedItemIds = [];
			$scope.isCheckedAll = false;
		}
	};

	$scope.checkAllSigns = function($event) {
		var checkbox = $event.target;
		var sign = checkbox.checked ? 1 : 0;
		if(checkbox.checked){
			angular.forEach($scope.purchaseOrderItems, function(value, key){
				$scope.searchForm.checkedItemSignIds.push(value.sku.id);
			});
		}else{
			$scope.searchForm.checkedItemSignIds = [];
		}

		var data = {};
		angular.extend(data, {cityId:$scope.searchForm.cityId});
		angular.extend(data, {depotId:$scope.searchForm.depotId});

		angular.extend(data, {skuIds:$scope.searchForm.skuIds});
		angular.extend(data, {sign:sign});

		$http({
			url: "/admin/api/purchase/order/changePurchaseOrderItemSign",
			method: "POST",
			data: data,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
		})
		.error(function (data, status, headers, config) {
		});
	};

	$scope.checkSign = function(purchaseOrderItem, $event) {
		var checkbox = $event.target;
		var sign = checkbox.checked ? 1 : 0;

		var data = {};
		angular.extend(data, {cityId:$scope.searchForm.cityId});
		angular.extend(data, {depotId:$scope.searchForm.depotId});

		var skuIds = [];
		skuIds.push(purchaseOrderItem.sku.id);
		angular.extend(data, {skuIds:skuIds});
		angular.extend(data, {sign:sign});

		$http({
			url: "/admin/api/purchase/order/changePurchaseOrderItemSign",
			method: "POST",
			data: data,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
		})
		.error(function (data, status, headers, config) {
		});
	};

	$scope.$watch('searchForm.batchVendorId', function(newBatchVendorId, oldBatchVendorId) {
		if ($scope.searchForm.checkedItemIds && $scope.searchForm.checkedItemIds.length > 0 && newBatchVendorId && newBatchVendorId != "") {
			angular.forEach($scope.purchaseOrderItems, function(item, key){
				angular.forEach($scope.searchForm.checkedItemIds, function(checkedId, key){
					if(checkedId == item.id){
						item.vendorId = newBatchVendorId;
					}
				});
			});
			$scope.batchUpdate = false;
			$scope.searchForm.batchVendorId = "";
		}
	});

	$scope.$watchCollection('purchaseOrderItems', function(newVal, oldVal) {
		for (var index = 0; index < $scope.purchaseOrderItems.length; index++) {
			var exp = 'purchaseOrderItems[' + index  + '].purchaseTotalPrice';

			if (!$scope.existsWatcherByExp(exp)) {
				$scope.$watch(exp, function(newVal, oldVal) {
					if (newVal != oldVal) {
						if (oldVal) {
							$scope.total = $scope.total - parseFloat(oldVal);
						}

						if (newVal) {
							$scope.total = $scope.total + parseFloat(newVal);
						}
					}
				}, true);
			}
		}
	});

	$scope.existsWatcherByExp = function(exp) {
		var exists = false;
		angular.forEach($scope.$$watchers, function(watcher, key) {
			if (watcher.exp === exp) {
				exists = true;
			}
		});
		return exists;
	}

	$scope.showProgress = false;
	$scope.progress = 0;//当前进度
	$scope.maxProgress = 100;//总进度

	$scope.random = function() {
		$scope.progress = 0;
		var intervalSeconds = 3;//n秒更新一次进度
		var totalSteps = 120;//总秒数
		var steps = 0;//已完成秒数
		var times = 0;//已更新次数

		$interval(function() {
			steps = Math.max(times * intervalSeconds, steps);
			steps = steps + Math.floor((Math.random() * intervalSeconds) + 1);
			$scope.progress = Math.floor((steps > totalSteps ? totalSteps : steps)  * $scope.maxProgress / totalSteps);
			if ($scope.progress == $scope.maxProgress) {
				$scope.progress = $scope.maxProgress - 1;
			}
			times = times + 1;
		}, intervalSeconds * 1000, totalSteps / intervalSeconds);

	};
});
'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderAddCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		if ($stateParams.add == 1) {
			$scope.add = true;
			$scope.edit = true;
		}

		if ($stateParams.edit == 1) {
			$scope.edit = true;
		}

		if ($stateParams.audit == 1) {
			$scope.canAudit = true;
		}
		$scope.searchForm = {};
        $scope.submitting = false;

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.candidateSkus = [];

		$scope.funcAsync = function (name) {
			if (name && name !== "") {
				$scope.candidateSkus = [];
				$http({
					url:"/admin/api/sku/candidates",
					method:'GET',
					params:{organizationId:$scope.searchForm.organizationId, name:name, showLoader:false}
				}).success(function (data) {
					$scope.candidateSkus = data;
				});
			}
		}

		$scope.resetCandidateSkus = function () {
			$scope.candidateSkus = [];
		}
		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
		   }
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		if ($scope.add) {
			$scope.$watch('searchForm.vendorId', function(newVal, oldVal) {
				if(newVal) {
					$http({
						url:"/admin/api/purchase/order/preItems",
						method:'GET',
						params:{vendorId:newVal, cityId:$scope.searchForm.cityId}
					}).success(function (data) {
						$scope.purchaseOrderItems = data.content;
						$scope.candidateSkus = [];
						angular.forEach($scope.purchaseOrderItems, function(item, key) {
							$scope.candidateSkus.push(item.sku);
						});
						console.log($scope.candidateSkus)
					});
				} else {
					$scope.purchaseOrderItems = [];
				}
			});
		}

		$scope.changeDepotId = function () {
			$scope.purchaseOrderItems = [];
		};

		$scope.purchaseOrderTotal = parseFloat(0);

		$scope.submit = function () {
			$scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;
			$http({
				url: "/admin/api/purchase/order/add",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("保存成功...");
				$scope.submitting = false;
				$state.go("oam.purchase-order-list", {audit:0,type:1,listType:1,toPrint:0});
			})
			.error(function (data, status, headers, config) {
				$scope.tableform.$show();
				alert("保存失败...");
				$scope.submitting = false;
			});
		}

		$scope.purchaseOrderTotal = parseFloat(0);

		$scope.purchaseOrderItems = [
		];

		$scope.remove = function(index) {
			$scope.purchaseOrderItems.splice(index, 1);
		}

		$scope.addItem = function() {
			$scope.inserted = {
			};
			$scope.purchaseOrderItems.push($scope.inserted);
		};

		$scope.$watchCollection('purchaseOrderItems', function(newVal, oldVal) {
			for (var index = 0; index < $scope.purchaseOrderItems.length; index++) {
				var exp = 'purchaseOrderItems[' + index  + '].purchaseTotalPrice';

				if (!$scope.existsWatcherByExp(exp)) {
					$scope.$watch(exp, function(newVal, oldVal) {
						if (newVal != oldVal) {
							if (oldVal) {
								$scope.purchaseOrderTotal = $scope.purchaseOrderTotal - parseFloat(oldVal);
							}

							if (newVal) {
								$scope.purchaseOrderTotal = $scope.purchaseOrderTotal + parseFloat(newVal);
							}
						}
					}, true);
				}
			}
		});

		$scope.existsWatcherByExp = function(exp) {
			var exists = false;
			angular.forEach($scope.$$watchers, function(watcher, key) {
				if (watcher.exp === exp) {
					exists = true;
				}
			});
			return exists;
		}

		$scope.searchSku = function(purchaseOrderItem) {
			$scope.candidateSkus = [];
			purchaseOrderItem.purchaseQuantity = 0;
			purchaseOrderItem.purchaseBundleQuantity = 0;
			purchaseOrderItem.purchasePrice = 0;
			purchaseOrderItem.purchaseTotalPrice = 0;

			$http({
				url:"/admin/api/purchase/order/sku",
				method:'GET',
				params:{cityId:$scope.searchForm.cityId, skuId:purchaseOrderItem.skuId, status:2}
			}).success(function (data, status, headers, config) {
				if (!data.sku) {
					alert('sku不存在或已失效');
					purchaseOrderItem.skuId = '';
					return;
				}
				$scope.candidateSkus.push(data.sku);
				purchaseOrderItem.name = data.sku.name;
				purchaseOrderItem.rate = data.sku.rate;
				if (data.stockTotal) {
					purchaseOrderItem.quantity = data.stockTotal.quantity;
					purchaseOrderItem.avgCost = data.stockTotal.avgCost;
				} else {
					purchaseOrderItem.quantity = 0;
					purchaseOrderItem.avgCost = 0;
				}
				purchaseOrderItem.singleUnit = data.sku.singleUnit;
				purchaseOrderItem.bundleUnit = data.sku.bundleUnit;
				purchaseOrderItem.capacityInBundle = data.sku.capacityInBundle;
				purchaseOrderItem.fixedPrice = data.fixedPrice;
				purchaseOrderItem.lastPurchasePrice = data.lastPurchasePrice;
			});
		};

		$scope.savePurchaseQuantity = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseQuantity || !REGEX.test(purchaseOrderItem.purchaseQuantity)) {
				purchaseOrderItem.purchaseQuantity = 0;
			}
			purchaseOrderItem.purchaseBundleQuantity = (purchaseOrderItem.purchaseQuantity / purchaseOrderItem.capacityInBundle).toFixed(6);
			purchaseOrderItem.purchaseTotalPrice = purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity;
		};

		$scope.savePurchaseBundleQuantity = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseBundleQuantity || !REGEX.test(purchaseOrderItem.purchaseBundleQuantity)) {
				purchaseOrderItem.purchaseBundleQuantity = 0;
			}
			purchaseOrderItem.purchaseQuantity = Math.round(purchaseOrderItem.purchaseBundleQuantity * purchaseOrderItem.capacityInBundle);
			purchaseOrderItem.purchaseTotalPrice = purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity;

		};

		$scope.savePurchasePrice = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchasePrice || !REGEX.test(purchaseOrderItem.purchasePrice)) {
				purchaseOrderItem.purchasePrice = 0;
			}
			purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.capacityInBundle).toFixed(6);
			purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity).toFixed(6);
		};

		$scope.savePurchaseBundlePrice = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseBundlePrice || !REGEX.test(purchaseOrderItem.purchaseBundlePrice)) {
				purchaseOrderItem.purchaseBundlePrice = 0;
			}
			purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseBundlePrice / purchaseOrderItem.capacityInBundle).toFixed(6);
			purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchaseBundlePrice * purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
		};

		$scope.savePurchaseTotalPrice = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseTotalPrice || !REGEX.test(purchaseOrderItem.purchaseTotalPrice)) {
				purchaseOrderItem.purchaseTotalPrice = 0;
			}
			purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseQuantity).toFixed(6);
			purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
		};

		if ($stateParams.id) {
			$http.get("/admin/api/purchase/order/" + $stateParams.id)
				.success(function (data, status) {
					$scope.searchForm.cityId = data.cityId;
					$scope.searchForm.organizationId = data.organizationId;
					$scope.searchForm.vendorId = data.vendor.id;
					$scope.searchForm.depotId = data.depot.id;
					$scope.searchForm.expectedArrivedDate = data.expectedArrivedDate;
					$scope.searchForm.remark = data.remark;
					$scope.searchForm.opinion = data.opinion;
					if ($scope.searchForm.opinion != null) {
						$scope.showOpinion = true;
					}
					$scope.purchaseOrderItems = data.purchaseOrderItems;
					angular.forEach($scope.purchaseOrderItems, function(item, key) {
						$scope.candidateSkus.push(item.sku);
					});

					$scope.purchaseOrderTotal = data.total;
				})
				.error(function (data, status) {
					window.alert("获取采购单信息失败...");
					return;
				});
		}

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/purchase/order/audit",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("审批完成...");
				$scope.submitting = false;
				$state.go("oam.purchase-order-list", {audit:1,type:1,listType:1,toPrint:0});
			})
			.error(function (data, status, headers, config) {
				alert("审批失败...");
				$scope.submitting = false;
			});
		}
	});
'use strict';

angular.module('sbAdminApp')
    .controller('PurchaseOrderInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/purchase/order/info/" + $stateParams.id).success(function (data) {
            $scope.purchaseOrder = data;
        }).error(function () {
            alert("加载失败...");
        });
    });


'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.type) {
			$scope.searchForm.type = $stateParams.type;
		}

		if ($stateParams.listType) {
			$scope.searchForm.listType = parseInt($stateParams.listType);
		}

		if ($stateParams.toPrint) {
			$scope.searchForm.toPrint = parseInt($stateParams.toPrint);
		}

		if ($stateParams.type == 1) {
			$scope.stockup = true;
		} else if ($stateParams.type == 2) {
			$scope.according = true;
		}

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		if ($stateParams.listType == 1) {
			$scope.purchase = true;
		} else if ($stateParams.listType == 2) {
			$scope.return = true;
		}

		if ($stateParams.toPrint == 1) {
			$scope.toPrint = true;
		}

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
		   }
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/purchase/order/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$http.get("/admin/api/purchase/order/printStatus")
			.success(function (data) {
				$scope.printStatuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.audit && $scope.purchase) {
			$scope.searchForm.status = 2;
		} else if ($scope.return) {
			$scope.searchForm.status = 4;
		}


		if ($scope.toPrint) {
			$scope.searchForm.status = 3;
		}

		$http({
			url: "/admin/api/purchase/order/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.sum = data.sum;
			$scope.purchaseOrders = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.submitPurchaseOrder = function (purchaseOrder) {
			$http({
				url: "/admin/api/purchase/order/submit/" + purchaseOrder.id,
				method: "GET",
			})
				.success(function (data, status, headers, config) {
					purchaseOrder.status = data;
					alert("提交审核成功...");
				})
				.error(function (data, status, headers, config) {
					alert("提交审核失败...");
				});
		}

		$scope.cancelPurchaseOrder = function (purchaseOrder) {
			$http({
				url: "/admin/api/purchase/order/cancel/" + purchaseOrder.id,
				method: "GET",
			})
				.success(function (data, status, headers, config) {
					purchaseOrder.status = data.content.status;
					alert(data.msg);
				})
				.error(function (data, status, headers, config) {
					alert("作废采购单失败");
				});
		}

		$scope.searchForm.checkedItemIds = [];

		$scope.printPurchaseOrdersByIds = function(){
			$window.open("/admin/api/purchase/order/printPurchaseOrders?type=" + $stateParams.type + "&purchaseOrderIds=" + $scope.searchForm.checkedItemIds);
		};

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/list/export?" + str.join("&"));
		}

		$scope.isCheckedAll = false;

		$scope.checkAll = function() {
			if(!($scope.isCheckedAll)){
				angular.forEach($scope.purchaseOrders, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.printMergedPurchaseOrdersByIds = function(){
			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersByIds?purchaseOrderIds=" + $scope.searchForm.checkedItemIds);
		};

		$scope.printMergedPurchaseOrdersByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersByCondition?" + str.join("&"));
		}

		$scope.printMergedPurchaseOrdersResultTogetherByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersResultTogetherByCondition?" + str.join("&"));
		}

		$scope.printMergedPurchaseOrdersTogetherByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersTogetherByCondition?" + str.join("&"));
		}
	});
'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderReturnCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.purchaseTotal = 0;
		$scope.returnTotal = 0;
		$scope.auditForm = {};
		$scope.submitting = false;

		$scope.tableInvalid = function () {

			if ($scope.tableform.$invalid) {
				return true;
			}

			var valid = true;
			var total = 0;
			angular.forEach($scope.returnNoteItems, function(item, key) {
				var remainedQuantity = item.purchaseOrderItem.purchaseQuantity - item.purchaseOrderItem.returnQuantity;
				total = total + item.returnQuantity;
				if (item.returnQuantity > remainedQuantity) {
					valid = false;
				}
			})

			if (total <= 0) {
				valid = false;
			}
			if (!valid) {
				return true;
			} else {
				return false;
			}
		}


		$scope.submit = function () {
		    $scope.submitting = true;
			var submitForm = {};
			submitForm.purchaseOrderId = $stateParams.purchaseOrderId;
			submitForm.depotId = $scope.depotId;
			submitForm.returnNoteItems = $scope.returnNoteItems;
			submitForm.remark = $scope.remark;

			$http({
				url: "/admin/api/purchase/order/returnNote/create",
				method: "POST",
				data: submitForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("保存成功...");
					$scope.submitting = false;
					$state.go("oam.purchase-order-list", {audit:0,type:0,listType:2});
				})
				.error(function (data, status, headers, config) {
					$scope.tableform.$show();
					alert("保存失败...");
					$scope.submitting = false;
				});
		}

		if ($stateParams.id) {
			$http.get("/admin/api/purchase/order/returnNote/" + $stateParams.id)
				.success(function (data, status) {
					$scope.returnNoteItems = data.returnNoteItems;
					angular.forEach($scope.returnNoteItems, function(item, key){
						$scope.purchaseTotal = $scope.purchaseTotal + item.purchaseOrderItem.purchasePrice * item.purchaseOrderItem.purchaseQuantity;
					});

					$scope.purchaseTotal = ($scope.purchaseTotal).toFixed(6);
					$scope.calculateReturnTotal();
					$scope.depotName = data.depot.name;
					$scope.auditForm.opinion = data.opinion;
					$scope.remark = data.remark;
				})
				.error(function (data, status) {
					window.alert("获取退货单信息失败...");
					return;
				});
		} else {
			$http.get("/admin/api/purchase/order/returnNote/tmp/" + $stateParams.purchaseOrderId)
				.success(function (data, status) {
					$scope.returnNoteItems = data.returnNoteItems;
					angular.forEach($scope.returnNoteItems, function(item, key){
						$scope.purchaseTotal = $scope.purchaseTotal + item.purchaseOrderItem.purchasePrice * item.purchaseOrderItem.purchaseQuantity;
					});

					$scope.purchaseTotal = ($scope.purchaseTotal).toFixed(6);
					$scope.calculateReturnTotal();
					$scope.getAvailableDepots(data.depot.city.id);
				})
				.error(function (data, status) {
					window.alert("获取退货单信息失败...");
					return;
				});
		}

		if ($stateParams.edit == 1) {
			$scope.edit = true;
		}

		if ($stateParams.audit == 1) {
			$scope.canAudit = true;
		}

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.auditForm.id = $stateParams.id;
			$scope.auditForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/purchase/order/returnNote/audit",
				method: "POST",
				data: $scope.auditForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("审批完成...");
					$scope.submitting = false;
					$state.go("oam.return-note-list", {audit:1});
				})
				.error(function (data, status, headers, config) {
				    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
					alert(errMsg + "审批失败...");
					$scope.submitting = false;
				});
		};

		$scope.getAvailableDepots = function (cityId) {
			$http.get("/admin/api/depot/list/" + cityId)
				.success(function (data, status, headers, config) {
					$scope.depots = data;
				});
		};

		$scope.modifyReturnPrice = function(returnPrice, item) {
			item.returnPrice = returnPrice;
		};

		$scope.calculateReturnQuantity = function(returnQuantity, item) {
			item.returnQuantity = returnQuantity;
		};

		$scope.calculateReturnTotal = function() {
			$scope.returnTotal = 0;
			angular.forEach($scope.returnNoteItems, function(item, key){
				var REGEX = /^\-?\d+(.\d+)?$/
				if (!item.returnPrice || !REGEX.test(item.returnPrice)) {
					item.returnPrice = 0;
				}

				if (!item.returnQuantity || !REGEX.test(item.returnQuantity)) {
					item.returnQuantity = 0;
				}

				$scope.returnTotal = $scope.returnTotal + item.returnPrice * item.returnQuantity;
			});

			$scope.returnTotal = ($scope.returnTotal).toFixed(6);
		};
	});
'use strict';

angular.module('sbAdminApp')
	.controller('ReturnNoteListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.audit) {
			$scope.searchForm.audit = parseInt($stateParams.audit);
		}

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
		   }
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/purchase/order/returnNote/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.audit) {
			$scope.searchForm.status = 1;
		}

		$http({
			url: "/admin/api/purchase/order/returnNote/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.returnNotes = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.searchForm.checkedItemIds = [];
		$scope.print = function() {
			$window.open("/admin/api/purchase/order/returnNote/print/" + $scope.searchForm.checkedItemIds[0]);
		};

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/returnNote/list/export?" + str.join("&"));
		}

	});
'use strict';

angular.module('sbAdminApp')
    .controller('BlockSalesPerformanceListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http({
            url: '/admin/api/salesPerformance/block/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.salesPerformances = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/salesPerformance/block/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('RestaurantSalesPerformanceListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http({
            url: '/admin/api/salesPerformance/restaurant/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.salesPerformances = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/salesPerformance/restaurant/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('SellerSalesPerformanceListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http({
            url: '/admin/api/salesPerformance/seller/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.salesPerformances = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/salesPerformance/seller/export?" + str.join("&"));
        };

    });
'use strict';
angular.module('sbAdminApp')
    .controller('SellCancelDetailCtrl', function ($scope, $http, $stateParams, $state) {

        $http.get("/admin/api/sellCancel/" + $stateParams.id).success(function (data) {
            $scope.sellCancel = data;
        });
    });


'use strict';

angular.module('sbAdminApp')
    .controller('SellCancelListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                });
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                });
                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.searchForm.organizationId = null;
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.depots = [];
                $scope.searchForm.organizationId = null;
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/sellCancel/type/list").success(function (data) {
            $scope.type = data;
        });

        $http({
            url: '/admin/api/sellCancel/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.sellCancels = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $location.search($scope.searchForm);
        };

        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellCancel/export/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('SellCancelItemListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                });
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                });
                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.searchForm.organizationId = null;
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.depots = [];
                $scope.searchForm.organizationId = null;
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/sellCancel/type/list").success(function (data) {
            $scope.type = data;
        });

        $http({
            url: '/admin/api/sellCancelItem/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.sellCancelItems = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $location.search($scope.searchForm);
        };

        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellCancelItem/export/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
	.controller('SellReturnCheckListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

		$scope.searchForm = {
			type : $stateParams.type,
			status : $stateParams.status,
			pageType:$stateParams.pageType
		};

		if ($scope.searchForm.pageType == 2) {
			$scope.searchForm.type = 2;
		}

		$scope.page = {
			itemsPerPage: 20
		};
		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.openStart = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedStart = true;
		};

		$scope.openEnd = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedEnd = true;
		};

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';
		$scope.date = new Date().toLocaleDateString();

		if($rootScope.user) {
		   var data = $rootScope.user;
			$scope.cities = data.depotCities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
		   }
		});

		$http.get("/admin/api/sellReturn/type/list").success(function (data) {
			$scope.types = data;
		});
		$http.get("/admin/api/sellReturn/status")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search();
		}

		$scope.searchForm.pageSize = $scope.page.itemsPerPage;
		$scope.searchForm.type = $stateParams.type;
		$http({
			url: "/admin/api/sellReturn",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.sellReturns = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.itemsPerPage = data.pageSize;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});


		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellReturn/export/list?" + str.join("&"));
        };
	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderDetailCtrl
 * @description
 * # OrderDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SellReturnDetailCtrl', function ($scope, $http, $stateParams, $state) {
        $http.get("/admin/api/sellReturn/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                console.log(data);
                $scope.sellReturn = data;
                $scope.order = data.order;
            });

        $scope.sellReturnForm = {};

        $scope.refundObj = {
            reasonId : null,
            skuRefundRequests : []
        };

        $scope.audit = function (status) {
            if (status) {
                $scope.sellReturnForm.status = 1;
            } else {
                $scope.sellReturnForm.status = 2;
            }
            $http({
                url: "/admin/api/sellReturn/" + $stateParams.id,
                method: "PUT",
                params: $scope.sellReturnForm,
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("审核成功...");
                $state.go("oam.sellReturn-list");
            })
            .error(function (data, status, headers, config) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }
    });


'use strict';

angular.module('sbAdminApp')
    .controller('SellReturnItemListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {};

        $scope.page = {
            itemsPerPage: 20
        };
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }
        $scope.searchForm.pageSize = $scope.page.itemsPerPage;

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                    if ($scope.organizations && $scope.organizations.length == 1) {
                        $scope.searchForm.organizationId = $scope.organizations[0].id;
                    }
                });
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.searchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.searchForm.organizationId = null;
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.depots = [];
                $scope.searchForm.organizationId = null;
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/sellReturn/type/list").success(function (data) {
            $scope.types = data;
        });
        $http.get("/admin/api/sellReturn/status").success(function (data) {
            $scope.statuses = data;
        });

        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $http({
            url: "/admin/api/sellReturnItem",
            method: "GET",
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.sellReturnItems = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.searchForm);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellReturnItem/export/list?" + str.join("&"));
        };
    });
    'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StopOrdersCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window, $state) {

        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.orders = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            start: $stateParams.start,
            end: $stateParams.end,
            customerId: $stateParams.customerId,
            restaurantId: $stateParams.restaurantId,
            restaurantName: $stateParams.restaurantName,
            warehouseId: $stateParams.warehouseId,
            vendorName:$stateParams.vendorName,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            vendorId: $stateParams.vendorId,
            orderId:$stateParams.orderId,
            coordinateLabeled:$stateParams.coordinateLabeled,
            refundsIsNotEmpty:$stateParams.refundsIsNotEmpty,
            depotId:$stateParams.depotId,
            blockId:$stateParams.blockId,
            orderType:$stateParams.orderType,
            status:3,
            type:4 //地图类型
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderListSearchForm.cityId = $scope.cities[0].id;
            }
       }

        $scope.submitting = false;

        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };


        $scope.format = 'yyyy-MM-dd HH:mm';
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.date = new Date().toLocaleDateString();

        $scope.page = {
            itemsPerPage: 100
        };

            /*订单状态*/
      $http.get("/admin/api/order/status")
           .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
           }).error(function (data, status) {
                alert("订单状态加载失败！");
       });

       $http.get("/admin/api/order/orderType/get")
          .success(function (data, status, headers, config) {
               $scope.orderTypes = data;
          }).error(function (data, status) {
               alert("订单状态加载失败！");
      });

        $scope.$watch('orderListSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.orderListSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.orderListSearchForm.depotId = null;
                }
            }else{
                $scope.depots = [];
                $scope.orderListSearchForm.depotId = null;
            }
        });

        $scope.$watch('orderListSearchForm.depotId', function(newVal, oldVal) {
            if(newVal){
                $http.get("/admin/api/warehouse/depot/" + newVal).success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderListSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.warehouseId = null;
               }
            }else {
                $scope.availableWarehouses = [];
                $scope.orderListSearchForm.warehouseId = null;
            }
        });

        $scope.$watch('orderListSearchForm.warehouseId', function(newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/block/warehouse/" + newVal).success(function (data) {
                   $scope.blocks = data;
                   if ($scope.blocks && $scope.blocks.length == 1) {
                       $scope.orderListSearchForm.blockId = $scope.blocks[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.blockId = null;
               }
            } else {
                $scope.blocks = [];
                $scope.orderListSearchForm.blockId = null;
            }
        });

        if($stateParams.sortField) {
            $scope.orderListSearchForm.sortField = $stateParams.sortField;
        } else {
            $scope.orderListSearchForm.sortField = "id";
        }

        if($stateParams.asc) {
            if ($stateParams.asc == 'true') {
                $scope.orderListSearchForm.asc = true;
            }
            if ($stateParams.asc == 'false') {
                $scope.orderListSearchForm.asc = false;
            }
        }

        if($scope.orderListSearchForm.start) {
            $scope.startDate = Date.parse($scope.orderListSearchForm.start);
        }

        if($scope.orderListSearchForm.end) {
            $scope.endDate = Date.parse($scope.orderListSearchForm.end);
        }

        if($stateParams.status) {
            $scope.orderListSearchForm.status = parseInt($stateParams.status);
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        if($stateParams.warehouseId) {
            $scope.orderListSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if($stateParams.cityId) {
            $scope.orderListSearchForm.cityId = parseInt($stateParams.cityId);
         }

        if($stateParams.organizationId){
            $scope.orderListSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.orderId){
            $scope.orderListSearchForm.orderId = parseInt($stateParams.orderId);
        }

        if($stateParams.coordinateLabeled){
            $scope.orderListSearchForm.coordinateLabeled = parseInt($stateParams.coordinateLabeled);
        }
        if($stateParams.refundsIsNotEmpty){
            $scope.orderListSearchForm.refundsIsNotEmpty = true;
        }
        if($stateParams.depotId){
            $scope.orderListSearchForm.depotId = parseInt($stateParams.depotId);
        }
        if($stateParams.blockId){
            $scope.orderListSearchForm.blockId = parseInt($stateParams.blockId);
        }
        if($stateParams.orderType){
            $scope.orderListSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.$watch('startDate', function(d) {
           $scope.orderListSearchForm.start = $filter('date')(d, 'yyyy-MM-dd HH:mm');
        });

        $scope.$watch('endDate', function(d) {
            $scope.orderListSearchForm.end= $filter('date')(d, 'yyyy-MM-dd HH:mm');
        });

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;
//            $scope.searchOrderList();
            $state.go($state.current, $scope.orderListSearchForm, {reload: true});
        }


        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrderList();
        }

        $scope.searchOrderList = function () {
           $location.search($scope.orderListSearchForm);

        }
        if ($scope.orderListSearchForm.depotId) {
            $http({
               url: '/admin/api/order',
               method: "GET",
               params: $scope.orderListSearchForm
            }).success(function (data, status, headers, config) {
               $scope.orders = data.orders;
               $scope.count = data.total;
               $scope.orderStatistics = data.orderStatistics;

               /*分页数据*/
               $scope.page.itemsPerPage = data.pageSize;
               $scope.page.totalItems = data.total;
               $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
               window.alert("搜索失败...");
            });
        }

        $scope.sort = function(field) {
            if(field && field == $scope.orderListSearchForm.sortField) {
                $scope.orderListSearchForm.asc = !$scope.orderListSearchForm.asc;
            } else {
                $scope.orderListSearchForm.sortField = field;
                $scope.orderListSearchForm.asc = false;
            }

            $scope.orderListSearchForm.page = 0;

            $location.search($scope.orderListSearchForm);
        }

        $scope.stopOrder = function () {
            $scope.submitting = true;
            $http({
               url: '/admin/api/order/stop',
               method: "GET",
               params: $scope.orderListSearchForm
            }).success(function (data, status, headers, config) {
               alert("截单成功！");
               $scope.submitting = false;
               $scope.resetPageAndSearchOrderList();
            }).error(function (data, status, headers, config) {
               window.alert("截单失败..." + data.errmsg);
               $scope.submitting = false;
            });
        }

        $scope.stopOrderMap = function(){
            var orderListSearchMap = $scope.orderListSearchForm;
            orderListSearchMap.type = 4;
            $state.go("stop-order-map", orderListSearchMap);
        }
});

'use strict';

angular.module('sbAdminApp')
	.controller('TransferAddCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.searchForm = {};

		$scope.candidateSkus = [];

		$scope.funcAsync = function (name) {
			if (name && name !== "") {
				$scope.candidateSkus = [];
				$http.get("/admin/api/sku/candidates?name="+name).then(
					function (data) {
						$scope.candidateSkus = data.data;
					}
				)
			}
		}

		$scope.resetCandidateSkus = function () {
			$scope.candidateSkus = [];
		}

        $scope.submitting = false;
		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
					});
			} else {
				$scope.depots = [];
			}
		});

		$scope.submit = function () {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.items = $scope.items;
			$http({
				url: "/admin/api/transfer/add",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("保存成功...");
				$scope.submitting = false;
				$state.go("oam.transfer-list", {audit:0});
			})
			.error(function (data, status, headers, config) {
				alert("保存失败...");
				$scope.submitting = false;
				$scope.tableform.$show();
			});
		}

		$scope.items = [
		];

		$scope.remove = function(index) {
			$scope.items.splice(index, 1);
		};

		$scope.addItem = function() {
			$scope.inserted = {
			};
			$scope.items.push($scope.inserted);
		};

		$scope.searchSku = function(item) {
			$scope.candidateSkus = [];
			$http.get("/admin/api/transfer/sku/" + $scope.searchForm.cityId + "/" + item.skuId + "?depotIds=" + $scope.searchForm.sourceDepotId + "," + $scope.searchForm.targetDepotId)
				.success(function (data, status, headers, config) {
					$scope.candidateSkus.push(data.sku);
					item.name = data.sku.name;
					item.sourceDepotStock = data.stocks[0];
					item.targetDepotStock = data.stocks[1];
					item.singleUnit = data.sku.singleUnit;
				});
		};

		if ($stateParams.id) {
			$http.get("/admin/api/transfer/" + $stateParams.id)
				.success(function (data, status) {
					$scope.searchForm.cityId = data.cityId;
					$scope.searchForm.sourceDepotId = data.sourceDepot.id;
					$scope.searchForm.targetDepotId = data.targetDepot.id;
					$scope.searchForm.remark = data.remark;
					$scope.searchForm.opinion = data.opinion;
					$scope.items = data.items;
					angular.forEach($scope.items, function(item, key) {
						$scope.candidateSkus.push(item.sku);
					});
				})
				.error(function (data, status) {
					window.alert("获取采购单信息失败...");
					return;
				});
		}

		if ($stateParams.add == 1) {
			$scope.add = true;
			$scope.edit = true;
		}

		if ($stateParams.edit == 1) {
			$scope.edit = true;
		}

		if ($stateParams.audit == 1) {
			$scope.canAudit = true;
		}

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/transfer/audit",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("审批完成...");
				$scope.submitting = false;
				$state.go("oam.transfer-list", {audit:1});
			})
			.error(function (data, status, headers, config) {
				var errMsg = '';
				if (data != null && data.errmsg != null) {
					errMsg = data.errmsg + ',';
				}
				alert(errMsg + "审批失败...");
				$scope.submitting = false;
			});
		}

		$scope.clearItems = function () {
			$scope.items = [];
		};
	});
'use strict';

angular.module('sbAdminApp')
	.controller('TransferListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location) {

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		$scope.searchForm = {};

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		$scope.openStart = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedStart = true;
		};

		$scope.openEnd = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedEnd = true;
		};

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
					});
			} else {
				$scope.depots = [];
			}
		});

		$http.get("/admin/api/transfer/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.searchForm.pageSize = $scope.page.itemsPerPage;
			$scope.search();
		}

		$scope.search = function () {
			$scope.searchForm.pageSize = $scope.page.itemsPerPage;
			$scope.searchForm.type = $stateParams.type;
			if ($scope.audit) {
				$scope.searchForm.status = 2;
			}
			$http({
				url: "/admin/api/transfer/list",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.transfers = data.content;
				$scope.page.totalItems = data.total;
				$scope.page.currentPage = data.page + 1;
			})
			.error(function (data, status, headers, config) {
				alert("加载失败...");
			});
		}

		$scope.submitTransfer = function (transfer) {
			$http({
				url: "/admin/api/transfer/submit/" + transfer.id,
				method: "GET",
			})
			.success(function (data, status, headers, config) {
				transfer.status.name = '待审核';
				transfer.showViewButton = true;
				transfer.hideEditButton = true;
				transfer.hideSubmitButton = true;
				alert("提交审核成功...");
			})
			.error(function (data, status, headers, config) {
				var errMsg = '';
				if (data != null && data.errmsg != null) {
					errMsg = data.errmsg + ',';
				}
				alert(errMsg + "提交审核失败...");
			});
		}

		$scope.search();
	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateDepotCtrl
 * @description
 * # AddOrUpdateDepotCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateDepotCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        /*仓库*/
        $scope.depot = {isMain : false};
        $scope.isEditDepot = false;

        /*根据id获取仓库信息*/
        if ($stateParams.id) {
            $scope.isEditDepot = true;

            $http.get("/admin/api/depot/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.depot = data;
                    $scope.depot.cityId = data.city.id;
                })
                .error(function (data, status) {
                    window.alert("获取仓库信息失败...");
                });
        }

        /*添加/编辑仓库*/
        $scope.createDepot = function () {
            if (($scope.depot.longitude != null && $scope.depot.longitude != '' && ($scope.depot.latitude == null || $scope.depot.latitude == ''))
                || ($scope.depot.latitude != null && $scope.depot.latitude != '' && ($scope.depot.longitude == null || $scope.depot.longitude == ''))) {
                window.alert("经度和纬度必须同时有值");
                return;
            }
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/depot/' + $stateParams.id,
                    data: $scope.depot,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/depot',
                    data: $scope.depot,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                })
            }
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListDepotCtrl
 * @description
 * # ListDepotCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('ListDepotCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.depots = {};
	    $scope.formData = {
            cityId : $stateParams.cityId
        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

		$scope.searchForm = function() {
			$http({
				url : "/admin/api/depot/list",
				method : 'GET',
				params: $scope.formData,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			}).success(function(data){
				$scope.depots = data;
			}).error(function(data){
                window.alert("加载失败...");
			});
		}

		$scope.updateDepot = function (depot, isMain) {
			$http({
				url : "/admin/api/depot/main/" + depot.id,
				method : 'PUT',
			}).success(function(data){
				$scope.depots = data;
			}).error(function(data){
				window.alert("加载失败...");
			});
		}

		$scope.searchForm();
	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddBatchShelfCtrl
 * @description
 * # AddBatchShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddBatchShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams) {

        $scope.shelf = {};
        $scope.shelfAreas = [];
	    $scope.shelfRows = [];
	    $scope.shelfNumbers = [];
	    $scope.submitting = false;
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.shelf.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('shelf.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.shelf.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.shelf.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.shelf.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.shelf.depotId = null;
            }
        });

        $scope.initAreaRowNum = function () {
            for (var i = 1; i < 100; i++) {
                var obj = new Object();
                obj.code = (i < 10 ? '0'+i : i+'');
                $scope.shelfAreas.push(obj);
                $scope.shelfRows.push(obj);
                $scope.shelfNumbers.push(obj);
            }
        };
        $scope.initAreaRowNum();

        $scope.$watch('shelf.area', function (newVal, oldVal) {
            if (newVal == null && oldVal != null) {
                $scope.shelf.row = null;
                $scope.shelf.number = null;
            }
        });
        $scope.$watch('shelf.row', function (newVal, oldVal) {
            if ($scope.shelf.area == null && newVal != null) {
                $scope.shelf.row = null;
            } else if (newVal == null && oldVal != null) {
                $scope.shelf.number = null;
            }
        });
        $scope.$watch('shelf.number', function (newVal, oldVal) {
            if (($scope.shelf.area == null || $scope.shelf.row == null) && newVal != null) {
                $scope.shelf.number = null;
            }
        });

        /*添加货位 */
        $scope.createBatchShelf = function () {
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/batchShelf',
                data: $scope.shelf,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("添加成功!");
                $scope.submitting = false;
                $state.go($state.current, null, {reload: true});
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "添加失败!");
                $scope.submitting = false;
            });
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateShelfCtrl
 * @description
 * # AddOrUpdateShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateShelfCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.shelf = {};
        $scope.shelfAreas = [];
	    $scope.shelfRows = [];
	    $scope.shelfNumbers = [];
	    $scope.submitting = false;
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.shelf.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('shelf.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.shelf.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.shelf.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.shelf.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.shelf.depotId = null;
            }
        });

        $scope.initAreaRowNum = function () {
            for (var i = 1; i < 100; i++) {
                var obj = new Object();
                obj.code = (i < 10 ? '0'+i : i+'');
                $scope.shelfAreas.push(obj);
                $scope.shelfRows.push(obj);
                $scope.shelfNumbers.push(obj);
            }
        };
        $scope.initAreaRowNum();

        $scope.generalCodeName = function () {
            $scope.shelf.shelfCode = '';
            $scope.shelf.name = '';
            if ($scope.shelf.area != null) {
                $scope.shelf.shelfCode += $scope.shelf.area;
                $scope.shelf.name += $scope.shelf.area + '区';
            }
            if ($scope.shelf.row != null) {
                $scope.shelf.shelfCode += $scope.shelf.row;
                $scope.shelf.name += $scope.shelf.row + '排';
            }
            if ($scope.shelf.number != null) {
                $scope.shelf.shelfCode += $scope.shelf.number;
                $scope.shelf.name += $scope.shelf.number + '号';
            }
        };

        $scope.$watch('shelf.area', function (newVal, oldVal) {
            if (newVal == null && oldVal != null) {
                $scope.shelf.row = null;
                $scope.shelf.number = null;
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });
        $scope.$watch('shelf.row', function (newVal, oldVal) {
            if ($scope.shelf.area == null && newVal != null) {
                $scope.shelf.row = null;
                return;
            }
            if (newVal == null && oldVal != null) {
                $scope.shelf.number = null;
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });
        $scope.$watch('shelf.number', function (newVal, oldVal) {
            if (($scope.shelf.area == null || $scope.shelf.row == null) && newVal != null) {
                $scope.shelf.number = null;
                return;
            }
            if (newVal == null && oldVal != null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });

        $scope.isEdit = false;

        /*根据id获取货位信息*/
        if ($stateParams.id != null && $stateParams.id != '') {
            $scope.isEdit = true;
            $http.get("/admin/api/shelf/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.shelf = data;
                    $scope.shelf.cityId = data.depot.city.id;
                    $scope.shelf.depotId = data.depot.id;
                });
        }

        /*添加/编辑货位 */
        $scope.createShelf = function () {

            $scope.submitting = true;
            if ($scope.isEdit) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/shelf/' + $stateParams.id,
                    data: $scope.shelf,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                    $scope.submitting = false;
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/shelf',
                    data: $scope.shelf,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                    $scope.submitting = false;
                })
            }
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ShelfListCtrl
 * @description
 * # ShelfListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('ShelfListCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.shelfs = {};
	    $scope.formData = {};
	    $scope.page = {itemsPerPage : 100};
	    $scope.shelfAreas = [];
	    $scope.shelfRows = [];
	    $scope.shelfNumbers = [];
	    $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.formData.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.initAreaRowNum = function () {
            for (var i = 1; i < 100; i++) {
                var obj = new Object();
                obj.code = (i < 10 ? '0'+i : i+'');
                $scope.shelfAreas.push(obj);
                $scope.shelfRows.push(obj);
                $scope.shelfNumbers.push(obj);
            }
        };
        $scope.initAreaRowNum();

        $scope.isCheckedAll = false;
        $scope.formData.shelfIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.shelfIds = [];
                angular.forEach($scope.shelfs, function(value, key){
                    $scope.formData.shelfIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.shelfIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.shelfs = [];
            $scope.formData.shelfIds = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/shelf/list',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.shelfs = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.batchDelete = function () {
            if ($scope.formData.shelfIds.length == 0) {
                alert("请选择货位");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/shelf/del",
                method: "DELETE",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("删除成功...");
                $scope.submitting = false;
                $scope.searchForm();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "删除失败...");
                $scope.submitting = false;
            });
        };

	});
'use strict';

angular.module('sbAdminApp')
    .controller('StockAdjustListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.stockSearchForm = {
            status: 0
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockShelf/list',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockAdjustQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.searchForm = {};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.formData = {};
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.searchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/stockAdjust/status/list")
        .success(function (data) {
            $scope.status = data;
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.search = function (page) {
            $scope.stockAdjusts = [];
            $scope.searchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockAdjust/query',
                method: "GET",
                params: $scope.searchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stockAdjusts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.searchForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/stockAdjust/export/list?" + str.join("&"));
        };

        $scope.cancel = function (id) {
            $scope.formData.adjustIds = [];
            $scope.formData.adjustIds.push(id);
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/cancel",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("作废成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "作废失败...");
                $scope.submitting = false;
            });
        };
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockAdjustSurplusCtrl
 * @description
 * # StockAdjustSurplusCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockAdjustSurplusCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

        $scope.adjust = {};
	    $scope.submitting = false;
	    $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.skuSearchForm = {
            pageSize: 20,
            showLoader: false
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.adjust.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('adjust.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.adjust.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.adjust.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.adjust.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.adjust.depotId = null;
            }
        });

        $scope.candidateSkus = [];

        $scope.funcAsyncSkus = function (name) {
            if (name && name !== "") {
                $scope.candidateSkus = [];
                $http({
                    url:"/admin/api/stockAdjust/defaultOrganization",
                    method:'GET',
                    params:{showLoader:false}
                }).success(function (organization) {
                    $scope.skuSearchForm.organizationId = organization == null ? null : organization.id;
                    $scope.skuSearchForm.name = name;
                    $http({
                        url:"/admin/api/sku/candidates",
                        method:'GET',
                        params:$scope.skuSearchForm
                    }).success(function (data) {
                        $scope.candidateSkus = data;
                    });
                });
            }
        }

        $scope.resetCandidateSkus = function () {
            $scope.candidateSkus = [];
        }

        $scope.searchSku = function(adjust) {
			$scope.candidateSkus = [];
			if (adjust.skuId == null) {
			    return;
			}
			$http({
				url:"/admin/api/stockAdjust/sku/" + adjust.skuId,
				method:'GET',
			}).success(function (data, status, headers, config) {
			    if (data != null && data.id != null) {
			        $scope.candidateSkus.push(data);
			        adjust.skuId = data.id;
			    } else {
			        alert('sku不存在');
                    adjust.skuId = null;
			    }
			});
		};

        $scope.createAdjust = function () {

            if (!angular.isNumber($scope.adjust.avgCost)) {
                alert('请输入有效的平均成本');
                return;
            }
            if (!angular.isNumber($scope.adjust.adjustQuantity)) {
                alert('请输入有效的调整后数量');
                return;
            }
            if ($scope.adjust.adjustQuantity <= 0) {
                alert('调整后数量应该大于0');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stockAdjust/createAdjust',
                data: $scope.adjust,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('商品盘盈单创建成功...')
                $scope.submitting = false;
                $state.go("oam.stock-adjust-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "商品盘盈单创建失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockAdjustCtrl
 * @description
 * # StockAdjustCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockAdjustCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-adjust-list");
            return;
        }
        $scope.adjust = $stateParams.stock;
        $scope.adjust.productionDateStr = null;
        if($scope.adjust.productionDate != null){
            $scope.adjust.productionDateStr = $filter('date')($scope.adjust.productionDate, 'yyyy-MM-dd');
        }

        $scope.adjustForm = {
            stockId: $scope.adjust.id,
            quantity: $scope.adjust.quantity
        };

        $scope.submitting = false;
        $scope.adjustStock = function () {

            if (!angular.isNumber($scope.adjust.adjustQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.adjust.adjustQuantity < 0) {
                alert('数量应该大于等于0');
                return;
            }
            if ($scope.adjust.adjustQuantity - $scope.adjust.quantity == 0) {
                alert('原数量和调整后数量相等,没必要做调整');
                return;
            }

            $scope.adjustForm.adjustQuantity = $scope.adjust.adjustQuantity;
            $scope.adjustForm.comment = $scope.adjust.comment;

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stockAdjust/adjust',
                data: $scope.adjustForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('调整单创建成功...')
                $scope.submitting = false;
                $state.go("oam.stock-adjust-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "调整单创建失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockAdjustConfirmListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.searchForm = {
            status: 0
        };
        $scope.formData = {};
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.searchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.isCheckedAll = false;
        $scope.formData.adjustIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.adjustIds = [];
                angular.forEach($scope.stockAdjusts, function(value, key){
                    $scope.formData.adjustIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.adjustIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.search = function (page) {
            $scope.stockAdjusts = [];
            $scope.formData.adjustIds = [];
            $scope.searchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockAdjust/query',
                method: "GET",
                params: $scope.searchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stockAdjusts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.searchForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.batchConfirm = function () {
            if ($scope.formData.adjustIds.length == 0) {
                alert("请选择调整单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/confirm",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("审核成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "审核失败...");
                $scope.submitting = false;
            });
        };

        $scope.batchReject = function () {
            if ($scope.formData.adjustIds.length == 0) {
                alert("请选择调整单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/reject",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("审核成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "审核失败...");
                $scope.submitting = false;
            });
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('AvgCostCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.avgCostForm = {};
        $scope.page = {};

        if ($stateParams.page) {
            $scope.avgCostForm.page = parseInt($stateParams.page);
        }

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.avgCostForm.cityId = $scope.cities[0].id;
            }
        }

        $http({
            url: '/admin/api/stock/avgcost/list',
            method: "GET",
            params: $scope.avgCostForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.avgCosts = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.page.itemsPerPage = data.pageSize;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.avgCostForm);
        };
        $scope.pageChanged = function () {
            $scope.avgCostForm.page = $scope.page.currentPage - 1;
            $location.search($scope.avgCostForm);
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchDateCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-production-date-list");
            return;
        }
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.stocks = $stateParams.stocks;
        $scope.submitting = false;

        $scope.batchInputDate = function () {

            $scope.stockForm = {
                stockProductionDateDatas: []
            };
            angular.forEach($scope.stocks, function(value, key){
                if (value.productionDate != null) {
                    var obj = new Object();
                    obj.id = value.id;
                    obj.quantity = value.quantity;
                    var productionObj = new Object();
                    productionObj.productionDate = value.productionDate;
                    productionObj.quantity = value.quantity;
                    obj.stockProductionDates = [];
                    obj.stockProductionDates.push(productionObj);
                    $scope.stockForm.stockProductionDateDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockProductionDateDatas.length == 0) {
                alert('请输入生产日期再提交');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchProductionDate',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量录入生产日期成功...')
                $scope.submitting = false;
                $state.go("oam.stock-production-date-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量录入生产日期失败...");
                $scope.submitting = false;
            });
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchMoveShelfCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-moveshelf-list");
            return;
        }
        $scope.stocks = $stateParams.stocks;
        $scope.submitting = false;

        $scope.codeKeyUp = function(e, index){
           var keyCodeValue = (window.event?window.event.keyCode:e.which);
           if (keyCodeValue == 13) {
              if (index >= $scope.stocks.length-1) {
                angular.element('button[type=submit]:first').focus();
              } else {
                angular.element('#shelfCode_'+(index+1)).focus();
              }
           }
        };

        $scope.codeBlur = function (stock) {
            if(stock.targetShelfCode != null && stock.targetShelfCode != "") {
                $scope.shelfForm = {
                    depotId: stock.depotId,
                    shelfCode: stock.targetShelfCode
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        stock.targetShelfName = data.name;
                        stock.shelfId = data.id;
                    } else{
                        stock.targetShelfName = null;
                        stock.shelfId = null;
                        stock.targetShelfCode = null;
                    }
                });
            } else {
                stock.targetShelfName = null;
                stock.shelfId = null;
                stock.targetShelfCode = null;
            }
        };

        $scope.batchMoveShelf = function () {

            $scope.stockForm = {
                stockShelfDatas: []
            };

            var pass = true;
            angular.forEach($scope.stocks, function(value, key){

                if (pass && value.shelfId != null) {
                    if (value.shelfCode == value.targetShelfCode) {
                         alert('第' + (key+1) + '行源货位应该和目标货位不一致');
                         pass = false;
                    }
                    var obj = new Object();
                    obj.id = value.id;
                    obj.quantity = value.quantity;
                    obj.moveQuantity = value.quantity;
                    obj.shelfId = value.shelfId;
                    obj.shelfCode = value.targetShelfCode;
                    $scope.stockForm.stockShelfDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockShelfDatas.length == 0) {
                alert('请输入目标货位再提交');
                return;
            }
            if (!pass) {
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchMoveShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量移位成功...')
                $scope.submitting = false;
                $state.go("oam.stock-moveshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量移位失败...");
                $scope.submitting = false;
            });
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchOnShelfCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-willshelf-list");
            return;
        }
        $scope.stocks = $stateParams.stocks;
        $scope.submitting = false;

        $scope.codeKeyUp = function(e, index){
           var keyCodeValue = (window.event?window.event.keyCode:e.which);
           if (keyCodeValue == 13) {
              if (index >= $scope.stocks.length-1) {
                angular.element('button[type=submit]:first').focus();
              } else {
                angular.element('#shelfCode_'+(index+1)).focus();
              }
           }
        };

        $scope.codeBlur = function (stock) {
            if(stock.shelfCode != null && stock.shelfCode != "") {
                $scope.shelfForm = {
                    depotId: stock.depotId,
                    shelfCode: stock.shelfCode
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        stock.shelfName = data.name;
                        stock.shelfId = data.id;
                    } else{
                        stock.shelfName = null;
                        stock.shelfId = null;
                        stock.shelfCode = null;
                    }
                });
            } else {
                stock.shelfName = null;
                stock.shelfId = null;
                stock.shelfCode = null;
            }
        };

        $scope.batchOnShelf = function () {

            $scope.stockForm = {
                stockShelfDatas: []
            };
            angular.forEach($scope.stocks, function(value, key){
                if (value.shelfId != null) {
                    var obj = new Object();
                    obj.depotId = value.depotId;
                    obj.skuId = value.skuId;
                    obj.availableQuantity = value.availableQuantity;
                    obj.expirationDate = value.expirationDate;
                    var shelfObj = new Object();
                    shelfObj.shelfId = value.shelfId;
                    shelfObj.shelfCode = value.shelfCode;
                    shelfObj.quantity = value.availableQuantity;
                    obj.stockShelfs = [];
                    obj.stockShelfs.push(shelfObj);
                    $scope.stockForm.stockShelfDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockShelfDatas.length == 0) {
                alert('请输入货位码再提交');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量上架成功...')
                $scope.submitting = false;
                $state.go("oam.stock-willshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量上架失败...");
                $scope.submitting = false;
            });
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockDepotListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {};
        $scope.stockSearchForm = {};

        if ($stateParams.page) {
            $scope.stockSearchForm.page = parseInt($stateParams.page);
        }

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $http({
            url: '/admin/api/stock/depot/list',
            method: "GET",
            params: $scope.stockSearchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data, status, headers, config) {
            $scope.stocks = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status) {
            window.alert("加载失败...");
        });
        $scope.search = function () {
            $location.search($scope.stockSearchForm);
        };
        $scope.pageChanged = function () {
            $scope.stockSearchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockSearchForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockSearchForm) {
                if ($scope.stockSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockSearchForm[p]));
                }
            }
            $window.open("/admin/api/stockDepot/export/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockDullSaleListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.stockSearchForm = {
            status: 0
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            if ($scope.stockSearchForm.dullSaleDays == null) {
                $scope.stockSearchForm.dullSaleDays = 30;
            }
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stock/dullSaleList',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {

            var str = [];
            for (var p in $scope.stockSearchForm) {
                if ($scope.stockSearchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockSearchForm[p]));
                }
            }
            $window.open("/admin/api/stock/exportDullSale/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockExpirationListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.stockSearchForm = {
            status: 0
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            if ($scope.stockSearchForm.expireDays == null) {
                $scope.stockSearchForm.expireDays = 30;
            }
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stock/expirationList',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockSearchForm) {
                if ($scope.stockSearchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockSearchForm[p]));
                }
            }
            $window.open("/admin/api/stock/exportExpiration/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockMoveShelfListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $window) {

        $scope.page = {itemsPerPage : 30};
        $scope.stockSearchForm = {
            shelfIsNull: false,
            sortField: "shelfCode",
            asc:true
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.isCheckedAll = false;
        $scope.stockSearchForm.selectStocks = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.stockSearchForm.selectStocks = [];
                angular.forEach($scope.stocks, function(value, key){
                    $scope.stockSearchForm.selectStocks.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.stockSearchForm.selectStocks = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.selectStocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockShelf/list',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }
        $scope.sort = function(field) {
            if(field && field == $scope.stockSearchForm.sortField) {
                $scope.stockSearchForm.asc = !$scope.stockSearchForm.asc;
            } else {
                $scope.stockSearchForm.sortField = field;
                $scope.stockSearchForm.asc = true;
            }
            $scope.searchForm();
        }

        $scope.batchMoveShelf = function() {

            if ($scope.stockSearchForm.selectStocks.length == 0) {
                alert('请选择要移位的商品');
                return;
            }

            $state.go('oam.batch-moveShelf', {
                stocks: $scope.stockSearchForm.selectStocks
            });
        };
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockOnShelfCtrl
 * @description
 * # StockOnShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockMoveShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-moveshelf-list");
            return;
        }
        $scope.stock = $stateParams.stock;
        $scope.stock.productionDateStr = null;
        if($scope.stock.productionDate != null){
            $scope.stock.productionDateStr = $filter('date')($scope.stock.productionDate, 'yyyy-MM-dd');
        }
        $scope.stock.moveQuantity = $scope.stock.quantity;

        $scope.stockForm = {
            id: $scope.stock.id,
            quantity: $scope.stock.quantity
        };
        $scope.submitting = false;

        $scope.codeKeyUp = function(e){
            var keycode = window.event?e.keyCode:e.which;
            if(keycode == 13){
                $scope.searchShelfName($scope.stock.targetShelfCode);
            }
        };

        $scope.searchShelfName = function (code) {
            if(code != null && code != "") {
                $scope.shelfForm = {
                    depotId: $scope.stock.depotId,
                    shelfCode: code
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        $scope.stock.targetShelfName = data.name;
                        $scope.stock.shelfId = data.id;
                    } else{
                        $scope.stock.targetShelfName = null;
                        $scope.stock.shelfId = null;
                    }
                });
            }
        };

        $scope.moveShelf = function () {

            if (!angular.isNumber($scope.stock.moveQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.moveQuantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.moveQuantity > $scope.stock.quantity) {
                alert('数量不能大于源数量');
                return;
            }

            var code = "";
            if ($scope.stock.targetShelfName.length >= 2) {
                code += $scope.stock.targetShelfName.substring(0,2);
            }
            if ($scope.stock.targetShelfName.length >= 5) {
                code += $scope.stock.targetShelfName.substring(3,5);
            }
            if ($scope.stock.targetShelfName.length >= 8) {
                code += $scope.stock.targetShelfName.substring(6,8);
            }

            if (code == $scope.stock.shelfCode) {
                alert('源货位应该和目标货位不一致');
                return;
            }
            $scope.stockForm.shelfId = $scope.stock.shelfId;
            $scope.stockForm.shelfCode = code;
            $scope.stockForm.moveQuantity = $scope.stock.moveQuantity;

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/moveShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('移位成功...')
                $scope.submitting = false;
                $state.go("oam.stock-moveshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "移位失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockOnShelfCtrl
 * @description
 * # StockOnShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockOnShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-willshelf-list");
            return;
        }
        $scope.stock = $stateParams.stock;
        $scope.stock.productionDateStr = null;
        if($scope.stock.productionDate != null){
            $scope.stock.productionDateStr = $filter('date')($scope.stock.productionDate, 'yyyy-MM-dd');
        }
        $scope.stock.quantity = $scope.stock.availableQuantity;

        $scope.stockForm = {

            depotId: $scope.stock.depotId,
            skuId: $scope.stock.skuId,
            availableQuantity: $scope.stock.availableQuantity,
            expirationDate: $scope.stock.expirationDate,
            stockShelfs: []
        };
        $scope.submitting = false;

        $scope.codeKeyUp = function(e){
            var keycode = window.event?e.keyCode:e.which;
            if(keycode == 13){
                $scope.searchShelfName($scope.stock.shelfCode);
            }
        };

        $scope.searchShelfName = function (code) {
            if(code != null && code != "") {
                $scope.shelfForm = {
                    depotId: $scope.stock.depotId,
                    shelfCode: code
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        $scope.stock.shelfName = data.name;
                        $scope.stock.shelfId = data.id;
                    } else{
                        $scope.stock.shelfName = null;
                        $scope.stock.shelfId = null;
                    }
                });
            }
        };

        $scope.add = function(shelfId, shelfCode, quantity) {
            $scope.inserted = {
                shelfId:shelfId,
                shelfCode:shelfCode,
                quantity:quantity
            };
            $scope.stockForm.stockShelfs.push($scope.inserted);
        };

        $scope.addShelf = function () {
            if (!angular.isNumber($scope.stock.quantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.quantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.quantity > $scope.stock.availableQuantity) {
                alert('数量不能大于待上架数量');
                return;
            }

            $scope.stock.availableQuantity -= $scope.stock.quantity;
            var code = "";
            if ($scope.stock.shelfName.length >= 2) {
                code += $scope.stock.shelfName.substring(0,2);
            }
            if ($scope.stock.shelfName.length >= 5) {
                code += $scope.stock.shelfName.substring(3,5);
            }
            if ($scope.stock.shelfName.length >= 8) {
                code += $scope.stock.shelfName.substring(6,8);
            }

            $scope.add($scope.stock.shelfId, code, $scope.stock.quantity);
            $scope.stock.quantity = $scope.stock.availableQuantity;
            $scope.stock.shelfId = null;
            $scope.stock.shelfCode = null;
            $scope.stock.shelfName = null;
        };

        $scope.onShelf = function () {

            if ($scope.stockForm.stockShelfs.length == 0) {
                alert('请先输入数量上架再提交');
                return;
            }
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/onShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('上架成功...')
                $scope.submitting = false;
                $state.go("oam.stock-willshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "上架失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockProductionDateListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window, $state) {

        $scope.page = {itemsPerPage : 30};
        $scope.stockSearchForm = {
            status: 0,
            productionDate: "noDate",
            sortField: "skuId",
            asc:true
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });
        $scope.isCheckedAll = false;
        $scope.stockSearchForm.selectStocks = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.stockSearchForm.selectStocks = [];
                angular.forEach($scope.stocks, function(value, key){
                    $scope.stockSearchForm.selectStocks.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.stockSearchForm.selectStocks = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.selectStocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockShelf/list',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.batchProductionDate = function() {

            if ($scope.stockSearchForm.selectStocks.length == 0) {
                alert('请选择要录入生产日期的商品');
                return;
            }

            $state.go('oam.batch-production-date', {
                stocks: $scope.stockSearchForm.selectStocks
            });
        };
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockProductionDateCtrl
 * @description
 * # StockProductionDateCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockProductionDateCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-production-date-list");
            return;
        }
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.stock = $stateParams.stock;
        $scope.stock.moveQuantity = $scope.stock.quantity;

        $scope.stockForm = {
            id: $scope.stock.id,
            quantity: $scope.stock.quantity,
            stockProductionDates: []
        };
        $scope.submitting = false;

        $scope.add = function(productionDate, quantity) {
            $scope.inserted = {
                productionDate:productionDate,
                quantity:quantity
            };
            $scope.stockForm.stockProductionDates.push($scope.inserted);
        };

        $scope.addProductionDate = function () {
            if (!angular.isNumber($scope.stock.moveQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.moveQuantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.moveQuantity > $scope.stock.quantity) {
                alert('数量不能大于待录入数量');
                return;
            }

            $scope.stock.quantity -= $scope.stock.moveQuantity;
            $scope.add($scope.stock.productionDate, $scope.stock.moveQuantity);
            $scope.stock.moveQuantity = $scope.stock.quantity;
        };

        $scope.submitProductionDate = function () {

            if ($scope.stockForm.stockProductionDates.length == 0) {
                alert('请先输入数量录入生产日期再提交');
                return;
            }
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/inputProductionDate',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('录入生产日期成功...')
                $scope.submitting = false;
                $state.go("oam.stock-production-date-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "录入生产日期失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockShelfListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.stockSearchForm = {
            sortField: "shelfCode",
            asc:true
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockShelf/list',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }
        $scope.sort = function(field) {
            if(field && field == $scope.stockSearchForm.sortField) {
                $scope.stockSearchForm.asc = !$scope.stockSearchForm.asc;
            } else {
                $scope.stockSearchForm.sortField = field;
                $scope.stockSearchForm.asc = true;
            }
            $scope.searchForm();
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockSearchForm) {
                if ($scope.stockSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockSearchForm[p]));
                }
            }
            $window.open("/admin/api/stockShelf/export/list?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockTotalDailyCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockTotalForm = {};
        $scope.page = {};

        if ($stateParams.page) {
            $scope.stockTotalForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockTotalForm.cityId = $scope.cities[0].id;
            }
        }
        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $http({
            url: '/admin/api/stockTotalDaily/list',
            method: "GET",
            params: $scope.stockTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockTotalDailys = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.totalCost = data.amount[0];
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockTotalForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockTotalForm) {
                if ($scope.stockTotalForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockTotalForm[p]));
                }
            }
            $window.open("/admin/api/stockTotalDaily/export/list?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockTotalCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockTotalForm = {};
        $scope.page = {};

        if ($stateParams.page) {
            $scope.stockTotalForm.page = parseInt($stateParams.page);
        }

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockTotalForm.cityId = $scope.cities[0].id;
            }
        }
        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $http({
            url: '/admin/api/stockTotal/list',
            method: "GET",
            params: $scope.stockTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockTotals = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.page.itemsPerPage = data.pageSize;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockTotalForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockTotalForm) {
                if ($scope.stockTotalForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockTotalForm[p]));
                }
            }
            $window.open("/admin/api/stockTotal/export/list?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockWillShelfListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $window) {

        $scope.stockSearchForm = {};
        $scope.page = {itemsPerPage : 30};

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.isCheckedAll = false;
        $scope.stockSearchForm.selectStocks = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.stockSearchForm.selectStocks = [];
                angular.forEach($scope.stocks, function(value, key){
                    $scope.stockSearchForm.selectStocks.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.stockSearchForm.selectStocks = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.selectStocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stock/willShelfList',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {

            if ($scope.stockSearchForm.depotId == null) {
                alert('请选择仓库');
                return;
            }
            var str = [];
            for (var p in $scope.stockSearchForm) {
                if ($scope.stockSearchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockSearchForm[p]));
                }
            }
            $window.open("/admin/api/stock/export/willShelfList?" + str.join("&"));
        };

        $scope.batchOnShelf = function() {

            if ($scope.stockSearchForm.selectStocks.length == 0) {
                alert('请选择要上架的商品');
                return;
            }

            $state.go('oam.batch-onShelf', {
                stocks: $scope.stockSearchForm.selectStocks
            });
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.stockInType = $stateParams.stockInType;
        $scope.stockInForm = {
            stockInType: $scope.stockInType,
            stockInStatus: 0
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInForm.page = parseInt($stateParams.page);
        }

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockInForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    /*if ($scope.sourceDepots && $scope.sourceDepots.length == 1) {
                     $scope.stockInForm.sourceDepotId = $scope.sourceDepots[0].id;
                     }
                     if ($scope.targetDepots && $scope.targetDepots.length == 1) {
                     $scope.stockInForm.targetDepotId = $scope.targetDepots[0].id;
                     }*/
                });
            } else {
                $scope.depots = [];
                $scope.stockInForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockInForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockInForm.targetDepotId = null;
            }
        });
        if ($scope.stockInType == 1) {
            $http.get("/admin/api/purchase/order/types").success(function (data) {
                $scope.purchaseOrderTypes = data;
            });
            $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }

        $http({
            url: '/admin/api/stockIn/query',
            method: "GET",
            params: $scope.stockInForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIns = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockInForm);
        };
        $scope.pageChanged = function () {
            $scope.stockInForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInForm);
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {

        $scope.stockInType = $stateParams.stockInType;
        $scope.part = $stateParams.part;
        $scope.viewHeader = $scope.part == true || $scope.part == 'true' ? '部分收货入库' : '收货入库';
        $scope.stockInForm = {
            stockInType: $scope.stockInType
        };
        $scope.stockInItems = [];
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $http.get("/admin/api/stockIn/receive/" + $stateParams.stockInId).success(function (data) {
            $scope.stockInForm = data;
            $scope.stockInItems = data.stockInItems;
            $scope.loadSuccess = true;
        }).error(function (data) {
            alert("获取入库单信息失败...");
        });

        $scope.stockInReceive = function () {
            $scope.submitting = true;
            $scope.stockInForm.stockInId = $stateParams.stockInId;
            $scope.stockInForm.stockInType = $stateParams.stockInType;
            $scope.stockInForm.part = $scope.part;
            $scope.stockInForm.stockInItems = $scope.stockInItems;
            $http({
                url: "/admin/api/stockIn/receive/add",
                method: "POST",
                data: $scope.stockInForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("入库成功...");
                $scope.submitting = false;
                $state.go('oam.stockIn-list', {stockInType: $scope.stockInType});
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "入库失败...");
                $scope.submitting = false;
            });
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockInTotalListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window, $state) {

        $scope.stockInTotalForm = {};

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInTotalForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockInTotalForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockInTotalForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockInTotalForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInTotalForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInTotalForm.depotId = null;
            }

            if (newVal) {
                $http({
                    url: "/admin/api/vendor",
                    method: 'GET',
                    params: {cityId: newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        $http.get("/admin/api/stockIn/type/list").success(function (data) {
            $scope.type = data;
        });
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $http.get("/admin/api/purchase/order/types").success(function (data) {
            $scope.pType = data;
        });
        $http.get("/admin/api/sellReturn/type/list").success(function (data) {
            $scope.rType = data;
        });
        $http.get("/admin/api/stockPrint/status/list").success(function (data) {
            $scope.printStatus = data;
        });

        $scope.$watch('stockInTotalForm.stockInType', function (type) {
            if (type != null && type == 1) {
                $scope.isPurchase = true;
            } else {
                $scope.isPurchase = false;
                $scope.stockInTotalForm.purchaseOrderType = null;
            }
            if (type != null && type == 2) {
                $scope.isReturn = true;
            } else {
                $scope.isReturn = false;
                $scope.stockInTotalForm.sellReturnType = null;
            }
        });

        $http({
            url: '/admin/api/stockIn/query',
            method: "GET",
            params: $scope.stockInTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIns = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockInTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockInTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInTotalForm);
        };

        $scope.isCheckedAll = false;
        $scope.stockInTotalForm.stockInIds = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.stockInTotalForm.stockInIds = [];
                angular.forEach($scope.stockIns, function (value, key) {
                    $scope.stockInTotalForm.stockInIds.push(value.stockInId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.stockInTotalForm.stockInIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.batchPrint = function () {
            if ($scope.stockInTotalForm.stockInIds.length == 0) {
                alert("请选择入库单");
                return;
            }
            var win = $window.open("/admin/api/stockIn/export/bills?stockInIds=" + $scope.stockInTotalForm.stockInIds);
            win.onunload = function () {
                $state.go($state.current, $scope.stockInTotalForm, {reload: true});
            }
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutAllReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stockOuts == null) {
            $state.go("oam.stockOut-receive-list");
        }
        $scope.stockOutForm = {};
        $scope.stockOutForm.stockOuts = $stateParams.stockOuts;
        $scope.stockOutForm.type = 0;
        $scope.collectionments = [];
        $scope.methods = [];
        $scope.cashMethod = null;
        $scope.isReceiveAmountLessZero = false;
        $scope.cityId = null;
        $scope.submitting = false;

        $scope.stockOutForm.receiveAmount = 0;
        $scope.stockOutForm.stockOutIds = [];
        angular.forEach($scope.stockOutForm.stockOuts, function(value, key) {
            if ($scope.cityId == null) {
                $scope.cityId = value.cityId;
            }
            $scope.stockOutForm.stockOutIds.push(value.stockOutId);
            $scope.stockOutForm.receiveAmount += value.amount;
        });
        $scope.stockOutForm.receiveAmount = parseFloat($scope.stockOutForm.receiveAmount.toFixed(2));

        $http.get("/admin/api/accounting/payment/methods/" + $scope.cityId)
        .success(function (methodData) {
            $scope.methods = methodData;
            var keepGoing = true;
            angular.forEach($scope.methods,function(method, key) {
                if (keepGoing && method.cash) {
                    $scope.cashMethod = method.id;
                    keepGoing = false;
                }
            });

            $scope.add($scope.stockOutForm.receiveAmount);
        });

        $scope.stockOutForm.settle = false;
        if ($scope.stockOutForm.receiveAmount <= 0) {
            $scope.isReceiveAmountLessZero = true;
        }

        $scope.add = function(amount) {
			$scope.inserted = {
			    collectionPaymentMethodId:$scope.cashMethod,
			    amount:amount
			};
			$scope.collectionments.push($scope.inserted);
		};

        $scope.remove = function(index) {
            $scope.collectionments.splice(index, 1);
        };

        $scope.stockOutReceive = function () {

            if ($scope.stockOutForm.settle && !$scope.isReceiveAmountLessZero) {
                if ($scope.collectionments.length == 0) {
                    alert('已结款,请选择收款方式并输入收款金额')
                    return;
                }
                var pass = true;
                var collectionmentAmount = 0;
                angular.forEach($scope.collectionments,function(collectionment, key) {
                    if(pass && collectionment.collectionPaymentMethodId == null) {
                        alert('第' + (key+1) + '行请选择收款方式');
                        pass = false;
                    }

                    if (pass && !angular.isNumber(collectionment.amount)) {
                        alert('第' + (key+1) + '行请输入有效金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount <= 0) {
                        alert('第' + (key+1) + '行请输入大于0的金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount.toString().indexOf('.') >= 0 && collectionment.amount.toString().substring(collectionment.amount.toString().indexOf('.')).length > 3) {
                        alert('第' + (key+1) + '行请输入最多两位小数金额');
                        pass = false;
                    }


                    if (collectionment.amount != null && collectionment.amount != 'undefined') {
                        collectionmentAmount += collectionment.amount;
                    }
                });
                if (!pass) {
                    return;
                }
                if (parseFloat($scope.stockOutForm.receiveAmount) - parseFloat(collectionmentAmount.toFixed(2)) != 0) {
                    alert('收款金额和实收金额不符');
                    return;
                }
            } else {
                $scope.collectionments = [];
            }

            $scope.submitting = true;
            $scope.stockOutForm.collectionments = $scope.collectionments;

            $http({
                url: "/admin/api/stockOut/send/finish-all",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("收货成功...");
                $scope.submitting = false;
                $state.go("oam.stockOut-receive-list");
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "收货失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutBarcodeListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window, $state) {

        $scope.stockOutForm = {
            stockOutType: 1,
            stockOutStatus: 0
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage : 100};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockOutForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockOutForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.stockOutForm.cityId, newVal);
            }
        });

        $scope.search = function (page) {
            $scope.stockOutItems = [];
            $scope.stockOutForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOutItem/query',
                method: "GET",
                params: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOutItems = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
                alert("加载失败...");
            });
        }

        $scope.stockOutForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.excelStockBarcodeExport = function(fileType){

            if ($scope.stockOutForm.depotId == null) {
                alert('请选择仓库');
                return;
            }
            var str = [];
            for(var p in $scope.stockOutForm) {
                if($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            $window.open("/admin/api/stockOut/excel-barcode/"+ fileType +"?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutCollectionListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $state) {

        $scope.formData = {
            stockOutType: 1,
            stockOutStatus:2,
            settle: false
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage : 100}
        $scope.totalAmount = 0;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };

        $scope.openStartOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartOrderDate = true;
        };
        $scope.openEndOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndOrderDate = true;
        };
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.formData.depotId = $scope.depots[0].id;
                   }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.getTrackers(null, null);
        $scope.$watch('formData.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.formData.cityId, newVal);
            }
        });

        $scope.isCheckedAll = false;

        $scope.formData.selectStockOuts = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.selectStockOuts = [];
                angular.forEach($scope.stockOuts, function(value, key){
                    $scope.formData.selectStockOuts.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.selectStockOuts = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.SearchStockOutOrders = function (page) {
            $scope.stockOuts = [];
            $scope.formData.selectStockOuts = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOut/query',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOuts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount[1];
            }).error(function (data) {
            });
        }

        $scope.batchReceive = function() {

            if ($scope.formData.selectStockOuts.length == 0) {
                alert('请选择出库单');
                return;
            }
            var cityId = null;
            var pass = true;
            angular.forEach($scope.formData.selectStockOuts, function(value, key){
                if (pass && cityId != null && value.cityId != cityId) {
                    alert('请选择同一城市的出库单');
                    pass = false;
                }
                if (pass) {
                    cityId = value.cityId;
                }
            });
            if (!pass) {
                return;
            }

            $state.go('oam.stockOut-collection', {
                stockOuts: $scope.formData.selectStockOuts
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.SearchStockOutOrders();

        $scope.pageChanged = function () {
            $scope.SearchStockOutOrders($scope.page.currentPage - 1);
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutCollectionCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {

        if ($stateParams.stockOuts == null) {
            $state.go("oam.stockOut-collection-list");
        }
        $scope.stockOutForm = {};
        $scope.stockOutForm.stockOuts = $stateParams.stockOuts;
        $scope.stockOutForm.type = 1;
        $scope.collectionments = [];
        $scope.methods = [];
        $scope.cashMethod = null;
        $scope.isReceiveAmountLessZero = false;
        $scope.cityId = null;
        $scope.submitting = false;

        $scope.stockOutForm.receiveAmount = 0;
        $scope.stockOutForm.stockOutIds = [];
        angular.forEach($scope.stockOutForm.stockOuts, function(value, key) {
            if ($scope.cityId == null) {
                $scope.cityId = value.cityId;
            }
            $scope.stockOutForm.stockOutIds.push(value.stockOutId);
            $scope.stockOutForm.receiveAmount += value.receiveAmount;
        });
        $scope.stockOutForm.receiveAmount = parseFloat($scope.stockOutForm.receiveAmount.toFixed(2));

        $http.get("/admin/api/accounting/payment/methods/" + $scope.cityId)
        .success(function (methodData) {
            $scope.methods = methodData;
            var keepGoing = true;
            angular.forEach($scope.methods,function(method, key) {
                if (keepGoing && method.cash) {
                    $scope.cashMethod = method.id;
                    keepGoing = false;
                }
            });

            $scope.add($scope.stockOutForm.receiveAmount);
        });

        $scope.stockOutForm.settle = true;
        if ($scope.stockOutForm.receiveAmount <= 0) {
            $scope.isReceiveAmountLessZero = true;
        }

        $scope.add = function(amount) {
			$scope.inserted = {
			    collectionPaymentMethodId:$scope.cashMethod,
			    amount:amount
			};
			$scope.collectionments.push($scope.inserted);
		};

        $scope.remove = function(index) {
            $scope.collectionments.splice(index, 1);
        };

        $scope.stockOutReceive = function () {

            if ($scope.stockOutForm.settle && !$scope.isReceiveAmountLessZero) {
                if ($scope.collectionments.length == 0) {
                    alert('请选择收款方式并输入收款金额')
                    return;
                }
                var pass = true;
                var collectionmentAmount = 0;
                angular.forEach($scope.collectionments,function(collectionment, key) {
                    if(pass && collectionment.collectionPaymentMethodId == null) {
                        alert('第' + (key+1) + '行请选择收款方式');
                        pass = false;
                    }

                    if (pass && !angular.isNumber(collectionment.amount)) {
                        alert('第' + (key+1) + '行请输入有效金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount <= 0) {
                        alert('第' + (key+1) + '行请输入大于0的金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount.toString().indexOf('.') >= 0 && collectionment.amount.toString().substring(collectionment.amount.toString().indexOf('.')).length > 3) {
                        alert('第' + (key+1) + '行请输入最多两位小数金额');
                        pass = false;
                    }


                    if (collectionment.amount != null && collectionment.amount != 'undefined') {
                        collectionmentAmount += collectionment.amount;
                    }
                });
                if (!pass) {
                    return;
                }
                if (parseFloat($scope.stockOutForm.receiveAmount) - parseFloat(collectionmentAmount.toFixed(2)) != 0) {
                    alert('收款金额和实收金额不符');
                    return;
                }
            } else {
                $scope.collectionments = [];
            }

            $scope.submitting = true;
            $scope.stockOutForm.collectionments = $scope.collectionments;

            $http({
                url: "/admin/api/stockOut/send/finish-all",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("收款成功...");
                $scope.submitting = false;
                $state.go("oam.stockOut-collection-list");
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "收款失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window, $state, $timeout) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType,
            stockOutStatus: 0
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.timeOptions = {
            showMeridian: false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockOutForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                    $scope.stockOutForm.sourceDepotId = null;
                    $scope.stockOutForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.sourceDepots = [];
                $scope.targetDepots = [];
                $scope.stockOutForm.depotId = null;
                $scope.stockOutForm.sourceDepotId = null;
                $scope.stockOutForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutForm.cityId, newVal);
                }
            });
        }
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }

        $http.get("/admin/api/stockPrint/status/list").success(function (data) {
            $scope.printStatus = data;
        });
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.totalAmount = data.amount;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockOutForm);
            $scope.batchForm.stockOutIds = [];
        };
        $scope.pageChanged = function () {
            $scope.stockOutForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutForm);
            $scope.batchForm.stockOutIds = [];
        }

        $scope.excelSkuExport = function () {

            if ($scope.stockOutType != 2 && $scope.stockOutForm.depotId == null) {
                alert('请选择仓库');
                return;
            } else if ($scope.stockOutType == 2 && $scope.stockOutForm.sourceDepotId == null) {
                alert('请选择调出仓库');
                return;
            }
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            var win = $window.open("/admin/api/stockOut/excel-sku-pick?" + str.join("&"));
            win.onunload = function () {
                $state.go($state.current, $scope.stockOutForm, {reload: true});
            }
        };

        $scope.excelTrackerExport = function () {

            if ($scope.stockOutType != 2 && $scope.stockOutForm.depotId == null) {
                alert('请选择仓库');
                return;
            } else if ($scope.stockOutType == 2 && $scope.stockOutForm.sourceDepotId == null) {
                alert('请选择调出仓库');
                return;
            }
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            var win = $window.open("/admin/api/stockOut/excel-tracker-pick?" + str.join("&"));
            win.onunload = function () {
                $state.go($state.current, $scope.stockOutForm, {reload: true});
            }
        };

        $scope.excelAssociateExport = function () {
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/excel-associate?" + str.join("&"));
        };

        $scope.submitting = false;
        $scope.isCheckedAll = false;
        $scope.batchForm = {};
        $scope.batchForm.stockOutIds = [];

        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.batchForm.stockOutIds = [];
                angular.forEach($scope.stockOuts, function (value, key) {
                    $scope.batchForm.stockOutIds.push(value.stockOutId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.batchForm.stockOutIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.batchOut = function () {
            if ($scope.batchForm.stockOutIds.length == 0) {
                alert("请选择出库单");
                return;
            }

            $scope.submitting = true;
            $scope.skuName = "";
            $http({
                url: "/admin/api/stockOut/send/before-add-all",
                method: "POST",
                data: $scope.batchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                if (data.length == 0) {
                    $scope.batchOutConfirm();
                } else {
                    $scope.skuName = '【' + data.join('】,【') + '】';
                    angular.element('#befoeOutModal').modal({
                        backdrop : 'static'
                    });
                }

            }).error(function (data, status, headers, config) {
                alert("查询未配货品失败...");
                $scope.submitting = false;
            });
        };

        $scope.batchOutConfirm = function () {

            angular.element('#befoeOutModal').modal('hide');
            $timeout(function (){

                $http({
                    url: "/admin/api/stockOut/send/add-all",
                    method: "POST",
                    data: $scope.batchForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("出库成功...");
                    $scope.submitting = false;
                    $state.go($state.current, $scope.stockOutForm, {reload: true});
                    $scope.batchForm.stockOutIds = [];
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "出库失败...");
                    $scope.submitting = false;
                });

            }, 100);
        };
        angular.element('#befoeOutModal').on('hidden.bs.modal', function () {
            $scope.$apply(function () {
                $scope.submitting = false;
            });
        });

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutNotMatchListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window, $state) {

        $scope.stockOutForm = {
            stockOutType: 1,
            stockOutStatus: 0,
            stockOutItemStatus: 0
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage : 100};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockOutForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockOutForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.stockOutForm.cityId, newVal);
            }
        });

        $scope.search = function (page) {
            $scope.stockOutItems = [];
            $scope.stockOutForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOutItem/query',
                method: "GET",
                params: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOutItems = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
                alert("加载失败...");
            });
        }

        $scope.stockOutForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.excelStockNotMatchExport = function(){
            var str = [];
            for(var p in $scope.stockOutForm) {
                if($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            $window.open("/admin/api/stockOut/excel-notmatch?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutOutListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window) {

        $scope.formData = {
            stockOutType: 1,
            stockOutStatus: 2,
            startReceiveDate: $filter('date')(new Date(new Date().getFullYear(), new Date().getMonth(), 1), 'yyyy-MM-dd 00:00'),
            endReceiveDate: $filter('date')(new Date().setDate(new Date().getDate() + 1), 'yyyy-MM-dd 00:00')
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage: 100}
        $scope.totalAmount = [0, 0, 0];

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.formData.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };

        $scope.openStartOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartOrderDate = true;
        };
        $scope.openEndOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndOrderDate = true;
        };
        $scope.openStartReceiveDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartReceiveDate = true;
        };
        $scope.openEndReceiveDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndReceiveDate = true;
        };
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.formData.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.getTrackers(null, null);
        $scope.$watch('formData.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.formData.cityId, newVal);
            }
        });

        $scope.SearchStockOutOrders = function (page) {
            $scope.stockOuts = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOut/query',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOuts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount;
            }).error(function (data) {
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.SearchStockOutOrders();

        $scope.pageChanged = function () {
            $scope.SearchStockOutOrders($scope.page.currentPage - 1);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/stockOut/out/export?" + str.join("&"));
        };
        $scope.exportIncomeDailyReport = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/incomeDailyReport/export?" + str.join("&"));
        };
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutReceiveListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $state, $window) {

        $scope.formData = {
            stockOutType: 1,
            stockOutStatus:1
        };
        $scope.trackers = [];

        $scope.page = {itemsPerPage : 100}
        $scope.totalAmount = 0;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };

        $scope.openStartOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartOrderDate = true;
        };
        $scope.openEndOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndOrderDate = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.formData.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.getTrackers(null, null);
        $scope.$watch('formData.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.formData.cityId, newVal);
            }
        });

        $scope.isCheckedAll = false;

        $scope.formData.selectStockOuts = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.selectStockOuts = [];
                angular.forEach($scope.stockOuts, function(value, key){
                    $scope.formData.selectStockOuts.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.selectStockOuts = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.SearchStockOutOrders = function (page) {
            $scope.stockOuts = [];
            $scope.formData.selectStockOuts = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOut/query',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOuts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount[0];
            }).error(function (data) {
            });
        }

        $scope.batchReceive = function() {

            if ($scope.formData.selectStockOuts.length == 0) {
                alert('请选择出库单');
                return;
            }
            var cityId = null;
            var pass = true;
            angular.forEach($scope.formData.selectStockOuts, function(value, key){
                if (pass && cityId != null && value.cityId != cityId) {
                    alert('请选择同一城市的出库单');
                    pass = false;
                }
                if (pass) {
                    cityId = value.cityId;
                }
            });
            if (!pass) {
                return;
            }

            $state.go('oam.stockOut-all-receive', {
                stockOuts: $scope.formData.selectStockOuts
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.SearchStockOutOrders();

        $scope.pageChanged = function () {
            $scope.SearchStockOutOrders($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/stockOut/receive/export?" + str.join("&"));
        };

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        $scope.stockOutForm = {};
        $scope.stockOutItems = [];
        $scope.collectionments = [];
        $scope.methods = [];
        $scope.cashMethod = null;
        $scope.isReceiveAmountLessZero = false;
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $http.get("/admin/api/stockOut/send/" + $stateParams.stockOutId)
            .success(function (data) {
                $scope.stockOutForm = data;
                $scope.stockOutForm.stockOutType = data.stockOutType.value;
                angular.forEach(data.stockOutItems, function(v, k) {
                    if (v.stockOutItemStatusValue == 1) {
                        $scope.stockOutItems.push(v);
                    }
                });
                $scope.stockOutForm.receiveAmount = data.amount;
                $scope.stockOutForm.settle = false;
                if ($scope.stockOutForm.receiveAmount <= 0) {
                    $scope.isReceiveAmountLessZero = true;
                }
                $scope.loadSuccess = true;

                $http.get("/admin/api/accounting/payment/methods/" + data.cityId)
                .success(function (methodData) {
                    $scope.methods = methodData;
                    var keepGoing = true;
                    angular.forEach($scope.methods,function(method, key) {
                        if (keepGoing && method.cash) {
                            $scope.cashMethod = method.id;
                            keepGoing = false;
                        }
                    });

                    $scope.add($scope.stockOutForm.receiveAmount);
                });
            })
            .error(function (data) {
                window.alert("获取出库单信息失败...");
                return;
            });

        $http.get("/admin/api/sellReturn/reasons")
            .success(function(data) {
                $scope.reasons = data;
            });

        $scope.changeReceiveAmount = function() {
            $scope.stockOutForm.receiveAmount = $scope.stockOutForm.amount;
            angular.forEach($scope.stockOutItems, function(value, key) {
                if (value.returnQuantity) {
                    $scope.stockOutForm.receiveAmount = $scope.stockOutForm.receiveAmount - value.returnQuantity * value.price;
                }
            });
            $scope.stockOutForm.receiveAmount = parseFloat($scope.stockOutForm.receiveAmount.toFixed(2));
            if ($scope.stockOutForm.receiveAmount < 0) {
                $scope.stockOutForm.receiveAmount = 0;
            }
            if ($scope.collectionments.length == 1) {
                $scope.collectionments[0].amount = $scope.stockOutForm.receiveAmount;
            }
        }

        $scope.$watch('stockOutForm.receiveAmount', function (newVal, oldVal) {

            if (newVal != null) {
                $scope.isReceiveAmountLessZero = parseFloat(newVal) <= 0 ? true : false;
            }
            if($scope.isReceiveAmountLessZero) {
                $scope.stockOutForm.settle = true;
            }
        });

        $scope.add = function(amount) {
			$scope.inserted = {
			    collectionPaymentMethodId:$scope.cashMethod,
			    amount:amount
			};
			$scope.collectionments.push($scope.inserted);
		};

        $scope.remove = function(index) {
            $scope.collectionments.splice(index, 1);
        };

        $scope.stockOutReceive = function () {

            var passReceive = true;
            angular.forEach($scope.stockOutItems,function(item, key) {
                if (item.returnQuantity != null && item.returnQuantity.toString().length > 0) {
                    if (passReceive && !angular.isNumber(parseFloat(item.returnQuantity))) {
                        alert('第' + (key+1) + '行退货数量不是有效的数字');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) < 0) {
                        alert('第' + (key+1) + '行退货数量应该大于等于0');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) > parseFloat(item.realQuantity)) {
                        alert('第' + (key+1) + '行退货数量应该小于等于应收数量');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) > 0 && item.sellReturnReasonId == null) {
                        alert('第' + (key+1) + '行请选择退货原因');
                        passReceive = false;
                    }
                }
            });
            if (!passReceive) {
                return;
            }

            if ($scope.stockOutForm.settle && !$scope.isReceiveAmountLessZero) {
                if ($scope.collectionments.length == 0) {
                    alert('已结款,请选择收款方式并输入收款金额')
                    return;
                }
                var pass = true;
                var collectionmentAmount = 0;
                angular.forEach($scope.collectionments,function(collectionment, key) {
                    if(pass && collectionment.collectionPaymentMethodId == null) {
                        alert('第' + (key+1) + '行请选择收款方式');
                        pass = false;
                    }

                    if (pass && !angular.isNumber(collectionment.amount)) {
                        alert('第' + (key+1) + '行请输入有效金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount <= 0) {
                        alert('第' + (key+1) + '行请输入大于0的金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount.toString().indexOf('.') >= 0 && collectionment.amount.toString().substring(collectionment.amount.toString().indexOf('.')).length > 3) {
                        alert('第' + (key+1) + '行请输入最多两位小数金额');
                        pass = false;
                    }


                    if (collectionment.amount != null && collectionment.amount != 'undefined') {
                        collectionmentAmount += collectionment.amount;
                    }
                });
                if (!pass) {
                    return;
                }
                if (parseFloat($scope.stockOutForm.receiveAmount) - parseFloat(collectionmentAmount.toFixed(2)) != 0) {
                    alert('收款金额和实收金额不符');
                    return;
                }
            } else {
                $scope.collectionments = [];
            }


            $scope.submitting = true;
            $scope.stockOutForm.stockOutId = $stateParams.stockOutId;
            $scope.stockOutForm.stockOutItems = $scope.stockOutItems;
            $scope.stockOutForm.collectionments = $scope.collectionments;

            $http({
                url: "/admin/api/stockOut/send/finish",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("收货成功...");
                $scope.submitting = false;
                $state.go("oam.stockOut-receive-list");
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "收货失败...");
                $scope.submitting = false;
            });
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutSendCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType
        };
        $scope.stockOutItems = [];
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $http.get("/admin/api/stockOut/send/" + $stateParams.stockOutId).success(function (data) {
            $scope.stockOutForm = data;
            $scope.stockOutItems = data.stockOutItems;
            $scope.loadSuccess = true;
        }).error(function (data) {
            alert("获取出库单信息失败...");
        });

        $scope.stockOutSend = function () {
            $scope.submitting = true;
            $scope.stockOutForm.stockOutId = $stateParams.stockOutId;
            $scope.stockOutForm.stockOutType = $stateParams.stockOutType;
            $scope.stockOutForm.stockOutItems = [];
            angular.forEach($scope.stockOutItems, function(v, k) {
                if (v.stockOutItemStatusValue == 1) {
                    $scope.stockOutForm.stockOutItems.push(v);
                }
            });
            $http({
                url: "/admin/api/stockOut/send/add",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("出库成功...");
                $scope.submitting = false;
                $state.go('oam.stockOut-list', {stockOutType: $scope.stockOutType});
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "出库失败...");
                $scope.submitting = false;
            });
        }
    });
'use strict';

angular.module('sbAdminApp')
    .controller('StockOutTotalExportCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutTotalForm = {};

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutTotalForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockOutTotalForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutTotalForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutTotalForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockOutTotalForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutTotalForm.depotId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.stockOutTotalForm.cityId, newVal);
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/depot/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.stockOutTotalForm.warehouseId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.warehouseId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/block/warehouse/" + newVal + "").success(function (data) {
                    $scope.blocks = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.blockId = null;
                }
            } else {
                $scope.blocks = [];
                $scope.stockOutTotalForm.blockId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.stockOutType', function (type) {
            if (type != null && type == 1) {
                $scope.isOrder = true;
                $scope.getTrackers(null, null);
            } else {
                $scope.isOrder = false;
                $scope.stockOutTotalForm.warehouseId = null;
                $scope.stockOutTotalForm.blockId = null;
                $scope.stockOutTotalForm.trackerId = null;
                $scope.stockOutTotalForm.startOrderDate = null;
                $scope.stockOutTotalForm.endOrderDate = null;
            }
        });
        $http.get("/admin/api/stockOut/type/list").success(function (data) {
            $scope.type = data;
        });
        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockOutTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockOutTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutTotalForm);
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.stockOutTotalForm) {
                if ($scope.stockOutTotalForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutTotalForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/total/export?" + str.join("&"));
        };

    });

'use strict';

angular.module('sbAdminApp')
    .controller('StockOutTotalListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window, $state) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutTotalForm = {
            stockOutType: $scope.stockOutType
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutTotalForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockOutTotalForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutTotalForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutTotalForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockOutTotalForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.depotId = null;
                    $scope.stockOutTotalForm.sourceDepotId = null;
                    $scope.stockOutTotalForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.sourceDepots = [];
                $scope.targetDepots = [];
                $scope.stockOutTotalForm.depotId = null;
                $scope.stockOutTotalForm.sourceDepotId = null;
                $scope.stockOutTotalForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutTotalForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutTotalForm.cityId, newVal);
                }
                if (newVal != null && newVal != "") {
                    $http.get("/admin/api/warehouse/depot/" + newVal + "").success(function (data) {
                        $scope.warehouses = data;
                    });
                    if (typeof oldVal != "undefined" && newVal != oldVal) {
                        $scope.stockOutTotalForm.warehouseId = null;
                    }
                } else {
                    $scope.warehouses = [];
                    $scope.stockOutTotalForm.warehouseId = null;
                }
            });
            $scope.$watch('stockOutTotalForm.warehouseId', function (newVal, oldVal) {
                if (newVal != null && newVal != "") {
                    $http.get("/admin/api/block/warehouse/" + newVal + "").success(function (data) {
                        $scope.blocks = data;
                    });
                    if (typeof oldVal != "undefined" && newVal != oldVal) {
                        $scope.stockOutTotalForm.blockId = null;
                    }
                } else {
                    $scope.blocks = [];
                    $scope.stockOutTotalForm.blockId = null;
                }
            });
        }
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutTotalForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }

        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        $http.get("/admin/api/stockPrint/status/list").success(function (data) {
            $scope.printStatus = data;
        });
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockOutTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockOutTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutTotalForm);
        };

        $scope.isCheckedAll = false;
        $scope.stockOutTotalForm.stockOutIds = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.stockOutTotalForm.stockOutIds = [];
                angular.forEach($scope.stockOuts, function (value, key) {
                    $scope.stockOutTotalForm.stockOutIds.push(value.stockOutId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.stockOutTotalForm.stockOutIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.batchPrint = function () {
            if ($scope.stockOutTotalForm.stockOutIds.length == 0) {
                alert("请选择出库单");
                return;
            }
            var win = $window.open("/admin/api/stockOut/export/bills?stockOutIds=" + $scope.stockOutTotalForm.stockOutIds);
            win.onunload = function(){
                $state.go($state.current, $scope.stockOutTotalForm, {reload: true});
            }
        };

    });

'use strict';

angular.module('sbAdminApp')
	.controller('ExcelExportTaskList', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window, $interval) {

		$scope.searchForm = {};

		$scope.search = function() {
			$http({
				url: "/admin/api/task/excel/myTaskList",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.taskList = data;
			})
		}

		$scope.search();

		var timer = $interval(function() {
			$scope.search();
		}, 10 * 1000, 20);

		$scope.downLoad = function(taskId){
			$window.open("/admin/api/task/excel/download?taskId="+taskId);
		}

		$scope.$on('$destroy', function () { $interval.cancel(timer); });
	});
/**
 * Created by challenge on 15/10/29.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('TokenManagementCtrl', function($scope, $http , $stateParams) {


        $scope.obtainToken = function () {
            $http.get("/admin/api/customer/token/" + $scope.username.token)
                .success(function(data){
                    alert("用户token为:"+data.token+"有效期为30分钟!");
                    $scope.username = data;
                })
                .error(function(data){
                    alert("获取失败");
                });
        }
    });
/**
 * Created by challenge on 15/9/14.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddcancelOrderReasonCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

        $http.get("/admin/api/order/reason")
            .success(function(data) {
                $scope.reasons = data;
            });

        $scope.formData = {
            orderId : $stateParams.orderId,
            reasonId : $stateParams.reasonId,
            remark : $stateParams.memo
        };

        $scope.cancelOrder = function() {
            $http({
                method: 'POST',
                url: '/admin/api/order/cancel/reason',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
                .success(function(data, status, headers, config) {
                    window.alert("提交成功!");
                    $state.go("oam.orderList", {orderId:$scope.formData.orderId,status:-1});

                })
                .error(function(data, status, headers, config) {
                    window.alert("提交失败！");
                });
        };
    })


/**
 * Created by challenge on 15/11/30.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('AddOrderCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

        $scope.restaurants = [];
        $scope.candidateRestaurants = [];
        $scope.restaurant = {};

        $scope.funcAsyncRestaurant = function (name) {
            if (name && name !== "") {
                $scope.candidateRestaurants = [];
                $http({
                    url: "/admin/api/restaurant/candidates",
                    method: 'GET',
                    params: {page: 0, pageSize: 20, name: name, showLoader:false}
                }).then(
                //$http.get("/admin/api/restaurant/candidates?page=0&pageSize=20&name="+name).then(
                    function (data) {
                        $scope.candidateRestaurants = data.data;
                    }
                )
            }
        }

        $scope.searchRestaurant = function(restaurant) {
            $scope.candidateRestaurants = [];

            $http.get("/admin/api/restaurant/" + restaurant.id).success(function (data, status, headers, config) {
                if (!data) {
                    alert('餐馆不存在或已失效');
                    restaurant.id = '';
                    return;
                }
                $scope.candidateRestaurants.push(data);
                $scope.restaurant = data;
            }).error(function (data, status, headers, config) {
                alert('餐馆不存在或已失效');
                restaurant.id = '';
                return;
            });
        };

        $scope.resetCandidateRestaurants = function () {
            $scope.candidateRestaurants = [];
        }

        //$scope.types = [
        //    {
        //        "id": 1,
        //        "name": "普通"
        //    },
        //    {
        //        "id": 2,
        //        "name": "赠品"
        //    }
        //]
        $scope.types=[];
        $http({
            url: "/admin/api/order/orderType/get",
            method: 'GET'
        }).then(
            function (data) {
                $scope.types=data.data;
                $scope.searchForm.type = $scope.types[0].val;
                console.log($scope.types);
            }
        )

        $scope.searchForm = {};
        $scope.submitting = false;
        $scope.orderTotalPrice = 0;
        $scope.candidateDynamicPrices = [];

        $scope.funcAsync = function (name) {
            if (name && name !== "") {
                $scope.candidateDynamicPrices = [];
                $http({
                    url: "/admin/api/dynamic-price/candidates",
                    method: 'GET',
                    params: {warehouse: $scope.restaurant.customer.block.warehouse.id, name: name, showLoader: false}
                }).then(
                //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        angular.forEach(data.data,function(item) {
                            $scope.candidateDynamicPrices.push(item.sku);
                        })
                    }
                )
            }
        }

        $scope.resetcandidateDynamicPrices = function () {
            $scope.candidateDynamicPrices = [];

        }
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }


        $scope.orderItems = [
        ];

        $scope.remove = function(index) {
            $scope.orderItems.splice(index, 1);
        }

        $scope.addItem = function() {
            $scope.inserted = {
            };
            $scope.orderItems.push($scope.inserted);
        };

        $scope.totalBundleQuantity = function(orderItem) {
            orderItem.quantity = orderItem.bundleQuantity * orderItem.capacityInBundle;
            if($scope.searchForm.type == 2) {
                orderItem.totalPrice = 0;
            }else{
                $scope.calculatePrice(orderItem);
            }
        };

        $scope.totalQuantity = function(orderItem) {
            orderItem.bundleQuantity = orderItem.quantity / orderItem.capacityInBundle;
            if($scope.searchForm.type == 2) {
                orderItem.totalPrice = 0;
            }else{
                $scope.calculatePrice(orderItem);
            }
        };

        $scope.searchSku = function(orderItem) {
            $scope.candidateDynamicPrices = [];
            $http({
                url:"/admin/api/dynamic-price/sku",
                method:'GET',
                params:{skuId:orderItem.skuId,warehouse:$scope.restaurant.customer.block.warehouse.id},
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                if (!data.sku) {
                    alert('sku不存在或已失效');
                    orderItem.skuId = '';

                    return;
                }

                $scope.candidateDynamicPrices.push(data.sku);
                orderItem.skuId = data.sku.id;
                orderItem.name = data.sku.name;
                orderItem.rate = data.sku.rate;
                orderItem.singleUnit = data.sku.singleUnit;
                orderItem.bundleUnit = data.sku.bundleUnit;
                orderItem.capacityInBundle = data.sku.capacityInBundle;
                orderItem.singleSalePrice = data.singleDynamicSkuPriceStatus.singleSalePrice;
                orderItem.bundleSalePrice = data.bundleDynamicSkuPriceStatus.bundleSalePrice;
                if(data.singleDynamicSkuPriceStatus.singleInSale) {
                    $scope.singleEdit = true;
                }
                if(data.bundleDynamicSkuPriceStatus.bundleInSale) {
                    $scope.bundleEdit = true;
                }
                if(orderItem.quantity) {
                    if($scope.searchForm.type == 2) {
                        orderItem.totalPrice = 0;
                    }else{
                        orderItem.totalPrice = orderItem.singleSalePrice != 0 ? orderItem.singleSalePrice * orderItem.quantity : orderItem.bundleSalePrice * orderItem.bundleQuantity;
                        $scope.calculatePrice(orderItem);
                    }
                }

            });
        };

        $scope.orderRequest = {

            restaurantId : null,
            remark : null,
            type : null,
            requests : []
        };
        $scope.changeInput = function(typeId) {
            while($scope.orderItems.length > 0) {
                $scope.orderItems.pop();
            }
            $scope.orderTotalPrice = 0;
            //if(typeId == 1) {
            //    while($scope.orderItems.length > 0) {
            //        $scope.orderItems.pop();
            //    }
            //
            //}else if(typeId == 2) {
            //
            //}
        };

        $scope.createOrder = function() {
            $scope.orderRequest.requests = [];
            $scope.orderRequest.restaurantId = $scope.restaurant.id;
            $scope.orderRequest.remark = $scope.restaurant.remark;
            $scope.orderRequest.type = $scope.searchForm.type;
            for(var i = 0 ; i < $scope.orderItems.length ; i++){
                if(!$scope.orderItems[i].quantity) {
                    alert("请输入购买数量!");
                    return;
                }
                $scope.orderRequest.requests.push({
                    skuId : $scope.orderItems[i].skuId,
                    quantity : $scope.orderItems[i].quantity,
                    bundle : false,
                    price : $scope.orderItems[i].singleSalePrice
                })
            }
            $http({
                url:"/admin/api/order/create",
                method:"POST",
                data:$scope.orderRequest
            }).success(function(data) {
                alert("创建成功!")
                $scope.orderItems = [];
                $scope.restaurants = [];
                $scope.candidateRestaurants = [];
                $scope.restaurant = {};
                $scope.searchForm.type = 1;
            }).error(function(data) {
                alert("创建失败!")
            })
        };

        $scope.calculatePrice = function(orderItem) {

            orderItem.totalPrice = orderItem.singleSalePrice != 0 ? orderItem.singleSalePrice * orderItem.quantity : orderItem.bundleSalePrice * orderItem.bundleQuantity;
            $scope.orderTotalPrice = 0;
            for(var i = 0; i < $scope.orderItems.length; i++) {
                $scope.orderTotalPrice = $scope.orderTotalPrice + $scope.orderItems[i].totalPrice;
            }
        };

    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderDetailCtrl
 * @description
 * # OrderDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderDetailCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/order/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                $scope.order = data;
                $scope.sellReturnItems = data.sellReturnItems;
            });

        $http.get("/admin/api/sellReturn/reasons")
            .success(function(data) {
                $scope.reasons = data;
            });

        $http.get("/admin/api/order/reason")
            .success(function(data) {
                $scope.cancelOrderReasons = data;
            });

        $scope.refundObj = {
            reasonId : null,
            skuRefundRequests : []
        };
        $scope.refundArr = [];
        $scope.newPromotions = [];
        $scope.newCoupons = [];

        $scope.addSellCancelToArr = function (orderItem, check) {
            $scope.newPromotions = [];
            $scope.newCoupons = [];
            var tag = true;
            if (!orderItem.cancelQuantity) {
                orderItem.cancelQuantity = 0;
            }
            var availableQuantity = orderItem.bundle ? (orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity)/orderItem.sku.capacityInBundle : (orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity);
            if (availableQuantity < orderItem.cancelQuantity) {
                alert("数量不能大于可操作数量");
                return;
            }
            if ($scope.refundArr == null || $scope.refundArr.length === 0) {
                $scope.refundArr.push({
                    orderItemId: orderItem.id,
                    skuId:orderItem.sku.id,
                    bundle:orderItem.bundle,
                    quantity: orderItem.cancelQuantity,
                    availableQuantity:availableQuantity,
                    memo:orderItem.memo,
                    reasonId:orderItem.cancelOrderReasonId
                });
            } else {
                for (var i = 0; i < $scope.refundArr.length; i++) {
                    if ($scope.refundArr[i].orderItemId == orderItem.id) {
                        $scope.refundArr.splice(i, 1);
                        $scope.refundArr.push({
                            orderItemId: orderItem.id,
                            skuId:orderItem.sku.id,
                            bundle:orderItem.bundle,
                            quantity: orderItem.cancelQuantity,
                            availableQuantity:availableQuantity,
                            memo:orderItem.memo,
                            reasonId:orderItem.cancelOrderReasonId
                        });
                        tag = false;
                    }
                }

                if(tag){
                    $scope.refundArr.push({
                        orderItemId: orderItem.id,
                        skuId:orderItem.sku.id,
                        bundle:orderItem.bundle,
                        quantity: orderItem.cancelQuantity,
                        availableQuantity:availableQuantity,
                        memo:orderItem.memo,
                        reasonId:orderItem.cancelOrderReasonId
                    });
                }
            }

            if (check) {
                $scope.refundObj.orderId = $scope.order.id;
                $scope.refundObj.sellCancelItemRequest = $scope.refundArr;
                if (orderItem.cancelQuantity > 0) {
                    $http({
                        url: "/admin/api/order/newPromotion",
                        method: "POST",
                        data: $scope.refundObj
                    }).success(function (data) {
                        $scope.newPromotions = data.promotions;
                        $scope.newCoupons = data.customerCoupons;
                        var s = '';
                        if ($scope.newPromotions.length > 0) {
                            angular.forEach(data.promotions, function(value, key) {
                                s += "\n" + value.description;
                            });
                        }
                        if ($scope.newCoupons.length > 0) {
                            angular.forEach(data.customerCoupons, function(value, key) {
                                s += "\n" + value.coupon.description;
                            });
                        }
                        if (s.length > 0) {
                            alert("被取消的优惠:"+ s);
                        }
                    }).error(function (data) {
                        alert("操作失败");
                    });
                }
            }
        }

        $scope.addSellCancel = function () {

            for (var i = 0; i < $scope.refundArr.length; i++) {
                if ($scope.refundArr[i].availableQuantity < $scope.refundArr[i].quantity) {
                    console.log($scope.refundArr[i]);
                    alert("数量不能大于可操作数量");
                    return;
                }
                if (!$scope.refundArr[i].reasonId) {
                    alert("取消原因不能为空");
                    return;
                }
            }
            if ($scope.refundArr.length == 0) {
                alert("暂无取消");
                return;
            }
            $scope.refundObj.orderId = $scope.order.id;
            $scope.refundObj.sellCancelItemRequest = $scope.refundArr;
            $http({
                url: "/admin/api/sellCancel",
                method: "post",
                data: $scope.refundObj
            }).success(function (data) {
                alert("操作成功!");
                $scope.refundArr = [];
                $scope.order = data;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }

        $scope.addSellReturnToArr = function (orderItem) {
            var tag = true;
            if (!orderItem.returnQuantity) {
                orderItem.returnQuantity = 0
            }
            var availableQuantity = orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity;
            if (availableQuantity < orderItem.returnQuantity) {
                    alert("数量不能大于可操作数量");
            }
            if ($scope.refundArr == null || $scope.refundArr.length === 0) {
                $scope.refundArr.push({
                    orderItemId: orderItem.id,
                    availableQuantity:availableQuantity,
                    quantity: orderItem.returnQuantity,
                    reasonId: orderItem.reasonId,
                    memo: orderItem.memo
                });
            } else {
                for (var i = 0; i < $scope.refundArr.length; i++) {
                    if ($scope.refundArr[i].orderItemId == orderItem.id) {
                        $scope.refundArr.splice(i, 1);
                        $scope.refundArr.push({
                            orderItemId: orderItem.id,
                            availableQuantity:availableQuantity,
                            quantity: orderItem.returnQuantity,
                            reasonId: orderItem.reasonId,
                            memo: orderItem.memo
                        });
                        tag = false;
                    }
                }

                if(tag){
                    $scope.refundArr.push({
                        orderItemId: orderItem.id,
                        availableQuantity:availableQuantity,
                        quantity: orderItem.returnQuantity,
                        reasonId: orderItem.reasonId,
                        memo: orderItem.memo
                    });
                }
            }
        }

        $scope.addSellReturn = function () {

            for (var i = 0; i < $scope.refundArr.length; i++) {
                if (!$scope.refundArr[i].reasonId) {
                    alert("退货理由不能为空");
                    return;
                }
                if ($scope.refundArr[i].availableQuantity < $scope.refundArr[i].quantity) {
                    alert("数量不能大于可操作数量");
                    return;
                }
            }
            if ($scope.refundArr.length == 0) {
                alert("暂无退货");
                return;
            }
            $scope.refundObj.orderId = $scope.order.id;
            $scope.refundObj.sellReturnItemRequests = $scope.refundArr;
            $http({
                url: "/admin/api/sellReturn",
                method: "post",
                data: $scope.refundObj
            }).success(function (data) {
                alert("操作成功!");
                $scope.refundArr = [];
                $scope.order = data;
                $scope.sellReturnItems = data.sellReturnItems;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }

        $scope.backOut = function (sellCancelId) {
            $http({
                url:"/admin/api/sellReturn/" + sellCancelId,
                method:"PUT",
                params: {"status":2, "auditOpinion":"撤销"},
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }).success(function(data) {
                alert("撤销成功");
                $scope.order.havaUnFinishedSellCancel = false;
                angular.forEach($scope.sellReturnItems, function(value, key) {
                    value.sellReturnStatus.name = "已撤销";
                });
                $scope.order.sellCancelId = null;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }
    });


'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderItemsListCtrl
 * @description
 * # OrderItemsListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderEvaluate', function ($scope, $rootScope, $http, $stateParams, $filter, $location,$window) {
        /*订单评价查询对象*/
        $scope.orderEvaluate = {
           onlyNoScore : $stateParams.onlyNoScore=="true"
        };

        console.log($stateParams.onlyNoScore);
        console.log($scope.orderEvaluate);

        $scope.page = {
            evaluatePerPage: 100
        };

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.depotCities;
             if ($scope.cities && $scope.cities.length == 1) {
                $scope.orderEvaluate.cityId = $scope.cities[0].id;
             }
        }

        $scope.$watch('orderEvaluate.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderEvaluate.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderEvaluate.depotId = $scope.depots[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderEvaluate.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderEvaluate.organizationId = null;
                   $scope.orderEvaluate.depotId = null;
                   $scope.orderEvaluate.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.depots = [];
               $scope.availableWarehouses = [];
               $scope.orderEvaluate.organizationId = null;
               $scope.orderEvaluate.depotId = null;
               $scope.orderEvaluate.warehouseId = null;
           }
        });

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };


        if($stateParams.start) {
            $scope.orderEvaluate.start = $stateParams.start;
        }

        if($stateParams.end) {
            $scope.orderEvaluate.end = $stateParams.end;
        }

        if($scope.orderEvaluate.start) {
            $scope.startDate = Date.parse($scope.orderEvaluate.start);
        }

        if($scope.orderEvaluate.end) {
            $scope.endDate = Date.parse($scope.orderEvaluate.end);
        }

        if ($stateParams.cityId) {
            $scope.orderEvaluate.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.organizationId) {
            $scope.orderEvaluate.organizationId = parseInt($stateParams.organizationId);
        }

        if ($stateParams.warehouseId) {
            $scope.orderEvaluate.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.orderId) {
            $scope.orderEvaluate.orderId = parseInt($stateParams.orderId);
        }

        if ($stateParams.adminName) {
            $scope.orderEvaluate.adminName = $stateParams.adminName;
        }

        if ($stateParams.trackerName) {
            $scope.orderEvaluate.trackerName = $stateParams.trackerName;
        }

        if ($stateParams.page) {
            $scope.orderEvaluate.page = $stateParams.page;
        }

        if ($stateParams.pageSize) {
            $scope.orderEvaluate.pageSize = $stateParams.pageSize;
        }

        $scope.format = 'yyyy-MM-dd';

        $scope.$watch('startDate', function (d) {
            if(d) {
                $scope.orderEvaluate.start = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('endDate', function (d) {
            if (d) {
                $scope.orderEvaluate.end = $filter('date')(d, 'yyyy-MM-dd');
            }
        });
        
        $scope.resetPageAndSearchOrderEvaluates = function () {
           
            $scope.searchOrderEvaluate();
        }
        $scope.orderEvaluateData;
        $scope.searchOrderEvaluate = function () {
            $location.search($scope.orderEvaluate);

            $http({
                url:'/admin/api/order/evaluate',
                method: "GET",
                params: $scope.orderEvaluate
            }).success(function (data, status, headers, config) {
               $scope.orderEvaluateData = data.orderEvaluates;

                 /*分页数据*/
                $scope.page.evaluatePerPage = data.pageSize;
                $scope.page.totalEvaluate = data.total;
                $scope.page.currentPage = data.page + 1;

            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.searchOrderEvaluate();

        $scope.bakScore=function(orderEvaluate){
            $http({
                url:'/admin/api/order/evaluate/score/send',
                method: "GET",
                params: {
                    orderId: orderEvaluate.orderId
                }
            }).success(function (data, status, headers, config) {
                orderEvaluate.scoreLog=data;

            }).error(function (data, status) {
                window.alert("操作失败");
            });
        };

        $scope.pageChanged = function () {
            $scope.orderEvaluate.page = $scope.page.currentPage - 1;
            $scope.orderEvaluate.pageSize = $scope.page.evaluatePerPage;

            $scope.searchOrderEvaluate();
        }


         $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderEvaluate) {
                if($scope.orderEvaluate[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderEvaluate[p]));
                }
            }


            $window.open("/admin/api/order-evaluate/excelExport?" + str.join("&"));
         };

    })
'use strict';

angular.module('sbAdminApp')
    .controller('OrderInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/order/info/" + $stateParams.id).success(function (data) {
            $scope.order = data;
        }).error(function () {
            alert("加载失败...");
        });
    });


'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderItemsListCtrl
 * @description
 * # OrderItemsListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderItemsListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window) {
        $scope.page = {
            itemsPerPage: 100
        };

        /*订单明细列表搜索表单数据*/
        $scope.orderItemsSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            start: $stateParams.start,
            end: $stateParams.end,
            restaurantId: $stateParams.restaurantId,
            skuId: $stateParams.skuId,
            productName:$stateParams.productName,
            restaurantName:$stateParams.restaurantName,
            warehouseId:$stateParams.warehouseId,
            orderId:$stateParams.orderId,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            orderType:$stateParams.orderType

        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderItemsSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/order/orderType/get")
            .success(function (data, status, headers, config) {
                $scope.orderTypes = data;
            }).error(function (data, status) {
            alert("订单状态加载失败！");
        });


        /*$scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';*/

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };

        $scope.orderItems = {};


        $scope.$watch('orderItemsSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderItemsSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderItemsSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderItemsSearchForm.organizationId = null;
                   $scope.orderItemsSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.availableWarehouses = [];
               $scope.orderItemsSearchForm.organizationId = null;
               $scope.orderItemsSearchForm.warehouseId = null;
           }
        });


        $http.get("/admin/api/order/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });


        if($stateParams.orderStatus){
            $scope.orderItemsSearchForm.orderStatus = parseInt($stateParams.orderStatus);
        }
         if($stateParams.cityId) {
            $scope.orderItemsSearchForm.cityId = parseInt($stateParams.cityId);
         }

         if($stateParams.organizationId){
             $scope.orderItemsSearchForm.organizationId = parseInt($stateParams.organizationId);
          }


        if($stateParams.warehouseId){
            $scope.orderItemsSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.orderType) {
            $scope.orderItemsSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.searchOrderItems = function () {

            $http({
                url: '/admin/api/order/item',
                method: "GET",
                params: $scope.orderItemsSearchForm
            }).success(function (data, status, headers, config) {
                $scope.orderItems = data.orderItems;

                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;

            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }
        $scope.resetPageAndSearchOrderItems = function () {
            $scope.orderItemsSearchForm.page = 0;
            $scope.orderItemsSearchForm.pageSize = 100;

            $location.search($scope.orderItemsSearchForm);
        }


        $scope.pageChanged = function() {
            $scope.orderItemsSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderItemsSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.orderItemsSearchForm);
        }

        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderItemsSearchForm) {
                if($scope.orderItemsSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderItemsSearchForm[p]));
                }
            }
            $window.open("/admin/api/order/item/export?" + str.join("&"));
        };

        $scope.searchOrderItems();
})
    'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListOrdersCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {
        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.orders = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderListSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/order/orderType/get")
            .success(function (data, status, headers, config) {
                $scope.orderTypes = data;
            }).error(function (data, status) {
            alert("订单状态加载失败！");
        });

        $scope.dateOptions = {
                dateFormat: 'yyyy-MM-dd',
                formatYear: 'yyyy',
                startingDay: 1,
                startWeek: 1
            };

        $scope.submitDateFormat = "yyyy-MM-dd";

        /*$scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }


        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };*/


        $scope.page = {
            itemsPerPage: 100
        };

        $scope.coordinateLabeleds = [{key:0,value:"坐标缺失"},{key:1,value:"坐标已标注"}];
        $scope.refundsIsNotEmptys = [{key:true,value:"有退货的订单"}];

            /*订单状态*/
      $http.get("/admin/api/order/status")
           .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
           }).error(function (data, status) {
                alert("订单状态加载失败！");
       });

       $scope.$watch('orderListSearchForm.cityId',function(newVal,oldVal){
           if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderListSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderListSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderListSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.organizationId = null;
                   $scope.orderListSearchForm.depotId = null;
                   $scope.orderListSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.depots = [];
               $scope.availableWarehouses = [];
               $scope.orderListSearchForm.organizationId = null;
               $scope.orderListSearchForm.depotId = null;
               $scope.orderListSearchForm.warehouseId = null;
           }
       });

        $scope.$watch('orderListSearchForm.organizationId',function(newVal,oldVal){
            //选择销售
            if(newVal){
                $http({
                    method:"GET",
                    url:"/admin/api/admin-user/global?role=CustomerService",
                    params:{organizationId:newVal}
                }).success(function(data){
                    $scope.adminUsers = data;
                })
            }
        });

        if($stateParams.spikeItemId!=null){
            $scope.orderListSearchForm.spikeItemId = $stateParams.spikeItemId;
        }

        if($stateParams.sortField) {
            $scope.orderListSearchForm.sortField = $stateParams.sortField;
        } else {
            $scope.orderListSearchForm.sortField = "id";
        }

        if($stateParams.asc) {
            $scope.orderListSearchForm.asc = true;
        } else {
            $scope.orderListSearchForm.asc = false;
        }

        if ($stateParams.orderType) {
            $scope.orderListSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.$watch('order.selected', function(arg){
            if(arg){
                $scope.orderListSearchForm.customerId = arg.customer.id;
            }
        });

        $scope.searchOrderList = function () {
            $location.search($scope.orderListSearchForm);
        }

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;
            $scope.searchOrderList();
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrderList();
        }

        $scope.deliverNow =  function(order) {
            $http.post("/admin/api/order/" + order.id + "/deliver")
                .success(function (data, status, headers, config) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("修改成功");
                });
        }

        $scope.cancelOrder = function(order) {
            $http.post("api/order/" + order.id + "/cancel")
                .success(function (data) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $scope.NewTicket = function(order){
            // console.log(order);
            var arr = {
                "username": $rootScope.user.realname,
                "info": order
            };
            arr = JSON.stringify(arr);
            // console.log(arr);
            arr = encodeURIComponent(arr);
            console.log(arr)
            window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data="+arr);
        }

        $scope.completeOrder = function(order) {
            $http.post("api/order/" + order.id + "/complete")
                .success(function (data) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $scope.cancelOrder = function(order) {
            $http.post("api/order/" + order.id + "/cancel")
                .success(function (data) {
                    order.status.name = data.status.name;
                    order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $http({
           url: '/admin/api/order',
           method: "GET",
           params: $scope.orderListSearchForm
        }).success(function (data, status, headers, config) {
           $scope.orders = data.orders;
           $scope.count = data.total;
           $scope.orderStatistics = data.orderStatistics;


           /*分页数据*/
           $scope.page.itemsPerPage = data.pageSize;
           $scope.page.totalItems = data.total;
           $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
           window.alert("搜索失败...");
        });


        $scope.downloadErrorFile = function(fileName) {
            $window.open("/admin/api/dynamic-price/errorFile/" + fileName);
        }

        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderListSearchForm) {
                if($scope.orderListSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderListSearchForm[p]));
                }
            }

            $window.open("/admin/api/order/excelExport?" + str.join("&"));
        };

        $scope.sort = function(field) {
            if(field && field == $scope.orderListSearchForm.sortField) {
                $scope.orderListSearchForm.asc = !$scope.orderListSearchForm.asc;
            } else {
                $scope.orderListSearchForm.sortField = field;
                $scope.orderListSearchForm.asc = false;
            }

            $scope.orderListSearchForm.page = 0;

            $location.search($scope.orderListSearchForm);
        }
});

angular
		.module('sbAdminApp')
		.controller(
				'SkuSaleDetail',
				function($scope, $rootScope, $http, $filter, $stateParams,$window) {

					/* 订单明细列表搜索表单数据 */
					$scope.skuSaleDetailSearchForm = {

					};

                    if($rootScope.user) {
                         var data = $rootScope.user;
                         $scope.cities = data.cities;
                         if ($scope.cities && $scope.cities.length == 1) {
							$scope.skuSaleDetailSearchForm.cityId = $scope.cities[0].id;
						 }
                    }

					$scope.$watch('skuSaleDetailSearchForm.cityId', function(newVal, oldVal) {
						if(newVal){
						   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
							   $scope.organizations = data;
							   if ($scope.organizations && $scope.organizations.length == 1) {
								  $scope.skuSaleDetailSearchForm.organizationId = $scope.organizations[0].id;
							   }
						   });
						   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
							   $scope.depots = data;
							   if ($scope.depots && $scope.depots.length == 1) {
								   $scope.skuSaleDetailSearchForm.depotId = $scope.depots[0].id;
							   }
						   });
						   $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
							   $scope.availableWarehouses = data;
							   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
								   $scope.skuSaleDetailSearchForm.warehouseId = $scope.availableWarehouses[0].id;
							   }
						   });
						   if(typeof oldVal != 'undefined' && newVal != oldVal){
							   $scope.skuSaleDetailSearchForm.organizationId = null;
							   $scope.skuSaleDetailSearchForm.depotId = null;
							   $scope.skuSaleDetailSearchForm.warehouseId = null;
						   }
					   }else{
						   $scope.organizations = [];
						   $scope.depots = [];
						   $scope.availableWarehouses = [];
						   $scope.skuSaleDetailSearchForm.organizationId = null;
						   $scope.skuSaleDetailSearchForm.depotId = null;
						   $scope.skuSaleDetailSearchForm.warehouseId = null;
					   }
					});


					$scope.$watch('skuSaleDetailSearchForm.organizationId', function(organizationId) {
						   if (organizationId) {
							   $http({
								   url: "/admin/api/vendor",
								   method: "GET",
								   params: {'organizationId':organizationId}
							   }).success(function (data) {
									$scope.vendors = data.vendors;
							   });

							   if (typeof old != 'undefined' && cityId != old) {
								   $scope.skuSaleDetailSearchForm.vendorId = null;
							   }
						   } else {
							   $scope.vendors = [];
							   $scope.skuSaleDetailSearchForm.vendorId = null;
						   }
						});


					$scope.openStart = function($event) {
						$event.preventDefault();
						$event.stopPropagation();
						$scope.openedStart = true;
					};


					$scope.openEnd = function($event) {
						$event.preventDefault();
						$event.stopPropagation();
						$scope.openedEnd = true;
					};

					$scope.dateOptions = {
						dateFormat : 'yyyy-MM-dd',
						formatYear : 'yyyy',
						startingDay : 1,
						startWeek : 1
					};

					$scope.format = 'yyyy-MM-dd';

					$scope.page = {
						itemsPerPage : 100
					};

					$scope.$watch('startDate', function(d) {
						$scope.skuSaleDetailSearchForm.start = $filter('date')(
								d, 'yyyy-MM-dd');
					});

					$scope.$watch('endDate', function(d) {
						$scope.skuSaleDetailSearchForm.end = $filter('date')(d,
								'yyyy-MM-dd');
					})

					$scope.skus  = {};
					/* 获取品牌 */
					$http.get("/admin/api/brand").success(
							function(data, status, headers, config) {
								$scope.brands = data;
							}).error(function(data, status) {
						alert("数据加载失败！");
					});

					$scope.search = function() {
						$http({
							method : 'GET',
							url : '/admin/api/sku/sales',
							params : $scope.skuSaleDetailSearchForm

						}).success(function(data, status, headers, config) {
							$scope.skus = data.skuSales;
							
							$scope.page.itemsPerPage = data.pageSize;
			                $scope.page.totalItems = data.total;
			                $scope.page.currentPage = data.page + 1;
						})

					};

					$scope.search();
					$scope.pageChanged = function() {
						$scope.skuSaleDetailSearchForm.page = $scope.page.currentPage - 1;
						$scope.skuSaleDetailSearchForm.pageSize = $scope.page.itemsPerPage;

						$scope.search();
					}
					
					
					 $scope.excelExport = function(){
				            var str = [];
				            for(var p in $scope.skuSaleDetailSearchForm) {
				                if($scope.skuSaleDetailSearchForm[p]) {
				                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.skuSaleDetailSearchForm[p]));
				                }
				            }

				        	$window.open("/admin/api/sku-sale-detail/excelExport?" + str.join("&"));
				        };

				});

'use strict';

angular.module('sbAdminApp')
	.controller('CarDetailCtrl',function($scope, $rootScope, $http, $filter, $state, $stateParams, $location){

    $scope.modelTip = [
        {name:'轻型封闭货车', id:1},
        {name:'面包', id:2},
        {name:'金杯', id:3}
    ];
    $scope.statusTip = [{name:'无效' , id:0} , {name:'有效' , id:1}];

    $scope.carSourceArray = new Array("云鸟","一号货车","58网","快狗","领翔货的","正时达物流","一运全城","个人");
    $scope.taxingPointArray = new Array("6%","6%","6%","6%","6%","6%","4%","无");

    $scope.formData = {
        cityId:$stateParams.cityId,
        depotId:$stateParams.depotId,
        trackerId:$stateParams.trackerId,
        licencePlateNumber:$stateParams.licencePlateNumber,
        vehicleLength:$stateParams.vehicleLength,
        vehicleWidth:$stateParams.vehicleWidth,
        vehicleHeight:$stateParams.vehicleHeight,
        vehicleModel:1,
        weight:$stateParams.weight,
        cubic:$stateParams.cubic,
        name:$stateParams.name,
        expenses:$stateParams.expenses,
        taxingPoint:$stateParams.taxingPoint,
        source:$stateParams.source,
        status:0
    }


    if($rootScope.user) {
        var data = $rootScope.user;
        $scope.cities = data.depotCities;
        if ($scope.cities && $scope.cities.length == 1) {
           $scope.formData.cityId = $scope.cities[0].id;
        }
    }

    $scope.$watch('formData.cityId',function(newVal,oldVal){
        if(newVal){
           $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
               $scope.depots = data;
               if ($scope.depots && $scope.depots.length == 1) {
                   $scope.formData.depotId = $scope.depots[0].id;
               }
           });
           if(typeof oldVal != 'undefined' && newVal != oldVal){
               $scope.formData.depotId = null;
           }
       }else{
           $scope.formData.depotId = null;
       }
    });

    $scope.$watch('formData.cityId',function(newVal,oldVal){
        if(newVal){
            $http({
                url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
                method:"GET",
                params:{"cityId":$scope.formData.cityId}
            })
            .success(function(data) {
                $scope.trackers = data;
            });
        }else{
            $scope.trackers = [];
        }
    });

    $scope.$watch('formData.source' , function(newVal,oldVal){
        if(newVal){
            for(var i=0;i<$scope.carSourceArray.length;i++){
                if($scope.carSourceArray[i] == newVal){
                    $scope.formData.taxingPoint = $scope.taxingPointArray[i];
                    return;
                }
            }
        }
    });

    if($stateParams.id){
        $http({
            url: "/admin/api/car/" + $stateParams.id,
            method: 'GET'
        }).success(function (data) {
            $scope.formData.cityId = data.city.id;
            $scope.formData.depotId = data.depot.id;
            $scope.formData.trackerId = data.adminUser.id;
            $scope.formData.licencePlateNumber = data.licencePlateNumber;
            $scope.formData.vehicleLength = data.vehicleLength;
            $scope.formData.vehicleWidth = data.vehicleWidth;
            $scope.formData.vehicleHeight = data.vehicleHeight;
            $scope.formData.weight = data.weight;
            $scope.formData.cubic = data.cubic;
            $scope.formData.status = data.status;
            $scope.formData.vehicleModel = data.vehicleModel;
            $scope.formData.name = data.name;
            $scope.formData.expenses = data.expenses;
            $scope.formData.source = data.source;
            $scope.formData.taxingPoint = data.taxingPoint;

        }).error(function (data) {
            alert("读取失败!");
        });
    }

    $scope.saveOrUpdateCar = function () {

        if($scope.formData.depotId == null){
            alert("请选择仓库");
            return;
        }

        if($scope.formData.trackerId == null){
            alert("请选择跟车员");
            return;
        }

        if($scope.formData.vehicleModel == null){
            alert("请选择车型");
            return;
        }

        if($scope.formData.status == null){
            alert("请选择是否有效");
            return;
        }

        if($stateParams.id)
            $scope.formData.id = $stateParams.id;

        $http({
            url: "/admin/api/car/saveOrUpdate",
            method: 'POST',
            data: $scope.formData,
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            }
        }).success(function (data) {
            if($stateParams.id){
                alert("更新成功");
            }else{
                alert("保存成功！");
                initFormData();
            }
        }).error(function (data) {
            alert("保存失败!");
        });
    }

    function initFormData(){
        $scope.formData.licencePlateNumber = null;
        $scope.formData.vehicleLength = null;
        $scope.formData.vehicleWidth = null;
        $scope.formData.vehicleHeight = null;
        $scope.formData.weight = null;
        $scope.formData.cubic = null;
        $scope.formData.name = null;
        $scope.formData.source = null;
        $scope.formData.taxingPoint = null;
    }

});

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:CarListCtrl
 * @description
 * # CarListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('CarListCtrl',function($scope, $rootScope, $http, $filter, $state, $stateParams, $location){

    $scope.carSearchForm = {
        cityId:$stateParams.cityId,
        depotId:$stateParams.depotId,
        page: $stateParams.page,
        pageSize: $stateParams.pageSize
    }

    $scope.modelTip = ["暂无","轻型封闭货车","面包","金杯"];
    $scope.statusTip = ["无效","有效"];
    $scope.page = {
        itemsPerPage: 100
    };

    if($rootScope.user) {
       var data = $rootScope.user;
        $scope.cities = data.depotCities;
        if ($scope.cities && $scope.cities.length == 1) {
           $scope.carSearchForm.cityId = $scope.cities[0].id;
        }
    }

    $scope.$watch('carSearchForm.cityId',function(newVal,oldVal){
        if(newVal){
           $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
               $scope.depots = data;
               if ($scope.depots && $scope.depots.length == 1) {
                   $scope.carSearchForm.depotId = $scope.depots[0].id;
               }
           });
           if(typeof oldVal != 'undefined' && newVal != oldVal){
               $scope.carSearchForm.depotId = null;
           }
       }else{
           $scope.depots = [];
           $scope.carSearchForm.depotId = null;
       }
    });

    $scope.search = function(){
        $http({
            url: '/admin/api/car/cars',
            method: "GET",
            params: $scope.carSearchForm
        })
        .success(function(data){
            $scope.cars = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        })
        .error(function(data){
            window.alert("搜索失败...");
        });
    }
});

'use strict';

angular.module('sbAdminApp')
    .config(['uiMapLoadParamsProvider',
        function (uiMapLoadParamsProvider) {
            uiMapLoadParamsProvider.setParams({
                v: '1.5',
                ak: '1507703fda1fb9594c7e7199da8c41d8'
            });
        }])
    .controller('DeliveryMapCtrl', function ($scope, $http, $stateParams) {
        $scope.myMarkers = [];

        if ($stateParams.cityId == 1) {
            $scope.lng = 116.403119;
            $scope.lat = 39.914714;
        } else if ($stateParams.cityId == 2) {
            $scope.lng = 104.072653;
            $scope.lat = 30.664043;
        }

        $scope.mapOptions = {
            ngCenter: {
                lng: $scope.lng,
                lat: $scope.lat
            },
            ngZoom: 12,
            scrollzoom: true
        };

        /*添加marker事件*/
        $scope.addMarker = function (restaurant) {
            if (restaurant.address.wgs84Point) {

                var lon = restaurant.address.wgs84Point.longitude;
                var lat = restaurant.address.wgs84Point.latitude;
                var name = restaurant.name;

                // 自定义覆盖物标签
                var ComplexCustomOverlay = function(point, text){
                  this._point = point;
                  this._text = text;
                }
                ComplexCustomOverlay.prototype = new BMap.Overlay();
                ComplexCustomOverlay.prototype.initialize = function(map){
                  this._map = map;
                  var div = this._div = document.createElement("div");
                  div.style.position = "absolute";
                  div.style.zIndex = BMap.Overlay.getZIndex(this._point.lat);
                  div.style.backgroundColor = "white";
                  div.style.border = "1px solid red";
                  div.style.color = "red";
                  div.style.height = "20px";
                  div.style.padding = "0px";
                  div.style.lineHeight = "20px";
                  div.style.whiteSpace = "nowrap";
                  div.style.fontSize = "12px"
                  var span = this._span = document.createElement("span");
                  div.appendChild(span);
                  span.appendChild(document.createTextNode(this._text));

                  var arrow = this._arrow = document.createElement("div");
                  arrow.style.position = "absolute";
                  arrow.style.width = "11px";
                  arrow.style.height = "10px";
                  arrow.style.top = "22px";
                  arrow.style.left = "10px";
                  arrow.style.overflow = "hidden";
                  div.appendChild(arrow);

                  $scope.myMap.getPanes().labelPane.appendChild(div);

                  return div;
                }

                ComplexCustomOverlay.prototype.draw = function(){
                  var map = this._map;
                  var pixel = map.pointToOverlayPixel(this._point);
                  this._div.style.left = pixel.x + 10 + "px";
                  this._div.style.top  = pixel.y - 22 + "px";
                }

                var point = new BMap.Point(lon, lat);
                var marker = new BMap.Marker(point);

                $scope.myMap.addOverlay(marker);

                //创建信息窗口
                var opts = {
                    width: 200,
                    height: 70,
                    title: "<font style='font-weight:bold;'>[" + name + "]</font>",
                    enableMessage: false
                };
                var infoWindow = new BMap.InfoWindow("地址：" + restaurant.address.address, opts);

                //添加单击事件
                marker.addEventListener("click", function () {
                    $scope.myMap.openInfoWindow(infoWindow, point);
                });

            }
        };

        $scope.$watch('myMap', function (map) {
            if (map) {
                $http({
                    url: '/admin/api/restaurant/delivery',
                    params: {
                        cityId: $stateParams.cityId,
                        organizationId: $stateParams.organizationId,
                        startOrderDate:$stateParams.startOrderDate,
                        endOrderDate:$stateParams.endOrderDate
                    },
                    method: 'GET'
                }).success(function (data) {
                    var restaurants = data.content;
                    for (var i = 0; i < restaurants.length; i++) {
                        $scope.addMarker(restaurants[i]);
                    }
                })
            }
        });
    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderGroupDetailCtrl
 * @description
 * # OrderGroupDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .config(['uiMapLoadParamsProvider',
        function (uiMapLoadParamsProvider) {
            uiMapLoadParamsProvider.setParams({
                v: '1.5',
                ak: '1507703fda1fb9594c7e7199da8c41d8'
            });
        }])
    .controller('OrderGroupDetailCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $state) {
    /*
     * $stateParams.type : 0 新增 | 1:修改
     */

    //--------------------- detail -----------------------
        $scope.allCheckedBlock = false;
        $scope.allUnSelectOrderGroup = false;
        $scope.allSelectOrderGroup = false;

        $scope.ungroupedOrders = []; //未分配的订单
        $scope.groupedOrders = []; //已分配的订单
        $scope.subTotalFilterOrders = []; //过滤总价暂存数组
        $scope.formData = {};
        $scope.formData.name = $filter('date')(new Date(), $scope.format);
        $scope.allCityblocks = [];
        $scope.blocks = [];
        $scope.subTotalFilterValue = 0;
        var markerHashMap = new Object();

        $scope.checkFormData = {
            selectedUngroupedOrders:[], //未分配订单
            selectedGroupedOrders:[],  //已分配订单
            selectBlock:[]  //区块
        };

        $scope.orderGroupSearchForm = {
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId,
            startOrderDate:$stateParams.startOrderDate,
            endOrderDate:$stateParams.endOrderDate
        }

        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };


        $scope.format = 'yyyy-MM-dd HH:mm';
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }


        //默认坐标中心点为北京
        $scope.lng = 116.403119;
        $scope.lat = 39.914714;

        $scope.mapOptions = {
            ngCenter: {lng: $scope.lng, lat: $scope.lat},
            ngZoom: 12,
            scrollzoom: true
        };

        //填充搜索栏城市信息
        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
            }
        }


        //根据城市变化填充仓库信息和区块信息
        $scope.$watch('orderGroupSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               drawMap(); //根据城市变化绘制地图、区块

               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderGroupSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderGroupSearchForm.depotId = null;
               }

               $http.get("/admin/api/city/"+ $scope.orderGroupSearchForm.cityId +"/blocks?status=1")
               .success(function (data, status, headers) {
                   //获取城市下的所有区块
                   $scope.allCityblocks = data;
                   $scope.blocks = data;
               })
               .error(function (data, status, headers) {
                   console.log(status);
                   window.alert("区块获取失败...");
               });

           }else{
               $scope.depots = [];
               $scope.orderGroupSearchForm.depotId = null;
           }
        });

        $scope.$watch('orderGroupSearchForm.depotId',function(newVal,oldVal){
            if(newVal){
               //获取该仓库下的所有市场,再用市场ID和区块ID对比
               $http.get("/admin/api/warehouse/depot/"+ newVal)
               .success(function (data, status, headers) {
                   //获取城市下的所有区块
                   $scope.blocks = [];
                   for(var i=0; i<data.length; i++){
                        var warehouseObj = data[i];
                        for(var j=0; j< $scope.allCityblocks.length; j++){
                            if( $scope.allCityblocks[j].warehouse.id == warehouseObj.id){
                                $scope.blocks.push($scope.allCityblocks[j]);
                            }
                        }
                   }
               })
               .error(function (data, status, headers) {
                   console.log(status);
                   window.alert("市场获取失败...");
               });


                //跟车员
               $http({
                   url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
                   method:"GET",
                   params:$scope.orderGroupSearchForm
               })
               .success(function(data) {
                   $scope.trackers = data;
               });

           }else{
               $scope.trackers = [];
               $scope.blocks = $scope.allCityblocks;
           }
        });


        //查询入口
        $scope.search = function(){
            //查询form赋值
            $scope.orderGroupSearchForm.blockIds = $scope.checkFormData.selectBlock;
            $scope.orderGroupSearchForm.startOrderDate = $filter('date')($scope.start , $scope.format);
            $scope.orderGroupSearchForm.endOrderDate = $filter('date')($scope.end , $scope.format);

            $http({
                url: '/admin/api/ungrouped-order',
                method: 'GET',
                params: $scope.orderGroupSearchForm
            }).success(function (data) {

                $scope.removeAllMarker();
                $scope.ungroupedOrders = []; //未分配的订单
                for(var i=0;i<data.content.length;i++){
                    var flag = true;

                    for(var s=0;s<$scope.groupedOrders.length;s++){
                        if(data.content[i].id == $scope.groupedOrders[s].id){
                            flag = false;
                            break;
                        }
                    }
                    if(flag == true){
                        $scope.ungroupedOrders.push(data.content[i]);
                        var restaurant = data.content[i].restaurant;
                        if (restaurant.address && restaurant.address.wgs84Point) {
                            $scope.addMarker(restaurant , data.content[i].id);
                        }
                    }

                }
            });
        }


        //区块全选
        $scope.clickAllBlock = function(){
             $scope.allCheckedBlock = !$scope.allCheckedBlock;
             if($scope.allCheckedBlock){
                 for(var i=0; i<$scope.blocks.length; i++){
                    $scope.checkFormData.selectBlock.push($scope.blocks[i].id);
                 }
             }else{
                $scope.checkFormData.selectBlock = [];
             }
        }

        //金额过滤
        $scope.subTotalFilter = function(){
            if($scope.subTotalFilterValue == undefined){
                alert("请输入一个正数");
                return;
            }
            for (var i = $scope.ungroupedOrders.length-1; i >=0; i--) {
//                alert("sss : " + JSON.stringify($scope.ungroupedOrders[i]));
                if($scope.subTotalFilterValue > $scope.ungroupedOrders[i].subTotal){
                    $scope.subTotalFilterOrders.push($scope.ungroupedOrders[i]);
                    $scope.ungroupedOrders.splice(i, 1);
                }
            }

            for (var j = $scope.subTotalFilterOrders.length-1; j >= 0; j--){
                if($scope.subTotalFilterValue <= $scope.subTotalFilterOrders[j].subTotal){
                    $scope.ungroupedOrders.push($scope.subTotalFilterOrders[j]);
                    $scope.subTotalFilterOrders.splice(j, 1);
                }
            }
        }

        //未分配订单全选
        $scope.clickAllUnSelectOrderGroup = function(){
             $scope.allUnSelectOrderGroup = !$scope.allUnSelectOrderGroup;
             if($scope.allUnSelectOrderGroup == true){
                $scope.checkFormData.selectedUngroupedOrders = [];
                for (var i = 0; i < $scope.ungroupedOrders.length; i++) {
                    $scope.checkFormData.selectedUngroupedOrders.push($scope.ungroupedOrders[i].id);
                }
             }else{
                $scope.checkFormData.selectedUngroupedOrders = [];
             }
        }



        //已分配订单全选
        $scope.clickAllSelectOrderGroup = function(){
             $scope.allSelectOrderGroup = !$scope.allSelectOrderGroup;
             if($scope.allSelectOrderGroup == true){
                for (var i = 0; i < $scope.groupedOrders.length; i++) {
                    $scope.checkFormData.selectedGroupedOrders.push(eval($scope.groupedOrders[i].id));
                }
             }else{
                 $scope.checkFormData.selectedGroupedOrders = [];
             }
        }


        //未分配订单 TO 已分配订单
        $scope.selectOrders = function () {
            for (var i = 0; i < $scope.checkFormData.selectedUngroupedOrders.length; i++) {
                for (var j = $scope.ungroupedOrders.length - 1; j >= 0; j--) {
                    if ($scope.ungroupedOrders[j].id == $scope.checkFormData.selectedUngroupedOrders[i]) {
                        //删除相应的marker
                        if ($scope.ungroupedOrders[j].restaurant) {
                            $scope.removeMarker($scope.ungroupedOrders[j].id);
                        }
                        $scope.groupedOrders.push($scope.ungroupedOrders[j]);
                        $scope.ungroupedOrders.splice(j, 1);
                        break;
                    }
                }
            }
            $scope.checkFormData.selectedUngroupedOrders = [];
            $scope.allUnSelectOrderGroup = false;
            $scope.allSelectOrderGroup = false;
        }

        //已分配订单 TO 未分配订单
        $scope.unselectOrders = function () {
            for (var i = 0; i < $scope.checkFormData.selectedGroupedOrders.length; i++) {
                for (var j = $scope.groupedOrders.length - 1; j >= 0; j--) {
                    if ($scope.groupedOrders[j].id == $scope.checkFormData.selectedGroupedOrders[i]) {
                        //撤销餐馆订单同时添加相应marker
                        if ($scope.groupedOrders[j].restaurant && $scope.groupedOrders[j].restaurant.address.wgs84Point) {
                            $scope.addMarker($scope.groupedOrders[j].restaurant , $scope.groupedOrders[j].id);
                        }
                        $scope.ungroupedOrders.push($scope.groupedOrders[j]);
                        $scope.groupedOrders.splice(j, 1);
                        break;
                    }
                }
            }
            $scope.checkFormData.selectedGroupedOrders = [];
            $scope.allUnSelectOrderGroup = false;
            $scope.allSelectOrderGroup = false;
        }


        $scope.tracker = {};

        //记录跟车员
        $scope.$watch('tracker.selected', function (newVal , oldVal) {
            if (newVal) {
                $scope.formData.trackerId = newVal.id;
                //根据跟车员查询车辆信息
                $http({
                   url : '/admin/api/car/cars',
                   method:"GET",
                   params:{"trackerId":$scope.formData.trackerId,"status":1}
                })
                .success(function(data) {
                    $scope.cars = data.content;
                });
            }
        })

        //订单包编辑状态下数据回显
        if($stateParams.type == 1){
            $http({
                url: "/admin/api/order-group/" + $stateParams.id,
                method: 'GET'
            }).success(function (data) {
                $scope.orderGroupSearchForm.cityId = data.city.id;
                $scope.orderGroupSearchForm.depotId = data.depot.id;
                $scope.tracker.selected = data.tracker;
                $scope.formData.trackerId = data.tracker.id;
                $scope.formData.name = data.name;
                $scope.groupedOrders = data.members;
            }).error(function (data) {
                alert("获取订单状态失败!");
            });
        }

        $scope.saveOrderGroup = function () {
            if($scope.formData.trackerId == null){
                alert("请选择跟车员");
                return;
            }
            $scope.formData.orderIds = [];
            $scope.formData.orderIds = $scope.groupedOrders.map(function (order) {
                return order.id;
            });

            $scope.formData.cityId = $scope.orderGroupSearchForm.cityId; //城市
            $scope.formData.depotId = $scope.orderGroupSearchForm.depotId; //仓库

            $http({
                url: "/admin/api/order-group/" + $stateParams.id,
                method: 'PUT',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                //保存或者更新
                if (data.name) {
                    $scope.formData.name = data.name;
                } else {
                    $scope.formData.name = $filter('date')(new Date(), $scope.format);
                }

                if (data.tracker) {
                    $scope.formData.trackerId = data.tracker.id;
                    $scope.tracker.selected = data.tracker;
                }

                if($stateParams.type == 1){
                    alert("更新成功");
                }else {
                    alert("保存成功");
                     $scope.groupedOrders = []; //情况已分配订单
                     $scope.createOrderGroup(); //不刷新页面情况下连续添加
                     $scope.search();
                }
            }).error(function (data) {
                alert("操作失败!");
            });
        }

        $scope.createOrderGroup = function() {
            $http({
                url: "/admin/api/order-group/",
                method: 'POST',
                data: {"depotId":null},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                $stateParams.id = data.id;
            });
        }

        //------------------------- 地图 ----------------------------



        function drawMap(){
            /*基于地图绘制显示区块*/
            $scope.$watch('myMap', function (map) {
                if(map){
                     if ($scope.orderGroupSearchForm.cityId) {
                        if ($scope.orderGroupSearchForm.cityId == 1) {
                            $scope.lng = 116.403119;
                            $scope.lat = 39.914714;
                        } else if ($scope.orderGroupSearchForm.cityId == 2) {
                            $scope.lng = 104.072653;
                            $scope.lat = 30.664043;
                        } else if ($scope.orderGroupSearchForm.cityId == 3) {
                            $scope.lng = 120.18469;
                            $scope.lat = 30.267334;
                        } else if ($scope.orderGroupSearchForm.cityId == 4) {
                            $scope.lng = 117.024316;
                            $scope.lat = 36.667227;
                        }


                        $scope.myMap.centerAndZoom(new BMap.Point($scope.lng,$scope.lat), 12);  //创建中心点,缩放等级
                        $scope.myMap.setCenter(new BMap.Point($scope.lng,$scope.lat));
                        $http.get("/admin/api/city/"+ $scope.orderGroupSearchForm.cityId +"/simpleBlocks?status=1")
                        .success(function (data, status, headers) {
                            $scope.blocksMap = data;

                            if ($scope.blocksMap && $scope.blocksMap.length > 0) {
                                for (var i=0; i < $scope.blocksMap.length; i++) {
                                    var pointObjects = [];
                                    var borderColorArr = ["blue","green","purple","yellow","orange","pink","dark","fuchsia","crimson","greenyellow"];
                                    var strokeColor = borderColorArr[parseInt(Math.random()*9)];

                                    if ($scope.blocksMap[i].points && $scope.blocksMap[i].points.length > 0) {
                                        for (var j=0; j < $scope.blocksMap[i].points.length; j++) {
                                            var lng = $scope.blocksMap[i].points[j].longitude;
                                            var lat = $scope.blocksMap[i].points[j].latitude;
                                            pointObjects.push(new BMap.Point(lng,lat));
                                        }
                                    }

                                    //创建区块多边形
                                    var polygon = new BMap.Polygon(pointObjects, {strokeColor:strokeColor, strokeWeight:1, strokeOpacity:1});
                                    polygon.setFillOpacity(0.001);
                                    $scope.addEventListenerToPolygon(polygon,strokeColor,$scope.blocksMap[i].name);
                                    $scope.myMap.addOverlay(polygon);
                                }
                            }
                        })
                        .error(function (data, status, headers) {
                            console.log(status);
                            window.alert("区块获取失败...");
                        });
                    }

                }
            });
        }


        /*添加marker事件*/
        $scope.addMarker = function (restaurant , orderId) {
            var lon = restaurant.address.wgs84Point.longitude;
            var lat = restaurant.address.wgs84Point.latitude;
            var address = restaurant.address.address;
            var name = restaurant.name;
            var restaurantId = restaurant.id;

            var point = new BMap.Point(lon, lat);
            var marker = new BMap.Marker(point);
            markerHashMap[orderId] = marker;

            $scope.myMap.addOverlay(marker);

            //创建信息窗口
            var opts = {
                width: 200,
                height: 70,
                title: "<font style='font-weight:bold;'>[" + name + "]</font>",
                enableMessage: false
            };
            var infoWindow = new BMap.InfoWindow("地址：" + address, opts);

            //添加单击事件
            marker.addEventListener("click", function () {
                $scope.myMap.openInfoWindow(infoWindow, point);
            });

            //创建右键菜单
            var markerMenu = new BMap.ContextMenu();
            var boundAssignRestaurantToGroup = $scope.assignRestaurantToGroup.bind(marker, restaurantId);
            //绑定菜单事件
            markerMenu.addItem(new BMap.MenuItem('配送', boundAssignRestaurantToGroup));
            marker.addContextMenu(markerMenu);
        };

        //删除当前已经添加到map上全部marker
        $scope.removeAllMarker = function (){
            for(var key in markerHashMap){
                 $scope.myMap.removeOverlay(markerHashMap[key]);
            }
            markerHashMap = new Object();
        }

        $scope.addEventListenerToPolygon = function (polygon, strokeColor, blockName) {
            //鼠标在区块多边形上移动时边线变红加粗
            polygon.addEventListener("mousemove", function(e) {
                polygon.setStrokeColor("red");
                polygon.setStrokeWeight(2);
            });

            //鼠标在区块多边形上移出时边线复原
            polygon.addEventListener("mouseout", function(e) {
                polygon.setStrokeColor(strokeColor);
                polygon.setStrokeWeight(1);
            });

            //点击区块显示区块名称
            polygon.addEventListener("click", function(e) {
                alert(blockName);
            });
        };


        //删除marker事件
        $scope.removeMarker = function (orderId) {
             $scope.myMap.removeOverlay(markerHashMap[orderId]);
             delete markerHashMap[orderId];
        }

        /*右键配送事件*/
        $scope.assignRestaurantToGroup = function (restaurantId) {
            for (var j = $scope.ungroupedOrders.length - 1; j >= 0; j--) {
                if ($scope.ungroupedOrders[j].restaurant.id == restaurantId) {

                    $scope.removeMarker( $scope.ungroupedOrders[j].id);
                    //添加到“已分配订单”多选下拉框中
                    $scope.groupedOrders.push($scope.ungroupedOrders[j]);
                    $scope.ungroupedOrders.splice(j, 1);
                }
            }
            $scope.$apply();
        }


    //----------------------------------------------------

        $scope.selectedRestaurantIds = function () {
            var restaurantIds = [];
            for (var i = 0; i < $scope.groupedOrders.length; i++) {
                restaurantIds.push($scope.groupedOrders[i].restaurant.id);
            }

            return restaurantIds
        };

        $scope.$watch('ungroupedOrders' , function(data){

            var sum = 0;
            $scope.unGroupedOrdersTotal = 0;
            $scope.unGroupedOrdersCount = 0;
            $scope.unGroupedOrdersWight = 0.0;
            $scope.unGroupedOrdersTotalVolume = 0.0;
            $scope.unGroupedOrdersQuantity = 0;

            for(var i = 0; i < data.length; i++) {
                sum += data[i].total;
                $scope.unGroupedOrdersCount += 1;
                $scope.unGroupedOrdersWight += data[i].totalWight;
                $scope.unGroupedOrdersTotalVolume += data[i].totalVolume;
                $scope.unGroupedOrdersQuantity += data[i].quantity;
            }

            $scope.unGroupedOrdersTotal = sum.toFixed(2);
        }, true);


        //计算已选择配送订单合计
        $scope.$watch('groupedOrders', function(data) {
            var sum = 0;
            $scope.groupedOrdersTotal = 0;
            $scope.groupedOrdersCount = 0;
            $scope.groupedOrdersWight = 0.0;
            $scope.groupedOrdersTotalVolume = 0.0;
            $scope.groupedOrdersQuantity = 0;

            for(var i = 0; i < data.length; i++) {
                sum += data[i].total;
                $scope.groupedOrdersCount += 1;
                $scope.groupedOrdersWight += data[i].totalWight;
                $scope.groupedOrdersTotalVolume += data[i].totalVolume;
                $scope.groupedOrdersQuantity += data[i].quantity;
            }

            $scope.groupedOrdersTotal = sum.toFixed(2);
        }, true);


    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('OrderGroupListCtrl',function($scope, $q, $rootScope, $http, $filter, $state, $stateParams, $location){

        $scope.orderGroupSearchForm = {
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId,
            trackerId:$stateParams.trackerId,
            startOrderDate:$stateParams.startOrderDate,
            endOrderDate:$stateParams.endOrderDate,
            queryDateType:1,
            page: $stateParams.page,
            pageSize: $stateParams.pageSize
        }

        $scope.page = {
            itemsPerPage: 100
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };


        $scope.format = 'yyyy-MM-dd HH:mm';
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.orderGroups = [];

        $scope.sizeOfUngroupedOrders = 0;

        /*订单列表搜索表单*/
//        $scope.expectedArrivedDate = $filter('date')(new Date(),'yyyy-MM-dd');
        /*$scope.orderGroupSearchForm = {
            expectedArrivedDate : $filter('date')($scope.expectedArrivedDate ,'yyyy-MM-dd')
        };*/

        if ($stateParams.cityId) {
            $scope.orderGroupSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.depotId) {
            $scope.orderGroupSearchForm.depotId = parseInt($stateParams.depotId);
        }
        if ($stateParams.trackerId){
            $scope.orderGroupSearchForm.trackerId = parseInt($stateParams.trackerId);
        }
        if ($stateParams.startOrderDate) {
            $scope.start = $filter('date')($stateParams.startOrderDate, $scope.format);
        }
        if ($stateParams.endOrderDate) {
            $scope.end = $filter('date')($stateParams.endOrderDate, $scope.format);
        }

        $scope.$watch('start', function(d) {
            $scope.orderGroupSearchForm.startOrderDate = $filter('date')(d, $scope.format);
        });
        $scope.$watch('end', function(d) {
            $scope.orderGroupSearchForm.endOrderDate = $filter('date')(d, $scope.format);
        });

        $scope.findUngroupedOrder = function () {
            if($scope.orderGroupSearchForm.cityId) {
                var unGroupOrderSearchForm = {
                        cityId:$scope.orderGroupSearchForm.cityId,
                        depotId:$scope.orderGroupSearchForm.depotId,
                        page: $scope.orderGroupSearchForm.page,
                        pageSize: $scope.orderGroupSearchForm.pageSize
                    };
                $http({
                        url: '/admin/api/ungrouped-order/size',
                        method: 'GET',
                        params: unGroupOrderSearchForm
                    }
                ).success(function (data) {
                        $scope.sizeOfUngroupedOrders = data;
                });
            }
        }

		$scope.searchForm = function () {
		    $scope.findUngroupedOrder();
		    $location.search($scope.orderGroupSearchForm);
		}

        if($scope.orderGroupSearchForm.cityId && $scope.orderGroupSearchForm.startOrderDate && $scope.orderGroupSearchForm.endOrderDate) {
            $http({
                    url: '/admin/api/order-group',
                method: "GET",
                params: $scope.orderGroupSearchForm
            })
            .success(function(data,status,headers,congfig){
                $scope.orderGroups = data.content;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function(data,status,headers,config){
                window.alert("搜索失败...");
            });
        }

        $scope.searchForm();

        $scope.search = function() {
            if($scope.orderGroupSearchForm.cityId) {
                $state.go($state.current, $scope.orderGroupSearchForm, {reload: true});
            }
        }

        $scope.pageChanged = function() {
            $scope.orderGroupSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderGroupSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchForm();
        }

        $scope.createOrderGroupAndJump = function() {
            $http({
                url: "/admin/api/order-group/",
                method: 'POST',
                data: {"depotId":null},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                $state.go("oam.order-group-detail", {id: data.id , type:0});

            }).error(function (data) {
                alert("提交失败!");
            });
            /*$state.go("oam.order-group-detail", {cityId:cityId, depotId:depotId, startOrderDate:startOrderDate, endOrderDate:endOrderDate});*/
        }

        $scope.$watch('orderGroupSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderGroupSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderGroupSearchForm.depotId = null;
               }

           }else{
               $scope.depots = [];
               $scope.orderGroupSearchForm.depotId = null;
           }
        });

        $scope.$watch('orderGroupSearchForm.depotId',function(newVal,oldVal){
            if(newVal){
                //-------------- 仓库下线路数据 -------------------
                $http({
                    url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
                    method:"GET",
                    params:$scope.orderGroupSearchForm
                })
                .success(function(data) {
                    $scope.trackers = data;
                });

            }else{
                $scope.trackers = [];
                $scope.trackers = null;
            }
        });

        //-------------- 线路数据初始化 -------------------
        //$http({
        //    url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
        //    method:"GET"
        //})
        //.success(function(data) {
        //    $scope.trackers = data;
        //});



        function isLock(id){
            var defer = $q.defer();
            $http({
                url: "/admin/api/order-group/" + id,
                method: 'GET'
            }).success(function (data) {
                console.log(JSON.stringify(data));
                defer.resolve(data.lock == 1);
            }).error(function (data) {
                alert("获取订单状态失败!");
                defer.resolve(true);
            });
            return defer.promise;
        }

        $scope.editOrderGroup = function (id){
            isLock(id).then(function(result){
                if(result){
                    alert("订单已经出库,不能编辑");
                    return ;
                }else{
                    $state.go("oam.order-group-detail", {id: id,type:1});
                }
            })
        }

        //取消分车
        $scope.removeOrderGroup = function (id){
            isLock(id).then(function(result){
                if(result){
                    alert("订单已经出库,不能取消分车");
                    return ;
                }else{
                    $scope.formData = {};
                    $scope.formData.orderIds = [];
                    $scope.formData.cityId = null;
                    $scope.formData.depotId = null;
                    $scope.formData.trackerId = null;
                    $scope.formData.name = null;

                    $http({
                        url: "/admin/api/order-group/" + id,
                        method: 'PUT',
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    }).success(function (data) {
                        alert("取消成功！");
                        $scope.search();
                    }).error(function (data) {
                        alert("取消失败!");
                    });
                }
            })
        }

	});

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderGroupDetailCtrl
 * @description
 * # OrderGroupDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .config(['uiMapLoadParamsProvider',
        function (uiMapLoadParamsProvider) {
            uiMapLoadParamsProvider.setParams({
                v: '1.5',
                ak: '1507703fda1fb9594c7e7199da8c41d8'
            });
        }])
    .controller('OrderGroupMapCtrl', function ($scope, $http, $stateParams) {
        $scope.myMarkers = [];
        if ($stateParams.cityId == 1) {
            $scope.lng = 116.403119;
            $scope.lat = 39.914714;
        } else if ($stateParams.cityId == 2) {
            $scope.lng = 104.072653;
            $scope.lat = 30.664043;
        } else if ($stateParams.cityId == 3){
            $scope.lng = 120.219375;
            $scope.lat = 30.259244;
        } else if ($stateParams.cityId ==  4){
            $scope.lng = 117.024967;
            $scope.lat = 36.682785;
        } else if ($stateParams.cityId == 5){
            $scope.lng = 125.313642;
            $scope.lat = 43.898338;
        }

        $scope.mapOptions = {
            ngCenter: {
                lng: $scope.lng,
                lat: $scope.lat
            },
            ngZoom: 12,
            scrollzoom: true
        };

        //地图上显示的点位
        var marker = {
            lon:null,
            lat:null,
            name:null
        }


        /*添加marker事件*/
        $scope.addMarker = function (marker) {
            if (marker.lon && marker.lat) {

                var lon = marker.lon;
                var lat = marker.lat;
                var name = marker.name;

                // 自定义覆盖物标签
                var ComplexCustomOverlay = function(point, text){
                  this._point = point;
                  this._text = text;
                }
                ComplexCustomOverlay.prototype = new BMap.Overlay();
                ComplexCustomOverlay.prototype.initialize = function(map){
                  this._map = map;
                  var div = this._div = document.createElement("div");
                  div.style.position = "absolute";
                  div.style.zIndex = BMap.Overlay.getZIndex(this._point.lat);
                  div.style.backgroundColor = "white";
                  div.style.border = "1px solid red";
                  div.style.color = "red";
                  div.style.height = "20px";
                  div.style.padding = "0px";
                  div.style.lineHeight = "20px";
                  div.style.whiteSpace = "nowrap";
                  div.style.fontSize = "12px"
                  var span = this._span = document.createElement("span");
                  div.appendChild(span);
                  span.appendChild(document.createTextNode(this._text));

                  var arrow = this._arrow = document.createElement("div");
                  arrow.style.position = "absolute";
                  arrow.style.width = "11px";
                  arrow.style.height = "10px";
                  arrow.style.top = "22px";
                  arrow.style.left = "10px";
                  arrow.style.overflow = "hidden";
                  div.appendChild(arrow);

                  $scope.myMap.getPanes().labelPane.appendChild(div);

                  return div;
                }

                ComplexCustomOverlay.prototype.draw = function(){
                  var map = this._map;
                  var pixel = map.pointToOverlayPixel(this._point);
                  this._div.style.left = pixel.x + 10 + "px";
                  this._div.style.top  = pixel.y - 22 + "px";
                }

                var myCompOverlay = new ComplexCustomOverlay(new BMap.Point(lon, lat), name);

                $scope.myMap.addOverlay(myCompOverlay);

                var point = new BMap.Point(lon, lat);
                var marker = new BMap.Marker(point);

                $scope.myMap.addOverlay(marker);

            }
        };

//        $scope.$watch('myMap', function (map) {
//            if (map) {
//                if ($stateParams.rid) {
//                    $http({
//                        url: '/admin/api/restaurant/batch',
//                        params: {
//                            restaurantId: $stateParams.rid
//                        },
//                        method: 'GET'
//                    }).success(function (data) {
//                        for (var i = 0; i < data.length; i++) {
//                            $scope.addMarker(data[i]);
//                        }
//                    })
//                }
//            }
//        });


         $scope.$watch('myMap', function (map) {
            if (map) {
                if($stateParams.type == 1){
                    //订单包下订单分布图
                   $http({
                       url : '/admin/api/order-group/'+$stateParams.id,
                       method:"GET"
                   })
                   .success(function(data) {
                       for(var i = 0; i < data.members.length; i++){
                            var orderItem = data.members[i];
                            var restaurant = orderItem.restaurant;
                            marker.name = restaurant.name;
                            marker.lon = restaurant.address.wgs84Point.longitude;
                            marker.lat = restaurant.address.wgs84Point.latitude;
                            $scope.addMarker(marker);
                       }
                   });

                }else if($stateParams.type == 2){
                    //订单已经分配的地图
                    $http({
                        url: '/admin/api/restaurant/batch',
                        params: {
                            restaurantId: $stateParams.id
                        },
                        method: 'GET'
                    }).success(function (data) {
                        for (var i = 0; i < data.length; i++) {
                            marker.name = data[i].name;
                            marker.lon = data[i].address.wgs84Point.longitude;
                            marker.lat = data[i].address.wgs84Point.latitude;
                            $scope.addMarker(marker);
                        }
                    })
                }else if($stateParams.type == 3){
                    //查看配送地图
                    if($stateParams.cityId == null){
                        alert("请选择城市");
                        window.close();
                        return;
                    }
                    $http({
                        url: '/admin/api/restaurant/delivery',
                        params: {
                            cityId: $stateParams.cityId,
                            orderStatus:-3
                        },
                        method: 'GET'
                    }).success(function (data) {
                        var restaurants = data.content;
                        for (var i = 0; i < restaurants.length; i++) {
                            if(restaurants[i].address.wgs84Point){
                                marker.name = restaurants[i].name;
                                marker.lon = restaurants[i].address.wgs84Point.longitude;
                                marker.lat = restaurants[i].address.wgs84Point.latitude;
                                $scope.addMarker(marker);
                            }
                        }
                    })
                }else if($stateParams.type == 4){
                    //截单地图
                    $scope.orderListSearchForm = {
                        page: 0,
                        pageSize: 1000,
                        start: $stateParams.start,
                        end: $stateParams.end,
                        customerId: $stateParams.customerId,
                        restaurantId: $stateParams.restaurantId,
                        restaurantName: $stateParams.restaurantName,
                        warehouseId: $stateParams.warehouseId,
                        vendorName:$stateParams.vendorName,
                        cityId:$stateParams.cityId,
                        organizationId:$stateParams.organizationId,
                        vendorId: $stateParams.vendorId,
                        orderId:$stateParams.orderId,
                        coordinateLabeled:$stateParams.coordinateLabeled,
                        refundsIsNotEmpty:$stateParams.refundsIsNotEmpty,
                        depotId:$stateParams.depotId,
                        blockId:$stateParams.blockId,
                        orderType:$stateParams.orderType,
                        status:3
                    };

                    $http({
                       url: '/admin/api/order',
                       method: "GET",
                       params: $scope.orderListSearchForm
                    }).success(function (data, status, headers, config) {
                       for (var i = 0; i < data.orders.length; i++) {
                           var restaurant = data.orders[i].restaurant;
                           if(restaurant.address.wgs84Point){
                               marker.name = restaurant.name;
                               marker.lon = restaurant.address.wgs84Point.longitude;
                               marker.lat = restaurant.address.wgs84Point.latitude;
                               $scope.addMarker(marker);
                           }
                       }
                    }).error(function (data, status, headers, config) {
                       window.alert("搜索失败...");
                    });

                }
            }
        });


    });
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderGroupDetailCtrl
 * @description
 * # OrderGroupDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderGroupViewCtrl', function ($scope, $http, $filter, $stateParams) {
        $scope.totalVolume = 0;
        $scope.totalWight = 0;
        $scope.quantity = 0;

        if ($stateParams.id) {

           $http({
               url : '/admin/api/order-group/'+$stateParams.id,
               method:"GET"
           })
           .success(function(data) {
               $scope.orderGroupModel = data;
//               console.log(JSON.stringify(data.members));

               for(var i = 0; i < data.members.length; i++){
                    var orderItem = data.members[i];
                    $scope.quantity += orderItem.quantity;
                    $scope.totalVolume += orderItem.totalVolume;
                    $scope.totalWight += orderItem.totalWight;
               }
           });
        }
    });

'use strict';

angular.module('sbAdminApp')
    .controller('UpdateBrandCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.brandForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/brand/" + $stateParams.id).success(function (data) {
                $scope.brandForm = data;
                if ($scope.brandForm.status == "有效") {
                    $scope.brandForm.status = 1;
                } else {
                    $scope.brandForm.status = 0;
                }
            })
        } else {
            $scope.brandForm.status = 1;
        }
        $scope.updateBrand = function () {
            $http({
                method: 'POST',
                url: '/admin/api/brand/update',
                data: $scope.brandForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
            }).error(function () {
                alert("操作失败...");
            })
        }

    });
'use strict';

angular.module('sbAdminApp')
    .controller('BrandListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.searchForm = {
            pageSize: 100
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $http({
            url: "/admin/api/brand/list",
            method: 'GET',
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.brands = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.searchForm);
        };
    });
'use strict';
 angular.module('sbAdminApp')
 .controller('updateVendorPasswordCtrl', function($scope, $http, $stateParams) {

 	$scope.updateVendorPassword = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/vendor/updateVendorPassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
			alert("修改成功!");
 		})
 		.error(function() {
 			alert("修改失败!");
 		});
 	};
 });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SupplierDetailCtrl
 * @description
 * # SupplierDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('VendorDetailCtrl', function ($scope, $rootScope, $http, $stateParams, AlertService) {
        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.$watch('vendorFormData.city.id', function(cityId,old) {
            if(cityId) {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;

                    if (typeof old != 'undefined') {
                        $scope.vendorFormData.organization.id = null;
                    }
                });
            } else {
                $scope.organizations = [];
            }
        });

        $scope.$watch('vendorFormData.city.id', function(newVal, oldVal) {
            if(newVal) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId: newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        /*供应商添加/编辑form*/
        $scope.vendorFormData = {};

        /*根据供应商id获取编辑信息*/
        if ($stateParams.id) {

            $http.get("/admin/api/vendor/" + $stateParams.id)
                .success(function (data, status, headers, config) {
                    $scope.vendorFormData = data;
                })
                .error(function (data, status, headers, congfig) {
                    window.alert("获取供应商信息失败...");
                })
        }

        /*提交添加/编辑供应商表单数据*/
        $scope.saveVendor = function () {
            if ($stateParams.id == '') {
                $http({
                    url: '/admin/api/vendor',
                    method: 'POST',
                    data: $scope.vendorFormData,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status) {
                        window.alert("供货商信息添加成功！");
                    })
                    .error(function () {
                        window.alert("添加失败...");
                    })
            } else {
                $http({
                    url: '/admin/api/vendor/' + $stateParams.id,
                    method: 'PUT',
                    data: $scope.vendorFormData,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status) {
                        alert("修改成功！");
                    })
                    .error(function (data, status) {
                        alert("修改失败...");
                    })
            }
        }

        $scope.vendorFilter = function(item) {
            return item.city.id == $scope.vendorFormData.city.id;
        }

    });
'use strict';

angular.module('sbAdminApp')
	.controller('VendorListCtrl',function($scope, $rootScope, $http, $stateParams, $location){

		$scope.page = {
			itemsPerPage: 100
		};

		$scope.formData = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.formData.page = parseInt($stateParams.page);
		}

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
             if ($scope.cities && $scope.cities.length == 1) {
				$scope.formData.cityId = $scope.cities[0].id;
			 }
        }

		$scope.$watch('formData.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.formData.organizationId = $scope.organizations[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.formData.organizationId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.formData.organizationId = null;
		   }
		});

		$scope.$watch('formData.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.formData.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.candidateVendors = data.vendors;
				});
			} else {
				$scope.candidateVendors = [];
			}
		});


		$http({
			url : "/admin/api/vendor",
			method : 'GET',
			params: $scope.formData
		}).success(function(data){
			$scope.vendors = data.vendors;
			$scope.page.itemsPerPage = data.pageSize;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		}).error(function(data){

		});

		$scope.pageChanged = function() {
			$scope.formData.page = $scope.page.currentPage - 1;
			$location.search($scope.formData);
		}

		$scope.search = function () {
			$scope.formData.page = 0;
			$location.search($scope.formData);
		}
	});
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AlarmRestaurantCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location) {

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd 00:00';

        $scope.restaurantSearchForm = {
            orderDate : $stateParams.orderDate
        }

        $scope.$watch('orderDate', function(newVal) {
            if(newVal){
                $scope.startDate = $filter('date')(newVal, $scope.format);
                var date = $filter('date')(newVal, $scope.format);
                date = new Date(date);
                date = date.setDate(date.getDate() + 1);
                $scope.endDate = $filter('date')(date, $scope.format);
                $scope.restaurantSearchForm.orderDate = $filter('date')(newVal, $scope.format);
            }
        });

        if($scope.restaurantSearchForm.orderDate) {
            $scope.orderDate = Date.parse($scope.restaurantSearchForm.orderDate);
        }

        $http({
                url: "/admin/api/restaurant/alarm",
                method: "GET",
                params: $scope.restaurantSearchForm
            })
            .success(function (data, status, headers, config) {
                $scope.restaurants = data.restaurants;
                $scope.alarmCount = data.alarmCount;
            })
            .error(function (data, status, headers, config) {
                alert("加载失败...");
            });

        $scope.resetPageAndSearchForm = function() {
            $location.search($scope.restaurantSearchForm);
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('EditRestaurantCtrl', function($scope, $rootScope, $http, $stateParams) {

 	if($rootScope.user) {
		var data = $rootScope.user;
		 $scope.cities = data.cities;
	}

    $http.get("/admin/api/restaurant/status")
     .success(function(data) {
         $scope.availableStatus = data;
     });

 	$http.get("/admin/api/restaurantType/parent")
	  .success(function(data) {
		  $scope.restaurantType = data;
	  });

	 $http.get("/admin/api/restaurant/reasons")
		 .success(function(data) {
			 $scope.restaurantReasons = data;
		 });


 	/*修改餐馆-表单数据集*/
 	$scope.formData = {
 	};
    $scope.restaurant = {};


	$scope.$watch('formData.type', function(newVal, oldVal){
		if (newVal != null && newVal != '') {
			$http({
				method:"GET",
				url:"/admin/api/restaurantType/"+ newVal +"/child",
				params: {status:1}
			})
			.success(function(data, status, headers, config) {
				$scope.restaurantType2 = data;
				if (data.length == 1) {
					$scope.formData.type2 = data[0].id;
				}
			})
			.error(function(data, status, headers, config) {
				window.alert("加载失败！");
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.type2 = null;
			}
		} else {
			 $scope.restaurantType2 = [];
			 $scope.formData.type2 = null;
		}
	});

	$scope.$watch('formData.cityId', function(newVal, oldVal){

		if(newVal != null && newVal != '') {
			$http({
				method:"GET",
				url: "/admin/api/city/"+ newVal +"/blocks",
				params: {status:true},
				headers: {
					'Content-Type': 'application/json;charset=UTF-8'
				}
			})
			.success(function(data, status, headers, config) {
				$scope.blocks = data;
				if (data.length == 1) {
					$scope.formData.blockId = data[0].id;
				}
			})
			.error(function(data, status, headers, config) {
				window.alert("加载失败！");
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.blockId = null;
			}
		} else {
			$scope.blocks = [];
			$scope.formData.blockId = null;
		}
	});

 	/*餐馆编辑信息view*/
 	if ($stateParams.id != "") {
 		$http.get("/admin/api/restaurant/" + $stateParams.id)
 		.success(function(data, status, headers, config) {

            $scope.restaurant = data;

 			$scope.formData.name = data.name;

			if (data.customer) {
				$scope.formData.cityId = data.customer.cityId;
			}

			if (data.customer.block) {
				$scope.formData.blockId = data.customer.block.id;
				$scope.formData.cityId = data.customer.block.city.id;
			}

 			$scope.formData.contact = data.receiver;
 			$scope.formData.telephone = data.telephone;


			if (data.address) {
 				$scope.formData.address = data.address.address;
			    $scope.formData.streeNumer = data.address.streeNumer;

				if (data.address.wgs84Point) {
					$scope.formData.wgs84Point =  data.address.wgs84Point.longitude + "," + data.address.wgs84Point.latitude;
				};
			}

			if (data.status) {
 				$scope.formData.status = data.status.value;
			}
			if (data.type) {
 				$scope.formData.type = data.type.parentRestaurantTypeId;
 				$scope.formData.type2 = data.type.id;
			}
			if (data.restaurantReason) {
				$scope.formData.restaurantReason = data.restaurantReason.value;
			}
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("加载失败！");
 		});
 	}

 	/*修改餐馆-提交表单数据请求update*/
 	$scope.updateRestaurant = function() {
 		$http({
 			method: 'PUT',
 			url: '/admin/api/restaurant/' + $stateParams.id,
 			data: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("提交成功!");
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("提交失败！");
 		});
 	};
 })

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RestaurantManagementCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

        /*搜索表单数据*/
        $scope.restaurantSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            adminUserId: $stateParams.adminUserId,
            name: $stateParams.name,
            telephone: $stateParams.telephone,
            start: $stateParams.start,
            end: $stateParams.end,
            warehouseId: $stateParams.warehouseId,
            id: $stateParams.id,
            registPhone: $stateParams.registPhone,
            blankTime: $stateParams.blankTime,
            cityId: $stateParams.cityId,
            grade: $stateParams.grade,
            warning: $stateParams.warning,
            neverOrder:$stateParams.neverOrder
        };

        /*获取可选状态*/
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.restaurantSearchForm.cityId = $scope.cities[0].id;
            }
            if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                $scope.restaurantSearchForm.warehouseId = $scope.availableWarehouses[0].id;
            }
        }

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService").success(function (data) {
            $scope.adminUsers = [{id: 0, realname: "未分配销售"}].concat(data);
        })

        /*状态combobox*/
        $http.get("/admin/api/restaurant/status").success(function (data) {
            $scope.availableStatus = data;
        })

        $http.get("/admin/api/restaurant/grades").success(function (data) {
            $scope.grades = data;
        })

        $scope.warnings = [{key: 1, value: "预警状态-是"}, {key: 0, value: "预警状态-否"}];
        $scope.neverOrders = [{key: 1, value:"注册未下单"},{key:0, value:"注册已下单"}];

        $scope.$watch('restaurantSearchForm.cityId', function (cityId, old) {
            if (cityId) {
                $http.get("/admin/api/city/" + cityId + "/warehouses").success(function (data, status, headers, config) {
                    $scope.availableWarehouses = data;
                    if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                        $scope.restaurantSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                    }
                });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.restaurantSearchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.restaurantSearchForm.warehouseId = null;
            }
        });

        $scope.restaurants = [];
        $scope.page = {
            itemsPerPage: 100
        };


        if ($stateParams.sortField) {
            $scope.restaurantSearchForm.sortField = $stateParams.sortField;
        } else {
            $scope.restaurantSearchForm.sortField = "id";
        }

        if ($stateParams.asc) {
            $scope.restaurantSearchForm.asc = true;
        } else {
            $scope.restaurantSearchForm.asc = false;
        }

        $scope.date = new Date().toLocaleDateString();

        $scope.$watch('startDate', function (newVal) {
            $scope.restaurantSearchForm.start = $filter('date')(newVal, 'yyyy-MM-dd');
        });

        $scope.$watch('endDate', function (newVal) {
            $scope.restaurantSearchForm.end = $filter('date')(newVal, 'yyyy-MM-dd');
        });

        if ($stateParams.status) {
            $scope.restaurantSearchForm.status = parseInt($stateParams.status);
        }

        if ($scope.restaurantSearchForm.start) {
            $scope.startDate = Date.parse($scope.restaurantSearchForm.start);
        }

        if ($scope.restaurantSearchForm.end) {
            $scope.endDate = Date.parse($scope.restaurantSearchForm.end);
        }

        if ($stateParams.warehouseId) {
            $scope.restaurantSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.cityId) {
            $scope.restaurantSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if ($stateParams.blankTime) {
            $scope.restaurantSearchForm.blankTime = parseInt($stateParams.blankTime);
        }

        if ($stateParams.grade) {
            $scope.restaurantSearchForm.grade = parseInt($stateParams.grade);
        }

        if ($stateParams.warning) {
            $scope.restaurantSearchForm.warning = parseInt($stateParams.warning);
        }

        if ($stateParams.neverOrder) {
            $scope.restaurantSearchForm.neverOrder = parseInt($stateParams.neverOrder);
        }


        $scope.resetPageAndSearchRestaurant = function () {
            $scope.restaurantSearchForm.page = 0;
            $scope.restaurantSearchForm.pageSize = 100;

            $location.search($scope.restaurantSearchForm);
        }


        $http({
            url: "/admin/api/restaurant",
            method: "GET",
            params: $scope.restaurantSearchForm
        }).success(function (data, status, headers, config) {
            $scope.restaurants = data.restaurants;
            $scope.consumption = data.consumption;
            $scope.restaurantSummary = data.restaurantSummary;
            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("加载失败...");
        });


        $scope.pageChanged = function () {
            $scope.restaurantSearchForm.page = $scope.page.currentPage - 1;
            $scope.restaurantSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.restaurantSearchForm);
        }

        $scope.checkPass = function (restaurant) {

            $http({
                method: 'PUT',
                url: '/admin/api/restaurant/' + restaurant.id + '/status',
                params: {status: 2},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                restaurant.status.value = 2;
                window.alert("审核成功!");
            }).error(function () {
                window.alert("审核失败！");
            });
        }

        $scope.sort = function (field) {
            if (field && field == $scope.restaurantSearchForm.sortField) {
                $scope.restaurantSearchForm.asc = !$scope.restaurantSearchForm.asc;
            } else {
                $scope.restaurantSearchForm.sortField = field;
                $scope.restaurantSearchForm.asc = false;
            }

            $scope.restaurantSearchForm.page = 0;

            $location.search($scope.restaurantSearchForm);
        }

        $scope.filterTelephone = function (telephone) {
            if (telephone) {
                $location.search({telephone: telephone});
            }
        }

        $scope.filterAdminUser = function (adminUserId) {
            if (adminUserId) {
                $location.search({adminUserId: adminUserId});
            }
        }

        $scope.NewTicket = function (restaurant) {
            // console.log(order);
            var arr = {
                "username": $rootScope.user.realname,
                "restaurant": restaurant
            };
            console.log(arr);
            arr = JSON.stringify(arr);
            arr = encodeURIComponent(arr);
            // console.log(arr)
            window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data=" + arr);
        }

        $scope.export = function () {
            var str = [];
            for (var p in $scope.restaurantSearchForm) {
                if ($scope.restaurantSearchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.restaurantSearchForm[p]));
                }
            }
            $window.open("/admin/api/restaurant/export?" + str.join("&"));
        };
    });

/**
 * Created by challenge on 16/1/20.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RestaurantTypeDetailCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {

        };

        /*分类状态list*/
        $http.get("/admin/api/restaurantType/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })



        $http.get("/admin/api/restaurantType")
            .success(function (data, status, headers, config) {
                $scope.availableParentRestaurantTypes = [];

                angular.forEach(data, function (value, key) {
                    if (!$stateParams.id || $stateParams.id != value.id) {
                        this.push(value);
                    }
                }, $scope.availableParentRestaurantTypes);
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })

        if ($stateParams.id) {
            $http.get("/admin/api/restaurantType/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                $scope.formData.status = data.status.value;
                if(data.parentRestaurantTypeId) {
                    $scope.formData.parentRestaurantTypeId = data.parentRestaurantTypeId;
                }

                $scope.formData.displayOrder = data.displayOrder;

            });
        }

        $scope.saverestaurantType = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/restaurantType/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("修改成功!");
                    })
                    .error(function (data) {
                        alert("修改失败,"+data.errmsg);
                    });
            } else {
                $http({
                    url: "/admin/api/restaurantType",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败,"+data.errmsg);
                    });
            }
        }
    });


/**
 * Created by challenge on 16/1/20.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RestaurantTypeListCtrl', function($scope, $http, $stateParams, $location, $rootScope) {

        //if($rootScope.user) {
        //    var data = $rootScope.user;
        //    $scope.cities = data.cities;
        //}


        $scope.treeOptions = {
            dropped : function(event) {
                console.log(event);
                var destParentrestaurantTypeId = 0;
                var destChildren = [];
                if(event.dest.nodesScope.$parent.$modelValue) {
                    destParentrestaurantTypeId = event.dest.nodesScope.$parent.$modelValue.id;

                    event.dest.nodesScope.$parent.$modelValue.children.forEach(function(child) {destChildren.push(child.id);});
                }

                var sourceParentrestaurantTypeId = 0;
                var sourceChildren = [];
                if(event.source.nodesScope.$parent.$modelValue) {
                    sourceParentrestaurantTypeId = event.source.nodesScope.$parent.$modelValue.id;

                    event.source.nodesScope.$parent.$modelValue.children.forEach(function(child) {sourceChildren.push
                    (child.id);});
                }

                $http({
                    url: "/admin/api/restaurantType/" + destParentrestaurantTypeId + "/children",
                    params: {children: destChildren},
                    method: 'PUT'
                })
                    .then(function (data) {
                        $http({
                            url: "/admin/api/restaurantType/" + sourceParentrestaurantTypeId + "/children",
                            params: {children: sourceChildren},
                            method: 'PUT'
                        })
                            .success(function (data) {
                                alert("修改成功!");
                            })
                            .error(function (data) {
                                alert("修改失败!");
                            });
                    })



            }
        }

        $scope.form = {
            status : [1,2,3]
        }

        $scope.visible = function (item) {
            return $scope.form.status.indexOf(item.status) >= 0;
        };

        $http({
            url:"/admin/api/restaurantType/treeJson",
            method:'GET',
            params: {'cityId' :$stateParams.cityId}
        })
            .success(function(data){
                $scope.nodes = data;
            })
            .error(function(data){

            });

        $scope.setrestaurantTypeCity = function(node, cityId, active) {
            $http({
                url:"/admin/api/restaurantType/"+ node.id +"/changeCity",
                method:'PUT',
                params: {'cityId' :cityId, 'active': active}
            })
                .success(function(data){
                    if (active === true) {
                        if (node.cityIds.indexOf(cityId) == -1) {
                            node.cityIds.push(cityId);
                        }
                    } else {
                        if (node.cityIds.indexOf(cityId) != -1) {
                            node.cityIds.splice(node.cityIds.indexOf(cityId), 1);
                        }
                    }
                })
                .error(function(data){
                    alert("失败");
                });
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('AddsaleVisitStaffCtrl', function($scope, $rootScope, $http, $stateParams, $state) {
 	
 	$http.get("/admin/api/saleVisit/status")
 	.success(function(data) {
 		$scope.saleVisitStatus = data;
 	});

 	$scope.formData = {
 		restaurantId : $stateParams.restaurantId
 	};

	$scope.$watch('formData.status', function(newVal, oldVal){

		if(newVal != null && newVal != '') {
			$http.get("/admin/api/saleVisit/status/" + newVal + "/reason")
			.success(function (data, status, headers, config) {
				if (data) {
					$scope.reasons = data;
				} else {
					$scope.reasons = [{name : "无",value : -1}];
					$scope.formData.reasonId = -1;
				}
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.reasonId = null;
			}
		} else {
			$scope.reasons = [];
			$scope.formData.reasonId = null;
		}
	});

 	$scope.createSaleVisit = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/saleVisit',
 			data: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("提交成功!");
 			$state.go("oam.saleVisit-list", {"restaurantId":$scope.formData.restaurantId});

 		})
 		.error(function(data, status, headers, config) {
 			window.alert("提交失败！");
 		});
 	};
 })

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('saleVisitManagementCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location) {

        $http.get("/admin/api/saleVisit/status")
            .success(function(data) {
                $scope.saleVisitStatus = data;
            });

        $scope.saleVisitForm = {
            restaurantId : $stateParams.restaurantId
        };

        $scope.$watch('saleVisitForm.status', function(newVal, oldVal){

            if(newVal != null && newVal != '') {
                $http.get("/admin/api/saleVisit/status/" + newVal + "/reason")
                    .success(function (data, status, headers, config) {
                        if (data) {
                            $scope.reasons = data;
                        } else {
                            $scope.reasons = [{name : "无",value : -1}];
                            $scope.saleVisitForm.reasonId = -1;
                        }
                    });

                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.saleVisitForm.reasonId = null;
                }
            } else {
                $scope.reasons = [];
                $scope.saleVisitForm.reasonId = null;
            }
        });






        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.adminUsers = data;
            })

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.saleVisitForm = {
            page : $stateParams.page,
            pageSize : $stateParams.pageSize,
            restaurantId : $stateParams.restaurantId,
            status : $stateParams.status,
            reasonId : $stateParams.reasonId
        }

        $scope.$watch('startDate', function(newVal) {
            if(newVal){
                $scope.saleVisitForm.start = $filter('date')(newVal, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('endDate', function(newVal) {
            if(newVal){
                $scope.saleVisitForm.end = $filter('date')(newVal, 'yyyy-MM-dd');
            }
        });


        $scope.searchSaleVisit = function () {
            $http({
                url: "/admin/api/saleVisit",
                method: "GET",
                params: $scope.saleVisitForm
            })
                .success(function (data, status, headers, config) {
                    $scope.saleVisits = data.saleVisits;
                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });

        }

        $scope.searchSaleVisit();

        $scope.pageChanged = function() {
            $scope.saleVisitForm.page = $scope.page.currentPage - 1;
            $scope.saleVisitForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchSaleVisit();
        }

        $scope.resetPageAndSearchSaleVisit = function() {
            $scope.saleVisitForm.page = 0;
            $scope.saleVisitForm.pageSize = 100;

            $scope.searchSaleVisit();
        }
    });

'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:zoneListCtrl
 * @description
 * # zoneListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('zoneListCtrl', function ($scope, $http, $stateParams, editableOptions) {
        editableOptions.theme = 'bs3';

    	$scope.zoneList = [];

        $http.get("/admin/api/zone")
            .success(function (data, status, headers, config) {
                $scope.zoneList = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.updateZone = function(zone ,active){
        	$http({
                method: 'put',
                url: '/admin/api/zone/' + zone.id,
                data: 'active=' + active,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
            .success(function(data) {
            	window.alert("保存成功!");
                zone.active = data.active;
            })
            .error(function(data) {
            	window.alert("保存失败!");
            });
        }

        /*商圈-市场*/
        $http.get("/admin/api/warehouse")
            .success(function (data, status, headers, config) {
                $scope.availableWarehouses = data;
            });

        $scope.editSaveZone = function(warehouse, zone) {
            var saveZoneWarehouse = function(){
                $http({
                       method: 'PUT',
                       url: '/admin/api/zone/' + zone.id,
                       data: 'warehouseId=' + warehouse.warehouseId,
                       headers: {
                           'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                       }
                   })
                   .success(function(data) {
                        zone.warehouse.id = data.warehouse.id;
                        zone.warehouse.name = data.warehouse.name;
                        window.alert("修改成功!");
                   })
                   .error(function(data) {
                        window.alert("修改失败!");
                   });
            }

            return saveZoneWarehouse();
        };
    })


/**
 * Created by king-ck on 2015/11/13.
 */
'use strict';
angular.module('sbAdminApp').controller('exchangeScoreSearchList',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {
        $scope.exchangeScoreSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            adminUserId: $stateParams.adminUserId!=null?parseInt($stateParams.adminUserId):null,
            warehouseId: $stateParams.warehouseId
        };

        //alert($stateParams.adminUserId);
        //alert($scope.exchangeScoreSearchForm.adminUserId);

        $scope.page={};

        $scope.$watch("scoreLogsBefore",function(newVal,oldVal){

            if(newVal){
                var scoreLogsAfter=[];
                console.log(newVal)
                for(var i=0;i<newVal.length;i++){
                    for(var r=0;r<newVal[i].customer.restaurant.length;r++){
                        //console.info(newVal[i].customer.restaurant[r]);
                        scoreLogsAfter.push({
                            restaurantId: newVal[i].customer.restaurant[r].id,
                            restaurantName: newVal[i].customer.restaurant[r].name,
                            createTime: newVal[i].createTime,
                            remark: newVal[i].remark,
                            integral: newVal[i].integral,
                            count:newVal[i].count,
                            customerId: newVal[i].customer.id,
                            oldScore:newVal[i]
                        });
                    }
                }
                $scope.scoreLogsAfter=scoreLogsAfter;

                console.log($scope.scoreLogsAfter);
            }
        });

        $scope.initLoad=function(){
            console.log($rootScope.user);

            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if ($scope.cities && $scope.cities.length == 1) {
                    $scope.exchangeScoreSearchForm.cityId = $scope.cities[0].id;
                }
            }
            //销售信息
            $http({
                method:"GET",
                url:"/admin/api/admin-user/global?role=CustomerService",
            }).success(function(data){
                $scope.adminUsers = data;
            })

            //状态
            $http.get("/admin/api/restaurant/status").success(function (data) {
                $scope.availableStatus = data;
            })

            //等级
            $http.get("/admin/api/restaurant/grades").success(function (data) {
                $scope.grades = data;
            })

        }

        $scope.searchLoad=function(){
            $scope.exchangeScoreSearchForm =angular.extend($scope.getDefaultSearchForm(),$scope.exchangeScoreSearchForm );
            $http({
                url: '/admin/api/scoreLog/query',
                method: "GET",
                params:$scope.exchangeScoreSearchForm
            }).success(function (data, status, headers, config) {

                console.log(data)
                $scope.scoreLogsBefore = data.scoreLogs;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });

        }

        $scope.$watch('exchangeScoreSearchForm.cityId',function(newVal,oldVal){
            if(newVal!=null){
                //加载市场
                console.info(newVal);
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.availableWarehouses = data;
                    if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                        $scope.exchangeScoreSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                    }else{
                        if( $scope.exchangeScoreSearchForm.warehouseId!=null){
                            for(var i=0;i<data.length;i++){
                                if(data[i].id==$scope.exchangeScoreSearchForm.warehouseId){
                                    return;
                                }
                            }
                            $scope.exchangeScoreSearchForm.warehouseId=null;
                        }
                    }
                });
            }else{
                $scope.exchangeScoreSearchForm.warehouseId = null;
            }
        });

        $scope.getDefaultSearchForm=function(){
            return {
                cityId : null,
                restaurantName : null,
                warehouseId : null,
                restaurantId : null,
                adminUserId : null,
                status : null,
                grade : null,
                scoreLogStatus: 3,
                page : 0,
                pageSize : 100
            }; //搜索表单的参数
        }

        $scope.pageChanged = function() {
            $scope.exchangeScoreSearchForm.page = $scope.page.currentPage - 1;
            $scope.exchangeScoreSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.exchangeScoreSearchForm);
        }

        $scope.initSearchForm=function(){
            $scope.exchangeScoreSearchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.exchangeScoreSearchForm);
        }

        $scope.initLoad();
        $scope.searchLoad();

        console.log($scope.exchangeScoreSearchForm);

    });
'use strict';

angular.module('sbAdminApp')
.controller('scoreSearchList',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {


        $scope.getDefaultSearchForm=function(){
            return {
                page: $stateParams.page==null?0:$stateParams.page,
                pageSize: $stateParams.pageSize==null?100:$stateParams.pageSize
            };
        }

        $scope.scoreSearchForm = angular.extend($scope.getDefaultSearchForm(),{
            adminUserId:!!$stateParams.adminUserId?parseInt($stateParams.adminUserId):null,
            warehouseId: $stateParams.warehouseId,
            sortField: $stateParams.sortField,
            asc: $stateParams.asc
        });
        $scope.page={};

        $scope.$watch('scoresBefore',function(newVal,oldVal){
            if(newVal){
                var scoresAfter=[];

                for(var i=0;i<newVal.length;i++){
                    for(var r=0;r<newVal[i].customer.restaurant.length;r++){
                        //console.info(newVal[i].customer.restaurant[r]);
                        scoresAfter.push({
                            restaurantId: newVal[i].customer.restaurant[r].id,
                            restaurantName: newVal[i].customer.restaurant[r].name,
                            totalScore: newVal[i].totalScore,               //总积分
                            exchangeScore: newVal[i].exchangeScore,         //已兑换积分
                            availableScore: newVal[i].totalScore- newVal[i].exchangeScore,//剩余积分
                            customerId: newVal[i].customer.id,

                            oldScore:newVal[i]
                        });
                    }
                }
                $scope.scoresAfter=scoresAfter;
            }
        });
        $scope.$watch('scoreSearchForm.cityId',function(newVal,oldVal){
            if(newVal!=null){
                //加载市场
                console.info(newVal);
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.availableWarehouses = data;
                    if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                        $scope.scoreSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                    }else{
                        if( $scope.scoreSearchForm.warehouseId!=null){
                            for(var i=0;i<data.length;i++){
                                if(data[i].id==$scope.scoreSearchForm.warehouseId){
                                    return;
                                }
                            }
                            $scope.scoreSearchForm.warehouseId=null;
                        }
                    }
                });
            }else{
                $scope.scoreSearchForm.warehouseId=null;
            }
        });

        $scope.initLoad=function(){
            console.log($rootScope.user);
            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if ($scope.cities && $scope.cities.length == 1) {
                    $scope.scoreSearchForm.cityId = $scope.cities[0].id;
                }
            }
            //销售信息
            $http({
                method:"GET",
                url:"/admin/api/admin-user/global?role=CustomerService",
            }).success(function(data){
                $scope.adminUsers = data;
            })

            //状态
            $http.get("/admin/api/restaurant/status").success(function (data) {
                $scope.availableStatus = data;
            })

            //等级
            $http.get("/admin/api/restaurant/grades").success(function (data) {
                $scope.grades = data;
            })

        }

        $scope.searchLoad=function(){

            $scope.scoreSearchForm =angular.extend($scope.getDefaultSearchForm(), $scope.scoreSearchForm )
            $http({
                url: '/admin/api/score/query',
                method: "GET",
                params: $scope.scoreSearchForm
            }).success(function (data, status, headers, config) {

                console.log(data);
                $scope.scoresBefore = data.scores;
                $scope.count = data.total;
                console.log($scope.scores);

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }
        $scope.pageChanged = function() {
            $scope.scoreSearchForm.page = $scope.page.currentPage - 1;
            $scope.scoreSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.scoreSearchForm);
        }

        $scope.sort = function(field) {

            if(field && field == $scope.scoreSearchForm.sortField) {

                $scope.scoreSearchForm.asc = $scope.scoreSearchForm.asc=="false";

            } else {
                $scope.scoreSearchForm.sortField = field;
                $scope.scoreSearchForm.asc = false;
            }

            $scope.scoreSearchForm.page = 0;



            $location.search($scope.scoreSearchForm);
        }

        $scope.initSearchForm=function(){
            $scope.scoreSearchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.scoreSearchForm);
        }

        $scope.initLoad();
        $scope.searchLoad();

        console.log($scope.scoreSearchForm);

});
'use strict';

angular.module('sbAdminApp')
    .controller('scoreDetailSearchList', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window){
        console.log("controller-scoreDetailSearchList");

        $scope.getDefaultSearchForm=function(){
            return {
                customerId: $stateParams.customerId,
                restaurantId: $stateParams.restaurantId,
                scoreLogStatus: 1,
                page: $stateParams.page==null?0:$stateParams.page,
                pageSize: $stateParams.pageSize==null?100:$stateParams.pageSize
            }; //搜索表单的参数
        }

        $scope.searchForm=angular.extend($scope.getDefaultSearchForm(),{
            orderBeginDate: $stateParams.orderBeginDate,
            orderEndDate:   $stateParams.orderEndDate
        });

        $scope.page={};

        $scope.submitDateFormat="yyyy-MM-dd";
        $scope.dateInput={
            format:'yyyy-MM-dd',
            beginDateOptions : false,
            endDateOptions:false,

            beginDateOpen : function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dateInput.beginDateOptions = true;
                $scope.dateInput.endDateOptions=false;
            },
            endDateOpen : function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dateInput.beginDateOptions = false;
                $scope.dateInput.endDateOptions=true;
            },
            dateOptions : {
                dateFormat : 'yyyy-MM-dd',
                startingDay : 1
            }
        };

        $scope.searchLoad=function(){
            $http({
                url: '/admin/api/scoreLog/query',
                method: "GET",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                //console.log(data)
                $scope.scoreLogs = data.scoreLogs;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });

        }

        //合计查询
        $scope.sumLoad=function(){
            $http({
                url: '/admin/api/score/order/sum',
                method: "GET",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                //console.log(data)

                $scope.scoreSumInfo = data;

            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.initSearchForm=function(){
            $scope.searchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.searchForm);
        }

        $scope.sumLoad();
        $scope.searchLoad();


    });
'use strict';
angular.module('sbAdminApp')
    .controller('scoreExchangeCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window){

        $scope.inParam={
            customerId: $stateParams.customerId
        }

        //$scope.loadCustomer=function(){
        //    $http({
        //        url: '/admin/api/score/exchange-coupon/'+ $scope.inParam.customerId,
        //        method: "GET"
        //    }).success(function (data, status, headers, config) {
        //
        //    }).error(function (data, status, headers, config) {
        //        window.alert("搜索失败...");
        //    });
        //}


        $scope.loadCoupon=function(){
            $http({
                url: '/admin/api/score/exchange-coupon/'+ $scope.inParam.customerId,
                method: "GET"
            }).success(function (data, status, headers, config) {
                $scope.couponData=data;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.$watch('inParam.couponId', function (newVal) {
            if(newVal!=null){
                for(var i=0;i<$scope.couponData.coupons.length;i++){
                    console.log($scope.couponData.coupons[i].id );
                    console.log(newVal);
                    if($scope.couponData.coupons[i].id == newVal){

                        $scope.currentCoupon = $scope.couponData.coupons[i];
                        console.log($scope.currentCoupon);
                        return;
                    }
                }
            }
        });
        //$scope.$watch('inParam.exchangeNum', function (newVal) {
        //    alert(newVal);
        //
        //});

        $scope.checkExchangeScore= function(){
            if($scope.currentCoupon==null || $scope.currentCoupon.score ==null){
                return false;
            }
            if(isNaN($scope.currentCoupon.score * $scope.inParam.count)){
                return false;
            }
            if($scope.currentCoupon.score * $scope.inParam.count > $scope.couponData.score.totalScore - $scope.couponData.score.exchangeScore){
                return false;
            }
            return true;
        }

        $scope.submitForm=function(){
            //var needScore = $scope.currentCoupon.score * $scope.inParam.count
            //var availableScore = $scope.couponData.score.totalScore - $scope.couponData.score.exchangeScore;
            //
            //if(isNaN(needScore)){
            //    alert("无法兑换");
            //}
            //if(needScore>availableScore){
            //    alert("积分不足");
            //}
            //兑换
            $http({
                url: '/admin/api/score/exchange',
                method: "PUT",
                data: $scope.inParam,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.couponData = data;
                alert("执行成功");
                $location.search($scope.inParam);
            }).error(function (data, status, headers, config) {
                if(data!=null && data.errmsg!=null){
                    window.alert(data.errmsg);
                }else {
                    window.alert("操作失败");
                }
            });
            return ;
        }
        $scope.isNaN= function(intVal){
            return isNaN(intVal);
        }
        $scope.loadCoupon();

    });
'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */
angular.module('sbAdminApp')
	.directive('headerNotification',function(){
		return {
        templateUrl:'app/directives/header/header-notification/header-notification.html',
        restrict: 'E',
        replace: true
    	}
	});


