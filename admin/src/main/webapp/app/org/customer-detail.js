'use strict';
angular.module('sbAdminApp')
    .controller('CustomerDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.iForm = {};

        $scope.iForm.stocks = [];

        $http({
            url: "/product/list/all",
            method: "GET",
        })
        .success(function (data) {
            $scope.products = data;
        });

        $scope.add = false;
        if ($stateParams.id) {
            $http({
                url: "/org/customer/" + $stateParams.id,
                method: "GET",
            })
            .success(function (data) {
                $scope.iForm = data;
                $scope.iForm.level = data.level.value;
            });

        } else {
            $scope.add = true;
            $scope.iForm.level = 0;
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
                    url: '/org/founder/add',
                    data: $scope.iForm,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                    }).success(function(data) {
                        if (data.success) {
                            alert("添加成功!");
                            $state.go("oam.customer-list", {searchFounder:1,level:0});
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

        $scope.remove = function(productId) {
            angular.forEach(scope.iForm.stocks, function(item, itemIndex){
                if (item.productId == productId) {
                    $scope.iForm.stocks.splice(itemIndex, 1);
                }
            });
        }

        $scope.addItem = function() {
            $scope.inserted = {
            };
            $scope.iForm.stocks.push($scope.inserted);
        };
    });
