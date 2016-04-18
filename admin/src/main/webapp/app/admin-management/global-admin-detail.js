'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddGlobalStaffCtrl', function($scope, $state, $stateParams, $http) {


        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }

        $scope.readyCB = function() {
            $scope.treeInstance.jstree(true).check_node($scope.cityIds);
            $scope.treeInstance.jstree(true).check_node($scope.warehouseIds);
            $scope.treeInstance.jstree(true).check_node($scope.blockIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotCityIds);
            $scope.depotTreeInstance.jstree(true).check_node($scope.depotIds);
        };

        $scope.repeatPassword = null;
        $scope.formData = {
            adminRoleIds: [],
            blockIds:[]
        };

        $http.get("/api/admin-role")
            .success(function(data) {
                $scope.adminRoles = data;
            });
        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/api/admin-user/" + $stateParams.id).success(function(data) {
                $scope.formData.username = data.username;
                $scope.formData.realname = data.realname;
                $scope.formData.telephone = data.telephone;

                if (data.adminRoles) {
                    for (var i = 0; i < data.adminRoles.length; i++) {
                        $scope.formData.adminRoleIds.push(data.adminRoles[i].id);
                    }
                }

                $scope.cityIds = data.cityIds;
                $scope.warehouseIds = data.warehouseIds;
                $scope.blockIds = data.blockIds;
                $scope.depotCityIds = data.depotCityIds;
                $scope.depotIds = data.depotIds;


                $scope.formData.enable = data.enabled;
                $scope.readyCB();

            });
        }


        $scope.createAdminUser = function() {
            if($scope.formData.password != $scope.repeatPassword){
                window.alert("请再次确认密码！");
                return;
            }

            $scope.formData.cityWarehouseBlockIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.depotIds = $scope.depotTreeInstance.jstree(true).get_top_selected();

            $scope.formData.globalAdmin = true;

            if ($stateParams.id == '') {
                $http({
                    method: 'post',
                    url: '/api/admin-user',
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("保存成功!");
                    }).error(function(data) {
                        alert("保存失败!");
                    });
            } else {
                $http({
                    method: 'put',
                    url: '/api/admin-user/' + $stateParams.id,
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("修改成功！");
                    }).error(function(data) {
                        alert("修改失败!");
                    });
            }
        }

            /*表单重置*/
            $scope.resetAdminForm = function(){
                $scope.formData = {
                    adminRoleIds: []
                };
                $scope.repeatPassword = null;
                $scope.isCheckedAll = false;
            }

    });
