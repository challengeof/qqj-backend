'use strict';

angular.module('sbAdminApp')
    .controller('UpdateVersionCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.versionForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/version/" + $stateParams.id).success(function (data) {
                $scope.versionForm = data;
            })
        }
        $scope.updateVersion = function () {
            $http({
                method: 'POST',
                url: '/admin/api/version/update',
                data: $scope.versionForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
            }).error(function () {
                alert("操作失败...");
            })
        }

    });