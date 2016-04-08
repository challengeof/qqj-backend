'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddressBookCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

            $scope.page = {
                itemsPerPage : 100
            }

            $scope.adminUserForm = {
                page : $stateParams.page,
                pageSize : $stateParams.pageSize,
                username: $stateParams.username,
                realname: $stateParams.realname,
                telephone: $stateParams.telephone
            }

            if($stateParams.page) {
                $scope.adminUserForm.page = parseInt($stateParams.page);
            }

            if($stateParams.pageSize) {
                $scope.adminUserForm.pageSize = parseInt($stateParams.pageSize);
            }

            if ($stateParams.username) {
                $scope.adminUserForm.username = $stateParams.username;
            }

            if ($stateParams.realname) {
                $scope.adminUserForm.realname = $stateParams.realname;
            }

            if ($stateParams.telephone) {
                $scope.adminUserForm.telephone = $stateParams.telephone;
            }

            $http({
                url: "/admin/api/admin-user",
                method: "GET",
                params:$scope.adminUserForm
            })
            .success(function (data) {
                $scope.users = data.adminUsers;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            });
            

            $scope.resetPageAndSearchForm = function () {
                $scope.adminUserForm.page = 0;
                $scope.adminUserForm.pageSize = 100;

                $location.search($scope.adminUserForm);
            }

            $scope.pageChanged = function() {
                $scope.adminUserForm.page = $scope.page.currentPage - 1;
                $scope.adminUserForm.pageSize = $scope.page.itemsPerPage;

                $location.search($scope.adminUserForm);
            }
    });