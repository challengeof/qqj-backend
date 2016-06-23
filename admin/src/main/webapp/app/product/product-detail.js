'use strict';
angular.module('sbAdminApp')
    .controller('ProductDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.data = {};

        $scope.isEdit = false;

        if ($stateParams.id) {
            $scope.isEdit = true;
            $http.get("/product/" + $stateParams.id).success(function(data) {
                $scope.data = data;
            });
        }

        $http({
            url: "/product/status/list",
            method: "GET"
        })
        .success(function (data) {
            $scope.statusList = data;
        });

        $scope.createProduct = function() {
            if ($stateParams.id == '') {
                $http({
                    method: 'post',
                    url: '/product/add',
                    data: $scope.data,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("添加成功!");
                        $state.go("oam.product-list");
                    }).error(function(data) {
                        alert("添加失败!");
                    });
            } else {
                $http({
                    method: 'put',
                    url: '/product/modify/' + $stateParams.id,
                    data: $scope.data,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        alert("修改成功！");
                        $state.go("oam.product-list");
                    }).error(function(data) {
                        alert("修改失败!");
                    });
            }
        }
    });
