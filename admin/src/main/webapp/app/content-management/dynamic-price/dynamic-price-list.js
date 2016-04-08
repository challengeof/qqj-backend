'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, editableOptions,
    $upload, $window) {
//        editableOptions.theme = 'bs3';

        $scope.dynamicPrices = [];

        $scope.page = {
            itemsPerPage : 100
        };


        $scope.dynamicPriceSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            productId: $stateParams.productId,
            productName: $stateParams.productName,
            skuId : $stateParams.skuId,
            cityId : $stateParams.cityId,
            organizationId : $stateParams.organizationId,
            categoryId : $stateParams.categoryId,
            status:$stateParams.status,
            singleAvailable:$stateParams.singleAvailable,
            singleInSale:$stateParams.singleInSale,
            bundleAvailable:$stateParams.bundleAvailable,
            bundleInSale:$stateParams.bundleInSale,
            pageType:$stateParams.pageType,
            skuCreateDate:$stateParams.skuCreateDate
        };

        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.skuStatus = data;
            })
            .error(function(data, status) {
                alert("数据加载失败");
            });

        if($stateParams.warehouseId) {
            $scope.dynamicPriceSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if($stateParams.brandId) {
            $scope.dynamicPriceSearchForm.brandId = parseInt($stateParams.brandId);
        }

        if($stateParams.cityId) {
            $scope.dynamicPriceSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.organizationId) {
            $scope.dynamicPriceSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.skuId) {
            $scope.dynamicPriceSearchForm.skuId = parseInt($stateParams.skuId);
        }

        if ($stateParams.categoryId) {
            $scope.dynamicPriceSearchForm.categoryId = parseInt($stateParams.categoryId);
        }

        if ($stateParams.status) {
            $scope.dynamicPriceSearchForm.status = parseInt($stateParams.status);
        } else {
            $scope.dynamicPriceSearchForm.status = 2;
        }

        if ($stateParams.singleAvailable) {
            $scope.dynamicPriceSearchForm.singleAvailable = eval($stateParams.singleAvailable);
        }

        if ($stateParams.singleInSale) {
            $scope.dynamicPriceSearchForm.singleInSale = eval($stateParams.singleInSale);
        }

        if ($stateParams.bundleAvailable) {
            $scope.dynamicPriceSearchForm.bundleAvailable = eval($stateParams.bundleAvailable);
        }

        if ($stateParams.bundleInSale) {
            $scope.dynamicPriceSearchForm.bundleInSale = eval($stateParams.bundleInSale);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.searchDynamicPrice = function() {
            if($scope.dynamicPriceSearchForm.warehouseId) {

                $location.search($scope.dynamicPriceSearchForm);

                $http({
                    url: "/admin/api/dynamic-price",
                    method: "GET",
                    params: $scope.dynamicPriceSearchForm
                }).success(function (data) {
                    $scope.dynamicPrices = data.content;

                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                });
            }
        }



        /*获取可选状态*/
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.dynamicPriceSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('dynamicPriceSearchForm.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.dynamicPriceSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.dynamicPriceSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.dynamicPriceSearchForm.organizationId = null;
                   $scope.dynamicPriceSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.availableWarehouses = [];
               $scope.dynamicPriceSearchForm.organizationId = null;
               $scope.dynamicPriceSearchForm.warehouseId = null;
           }
        });


        $scope.$watch('dynamicPriceSearchForm.organizationId', function(organizationId) {
           if (organizationId) {
               $http.get("/admin/api/category")
                   .success(function (data, status, headers, config) {
                       $scope.categories = data;
                   });

               if (typeof old != 'undefined' && cityId != old) {
                   $scope.dynamicPriceSearchForm.categoryId = null;
               }
           } else {
               $scope.dynamicPriceSearchForm.categoryId = null;
           }
        });


        $scope.resetPageAndSearchDynamicPrice = function () {
            $scope.dynamicPriceSearchForm.page = 0;
            $scope.dynamicPriceSearchForm.pageSize = 100;

            $scope.searchDynamicPrice();
        }

        $scope.searchDynamicPrice();

        $scope.pageChanged = function() {
            $scope.dynamicPriceSearchForm.page = $scope.page.currentPage - 1;
            $scope.dynamicPriceSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchDynamicPrice();
        }

        /*保存修改商品进价、售价*/
        $scope.savePrice = function(data, skuId, warehouseId) {
            angular.extend(data, {skuId: skuId,warehouseId:warehouseId});
            return $http.post('/admin/api/dynamic-price', data);
        };
        
        $scope.fastSavePrice = function(data,skuId,warehouseId) {
            angular.extend(data, {skuId: skuId,warehouseId:warehouseId});
            return $http.post('/admin/api/dynamic-price-temp/fast', data);
        };

        $scope.$watch('dynamicMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/dynamic-price/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.dynamicPriceSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.priceListSize) {
                    		successSize = data.priceListSize;
                    	}
                    	if(data.exceptionMsg) {
                    		exceptionMsgSize = data.exceptionMsg.length;
                    	}
                    	var msg = "成功导入"+ successSize +"件商品， 失败了"+ exceptionMsgSize +"件商品\n";
                    	if(data.headMsg) {
                    		msg += data.headMsg + "\n";
                    	}
                    	if(data.errorFileName) {
                    		msg += "是否下载错误文件?";

                    		if(confirm(msg)){
                    			$window.open("/admin/api/dynamic-price/errorFile?errorFileName=" + data.errorFileName);
                    		}
                    	} else {
                    		alert(msg);
                    	}
                        
                    }).error(function (data) {
                        alert("导入失败，excel格式错误");
                    })
                }
            }
        });
        
        $scope.downloadErrorFile = function(fileName) {
        	$window.open("/admin/api/dynamic-price/errorFile/" + fileName);
        }
        
        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.dynamicPriceSearchForm) {
                if($scope.dynamicPriceSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.dynamicPriceSearchForm[p]));
                }
            }

        	$window.open("/admin/api/dynamic-price/excelExport?" + str.join("&"));
        };
        
        $scope.synchronizeToEdb = function(id) {
        	$http({
                url: "/admin/api/dynamic-price-edb/" + id,
                method: "PUT",
            }).success(function (data) {
            	if (data != "") {
            		alert(data);
            	} else {
            		alert("同步成功!");
            	}
            });
        };

        $scope.showVendor = function(vendor, candidateVendors) {
            if (!vendor) {
                return '';
            }

            if (!candidateVendors) {
                return vendor.name;
            }

            var name = '';
            angular.forEach(candidateVendors, function(item, key){
                if (item.id==vendor.id) {
                    name = item.name;
                }
            });

            return name;
        };
    });