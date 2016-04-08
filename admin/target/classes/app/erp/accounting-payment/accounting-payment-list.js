'use strict';

angular.module('sbAdminApp')
	.controller('AccountingPaymentListCtrl', function($scope, $rootScope, $http, $filter, $location, $stateParams, $window) {

		$scope.page = {
			itemsPerPage: 20
		};

		$scope.searchForm = {pageSize : $scope.page.itemsPerPage};
		$scope.searchForm.cancel = $stateParams.cancel;

		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
		}

		if($rootScope.user) {
			var data = $rootScope.user;
			$scope.cities = data.cities;
			if ($scope.cities && $scope.cities.length == 1) {
			   $scope.searchForm.cityId = $scope.cities[0].id;
			}
		}


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
		$scope.date = new Date().toLocaleDateString();

		$scope.$watch('searchForm.startDate', function(d) {
			if(d){
				$scope.searchForm.startDate = $filter('date')(d, 'yyyy-MM-dd');
			}
		});

		$scope.$watch('searchForm.endDate', function(d) {
			if(d){
				$scope.searchForm.endDate= $filter('date')(d, 'yyyy-MM-dd');
			}
		});

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
					params:{cityId:$scope.searchForm.cityId,organizationId:newVal}
				}).success(function (data) {
					$scope.vendors = data.vendors;
				});
			} else {
				$scope.vendors = [];
			}
		});

		$http.get("/admin/api/accounting/payment/statuses")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$location.search($scope.searchForm);
		}

		$http({
			url: "/admin/api/accounting/payment/list",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.totalAmount = data.totalAmount;
			$scope.payments = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});

		$scope.cancelPayment = function (payment) {
			$http({
				url: "/admin/api/accounting/payment/cancel",
				method: "POST",
				params: {"id":payment.id},
				headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
			})
			.success(function (data, status, headers, config) {
				alert("付款录入取消成功...");
				payment.hideCancel = true;
			})
			.error(function (data, status, headers, config) {
				alert("付款录入取消失败...");
			});
		};

		$scope.exportPayment = function(){
			var str = [];
			for(var p in $scope.searchForm) {
				if($scope.searchForm[p] != null) {
					str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
				}
			}

			if (str.length == 0) {
				$window.open("/admin/api/accounting/payment/export");
			} else {
				$window.open("/admin/api/accounting/payment/export?" + str.join("&"));
			}
		}
	});