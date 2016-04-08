'use strict';

angular.module('sbAdminApp')
    .controller('OrderInfoCtrl', function ($scope, $http, $stateParams) {
        $http.get("/admin/api/order/info/" + $stateParams.id).success(function (data) {
            $scope.order = data;
        }).error(function () {
            alert("加载失败...");
        });
    });

