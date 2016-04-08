'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('DynamicPriceListTempCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state,
    editableOptions,
    $upload, $window) {
        editableOptions.theme = 'bs3';

        $scope.changeDetailResponses = [];

        $scope.page = {
            itemsPerPage: 100
        };

        $scope.dynamicPriceSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            status:$stateParams.status,
            skuName:$stateParams.skuName
        };

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


        $scope.resetPageAndSearchDynamicPrice = function () {
            $scope.dynamicPriceSearchForm.page = 0;
            $scope.dynamicPriceSearchForm.pageSize = 100;

            $state.go($state.current, $scope.dynamicPriceSearchForm, {reload: true});
        }
        if($stateParams.status) {
            $scope.dynamicPriceSearchForm.status = parseInt($stateParams.status);
        }
        if($stateParams.organizationId){
            $scope.dynamicPriceSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.cityId){
            $scope.dynamicPriceSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if($stateParams.warehouseId){
            $scope.dynamicPriceSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }
        if($stateParams.skuName){
            $scope.dynamicPriceSearchForm.skuName = $stateParams.skuName;
        }
        if($stateParams.submitRealName){
            $scope.dynamicPriceSearchForm.submitRealName = $stateParams.submitRealName;
        }
        if($stateParams.checkRealName){
            $scope.dynamicPriceSearchForm.checkRealName = $stateParams.checkRealName;
        }

        $scope.searchDynamicPrice = function () {
            $location.search($scope.dynamicPriceSearchForm);
        }
        $http({
            url: "/admin/api/dynamic-price-temp",
            method: "GET",
            params: $scope.dynamicPriceSearchForm
        }).success(function (data) {
            $scope.changeDetailResponses = data.dynamicPriceTempResponses;


            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });
        /*$scope.searchDynamicPrice();*/
        $http.get("/admin/api/check/status")
            .success(function (data) {
                $scope.checkStatuss = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.checkThrough = function (changeDetailResponse, status) {

            $http({
                method: 'POST',
                url: '/admin/api/dynamic-price/' + changeDetailResponse.id,
                params: {status: status},
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
                .success(function () {
                    changeDetailResponse.status = 2;
                    window.alert("审核成功!");
                })
                .error(function () {
                    window.alert("审核失败！");
                });
        }

 /*全选、反选*/
    $scope.formData = {
            
            changeDetailIds:[],
            status:2
            
        };

        $scope.isCheckedAll = false;
        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                angular.forEach($scope.changeDetailResponses, function(value, key){
                    if(value.status == 0){
                        $scope.formData.changeDetailIds.push(value.id);
                    }
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.changeDetailIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.batchUpdate = function(status){
             $scope.formData.status = status;
            $http({
               
                method:'POST',
                url:'/admin/api/dynamic-price/batchUpdate',
                data:$scope.formData
                
            })
                .success(function() {
                    angular.forEach($scope.changeDetailResponses, function(value, key){
                          for(var i = 0;i < $scope.formData.changeDetailIds.length;i++){
                            if((value.id == $scope.formData.changeDetailIds[i])){
                                value.status = status;
                            }
                          }
                    });
                    $scope.formData.changeDetailIds = [];
                    window.alert("审核成功!");
                })
                .error(function() {
                $scope.formData.status = 0;
                    window.alert("审核失败！");
                });
        };



        $scope.pageChanged = function () {
            $scope.dynamicPriceSearchForm.page = $scope.page.currentPage - 1;
            $scope.dynamicPriceSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchDynamicPrice();
        }


        $scope.checkAvailable = function(){
            return $scope.formData.changeDetailIds.length == 0;
        }

        $scope.checkVendorDiff = function(changeDetailResponse){
            if(changeDetailResponse.originDynamicSkuPriceWrapper.vendor != null && changeDetailResponse.dynamicSkuPriceWrapper.vendor == null )
                return true;
            if(changeDetailResponse.originDynamicSkuPriceWrapper.vendor == null && changeDetailResponse.dynamicSkuPriceWrapper.vendor != null )
                return true;
//            return changeDetailResponse.originDynamicSkuPriceWrapper.vendor.name != changeDetailResponse.dynamicSkuPriceWrapper.vendor.name;
        }

    });