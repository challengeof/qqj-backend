'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('WarehouseDetailCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        $scope.isEdit = false;

        $scope.warehouse = {
            cityId:undefined,
            depotId:undefined
        };

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/warehouse/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                $scope.warehouse = data;
                $scope.warehouse.cityId = data.city.id;
                if (data.depot) {
                    $scope.warehouse.depotId = data.depot.id;
                }

            });
        }

        $scope.$watch('warehouse.cityId', function(cityId, old) {
            $http({
                method:"GET",
                url:"/admin/api/depot/list",
                params:{cityId:cityId}
            })
            .success(function(data) {
                $scope.depot = data;
            });
        });

        $scope.createCity = function() {

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/warehouse',
                        data: $scope.warehouse,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
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
                        url: '/admin/api/warehouse/' + $stateParams.id,
                        data: $scope.warehouse,
                        headers: {
                            'Content-Type': 'application/json;charset=UTF-8'
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
