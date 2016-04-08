'use strict';
angular.module('sbAdminApp')
    .controller('suggestionListCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $location, $window) {

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

        $http({
            method: 'GET',
            url: "/admin/api/suggestion/list",
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.suggestions = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("查询失败");
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        //$scope.export = function () {
        //    var str = [];
        //    for (var p in $scope.searchForm) {
        //        if ($scope.searchForm[p]) {
        //            str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
        //        }
        //    }
        //    $window.open("/admin/api/suggestion/export?" + str.join("&"));
        //};

    });