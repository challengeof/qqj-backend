'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderItemsListCtrl
 * @description
 * # OrderItemsListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderItemsListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window) {
        $scope.page = {
            itemsPerPage: 100
        };

        /*订单明细列表搜索表单数据*/
        $scope.orderItemsSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            start: $stateParams.start,
            end: $stateParams.end,
            restaurantId: $stateParams.restaurantId,
            skuId: $stateParams.skuId,
            productName:$stateParams.productName,
            restaurantName:$stateParams.restaurantName,
            warehouseId:$stateParams.warehouseId,
            orderId:$stateParams.orderId,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            orderType:$stateParams.orderType

        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderItemsSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/order/orderType/get")
            .success(function (data, status, headers, config) {
                $scope.orderTypes = data;
            }).error(function (data, status) {
            alert("订单状态加载失败！");
        });


        /*$scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';*/

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.isOpen = false;
        $scope.isOpen1 = false;
        $scope.openCalendar = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen = true;
        };
        $scope.openCalendar1 = function(e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isOpen1 = true;
        };

        $scope.orderItems = {};


        $scope.$watch('orderItemsSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderItemsSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderItemsSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderItemsSearchForm.organizationId = null;
                   $scope.orderItemsSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.availableWarehouses = [];
               $scope.orderItemsSearchForm.organizationId = null;
               $scope.orderItemsSearchForm.warehouseId = null;
           }
        });


        $http.get("/admin/api/order/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });


        if($stateParams.orderStatus){
            $scope.orderItemsSearchForm.orderStatus = parseInt($stateParams.orderStatus);
        }
         if($stateParams.cityId) {
            $scope.orderItemsSearchForm.cityId = parseInt($stateParams.cityId);
         }

         if($stateParams.organizationId){
             $scope.orderItemsSearchForm.organizationId = parseInt($stateParams.organizationId);
          }


        if($stateParams.warehouseId){
            $scope.orderItemsSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.orderType) {
            $scope.orderItemsSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.searchOrderItems = function () {

            $http({
                url: '/admin/api/order/item',
                method: "GET",
                params: $scope.orderItemsSearchForm
            }).success(function (data, status, headers, config) {
                $scope.orderItems = data.orderItems;

                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;

            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }
        $scope.resetPageAndSearchOrderItems = function () {
            $scope.orderItemsSearchForm.page = 0;
            $scope.orderItemsSearchForm.pageSize = 100;

            $location.search($scope.orderItemsSearchForm);
        }


        $scope.pageChanged = function() {
            $scope.orderItemsSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderItemsSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.orderItemsSearchForm);
        }

        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderItemsSearchForm) {
                if($scope.orderItemsSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderItemsSearchForm[p]));
                }
            }
            $window.open("/admin/api/order/item/export?" + str.join("&"));
        };

        $scope.searchOrderItems();
})