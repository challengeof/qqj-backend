'use strict';

angular.module('sbAdminApp')
    .controller('StockOutDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {
        $scope.stockOutType = $stateParams.stockOutType;
        $scope.id = $stateParams.id;
        $http({
            url: '/admin/api/stockOut/' + $scope.id,
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOut = data;
            $scope.stockOutItems = data.stockOutItems;
        });
    });