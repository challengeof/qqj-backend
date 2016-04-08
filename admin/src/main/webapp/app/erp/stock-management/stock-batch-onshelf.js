'use strict';

angular.module('sbAdminApp')
    .controller('StockBatchOnShelfCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $state, $filter) {
        if ($stateParams.stocks == null) {
            $state.go("oam.stock-willshelf-list");
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
            if(stock.shelfCode != null && stock.shelfCode != "") {
                $scope.shelfForm = {
                    depotId: stock.depotId,
                    shelfCode: stock.shelfCode
                };
                $http({
                    method: 'GET',
                    url: '/admin/api/shelf/code',
                    params: $scope.shelfForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    if (data != null && data != '') {
                        stock.shelfName = data.name;
                        stock.shelfId = data.id;
                    } else{
                        stock.shelfName = null;
                        stock.shelfId = null;
                        stock.shelfCode = null;
                    }
                });
            } else {
                stock.shelfName = null;
                stock.shelfId = null;
                stock.shelfCode = null;
            }
        };

        $scope.batchOnShelf = function () {

            $scope.stockForm = {
                stockShelfDatas: []
            };
            angular.forEach($scope.stocks, function(value, key){
                if (value.shelfId != null) {
                    var obj = new Object();
                    obj.depotId = value.depotId;
                    obj.skuId = value.skuId;
                    obj.availableQuantity = value.availableQuantity;
                    obj.expirationDate = value.expirationDate;
                    var shelfObj = new Object();
                    shelfObj.shelfId = value.shelfId;
                    shelfObj.shelfCode = value.shelfCode;
                    shelfObj.quantity = value.availableQuantity;
                    obj.stockShelfs = [];
                    obj.stockShelfs.push(shelfObj);
                    $scope.stockForm.stockShelfDatas.push(obj);
                }
            });
            if ($scope.stockForm.stockShelfDatas.length == 0) {
                alert('请输入货位码再提交');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stock/batchShelf',
                data: $scope.stockForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('批量上架成功...')
                $scope.submitting = false;
                $state.go("oam.stock-willshelf-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "批量上架失败...");
                $scope.submitting = false;
            });
        }
    });