'use strict';

angular.module('sbAdminApp')
    .controller('PreciseBatchPushCtrl', function ($scope, $http, $stateParams) {

        $scope.pushForm = {};
        $scope.pushForm.restaurantIds = [];

        if ($stateParams.ids) {
            $scope.pushForm.ids = $stateParams.ids;
        }

        $scope.createPush = function () {
            if (!new RegExp("^[0-9,]+$").test($scope.pushForm.ids)) {
                alert("餐馆ID包含非法字符");
                return;
            }
            if (window.confirm("本次推送共选择" + $scope.pushForm.ids.trim().split(',').length + "个餐馆,推送不可撤销,是否继续?") == true) {
                angular.forEach($scope.pushForm.ids.trim().split(','), function (value) {
                    $scope.pushForm.restaurantIds.push(value);
                });
                $http({
                    method: 'POST',
                    url: '/admin/api/push/precise/create',
                    data: $scope.pushForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function () {
                    alert("推送成功!");
                }).error(function () {
                    alert("推送失败!");
                })
            }
        };

    });
