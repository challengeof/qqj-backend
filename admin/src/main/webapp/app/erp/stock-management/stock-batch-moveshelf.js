'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchMoveShelfCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-moveshelf-list");
            return;
        }
        $scope.stocks = $stateParams.stocks;
        $scope.submitting = false;

        $scope.codeKeyUp = function(e, index){
           var keyCodeValue = (window.event?window.event.keyCode:e.which);
           if (keyCodeValue == 13) {
              if (index >= $scope.stocks.length-1) {
                angular.element('button[type=submit]:first').focus();
              } else {
                angular.element('#shelfCode_'+(index+1)).focus();
              }
           }
        };

        $scope.codeBlur = function (stock) {
            if(stock.targetShelfCode != null && stock.targetShelfCode != "") {
                $scope.shelfForm = {
                    depotId: stock.depotId,
                    shelfCode: stock.targetShelfCode
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        stock.targetShelfName = data.name;
                        stock.shelfId = data.id;
                    } else{
                        stock.targetShelfName = null;
                        stock.shelfId = null;
                        stock.targetShelfCode = null;
                    }
                });
            } else {
                stock.targetShelfName = null;
                stock.shelfId = null;
                stock.targetShelfCode = null;
            }
        };

        $scope.batchMoveShelf = function () {

            $scope.stockForm = {
                stockShelfDatas: []
            };

            var pass = true;
            angular.forEach($scope.stocks, function(value, key){

                if (pass && value.shelfId != null) {
                    if (value.shelfCode == value.targetShelfCode) {
                         alert('第' + (key+1) + '行源货位应该和目标货位不一致');
                         pass = false;
                    }
                    var obj = new Object();
                    obj.id = value.id;
                    obj.quantity = value.quantity;
                    obj.moveQuantity = value.quantity;
                    obj.shelfId = value.shelfId;
                    obj.shelfCode = value.targetShelfCode;
                    $scope.stockForm.stockShelfDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockShelfDatas.length == 0) {
                alert('请输入目标货位再提交');
                return;
            }
            if (!pass) {
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchMoveShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量移位成功...')
                $scope.submitting = false;
                $state.go("oam.stock-moveshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量移位失败...");
                $scope.submitting = false;
            });
        }
    });