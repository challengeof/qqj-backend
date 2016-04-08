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