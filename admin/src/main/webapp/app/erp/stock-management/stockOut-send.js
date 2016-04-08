'use strict';

angular.module('sbAdminApp')
    .controller('StockOutSendCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType
        };
        $scope.stockOutItems = [];
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $http.get("/admin/api/stockOut/send/" + $stateParams.stockOutId).success(function (data) {
            $scope.stockOutForm = data;
            $scope.stockOutItems = data.stockOutItems;
            $scope.loadSuccess = true;
        }).error(function (data) {
            alert("获取出库单信息失败...");
        });

        $scope.stockOutSend = function () {
            $scope.submitting = true;
            $scope.stockOutForm.stockOutId = $stateParams.stockOutId;
            $scope.stockOutForm.stockOutType = $stateParams.stockOutType;
            $scope.stockOutForm.stockOutItems = [];
            angular.forEach($scope.stockOutItems, function(v, k) {
                if (v.stockOutItemStatusValue == 1) {
                    $scope.stockOutForm.stockOutItems.push(v);
                }
            });
            $http({
                url: "/admin/api/stockOut/send/add",
                method: "POST",
                data: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("出库成功...");
                $scope.submitting = false;
                $state.go('oam.stockOut-list', {stockOutType: $scope.stockOutType});
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "出库失败...");
                $scope.submitting = false;
            });
        }
    });