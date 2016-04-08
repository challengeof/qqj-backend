'use strict';

angular.module('sbAdminApp')
	.controller('CarDetailCtrl',function($scope, $rootScope, $http, $filter, $state, $stateParams, $location){

    $scope.modelTip = [
        {name:'轻型封闭货车', id:1},
        {name:'面包', id:2},
        {name:'金杯', id:3}
    ];
    $scope.statusTip = [{name:'无效' , id:0} , {name:'有效' , id:1}];

    $scope.carSourceArray = new Array("云鸟","一号货车","58网","快狗","领翔货的","正时达物流","一运全城","个人");
    $scope.taxingPointArray = new Array("6%","6%","6%","6%","6%","6%","4%","无");

    $scope.formData = {
        cityId:$stateParams.cityId,
        depotId:$stateParams.depotId,
        trackerId:$stateParams.trackerId,
        licencePlateNumber:$stateParams.licencePlateNumber,
        vehicleLength:$stateParams.vehicleLength,
        vehicleWidth:$stateParams.vehicleWidth,
        vehicleHeight:$stateParams.vehicleHeight,
        vehicleModel:1,
        weight:$stateParams.weight,
        cubic:$stateParams.cubic,
        name:$stateParams.name,
        expenses:$stateParams.expenses,
        taxingPoint:$stateParams.taxingPoint,
        source:$stateParams.source,
        status:0
    }


    if($rootScope.user) {
        var data = $rootScope.user;
        $scope.cities = data.depotCities;
        if ($scope.cities && $scope.cities.length == 1) {
           $scope.formData.cityId = $scope.cities[0].id;
        }
    }

    $scope.$watch('formData.cityId',function(newVal,oldVal){
        if(newVal){
           $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
               $scope.depots = data;
               if ($scope.depots && $scope.depots.length == 1) {
                   $scope.formData.depotId = $scope.depots[0].id;
               }
           });
           if(typeof oldVal != 'undefined' && newVal != oldVal){
               $scope.formData.depotId = null;
           }
       }else{
           $scope.formData.depotId = null;
       }
    });

    $scope.$watch('formData.cityId',function(newVal,oldVal){
        if(newVal){
            $http({
                url : '/admin/api/accounting/tracker/list?role=LogisticsStaff',
                method:"GET",
                params:{"cityId":$scope.formData.cityId}
            })
            .success(function(data) {
                $scope.trackers = data;
            });
        }else{
            $scope.trackers = [];
        }
    });

    $scope.$watch('formData.source' , function(newVal,oldVal){
        if(newVal){
            for(var i=0;i<$scope.carSourceArray.length;i++){
                if($scope.carSourceArray[i] == newVal){
                    $scope.formData.taxingPoint = $scope.taxingPointArray[i];
                    return;
                }
            }
        }
    });

    if($stateParams.id){
        $http({
            url: "/admin/api/car/" + $stateParams.id,
            method: 'GET'
        }).success(function (data) {
            $scope.formData.cityId = data.city.id;
            $scope.formData.depotId = data.depot.id;
            $scope.formData.trackerId = data.adminUser.id;
            $scope.formData.licencePlateNumber = data.licencePlateNumber;
            $scope.formData.vehicleLength = data.vehicleLength;
            $scope.formData.vehicleWidth = data.vehicleWidth;
            $scope.formData.vehicleHeight = data.vehicleHeight;
            $scope.formData.weight = data.weight;
            $scope.formData.cubic = data.cubic;
            $scope.formData.status = data.status;
            $scope.formData.vehicleModel = data.vehicleModel;
            $scope.formData.name = data.name;
            $scope.formData.expenses = data.expenses;
            $scope.formData.source = data.source;
            $scope.formData.taxingPoint = data.taxingPoint;

        }).error(function (data) {
            alert("读取失败!");
        });
    }

    $scope.saveOrUpdateCar = function () {

        if($scope.formData.depotId == null){
            alert("请选择仓库");
            return;
        }

        if($scope.formData.trackerId == null){
            alert("请选择跟车员");
            return;
        }

        if($scope.formData.vehicleModel == null){
            alert("请选择车型");
            return;
        }

        if($scope.formData.status == null){
            alert("请选择是否有效");
            return;
        }

        if($stateParams.id)
            $scope.formData.id = $stateParams.id;

        $http({
            url: "/admin/api/car/saveOrUpdate",
            method: 'POST',
            data: $scope.formData,
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            }
        }).success(function (data) {
            if($stateParams.id){
                alert("更新成功");
            }else{
                alert("保存成功！");
                initFormData();
            }
        }).error(function (data) {
            alert("保存失败!");
        });
    }

    function initFormData(){
        $scope.formData.licencePlateNumber = null;
        $scope.formData.vehicleLength = null;
        $scope.formData.vehicleWidth = null;
        $scope.formData.vehicleHeight = null;
        $scope.formData.weight = null;
        $scope.formData.cubic = null;
        $scope.formData.name = null;
        $scope.formData.source = null;
        $scope.formData.taxingPoint = null;
    }

});
