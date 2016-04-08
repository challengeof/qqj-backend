'use strict';

angular.module('sbAdminApp')
	.controller('CutOrderListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $state) {

		$scope.searchForm = {};

		$scope.canCheck = false;

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm.pageSize = $scope.page.itemsPerPage;

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

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search($scope.searchForm);
		}

		$scope.search = function() {
			$scope.searchForm.checkedItemIds = [];
			$scope.isCheckedAll = false;
			$scope.canCheck = false;
			$http({
				url: "/admin/api/purchase/order/cut-order-list",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.cutOrders = data.content;
				$scope.page.totalItems = data.total;
				$scope.page.currentPage = data.page + 1;
				angular.forEach($scope.cutOrders, function(cutOrder, key){
					if (cutOrder.status.value == 1) {
						$scope.searchForm.checkedItemIds.push(cutOrder.id);
					}

					if ($scope.searchForm.checkedItemIds.length == 0) {
						$scope.canCheck = true;
					}
				});
			})
			.error(function (data, status, headers, config) {
				alert("加载失败...");
			});;
		}

		$scope.checkAll = function() {
			if(!($scope.isCheckedAll)){
				angular.forEach($scope.cutOrders, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.purchaseResult = function() {
			$http({
				url: "/admin/api/purchase/order/createAccordingResult",
				method: "POST",
				data: $scope.searchForm.checkedItemIds,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				$state.go("oam.purchase-according-result", {cityId:$scope.searchForm.cityId,organizationId:$scope.searchForm.organizationId,depotId:$scope.searchForm.depotId,cutOrders:$scope.searchForm.checkedItemIds});
			})
			.error(function (data, status, headers, config) {
				alert("请求失败...");
			});
		};

	});