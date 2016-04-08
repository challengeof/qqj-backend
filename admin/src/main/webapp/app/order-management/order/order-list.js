    'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListOrdersCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {
        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.orders = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderListSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/order/orderType/get")
            .success(function (data, status, headers, config) {
                $scope.orderTypes = data;
            }).error(function (data, status) {
            alert("订单状态加载失败！");
        });

        $scope.dateOptions = {
                dateFormat: 'yyyy-MM-dd',
                formatYear: 'yyyy',
                startingDay: 1,
                startWeek: 1
            };

        $scope.submitDateFormat = "yyyy-MM-dd";

        /*$scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }


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
        };*/


        $scope.page = {
            itemsPerPage: 100
        };

        $scope.coordinateLabeleds = [{key:0,value:"坐标缺失"},{key:1,value:"坐标已标注"}];
        $scope.refundsIsNotEmptys = [{key:true,value:"有退货的订单"}];

            /*订单状态*/
      $http.get("/admin/api/order/status")
           .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
           }).error(function (data, status) {
                alert("订单状态加载失败！");
       });

       $scope.$watch('orderListSearchForm.cityId',function(newVal,oldVal){
           if(newVal){
               $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                   $scope.organizations = data;
                   if ($scope.organizations && $scope.organizations.length == 1) {
                      $scope.orderListSearchForm.organizationId = $scope.organizations[0].id;
                   }
               });
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderListSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                   $scope.availableWarehouses = data;
                   if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                       $scope.orderListSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderListSearchForm.organizationId = null;
                   $scope.orderListSearchForm.depotId = null;
                   $scope.orderListSearchForm.warehouseId = null;
               }
           }else{
               $scope.organizations = [];
               $scope.depots = [];
               $scope.availableWarehouses = [];
               $scope.orderListSearchForm.organizationId = null;
               $scope.orderListSearchForm.depotId = null;
               $scope.orderListSearchForm.warehouseId = null;
           }
       });

        $scope.$watch('orderListSearchForm.organizationId',function(newVal,oldVal){
            //选择销售
            if(newVal){
                $http({
                    method:"GET",
                    url:"/admin/api/admin-user/global?role=CustomerService",
                    params:{organizationId:newVal}
                }).success(function(data){
                    $scope.adminUsers = data;
                })
            }
        });

        if($stateParams.spikeItemId!=null){
            $scope.orderListSearchForm.spikeItemId = $stateParams.spikeItemId;
        }

        if($stateParams.sortField) {
            $scope.orderListSearchForm.sortField = $stateParams.sortField;
        } else {
            $scope.orderListSearchForm.sortField = "id";
        }

        if($stateParams.asc) {
            $scope.orderListSearchForm.asc = true;
        } else {
            $scope.orderListSearchForm.asc = false;
        }

        if ($stateParams.orderType) {
            $scope.orderListSearchForm.orderType = parseInt($stateParams.orderType);
        }

        $scope.$watch('order.selected', function(arg){
            if(arg){
                $scope.orderListSearchForm.customerId = arg.customer.id;
            }
        });

        $scope.searchOrderList = function () {
            $location.search($scope.orderListSearchForm);
        }

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;
            $scope.searchOrderList();
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrderList();
        }

        $scope.deliverNow =  function(order) {
            $http.post("/admin/api/order/" + order.id + "/deliver")
                .success(function (data, status, headers, config) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("修改成功");
                });
        }

        $scope.cancelOrder = function(order) {
            $http.post("api/order/" + order.id + "/cancel")
                .success(function (data) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $scope.NewTicket = function(order){
            // console.log(order);
            var arr = {
                "username": $rootScope.user.realname,
                "info": order
            };
            arr = JSON.stringify(arr);
            // console.log(arr);
            arr = encodeURIComponent(arr);
            console.log(arr)
            window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data="+arr);
        }

        $scope.completeOrder = function(order) {
            $http.post("api/order/" + order.id + "/complete")
                .success(function (data) {
                	order.status.name = data.status.name;
                	order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $scope.cancelOrder = function(order) {
            $http.post("api/order/" + order.id + "/cancel")
                .success(function (data) {
                    order.status.name = data.status.name;
                    order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

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


        $scope.downloadErrorFile = function(fileName) {
            $window.open("/admin/api/dynamic-price/errorFile/" + fileName);
        }

        $scope.excelExport = function(){
            var str = [];
            for(var p in $scope.orderListSearchForm) {
                if($scope.orderListSearchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.orderListSearchForm[p]));
                }
            }

            $window.open("/admin/api/order/excelExport?" + str.join("&"));
        };

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
});
