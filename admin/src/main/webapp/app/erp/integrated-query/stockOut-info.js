'use strict';

angular.module('sbAdminApp')
    .controller('StockOutInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/stockOut/" + $stateParams.id).success(function (data) {
            $scope.stockOut = data;
        }).error(function () {
            alert("加载失败...");
        });
    });

