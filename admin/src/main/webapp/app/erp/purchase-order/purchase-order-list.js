'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.type) {
			$scope.searchForm.type = $stateParams.type;
		}

		if ($stateParams.listType) {
			$scope.searchForm.listType = parseInt($stateParams.listType);
		}

		if ($stateParams.toPrint) {
			$scope.searchForm.toPrint = parseInt($stateParams.toPrint);
		}

		if ($stateParams.type == 1) {
			$scope.stockup = true;
		} else if ($stateParams.type == 2) {
			$scope.according = true;
		}

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		if ($stateParams.listType == 1) {
			$scope.purchase = true;
		} else if ($stateParams.listType == 2) {
			$scope.return = true;
		}

		if ($stateParams.toPrint == 1) {
			$scope.toPrint = true;
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

		$http.get("/admin/api/purchase/order/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$http.get("/admin/api/purchase/order/printStatus")
			.success(function (data) {
				$scope.printStatuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.audit && $scope.purchase) {
			$scope.searchForm.status = 2;
		} else if ($scope.return) {
			$scope.searchForm.status = 4;
		}


		if ($scope.toPrint) {
			$scope.searchForm.status = 3;
		}

		$http({
			url: "/admin/api/purchase/order/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.sum = data.sum;
			$scope.purchaseOrders = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.submitPurchaseOrder = function (purchaseOrder) {
			$http({
				url: "/admin/api/purchase/order/submit/" + purchaseOrder.id,
				method: "GET",
			})
				.success(function (data, status, headers, config) {
					purchaseOrder.status = data;
					alert("提交审核成功...");
				})
				.error(function (data, status, headers, config) {
					alert("提交审核失败...");
				});
		}

		$scope.cancelPurchaseOrder = function (purchaseOrder) {
			$http({
				url: "/admin/api/purchase/order/cancel/" + purchaseOrder.id,
				method: "GET",
			})
				.success(function (data, status, headers, config) {
					purchaseOrder.status = data.content.status;
					alert(data.msg);
				})
				.error(function (data, status, headers, config) {
					alert("作废采购单失败");
				});
		}

		$scope.searchForm.checkedItemIds = [];

		$scope.printPurchaseOrdersByIds = function(){
			$window.open("/admin/api/purchase/order/printPurchaseOrders?type=" + $stateParams.type + "&purchaseOrderIds=" + $scope.searchForm.checkedItemIds);
		};

		$scope.export = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/list/export?" + str.join("&"));
		}

		$scope.isCheckedAll = false;

		$scope.checkAll = function() {
			if(!($scope.isCheckedAll)){
				angular.forEach($scope.purchaseOrders, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.printMergedPurchaseOrdersByIds = function(){
			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersByIds?purchaseOrderIds=" + $scope.searchForm.checkedItemIds);
		};

		$scope.printMergedPurchaseOrdersByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersByCondition?" + str.join("&"));
		}

		$scope.printMergedPurchaseOrdersResultTogetherByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersResultTogetherByCondition?" + str.join("&"));
		}

		$scope.printMergedPurchaseOrdersTogetherByCondition = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p]) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/purchase/order/printMergedPurchaseOrdersTogetherByCondition?" + str.join("&"));
		}
	});