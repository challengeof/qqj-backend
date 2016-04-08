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