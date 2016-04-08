'use strict';

angular.module('sbAdminApp')
	.controller('SellReturnCheckListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

		$scope.searchForm = {
			type : $stateParams.type,
			status : $stateParams.status,
			pageType:$stateParams.pageType
		};

		if ($scope.searchForm.pageType == 2) {
			$scope.searchForm.type = 2;
		}

		$scope.page = {
			itemsPerPage: 20
		};
		if ($stateParams.page) {
			$scope.searchForm.page = parseInt($stateParams.page);
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

		if($rootScope.user) {
		   var data = $rootScope.user;
			$scope.cities = data.depotCities;
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

		$http.get("/admin/api/sellReturn/type/list").success(function (data) {
			$scope.types = data;
		});
		$http.get("/admin/api/sellReturn/status")
			.success(function (data) {
				$scope.statuses = data;
			})

		$scope.pageChanged = function() {
			$scope.searchForm.page = $scope.page.currentPage - 1;
			$scope.search();
		}

		$scope.searchForm.pageSize = $scope.page.itemsPerPage;
		$scope.searchForm.type = $stateParams.type;
		$http({
			url: "/admin/api/sellReturn",
			method: "GET",
			params: $scope.searchForm
		})
		.success(function (data, status, headers, config) {
			$scope.sellReturns = data.content;
			$scope.page.totalItems = data.total;
			$scope.page.itemsPerPage = data.pageSize;
			$scope.page.currentPage = data.page + 1;
		})
		.error(function (data, status, headers, config) {
			alert("加载失败...");
		});


		$scope.search = function () {
			$location.search($scope.searchForm);
		}

		$scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellReturn/export/list?" + str.join("&"));
        };
	});