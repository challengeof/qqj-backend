'use strict';
angular.module('sbAdminApp')
    .controller('PurchaseListCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

        $scope.page = {itemsPerPage: 20};

        $scope.iForm = {stage : 3, pageSize : $scope.page.itemsPerPage};

        $scope.showLevel = true;

        if ($stateParams.level) {
            $scope.iForm.level = $stateParams.level;
            $scope.showLevel = false;
        }

        if ($stateParams.page) {
            $scope.iForm.page = parseInt($stateParams.page);
        }

        $http.get("/org/customer/level-enumeration")
            .success(function (data) {
                $scope.levels = data;
            });

        $http.get("/org/purchase/status-enumeration")
            .success(function (data) {
                $scope.statuses = data;
            });

        $http({
            url: "/org/team/all",
            method: "GET"
        })
        .success(function (data) {
            $scope.teams = data;
        });

        $http({
            url: "/org/purchase/list",
            method: "GET",
            params: $scope.iForm
        })
        .success(function (data) {
            $scope.items = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.pageChanged = function() {
            $scope.iForm.page = $scope.page.currentPage - 1;
            $location.search($scope.iForm);
        }

        $scope.search = function () {
            $scope.iForm.page = 0;
            $location.search($scope.iForm);
        }
    });