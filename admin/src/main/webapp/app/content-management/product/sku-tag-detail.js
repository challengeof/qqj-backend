'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuTagDetailCtrl', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            skuRequests: [],
            organization : [],
            cityIds:[],
            limitedCityIds:[],
            skuId:[]
        };

        $http.get("/admin/api/city")
        .success(function (data) {
            $scope.cities = data;
        });


        if ($stateParams.id) {
            $http.get("/admin/api/product/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                if (data.category) {
                    $scope.formData.categoryName = data.category.hierarchyName;
                }

                if(data.skus) {
                    $scope.formData.skuRequest = data.skus[0];
                    $scope.formData.skuRequest.status = $scope.formData.skuRequest.status.name;
                    $scope.formData.skuId = $scope.formData.skuRequest.id;
                    if ($scope.formData.skuRequest.skuTags) {
                        angular.forEach($scope.formData.skuRequest.skuTags, function(value, key) {
                            $scope.formData.cityIds.push(value.cityId);
                            if(value.limitedQuantity) {
                                $scope.formData.limitedCityIds.push(value.cityId);
                                $scope.formData.limitedQuantity = value.limitedQuantity;
                            }
                        })
                    }
                }
            });
        }else{

        }

        /*提交保存*/
        $scope.saveProduct = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/skuTag/sku/" + $scope.formData.skuId,
                    method: 'PUT',
                    params: {"cityIds":$scope.formData.cityIds,"limitedQuantity" : $scope.formData.limitedQuantity, "limitedCityIds" : $scope.formData.limitedCityIds},
                    contentType:"application/x-www-form-urlencoded"
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
