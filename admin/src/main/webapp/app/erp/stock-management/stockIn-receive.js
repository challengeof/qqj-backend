'use strict';

angular.module('sbAdminApp')
    .controller('StockInReceiveCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {

        $scope.stockInType = $stateParams.stockInType;
        $scope.part = $stateParams.part;
        $scope.viewHeader = $scope.part == true || $scope.part == 'true' ? '部分收货入库' : '收货入库';
        $scope.stockInForm = {
            stockInType: $scope.stockInType
        };
        $scope.stockInItems = [];
        $scope.submitting = false;
        $scope.loadSuccess = false;

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $http.get("/admin/api/stockIn/receive/" + $stateParams.stockInId).success(function (data) {
            $scope.stockInForm = data;
            $scope.stockInItems = data.stockInItems;
            $scope.loadSuccess = true;
        }).error(function (data) {
            alert("获取入库单信息失败...");
        });

        $scope.stockInReceive = function () {
            $scope.submitting = true;
            $scope.stockInForm.stockInId = $stateParams.stockInId;
            $scope.stockInForm.stockInType = $stateParams.stockInType;
            $scope.stockInForm.part = $scope.part;
            $scope.stockInForm.stockInItems = $scope.stockInItems;
            $http({
                url: "/admin/api/stockIn/receive/add",
                method: "POST",
                data: $scope.stockInForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("入库成功...");
                $scope.submitting = false;
                $state.go('oam.stockIn-list', {stockInType: $scope.stockInType});
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "入库失败...");
                $scope.submitting = false;
            });
        }
    });