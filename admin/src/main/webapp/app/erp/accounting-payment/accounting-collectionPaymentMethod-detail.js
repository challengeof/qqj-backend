'use strict';
angular.module('sbAdminApp')
    .controller('AccountCollectionPaymentMethodDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

        /*收付款方式*/
        $scope.accountCollectionPaymentMethod = {
            cash : false,
            valid : true
        };
        $scope.edit = false;

        /*根据id获取信息*/
        if ($stateParams.id) {
            $scope.edit = true;
            $http.get("/admin/api/account/collectionPaymentMethod/" + $stateParams.id).success(function (data, status) {
                $scope.accountCollectionPaymentMethod = data;
                $scope.accountCollectionPaymentMethod.cityId = data.cityId;
            }).error(function (data, status) {
                window.alert("获取信息失败...");
            });
        }

        /*添加/编辑收付款方式*/
        $scope.create = function () {
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/account/collectionPaymentMethod/' + $stateParams.id,
                    data: $scope.accountCollectionPaymentMethod,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("修改成功!");
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/account/collectionPaymentMethod',
                    data: $scope.accountCollectionPaymentMethod,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("添加成功!");
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                })
            }
        }
    });