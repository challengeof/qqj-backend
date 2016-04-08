angular
		.module('sbAdminApp')
		.controller(
				'SkuSaleDetail',
				function($scope, $rootScope, $http, $filter, $stateParams,$window) {

					/* 订单明细列表搜索表单数据 */
					$scope.skuSaleDetailSearchForm = {

					};

                    if($rootScope.user) {
                         var data = $rootScope.user;
                         $scope.cities = data.cities;
                         if ($scope.cities && $scope.cities.length == 1) {
							$scope.skuSaleDetailSearchForm.cityId = $scope.cities[0].id;
						 }
                    }

					$scope.$watch('skuSaleDetailSearchForm.cityId', function(newVal, oldVal) {
						if(newVal){
						   $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
							   $scope.organizations = data;
							   if ($scope.organizations && $scope.organizations.length == 1) {
								  $scope.skuSaleDetailSearchForm.organizationId = $scope.organizations[0].id;
							   }
						   });
						   $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
							   $scope.depots = data;
							   if ($scope.depots && $scope.depots.length == 1) {
								   $scope.skuSaleDetailSearchForm.depotId = $scope.depots[0].id;
							   }
						   });
						   $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
							   $scope.availableWarehouses = data;
							   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
								   $scope.skuSaleDetailSearchForm.warehouseId = $scope.availableWarehouses[0].id;
							   }
						   });
						   if(typeof oldVal != 'undefined' && newVal != oldVal){
							   $scope.skuSaleDetailSearchForm.organizationId = null;
							   $scope.skuSaleDetailSearchForm.depotId = null;
							   $scope.skuSaleDetailSearchForm.warehouseId = null;
						   }
					   }else{
						   $scope.organizations = [];
						   $scope.depots = [];
						   $scope.availableWarehouses = [];
						   $scope.skuSaleDetailSearchForm.organizationId = null;
						   $scope.skuSaleDetailSearchForm.depotId = null;
						   $scope.skuSaleDetailSearchForm.warehouseId = null;
					   }
					});


					$scope.$watch('skuSaleDetailSearchForm.organizationId', function(organizationId) {
						   if (organizationId) {
							   $http({
								   url: "/admin/api/vendor",
								   method: "GET",
								   params: {'organizationId':organizationId}
							   }).success(function (data) {
									$scope.vendors = data.vendors;
							   });

							   if (typeof old != 'undefined' && cityId != old) {
								   $scope.skuSaleDetailSearchForm.vendorId = null;
							   }
						   } else {
							   $scope.vendors = [];
							   $scope.skuSaleDetailSearchForm.vendorId = null;
						   }
						});


					$scope.openStart = function($event) {
						$event.preventDefault();
						$event.stopPropagation();
						$scope.openedStart = true;
					};


					$scope.openEnd = function($event) {
						$event.preventDefault();
						$event.stopPropagation();
						$scope.openedEnd = true;
					};

					$scope.dateOptions = {
						dateFormat : 'yyyy-MM-dd',
						formatYear : 'yyyy',
						startingDay : 1,
						startWeek : 1
					};

					$scope.format = 'yyyy-MM-dd';

					$scope.page = {
						itemsPerPage : 100
					};

					$scope.$watch('startDate', function(d) {
						$scope.skuSaleDetailSearchForm.start = $filter('date')(
								d, 'yyyy-MM-dd');
					});

					$scope.$watch('endDate', function(d) {
						$scope.skuSaleDetailSearchForm.end = $filter('date')(d,
								'yyyy-MM-dd');
					})

					$scope.skus  = {};
					/* 获取品牌 */
					$http.get("/admin/api/brand").success(
							function(data, status, headers, config) {
								$scope.brands = data;
							}).error(function(data, status) {
						alert("数据加载失败！");
					});

					$scope.search = function() {
						$http({
							method : 'GET',
							url : '/admin/api/sku/sales',
							params : $scope.skuSaleDetailSearchForm

						}).success(function(data, status, headers, config) {
							$scope.skus = data.skuSales;
							
							$scope.page.itemsPerPage = data.pageSize;
			                $scope.page.totalItems = data.total;
			                $scope.page.currentPage = data.page + 1;
						})

					};

					$scope.search();
					$scope.pageChanged = function() {
						$scope.skuSaleDetailSearchForm.page = $scope.page.currentPage - 1;
						$scope.skuSaleDetailSearchForm.pageSize = $scope.page.itemsPerPage;

						$scope.search();
					}
					
					
					 $scope.excelExport = function(){
				            var str = [];
				            for(var p in $scope.skuSaleDetailSearchForm) {
				                if($scope.skuSaleDetailSearchForm[p]) {
				                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.skuSaleDetailSearchForm[p]));
				                }
				            }

				        	$window.open("/admin/api/sku-sale-detail/excelExport?" + str.join("&"));
				        };

				});
