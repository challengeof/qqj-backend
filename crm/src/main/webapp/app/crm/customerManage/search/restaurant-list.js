'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('restaurantListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

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
            warning: $stateParams.warning
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
        $http.get($rootScope.rootPath+"/api/admin-user/global?role=CustomerService").success(function (data) {
            $scope.adminUsers = [{id: 0, realname: "未分配销售"}].concat(data);
        })

        /*状态combobox*/
        $http.get($rootScope.rootPath+"/api/restaurant/status").success(function (data) {
            $scope.availableStatus = data;
        })

        $http.get($rootScope.rootPath+"/api/restaurant/grades").success(function (data) {
            $scope.grades = data;
        })

        $scope.warnings = [{key: 1, value: "预警状态-是"}, {key: 0, value: "预警状态-否"}];

        $scope.$watch('restaurantSearchForm.cityId', function (cityId, old) {
            if (cityId) {
                $http.get($rootScope.rootPath+"/api/city/" + cityId + "/warehouses").success(function (data, status, headers, config) {
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

        $scope.restaurantSearchForm.asc = $stateParams.asc!=null && $stateParams.asc!="false";
        $scope.restaurantSearchForm.neverOrder = $stateParams.neverOrder!=null && $stateParams.neverOrder!="false";

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

        $scope.resetPageAndSearchRestaurant = function () {
            $scope.restaurantSearchForm.page = 0;
            $scope.restaurantSearchForm.pageSize = 100;

            $location.search($scope.restaurantSearchForm);
        }


        //加载列表数据
        $http({
            url: $rootScope.rootPath+"/api/restaurant",
            method: "GET",
            params: $scope.restaurantSearchForm
        }).success(function (data, status, headers, config) {
            $scope.restaurants = data.restaurants;
            $scope.consumption = data.consumption;
            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("加载失败...");
        });

        //加载汇总数据
        $http({
            url: $rootScope.rootPath+"/api/restaurant/summary",
            method: "GET",
            params: $scope.restaurantSearchForm
        }).success(function (data, status, headers, config) {
            $scope.restaurantSummary = data.content;
        }).error(function (data, status, headers, config) {
            alert("汇总数据加载失败...");
        });

        $scope.pageChanged = function () {
            $scope.restaurantSearchForm.page = $scope.page.currentPage - 1;
            $scope.restaurantSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.restaurantSearchForm);
        }

        //$scope.checkPass = function (restaurant) {
        //
        //    $http({
        //        method: 'PUT',
        //        url: $rootScope.rootPath+'/api/restaurant/' + restaurant.id + '/status',
        //        params: {status: 2},
        //        headers: {'Content-Type': 'application/json;charset=UTF-8'}
        //    }).success(function () {
        //        restaurant.status.value = 2;
        //        window.alert("审核成功!");
        //    }).error(function () {
        //        window.alert("审核失败！");
        //    });
        //}

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
        //
        //$scope.NewTicket = function (restaurant) {
        //    // console.log(order);
        //    var arr = {
        //        "username": $rootScope.user.realname,
        //        "restaurant": restaurant
        //    };
        //    console.log(arr);
        //    arr = JSON.stringify(arr);
        //    arr = encodeURIComponent(arr);
        //    // console.log(arr)
        //    window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data=" + arr);
        //}
        //
        $scope.export = function () {
            var str = [];
            for (var p in $scope.restaurantSearchForm) {
                if ($scope.restaurantSearchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.restaurantSearchForm[p]));
                }
            }
            $window.open($rootScope.rootPath+"/api/restaurant/export?" + str.join("&"));
        };
    });
