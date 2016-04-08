'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
	.controller('AccountingPayableListCtrl', ['$scope', '$rootScope', '$http', '$filter', '$stateParams', '$state', '$compile', '$window', '$location', function($scope, $rootScope, $http, $filter, $stateParams, $state, $compile, $window, $location) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if ($stateParams.edit) {
			$scope.searchForm.edit = parseInt($stateParams.edit);
		}

		if ($stateParams.edit == 1) {
			$scope.writeOffPayment = true;
			$scope.searchForm.includeWriteOff = false;
		} else {
			$scope.searchPayable = true;
			$scope.searchForm.includeWriteOff = true;
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
					});

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
					params:{organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/payable/statuses")
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

		$scope.accountPayablesToWriteOff = [];
		$scope.searchForm.checkedItemIds = [];
		$scope.isCheckedAll = false;
		$http({
			url: "/admin/api/accounting/payable/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalAmount = data.totalAmount;
			$scope.totalWriteOffAmount = data.totalWriteOffAmount;
			$scope.totalUnWriteOffAmount = data.totalUnWriteOffAmount;
			$scope.accountPayables = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.writeOff = function () {

			var invalid = false;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						var writeOffAmount = accountPayable.currentWriteOffAmount;
						if (Math.abs(writeOffAmount) > Math.abs(accountPayable.unWriteOffAmount)) {
							alert('本次销账金额大于未销金额');
							invalid = true;
						}

						if (writeOffAmount>=0 && writeOffAmount > accountPayable.balance) {
							alert('本次销账金额大于供应商账户余额');
							invalid = true;
						}
					}
				});
			});

			if (invalid) {
				return;
			}

		    $scope.submitting = true;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						$scope.accountPayablesToWriteOff.push(accountPayable);
					}
				});
			});
			$http({
				url: "/admin/api/accounting/payable/writeOff",
				method: "POST",
				data: $scope.accountPayablesToWriteOff,
			})
			.success(function (data, status, headers, config) {
				alert("核销成功...");
				$scope.submitting = false;
				//$location.search($scope.searchForm);
					$state.reload();
			})
			.error(function (data, status, headers, config) {
				alert("核销失败...");
				$scope.submitting = false;
			});
		};

		$scope.checkAll = function() {
			if(!$scope.isCheckedAll){
				$scope.searchForm.checkedItemIds = [];
				angular.forEach($scope.accountPayables, function(value, key){
					$scope.searchForm.checkedItemIds.push(value.id);
				});
				$scope.isCheckedAll = true;
			}else{
				$scope.searchForm.checkedItemIds = [];
				$scope.isCheckedAll = false;
			}
		};

		$scope.tableInvalid = function() {

			if (!$scope.searchForm.checkedItemIds || $scope.searchForm.checkedItemIds.length==0 || $scope.submitting) {
				return true;
			}

			var invalid = false;
			angular.forEach($scope.searchForm.checkedItemIds, function(checkedItemId, key) {
				angular.forEach($scope.accountPayables, function (accountPayable, key) {
					if (checkedItemId == accountPayable.id) {
						var writeOffAmount = accountPayable.currentWriteOffAmount;
						var writeOffDate = accountPayable.writeOffDate;
						var REGEX = /^\-?\d+(.\d+)?$/
						if (!writeOffDate || writeOffDate == "" || !angular.isDate(writeOffDate) ||!writeOffAmount || writeOffAmount == "" || !REGEX.test(writeOffAmount)) {
							invalid = true;
						}
					}
				});
			});

			return invalid;
		}

		$scope.exportAccountPayables = function(){
			if ($scope.writeOffPayment) {
				$scope.searchForm.includeWriteOff = false;
			} else {
				$scope.searchForm.includeWriteOff = true;
			}
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			if (str.length == 0) {
				$window.open("/admin/api/accounting/payable/exportAccountPayables");
			} else {
				$window.open("/admin/api/accounting/payable/exportAccountPayables?" + str.join("&"));
			}
		}

		$http.get("/admin/api/accounting/payable/types")
			.success(function(data) {
				$scope.accountPayableTypes = data;
			});

	}]);