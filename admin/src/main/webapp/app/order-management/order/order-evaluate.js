'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderItemsListCtrl
 * @description
 * # OrderItemsListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderEvaluate', function ($scope, $rootScope, $http, $stateParams, $filter, $location,$window) {
        /*订单评价查询对象*/
        $scope.orderEvaluate = {
           onlyNoScore : $stateParams.onlyNoScore=="true"
        };

        console.log($stateParams.onlyNoScore);
        console.log($scope.orderEvaluate);

        $scope.page = {
            evaluatePerPage: 100
        };

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.depotCities;
             if ($scope.cities && $scope.cities.length == 1) {
                $scope.orderEvaluate.cityId = $scope.cities[0].id;
             }
        }

        $scope.$watch('orderEvaluate.cityId', function(newVal, oldVal) {
            if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderEvaluate.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderEvaluate.depotId = $scope.depots[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderEvaluate.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderEvaluate.organizationId = null;
                   $scope.orderEvaluate.depotId = null;
                   $scope.orderEvaluate.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.depots = [];
               $scope.availableWarehouses = [];
               $scope.orderEvaluate.organizationId = null;
               $scope.orderEvaluate.depotId = null;
               $scope.orderEvaluate.warehouseId = null;
           }
        });

        $scope.openStart = function ($event) {
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


        if($stateParams.start) {
            $scope.orderEvaluate.start = $stateParams.start;
        }

        if($stateParams.end) {
            $scope.orderEvaluate.end = $stateParams.end;
        }

        if($scope.orderEvaluate.start) {
            $scope.startDate = Date.parse($scope.orderEvaluate.start);
        }

        if($scope.orderEvaluate.end) {
            $scope.endDate = Date.parse($scope.orderEvaluate.end);
        }

        if ($stateParams.cityId) {
            $scope.orderEvaluate.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.organizationId) {
            $scope.orderEvaluate.organizationId = parseInt($stateParams.organizationId);
        }

        if ($stateParams.warehouseId) {
            $scope.orderEvaluate.warehouseId = parseInt($stateParams.warehouseId);
        }

        if ($stateParams.orderId) {
            $scope.orderEvaluate.orderId = parseInt($stateParams.orderId);
        }

        if ($stateParams.adminName) {
            $scope.orderEvaluate.adminName = $stateParams.adminName;
        }

        if ($stateParams.trackerName) {
            $scope.orderEvaluate.trackerName = $stateParams.trackerName;
        }

        if ($stateParams.page) {
            $scope.orderEvaluate.page = $stateParams.page;
        }

        if ($stateParams.pageSize) {
            $scope.orderEvaluate.pageSize = $stateParams.pageSize;
        }

        $scope.format = 'yyyy-MM-dd';

        $scope.$watch('startDate', function (d) {
            if(d) {
                $scope.orderEvaluate.start = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('endDate', function (d) {
            if (d) {
                $scope.orderEvaluate.end = $filter('date')(d, 'yyyy-MM-dd');
            }
        });
        
        $scope.resetPageAndSearchOrderEvaluates = function () {
           
            $scope.searchOrderEvaluate();
        }
        $scope.orderEvaluateData;
        $scope.searchOrderEvaluate = function () {
            $location.search($scope.orderEvaluate);

            $http({
                url:'/admin/api/order/evaluate',
                method: "GET",
                params: $scope.orderEvaluate
            }).success(function (data, status, headers, config) {
               $scope.orderEvaluateData = data.orderEvaluates;

                 /*分页数据*/
                $scope.page.evaluatePerPage = data.pageSize;
                $scope.page.totalEvaluate = data.total;
                $scope.page.currentPage = data.page + 1;

            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.searchOrderEvaluate();

        $scope.bakScore=function(orderEvaluate){
            $http({
                url:'/admin/api/order/evaluate/score/send',
                method: "GET",
                params: {
                    orderId: orderEvaluate.orderId
                }
            }).success(function (data, status, headers, config) {
                orderEvaluate.scoreLog=data;

            }).error(function (data, status) {
                window.alert("操作失败");
            });
        };

        $scope.pageChanged = function () {
            $scope.orderEvaluate.page = $scope.page.currentPage - 1;
            $scope.orderEvaluate.pageSize = $scope.page.evaluatePerPage;

            $scope.searchOrderEvaluate();
        }


         $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderEvaluate) {
                if($scope.orderEvaluate[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderEvaluate[p]));
                }
            }


            $window.open("/admin/api/order-evaluate/excelExport?" + str.join("&"));
         };

    })