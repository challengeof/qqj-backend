'use strict';
angular.module('sbAdminApp')
    .controller('CustomerSeaCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {

        $scope.searchForm = {
            pageSize: 20,
            blockId:$stateParams.blockId,
            restaurantTypeId:$stateParams.restaurantTypeId,
            devUserId:$stateParams.devUserId,
            adminUserId:$stateParams.adminUserId
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        }
        //城市
        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        ////区块
        //$scope.$watch('searchForm.cityId', function(newVal, oldVal){
        //    if(newVal != null && newVal != '') {
        //        $http({
        //            method:"GET",
        //            url: $rootScope.rootPath+"/api/city/"+ newVal +"/blocks",
        //            params: {status:true},
        //            headers: {
        //                'Content-Type': 'application/json;charset=UTF-8'
        //            }
        //        }) .success(function(data, status, headers, config) {
        //                $scope.blocks = data;
        //                if (data.length == 1) {
        //                    $scope.searchForm.blockId = data[0].id;
        //                }
        //            })
        //            .error(function(data, status, headers, config) {
        //                window.alert("区块信息加载失败！");
        //            });
        //
        //        if (typeof oldVal != 'undefined' && newVal != oldVal) {
        //            $scope.searchForm.blockId = null;
        //        }
        //    } else {
        //        $scope.blocks = [];
        //        $scope.searchForm.blockId = null;
        //    }
        //});

        $scope.findBlockName=function(blockid){
            if($scope.blocks==null){return}
            for(var i=0;i<$scope.blocks.length;i++){
                if($scope.blocks[i].id==blockid){
                    return $scope.blocks[i].name;
                }
            }
        }
        $scope.findRestaurantTypeName=function(typeId){
            if($scope.restaurantType==null){return}
            for(var i=0;i<$scope.restaurantType.length;i++){
                if($scope.restaurantType[i].id==typeId){
                    return $scope.restaurantType[i].name;
                }
            }
        }
        $scope.findGradeName=function(gradeVal){
            if($scope.grades==null){return}
            for(var i=0;i<$scope.grades.length;i++){
                if($scope.grades[i].id==gradeVal){
                    return $scope.grades[i].name;
                }
            }
        }
        $scope.findStatusName=function(restaurantStatus){
            if($scope.availableStatus==null){return}
            for(var i=0;i<$scope.availableStatus.length;i++){
                if($scope.availableStatus[i].value==restaurantStatus){
                    return $scope.availableStatus[i].name;
                }
            }
            console.log($scope.availableStatus);
        }
        $scope.findUserName=function(devUserId){
            if($scope.adminUsers==null){return}
            for(var i=0;i<$scope.adminUsers.length;i++){
                if($scope.adminUsers[i].id==devUserId){
                    return $scope.adminUsers[i].realname;
                }
            }
        }

        //区块信息
        $http({
            method:"GET",
            url: $rootScope.rootPath+"/api/block",
            params: {status:1,pageSize:65535},
            headers: {
                'Content-Type': 'application/json;charset=UTF-8'
            }
        }) .success(function(data, status, headers, config) {
            $scope.blocks = data.blocks;
            if (data.blocks.length == 1) {
                $scope.searchForm.blockId = data.blocks[0].id;
            }
        })
        .error(function(data, status, headers, config) {
            window.alert("区块信息加载失败！");
        });

        //合作状态
        $http.get($rootScope.rootPath+"/api/customerInfo/constants/cooperatingState")
            .success(function(data) {
                $scope.cooperatingStates = data;
            });
        //餐馆状态
        $http.get($rootScope.rootPath+"/api/restaurant/status")
            .success(function(data) {
                $scope.availableStatus = data;
            });
        //餐馆分类
        $http.get($rootScope.rootPath+"/api/restaurant/type")
            .success(function(data) {
                $scope.restaurantType = data;
            });
        //销售
        $http.get($rootScope.rootPath+"/api/admin-user/global?role=CustomerService").success(function (data) {
            $scope.adminUsers = data;
        })

        //客户等级
        $http.get($rootScope.rootPath+"/api/restaurant/grades").success(function (data) {
            $scope.grades = data;
        })

        //销售类型
        $http.get($rootScope.rootPath+"/api/customerInfo/constants/customerFollowUpStatus").success(function (data) {
            $scope.customerFollowUpStatus = data;
        })
        //跟进状态
        $http.get($rootScope.rootPath+"/api/customerInfo/constants/restaurantSellerType").success(function (data) {
            $scope.restaurantSellerType = data;
        })

        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.searchForm);
        }

        $scope.search=function(){
            $scope.searchForm.page = 0
            $scope.searchForm.pageSize = 50;
            console.log($scope.searchForm);
            $location.search($scope.searchForm);
        }

        console.log($scope.searchForm);
        //选中的行是否存在潜在客户
        $scope.hasPotential = function(){
            for(var i=0;i<$scope.customerSelect.val.length;i++){
                console.log($scope.customerSelect.val[i].activeType);
                if($scope.customerSelect.val[i].activeType.val == 1){
                    //console.log($scope.customerSelect.val[i].activeType);
                    return true;
                }
            }
            return false;
        }

        $scope.listLoad=function() {
            $http({
                method: "GET",
                url: $rootScope.rootPath + "/api/restaurant/sea",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                    console.log("--------");
                    console.log(data);

                    $scope.content = data.restaurants;
                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;

                    console.log(data);
                })
                .error(function (data, status, headers, config) {
                    window.alert("列表信息加载失败");
                });
        };

        /******复选框*****/
        $scope.customerSelect = { val: [] };
        $scope.checkAllSelect=false;
        //全选
        $scope.$watch("checkAllSelect", function(newVal, oldVal){
            $scope.customerSelect.val=[];
            if( newVal ){
                for(var ix =0; ix <  $scope.content.length;ix++ ){
                     $scope.customerSelect.val.push($scope.content[ix]);
                }
            }
        });

        $scope.getSelectedCheckbox=function(){
            var selectedArray = [];
            for(var c =0;c<$scope.customerSelect.val.length;c++){
                console.log($scope.customerSelect.val[c]);
                selectedArray.push($scope.customerSelect.val[c].id);
            }
            return selectedArray;
        }
        /******复选框*****/

        //认领客户
        $scope.claim=function(){
            if(!confirm("您确定要认领这些客户吗？")){
                return ;
            }
            var param ={restaurantId: $scope.getSelectedCheckbox()};

            $http({
                method:"post",
                url: $rootScope.rootPath+"/api/customerInfo/claim",
                data: param
            }) .success(function(data, status, headers, config) {
                alert("认领成功,请等待审核");
                $scope.listLoad();
            })
            .error(function(data, status, headers, config) {
                window.alert("认领失败");
            });

        }

        $scope.allotForm={};


        var $confirm = $("#modalConfirm");
        //分配销售弹框
        $scope.allotConfirmYesNo = function () {

            var now =new Date();
            var day30=new Date();
            day30.setDate(day30.getDate()+30);

            $confirm.modal('show');
            $scope.allotForm={};
            $scope.allotForm.beginDate= now;
            $scope.allotForm.endDate=day30;

            $("#btnNoConfirmYesNo").off('click').click(function () {
                $confirm.modal("hide");
            });
        };

        //请求分配参数是否正确验证
        $scope.allotEnterEnable=function(){
             return $scope.allotForm.allotUser!=null;
                 //$scope.allotForm.beginDate!=null &&
                 //$scope.allotForm.endDate!=null &&
                 //$scope.allotForm.sellerType!=null &&
                 //$scope.allotForm.followUpStatus!=null;
        }
        //请求分配
        $scope.allotReq = function () {
            $scope.allotForm.restaurantId = $scope.getSelectedCheckbox();
            $http({
                method:"post",
                url: $rootScope.rootPath+"/api/customerInfo/allot",
                data: $scope.allotForm
            }) .success(function(data, status, headers, config) {
                alert("分配成功");
                $confirm.modal("hide");
                $scope.listLoad();
            }) .error(function(data, status, headers, config) {
                alert("分配失败");
            });

        }

        $scope.listLoad();



    });
