'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderAddCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		if ($stateParams.add == 1) {
			$scope.add = true;
			$scope.edit = true;
		}

		if ($stateParams.edit == 1) {
			$scope.edit = true;
		}

		if ($stateParams.audit == 1) {
			$scope.canAudit = true;
		}
		$scope.searchForm = {};
        $scope.submitting = false;

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.candidateSkus = [];

		$scope.funcAsync = function (name) {
			if (name && name !== "") {
				$scope.candidateSkus = [];
				$http({
					url:"/admin/api/sku/candidates",
					method:'GET',
					params:{organizationId:$scope.searchForm.organizationId, name:name, showLoader:false}
				}).success(function (data) {
					$scope.candidateSkus = data;
				});
			}
		}

		$scope.resetCandidateSkus = function () {
			$scope.candidateSkus = [];
		}
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

		if ($scope.add) {
			$scope.$watch('searchForm.vendorId', function(newVal, oldVal) {
				if(newVal) {
					$http({
						url:"/admin/api/purchase/order/preItems",
						method:'GET',
						params:{vendorId:newVal, cityId:$scope.searchForm.cityId}
					}).success(function (data) {
						$scope.purchaseOrderItems = data.content;
						$scope.candidateSkus = [];
						angular.forEach($scope.purchaseOrderItems, function(item, key) {
							$scope.candidateSkus.push(item.sku);
						});
						console.log($scope.candidateSkus)
					});
				} else {
					$scope.purchaseOrderItems = [];
				}
			});
		}

		$scope.changeDepotId = function () {
			$scope.purchaseOrderItems = [];
		};

		$scope.purchaseOrderTotal = parseFloat(0);

		$scope.submit = function () {
			$scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.purchaseOrderItems = $scope.purchaseOrderItems;
			$http({
				url: "/admin/api/purchase/order/add",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("保存成功...");
				$scope.submitting = false;
				$state.go("oam.purchase-order-list", {audit:0,type:1,listType:1,toPrint:0});
			})
			.error(function (data, status, headers, config) {
				$scope.tableform.$show();
				alert("保存失败...");
				$scope.submitting = false;
			});
		}

		$scope.purchaseOrderTotal = parseFloat(0);

		$scope.purchaseOrderItems = [
		];

		$scope.remove = function(index) {
			$scope.purchaseOrderItems.splice(index, 1);
		}

		$scope.addItem = function() {
			$scope.inserted = {
			};
			$scope.purchaseOrderItems.push($scope.inserted);
		};

		$scope.$watchCollection('purchaseOrderItems', function(newVal, oldVal) {
			for (var index = 0; index < $scope.purchaseOrderItems.length; index++) {
				var exp = 'purchaseOrderItems[' + index  + '].purchaseTotalPrice';

				if (!$scope.existsWatcherByExp(exp)) {
					$scope.$watch(exp, function(newVal, oldVal) {
						if (newVal != oldVal) {
							if (oldVal) {
								$scope.purchaseOrderTotal = $scope.purchaseOrderTotal - parseFloat(oldVal);
							}

							if (newVal) {
								$scope.purchaseOrderTotal = $scope.purchaseOrderTotal + parseFloat(newVal);
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

		$scope.searchSku = function(purchaseOrderItem) {
			$scope.candidateSkus = [];
			purchaseOrderItem.purchaseQuantity = 0;
			purchaseOrderItem.purchaseBundleQuantity = 0;
			purchaseOrderItem.purchasePrice = 0;
			purchaseOrderItem.purchaseTotalPrice = 0;

			$http({
				url:"/admin/api/purchase/order/sku",
				method:'GET',
				params:{cityId:$scope.searchForm.cityId, skuId:purchaseOrderItem.skuId, status:2}
			}).success(function (data, status, headers, config) {
				if (!data.sku) {
					alert('sku不存在或已失效');
					purchaseOrderItem.skuId = '';
					return;
				}
				$scope.candidateSkus.push(data.sku);
				purchaseOrderItem.name = data.sku.name;
				purchaseOrderItem.rate = data.sku.rate;
				if (data.stockTotal) {
					purchaseOrderItem.quantity = data.stockTotal.quantity;
					purchaseOrderItem.avgCost = data.stockTotal.avgCost;
				} else {
					purchaseOrderItem.quantity = 0;
					purchaseOrderItem.avgCost = 0;
				}
				purchaseOrderItem.singleUnit = data.sku.singleUnit;
				purchaseOrderItem.bundleUnit = data.sku.bundleUnit;
				purchaseOrderItem.capacityInBundle = data.sku.capacityInBundle;
				purchaseOrderItem.fixedPrice = data.fixedPrice;
				purchaseOrderItem.lastPurchasePrice = data.lastPurchasePrice;
			});
		};

		$scope.savePurchaseQuantity = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseQuantity || !REGEX.test(purchaseOrderItem.purchaseQuantity)) {
				purchaseOrderItem.purchaseQuantity = 0;
			}
			purchaseOrderItem.purchaseBundleQuantity = (purchaseOrderItem.purchaseQuantity / purchaseOrderItem.capacityInBundle).toFixed(6);
			purchaseOrderItem.purchaseTotalPrice = purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity;
		};

		$scope.savePurchaseBundleQuantity = function(purchaseOrderItem) {
			var REGEX = /^\-?\d+(.\d+)?$/
			if (!purchaseOrderItem.purchaseBundleQuantity || !REGEX.test(purchaseOrderItem.purchaseBundleQuantity)) {
				purchaseOrderItem.purchaseBundleQuantity = 0;
			}
			purchaseOrderItem.purchaseQuantity = Math.round(purchaseOrderItem.purchaseBundleQuantity * purchaseOrderItem.capacityInBundle);
			purchaseOrderItem.purchaseTotalPrice = purchaseOrderItem.purchasePrice * purchaseOrderItem.purchaseQuantity;

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
			purchaseOrderItem.purchasePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseQuantity).toFixed(6);
			purchaseOrderItem.purchaseBundlePrice = (purchaseOrderItem.purchaseTotalPrice / purchaseOrderItem.purchaseBundleQuantity).toFixed(6);
		};

		if ($stateParams.id) {
			$http.get("/admin/api/purchase/order/" + $stateParams.id)
				.success(function (data, status) {
					$scope.searchForm.cityId = data.cityId;
					$scope.searchForm.organizationId = data.organizationId;
					$scope.searchForm.vendorId = data.vendor.id;
					$scope.searchForm.depotId = data.depot.id;
					$scope.searchForm.expectedArrivedDate = data.expectedArrivedDate;
					$scope.searchForm.remark = data.remark;
					$scope.searchForm.opinion = data.opinion;
					if ($scope.searchForm.opinion != null) {
						$scope.showOpinion = true;
					}
					$scope.purchaseOrderItems = data.purchaseOrderItems;
					angular.forEach($scope.purchaseOrderItems, function(item, key) {
						$scope.candidateSkus.push(item.sku);
					});

					$scope.purchaseOrderTotal = data.total;
				})
				.error(function (data, status) {
					window.alert("获取采购单信息失败...");
					return;
				});
		}

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/purchase/order/audit",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("审批完成...");
				$scope.submitting = false;
				$state.go("oam.purchase-order-list", {audit:1,type:1,listType:1,toPrint:0});
			})
			.error(function (data, status, headers, config) {
				alert("审批失败...");
				$scope.submitting = false;
			});
		}
	});