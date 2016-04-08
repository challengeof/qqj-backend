'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceDetailCtrl
 * @description
 * # DynamicPriceDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceDetailCtrl', function ($scope, $http, $stateParams) {

        $scope.formData = {};

        $scope.format = 'yyyy-MM-dd';

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            startingDay: 1
        };

        $http.get("/admin/api/city/warehouses/" + $stateParams.warehouseId)
            .success(function (data, status, headers, config) {
                $scope.warehouses = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.formData.skuId = $stateParams.skuId;

        $http.get("/admin/api/sku/" + $stateParams.skuId).success(function (data) {
            $scope.sku = data;
        });

        if($stateParams.warehouseId) {
            $scope.formData.warehouseId = parseInt($stateParams.warehouseId, 10); ;
        }

        $scope.$watch(function () {
            return $scope.formData.warehouseId
        }, function () {
            if ($scope.formData.warehouseId) {
                $http({
                    method: 'GET',
                    url: "/admin/api/dynamic-price/unique",
                    params: {
                        skuId: $stateParams.skuId,
                        warehouseId: $scope.formData.warehouseId
                    }
                }).success(function (data) {
                    if (data) {
                        if (data.singleDynamicSkuPriceStatus) {
                            $scope.formData.singleSalePrice = data.singleDynamicSkuPriceStatus.singleSalePrice;
                            $scope.formData.singleAvailable = data.singleDynamicSkuPriceStatus.singleAvailable;
                            $scope.formData.singleInSale = data.singleDynamicSkuPriceStatus.singleInSale;
                        }
                        if (data.bundleDynamicSkuPriceStatus) {
                            $scope.formData.bundleSalePrice = data.bundleDynamicSkuPriceStatus.bundleSalePrice;
                            $scope.formData.bundleAvailable = data.bundleDynamicSkuPriceStatus.bundleAvailable;
                            $scope.formData.bundleInSale = data.bundleDynamicSkuPriceStatus.bundleInSale;
                        }
                        $scope.formData.fixedPrice = data.fixedPrice;
                    }
                });
            }
        });


        $scope.saveDynamicPrice = function () {
            $http({
                method: 'post',
                url: '/admin/api/dynamic-price-temp',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
                .success(function (data) {
                    alert("保存成功!");
                })
                .error(function (data) {
                    alert("保存失败!");
                });
        }

        $scope.disableForm = function () {
            if ($scope.formData.effectType != null) {
                if ($scope.formData.effectType) {
                    return false;
                } else {
                    if ($scope.formData.effectTime != null) {
                        return false;
                    }
                }
            }

            return true;
        }
    });
