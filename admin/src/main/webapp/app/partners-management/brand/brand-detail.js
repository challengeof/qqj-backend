'use strict';

angular.module('sbAdminApp')
    .controller('UpdateBrandCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.brandForm = {};
        if ($stateParams.id) {
            $http.get("/admin/api/brand/" + $stateParams.id).success(function (data) {
                $scope.brandForm = data;
                if ($scope.brandForm.status == "有效") {
                    $scope.brandForm.status = 1;
                } else {
                    $scope.brandForm.status = 0;
                }
            })
        } else {
            $scope.brandForm.status = 1;
        }
        $scope.updateBrand = function () {
            $http({
                method: 'POST',
                url: '/admin/api/brand/update',
                data: $scope.brandForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
            }).error(function () {
                alert("操作失败...");
            })
        }

    });