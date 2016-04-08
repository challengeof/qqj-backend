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