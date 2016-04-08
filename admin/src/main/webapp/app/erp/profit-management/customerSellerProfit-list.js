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