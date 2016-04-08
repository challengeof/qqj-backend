'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
    .controller('CreateCouponCtrl', function ($scope, $rootScope, $http, $stateParams, $state, $filter) {

        $scope.init = function(couponType, send) {
            $scope.showDiscount = [1,2,3,4,6,7,8].indexOf(couponType) >= 0;
            $scope.showSkuId = [5].indexOf(couponType) >= 0;
            $scope.showQuantity = [5].indexOf(couponType) >= 0;
            $scope.showSendRestrictionsTotal = [1,5,8].indexOf(couponType) >= 0;
            $scope.showSendRestrictionsCategories = [1,5,8].indexOf(couponType) >= 0;
            $scope.showUseRestrictionsCategories = [1,3,5,7,8].indexOf(couponType) >= 0;
            $scope.showDeadline = [5,8].indexOf(couponType) >= 0;
            $scope.showCityId = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showWarehouseId = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showName = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showUseRestrictionsTotal = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showStart = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showEnd = [1,2,3,4,5,7,8].indexOf(couponType) >= 0;
            $scope.showDescription = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showRemark = [1,2,3,4,5,6,7,8].indexOf(couponType) >= 0;
            $scope.showPeriodOfValidity = [1,3,6].indexOf(couponType) >= 0;
            $scope.showScore = [7].indexOf(couponType) >= 0;
            $scope.showBuySkuId = [8].indexOf(couponType) >=0;
            $scope.showBuySkuUnit = [8].indexOf(couponType) >=0;
            $scope.showBuyQuantity = [8].indexOf(couponType) >=0;
            $scope.showSendCouponQuantity = [8].indexOf(couponType) >=0;
            $scope.showBeginningDays = [3].indexOf(couponType) >=0;
            $scope.showBrandId = [1,2,5].indexOf(couponType) >= 0;
            $scope.showCouponRestriction = [1,5,8].indexOf(couponType) >= 0;
        }

        $scope.skuUnit = [
            {
                "name" : "请选择sku单位"
            },
            {
                "id" : true,
                "name" : "打包"
            },
            {
                "id" : false,
                "name" : "单品"
            }
        ]

        $scope.promotionPatterns = [
            {
                "name" : "请选择活动方式"
            },
            {
                "id": 1,
                "name": "满足条件就可以参加活动"
            },
            {
                "id": 2,
                "name": "今日首单"
            },
            {
                "id": 3,
                "name": "餐馆首单"
            }
        ]

        $scope.init(null, null);

        if ($stateParams.id && $stateParams.couponType == null) {
            $http.get("/admin/api/coupon/" + $stateParams.id).then(function(result) {
                $state.go($state.current, {couponType:result.data.couponType}, {reload: true});
            })
        } else {

            $scope.restaurants = [];

            $scope.candidateRestaurants = [];

            $scope.funcAsync = function (name) {
                if (name && name !== "") {
                    $scope.candidateRestaurants = [];
                    $http.get("/admin/api/restaurant/candidates?page=0&pageSize=20&name="+name).then(
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
                        alert('sku不存在或已失效');
                        restaurant.id = '';
                        return;
                    }
                    $scope.candidateRestaurants.push(data);
                }).error(function (data, status, headers, config) {
                    alert('sku不存在或已失效');
                    restaurant.id = '';
                    return;
                });
            };

            $scope.$watch('coupon.cityId',function(newVal, oldVal){
                if(newVal){
                    $http.get("/admin/api/city/" + newVal + "/warehouses").success(function(data) {
                        $scope.warehouses = data;
                    });
                }else{
                    $scope.warehouses = [];
                }
            });

            $scope.resetCandidateRestaurants = function () {
                $scope.candidateRestaurants = [];
            }

            $scope.addItem = function() {
                $scope.inserted = {
                };
                $scope.restaurants.push($scope.inserted);
            };

            $scope.remove = function(index) {
                $scope.restaurants.splice(index, 1);
            };

            $scope.coupon = {
                categoryIds:[]
            };
            if ($stateParams.type == 1) {
                $scope.add = true;
            } else if ($stateParams.type == 2) {
                $scope.edit = true;
            } else if ($stateParams.type == 3) {
                $scope.send = true;
            } else if ($stateParams.type == 4) {
                $scope.batchSendCoupon = true;
            }
            var couponType = $stateParams.couponType;

            if (couponType != null) {
                $scope.init(parseInt(couponType), $scope.send);
            }

            $scope.categories = {
                url : '/admin/api/category/treeJson'
            }

            $scope.treeConfig = {
                'plugins': ["wholerow", "checkbox"],
            }

            $scope.changeCouponType = function() {
                $state.go($state.current, {couponType:$scope.coupon.couponType}, {reload: true});
            };

            $http.get("/admin/api/admin-user/me")
                .success(function (data, status, headers, config) {
                    $scope.cities = data.cities;
                });

            $http.get("/admin/api/brand")
                .success(function (data) {
                    $scope.brands = data;
                })

            $scope.readyCB = function() {
                if ($scope.sendRestrictionsTreeInstance) {
                    $scope.sendRestrictionsTreeInstance.jstree(true).check_node($scope.sendRestrictionsCategoryIds);
                }

                if ($scope.useRestrictionsTreeInstance) {
                    $scope.useRestrictionsTreeInstance.jstree(true).check_node($scope.useRestrictionsCategoryIds);
                }
            };

            $http.get("/admin/api/coupon/couponEnums")
                .success(function (data, status, headers, config) {
                    $scope.couponTypes = data;
                });

            $http.get("/admin/api/coupon/sendCouponReasons")
                .success(function (data, status, headers, config) {
                    $scope.sendCouponReasons = data;
                });

            if ($stateParams.id) {
                $http.get("/admin/api/coupon/" + $stateParams.id).then(function(result) {
                    $scope.coupon = result.data;
                    $scope.sendRestrictionsCategoryIds = result.data.sendRestrictionsCategoryIds;
                    $scope.useRestrictionsCategoryIds = result.data.useRestrictionsCategoryIds;
                    $scope.readyCB();
                })
            }

            $scope.createCoupon = function () {
                if ($scope.sendRestrictionsTreeInstance) {
                    $scope.coupon.sendRestrictionsCategoryIds = $scope.sendRestrictionsTreeInstance.jstree(true).get_top_selected();
                }

                if ($scope.useRestrictionsTreeInstance) {
                    $scope.coupon.useRestrictionsCategoryIds = $scope.useRestrictionsTreeInstance.jstree(true).get_top_selected();
                }

                if ($stateParams.id != '') {
                    $http({
                        method: 'PUT',
                        url: '/admin/api/coupon/edit/' + $stateParams.id,
                        data: $scope.coupon,
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                        .success(function (data, status, headers, config) {
                            alert("修改成功!");
                            $state.go("oam.couponManagement");
                        })
                        .error(function (data, status, headers, config) {
                            alert("修改失败!");
                        })
                } else {
                    $http({
                        method: 'POST',
                        url: '/admin/api/coupon/create',
                        data: $scope.coupon,
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                        .success(function (data, status, headers, config) {
                            alert("添加成功!");
                            $state.go("oam.couponManagement");
                        })
                        .error(function (data, status, headers, config) {
                            alert("添加失败!");
                        })
                }
            }

            $scope.sendCouponRequest = {};

            $scope.sending = false;
            $scope.sendCouponToCustomers = function () {
                $scope.sending = true;

                if ($scope.send) {
                    $scope.sendCouponRequest.restaurantIds = [];
                    angular.forEach($scope.restaurants, function(item, key) {
                        $scope.sendCouponRequest.restaurantIds.push(item.id);
                    });
                } else if ($scope.batchSendCoupon) {
                    $scope.sendCouponRequest.restaurantIds = $scope.sendCouponRequest.restaurantIds.split('\n');
                }

                $scope.sendCouponRequest.couponId = $stateParams.id;

                $http({
                    method: 'PUT',
                    url: '/admin/api/coupon/send',
                    data: $scope.sendCouponRequest,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    $scope.restaurants = [];
                    $scope.sendCouponRequest.restaurantIds = [];
                    alert("发送成功!");
                    $scope.sending = false;
                })
                .error(function (data, status, headers, config) {
                    alert("发送失败!");
                    $scope.sending = false;
                })

            }

            $scope.changeSendCouponReason = function() {
                if ($scope.sendCouponRequest.reason == 10) {
                    $scope.sendCouponRequest.remark = '';
                } else {
                    var reason = $filter('filter')($scope.sendCouponReasons, {value:  $scope.sendCouponRequest.reason});
                    $scope.sendCouponRequest.remark = reason.length ? reason[0].name : '';
                }
            }
        }
    });
