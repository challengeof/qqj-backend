'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RoleCtrl', function($scope, $state, $http) {
        /* 用户角色 */
        $http.get("/admin/api/admin-role")
            .success(function(data) {
                $scope.adminRoles = data;
            });


        $http.get("/admin/api/admin-permission")
            .success(function(data) {
                $scope.permissions = data;
            })

        $scope.form = {
            permissionIds : []
        };

        $scope.$watch('form.roleId', function(v) {
            if(v) {
                $http.get('/admin/api/admin-role/' + v)
                    .success(function (data) {
                        $scope.role = data;
                        $scope.form.permissionIds.splice(0, $scope.form.permissionIds.length);
                        for(var i=0;i<data.adminPermissions.length;i++) {
                            $scope.form.permissionIds.push(data.adminPermissions[i].id);
                        }
                    })
            }
        });

        $scope.updateRole = function() {
            $http({
                url: '/admin/api/admin-role/' + $scope.form.roleId,
                method: 'PUT',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                data: $.param({
                    permissions: $scope.form.permissionIds
                })

            }).success(function() {
                alert("修改成功！");
            }).error(function(data) {
                alert("修改失败!");
            });;
        }



    });
