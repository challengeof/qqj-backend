'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('feedbackListCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $location, $window) {

        $scope.searchForm = {
            pageSize: 20
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }
        if ($stateParams.pageSize) {
            $scope.searchForm.pageSize = parseInt($stateParams.pageSize);
        }

        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $http.get("/admin/api/feedback/status")
            .success(function (data, status, headers, config) {
                $scope.feedbackStatus = data;
            });

        $http.get("/admin/api/feedback/type")
            .success(function (data, status, headers, config) {
                $scope.feedbackTypes = data;
            });

        $http({
            method: 'GET',
            url: "/admin/api/feedback/list",
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

        $scope.search = function () {
            $scope.searchForm.page = 0;
            console.log($scope.searchForm);
            $location.search($scope.searchForm);
        }
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
            $window.open("/admin/api/feedback/export?" + str.join("&"));
        };




});