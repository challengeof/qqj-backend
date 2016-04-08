'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrdersCtrl
 * @description
 * # ListOrdersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('OrderGroupListCtrl',function($scope, $q, $rootScope, $http, $filter, $state, $stateParams, $location){

        $scope.orderGroupSearchForm = {
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId,
            trackerId:$stateParams.trackerId,
            startOrderDate:$stateParams.startOrderDate,
            endOrderDate:$stateParams.endOrderDate,
            queryDateType:1,
            page: $stateParams.page,
            pageSize: $stateParams.pageSize
        }

        $scope.page = {
            itemsPerPage: 100
        };

        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
            }
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
        };


        $scope.format = 'yyyy-MM-dd HH:mm';
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian:false
        }

        $scope.orderGroups = [];

        $scope.sizeOfUngroupedOrders = 0;

        /*订单列表搜索表单*/
//        $scope.expectedArrivedDate = $filter('date')(new Date(),'yyyy-MM-dd');
        /*$scope.orderGroupSearchForm = {
            expectedArrivedDate : $filter('date')($scope.expectedArrivedDate ,'yyyy-MM-dd')
        };*/

        if ($stateParams.cityId) {
            $scope.orderGroupSearchForm.cityId = parseInt($stateParams.cityId);
        }
        if ($stateParams.depotId) {
            $scope.orderGroupSearchForm.depotId = parseInt($stateParams.depotId);
        }
        if ($stateParams.trackerId){
            $scope.orderGroupSearchForm.trackerId = parseInt($stateParams.trackerId);
        }
        if ($stateParams.startOrderDate) {
            $scope.start = $filter('date')($stateParams.startOrderDate, $scope.format);
        }
        if ($stateParams.endOrderDate) {
            $scope.end = $filter('date')($stateParams.endOrderDate, $scope.format);
        }

        $scope.$watch('start', function(d) {
            $scope.orderGroupSearchForm.startOrderDate = $filter('date')(d, $scope.format);
        });
        $scope.$watch('end', function(d) {
            $scope.orderGroupSearchForm.endOrderDate = $filter('date')(d, $scope.format);
        });

        $scope.findUngroupedOrder = function () {
            if($scope.orderGroupSearchForm.cityId) {
                var unGroupOrderSearchForm = {
                        cityId:$scope.orderGroupSearchForm.cityId,
                        depotId:$scope.orderGroupSearchForm.depotId,
                        page: $scope.orderGroupSearchForm.page,
                        pageSize: $scope.orderGroupSearchForm.pageSize
                    };
                $http({
                        url: '/admin/api/ungrouped-order/size',
                        method: 'GET',
                        params: unGroupOrderSearchForm
                    }
                ).success(function (data) {
                        $scope.sizeOfUngroupedOrders = data;
                });
            }
        }

		$scope.searchForm = function () {
		    $scope.findUngroupedOrder();
		    $location.search($scope.orderGroupSearchForm);
		}

        if($scope.orderGroupSearchForm.cityId && $scope.orderGroupSearchForm.startOrderDate && $scope.orderGroupSearchForm.endOrderDate) {
            $http({
                    url: '/admin/api/order-group',
                method: "GET",
                params: $scope.orderGroupSearchForm
            })
            .success(function(data,status,headers,congfig){
                $scope.orderGroups = data.content;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function(data,status,headers,config){
                window.alert("搜索失败...");
            });
        }

        $scope.searchForm();

        $scope.search = function() {
            if($scope.orderGroupSearchForm.cityId) {
                $state.go($state.current, $scope.orderGroupSearchForm, {reload: true});
            }
        }

        $scope.pageChanged = function() {
            $scope.orderGroupSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderGroupSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchForm();
        }

        $scope.createOrderGroupAndJump = function() {
            $http({
                url: "/admin/api/order-group/",
                method: 'POST',
                data: {"depotId":null},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                $state.go("oam.order-group-detail", {id: data.id , type:0});

            }).error(function (data) {
                alert("提交失败!");
            });
            /*$state.go("oam.order-group-detail", {cityId:cityId, depotId:depotId, startOrderDate:startOrderDate, endOrderDate:endOrderDate});*/
        }

        $scope.$watch('orderGroupSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderGroupSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderGroupSearchForm.depotId = null;
               }

           }else{
               $scope.depots = [];
               $scope.orderGroupSearchForm.depotId = null;
           }
        });

        $scope.$watch('orderGroupSearchForm.depotId',function(newVal,oldVal){
            if(newVal){
                //-------------- 仓库下线路数据 -------------------
                $http({
                    url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
                    method:"GET",
                    params:$scope.orderGroupSearchForm
                })
                .success(function(data) {
                    $scope.trackers = data;
                });

            }else{
                $scope.trackers = [];
                $scope.trackers = null;
            }
        });

        //-------------- 线路数据初始化 -------------------
        //$http({
        //    url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
        //    method:"GET"
        //})
        //.success(function(data) {
        //    $scope.trackers = data;
        //});



        function isLock(id){
            var defer = $q.defer();
            $http({
                url: "/admin/api/order-group/" + id,
                method: 'GET'
            }).success(function (data) {
                console.log(JSON.stringify(data));
                defer.resolve(data.lock == 1);
            }).error(function (data) {
                alert("获取订单状态失败!");
                defer.resolve(true);
            });
            return defer.promise;
        }

        $scope.editOrderGroup = function (id){
            isLock(id).then(function(result){
                if(result){
                    alert("订单已经出库,不能编辑");
                    return ;
                }else{
                    $state.go("oam.order-group-detail", {id: id,type:1});
                }
            })
        }

        //取消分车
        $scope.removeOrderGroup = function (id){
            isLock(id).then(function(result){
                if(result){
                    alert("订单已经出库,不能取消分车");
                    return ;
                }else{
                    $scope.formData = {};
                    $scope.formData.orderIds = [];
                    $scope.formData.cityId = null;
                    $scope.formData.depotId = null;
                    $scope.formData.trackerId = null;
                    $scope.formData.name = null;

                    $http({
                        url: "/admin/api/order-group/" + id,
                        method: 'PUT',
                        data: $scope.formData,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
                        }
                    }).success(function (data) {
                        alert("取消成功！");
                        $scope.search();
                    }).error(function (data) {
                        alert("取消失败!");
                    });
                }
            })
        }

	});
