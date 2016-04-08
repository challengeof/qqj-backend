/**
 * Created by Administrator on 2016/04/06.
 */
'use strict';
angular.module('sbAdminApp')
    .controller('CarRouteListCtrl',function($scope, $q, $rootScope, $http, $filter, $state, $stateParams, $location){
        $scope.queryForm = {
            page:$stateParams.page,
            pageSize:$stateParams.pageSize,
            name:$stateParams.name,
            price:$stateParams.price,
            cityId:$stateParams.cityId,
            depotId:$stateParams.depotId
        };
        $scope.page = {};

        $scope.list = function(){
            $http({
                url:"/admin/api/carRoute/list",
                method:'GET',
                params:$scope.queryForm,
                headers:{'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function(data){
                $scope.routies = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.currentPage = data.page + 1;

                $scope.idx = data.page*data.pageSize+1;
            }).error(function(data){
                alert("加载失败......");
            });
        }

        $scope.list();

        $scope.query = function(){
            $scope.queryForm.page = 0;
            $location.search($scope.queryForm);
        }


        $scope.pageChanged = function () {
            $scope.queryForm.page = $scope.page.currentPage - 1;
            $scope.queryForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.queryForm);
        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.depotCities;
            /*if ($scope.cities && $scope.cities.length == 1) {
             $scope.orderGroupSearchForm.cityId = $scope.cities[0].id;
             }*/
        }

        $scope.$watch('queryForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.queryForm.depotId = $scope.depots[0].id;
                    }
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.queryForm.depotId = null;
                }

            }else{
                $scope.depots = [];
                $scope.queryForm.depotId = null;
            }
        });

        $scope.clear = function(){
            $scope.queryForm.page = "",
            $scope.queryForm.pageSize = "",
            $scope.queryForm.name = "",
            $scope.queryForm.price = "",
            $scope.queryForm.cityId = "",
            $scope.queryForm.depotId = ""
        }

    });
