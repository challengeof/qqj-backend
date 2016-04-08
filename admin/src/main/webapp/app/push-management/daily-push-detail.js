'use strict';

angular.module('sbAdminApp')
    .controller('UpdateDailyPushCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

        $scope.dailyPushForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/push/daily/" + $stateParams.id).success(function (data) {
                $scope.dailyPushForm = data;
            })
        }
        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.dailyPushForm.tag == null) {
                $scope.dailyPushForm.tag = $scope.cities[0].name;
            }
        }
        $scope.updateDailyPush = function () {
            $http({
                method: 'POST',
                url: '/admin/api/push/daily/update',
                data: $scope.dailyPushForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
                $state.go("oam.dailyPush");
            }).error(function (data) {
                alert("操作失败..." + data.errmsg);
            })
        }

    });