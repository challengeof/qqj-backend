'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderGroupDetailCtrl
 * @description
 * # OrderGroupDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .config(['uiMapLoadParamsProvider',
        function (uiMapLoadParamsProvider) {
            uiMapLoadParamsProvider.setParams({
                v: '1.5',
                ak: '1507703fda1fb9594c7e7199da8c41d8'
            });
        }])
    .controller('OrderGroupDetailCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $state) {
    /*
     * $stateParams.type : 0 新增 | 1:修改
     */

    //--------------------- detail -----------------------
        $scope.allCheckedBlock = false;
        $scope.allUnSelectOrderGroup = false;
        $scope.allSelectOrderGroup = false;

        $scope.ungroupedOrders = []; //未分配的订单
        $scope.groupedOrders = []; //已分配的订单
        $scope.subTotalFilterOrders = []; //过滤总价暂存数组
        $scope.formData = {};
        $scope.formData.name = $filter('date')(new Date(), $scope.format);
        $scope.allCityblocks = [];
        $scope.blocks = [];
        $scope.subTotalFilterValue = 0;
        var markerHashMap = new Object();

        $scope.checkFormData = {
            selectedUngroupedOrders:[], //未分配订单
            selectedGroupedOrders:[],  //已分配订单
            selectBlock:[]  //区块
        };

        $scope.orderGroupSearchForm = {
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId,
            startOrderDate:$stateParams.startOrderDate,
            endOrderDate:$stateParams.endOrderDate
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


        //默认坐标中心点为北京
        $scope.lng = 116.403119;
        $scope.lat = 39.914714;

        $scope.mapOptions = {
            ngCenter: {lng: $scope.lng, lat: $scope.lat},
            ngZoom: 12,
            scrollzoom: true
        };

        //填充搜索栏城市信息
        if($rootScope.user) {
           var data = $rootScope.user;
            $scope.cities = data.depotCities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
            }
        }


        //根据城市变化填充仓库信息和区块信息
        $scope.$watch('orderGroupSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
               drawMap(); //根据城市变化绘制地图、区块

               $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                   $scope.depots = data;
                   if ($scope.depots && $scope.depots.length == 1) {
                       $scope.orderGroupSearchForm.depotId = $scope.depots[0].id;
                   }
               });
               if(typeof oldVal != 'undefined' && newVal != oldVal){
                   $scope.orderGroupSearchForm.depotId = null;
               }

               $http.get("/admin/api/city/"+ $scope.orderGroupSearchForm.cityId +"/blocks?status=1")
               .success(function (data, status, headers) {
                   //获取城市下的所有区块
                   $scope.allCityblocks = data;
                   $scope.blocks = data;
               })
               .error(function (data, status, headers) {
                   console.log(status);
                   window.alert("区块获取失败...");
               });

           }else{
               $scope.depots = [];
               $scope.orderGroupSearchForm.depotId = null;
           }
        });

        $scope.$watch('orderGroupSearchForm.depotId',function(newVal,oldVal){
            if(newVal){
               //获取该仓库下的所有市场,再用市场ID和区块ID对比
               $http.get("/admin/api/warehouse/depot/"+ newVal)
               .success(function (data, status, headers) {
                   //获取城市下的所有区块
                   $scope.blocks = [];
                   for(var i=0; i<data.length; i++){
                        var warehouseObj = data[i];
                        for(var j=0; j< $scope.allCityblocks.length; j++){
                            if( $scope.allCityblocks[j].warehouse.id == warehouseObj.id){
                                $scope.blocks.push($scope.allCityblocks[j]);
                            }
                        }
                   }
               })
               .error(function (data, status, headers) {
                   console.log(status);
                   window.alert("市场获取失败...");
               });


                //跟车员
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
               $scope.blocks = $scope.allCityblocks;
           }
        });


        //查询入口
        $scope.search = function(){
            //查询form赋值
            $scope.orderGroupSearchForm.blockIds = $scope.checkFormData.selectBlock;
            $scope.orderGroupSearchForm.startOrderDate = $filter('date')($scope.start , $scope.format);
            $scope.orderGroupSearchForm.endOrderDate = $filter('date')($scope.end , $scope.format);

            $http({
                url: '/admin/api/ungrouped-order',
                method: 'GET',
                params: $scope.orderGroupSearchForm
            }).success(function (data) {

                $scope.removeAllMarker();
                $scope.ungroupedOrders = []; //未分配的订单
                for(var i=0;i<data.content.length;i++){
                    var flag = true;

                    for(var s=0;s<$scope.groupedOrders.length;s++){
                        if(data.content[i].id == $scope.groupedOrders[s].id){
                            flag = false;
                            break;
                        }
                    }
                    if(flag == true){
                        $scope.ungroupedOrders.push(data.content[i]);
                        var restaurant = data.content[i].restaurant;
                        if (restaurant.address && restaurant.address.wgs84Point) {
                            $scope.addMarker(restaurant , data.content[i].id);
                        }
                    }

                }
            });
        }


        //区块全选
        $scope.clickAllBlock = function(){
             $scope.allCheckedBlock = !$scope.allCheckedBlock;
             if($scope.allCheckedBlock){
                 for(var i=0; i<$scope.blocks.length; i++){
                    $scope.checkFormData.selectBlock.push($scope.blocks[i].id);
                 }
             }else{
                $scope.checkFormData.selectBlock = [];
             }
        }

        //金额过滤
        $scope.subTotalFilter = function(){
            if($scope.subTotalFilterValue == undefined){
                alert("请输入一个正数");
                return;
            }
            for (var i = $scope.ungroupedOrders.length-1; i >=0; i--) {
//                alert("sss : " + JSON.stringify($scope.ungroupedOrders[i]));
                if($scope.subTotalFilterValue > $scope.ungroupedOrders[i].subTotal){
                    $scope.subTotalFilterOrders.push($scope.ungroupedOrders[i]);
                    $scope.ungroupedOrders.splice(i, 1);
                }
            }

            for (var j = $scope.subTotalFilterOrders.length-1; j >= 0; j--){
                if($scope.subTotalFilterValue <= $scope.subTotalFilterOrders[j].subTotal){
                    $scope.ungroupedOrders.push($scope.subTotalFilterOrders[j]);
                    $scope.subTotalFilterOrders.splice(j, 1);
                }
            }
        }

        //未分配订单全选
        $scope.clickAllUnSelectOrderGroup = function(){
             $scope.allUnSelectOrderGroup = !$scope.allUnSelectOrderGroup;
             if($scope.allUnSelectOrderGroup == true){
                $scope.checkFormData.selectedUngroupedOrders = [];
                for (var i = 0; i < $scope.ungroupedOrders.length; i++) {
                    $scope.checkFormData.selectedUngroupedOrders.push($scope.ungroupedOrders[i].id);
                }
             }else{
                $scope.checkFormData.selectedUngroupedOrders = [];
             }
        }



        //已分配订单全选
        $scope.clickAllSelectOrderGroup = function(){
             $scope.allSelectOrderGroup = !$scope.allSelectOrderGroup;
             if($scope.allSelectOrderGroup == true){
                for (var i = 0; i < $scope.groupedOrders.length; i++) {
                    $scope.checkFormData.selectedGroupedOrders.push(eval($scope.groupedOrders[i].id));
                }
             }else{
                 $scope.checkFormData.selectedGroupedOrders = [];
             }
        }


        //未分配订单 TO 已分配订单
        $scope.selectOrders = function () {
            for (var i = 0; i < $scope.checkFormData.selectedUngroupedOrders.length; i++) {
                for (var j = $scope.ungroupedOrders.length - 1; j >= 0; j--) {
                    if ($scope.ungroupedOrders[j].id == $scope.checkFormData.selectedUngroupedOrders[i]) {
                        //删除相应的marker
                        if ($scope.ungroupedOrders[j].restaurant) {
                            $scope.removeMarker($scope.ungroupedOrders[j].id);
                        }
                        $scope.groupedOrders.push($scope.ungroupedOrders[j]);
                        $scope.ungroupedOrders.splice(j, 1);
                        break;
                    }
                }
            }
            $scope.checkFormData.selectedUngroupedOrders = [];
            $scope.allUnSelectOrderGroup = false;
            $scope.allSelectOrderGroup = false;
        }

        //已分配订单 TO 未分配订单
        $scope.unselectOrders = function () {
            for (var i = 0; i < $scope.checkFormData.selectedGroupedOrders.length; i++) {
                for (var j = $scope.groupedOrders.length - 1; j >= 0; j--) {
                    if ($scope.groupedOrders[j].id == $scope.checkFormData.selectedGroupedOrders[i]) {
                        //撤销餐馆订单同时添加相应marker
                        if ($scope.groupedOrders[j].restaurant && $scope.groupedOrders[j].restaurant.address.wgs84Point) {
                            $scope.addMarker($scope.groupedOrders[j].restaurant , $scope.groupedOrders[j].id);
                        }
                        $scope.ungroupedOrders.push($scope.groupedOrders[j]);
                        $scope.groupedOrders.splice(j, 1);
                        break;
                    }
                }
            }
            $scope.checkFormData.selectedGroupedOrders = [];
            $scope.allUnSelectOrderGroup = false;
            $scope.allSelectOrderGroup = false;
        }


        $scope.tracker = {};

        //记录跟车员
        $scope.$watch('tracker.selected', function (newVal , oldVal) {
            if (newVal) {
                $scope.formData.trackerId = newVal.id;
                //根据跟车员查询车辆信息
                $http({
                   url : '/admin/api/car/cars',
                   method:"GET",
                   params:{"trackerId":$scope.formData.trackerId,"status":1}
                })
                .success(function(data) {
                    $scope.cars = data.content;
                });
            }
        })

        //订单包编辑状态下数据回显
        if($stateParams.type == 1){
            $http({
                url: "/admin/api/order-group/" + $stateParams.id,
                method: 'GET'
            }).success(function (data) {
                $scope.orderGroupSearchForm.cityId = data.city.id;
                $scope.orderGroupSearchForm.depotId = data.depot.id;
                $scope.tracker.selected = data.tracker;
                $scope.formData.trackerId = data.tracker.id;
                $scope.formData.name = data.name;
                $scope.groupedOrders = data.members;
            }).error(function (data) {
                alert("获取订单状态失败!");
            });
        }

        $scope.saveOrderGroup = function () {
            if($scope.formData.trackerId == null){
                alert("请选择跟车员");
                return;
            }
            $scope.formData.orderIds = [];
            $scope.formData.orderIds = $scope.groupedOrders.map(function (order) {
                return order.id;
            });

            $scope.formData.cityId = $scope.orderGroupSearchForm.cityId; //城市
            $scope.formData.depotId = $scope.orderGroupSearchForm.depotId; //仓库

            $http({
                url: "/admin/api/order-group/" + $stateParams.id,
                method: 'PUT',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                //保存或者更新
                if (data.name) {
                    $scope.formData.name = data.name;
                } else {
                    $scope.formData.name = $filter('date')(new Date(), $scope.format);
                }

                if (data.tracker) {
                    $scope.formData.trackerId = data.tracker.id;
                    $scope.tracker.selected = data.tracker;
                }

                if($stateParams.type == 1){
                    alert("更新成功");
                }else {
                    alert("保存成功");
                     $scope.groupedOrders = []; //情况已分配订单
                     $scope.createOrderGroup(); //不刷新页面情况下连续添加
                     $scope.search();
                }
            }).error(function (data) {
                alert("操作失败!");
            });
        }

        $scope.createOrderGroup = function() {
            $http({
                url: "/admin/api/order-group/",
                method: 'POST',
                data: {"depotId":null},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function (data) {
                $stateParams.id = data.id;
            });
        }

        //------------------------- 地图 ----------------------------



        function drawMap(){
            /*基于地图绘制显示区块*/
            $scope.$watch('myMap', function (map) {
                if(map){
                     if ($scope.orderGroupSearchForm.cityId) {
                        if ($scope.orderGroupSearchForm.cityId == 1) {
                            $scope.lng = 116.403119;
                            $scope.lat = 39.914714;
                        } else if ($scope.orderGroupSearchForm.cityId == 2) {
                            $scope.lng = 104.072653;
                            $scope.lat = 30.664043;
                        } else if ($scope.orderGroupSearchForm.cityId == 3) {
                            $scope.lng = 120.18469;
                            $scope.lat = 30.267334;
                        } else if ($scope.orderGroupSearchForm.cityId == 4) {
                            $scope.lng = 117.024316;
                            $scope.lat = 36.667227;
                        }


                        $scope.myMap.centerAndZoom(new BMap.Point($scope.lng,$scope.lat), 12);  //创建中心点,缩放等级
                        $scope.myMap.setCenter(new BMap.Point($scope.lng,$scope.lat));
                        $http.get("/admin/api/city/"+ $scope.orderGroupSearchForm.cityId +"/simpleBlocks?status=1")
                        .success(function (data, status, headers) {
                            $scope.blocksMap = data;

                            if ($scope.blocksMap && $scope.blocksMap.length > 0) {
                                for (var i=0; i < $scope.blocksMap.length; i++) {
                                    var pointObjects = [];
                                    var borderColorArr = ["blue","green","purple","yellow","orange","pink","dark","fuchsia","crimson","greenyellow"];
                                    var strokeColor = borderColorArr[parseInt(Math.random()*9)];

                                    if ($scope.blocksMap[i].points && $scope.blocksMap[i].points.length > 0) {
                                        for (var j=0; j < $scope.blocksMap[i].points.length; j++) {
                                            var lng = $scope.blocksMap[i].points[j].longitude;
                                            var lat = $scope.blocksMap[i].points[j].latitude;
                                            pointObjects.push(new BMap.Point(lng,lat));
                                        }
                                    }

                                    //创建区块多边形
                                    var polygon = new BMap.Polygon(pointObjects, {strokeColor:strokeColor, strokeWeight:1, strokeOpacity:1});
                                    polygon.setFillOpacity(0.001);
                                    $scope.addEventListenerToPolygon(polygon,strokeColor,$scope.blocksMap[i].name);
                                    $scope.myMap.addOverlay(polygon);
                                }
                            }
                        })
                        .error(function (data, status, headers) {
                            console.log(status);
                            window.alert("区块获取失败...");
                        });
                    }

                }
            });
        }


        /*添加marker事件*/
        $scope.addMarker = function (restaurant , orderId) {
            var lon = restaurant.address.wgs84Point.longitude;
            var lat = restaurant.address.wgs84Point.latitude;
            var address = restaurant.address.address;
            var name = restaurant.name;
            var restaurantId = restaurant.id;

            var point = new BMap.Point(lon, lat);
            var marker = new BMap.Marker(point);
            markerHashMap[orderId] = marker;

            $scope.myMap.addOverlay(marker);

            //创建信息窗口
            var opts = {
                width: 200,
                height: 70,
                title: "<font style='font-weight:bold;'>[" + name + "]</font>",
                enableMessage: false
            };
            var infoWindow = new BMap.InfoWindow("地址：" + address, opts);

            //添加单击事件
            marker.addEventListener("click", function () {
                $scope.myMap.openInfoWindow(infoWindow, point);
            });

            //创建右键菜单
            var markerMenu = new BMap.ContextMenu();
            var boundAssignRestaurantToGroup = $scope.assignRestaurantToGroup.bind(marker, restaurantId);
            //绑定菜单事件
            markerMenu.addItem(new BMap.MenuItem('配送', boundAssignRestaurantToGroup));
            marker.addContextMenu(markerMenu);
        };

        //删除当前已经添加到map上全部marker
        $scope.removeAllMarker = function (){
            for(var key in markerHashMap){
                 $scope.myMap.removeOverlay(markerHashMap[key]);
            }
            markerHashMap = new Object();
        }

        $scope.addEventListenerToPolygon = function (polygon, strokeColor, blockName) {
            //鼠标在区块多边形上移动时边线变红加粗
            polygon.addEventListener("mousemove", function(e) {
                polygon.setStrokeColor("red");
                polygon.setStrokeWeight(2);
            });

            //鼠标在区块多边形上移出时边线复原
            polygon.addEventListener("mouseout", function(e) {
                polygon.setStrokeColor(strokeColor);
                polygon.setStrokeWeight(1);
            });

            //点击区块显示区块名称
            polygon.addEventListener("click", function(e) {
                alert(blockName);
            });
        };


        //删除marker事件
        $scope.removeMarker = function (orderId) {
             $scope.myMap.removeOverlay(markerHashMap[orderId]);
             delete markerHashMap[orderId];
        }

        /*右键配送事件*/
        $scope.assignRestaurantToGroup = function (restaurantId) {
            for (var j = $scope.ungroupedOrders.length - 1; j >= 0; j--) {
                if ($scope.ungroupedOrders[j].restaurant.id == restaurantId) {

                    $scope.removeMarker( $scope.ungroupedOrders[j].id);
                    //添加到“已分配订单”多选下拉框中
                    $scope.groupedOrders.push($scope.ungroupedOrders[j]);
                    $scope.ungroupedOrders.splice(j, 1);
                }
            }
            $scope.$apply();
        }


    //----------------------------------------------------

        $scope.selectedRestaurantIds = function () {
            var restaurantIds = [];
            for (var i = 0; i < $scope.groupedOrders.length; i++) {
                restaurantIds.push($scope.groupedOrders[i].restaurant.id);
            }

            return restaurantIds
        };

        $scope.$watch('ungroupedOrders' , function(data){

            var sum = 0;
            $scope.unGroupedOrdersTotal = 0;
            $scope.unGroupedOrdersCount = 0;
            $scope.unGroupedOrdersWight = 0.0;
            $scope.unGroupedOrdersTotalVolume = 0.0;
            $scope.unGroupedOrdersQuantity = 0;

            for(var i = 0; i < data.length; i++) {
                sum += data[i].total;
                $scope.unGroupedOrdersCount += 1;
                $scope.unGroupedOrdersWight += data[i].totalWight;
                $scope.unGroupedOrdersTotalVolume += data[i].totalVolume;
                $scope.unGroupedOrdersQuantity += data[i].quantity;
            }

            $scope.unGroupedOrdersTotal = sum.toFixed(2);
        }, true);


        //计算已选择配送订单合计
        $scope.$watch('groupedOrders', function(data) {
            var sum = 0;
            $scope.groupedOrdersTotal = 0;
            $scope.groupedOrdersCount = 0;
            $scope.groupedOrdersWight = 0.0;
            $scope.groupedOrdersTotalVolume = 0.0;
            $scope.groupedOrdersQuantity = 0;

            for(var i = 0; i < data.length; i++) {
                sum += data[i].total;
                $scope.groupedOrdersCount += 1;
                $scope.groupedOrdersWight += data[i].totalWight;
                $scope.groupedOrdersTotalVolume += data[i].totalVolume;
                $scope.groupedOrdersQuantity += data[i].quantity;
            }

            $scope.groupedOrdersTotal = sum.toFixed(2);
        }, true);


    });
