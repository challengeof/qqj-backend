'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuTagListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.products = [];

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.productSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            skuId: $stateParams.skuId,
            productName: $stateParams.productName,
            skuTagCityId : $stateParams.skuTagCityId,
            organizationId : $stateParams.organizationId
        };

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

        $http.get("/admin/api/city")
            .success(function (data) {
                $scope.cities = data;
            });

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

        if($stateParams.skuTagCityId) {
            $scope.productSearchForm.skuTagCityId = parseInt($stateParams.skuTagCityId);
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
    });