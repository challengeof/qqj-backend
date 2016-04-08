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
    .controller('OrderGroupMapCtrl', function ($scope, $http, $stateParams) {
        $scope.myMarkers = [];
        if ($stateParams.cityId == 1) {
            $scope.lng = 116.403119;
            $scope.lat = 39.914714;
        } else if ($stateParams.cityId == 2) {
            $scope.lng = 104.072653;
            $scope.lat = 30.664043;
        } else if ($stateParams.cityId == 3){
            $scope.lng = 120.219375;
            $scope.lat = 30.259244;
        } else if ($stateParams.cityId ==  4){
            $scope.lng = 117.024967;
            $scope.lat = 36.682785;
        } else if ($stateParams.cityId == 5){
            $scope.lng = 125.313642;
            $scope.lat = 43.898338;
        }

        $scope.mapOptions = {
            ngCenter: {
                lng: $scope.lng,
                lat: $scope.lat
            },
            ngZoom: 12,
            scrollzoom: true
        };

        //地图上显示的点位
        var marker = {
            lon:null,
            lat:null,
            name:null
        }


        /*添加marker事件*/
        $scope.addMarker = function (marker) {
            if (marker.lon && marker.lat) {

                var lon = marker.lon;
                var lat = marker.lat;
                var name = marker.name;

                // 自定义覆盖物标签
                var ComplexCustomOverlay = function(point, text){
                  this._point = point;
                  this._text = text;
                }
                ComplexCustomOverlay.prototype = new BMap.Overlay();
                ComplexCustomOverlay.prototype.initialize = function(map){
                  this._map = map;
                  var div = this._div = document.createElement("div");
                  div.style.position = "absolute";
                  div.style.zIndex = BMap.Overlay.getZIndex(this._point.lat);
                  div.style.backgroundColor = "white";
                  div.style.border = "1px solid red";
                  div.style.color = "red";
                  div.style.height = "20px";
                  div.style.padding = "0px";
                  div.style.lineHeight = "20px";
                  div.style.whiteSpace = "nowrap";
                  div.style.fontSize = "12px"
                  var span = this._span = document.createElement("span");
                  div.appendChild(span);
                  span.appendChild(document.createTextNode(this._text));

                  var arrow = this._arrow = document.createElement("div");
                  arrow.style.position = "absolute";
                  arrow.style.width = "11px";
                  arrow.style.height = "10px";
                  arrow.style.top = "22px";
                  arrow.style.left = "10px";
                  arrow.style.overflow = "hidden";
                  div.appendChild(arrow);

                  $scope.myMap.getPanes().labelPane.appendChild(div);

                  return div;
                }

                ComplexCustomOverlay.prototype.draw = function(){
                  var map = this._map;
                  var pixel = map.pointToOverlayPixel(this._point);
                  this._div.style.left = pixel.x + 10 + "px";
                  this._div.style.top  = pixel.y - 22 + "px";
                }

                var myCompOverlay = new ComplexCustomOverlay(new BMap.Point(lon, lat), name);

                $scope.myMap.addOverlay(myCompOverlay);

                var point = new BMap.Point(lon, lat);
                var marker = new BMap.Marker(point);

                $scope.myMap.addOverlay(marker);

            }
        };

//        $scope.$watch('myMap', function (map) {
//            if (map) {
//                if ($stateParams.rid) {
//                    $http({
//                        url: '/admin/api/restaurant/batch',
//                        params: {
//                            restaurantId: $stateParams.rid
//                        },
//                        method: 'GET'
//                    }).success(function (data) {
//                        for (var i = 0; i < data.length; i++) {
//                            $scope.addMarker(data[i]);
//                        }
//                    })
//                }
//            }
//        });


         $scope.$watch('myMap', function (map) {
            if (map) {
                if($stateParams.type == 1){
                    //订单包下订单分布图
                   $http({
                       url : '/admin/api/order-group/'+$stateParams.id,
                       method:"GET"
                   })
                   .success(function(data) {
                       for(var i = 0; i < data.members.length; i++){
                            var orderItem = data.members[i];
                            var restaurant = orderItem.restaurant;
                            marker.name = restaurant.name;
                            marker.lon = restaurant.address.wgs84Point.longitude;
                            marker.lat = restaurant.address.wgs84Point.latitude;
                            $scope.addMarker(marker);
                       }
                   });

                }else if($stateParams.type == 2){
                    //订单已经分配的地图
                    $http({
                        url: '/admin/api/restaurant/batch',
                        params: {
                            restaurantId: $stateParams.id
                        },
                        method: 'GET'
                    }).success(function (data) {
                        for (var i = 0; i < data.length; i++) {
                            marker.name = data[i].name;
                            marker.lon = data[i].address.wgs84Point.longitude;
                            marker.lat = data[i].address.wgs84Point.latitude;
                            $scope.addMarker(marker);
                        }
                    })
                }else if($stateParams.type == 3){
                    //查看配送地图
                    if($stateParams.cityId == null){
                        alert("请选择城市");
                        window.close();
                        return;
                    }
                    $http({
                        url: '/admin/api/restaurant/delivery',
                        params: {
                            cityId: $stateParams.cityId,
                            orderStatus:-3
                        },
                        method: 'GET'
                    }).success(function (data) {
                        var restaurants = data.content;
                        for (var i = 0; i < restaurants.length; i++) {
                            if(restaurants[i].address.wgs84Point){
                                marker.name = restaurants[i].name;
                                marker.lon = restaurants[i].address.wgs84Point.longitude;
                                marker.lat = restaurants[i].address.wgs84Point.latitude;
                                $scope.addMarker(marker);
                            }
                        }
                    })
                }else if($stateParams.type == 4){
                    //截单地图
                    $scope.orderListSearchForm = {
                        page: 0,
                        pageSize: 1000,
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
                        status:3
                    };

                    $http({
                       url: '/admin/api/order',
                       method: "GET",
                       params: $scope.orderListSearchForm
                    }).success(function (data, status, headers, config) {
                       for (var i = 0; i < data.orders.length; i++) {
                           var restaurant = data.orders[i].restaurant;
                           if(restaurant.address.wgs84Point){
                               marker.name = restaurant.name;
                               marker.lon = restaurant.address.wgs84Point.longitude;
                               marker.lat = restaurant.address.wgs84Point.latitude;
                               $scope.addMarker(marker);
                           }
                       }
                    }).error(function (data, status, headers, config) {
                       window.alert("搜索失败...");
                    });

                }
            }
        });


    });