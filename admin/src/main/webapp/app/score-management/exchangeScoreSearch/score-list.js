/**
 * Created by king-ck on 2015/11/13.
 */
'use strict';
angular.module('sbAdminApp').controller('exchangeScoreSearchList',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {
        $scope.exchangeScoreSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            adminUserId: $stateParams.adminUserId!=null?parseInt($stateParams.adminUserId):null,
            warehouseId: $stateParams.warehouseId
        };

        //alert($stateParams.adminUserId);
        //alert($scope.exchangeScoreSearchForm.adminUserId);

        $scope.page={};

        $scope.$watch("scoreLogsBefore",function(newVal,oldVal){

            if(newVal){
                var scoreLogsAfter=[];
                console.log(newVal)
                for(var i=0;i<newVal.length;i++){
                    for(var r=0;r<newVal[i].customer.restaurant.length;r++){
                        //console.info(newVal[i].customer.restaurant[r]);
                        scoreLogsAfter.push({
                            restaurantId: newVal[i].customer.restaurant[r].id,
                            restaurantName: newVal[i].customer.restaurant[r].name,
                            createTime: newVal[i].createTime,
                            remark: newVal[i].remark,
                            integral: newVal[i].integral,
                            count:newVal[i].count,
                            customerId: newVal[i].customer.id,
                            oldScore:newVal[i]
                        });
                    }
                }
                $scope.scoreLogsAfter=scoreLogsAfter;

                console.log($scope.scoreLogsAfter);
            }
        });

        $scope.initLoad=function(){
            console.log($rootScope.user);

            if($rootScope.user) {
                var data = $rootScope.user;
                $scope.cities = data.cities;
                if ($scope.cities && $scope.cities.length == 1) {
                    $scope.exchangeScoreSearchForm.cityId = $scope.cities[0].id;
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
            $scope.exchangeScoreSearchForm =angular.extend($scope.getDefaultSearchForm(),$scope.exchangeScoreSearchForm );
            $http({
                url: '/admin/api/scoreLog/query',
                method: "GET",
                params:$scope.exchangeScoreSearchForm
            }).success(function (data, status, headers, config) {

                console.log(data)
                $scope.scoreLogsBefore = data.scoreLogs;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });

        }

        $scope.$watch('exchangeScoreSearchForm.cityId',function(newVal,oldVal){
            if(newVal!=null){
                //加载市场
                console.info(newVal);
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.availableWarehouses = data;
                    if ($scope.availableWarehouses && $scope.availableWarehouses.length == 1) {
                        $scope.exchangeScoreSearchForm.warehouseId = $scope.availableWarehouses[0].id;
                    }else{
                        if( $scope.exchangeScoreSearchForm.warehouseId!=null){
                            for(var i=0;i<data.length;i++){
                                if(data[i].id==$scope.exchangeScoreSearchForm.warehouseId){
                                    return;
                                }
                            }
                            $scope.exchangeScoreSearchForm.warehouseId=null;
                        }
                    }
                });
            }else{
                $scope.exchangeScoreSearchForm.warehouseId = null;
            }
        });

        $scope.getDefaultSearchForm=function(){
            return {
                cityId : null,
                restaurantName : null,
                warehouseId : null,
                restaurantId : null,
                adminUserId : null,
                status : null,
                grade : null,
                scoreLogStatus: 3,
                page : 0,
                pageSize : 100
            }; //搜索表单的参数
        }

        $scope.pageChanged = function() {
            $scope.exchangeScoreSearchForm.page = $scope.page.currentPage - 1;
            $scope.exchangeScoreSearchForm.pageSize = $scope.page.itemsPerPage;

            $location.search($scope.exchangeScoreSearchForm);
        }

        $scope.initSearchForm=function(){
            $scope.exchangeScoreSearchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.exchangeScoreSearchForm);
        }

        $scope.initLoad();
        $scope.searchLoad();

        console.log($scope.exchangeScoreSearchForm);

    });