'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderDetailCtrl
 * @description
 * # OrderDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderDetailCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/order/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                $scope.order = data;
                $scope.sellReturnItems = data.sellReturnItems;
            });

        $http.get("/admin/api/sellReturn/reasons")
            .success(function(data) {
                $scope.reasons = data;
            });

        $http.get("/admin/api/order/reason")
            .success(function(data) {
                $scope.cancelOrderReasons = data;
            });

        $scope.refundObj = {
            reasonId : null,
            skuRefundRequests : []
        };
        $scope.refundArr = [];
        $scope.newPromotions = [];
        $scope.newCoupons = [];

        $scope.addSellCancelToArr = function (orderItem, check) {
            $scope.newPromotions = [];
            $scope.newCoupons = [];
            var tag = true;
            if (!orderItem.cancelQuantity) {
                orderItem.cancelQuantity = 0;
            }
            var availableQuantity = orderItem.bundle ? (orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity)/orderItem.sku.capacityInBundle : (orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity);
            if (availableQuantity < orderItem.cancelQuantity) {
                alert("数量不能大于可操作数量");
                return;
            }
            if ($scope.refundArr == null || $scope.refundArr.length === 0) {
                $scope.refundArr.push({
                    orderItemId: orderItem.id,
                    skuId:orderItem.sku.id,
                    bundle:orderItem.bundle,
                    quantity: orderItem.cancelQuantity,
                    availableQuantity:availableQuantity,
                    memo:orderItem.memo,
                    reasonId:orderItem.cancelOrderReasonId
                });
            } else {
                for (var i = 0; i < $scope.refundArr.length; i++) {
                    if ($scope.refundArr[i].orderItemId == orderItem.id) {
                        $scope.refundArr.splice(i, 1);
                        $scope.refundArr.push({
                            orderItemId: orderItem.id,
                            skuId:orderItem.sku.id,
                            bundle:orderItem.bundle,
                            quantity: orderItem.cancelQuantity,
                            availableQuantity:availableQuantity,
                            memo:orderItem.memo,
                            reasonId:orderItem.cancelOrderReasonId
                        });
                        tag = false;
                    }
                }

                if(tag){
                    $scope.refundArr.push({
                        orderItemId: orderItem.id,
                        skuId:orderItem.sku.id,
                        bundle:orderItem.bundle,
                        quantity: orderItem.cancelQuantity,
                        availableQuantity:availableQuantity,
                        memo:orderItem.memo,
                        reasonId:orderItem.cancelOrderReasonId
                    });
                }
            }

            if (check) {
                $scope.refundObj.orderId = $scope.order.id;
                $scope.refundObj.sellCancelItemRequest = $scope.refundArr;
                if (orderItem.cancelQuantity > 0) {
                    $http({
                        url: "/admin/api/order/newPromotion",
                        method: "POST",
                        data: $scope.refundObj
                    }).success(function (data) {
                        $scope.newPromotions = data.promotions;
                        $scope.newCoupons = data.customerCoupons;
                        var s = '';
                        if ($scope.newPromotions.length > 0) {
                            angular.forEach(data.promotions, function(value, key) {
                                s += "\n" + value.description;
                            });
                        }
                        if ($scope.newCoupons.length > 0) {
                            angular.forEach(data.customerCoupons, function(value, key) {
                                s += "\n" + value.coupon.description;
                            });
                        }
                        if (s.length > 0) {
                            alert("被取消的优惠:"+ s);
                        }
                    }).error(function (data) {
                        alert("操作失败");
                    });
                }
            }
        }

        $scope.addSellCancel = function () {

            for (var i = 0; i < $scope.refundArr.length; i++) {
                if ($scope.refundArr[i].availableQuantity < $scope.refundArr[i].quantity) {
                    console.log($scope.refundArr[i]);
                    alert("数量不能大于可操作数量");
                    return;
                }
                if (!$scope.refundArr[i].reasonId) {
                    alert("取消原因不能为空");
                    return;
                }
            }
            if ($scope.refundArr.length == 0) {
                alert("暂无取消");
                return;
            }
            $scope.refundObj.orderId = $scope.order.id;
            $scope.refundObj.sellCancelItemRequest = $scope.refundArr;
            $http({
                url: "/admin/api/sellCancel",
                method: "post",
                data: $scope.refundObj
            }).success(function (data) {
                alert("操作成功!");
                $scope.refundArr = [];
                $scope.order = data;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }

        $scope.addSellReturnToArr = function (orderItem) {
            var tag = true;
            if (!orderItem.returnQuantity) {
                orderItem.returnQuantity = 0
            }
            var availableQuantity = orderItem.countQuantity - orderItem.sellCancelQuantity - orderItem.sellReturnQuantity;
            if (availableQuantity < orderItem.returnQuantity) {
                    alert("数量不能大于可操作数量");
            }
            if ($scope.refundArr == null || $scope.refundArr.length === 0) {
                $scope.refundArr.push({
                    orderItemId: orderItem.id,
                    availableQuantity:availableQuantity,
                    quantity: orderItem.returnQuantity,
                    reasonId: orderItem.reasonId,
                    memo: orderItem.memo
                });
            } else {
                for (var i = 0; i < $scope.refundArr.length; i++) {
                    if ($scope.refundArr[i].orderItemId == orderItem.id) {
                        $scope.refundArr.splice(i, 1);
                        $scope.refundArr.push({
                            orderItemId: orderItem.id,
                            availableQuantity:availableQuantity,
                            quantity: orderItem.returnQuantity,
                            reasonId: orderItem.reasonId,
                            memo: orderItem.memo
                        });
                        tag = false;
                    }
                }

                if(tag){
                    $scope.refundArr.push({
                        orderItemId: orderItem.id,
                        availableQuantity:availableQuantity,
                        quantity: orderItem.returnQuantity,
                        reasonId: orderItem.reasonId,
                        memo: orderItem.memo
                    });
                }
            }
        }

        $scope.addSellReturn = function () {

            for (var i = 0; i < $scope.refundArr.length; i++) {
                if (!$scope.refundArr[i].reasonId) {
                    alert("退货理由不能为空");
                    return;
                }
                if ($scope.refundArr[i].availableQuantity < $scope.refundArr[i].quantity) {
                    alert("数量不能大于可操作数量");
                    return;
                }
            }
            if ($scope.refundArr.length == 0) {
                alert("暂无退货");
                return;
            }
            $scope.refundObj.orderId = $scope.order.id;
            $scope.refundObj.sellReturnItemRequests = $scope.refundArr;
            $http({
                url: "/admin/api/sellReturn",
                method: "post",
                data: $scope.refundObj
            }).success(function (data) {
                alert("操作成功!");
                $scope.refundArr = [];
                $scope.order = data;
                $scope.sellReturnItems = data.sellReturnItems;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }

        $scope.backOut = function (sellCancelId) {
            $http({
                url:"/admin/api/sellReturn/" + sellCancelId,
                method:"PUT",
                params: {"status":2, "auditOpinion":"撤销"},
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }).success(function(data) {
                alert("撤销成功");
                $scope.order.havaUnFinishedSellCancel = false;
                angular.forEach($scope.sellReturnItems, function(value, key) {
                    value.sellReturnStatus.name = "已撤销";
                });
                $scope.order.sellCancelId = null;
            }).error(function (data) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }
    });

