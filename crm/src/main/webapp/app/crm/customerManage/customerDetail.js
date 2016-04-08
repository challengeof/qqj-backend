'use strict';
angular.module('sbAdminApp')
    .controller('CustomerDetailCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

        $scope.requestTypes={
            "add":{ key:"add", desc:"增加" , title:"添加客户"},
            "modify":{ key:"modify", desc:"修改", title:"修改客户"},
            "audit":{ key:"audit", desc:"审核", title:"餐馆审核"}
        };

        //$scope.addType ={
        //    "sea":{key:"sea",desc:"客户公海"},
        //    "my":{key:"my",desc:"我的客户"},
        //}

        $scope.InParam={
            type:$stateParams.type,
            restaurantId:$stateParams.restaurantId,
            addType:$stateParams.addType
        };

        console.log($scope.InParam);

        $scope.formData = {
            restaurantId:$scope.InParam.restaurantId,
            addType:$scope.InParam.addType
        };

        //销售
        $http.get($rootScope.rootPath+"/api/admin-user/global?role=CustomerService").success(function (data) {
            $scope.adminUsers = data;
        })


        //区块
        $scope.$watch('formData.cityId', function(newVal, oldVal){
            if(newVal != null && newVal != '') {
                $http({
                    method:"GET",
                    url: $rootScope.rootPath+"/api/city/"+ newVal +"/blocks",
                    params: {status:true},
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                }) .success(function(data, status, headers, config) {
                        $scope.blocks = data;
                        if (data.length == 1) {
                            $scope.formData.blockId = data[0].id;
                        }
                    })
                    .error(function(data, status, headers, config) {
                        window.alert("区块信息加载失败！");
                    });

                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.formData.blockId = null;
                }
            } else {
                $scope.blocks = [];
                $scope.formData.blockId = null;
            }
        });
        //经纬度
        $scope.$watch('formData.latLng', function(newVal, oldVal){
            if(newVal!=oldVal){
                if(newVal!=null){
                    var lantLngVal =newVal.split(",");
                    $scope.formData.lng=lantLngVal[0];
                    $scope.formData.lat=lantLngVal[1];
                }
            }
        })
        $scope.$watch('formData.lng', function(newVal, oldVal){
            if(newVal!=oldVal){
                $scope.formData.latLng=$scope.formData.lng+","+$scope.formData.lat;
            }
        })
        $scope.$watch('formData.lat', function(newVal, oldVal){
            if(newVal!=oldVal){
                $scope.formData.latLng=$scope.formData.lng+","+$scope.formData.lat;
            }
        })

        //自动分配区块
        $scope.blockAuto=function(e){
            var paramVal = {
                cityId:$scope.formData.cityId,
                lng:$scope.formData.lng,
                lat:$scope.formData.lat
            };
            if(paramVal.cityId==null || paramVal.cityId.length==0){
                alert("城市未设置");
                event.stopPropagation();
                return;
            }
            if(paramVal.lng==null || paramVal.lat==null){
                alert("坐标设置不正确");
                event.stopPropagation();
                return;
            }

            $http({
                method:"GET",
                url: $rootScope.rootPath+"/api/customerInfo/blockAuto",
                params:paramVal
            }).success(function(data, status, headers, config) {
                    if(data.content!=null){
                        $scope.formData.blockId = data.content.id;
                    }else{
                        alert("未找到匹配的区块");
                    }
            });
        }

        //餐馆分类
        $http.get($rootScope.rootPath+"/api/restaurant/type").success(function(data) {
                $scope.restaurantType = data;
        });

        //合作状态
        $http.get($rootScope.rootPath+"/api/customerInfo/constants/cooperatingState").success(function(data) {
                $scope.cooperatingStates = data;
        });

        //城市
        $http.get($rootScope.rootPath+"/api/city").success(function(data) {
            //alert(data);
            $scope.cities =data;
        });

        $http.get($rootScope.rootPath+"/api/restaurant/reasons")
            .success(function(data) {
                $scope.restaurantReasons = data;
            });
        $http.get($rootScope.rootPath+"/api/restaurant/status")
            .success(function(data) {
                $scope.availableStatus = data;
            });

        $scope.usernamePass=true;

        $scope.verify=function(){
            return $scope.formData.blockId!=null &&    //区块
                    $scope.formData.restaurantType!=null; //餐馆类型
        }

        $scope.submit=function() {

            //客户审核
            if($scope.InParam.type==$scope.requestTypes.audit.key){
                $http.post($rootScope.rootPath+"/api/customerInfo/audit", $scope.formData).success(function(){
                    alert("审核成功!");
                }).error(function (data) {
                    if(data.errmsg!=null){
                        alert(data.errmsg);
                    }else{
                        alert("审核失败!");
                    }
                });
            }

            //增加商户
            if($scope.InParam.type==$scope.requestTypes.add.key){
                $http.post($rootScope.rootPath+"/api/customerInfo/add", $scope.formData).success(function(){
                    alert("执行成功!");
                }).error(function (data) {
                    if(data.errmsg!=null){
                        alert(data.errmsg);
                    }else{
                        alert("执行失败!");
                    }
                });
            }

            //修改商户
            if($scope.InParam.type==$scope.requestTypes.modify.key){
                $http({
                    url: $rootScope.rootPath+"/api/customerInfo/modify",
                    method: "POST",
                    data:  $scope.formData,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功");
                })
                .error(function (data) {
                    if(data.errmsg!=null){
                        alert(data.errmsg);
                    }else{
                        alert("执行失败!");
                    }
                });
            }


        }
        if($scope.InParam.type!=$scope.requestTypes.add.key){

            $http({
                method:"GET",
                url: $rootScope.rootPath+"/api/customerInfo/restaurantInfo/"+$scope.InParam.restaurantId
                //params:paramVal
            }).success(function(data, status, headers, config) {
                $scope.formData=data.content;

            }).error(function (data) {
                alert("获取客户信息失败");
            });

        }

    });
