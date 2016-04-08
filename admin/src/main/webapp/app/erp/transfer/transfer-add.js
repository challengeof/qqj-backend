'use strict';

angular.module('sbAdminApp')
	.controller('TransferAddCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.searchForm = {};

		$scope.candidateSkus = [];

		$scope.funcAsync = function (name) {
			if (name && name !== "") {
				$scope.candidateSkus = [];
				$http.get("/admin/api/sku/candidates?name="+name).then(
					function (data) {
						$scope.candidateSkus = data.data;
					}
				)
			}
		}

		$scope.resetCandidateSkus = function () {
			$scope.candidateSkus = [];
		}

        $scope.submitting = false;
		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$scope.$watch('searchForm.cityId', function(newVal, oldVal) {
			if (newVal) {
				$http.get("/admin/api/depot/list/" + newVal)
					.success(function (data, status, headers, config) {
						$scope.depots = data;
					});
			} else {
				$scope.depots = [];
			}
		});

		$scope.submit = function () {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.items = $scope.items;
			$http({
				url: "/admin/api/transfer/add",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("保存成功...");
				$scope.submitting = false;
				$state.go("oam.transfer-list", {audit:0});
			})
			.error(function (data, status, headers, config) {
				alert("保存失败...");
				$scope.submitting = false;
				$scope.tableform.$show();
			});
		}

		$scope.items = [
		];

		$scope.remove = function(index) {
			$scope.items.splice(index, 1);
		};

		$scope.addItem = function() {
			$scope.inserted = {
			};
			$scope.items.push($scope.inserted);
		};

		$scope.searchSku = function(item) {
			$scope.candidateSkus = [];
			$http.get("/admin/api/transfer/sku/" + $scope.searchForm.cityId + "/" + item.skuId + "?depotIds=" + $scope.searchForm.sourceDepotId + "," + $scope.searchForm.targetDepotId)
				.success(function (data, status, headers, config) {
					$scope.candidateSkus.push(data.sku);
					item.name = data.sku.name;
					item.sourceDepotStock = data.stocks[0];
					item.targetDepotStock = data.stocks[1];
					item.singleUnit = data.sku.singleUnit;
				});
		};

		if ($stateParams.id) {
			$http.get("/admin/api/transfer/" + $stateParams.id)
				.success(function (data, status) {
					$scope.searchForm.cityId = data.cityId;
					$scope.searchForm.sourceDepotId = data.sourceDepot.id;
					$scope.searchForm.targetDepotId = data.targetDepot.id;
					$scope.searchForm.remark = data.remark;
					$scope.searchForm.opinion = data.opinion;
					$scope.items = data.items;
					angular.forEach($scope.items, function(item, key) {
						$scope.candidateSkus.push(item.sku);
					});
				})
				.error(function (data, status) {
					window.alert("获取采购单信息失败...");
					return;
				});
		}

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

		$scope.audit = function (approvalResult) {
		    $scope.submitting = true;
			$scope.searchForm.id = $stateParams.id;
			$scope.searchForm.approvalResult = approvalResult;
			$http({
				url: "/admin/api/transfer/audit",
				method: "POST",
				data: $scope.searchForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("审批完成...");
				$scope.submitting = false;
				$state.go("oam.transfer-list", {audit:1});
			})
			.error(function (data, status, headers, config) {
				var errMsg = '';
				if (data != null && data.errmsg != null) {
					errMsg = data.errmsg + ',';
				}
				alert(errMsg + "审批失败...");
				$scope.submitting = false;
			});
		}

		$scope.clearItems = function () {
			$scope.items = [];
		};
	});