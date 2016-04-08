'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductListTempCtrl
 * @description
 * # ProductListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductListTempCtrl', function ($scope, $rootScope, $http, $stateParams, $location, editableOptions,
    $upload, $window, $state) {
        editableOptions.theme = 'bs3';

        $scope.productSearchForm = {
            productName: $stateParams.productName,
            organizationId:1
        }

        $scope.changeDetailResponses = [];

        $scope.page = {
            itemsPerPage: 100
        };
        if($stateParams.status) {
            $scope.productSearchForm.status = parseInt($stateParams.status);
        }
        if($stateParams.organizationId){
            $scope.productSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.cityId){
            $scope.productSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if($stateParams.productName){
            $scope.productSearchForm.productName = $stateParams.productName;
        }
        if($stateParams.pageSize) {
            $scope.productSearchForm.pageSize = $stateParams.pageSize;
        }
        if($stateParams.page) {
            $scope.productSearchForm.page = $stateParams.page;
        }
        if($stateParams.submitRealName) {
            $scope.productSearchForm.submitRealName = $stateParams.submitRealName;
        }
        if($stateParams.checkRealName) {
            $scope.productSearchForm.checkRealName = $stateParams.checkRealName;
        }

        $http.get("/admin/api/check/status")
            .success(function (data) {
                $scope.checkStatuss = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $http({
            url: "/admin/api/product-temp",
            method: "GET",
            params: $scope.productSearchForm
        })
            .success(function (data, status, headers, config) {
                $scope.changeDetailResponses = data.changeDetailResponses;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function (data, status, headers, config) {
                alert("加载失败...");
            });

        $scope.searchProduct = function () {
            $location.search($scope.productSearchForm);
        }


        $scope.checkThrough = function(changeDetailResponse,status) {

            $http({
                method: 'POST',
                url: '/admin/api/product/' + changeDetailResponse.id ,
                params:{status:status},
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
                .success(function() {
                    changeDetailResponse.status = 2;
                    window.alert("审核成功!");
                })
                .error(function() {
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
                method: 'POST',
                url:'/admin/api/product/batchUpdate',
                data: $scope.formData
            })
                .success(function(data) {
                     angular.forEach($scope.changeDetailResponses, function(value, key){
                          for(var i = 0;i < $scope.formData.changeDetailIds.length;i++){
                            if((value.id == $scope.formData.changeDetailIds[i])){
                                value.status = status;
                            }
                          }
                    });
                     $scope.formData.changeDetailIds = [];
                    if(data.errorNum == 0){
                        alert("全部审核成功");
                    }else{
                        window.alert("审核失败"+data.errorNum+"件")
                    }
                })
                .error(function() {
                    window.alert("审核失败！");
                });
        }
        /*$scope.searchProduct();*/

        $scope.pageChanged = function () {
            $scope.productSearchForm.page = $scope.page.currentPage - 1;
            $scope.productSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchProduct();
        }

        $scope.resetPageAndSearchProduct = function () {
            $scope.productSearchForm.page = 0;
            $scope.productSearchForm.pageSize = 100;
            $state.go($state.current, $scope.productSearchForm, {reload: true});
        }

        $scope.checkAvailable = function(){
            return $scope.formData.changeDetailIds.length == 0;
        }
         $scope.checkCategoryDiff = function(changeDetailResponse){
            if (changeDetailResponse.originProductWrapper) {
                if(changeDetailResponse.originProductWrapper.category != null && changeDetailResponse.productWrapper.category == null )
                    return true;
                if(changeDetailResponse.originProductWrapper.category == null && changeDetailResponse.productWrapper.category != null )
                    return true;
                return changeDetailResponse.originProductWrapper.category.hierarchyName !=
                changeDetailResponse.productWrapper.category.hierarchyName;
            }

        }
    });