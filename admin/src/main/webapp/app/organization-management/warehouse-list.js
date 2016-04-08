'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListWarehouseCtrl', function ($scope, $rootScope, $http, $stateParams) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.$watch('formData.cityId', function(cityId) {
            $http({
                url: '/admin/api/warehouse',
                method: "GET",
                params: {"cityId":cityId}
            }).success(function (data, status) {
                $scope.warehouses = data;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        });


        $scope.updateWarehouse = function(warehouse ,isDefault){
            $http({
                method: 'put',
                url: '/admin/api/warehouse/isDefault/' + warehouse.id,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
            .success(function(data) {
                window.alert("保存成功!");
                $scope.warehouses = data;
            })
            .error(function(data) {
                window.alert("保存失败!");
            });
        }
    });
