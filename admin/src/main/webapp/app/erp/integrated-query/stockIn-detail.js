'use strict';

angular.module('sbAdminApp')
    .controller('StockInDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {
        $scope.id = $stateParams.id;
        $http({
            url: '/admin/api/stockIn/' + $scope.id,
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIn = data;
            $scope.stockInItems = data.stockInItems;
        });
    });