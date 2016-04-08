'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('AccountingPaymentWriteOffListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $state, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};
		$scope.searchForm.cancel = $stateParams.cancel;
		$scope.submitting = false;

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		$scope.cancel = $stateParams.cancel == "true";

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

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
						if ($scope.depots && $scope.depots.length == 1) {
						   $scope.searchForm.depotId = $scope.depots[0].id;
					   }
					});
				$http.get("/admin/api/city/" + newVal + "/organizations")
					.success(function(data) {
						$scope.organizations = data;
						if ($scope.organizations && $scope.organizations.length == 1) {
						  $scope.searchForm.organizationId = $scope.organizations[0].id;
					   }
					});

				$http.get("/admin/api/accounting/payment/methods/" + newVal)
					.success(function (data) {
						$scope.methods = data;
					})

				if(typeof oldVal != 'undefined' && newVal != oldVal){
					   $scope.searchForm.organizationId = null;
					   $scope.searchForm.depotId = null;
				   }
			} else {
				$scope.methods = [];
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

		$http.get("/admin/api/accounting/payable/writeOff/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$scope.search = function () {
			$scope.searchForm.page = 0;
			$location.search($scope.searchForm);
		}

		if ($scope.cancel) {
			$scope.searchForm.status = 1;
		}

		$http({
			url: "/admin/api/accounting/payable/writeOff/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalWriteOffAmount = data.totalWriteOffAmount;
			$scope.accountPayableWriteOffs = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.disableCancelWriteOff = function (cancelDate) {
			if (!cancelDate || !angular.isDate(cancelDate)) {
				return true;
			} else {
				return false;
			}
		}
		$scope.cancelWriteOff = function (id, cancelDate) {

			if (!cancelDate || !angular.isDate(cancelDate)) {
				alter();
			}

		    $scope.submitting = true;
			$http({
				url: "/admin/api/accounting/payable/writeOff/cancel",
				method: "POST",
				params: {"writeOffId":id, "cancelDate" : cancelDate},
				headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("取消核销成功...");
				$scope.submitting = false;
				$state.go($state.current, $scope.searchForm, {reload: true});
			})
			.error(function (data, status, headers, config) {
				alert("取消核销失败...");
				$scope.submitting = false;
			});
		};

		$scope.exportAccountPayableWriteOffs = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			$window.open("/admin/api/accounting/payable/writeOff/exportAccountPayableWriteOffs?" + str.join("&"));
		}

		$http.get("/admin/api/accounting/payable/types")
			.success(function(data) {
				$scope.accountPayableTypes = data;
			});
	});