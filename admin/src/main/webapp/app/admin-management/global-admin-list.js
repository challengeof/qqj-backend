'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListAllGlobalAdminCtrl', function ($scope, $http, $rootScope, $stateParams, $location) {

            $scope.adminUserForm = {
                page : $stateParams.page,
                pageSize : $stateParams.pageSize,
                cityId : $stateParams.cityId,
                organizationId : $stateParams.organizationId,
                username: $stateParams.username,
                realname: $stateParams.realname,
                telephone: $stateParams.telephone,
                isEnabled: 'true',
                global:true
            }

            var role;
            if ($rootScope.hasRole('CustomerServiceAssistant')) {
                $scope.adminUserForm.role = "CustomerService";
            }

            $scope.page = {
                itemsPerPage : 100
            }

            if($stateParams.page) {
                $scope.adminUserForm.page = parseInt($stateParams.page);
            }

            if($stateParams.pageSize) {
                $scope.adminUserForm.pageSize = parseInt($stateParams.pageSize);
            }
            if ($stateParams.cityId) {
                $scope.adminUserForm.cityId = parseInt($stateParams.cityId);
            }
            if ($stateParams.organizationId) {
                $scope.adminUserForm.organizationId = parseInt($stateParams.organizationId);
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

            if ($stateParams.isEnabled) {
                $scope.adminUserForm.isEnabled = $stateParams.isEnabled;
            }

            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if(data.cities.length == 1){
                    $scope.adminUserForm.cityId = $scope.cities[0].id;
                }
            }

            $scope.$watch('adminUserForm.cityId',function(cityId,oldVal){
                if(cityId){
                    $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                        $scope.organizations = data;
                        if ($scope.organizations && $scope.organizations.length == 1) {
                            $scope.adminUserForm.organizationId = $scope.organizations[0].id;
                        }
                    });
                    if(typeof oldVal != 'undefined' && cityId != oldVal){
                        $scope.adminUserForm.organizationId = null;
                    }
                }else{
                    $scope.organizations = [];
                    $scope.adminUserForm.organizationId = null;
                }

            });

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