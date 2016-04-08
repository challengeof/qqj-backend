'use strict';

angular.module('sbAdminApp')
.controller('scoreSearchList',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {


        $scope.getDefaultSearchForm=function(){
            return {
                page: $stateParams.page==null?0:$stateParams.page,
                pageSize: $stateParams.pageSize==null?100:$stateParams.pageSize
            };
        }

        $scope.scoreSearchForm = angular.extend($scope.getDefaultSearchForm(),{
            adminUserId:!!$stateParams.adminUserId?parseInt($stateParams.adminUserId):null,
            warehouseId: $stateParams.warehouseId,
            sortField: $stateParams.sortField,
            asc: $stateParams.asc
        });
        $scope.page={};

        $scope.$watch('scoresBefore',function(newVal,oldVal){
            if(newVal){
                var scoresAfter=[];

                for(var i=0;i<newVal.length;i++){
                    for(var r=0;r<newVal[i].customer.restaurant.length;r++){
                        //console.info(newVal[i].customer.restaurant[r]);
                        scoresAfter.push({
                            restaurantId: newVal[i].customer.restaurant[r].id,
                            restaurantName: newVal[i].customer.restaurant[r].name,
                            totalScore: newVal[i].totalScore,               //总积分
                            exchangeScore: newVal[i].exchangeScore,         //已兑换积分
                            availableScore: newVal[i].totalScore- newVal[i].exchangeScore,//剩余积分
                            customerId: newVal[i].customer.id,

                            oldScore:newVal[i]
                        });
                    }
                }
                $scope.scoresAfter=scoresAfter;
            }
        });
        $scope.$watch('scoreSearchForm.cityId',function(newVal,oldVal){
            if(newVal!=null){
                //加载市场
                console.info(newVal);
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.availableWarehouses = data;
                    if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                        $scope.scoreSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                    }else{
                        if( $scope.scoreSearchForm.warehouseId!=null){
                            for(var i=0;i<data.length;i++){
                                if(data[i].id==$scope.scoreSearchForm.warehouseId){
                                    return;
                                }
                            }
                            $scope.scoreSearchForm.warehouseId=null;
                        }
                    }
                });
            }else{
                $scope.scoreSearchForm.warehouseId=null;
            }
        });

        $scope.initLoad=function(){
            console.log($rootScope.user);
            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if ($scope.cities && $scope.cities.length == 1) {
                    $scope.scoreSearchForm.cityId = $scope.cities[0].id;
                }
            }
            //销售信息
            $http({
                method:"GET",
                url:"/admin/api/admin-user/global?role=CustomerService",
            }).success(function(data){
                $scope.adminUsers = data;
            })

            //状态
            $http.get("/admin/api/restaurant/status").success(function (data) {
                $scope.availableStatus = data;
            })

            //等级
            $http.get("/admin/api/restaurant/grades").success(function (data) {
                $scope.grades = data;
            })

        }

        $scope.searchLoad=function(){

            $scope.scoreSearchForm =angular.extend($scope.getDefaultSearchForm(), $scope.scoreSearchForm )
            $http({
                url: '/admin/api/score/query',
                method: "GET",
                params: $scope.scoreSearchForm
            }).success(function (data, status, headers, config) {

                console.log(data);
                $scope.scoresBefore = data.scores;
                $scope.count = data.total;
                console.log($scope.scores);

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }
        $scope.pageChanged = function() {
            $scope.scoreSearchForm.page = $scope.page.currentPage - 1;
            $scope.scoreSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.scoreSearchForm);
        }

        $scope.sort = function(field) {

            if(field && field == $scope.scoreSearchForm.sortField) {

                $scope.scoreSearchForm.asc = $scope.scoreSearchForm.asc=="false";

            } else {
                $scope.scoreSearchForm.sortField = field;
                $scope.scoreSearchForm.asc = false;
            }

            $scope.scoreSearchForm.page = 0;



            $location.search($scope.scoreSearchForm);
        }

        $scope.initSearchForm=function(){
            $scope.scoreSearchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.scoreSearchForm);
        }

        $scope.initLoad();
        $scope.searchLoad();

        console.log($scope.scoreSearchForm);

});