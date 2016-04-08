'use strict';

angular.module('sbAdminApp')
    .controller('PurchaseOrderInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/purchase/order/info/" + $stateParams.id).success(function (data) {
            $scope.purchaseOrder = data;
        }).error(function () {
            alert("加载失败...");
        });
    });

