'use strict';
angular.module('sbAdminApp')
    .controller('spikeItemModifyCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window, AlertErrorMessage) {

        $scope.searchForm = {
            spikeId : $stateParams.id
        };

        $scope.bundleOptions =[{val:true,name:'打包'},{val:false,name:'单品'}];

        if($scope.searchForm.spikeId!=null) {
            $http({
                method: 'GET',
                url: '/admin/api/spike/query/'+$scope.searchForm.spikeId
                //params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.data = data;
                $scope.initSku(data);
            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })

        }

        $scope.initSku=function(data){
            $scope.searchSkus=[];
            $.each(data.spikeItems, function(i,vo){
                vo.skuId = vo.sku.id;
                $scope.searchSkus.push(vo.sku);
            });
        }

        $scope.removeItem = function(index) {
            $scope.data.spikeItems.splice(index, 1);
        }

        $scope.funcAsync = function (name) {

            if (name && name !== "") {
                $scope.searchSkus = [];
                $http({
                    url: "/admin/api/dynamic-price/candidatesPlus",
                    method: 'GET',
                    params: { name: name, showLoader: false}
                }).then(
                    //$http.get("/admin/api/dynamic-price/candidates?warehouse="+$scope.restaurant.customer.block.warehouse.id+"&name="+name).then(
                    function (data) {
                        $scope.searchSkus=data.data;
                    }
                )
            }
        }

        //增加一项商品
        $scope.appendItem=function(){
            var item={
                skuId:null,
                name:null,
                price:null,
                num:null,
                bundle:false
            };
            $scope.data.spikeItems.push(item);
        }



        //$scope.skuSetting = function(item,selectedSku) {
        //    console.log(item);
        //    console.log(selectedSku);
        //    item.skuId=selectedSku.id;
        //    item.name=selectedSku.name;
        //};

        $scope.submit=function(){

            if($scope.data.spikeItems.length==0){
                alert("请至少添加一项商品");
                return;
            }

            for(var i=0;i<$scope.data.spikeItems.length;i++){
                if($scope.data.spikeItems[i].skuId==null || $scope.data.spikeItems[i].skuId.length==0){
                    alert("请选择第"+(i+1)+"项商品的sku！");
                    return;
                }
                if($scope.data.spikeItems[i].num!=null && $scope.data.spikeItems[i].perMaxNum > $scope.data.spikeItems[i].num){
                    alert("第"+(i+1)+"项商品的用户限购数量请小于活动数量！");
                    return;
                }
            }

            var param = {
                spikeId: $scope.data.id,
                description: $scope.data.description,
                items: $scope.data.spikeItems
            };
            //提交修改
            $http({
                method: 'PUT',
                url: '/admin/api/spike/modify',
                data: param,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
            .success(function(data,status,headers,config){
                window.alert("修改成功!");
                //$window.history.back();
            })
            .error(function(data,status,headers,config){
                AlertErrorMessage.alert(data);
            });
        }


});