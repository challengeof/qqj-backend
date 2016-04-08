'use strict';

angular.module('sbAdminApp')
	.controller('SkuPriceHistoryList', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		$scope.page = {
			itemsPerPage: 50
		};

		$scope.type = parseInt($stateParams.type);
		$scope.searchForm = {type: parseInt($stateParams.type), single: $stateParams.single, skuId: parseInt($stateParams.skuId), pageSize: parseInt($scope.page.itemsPerPage)};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$http({
			url:"/admin/api/purchase/order/sku",
			method:'GET',
			params:{cityId:parseInt($stateParams.cityId), skuId:$scope.searchForm.skuId}
		}).success(function (data, status, headers, config) {
			var singleOrBundle = '';
			if ($stateParams.single=='true') {
				singleOrBundle = '(单品)';
			} else if ($stateParams.single=='false') {
				singleOrBundle = '(打包)';
			}
			$scope.title = data.sku.id + '-' + data.sku.name + singleOrBundle;
		});

		$http({
			url: "/admin/api/skuPriceHistory/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.skuPriceHistoryList = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;

			$scope.showChart = data.labels && data.data && data.labels.length > 0 && data.data.length > 0;
			if ($scope.showChart) {
				$scope.labels = data.labels;
				$scope.series = ['价格历史'];
				$scope.data = [
					data.data
				];
			}
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
	});