'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockOnShelfCtrl
 * @description
 * # StockOnShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockOnShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams, $filter) {

        if ($stateParams.stock == null) {
            $state.go("oam.stock-willshelf-list");
            return;
        }
        $scope.stock = $stateParams.stock;
        $scope.stock.productionDateStr = null;
        if($scope.stock.productionDate != null){
            $scope.stock.productionDateStr = $filter('date')($scope.stock.productionDate, 'yyyy-MM-dd');
        }
        $scope.stock.quantity = $scope.stock.availableQuantity;

        $scope.stockForm = {

            depotId: $scope.stock.depotId,
            skuId: $scope.stock.skuId,
            availableQuantity: $scope.stock.availableQuantity,
            expirationDate: $scope.stock.expirationDate,
            stockShelfs: []
        };
        $scope.submitting = false;

        $scope.codeKeyUp = function(e){
            var keycode = window.event?e.keyCode:e.which;
            if(keycode == 13){
                $scope.searchShelfName($scope.stock.shelfCode);
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
                        $scope.stock.shelfName = data.name;
                        $scope.stock.shelfId = data.id;
                    } else{
                        $scope.stock.shelfName = null;
                        $scope.stock.shelfId = null;
                    }
                });
            }
        };

        $scope.add = function(shelfId, shelfCode, quantity) {
            $scope.inserted = {
                shelfId:shelfId,
                shelfCode:shelfCode,
                quantity:quantity
            };
            $scope.stockForm.stockShelfs.push($scope.inserted);
        };

        $scope.addShelf = function () {
            if (!angular.isNumber($scope.stock.quantity)) {
                alert('请输入有效的数量');
                return;
            }
            if ($scope.stock.quantity <= 0) {
                alert('数量应该大于0');
                return;
            }
            if ($scope.stock.quantity > $scope.stock.availableQuantity) {
                alert('数量不能大于待上架数量');
                return;
            }

            $scope.stock.availableQuantity -= $scope.stock.quantity;
            var code = "";
            if ($scope.stock.shelfName.length >= 2) {
                code += $scope.stock.shelfName.substring(0,2);
            }
            if ($scope.stock.shelfName.length >= 5) {
                code += $scope.stock.shelfName.substring(3,5);
            }
            if ($scope.stock.shelfName.length >= 8) {
                code += $scope.stock.shelfName.substring(6,8);
            }

            $scope.add($scope.stock.shelfId, code, $scope.stock.quantity);
            $scope.stock.quantity = $scope.stock.availableQuantity;
            $scope.stock.shelfId = null;
            $scope.stock.shelfCode = null;
            $scope.stock.shelfName = null;
        };

        $scope.onShelf = function () {

            if ($scope.stockForm.stockShelfs.length == 0) {
                alert('请先输入数量上架再提交');
                return;
            }
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/onShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('上架成功...')
                $scope.submitting = false;
                $state.go("oam.stock-willshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "上架失败...");
                $scope.submitting = false;
            });
        }

    });