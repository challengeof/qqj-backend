'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockAdjustCtrl
 * @description
 * # StockAdjustCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockAdjustCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-adjust-list");
            return;
        }
        $scope.adjust = $stateParams.stock;
        $scope.adjust.productionDateStr = null;
        if($scope.adjust.productionDate != null){
            $scope.adjust.productionDateStr = $filter('date')($scope.adjust.productionDate, 'yyyy-MM-dd');
        }

        $scope.adjustForm = {
            stockId: $scope.adjust.id,
            quantity: $scope.adjust.quantity
        };

        $scope.submitting = false;
        $scope.adjustStock = function () {

            if (!angular.isNumber($scope.adjust.adjustQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.adjust.adjustQuantity < 0) {
                alert('数量应该大于等于0');
                return;
            }
            if ($scope.adjust.adjustQuantity - $scope.adjust.quantity == 0) {
                alert('原数量和调整后数量相等,没必要做调整');
                return;
            }

            $scope.adjustForm.adjustQuantity = $scope.adjust.adjustQuantity;
            $scope.adjustForm.comment = $scope.adjust.comment;

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stockAdjust/adjust',
                data: $scope.adjustForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('调整单创建成功...')
                $scope.submitting = false;
                $state.go("oam.stock-adjust-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "调整单创建失败...");
                $scope.submitting = false;
            });
        }

    });