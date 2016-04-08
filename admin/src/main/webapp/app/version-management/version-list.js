'use strict';

angular.module('sbAdminApp')
    .controller('VersionListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.searchForm = {
            pageSize: 100
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $http({
            url: "/admin/api/version/list",
            method: 'GET',
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.versions = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.searchForm);
        };
    });