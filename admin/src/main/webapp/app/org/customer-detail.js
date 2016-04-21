'use strict';
angular.module('sbAdminApp')
    .controller('CustomerDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.iForm = {};

        if ($stateParams.level == 0) {
            $scope.isTop = true;
            $scope.iForm.top = true;
            $scope.iForm.level = 0;
        }

        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
        }

        $http({
            url: "/org/team/all",
            method: "GET"
        })
        .success(function (data) {
            $scope.teams = data;
        });

        $scope.createCustomer = function() {
            if ($stateParams.id == '') {
                $http({
                    method: 'post',
                    url: '/org/customer/add',
                    data: $scope.iForm,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        if (data.success) {
                            alert("添加成功!");
                            $state.go("oam.customer-list");
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
                    data: $scope.iForm,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("修改成功！");
                        $state.go("oam.team-list");
                    }).error(function(data) {
                        alert("修改失败!");
                    });
            }
        }
    });
