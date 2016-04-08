'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:CarListCtrl
 * @description
 * # CarListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('CarListCtrl',function($scope, $rootScope, $http, $filter, $state, $stateParams, $location){

    $scope.carSearchForm = {
        cityId:$stateParams.cityId,
        depotId:$stateParams.depotId,
        page: $stateParams.page,
        pageSize: $stateParams.pageSize
    }

    $scope.modelTip = ["暂无","轻型封闭货车","面包","金杯"];
    $scope.statusTip = ["无效","有效"];
    $scope.page = {
        itemsPerPage: 100
    };

    if($rootScope.user) {
       var data = $rootScope.user;
        $scope.cities = data.depotCities;
        if ($scope.cities && $scope.cities.length == 1) {
           $scope.carSearchForm.cityId = $scope.cities[0].id;
        }
    }

    $scope.$watch('carSearchForm.cityId',function(newVal,oldVal){
        if(newVal){
           $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
               $scope.depots = data;
               if ($scope.depots && $scope.depots.length == 1) {
                   $scope.carSearchForm.depotId = $scope.depots[0].id;
               }
           });
           if(typeof oldVal != 'undefined' && newVal != oldVal){
               $scope.carSearchForm.depotId = null;
           }
       }else{
           $scope.depots = [];
           $scope.carSearchForm.depotId = null;
       }
    });

    $scope.search = function(){
        $http({
            url: '/admin/api/car/cars',
            method: "GET",
            params: $scope.carSearchForm
        })
        .success(function(data){
            $scope.cars = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        })
        .error(function(data){
            window.alert("搜索失败...");
        });
    }
});
