'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrganizationCtrl
 * @description
 * # AddOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrganizationCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        /*$scope.treeData = {
            url : '/admin/api/category/treeJson'
        }*/

        $scope.cityBlockData = {
            url : '/admin/api/city/blocksTree'
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }


        $scope.readyCB = function() {
//            $scope.treeInstance.jstree(true).check_node($scope.formData.categoryIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.blockIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.warehouseIds);
            $scope.cityWarehouseBlockInstance.jstree(true).check_node($scope.cityIds);
        };


        $scope.formData = {
            blockIds: [],
            categoryIds:[]
        };

        $scope.$watch('formData.cityId', function(cityId) {
            if(cityId != null && cityId != '') {
                $http.get("/admin/api/city/"+cityId+"/warehouses").success(function(data) {
                    $scope.warehouses = data;
                });
            } else {
                $scope.warehouses = [];
            }
        });




        $scope.isEdit = false;
        if ($stateParams.id != '') {
            $scope.isEdit = true;

            $http.get("/admin/api/organization/" + $stateParams.id).success(function(data) {
                $scope.formData.id = data.id;
                $scope.formData.name = data.name;
//                $scope.formData.cityId = data.city.id;
                $scope.formData.telephone = data.telephone;

                $scope.cityIds = data.cityIds;
                $scope.warehouseIds = data.warehouseIds;
                $scope.blockIds = data.blockIds;

                if (data.blocks) {
                    for (var i = 0; i < data.blocks.length; i++) {
                        $scope.formData.blockIds.push(data.blocks[i].id);
                    }
                }

                $scope.formData.categoryIds = data.categoryIds;
                $scope.readyCB();
                $scope.formData.enable = data.enabled;
            });
        }


        $scope.createOrganization = function() {
//            $scope.formData.categoryIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.cityWarehouseBlockIds = $scope.cityWarehouseBlockInstance.jstree(true).get_top_selected();

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/organization',
                        data: $scope.formData,
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
                        url: '/admin/api/organization/' + $stateParams.id,
                        data: $scope.formData,
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

        $scope.isCheckedAll = [];

        /*地区权限全选、反选*/
        $scope.checkAll = function(warehouse) {
            if(!($scope.isCheckedAll[warehouse.id])){
                angular.forEach(warehouse.blocks, function(value, key){
                    $scope.formData.blockIds.push(value.id);
                });
                $scope.isCheckedAll[warehouse.id] = true;
            }else{
                angular.forEach(warehouse.blocks, function(value, key){
                    while ($scope.formData.blockIds.indexOf(value.id) != -1) {
                        $scope.formData.blockIds.splice($scope.formData.blockIds.indexOf(value.id), 1);
                    }
                });
                $scope.isCheckedAll[warehouse.id] = false;
            }
        };

        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                blockIds: []
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = [];
        }
        
    });
