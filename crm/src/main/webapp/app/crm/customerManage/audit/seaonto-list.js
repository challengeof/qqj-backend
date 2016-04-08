'use strict';
angular.module('sbAdminApp')
    .controller('seaontolistCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window) {


        $scope.reqType=3; //投放公海
        $scope.undefinedStatus = 1 ; //未审核状态值

        $scope.searchForm = {
            pageSize: 20,
            reqType: $scope.reqType
            //blockId:$stateParams.blockId,
            //restaurantTypeId:$stateParams.restaurantTypeId,
            //devUserId:$stateParams.devUserId,
            //adminUserId:$stateParams.adminUserId
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

        //$scope.findBlockName=function(blockid){
        //    if($scope.blocks==null){return}
        //    for(var i=0;i<$scope.blocks.length;i++){
        //        if($scope.blocks[i].id==blockid){
        //            return $scope.blocks[i].name;
        //        }
        //    }
        //}
        //$scope.findRestaurantTypeName=function(typeId){
        //    if($scope.restaurantType==null){return}
        //    for(var i=0;i<$scope.restaurantType.length;i++){
        //        if($scope.restaurantType[i].id==typeId){
        //            return $scope.restaurantType[i].name;
        //        }
        //    }
        //}
        //$scope.findGradeName=function(gradeVal){
        //    if($scope.grades==null){return}
        //    for(var i=0;i<$scope.grades.length;i++){
        //        if($scope.grades[i].id==gradeVal){
        //            return $scope.grades[i].name;
        //        }
        //    }
        //}
        //$scope.findStatusName=function(restaurantStatus){
        //    if($scope.availableStatus==null){return}
        //    for(var i=0;i<$scope.availableStatus.length;i++){
        //        if($scope.availableStatus[i].value==restaurantStatus){
        //            return $scope.availableStatus[i].name;
        //        }
        //    }
        //    console.log($scope.availableStatus);
        //}
        //$scope.findUserName=function(devUserId){
        //    if($scope.adminUsers==null){return}
        //    for(var i=0;i<$scope.adminUsers.length;i++){
        //        if($scope.adminUsers[i].id==devUserId){
        //            return $scope.adminUsers[i].realname;
        //        }
        //    }
        //}

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

        ////餐馆状态
        //$http.get($rootScope.rootPath+"/api/restaurant/status")
        //    .success(function(data) {
        //        $scope.availableStatus = data;
        //    });
        //餐馆分类
        $http.get($rootScope.rootPath+"/api/restaurant/type")
            .success(function(data) {
                $scope.restaurantType = data;
            });
        //销售
        $http.get($rootScope.rootPath+"/api/admin-user/global?role=CustomerService").success(function (data) {
            $scope.adminUsers = data;
        })
        //
        ////客户等级
        //$http.get($rootScope.rootPath+"/api/restaurant/grades").success(function (data) {
        //    $scope.grades = data;
        //})
        //
        ////销售类型
        //$http.get($rootScope.rootPath+"/api/customerInfo/constants/customerFollowUpStatus").success(function (data) {
        //    $scope.customerFollowUpStatus = data;
        //})
        ////跟进状态
        //$http.get($rootScope.rootPath+"/api/customerInfo/constants/restaurantSellerType").success(function (data) {
        //    $scope.restaurantSellerType = data;
        //})

        //审核状态
        $http.get($rootScope.rootPath+"/api/customerInfo/constants/restaurantReviewStatus").success(function (data) {
            $scope.restaurantReviewStatus = data;
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



        //批量通过， 驳回
        $scope.moreAudit = function( reviewType, reviewStatus){
            var auditReviewIds = $scope.getSelectedCheckbox();
            $scope.toAudit(auditReviewIds , reviewType, reviewStatus, function(data, status, headers, config){
                alert("操作成功");
                $scope.listLoad();
            });
        }

        //单个通过，驳回
        $scope.aAudit= function(auditReview , reviewType, reviewStatus){
            $scope.toAudit([auditReview.id] , reviewType, reviewStatus, function(data, status, headers, config){
                auditReview.id		    =	data[0].id;
                auditReview.operater	=	data[0].operater;
                auditReview.status	    =	data[0].status;
                auditReview.createUser	=	data[0].createUser;
                auditReview.reqType 	=	data[0].reqType;
                auditReview.createTime	=	data[0].createTime;
                auditReview.infoVo	    =	data[0].infoVo;
            });
        }

        //通过  //NOT_CHECK(1,"未审核"), PASS(2,"通过"),OVERRULE(3,"驳回");
        $scope.toAudit=function(auditReviewIds , reviewType, reviewStatus, callback){
            $http({
                method: "GET",
                url: $rootScope.rootPath + "/api/customerInfo/auditReq",
                params: {auditReviewId:auditReviewIds, reviewType: reviewType, reviewStatus: reviewStatus}
            }).success( callback )
                .error(function (data, status, headers, config) {
                    if(data.msg!=null){
                        alert(data.msg);
                    }else{
                        alert("审核操作失败");
                    }
                });
        };

        //检查是否可以批量审核
        $scope.canAudit=function(){
            //$scope.checkboxSelect
            //checkboxSelect = { val: [] };

            if($scope.checkboxSelect.val.length==0){
                return false;
            }
            for( var i=0;i<$scope.checkboxSelect.val.length ; i++ ){
                var selectVal = $scope.checkboxSelect.val[i];
                if(selectVal.status.val!=$scope.undefinedStatus){
                    return false;
                };
            }
            return true;
        }

        console.log($scope.searchForm);

        $scope.listLoad=function() {
            $http({
                method: "GET",
                url: $rootScope.rootPath + "/api/customerInfo/audit/list",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                    console.log("--------");
                    console.log($scope.searchForm);

                    $scope.infoVoList = data.content;
                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;

                    $scope.checkboxSelect.val.length=0;
                    console.log(data);
                })
                .error(function (data, status, headers, config) {
                    window.alert("列表信息加载失败");
                });
        };

        /******复选框*****/
        $scope.checkboxSelect = { val: [] };
        $scope.checkAllSelect=false;
        //全选
        $scope.$watch("checkAllSelect", function(newVal, oldVal){
            $scope.checkboxSelect.val.length=0;
            if( newVal ){
                for(var ix =0; ix <  $scope.infoVoList.length;ix++ ){
                    $scope.checkboxSelect.val.push($scope.infoVoList[ix]);
                }
            }
        });


        $scope.getSelectedCheckbox=function(){
            var selectedArray = [];
            for(var c =0;c<$scope.checkboxSelect.val.length;c++){
                //console.log(123);
                console.log($scope.checkboxSelect.val[c].id);
                selectedArray.push($scope.checkboxSelect.val[c].id);
            }
            return selectedArray;
        }
        /******复选框*****/

        $scope.listLoad();

    });
