'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateDepotCtrl
 * @description
 * # AddOrUpdateDepotCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateDepotCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        /*仓库*/
        $scope.depot = {isMain : false};
        $scope.isEditDepot = false;

        /*根据id获取仓库信息*/
        if ($stateParams.id) {
            $scope.isEditDepot = true;

            $http.get("/admin/api/depot/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.depot = data;
                    $scope.depot.cityId = data.city.id;
                })
                .error(function (data, status) {
                    window.alert("获取仓库信息失败...");
                });
        }

        /*添加/编辑仓库*/
        $scope.createDepot = function () {
            if (($scope.depot.longitude != null && $scope.depot.longitude != '' && ($scope.depot.latitude == null || $scope.depot.latitude == ''))
                || ($scope.depot.latitude != null && $scope.depot.latitude != '' && ($scope.depot.longitude == null || $scope.depot.longitude == ''))) {
                window.alert("经度和纬度必须同时有值");
                return;
            }
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/depot/' + $stateParams.id,
                    data: $scope.depot,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/depot',
                    data: $scope.depot,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                })
            }
        }

    });