'use strict';

angular.module('sbAdminApp')
    .controller('DailyPushListCtrl', function ($scope, $rootScope, $http, $state) {
        $http({
            url: "/admin/api/push/daily/list",
            method: 'GET',
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.pushes = data;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.deleteDailyPush = function (id) {
            $http.delete("/admin/api/push/daily/" + id).success(function () {
                alert("操作成功...");
                $state.reload();
            }).error(function () {
                alert("操作失败...");
            })
        }
    });