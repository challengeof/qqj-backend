/**
 * Created by Administrator on 2016/04/07.
 */
'use strict';
angular.module('sbAdminApp')
    .controller('CarRouteDetailCtrl',function($rootScope,$scope, $http, $stateParams,$state){
        $scope.carRouteForm = {


        };

        if($rootScope.user) {
         var data = $rootScope.user;
         $scope.cities = data.depotCities;
         /*if ($scope.cities && $scope.cities.length == 1) {
         $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
         }*/
         }

        $scope.$watch('carRouteForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.carRouteForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.carRouteForm.depotId = null;
                }

            }else{
                $scope.depots = [];
                $scope.carRouteForm.depotId = null;
            }
        });

        $scope.createRoute = function(){
            $http({
                url:"/admin/api/carRoute/update",
                method:"POST",
                data:$scope.carRouteForm,
                headers:{'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function(data){
                alert("添加成功");
                $state.go("oam.carRoute-list");
            }).error(function(data){
                console.log($scope.carRouteForm);
                alert("加载失败......");
            });
        }

        if ($stateParams.id) {
            $http.get("/admin/api/carRoute/" + $stateParams.id)
                .success(function (data) {
                    $scope.carRouteForm = data;
                })
                .error(function (data, status) {
                    window.alert("获取线路信息失败...");
                });

        }

    });


