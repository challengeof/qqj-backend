'use strict';
angular.module('sbAdminApp')
.controller('spikeAddCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {


        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.addForm={
            cityId: null,
            endTime: null,
            startTime: null,
            warehouseId:$stateParams.warehouseId,
            items:[]
        };

        //增加一项商品
        $scope.appendItem=function(){
            var item={
                skuId:null,
                name:null,
                price:null,
                num:null,
                bundle:false
            };
            $scope.addForm.items.push(item);
        }
        $scope.removeItem = function(index) {
            $scope.addForm.items.splice(index, 1);
        }

        $scope.funcAsync = function (name) {
            if (name && name !== "") {
                $scope.searchSkus = [];
                $http({
                    url: "/admin/api/dynamic-price/candidatesPlus",
                    method: 'GET',
                    params: {warehouse: $scope.addForm.warehouseId, name: name, showLoader: false}
                }).then(
                    //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        $scope.searchSkus=data.data;
                    }
                )
            }
        }

        //$scope.checkPerMaxNum=function(item) {
        //    var evt = window.event;
        //    var element=evt.srcElement || evt.target;
        //    if (element.checkValidity()) {
        //        if (item.num!=null && item.perMaxNum > item.num) {
        //            element.setCustomValidity("此值不能大于活动数量");
        //            element.validity.valid=true;
        //        }
        //    }
        //}


        $scope.submitForm=function(){

            if($scope.addForm.startTime>= $scope.addForm.endTime){
                alert("起始时间需要小于截止时间");
                return ;
            }
            if($scope.addForm.items.length==0){
                alert("请至少添加一项商品");
                return;
            }
            for(var i=0;i<$scope.addForm.items.length;i++){
                if($scope.addForm.items[i].skuId==null || $scope.addForm.items[i].skuId.length==0){
                    alert("请选择第"+(i+1)+"项商品的sku！");
                    return;
                }
                if($scope.addForm.items[i].num!=null && $scope.addForm.items[i].perMaxNum > $scope.addForm.items[i].num){
                    alert("第"+(i+1)+"项商品的用户限购数量请小于活动数量！");
                    return;
                }
            }

            $http({
                url:"/admin/api/spike/add",
                method:"POST",
                headers: {'Content-type': 'application/json;charset=UTF-8'},
                data:$scope.addForm
            }).success(function(data) {
                alert("创建成功!")
            }).error(function(data) {
                alert("创建失败!")
            })

        }


        $scope.skuSetting = function(item,selectedSku) {
            console.log(item);
            console.log(selectedSku);
            item.skuId=selectedSku.id;
            item.name=selectedSku.name;
        };


        //$scope.$watch('addForm.cityId', function (newVal, oldVal) {
        //    if (newVal != null && newVal != "") {
        //        $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
        //            $scope.warehouses = data;
        //            if ($scope.warehouses && $scope.warehouses.length == 1) {
        //                $scope.addForm.warehouseId = $scope.warehouses[0].id;
        //            }
        //        });
        //        if (typeof oldVal != "undefined" && newVal != oldVal) {
        //            $scope.addForm.warehouseId = null;
        //        }
        //    } else {
        //        $scope.warehouses = [];
        //        $scope.addForm.warehouseId = null;
        //    }
        //});
        //加载城市
        if ($rootScope.user) {
            $scope.cities = $rootScope.user.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.addForm.cityId = $scope.cities[0].id;
            }
        }





});