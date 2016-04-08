'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchDateCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-production-date-list");
            return;
        }
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.stocks = $stateParams.stocks;
        $scope.submitting = false;

        $scope.batchInputDate = function () {

            $scope.stockForm = {
                stockProductionDateDatas: []
            };
            angular.forEach($scope.stocks, function(value, key){
                if (value.productionDate != null) {
                    var obj = new Object();
                    obj.id = value.id;
                    obj.quantity = value.quantity;
                    var productionObj = new Object();
                    productionObj.productionDate = value.productionDate;
                    productionObj.quantity = value.quantity;
                    obj.stockProductionDates = [];
                    obj.stockProductionDates.push(productionObj);
                    $scope.stockForm.stockProductionDateDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockProductionDateDatas.length == 0) {
                alert('请输入生产日期再提交');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchProductionDate',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量录入生产日期成功...')
                $scope.submitting = false;
                $state.go("oam.stock-production-date-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量录入生产日期失败...");
                $scope.submitting = false;
            });
        }
    });