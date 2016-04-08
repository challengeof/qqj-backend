'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrganizationStaffCtrl', function($scope, $state, $stateParams, $http, $rootScope) {

        $http.get("/admin/api/admin-roles/organization").success(function(data) {
                         $scope.adminRoles = data;
         });

        $scope.treeData = {
            url:"/admin/api/city/blocksTree"
        }
        $scope.depotData = {
            url:"/admin/api/city/depotsTree"
        }

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


        /* 用户角色 */
        $scope.repeatPassword = null;
        $scope.formData = {
            adminRoleIds: [],
            blockIds:[],
            cityId:$stateParams.cityId,
            organizationId:$stateParams.organizationId
        };

        /*获取city*/
        if($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        if($stateParams.cityId) {
            $scope.formData.cityId = parseInt($stateParams.cityId);
         }

        if($stateParams.organizationId){
            $scope.formData.organizationId = parseInt($stateParams.organizationId);
        }


        /*$scope.$watch('formData.cityId',function(cityId,oldVal){
          if(cityId){
                $http.get("/admin/api/city/" + cityId+"/organizations").success(function(data) {
                    $scope.organizations = data;
                    if ($scope.organizations && $scope.organizations.length == 1) {
                        $scope.formData.organizationId = $scope.organizations[0].id;
                    }
                    if(typeof oldVal != 'undefined' && $scope.cities.length != 1){
                        $scope.formData.organizationId = null;
                    }
                });
           }else{
                $scope.organizations = [];
                $scope.formData.organizationId = null;
            }
       });*/


        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/admin/api/admin-user/" + $stateParams.id).success(function(data) {
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
            $scope.formData.globalAdmin = false;
            $scope.formData.cityWarehouseBlockIds = $scope.treeInstance.jstree(true).get_top_selected();
            $scope.formData.depotIds = $scope.depotTreeInstance.jstree(true).get_top_selected();

            if ($stateParams.id == '') {
                $http({
                        method: 'post',
                        url: '/admin/api/admin-user',
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
                        url: '/admin/api/admin-user/' + $stateParams.id,
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
        $scope.blocks = [];


       $scope.$watch('formData.organizationId',function(newVal,oldVal){
             if(newVal != "" && newVal != null){
                  $http.get('/admin/api/organization/'+newVal+'/blocks').success(function(data) {
                     $scope.blocks = data;
                 });
             }else{
                 $scope.blocks = [];
             }
       });
        $scope.isCheckedAll = false;

        /*地区权限全选、反选*/
        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                angular.forEach($scope.blocks, function(value, key){
                    $scope.formData.blockIds.push(value.id);
                });
                $scope.isCheckedAll= true;
            }else{
                angular.forEach($scope.blocks, function(value, key){
                    while ($scope.formData.blockIds.indexOf(value.id) != -1) {
                        $scope.formData.blockIds.splice($scope.formData.blockIds.indexOf(value.id), 1);
                    }
                });
                $scope.isCheckedAll = false;
            }
        };


        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                adminRoleIds: [],
                blocks:[]
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = false;
        }

    });
