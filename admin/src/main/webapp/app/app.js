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
        return $resource('/api/admin-user/me', {}, {
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
                .state('oam.admin-list', {
                    templateUrl: 'app/admin-management/admin-list.html',
                    url: '/admin-list?page&pageSize&username&realname&telephone&enabled',
                    controller: 'AdminListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                    files: [
                                    'app/admin-management/admin-list.js'
                                ]
                            })
                        }
                    }
                })
                .state('oam.admin-detail', {
                    templateUrl: 'app/admin-management/admin-detail.html',
                    url: '/admin-detail/{id}',
                    controller: 'AdminDetailCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/admin-management/admin-detail.js'
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
                .state('oam.team-list', {
                    templateUrl: 'app/org/team-list.html',
                    url: '/team-list?page&pageSize&name&founder&telephone',
                    controller: 'TeamListCtrl',
                    resolve: {
                        loadMyFiles: function ($ocLazyLoad) {
                            return $ocLazyLoad.load({
                                name: 'sbAdminApp',
                                files: [
                                    'app/org/team-list.js'
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
