'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:DynamicPriceListCtrl
 * @description
 * # DynamicPriceListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SkuVendorListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, editableOptions,
    $upload, $window) {

        $scope.page = {
            itemsPerPage: 100
        };

        $scope.searchForm = {status:2, pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $http.get("/admin/api/category")
            .success(function (data, status, headers, config) {
                $scope.categories = data;
            })

        $http.get("/admin/api/brand")
            .success(function (data, status, headers, config) {
                $scope.brands = data;
            });

        $http({
            url: "/admin/api/skuVendor/list",
            method: "GET",
            params: $scope.searchForm
        }).success(function (data) {
            $scope.skuVendorList = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
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
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.searchForm.organizationId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.searchForm.organizationId = null;
           }
        });

        $http.get("/admin/api/sku/status").success(function(data) {
            $scope.skuStatuses = data;
        });

        $scope.$watch('searchForm.organizationId', function(organizationId) {
            if(organizationId) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId:$scope.searchForm.cityId,organizationId:organizationId}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        $scope.search = function() {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        }

        $scope.pageChanged = function() {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        }

        $scope.$watch('dynamicMedia', function(files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/sku-price/excelImport',
                        method: 'POST',
                        file: files[i],
                        fields: $scope.searchForm
                    })
                    .success(function (data) {
                        alert(data.message);
                    })
                    .error(function (data) {
                        alert("任务创建失败");
                    })
                }
            }
        });

        $scope.downloadTemplate = function(){
            $window.open("/admin/api/sku-price/downloadTemplate");
        }
    });