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