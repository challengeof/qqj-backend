/**
 * Created by challenge on 16/1/20.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('RestaurantTypeListCtrl', function($scope, $http, $stateParams, $location, $rootScope) {

        //if($rootScope.user) {
        //    var data = $rootScope.user;
        //    $scope.cities = data.cities;
        //}


        $scope.treeOptions = {
            dropped : function(event) {
                console.log(event);
                var destParentrestaurantTypeId = 0;
                var destChildren = [];
                if(event.dest.nodesScope.$parent.$modelValue) {
                    destParentrestaurantTypeId = event.dest.nodesScope.$parent.$modelValue.id;

                    event.dest.nodesScope.$parent.$modelValue.children.forEach(function(child) {destChildren.push(child.id);});
                }

                var sourceParentrestaurantTypeId = 0;
                var sourceChildren = [];
                if(event.source.nodesScope.$parent.$modelValue) {
                    sourceParentrestaurantTypeId = event.source.nodesScope.$parent.$modelValue.id;

                    event.source.nodesScope.$parent.$modelValue.children.forEach(function(child) {sourceChildren.push
                    (child.id);});
                }

                $http({
                    url: "/admin/api/restaurantType/" + destParentrestaurantTypeId + "/children",
                    params: {children: destChildren},
                    method: 'PUT'
                })
                    .then(function (data) {
                        $http({
                            url: "/admin/api/restaurantType/" + sourceParentrestaurantTypeId + "/children",
                            params: {children: sourceChildren},
                            method: 'PUT'
                        })
                            .success(function (data) {
                                alert("修改成功!");
                            })
                            .error(function (data) {
                                alert("修改失败!");
                            });
                    })



            }
        }

        $scope.form = {
            status : [1,2,3]
        }

        $scope.visible = function (item) {
            return $scope.form.status.indexOf(item.status) >= 0;
        };

        $http({
            url:"/admin/api/restaurantType/treeJson",
            method:'GET',
            params: {'cityId' :$stateParams.cityId}
        })
            .success(function(data){
                $scope.nodes = data;
            })
            .error(function(data){

            });

        $scope.setrestaurantTypeCity = function(node, cityId, active) {
            $http({
                url:"/admin/api/restaurantType/"+ node.id +"/changeCity",
                method:'PUT',
                params: {'cityId' :cityId, 'active': active}
            })
                .success(function(data){
                    if (active === true) {
                        if (node.cityIds.indexOf(cityId) == -1) {
                            node.cityIds.push(cityId);
                        }
                    } else {
                        if (node.cityIds.indexOf(cityId) != -1) {
                            node.cityIds.splice(node.cityIds.indexOf(cityId), 1);
                        }
                    }
                })
                .error(function(data){
                    alert("失败");
                });
        }
    });
