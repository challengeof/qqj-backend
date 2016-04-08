'use strict';

angular.module('sbAdminApp')
    .controller('StockOutAllReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stockOuts == null) {
            $state.go("oam.stockOut-receive-list");
        }
        $scope.stockOutForm = {};
        $scope.stockOutForm.stockOuts = $stateParams.stockOuts;
        $scope.stockOutForm.type = 0;
        $scope.collectionments = [];
        $scope.methods = [];
        $scope.cashMethod = null;
        $scope.isReceiveAmountLessZero = false;
        $scope.cityId = null;
        $scope.submitting = false;

        $scope.stockOutForm.receiveAmount = 0;
        $scope.stockOutForm.stockOutIds = [];
        angular.forEach($scope.stockOutForm.stockOuts, function(value, key) {
            if ($scope.cityId == null) {
                $scope.cityId = value.cityId;
            }
            $scope.stockOutForm.stockOutIds.push(value.stockOutId);
            $scope.stockOutForm.receiveAmount += value.amount;
        });
        $scope.stockOutForm.receiveAmount = parseFloat($scope.stockOutForm.receiveAmount.toFixed(2));

        $http.get("/admin/api/accounting/payment/methods/" + $scope.cityId)
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

        $scope.stockOutForm.settle = false;
        if ($scope.stockOutForm.receiveAmount <= 0) {
            $scope.isReceiveAmountLessZero = true;
        }

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
            $scope.stockOutForm.collectionments = $scope.collectionments;

            $http({
                url: "/admin/api/stockOut/send/finish-all",
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