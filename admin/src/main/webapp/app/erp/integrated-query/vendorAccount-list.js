'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('VendorAccountListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				if(typeof oldVal != 'undefined' && newVal != oldVal){
					   $scope.searchForm.organizationId = null;
				   }
			} else {
				$scope.organizations = [];
				$scope.searchForm.organizationId = null;
			}
		});

		$scope.$watch('searchForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/vendorAccount/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.vendorAccountBalances = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");

		});

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/vendorAccount/list/export?" + str.join("&"));
		}
	});