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
