'use strict';

angular.module('sbAdminApp')
	.controller('VendorListCtrl',function($scope, $rootScope, $http, $stateParams, $location){

		$scope.page = {
			itemsPerPage: 100
		};

		$scope.formData = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.formData.page = parseInt($stateParams.page);
		}

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
             if ($scope.cities && $scope.cities.length == 1) {
				$scope.formData.cityId = $scope.cities[0].id;
			 }
        }

		$scope.$watch('formData.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.formData.organizationId = $scope.organizations[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.formData.organizationId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.formData.organizationId = null;
		   }
		});

		$scope.$watch('formData.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.formData.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.candidateVendors = data.vendors;
				});
			} else {
				$scope.candidateVendors = [];
			}
		});


		$http({
			url : "/admin/api/vendor",
			method : 'GET',
			params: $scope.formData
		}).success(function(data){
			$scope.vendors = data.vendors;
			$scope.page.itemsPerPage = data.pageSize;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		}).error(function(data){

		});

		$scope.pageChanged = function() {
			$scope.formData.page = $scope.page.currentPage - 1;
			$location.search($scope.formData);
		}

		$scope.search = function () {
			$scope.formData.page = 0;
			$location.search($scope.formData);
		}
	});