'use strict';
angular.module('sbAdminApp')
    .controller('AdminDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.formData = {};

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

                $scope.formData.enable = data.enabled;
            });
        }


        $scope.createAdminUser = function() {
            if($scope.formData.password != $scope.repeatPassword){
                window.alert("两次输入的密码不一致！");
                return;
            }

            if ($stateParams.id == '') {
                $http({
                    method: 'post',
                    url: '/api/admin-user',
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        if (data.success) {
                            alert("添加成功!");
                            $state.go("oam.admin-list");
                        } else {
                            alert(data.msg);
                        }
                    }).error(function(data) {
                        alert("添加失败!");
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
                        $state.go("oam.admin-list");
                    }).error(function(data) {
                        alert("修改失败!");
                    });
            }
        }
    });
