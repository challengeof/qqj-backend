    'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StopOrdersCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window, $state) {

        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.orders = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            start: $stateParams.start,
            end: $stateParams.end,
            customerId: $stateParams.customerId,
            restaurantId: $stateParams.restaurantId,
            restaurantName: $stateParams.restaurantName,
            warehouseId: $stateParams.warehouseId,
            vendorName:$stateParams.vendorName,
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId,
            vendorId: $stateParams.vendorId,
            orderId:$stateParams.orderId,
            coordinateLabeled:$stateParams.coordinateLabeled,
            refundsIsNotEmpty:$stateParams.refundsIsNotEmpty,
            depotId:$stateParams.depotId,
            blockId:$stateParams.blockId,
            orderType:$stateParams.orderType,
            status:3,
            type:4 //地图类型
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderListSearchForm.cityId = $scope.cities[0].id;
            }
       }

        $scope.submitting = false;

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


        $scope.format = 'yyyy-MM-dd HH:mm';
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.date = new Date().toLocaleDateString();

        $scope.page = {
            itemsPerPage: 100
        };

            /*订单状态*/
      $http.get("/admin/api/order/status")
           .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
           }).error(function (data, status) {
                alert("订单状态加载失败！");
       });

       $http.get("/admin/api/order/orderType/get")
          .success(function (data, status, headers, config) {
               $scope.orderTypes = data;
          }).error(function (data, status) {
               alert("订单状态加载失败！");
      });

        $scope.$watch('orderListSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.orderListSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.orderListSearchForm.depotId = null;
                }
            }else{
                $scope.depots = [];
                $scope.orderListSearchForm.depotId = null;
            }
        });

        $scope.$watch('orderListSearchForm.depotId', function(newVal, oldVal) {
            if(newVal){
                $http.get("/admin/api/warehouse/depot/" + newVal).success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderListSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.warehouseId = null;
               }
            }else {
                $scope.availableWarehouses = [];
                $scope.orderListSearchForm.warehouseId = null;
            }
        });

        $scope.$watch('orderListSearchForm.warehouseId', function(newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/block/warehouse/" + newVal).success(function (data) {
                   $scope.blocks = data;
                   if ($scope.blocks && $scope.blocks.length == 1) {
                       $scope.orderListSearchForm.blockId = $scope.blocks[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.blockId = null;
               }
            } else {
                $scope.blocks = [];
                $scope.orderListSearchForm.blockId = null;
            }
        });

        if($stateParams.sortField) {
            $scope.orderListSearchForm.sortField = $stateParams.sortField;
        } else {
            $scope.orderListSearchForm.sortField = "id";
        }

        if($stateParams.asc) {
            if ($stateParams.asc == 'true') {
                $scope.orderListSearchForm.asc = true;
            }
            if ($stateParams.asc == 'false') {
                $scope.orderListSearchForm.asc = false;
            }
        }

        if($scope.orderListSearchForm.start) {
            $scope.startDate = Date.parse($scope.orderListSearchForm.start);
        }

        if($scope.orderListSearchForm.end) {
            $scope.endDate = Date.parse($scope.orderListSearchForm.end);
        }

        if($stateParams.status) {
            $scope.orderListSearchForm.status = parseInt($stateParams.status);
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        if($stateParams.warehouseId) {
            $scope.orderListSearchForm.warehouseId = parseInt($stateParams.warehouseId);
        }

        if($stateParams.cityId) {
            $scope.orderListSearchForm.cityId = parseInt($stateParams.cityId);
         }

        if($stateParams.organizationId){
            $scope.orderListSearchForm.organizationId = parseInt($stateParams.organizationId);
        }

        if($stateParams.orderId){
            $scope.orderListSearchForm.orderId = parseInt($stateParams.orderId);
        }

        if($stateParams.coordinateLabeled){
            $scope.orderListSearchForm.coordinateLabeled = parseInt($stateParams.coordinateLabeled);
        }
        if($stateParams.refundsIsNotEmpty){
            $scope.orderListSearchForm.refundsIsNotEmpty = true;
        }
        if($stateParams.depotId){
            $scope.orderListSearchForm.depotId = parseInt($stateParams.depotId);
        }
        if($stateParams.blockId){
            $scope.orderListSearchForm.blockId = parseInt($stateParams.blockId);
        }
        if($stateParams.orderType){
            $scope.orderListSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.$watch('startDate', function(d) {
           $scope.orderListSearchForm.start = $filter('date')(d, 'yyyy-MM-dd HH:mm');
        });

        $scope.$watch('endDate', function(d) {
            $scope.orderListSearchForm.end= $filter('date')(d, 'yyyy-MM-dd HH:mm');
        });

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;
//            $scope.searchOrderList();
            $state.go($state.current, $scope.orderListSearchForm, {reload: true});
        }


        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrderList();
        }

        $scope.searchOrderList = function () {
           $location.search($scope.orderListSearchForm);

        }
        if ($scope.orderListSearchForm.depotId) {
            $http({
               url: '/admin/api/order',
               method: "GET",
               params: $scope.orderListSearchForm
            }).success(function (data, status, headers, config) {
               $scope.orders = data.orders;
               $scope.count = data.total;
               $scope.orderStatistics = data.orderStatistics;

               /*分页数据*/
               $scope.page.itemsPerPage = data.pageSize;
               $scope.page.totalItems = data.total;
               $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
               window.alert("搜索失败...");
            });
        }

        $scope.sort = function(field) {
            if(field && field == $scope.orderListSearchForm.sortField) {
                $scope.orderListSearchForm.asc = !$scope.orderListSearchForm.asc;
            } else {
                $scope.orderListSearchForm.sortField = field;
                $scope.orderListSearchForm.asc = false;
            }

            $scope.orderListSearchForm.page = 0;

            $location.search($scope.orderListSearchForm);
        }

        $scope.stopOrder = function () {
            $scope.submitting = true;
            $http({
               url: '/admin/api/order/stop',
               method: "GET",
               params: $scope.orderListSearchForm
            }).success(function (data, status, headers, config) {
               alert("截单成功！");
               $scope.submitting = false;
               $scope.resetPageAndSearchOrderList();
            }).error(function (data, status, headers, config) {
               window.alert("截单失败..." + data.errmsg);
               $scope.submitting = false;
            });
        }

        $scope.stopOrderMap = function(){
            var orderListSearchMap = $scope.orderListSearchForm;
            orderListSearchMap.type = 4;
            $state.go("stop-order-map", orderListSearchMap);
        }
});
