'use strict';

var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('PurchaseAccordingResultCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $state, $interval, $timeout) {

	$scope.page = {
		itemsPerPage: 40
	};

	$scope.searchForm = {};

	$scope.searchForm.cityId = parseInt($stateParams.cityId);

	$scope.searchForm.organizationId = parseInt($stateParams.organizationId);

	$scope.searchForm.depotId = parseInt($stateParams.depotId);

	$scope.total = parseFloat(0);

	$scope.searchForm.skuIds = [];

	$scope.searchForm.checkedItemSignIds = [];

	$scope.isCheckedAll = false;

	if (!angular.isArray($stateParams.cutOrders)) {
		$scope.searchForm.cutOrders = [];
		$scope.searchForm.cutOrders.push($stateParams.cutOrders);
	} else {
		$scope.searchForm.cutOrders = $stateParams.cutOrders;
	}

	$scope.searchForm.type = 2;
	$scope.submitting = false;

	$scope.searchForm.pageSize = $scope.page.itemsPerPage;

	if($rootScope.user) {
		var data = $rootScope.user;
		$scope.cities = data.cities;
	}

	$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
		if (newVal) {
			$http.get("/admin/api/depot/list/" + newVal)
				.success(function (data, status, headers, config) {
					$scope.depots = data;
				});
			$http.get("/admin/api/city/" + newVal + "/organizations").success(function(data) {
				$scope.organizations = data;
			});
		} else {
			$scope.depots = [];
			$scope.organizations = [];
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

	$http.get("/admin/api/purchase/order/item/signList")
		.success(function (data) {
			$scope.signList = data;
		})

	$scope.pageChanged = function() {
		if ($scope.tableform.$invalid||$scope.submitting) {
			alert('保存失败...');
			return;
		}

		$scope.savePurchaseAccordingResult(function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search();
		});
	}

	$scope.deleteCalculateWatchers = function() {
		var watchers = [];
		var REGEX = /^purchaseOrderItems\[\d+\]\.purchaseTotalPrice$/
		angular.forEach($scope.$$watchers, function(watcher, key) {
			if (!REGEX.test(watcher.exp)) {
				watchers.push(watcher);
			}
		});
		$scope.$$watchers = watchers;
	}

	$scope.search = function () {
		delete $scope.searchForm.purchaseOrderItems;
		$http({
			url: "/admin/api/purchase/order/items",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.searchForm.skuIds = [];
			$scope.searchForm.checkedItemSignIds = [];
			$scope.deleteCalculateWatchers();
			$scope.total = data.totalAmount;
			$scope.purchaseOrderItems = data.content;
			angular.forEach($scope.purchaseOrderItems, function(item, key){
				item.vendorId = item.purchaseOrder.vendor.id;
				$scope.searchForm.skuIds.push(item.sku.id);
				if (item.sign == 1) {
					$scope.searchForm.checkedItemSignIds.push(item.sku.id);
				}
			});

			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
			if ($scope.purchaseOrderItems.length != 0 && $scope.purchaseOrderItems[0].purchaseOrder.status.value == 1) {
				$scope.canEdit = true;
			}

		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});
	}

	$scope.resetPageAndSearchProduct = function(){
		$scope.searchForm.page = 0;
		$scope.search();
	}

	$scope.search();

	$scope.savePurchaseAccordingResult = function (callback) {
	    $scope.submitting = true;
		$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;

		$http({
			url: "/admin/api/purchase/order/accordingResult",
			method: "POST",
			data: $scope.searchForm,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
			$scope.submitting = false;
			alert("保存成功...");
			if (callback) {
				callback();
			}
		})
		.error(function (data, status, headers, config) {
			alert("保存失败...");
			$scope.submitting = false;
		});
	}

	$scope.submitPurchaseAccordingResult = function () {
		$scope.savePurchaseAccordingResult(function() {
			$scope.showProgress = true;
			$scope.random();
			$scope.submitting = true;
			$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;
			$http({
				url: "/admin/api/purchase/order/submitAccordingResult",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				if (data.success == false) {
					alert(data.msg);
					$scope.progress = 0;
					$scope.showProgress = false;
					$scope.submitting = false;
				} else {
					$scope.progress = 100;
                    $timeout(function () {
						$scope.showProgress = false;
						$scope.canEdit = false;
						$scope.submitting = false;
						$state.go("oam.cut-order-list");
                	}, 1000);
				}
			})
			.error(function (data, status, headers, config) {
				$scope.progress = 0;
				$scope.showProgress = false;
				alert("提交失败...");
				$scope.submitting = false;
			});
		});
	}

	$scope.showVendor = function(vendorId) {
		var v = $filter('filter')($scope.vendors, {id: vendorId});
		return $filter('filter')($scope.vendors, {id: vendorId})[0].name;
	};

	$scope.purchaseOrderItems = [
	];

	$scope.savePurchaseQuantity = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseQuantity || !REGEX.test(purchaseOrderItem.purchaseQuantity)) {
			purchaseOrderItem.purchaseQuantity = 0;
		}
		purchaseOrderItem.purchaseBundleQuantity = (purchaseOrderItem.purchaseQuantity / purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity).toFixed(6);
	};

	$scope.savePurchaseBundleQuantity = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseBundleQuantity || !REGEX.test(purchaseOrderItem.purchaseBundleQuantity)) {
			purchaseOrderItem.purchaseBundleQuantity = 0;
		}
		purchaseOrderItem.purchaseQuantity = Math.round(purchaseOrderItem.purchaseBundleQuantity * purchaseOrderItem.capacityInBundle);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchaseBundlePrice * purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
	};

	$scope.savePurchasePrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchasePrice || !REGEX.test(purchaseOrderItem.purchasePrice)) {
			purchaseOrderItem.purchasePrice = 0;
		}
		purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity).toFixed(6);
	};

	$scope.savePurchaseBundlePrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseBundlePrice || !REGEX.test(purchaseOrderItem.purchaseBundlePrice)) {
			purchaseOrderItem.purchaseBundlePrice = 0;
		}
		purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseBundlePrice / purchaseOrderItem.capacityInBundle).toFixed(6);
		purchaseOrderItem.purchaseTotalPrice = (purchaseOrderItem.purchaseBundlePrice * purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
	};

	$scope.savePurchaseTotalPrice = function(purchaseOrderItem) {
		var REGEX = /^\-?\d+(.\d+)?$/
		if (!purchaseOrderItem.purchaseTotalPrice || !REGEX.test(purchaseOrderItem.purchaseTotalPrice)) {
			purchaseOrderItem.purchaseTotalPrice = 0;
		}

		if (purchaseOrderItem.purchaseQuantity != 0) {
			purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseQuantity).toFixed(6);
			purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
		}
	};

	$scope.$watchCollection('searchForm.checkedItemIds', function(newVal, oldVal) {
		if (newVal && newVal.length > 0) {
			$scope.batchUpdate = true;
		} else {
			$scope.batchUpdate = false;
		}
	});

	$scope.searchForm.checkedItemIds = [];

	$scope.checkAll = function() {
		if(!($scope.isCheckedAll)){
			angular.forEach($scope.purchaseOrderItems, function(value, key){
				$scope.searchForm.checkedItemIds.push(value.id);
			});
			$scope.isCheckedAll = true;
		}else{
			$scope.searchForm.checkedItemIds = [];
			$scope.isCheckedAll = false;
		}
	};

	$scope.checkAllSigns = function($event) {
		var checkbox = $event.target;
		var sign = checkbox.checked ? 1 : 0;
		if(checkbox.checked){
			angular.forEach($scope.purchaseOrderItems, function(value, key){
				$scope.searchForm.checkedItemSignIds.push(value.sku.id);
			});
		}else{
			$scope.searchForm.checkedItemSignIds = [];
		}

		var data = {};
		angular.extend(data, {cityId:$scope.searchForm.cityId});
		angular.extend(data, {depotId:$scope.searchForm.depotId});

		angular.extend(data, {skuIds:$scope.searchForm.skuIds});
		angular.extend(data, {sign:sign});

		$http({
			url: "/admin/api/purchase/order/changePurchaseOrderItemSign",
			method: "POST",
			data: data,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
		})
		.error(function (data, status, headers, config) {
		});
	};

	$scope.checkSign = function(purchaseOrderItem, $event) {
		var checkbox = $event.target;
		var sign = checkbox.checked ? 1 : 0;

		var data = {};
		angular.extend(data, {cityId:$scope.searchForm.cityId});
		angular.extend(data, {depotId:$scope.searchForm.depotId});

		var skuIds = [];
		skuIds.push(purchaseOrderItem.sku.id);
		angular.extend(data, {skuIds:skuIds});
		angular.extend(data, {sign:sign});

		$http({
			url: "/admin/api/purchase/order/changePurchaseOrderItemSign",
			method: "POST",
			data: data,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}
		})
		.success(function (data, status, headers, config) {
		})
		.error(function (data, status, headers, config) {
		});
	};

	$scope.$watch('searchForm.batchVendorId', function(newBatchVendorId, oldBatchVendorId) {
		if ($scope.searchForm.checkedItemIds && $scope.searchForm.checkedItemIds.length > 0 && newBatchVendorId && newBatchVendorId != "") {
			angular.forEach($scope.purchaseOrderItems, function(item, key){
				angular.forEach($scope.searchForm.checkedItemIds, function(checkedId, key){
					if(checkedId == item.id){
						item.vendorId = newBatchVendorId;
					}
				});
			});
			$scope.batchUpdate = false;
			$scope.searchForm.batchVendorId = "";
		}
	});

	$scope.$watchCollection('purchaseOrderItems', function(newVal, oldVal) {
		for (var index = 0; index < $scope.purchaseOrderItems.length; index++) {
			var exp = 'purchaseOrderItems[' + index  + '].purchaseTotalPrice';

			if (!$scope.existsWatcherByExp(exp)) {
				$scope.$watch(exp, function(newVal, oldVal) {
					if (newVal != oldVal) {
						if (oldVal) {
							$scope.total = $scope.total - parseFloat(oldVal);
						}

						if (newVal) {
							$scope.total = $scope.total + parseFloat(newVal);
						}
					}
				}, true);
			}
		}
	});

	$scope.existsWatcherByExp = function(exp) {
		var exists = false;
		angular.forEach($scope.$$watchers, function(watcher, key) {
			if (watcher.exp === exp) {
				exists = true;
			}
		});
		return exists;
	}

	$scope.showProgress = false;
	$scope.progress = 0;//当前进度
	$scope.maxProgress = 100;//总进度

	$scope.random = function() {
		$scope.progress = 0;
		var intervalSeconds = 3;//n秒更新一次进度
		var totalSteps = 120;//总秒数
		var steps = 0;//已完成秒数
		var times = 0;//已更新次数

		$interval(function() {
			steps = Math.max(times * intervalSeconds, steps);
			steps = steps + Math.floor((Math.random() * intervalSeconds) + 1);
			$scope.progress = Math.floor((steps > totalSteps ? totalSteps : steps)  * $scope.maxProgress / totalSteps);
			if ($scope.progress == $scope.maxProgress) {
				$scope.progress = $scope.maxProgress - 1;
			}
			times = times + 1;
		}, intervalSeconds * 1000, totalSteps / intervalSeconds);

	};
});