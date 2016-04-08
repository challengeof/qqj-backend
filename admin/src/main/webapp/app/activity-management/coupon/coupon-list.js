'use strict';

angular.module('sbAdminApp')
	.controller('CouponListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
				$scope.searchForm.cityId = $scope.cities[0].id;
			}
		}

		$http.get("/admin/api/coupon/couponEnums")
			.success(function (data, status, headers, config) {
				$scope.couponTypes = data;
			});

		$http.get("/admin/api/purchase/order/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$http({
			url: "/admin/api/coupon",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.coupons = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.couponTypeFilter = function(item) {
			if($rootScope.hasPermission('coupon-list')) {
				return true;
			} else if ($rootScope.hasPermission('custom-service-coupon-list')) {
				return item.type == 6;
			} else {
				return false;
			}
		}

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}
	});