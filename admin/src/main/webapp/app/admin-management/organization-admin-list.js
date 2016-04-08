'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListAllOrganzationAdminCtrl', function($scope, $state, $stateParams, $http,$rootScope,$location) {

        var role;
        if ($rootScope.hasRole('LogisticsAssistant')) {
            role = "LogisticsStaff";
        }
        $scope.formData = {
            page : $stateParams.page,
            pageSize : $stateParams.pageSize,
			cityId : $stateParams.cityId,
			organizationId : $stateParams.organizationId,
            username: $stateParams.username,
            realname: $stateParams.realname,
            telephone: $stateParams.telephone,
			isEnabled: 'true',
			role:role
		};

		$scope.page = {
            itemsPerPage : 100
        }


        if($stateParams.page) {
            $scope.formData.page = parseInt($stateParams.page);
        }

        if($stateParams.pageSize) {
            $scope.formData.pageSize = parseInt($stateParams.pageSize);
        }

        if ($stateParams.cityId) {
            $scope.formData.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.organizationId) {
            $scope.formData.organizationId = parseInt($stateParams.organizationId);
        }
        if ($stateParams.username) {
            $scope.formData.username = $stateParams.username;
        }

        if ($stateParams.realname) {
            $scope.formData.realname = $stateParams.realname;
        }

        if ($stateParams.telephone) {
            $scope.formData.telephone = $stateParams.telephone;
        }

        if ($stateParams.isEnabled) {
            $scope.formData.isEnabled = $stateParams.isEnabled;
        }


        $scope.globalAdmin = false;
        /*获取city*/
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if(data.cities.length == 1){
                $scope.formData.cityId = $scope.cities[0].id;
            }
        }

          $scope.$watch('formData.cityId',function(cityId,oldVal){
                if(cityId){
                    $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                        $scope.organizations = data;
                        if ($scope.organizations && $scope.organizations.length == 1) {
                            $scope.formData.organizationId = $scope.organizations[0].id;
                        }
                    });
                    if(typeof oldVal != 'undefined' && cityId != oldVal){
                        $scope.formData.organizationId = null;
                    }
                }else{
                    $scope.organizations = [];
                }

           })


         $http({
              url: "/admin/api/organization/adminUsers",
              method: "GET",
              params: $scope.formData
          }).success(function (data) {
              $scope.users = data.adminUsers;
              $scope.page.itemsPerPage = data.pageSize;
             $scope.page.totalItems = data.total;
             $scope.page.currentPage = data.page + 1;
          })


           $scope.resetPageAndSearchForm = function () {
               $scope.formData.page = 0;
               $scope.formData.pageSize = 100;

               $location.search($scope.formData);
           }

           $scope.pageChanged = function() {
               $scope.formData.page = $scope.page.currentPage - 1;
               $scope.formData.pageSize = $scope.page.itemsPerPage;

               $location.search($scope.formData);
           }
    });
