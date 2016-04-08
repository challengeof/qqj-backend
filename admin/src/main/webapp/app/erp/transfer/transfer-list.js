'use strict';

angular.module('sbAdminApp')
	.controller('TransferListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location) {

		if ($stateParams.audit == 1) {
			$scope.audit = true;
		}

		$scope.searchForm = {};

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.submitDateFormat = "yyyy-MM-dd HH:mm";

		$scope.openStart = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedStart = true;
		};

		$scope.openEnd = function ($event) {
			$event.preventDefault();
			$event.stopPropagation();
			$scope.openedEnd = true;
		};

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

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

		$http.get("/admin/api/transfer/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.searchForm.pageSize = $scope.page.itemsPerPage;
			$scope.search();
		}

		$scope.search = function () {
			$scope.searchForm.pageSize = $scope.page.itemsPerPage;
			$scope.searchForm.type = $stateParams.type;
			if ($scope.audit) {
				$scope.searchForm.status = 2;
			}
			$http({
				url: "/admin/api/transfer/list",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.transfers = data.content;
				$scope.page.totalItems = data.total;
				$scope.page.currentPage = data.page + 1;
			})
			.error(function (data, status, headers, config) {
				alert("加载失败...");
			});
		}

		$scope.submitTransfer = function (transfer) {
			$http({
				url: "/admin/api/transfer/submit/" + transfer.id,
				method: "GET",
			})
			.success(function (data, status, headers, config) {
				transfer.status.name = '待审核';
				transfer.showViewButton = true;
				transfer.hideEditButton = true;
				transfer.hideSubmitButton = true;
				alert("提交审核成功...");
			})
			.error(function (data, status, headers, config) {
				var errMsg = '';
				if (data != null && data.errmsg != null) {
					errMsg = data.errmsg + ',';
				}
				alert(errMsg + "提交审核失败...");
			});
		}

		$scope.search();
	});