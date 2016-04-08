'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockOnShelfCtrl
 * @description
 * # StockOnShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockMoveShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-moveshelf-list");
            return;
        }
        $scope.stock = $stateParams.stock;
        $scope.stock.productionDateStr = null;
        if($scope.stock.productionDate != null){
            $scope.stock.productionDateStr = $filter('date')($scope.stock.productionDate, 'yyyy-MM-dd');
        }
        $scope.stock.moveQuantity = $scope.stock.quantity;

        $scope.stockForm = {
            id: $scope.stock.id,
            quantity: $scope.stock.quantity
        };
        $scope.submitting = false;

        $scope.codeKeyUp = function(e){
            var keycode = window.event?e.keyCode:e.which;
            if(keycode == 13){
                $scope.searchShelfName($scope.stock.targetShelfCode);
            }
        };

        $scope.searchShelfName = function (code) {
            if(code != null && code != "") {
                $scope.shelfForm = {
                    depotId: $scope.stock.depotId,
                    shelfCode: code
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        $scope.stock.targetShelfName = data.name;
                        $scope.stock.shelfId = data.id;
                    } else{
                        $scope.stock.targetShelfName = null;
                        $scope.stock.shelfId = null;
                    }
                });
            }
        };

        $scope.moveShelf = function () {

            if (!angular.isNumber($scope.stock.moveQuantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.moveQuantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.moveQuantity > $scope.stock.quantity) {
                alert('数量不能大于源数量');
                return;
            }

            var code = "";
            if ($scope.stock.targetShelfName.length >= 2) {
                code += $scope.stock.targetShelfName.substring(0,2);
            }
            if ($scope.stock.targetShelfName.length >= 5) {
                code += $scope.stock.targetShelfName.substring(3,5);
            }
            if ($scope.stock.targetShelfName.length >= 8) {
                code += $scope.stock.targetShelfName.substring(6,8);
            }

            if (code == $scope.stock.shelfCode) {
                alert('源货位应该和目标货位不一致');
                return;
            }
            $scope.stockForm.shelfId = $scope.stock.shelfId;
            $scope.stockForm.shelfCode = code;
            $scope.stockForm.moveQuantity = $scope.stock.moveQuantity;

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/moveShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('移位成功...')
                $scope.submitting = false;
                $state.go("oam.stock-moveshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "移位失败...");
                $scope.submitting = false;
            });
        }

    });