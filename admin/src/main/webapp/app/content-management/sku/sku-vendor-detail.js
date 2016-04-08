'use strict';

angular.module('sbAdminApp')
	.controller('SkuVendorDetailCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

		$scope.iForm = {"fixedPrice":0, "salePriceLimit":0, changeFixedPriceReason:"新增", skuId:parseInt($stateParams.skuId)};

		$scope.showReason = false;

		$scope.originFixedPrice = 0;
		$scope.originSalePriceLimitPrice = 0;

		if($rootScope.user) {
			$scope.cities = $rootScope.user.cities;
		}

		if ($stateParams.id) {
			$scope.iForm.skuVendorId = parseInt($stateParams.id);
		}

		if ($stateParams.id) {
			$http.get("/admin/api/skuVendor/" + $stateParams.id)
			.success(function (data, status) {
				$scope.iForm.vendorId = data.vendor.id;
				$scope.iForm.organizationId = data.vendor.organization.id;
				$scope.iForm.fixedPrice = data.fixedPrice;
				$scope.iForm.singleSalePriceLimit = data.singleSalePriceLimit;
				$scope.iForm.bundleSalePriceLimit = data.bundleSalePriceLimit;
				$scope.originFixedPrice = data.fixedPrice;
				$scope.originSingleSalePriceLimitPrice = data.singleSalePriceLimit;
				$scope.originBundleSalePriceLimitPrice = data.bundleSalePriceLimit;
			})
			.error(function (data, status) {
				alert("获取sku信息失败...");
			});
		} else {
			$scope.showChangeFixedPriceReason = true;
			$scope.showChangeSalePriceLimitReason = true;
			$scope.iForm.changeSalePriceLimitReason = "新增";
		}

		$scope.$watch('iForm.cityId', function(newVal, oldVal) {
			if(newVal){
				$http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
					$scope.organizations = data;
					if ($scope.organizations && $scope.organizations.length == 1) {
						$scope.iForm.organizationId = $scope.organizations[0].id;
					}
				});
			}else{
				$scope.organizations = [];
			}
		});

		$scope.$watch('iForm.organizationId', function(newVal, oldVal) {
			if(newVal) {
				$http({
					url:"/admin/api/vendor",
					method:'GET',
					params:{cityId:$scope.iForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.iForm.vendorId = null;
				$scope.vendors = [];
			}
		});

		$scope.$watch('iForm.fixedPrice', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originFixedPrice && $stateParams.id){
				$scope.iForm.changeFixedPriceReason = null;
				$scope.showChangeFixedPriceReason = true;
			}
		});

		$scope.$watch('iForm.singleSalePriceLimit', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originSingleSalePriceLimitPrice && $stateParams.id){
				//$scope.iForm.changeSalePriceLimitReason = null;
				$scope.showChangeSalePriceLimitReason = true;
			}
		});

		$scope.$watch('iForm.bundleSalePriceLimit', function(newVal, oldVal) {
			if(newVal != null && newVal != $scope.originBundleSalePriceLimitPrice && $stateParams.id){
				//$scope.iForm.changeSalePriceLimitReason = null;
				$scope.showChangeSalePriceLimitReason = true;
			}
		});

		$scope.saveSkuVendor = function() {
			$http({
				url: "/admin/api/sku/updateSkuVendor",
				method: "POST",
				data: $scope.iForm,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			})
				.success(function (data, status, headers, config) {
					alert("保存成功...");
				})
				.error(function (data, status, headers, config) {
					alert("保存失败...");
				});
		};

	});