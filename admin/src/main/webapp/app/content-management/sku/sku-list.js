'use strict';

angular.module('sbAdminApp')
	.controller('SkuListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 50
		};

		$scope.searchForm = {status : 2, pageSize : $scope.page.itemsPerPage, type : parseInt($stateParams.type)};

		$scope.type = $stateParams.type;
		if ($stateParams.type == 0) {
			$scope.showPrimary = true;
			$scope.title = '商品信息';
		} else if ($stateParams.type == 2) {
			$scope.showFixedPrice = true;
			$scope.title = '商品定价历史信息';
		} else if ($stateParams.type == 1) {
			$scope.showPriceLimit = true;
			$scope.title = '商品限价历史信息';
		} else if ($stateParams.type == 4) {
			$scope.showPurchasePrice = true;
			$scope.title = '商品采购价历史信息';
		} else if ($stateParams.type == 3) {
			$scope.showSalePrice = true;
			$scope.title = '商品售价历史信息';
		}

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$http.get("/admin/api/category")
			.success(function (data, status, headers, config) {
				$scope.categories = data;
			});

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if(newVal){
				//$http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
				//	$scope.availableWarehouses = data;
				//	if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
				//		$scope.searchForm.warehouseId = $scope.availableWarehouses[0].id;
				//	}
				//});
				$http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
				   $scope.organizations = data;
				   if ($scope.organizations && $scope.organizations.length == 1) {
					  $scope.searchForm.organizationId = $scope.organizations[0].id;
				   }
				});
				if(typeof oldVal != 'undefined' && newVal != oldVal){
					//$scope.searchForm.warehouseId = null;
					$scope.searchForm.organizationId = null;
				}
			}else{
				$scope.organizations = [];
				$scope.availableWarehouses = [];
				$scope.searchForm.organizationId = null;
				//$scope.searchForm.warehouseId = null;
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

		$http.get("/admin/api/sku/status")
		.success(function (data, status, headers, config) {
			$scope.status = data;
		})
		.error(function (data, status) {
			alert("数据加载失败！");
		});

		$http({
			url: "/admin/api/skuPrice/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.skuPriceList = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		$scope.export = function () {
			var str = [];
			for (var p in $scope.searchForm) {
				if ($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}
			$http.get("/admin/api/skuPrice/list/export?" + str.join("&"))
				.success(function (data) {
					alert("任务创建成功,请到 excel导出任务-我的任务 中下载");
				})
				.error(function (data) {
					alert("任务创建失败");
				})
		};
	});