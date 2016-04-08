'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductDetailCtrl', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            skuRequests: [],
            organization : [],
            mediaFiles:[],
            mediaFileIds:[]
        };

        $http.get("/admin/api/organization?enable=true")
        .success(function (data, status, headers, config) {
            $scope.organizations = data.organizations;
            if ($scope.organizations && $scope.organizations.length == 1) {
                $scope.formData.organizationId = $scope.organizations[0].id;
            }
        })
        .error(function (data, status) {
            alert("数据加载失败！");
        });

        $http.get("/admin/api/sku/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http.get("/admin/api/sku/skuSingleUnit")
            .success(function (data, status, headers, config) {
                $scope.skuSingleUnit = data;
            })

        $http.get("/admin/api/sku/skuBundleUnit")
            .success(function (data, status, headers, config) {
                $scope.skuBundleUnit = data;
            })

        $http.get("/admin/api/sku/rateValues")
            .success(function (data, status, headers, config) {
                $scope.rateValues = data;
            })




        /*品牌*/
        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.$watch('formData.cityId', function(cityId, old) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;
                });

                if (typeof old != 'undefined' && cityId != old) {
                    $scope.formData.organizationId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.formData.organizationId = null;
            }
        });

        $scope.$watch('formData.organizationId', function(newValue, oldValue) {
            if(newValue != null && newValue != '') {
                $http.get("/admin/api/category")
                    .success(function (data, status, headers, config) {
                        $scope.categories = data;
                    });
                if (typeof oldValue != 'undefined' && oldValue != null && newValue != oldValue) {
                    $scope.formData.categoryId = null;
                }
            } else {
                $scope.categories = [];
                $scope.formData.categoryId = null;

            }
        });



        if ($stateParams.id) {
            $http.get("/admin/api/product/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                if (data.category) {
                    $scope.formData.categoryId = data.category.id;
                }
                $scope.formData.properties=data.properties;
                $scope.formData.details=data.details;

                if (data.brand) {
                    $scope.formData.brandId = data.brand.id;
                }
                if(data.mediaFiles) {
                    $scope.formData.mediaFiles = data.mediaFiles;
                    angular.forEach(data.mediaFiles, function(value) {
                        $scope.formData.mediaFileIds.push(value.id);
                    });
                }

                $scope.formData.capacityInBundle = data.capacityInBundle;
                $scope.formData.barCode = data.barCode;
                $scope.formData.discrete = data.discrete;
                $scope.formData.specification = data.specification;
                $scope.formData.shelfLife = data.shelfLife;

                if(data.skus) {
                    $scope.formData.skuRequest = data.skus[0];
                    $scope.formData.skuRequest.status = $scope.formData.skuRequest.status.value;
                }
            });
        }else{

        }

        $scope.$watch('media', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        if($scope.formData.mediaFileIds.indexOf(data.id) == -1) {
                            $scope.formData.mediaFiles.push(data);
                            $scope.formData.mediaFileIds.push(data.id);
                        }
                        if ($scope.formData.mediaFileIds.length > 1) {
                            var defaultImg = $scope.formData.mediaFileIds.indexOf(7713);
                            if (defaultImg != -1) {
                                $scope.formData.mediaFileIds.splice(defaultImg, 1);
                                $scope.formData.mediaFiles.splice(defaultImg, 1);
                            }
                        }
                    })
                }
            }
        });

        $scope.deleteImg = function(id) {
            var index = $scope.formData.mediaFileIds.indexOf(id);
            if (index != -1) {
                $scope.formData.mediaFileIds.splice(index, 1);
                $scope.formData.mediaFiles.splice(index, 1);
            }
            if ($scope.formData.mediaFileIds.length < 1) {
                var defaultImg = 7713;
                var defaultImgUrl = "http://7xijms.com1.z0.glb.clouddn.com/default";
                $scope.formData.mediaFileIds.push(defaultImg);
                $scope.formData.mediaFiles.push({id:defaultImg,url:defaultImgUrl});
            }
        };

        /*提交保存*/
        $scope.saveProduct = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/productTemp/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function () {
                        alert("保存失败!");
                    });
            } else {
                $http({
                    url: "/admin/api/product-temp",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function () {
                        alert("保存失败!");
                    });
            }
        };
    });
