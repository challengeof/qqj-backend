'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateSystemEmailCtrl
 * @description
 * # AddOrUpdateSystemEmailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateSystemEmailCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.systemEmail = {};
        $scope.submitting = false;
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.systemEmail.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/systemEmail/type/list").success(function (data) {
            $scope.type = data;
        });

        $scope.isEdit = false;

        /*根据id获取信息*/
        if ($stateParams.id != null && $stateParams.id != '') {
            $scope.isEdit = true;
            $http.get("/admin/api/systemEmail/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.systemEmail = data;
                });
        }

        /*添加/编辑 */
        $scope.createSystemEmail = function () {

            $scope.submitting = true;
            if ($scope.isEdit) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/systemEmail/' + $stateParams.id,
                    data: $scope.systemEmail,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                    $scope.submitting = false;
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/systemEmail',
                    data: $scope.systemEmail,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                    $scope.submitting = false;
                })
            }
        }

    });