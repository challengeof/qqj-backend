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