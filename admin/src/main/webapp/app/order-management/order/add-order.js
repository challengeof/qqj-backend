/**
 * Created by challenge on 15/11/30.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('AddOrderCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

        $scope.restaurants = [];
        $scope.candidateRestaurants = [];
        $scope.restaurant = {};

        $scope.funcAsyncRestaurant = function (name) {
            if (name && name !== "") {
                $scope.candidateRestaurants = [];
                $http({
                    url: "/admin/api/restaurant/candidates",
                    method: 'GET',
                    params: {page: 0, pageSize: 20, name: name, showLoader:false}
                }).then(
                //$http.get("/admin/api/restaurant/candidates?page=0&pageSize=20&name="+name).then(
                    function (data) {
                        $scope.candidateRestaurants = data.data;
                    }
                )
            }
        }

        $scope.searchRestaurant = function(restaurant) {
            $scope.candidateRestaurants = [];

            $http.get("/admin/api/restaurant/" + restaurant.id).success(function (data, status, headers, config) {
                if (!data) {
                    alert('餐馆不存在或已失效');
                    restaurant.id = '';
                    return;
                }
                $scope.candidateRestaurants.push(data);
                $scope.restaurant = data;
            }).error(function (data, status, headers, config) {
                alert('餐馆不存在或已失效');
                restaurant.id = '';
                return;
            });
        };

        $scope.resetCandidateRestaurants = function () {
            $scope.candidateRestaurants = [];
        }

        //$scope.types = [
        //    {
        //        "id": 1,
        //        "name": "普通"
        //    },
        //    {
        //        "id": 2,
        //        "name": "赠品"
        //    }
        //]
        $scope.types=[];
        $http({
            url: "/admin/api/order/orderType/get",
            method: 'GET'
        }).then(
            function (data) {
                $scope.types=data.data;
                $scope.searchForm.type = $scope.types[0].val;
                console.log($scope.types);
            }
        )

        $scope.searchForm = {};
        $scope.submitting = false;
        $scope.orderTotalPrice = 0;
        $scope.candidateDynamicPrices = [];

        $scope.funcAsync = function (name) {
            if (name && name !== "") {
                $scope.candidateDynamicPrices = [];
                $http({
                    url: "/admin/api/dynamic-price/candidates",
                    method: 'GET',
                    params: {warehouse: $scope.restaurant.customer.block.warehouse.id, name: name, showLoader: false}
                }).then(
                //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        angular.forEach(data.data,function(item) {
                            $scope.candidateDynamicPrices.push(item.sku);
                        })
                    }
                )
            }
        }

        $scope.resetcandidateDynamicPrices = function () {
            $scope.candidateDynamicPrices = [];

        }
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }


        $scope.orderItems = [
        ];

        $scope.remove = function(index) {
            $scope.orderItems.splice(index, 1);
        }

        $scope.addItem = function() {
            $scope.inserted = {
            };
            $scope.orderItems.push($scope.inserted);
        };

        $scope.totalBundleQuantity = function(orderItem) {
            orderItem.quantity = orderItem.bundleQuantity * orderItem.capacityInBundle;
            if($scope.searchForm.type == 2) {
                orderItem.totalPrice = 0;
            }else{
                $scope.calculatePrice(orderItem);
            }
        };

        $scope.totalQuantity = function(orderItem) {
            orderItem.bundleQuantity = orderItem.quantity / orderItem.capacityInBundle;
            if($scope.searchForm.type == 2) {
                orderItem.totalPrice = 0;
            }else{
                $scope.calculatePrice(orderItem);
            }
        };

        $scope.searchSku = function(orderItem) {
            $scope.candidateDynamicPrices = [];
            $http({
                url:"/admin/api/dynamic-price/sku",
                method:'GET',
                params:{skuId:orderItem.skuId,warehouse:$scope.restaurant.customer.block.warehouse.id},
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                if (!data.sku) {
                    alert('sku不存在或已失效');
                    orderItem.skuId = '';

                    return;
                }

                $scope.candidateDynamicPrices.push(data.sku);
                orderItem.skuId = data.sku.id;
                orderItem.name = data.sku.name;
                orderItem.rate = data.sku.rate;
                orderItem.singleUnit = data.sku.singleUnit;
                orderItem.bundleUnit = data.sku.bundleUnit;
                orderItem.capacityInBundle = data.sku.capacityInBundle;
                orderItem.singleSalePrice = data.singleDynamicSkuPriceStatus.singleSalePrice;
                orderItem.bundleSalePrice = data.bundleDynamicSkuPriceStatus.bundleSalePrice;
                if(data.singleDynamicSkuPriceStatus.singleInSale) {
                    $scope.singleEdit = true;
                }
                if(data.bundleDynamicSkuPriceStatus.bundleInSale) {
                    $scope.bundleEdit = true;
                }
                if(orderItem.quantity) {
                    if($scope.searchForm.type == 2) {
                        orderItem.totalPrice = 0;
                    }else{
                        orderItem.totalPrice = orderItem.singleSalePrice != 0 ? orderItem.singleSalePrice * orderItem.quantity : orderItem.bundleSalePrice * orderItem.bundleQuantity;
                        $scope.calculatePrice(orderItem);
                    }
                }

            });
        };

        $scope.orderRequest = {

            restaurantId : null,
            remark : null,
            type : null,
            requests : []
        };
        $scope.changeInput = function(typeId) {
            while($scope.orderItems.length > 0) {
                $scope.orderItems.pop();
            }
            $scope.orderTotalPrice = 0;
            //if(typeId == 1) {
            //    while($scope.orderItems.length > 0) {
            //        $scope.orderItems.pop();
            //    }
            //
            //}else if(typeId == 2) {
            //
            //}
        };

        $scope.createOrder = function() {
            $scope.orderRequest.requests = [];
            $scope.orderRequest.restaurantId = $scope.restaurant.id;
            $scope.orderRequest.remark = $scope.restaurant.remark;
            $scope.orderRequest.type = $scope.searchForm.type;
            for(var i = 0 ; i < $scope.orderItems.length ; i++){
                if(!$scope.orderItems[i].quantity) {
                    alert("请输入购买数量!");
                    return;
                }
                $scope.orderRequest.requests.push({
                    skuId : $scope.orderItems[i].skuId,
                    quantity : $scope.orderItems[i].quantity,
                    bundle : false,
                    price : $scope.orderItems[i].singleSalePrice
                })
            }
            $http({
                url:"/admin/api/order/create",
                method:"POST",
                data:$scope.orderRequest
            }).success(function(data) {
                alert("创建成功!")
                $scope.orderItems = [];
                $scope.restaurants = [];
                $scope.candidateRestaurants = [];
                $scope.restaurant = {};
                $scope.searchForm.type = 1;
            }).error(function(data) {
                alert("创建失败!")
            })
        };

        $scope.calculatePrice = function(orderItem) {

            orderItem.totalPrice = orderItem.singleSalePrice != 0 ? orderItem.singleSalePrice * orderItem.quantity : orderItem.bundleSalePrice * orderItem.bundleQuantity;
            $scope.orderTotalPrice = 0;
            for(var i = 0; i < $scope.orderItems.length; i++) {
                $scope.orderTotalPrice = $scope.orderTotalPrice + $scope.orderItems[i].totalPrice;
            }
        };

    });