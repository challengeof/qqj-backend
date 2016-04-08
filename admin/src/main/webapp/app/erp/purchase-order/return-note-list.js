'use strict';

angular.module('sbAdminApp')
	.controller('ReturnNoteListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.audit) {
			$scope.searchForm.audit = parseInt($stateParams.audit);
		}

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
			   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
			   });
			   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
				   $scope.depots = data;
				   if ($scope.depots && $scope.depots.length == 1) {
					   $scope.searchForm.depotId = $scope.depots[0].id;
				   }
			   });
			   if(typeof oldVal != 'undefined' && newVal != oldVal){
				   $scope.searchForm.organizationId = null;
				   $scope.searchForm.depotId = null;
			   }
		   }else{
			   $scope.organizations = [];
			   $scope.depots = [];
			   $scope.searchForm.organizationId = null;
			   $scope.searchForm.depotId = null;
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

		$http.get("/admin/api/purchase/order/returnNote/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.audit) {
			$scope.searchForm.status = 1;
		}

		$http({
			url: "/admin/api/purchase/order/returnNote/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.returnNotes = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.searchForm.checkedItemIds = [];
		$scope.print = function() {
			$window.open("/admin/api/purchase/order/returnNote/print/" + $scope.searchForm.checkedItemIds[0]);
		};

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/returnNote/list/export?" + str.join("&"));
		}

	});