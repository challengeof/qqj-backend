'use strict';

angular.module('sbAdminApp')
    .controller('StockOutReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        $scope.stockOutForm = {};
        $scope.stockOutItems = [];
        $scope.collectionments = [];
        $scope.methods = [];
        $scope.cashMethod = null;
        $scope.isReceiveAmountLessZero = false;
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $http.get("/admin/api/stockOut/send/" + $stateParams.stockOutId)
            .success(function (data) {
                $scope.stockOutForm = data;
                $scope.stockOutForm.stockOutType = data.stockOutType.value;
                angular.forEach(data.stockOutItems, function(v, k) {
                    if (v.stockOutItemStatusValue == 1) {
                        $scope.stockOutItems.push(v);
                    }
                });
                $scope.stockOutForm.receiveAmount = data.amount;
                $scope.stockOutForm.settle = false;
                if ($scope.stockOutForm.receiveAmount <= 0) {
                    $scope.isReceiveAmountLessZero = true;
                }
                $scope.loadSuccess = true;

                $http.get("/admin/api/accounting/payment/methods/" + data.cityId)
                .success(function (methodData) {
                    $scope.methods = methodData;
                    var keepGoing = true;
                    angular.forEach($scope.methods,function(method, key) {
                        if (keepGoing && method.cash) {
                            $scope.cashMethod = method.id;
                            keepGoing = false;
                        }
                    });

                    $scope.add($scope.stockOutForm.receiveAmount);
                });
            })
            .error(function (data) {
                window.alert("获取出库单信息失败...");
                return;
            });

        $http.get("/admin/api/sellReturn/reasons")
            .success(function(data) {
                $scope.reasons = data;
            });

        $scope.changeReceiveAmount = function() {
            $scope.stockOutForm.receiveAmount = $scope.stockOutForm.amount;
            angular.forEach($scope.stockOutItems, function(value, key) {
                if (value.returnQuantity) {
                    $scope.stockOutForm.receiveAmount = $scope.stockOutForm.receiveAmount - value.returnQuantity * value.price;
                }
            });
            $scope.stockOutForm.receiveAmount = parseFloat($scope.stockOutForm.receiveAmount.toFixed(2));
            if ($scope.stockOutForm.receiveAmount < 0) {
                $scope.stockOutForm.receiveAmount = 0;
            }
            if ($scope.collectionments.length == 1) {
                $scope.collectionments[0].amount = $scope.stockOutForm.receiveAmount;
            }
        }

        $scope.$watch('stockOutForm.receiveAmount', function (newVal, oldVal) {

            if (newVal != null) {
                $scope.isReceiveAmountLessZero = parseFloat(newVal) <= 0 ? true : false;
            }
            if($scope.isReceiveAmountLessZero) {
                $scope.stockOutForm.settle = true;
            }
        });

        $scope.add = function(amount) {
			$scope.inserted = {
			    collectionPaymentMethodId:$scope.cashMethod,
			    amount:amount
			};
			$scope.collectionments.push($scope.inserted);
		};

        $scope.remove = function(index) {
            $scope.collectionments.splice(index, 1);
        };

        $scope.stockOutReceive = function () {

            var passReceive = true;
            angular.forEach($scope.stockOutItems,function(item, key) {
                if (item.returnQuantity != null && item.returnQuantity.toString().length > 0) {
                    if (passReceive && !angular.isNumber(parseFloat(item.returnQuantity))) {
                        alert('第' + (key+1) + '行退货数量不是有效的数字');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) < 0) {
                        alert('第' + (key+1) + '行退货数量应该大于等于0');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) > parseFloat(item.realQuantity)) {
                        alert('第' + (key+1) + '行退货数量应该小于等于应收数量');
                        passReceive = false;
                    }
                    if (passReceive && parseFloat(item.returnQuantity) > 0 && item.sellReturnReasonId == null) {
                        alert('第' + (key+1) + '行请选择退货原因');
                        passReceive = false;
                    }
                }
            });
            if (!passReceive) {
                return;
            }

            if ($scope.stockOutForm.settle && !$scope.isReceiveAmountLessZero) {
                if ($scope.collectionments.length == 0) {
                    alert('已结款,请选择收款方式并输入收款金额')
                    return;
                }
                var pass = true;
                var collectionmentAmount = 0;
                angular.forEach($scope.collectionments,function(collectionment, key) {
                    if(pass && collectionment.collectionPaymentMethodId == null) {
                        alert('第' + (key+1) + '行请选择收款方式');
                        pass = false;
                    }

                    if (pass && !angular.isNumber(collectionment.amount)) {
                        alert('第' + (key+1) + '行请输入有效金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount <= 0) {
                        alert('第' + (key+1) + '行请输入大于0的金额');
                        pass = false;
                    }
                    if (pass && collectionment.amount.toString().indexOf('.') >= 0 && collectionment.amount.toString().substring(collectionment.amount.toString().indexOf('.')).length > 3) {
                        alert('第' + (key+1) + '行请输入最多两位小数金额');
                        pass = false;
                    }


                    if (collectionment.amount != null && collectionment.amount != 'undefined') {
                        collectionmentAmount += collectionment.amount;
                    }
                });
                if (!pass) {
                    return;
                }
                if (parseFloat($scope.stockOutForm.receiveAmount) - parseFloat(collectionmentAmount.toFixed(2)) != 0) {
                    alert('收款金额和实收金额不符');
                    return;
                }
            } else {
                $scope.collectionments = [];
            }


            $scope.submitting = true;
            $scope.stockOutForm.stockOutId = $stateParams.stockOutId;
            $scope.stockOutForm.stockOutItems = $scope.stockOutItems;
            $scope.stockOutForm.collectionments = $scope.collectionments;

            $http({
                url: "/admin/api/stockOut/send/finish",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("收货成功...");
                $scope.submitting = false;
                $state.go("oam.stockOut-receive-list");
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "收货失败...");
                $scope.submitting = false;
            });
        }

    });