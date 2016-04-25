'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AdminListCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

        $scope.page = {itemsPerPage: 20};

        $scope.iForm = {pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.iForm.page = parseInt($stateParams.page);
        }

        $http({
            url: "/api/admin-user",
            method: "GET",
            params:$scope.iForm
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