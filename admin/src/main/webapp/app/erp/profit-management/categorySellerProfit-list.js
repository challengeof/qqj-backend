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