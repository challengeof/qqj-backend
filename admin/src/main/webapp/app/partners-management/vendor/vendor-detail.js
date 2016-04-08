'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SupplierDetailCtrl
 * @description
 * # SupplierDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('VendorDetailCtrl', function ($scope, $rootScope, $http, $stateParams, AlertService) {
        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.$watch('vendorFormData.city.id', function(cityId,old) {
            if(cityId) {
                $http.get("/admin/api/city/" + cityId + "/organizations").success(function(data) {
                    $scope.organizations = data;

                    if (typeof old != 'undefined') {
                        $scope.vendorFormData.organization.id = null;
                    }
                });
            } else {
                $scope.organizations = [];
            }
        });

        $scope.$watch('vendorFormData.city.id', function(newVal, oldVal) {
            if(newVal) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId: newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        /*供应商添加/编辑form*/
        $scope.vendorFormData = {};

        /*根据供应商id获取编辑信息*/
        if ($stateParams.id) {

            $http.get("/admin/api/vendor/" + $stateParams.id)
                .success(function (data, status, headers, config) {
                    $scope.vendorFormData = data;
                })
                .error(function (data, status, headers, congfig) {
                    window.alert("获取供应商信息失败...");
                })
        }

        /*提交添加/编辑供应商表单数据*/
        $scope.saveVendor = function () {
            if ($stateParams.id == '') {
                $http({
                    url: '/admin/api/vendor',
                    method: 'POST',
                    data: $scope.vendorFormData,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status) {
                        window.alert("供货商信息添加成功！");
                    })
                    .error(function () {
                        window.alert("添加失败...");
                    })
            } else {
                $http({
                    url: '/admin/api/vendor/' + $stateParams.id,
                    method: 'PUT',
                    data: $scope.vendorFormData,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status) {
                        alert("修改成功！");
                    })
                    .error(function (data, status) {
                        alert("修改失败...");
                    })
            }
        }

        $scope.vendorFilter = function(item) {
            return item.city.id == $scope.vendorFormData.city.id;
        }

    });