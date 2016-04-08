'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.products = [];
        $scope.edit = false;

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.productSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            skuId: $stateParams.skuId,
            productName: $stateParams.productName,
            cityId : $stateParams.cityId,
            organizationId : $stateParams.organizationId
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.submitDateFormat = "yyyy-MM-dd";

        $http.get("/admin/api/organization?enable=true")
        .success(function (data, status, headers, config) {
            $scope.organizations = data.organizations;
            if ($scope.organizations && $scope.organizations.length == 1) {
                $scope.productSearchForm.organizationId = $scope.organizations[0].id;
            }
        })
        .error(function (data, status) {
            alert("数据加载失败！");
        });

/*
        $scope.$watch('productSearchForm.cityId', function(cityId, old) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;
                });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.productSearchForm.organizationId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.productSearchForm.organizationId = null;
            }
        });*/

        $scope.$watch('productSearchForm.organizationId', function(oldValue, newValue) {
            if(oldValue) {
                $http.get("/admin/api/category")
                    .success(function (data, status, headers, config) {
                        $scope.categories = data;
                    });
                if (typeof old != 'undefined' && cityId != old) {
                    $scope.productSearchForm.categoryId = null;
                }
            } else {
                $scope.categories = [];
                $scope.productSearchForm.categoryId = null;

            }
        });

        if($stateParams.productId) {
            $scope.productSearchForm.productId = parseInt($stateParams.productId);
        }

        if($stateParams.skuId) {
            $scope.productSearchForm.skuId = parseInt($stateParams.skuId);
        }

        if ($stateParams.productName) {
            $scope.productSearchForm.productName = $stateParams.productName;
        }

        if($stateParams.brandId) {
            $scope.productSearchForm.brandId = parseInt($stateParams.brandId);
        }

        if($stateParams.categoryId) {
            $scope.productSearchForm.categoryId = parseInt($stateParams.categoryId);
        }

        if($stateParams.cityId) {
            $scope.productSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.organizationId) {
            $scope.productSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.status) {
            $scope.productSearchForm.status = parseInt($stateParams.status);
        }

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.status = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        /*获取品牌*/
        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        /*获取餐馆列表信息*/
        $scope.searchProduct = function () {
            $location.search($scope.productSearchForm);

            $http({
                url: "/admin/api/sku",
                method: "GET",
                params: $scope.productSearchForm
            })
                .success(function (data, status, headers, config) {
                    $scope.skus = data.skus;

                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });
        }

        $scope.searchProduct();

        $scope.resetPageAndSearchProduct = function(){
            $scope.productSearchForm.page = 0;
            $scope.productSearchForm.pageSize = 100;

            $scope.searchProduct();
        }


        $scope.pageChanged = function() {
            $scope.productSearchForm.page = $scope.page.currentPage - 1;
            $scope.productSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchProduct();
        }

        /*编辑保存商品名称、大箱包装数量*/
        $scope.saveProduct = function(data, productId) {
            return $http.put("/admin/api/product/" + productId, data);
        };

        $scope.$watch('excelMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.productListSize) {
                    		successSize = data.productListSize;
                    	}
                    	if(data.exceptionMsg) {
                    		exceptionMsgSize = data.exceptionMsg.length;
                    	}
                    	var msg = "成功导入"+ successSize +"件商品， 失败了"+ exceptionMsgSize +"件商品\n";
                    	if(data.headMsg) {
                    		msg += data.headMsg + "\n";
                    	}
                    	if(data.errorFileName) {
                    		console.log(data.errorFileName);
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
        
        $scope.$watch('photoMediaName', function(files) {
        	if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/photoImportByName',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                    	var successSize = 0;
                    	var exceptionMsgSize = 0;
                    	if(data.productListSize) {
                    		successSize = data.productListSize;
                    	}
                    	if(data.errorSize) {
                    		exceptionMsgSize = data.errorSize;
                    	}
                    	var msg = "成功导入"+ successSize +"张图片， 失败了"+ exceptionMsgSize +"张图片\n";
                    	if(data.errorMsg) {
                    		msg = data.errorMsg;
                    	}
                    	if(data.errorFileName) {
                    		console.log(data.errorFileName);
                    		msg += "是否下载错误文件?";

                    		if(confirm(msg)){
                    			$window.open("/admin/api/product/errorFile?fileName=" + data.errorFileName);
                    		}
                    	} else {
                    		alert(msg);
                    	}
                    	
                    }).error(function (data) {
                        alert("导入失败");
                    })
                }
            }
        });

        $scope.$watch('photoMediaId', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/product/photoImportById',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.productSearchForm
                    }).success(function (data) {
                        var successSize = 0;
                        var exceptionMsgSize = 0;
                        if(data.productListSize) {
                            successSize = data.productListSize;
                        }
                        if(data.errorSize) {
                            exceptionMsgSize = data.errorSize;
                        }
                        var msg = "成功导入"+ successSize +"张图片， 失败了"+ exceptionMsgSize +"张图片\n";
                        if(data.errorMsg) {
                            msg = data.errorMsg;
                        }
                        if(data.errorFileName) {
                            console.log(data.errorFileName);
                            msg += "是否下载错误文件?";

                            if(confirm(msg)){
                                $window.open("/admin/api/product/errorFile?fileName=" + data.errorFileName);
                            }
                        } else {
                            alert(msg);
                        }

                    }).error(function (data) {
                        alert("导入失败");
                    })
                }
            }
        });

        $scope.updateCapacityInBundle = function(sku) {
            $http({
                url: "/admin/api/sku/capacityInBundle/" + sku.id,
                method: "PUT",
                params: {capacityInBundle:sku.capacityInBundle},
                headers:{'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("修改成功");
            })
            .error(function (data) {
                alert("修改失败\n" + data.errmsg);
            });
        }
    });