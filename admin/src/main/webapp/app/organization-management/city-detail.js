'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('cityDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.isEdit = false;
        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/city/" + $stateParams.id).success(function(data) {
                $scope.city = data;
            });
        }

        $scope.createCity = function() {

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/city',
                        data: "name=" + $scope.city.name,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("保存成功!");
                    })
                    .error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                        method: 'put',
                        url: '/admin/api/city/' + $stateParams.id,
                        data: "name=" + $scope.city.name,
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                        }
                    })
                    .success(function(data) {
                        alert("修改成功！");
                    })
                    .error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

    });
