'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockProductionDateCtrl
 * @description
 * # StockProductionDateCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockProductionDateCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-production-date-list");
            return;
        }
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.stock = $stateParams.stock;
        $scope.stock.moveQuantity = $scope.stock.quantity;

        $scope.stockForm = {
            id: $scope.stock.id,
            quantity: $scope.stock.quantity,
            stockProductionDates: []
        };
        $scope.submitting = false;

        $scope.add = function(productionDate, quantity) {
            $scope.inserted = {
                productionDate:productionDate,
                quantity:quantity
            };
            $scope.stockForm.stockProductionDates.push($scope.inserted);
        };

        $scope.addProductionDate = function () {
            if (!angular.isNumber($scope.stock.moveQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.moveQuantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.moveQuantity > $scope.stock.quantity) {
                alert('数量不能大于待录入数量');
                return;
            }

            $scope.stock.quantity -= $scope.stock.moveQuantity;
            $scope.add($scope.stock.productionDate, $scope.stock.moveQuantity);
            $scope.stock.moveQuantity = $scope.stock.quantity;
        };

        $scope.submitProductionDate = function () {

            if ($scope.stockForm.stockProductionDates.length == 0) {
                alert('请先输入数量录入生产日期再提交');
                return;
            }
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/inputProductionDate',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('录入生产日期成功...')
                $scope.submitting = false;
                $state.go("oam.stock-production-date-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "录入生产日期失败...");
                $scope.submitting = false;
            });
        }

    });