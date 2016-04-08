'use strict';

angular.module('sbAdminApp')
	.controller('PurchaseOrderReturnCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.purchaseTotal = 0;
		$scope.returnTotal = 0;
		$scope.auditForm = {};
		$scope.submitting = false;

		$scope.tableInvalid = function () {

			if ($scope.tableform.$invalid) {
				return true;
			}

			var valid = true;
			var total = 0;
			angular.forEach($scope.returnNoteItems, function(item, key) {
				var remainedQuantity = item.purchaseOrderItem.purchaseQuantity - item.purchaseOrderItem.returnQuantity;
				total = total + item.returnQuantity;
				if (item.returnQuantity > remainedQuantity) {
					valid = false;
				}
			})

			if (total <= 0) {
				valid = false;
			}
			if (!valid) {
				return true;
			} else {
				return false;
			}
		}


		$scope.submit = function () {
		    $scope.submitting = true;
			var submitForm = {};
			submitForm.purchaseOrderId = $stateParams.purchaseOrderId;
			submitForm.depotId = $scope.depotId;
			submitForm.returnNoteItems = $scope.returnNoteItems;
			submitForm.remark = $scope.remark;

			$http({
				url: "/admin/api/purchase/order/returnNote/create",
				method: "POST",
				data: submitForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("保存成功...");
					$scope.submitting = false;
					$state.go("oam.purchase-order-list", {audit:0,type:0,listType:2});
				})
				.error(function (data, status, headers, config) {
					$scope.tableform.$show();
					alert("保存失败...");
					$scope.submitting = false;
				});
		}

		if ($stateParams.id) {
			$http.get("/admin/api/purchase/order/returnNote/" + $stateParams.id)
				.success(function (data, status) {
					$scope.returnNoteItems = data.returnNoteItems;
					angular.forEach($scope.returnNoteItems, function(item, key){
						$scope.purchaseTotal = $scope.purchaseTotal + item.purchaseOrderItem.purchasePrice * item.purchaseOrderItem.purchaseQuantity;
					});

					$scope.purchaseTotal = ($scope.purchaseTotal).toFixed(6);
					$scope.calculateReturnTotal();
					$scope.depotName = data.depot.name;
					$scope.auditForm.opinion = data.opinion;
					$scope.remark = data.remark;
				})
				.error(function (data, status) {
					window.alert("获取退货单信息失败...");
					return;
				});
		} else {
			$http.get("/admin/api/purchase/order/returnNote/tmp/" + $stateParams.purchaseOrderId)
				.success(function (data, status) {
					$scope.returnNoteItems = data.returnNoteItems;
					angular.forEach($scope.returnNoteItems, function(item, key){
						$scope.purchaseTotal = $scope.purchaseTotal + item.purchaseOrderItem.purchasePrice * item.purchaseOrderItem.purchaseQuantity;
					});

					$scope.purchaseTotal = ($scope.purchaseTotal).toFixed(6);
					$scope.calculateReturnTotal();
					$scope.getAvailableDepots(data.depot.city.id);
				})
				.error(function (data, status) {
					window.alert("获取退货单信息失败...");
					return;
				});
		}

		if ($stateParams.edit == 1) {
			$scope.edit = true;
		}

		if ($stateParams.audit == 1) {
			$scope.canAudit = true;
		}

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.auditForm.id = $stateParams.id;
			$scope.auditForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/purchase/order/returnNote/audit",
				method: "POST",
				data: $scope.auditForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("审批完成...");
					$scope.submitting = false;
					$state.go("oam.return-note-list", {audit:1});
				})
				.error(function (data, status, headers, config) {
				    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
					alert(errMsg + "审批失败...");
					$scope.submitting = false;
				});
		};

		$scope.getAvailableDepots = function (cityId) {
			$http.get("/admin/api/depot/list/" + cityId)
				.success(function (data, status, headers, config) {
					$scope.depots = data;
				});
		};

		$scope.modifyReturnPrice = function(returnPrice, item) {
			item.returnPrice = returnPrice;
		};

		$scope.calculateReturnQuantity = function(returnQuantity, item) {
			item.returnQuantity = returnQuantity;
		};

		$scope.calculateReturnTotal = function() {
			$scope.returnTotal = 0;
			angular.forEach($scope.returnNoteItems, function(item, key){
				var REGEX = /^\-?\d+(.\d+)?$/
				if (!item.returnPrice || !REGEX.test(item.returnPrice)) {
					item.returnPrice = 0;
				}

				if (!item.returnQuantity || !REGEX.test(item.returnQuantity)) {
					item.returnQuantity = 0;
				}

				$scope.returnTotal = $scope.returnTotal + item.returnPrice * item.returnQuantity;
			});

			$scope.returnTotal = ($scope.returnTotal).toFixed(6);
		};
	});