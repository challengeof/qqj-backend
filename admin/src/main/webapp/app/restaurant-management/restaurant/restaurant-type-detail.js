/**
 * Created by challenge on 16/1/20.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RestaurantTypeDetailCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {

        };

        /*分类状态list*/
        $http.get("/admin/api/restaurantType/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })



        $http.get("/admin/api/restaurantType")
            .success(function (data, status, headers, config) {
                $scope.availableParentRestaurantTypes = [];

                angular.forEach(data, function (value, key) {
                    if (!$stateParams.id || $stateParams.id != value.id) {
                        this.push(value);
                    }
                }, $scope.availableParentRestaurantTypes);
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })

        if ($stateParams.id) {
            $http.get("/admin/api/restaurantType/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                $scope.formData.status = data.status.value;
                if(data.parentRestaurantTypeId) {
                    $scope.formData.parentRestaurantTypeId = data.parentRestaurantTypeId;
                }

                $scope.formData.displayOrder = data.displayOrder;

            });
        }

        $scope.saverestaurantType = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/restaurantType/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("修改成功!");
                    })
                    .error(function (data) {
                        alert("修改失败,"+data.errmsg);
                    });
            } else {
                $http({
                    url: "/admin/api/restaurantType",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败,"+data.errmsg);
                    });
            }
        }
    });

