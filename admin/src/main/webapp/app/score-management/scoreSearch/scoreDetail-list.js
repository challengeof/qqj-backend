'use strict';

angular.module('sbAdminApp')
    .controller('scoreDetailSearchList', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window){
        console.log("controller-scoreDetailSearchList");

        $scope.getDefaultSearchForm=function(){
            return {
                customerId: $stateParams.customerId,
                restaurantId: $stateParams.restaurantId,
                scoreLogStatus: 1,
                page: $stateParams.page==null?0:$stateParams.page,
                pageSize: $stateParams.pageSize==null?100:$stateParams.pageSize
            }; //搜索表单的参数
        }

        $scope.searchForm=angular.extend($scope.getDefaultSearchForm(),{
            orderBeginDate: $stateParams.orderBeginDate,
            orderEndDate:   $stateParams.orderEndDate
        });

        $scope.page={};

        $scope.submitDateFormat="yyyy-MM-dd";
        $scope.dateInput={
            format:'yyyy-MM-dd',
            beginDateOptions : false,
            endDateOptions:false,

            beginDateOpen : function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dateInput.beginDateOptions = true;
                $scope.dateInput.endDateOptions=false;
            },
            endDateOpen : function ($event) {
                $event.preventDefault();
                $event.stopPropagation();
                $scope.dateInput.beginDateOptions = false;
                $scope.dateInput.endDateOptions=true;
            },
            dateOptions : {
                dateFormat : 'yyyy-MM-dd',
                startingDay : 1
            }
        };

        $scope.searchLoad=function(){
            $http({
                url: '/admin/api/scoreLog/query',
                method: "GET",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                //console.log(data)
                $scope.scoreLogs = data.scoreLogs;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });

        }

        //合计查询
        $scope.sumLoad=function(){
            $http({
                url: '/admin/api/score/order/sum',
                method: "GET",
                params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                //console.log(data)

                $scope.scoreSumInfo = data;

            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.initSearchForm=function(){
            $scope.searchForm=$scope.getDefaultSearchForm();
        }

        $scope.formSubmit=function(){
            $location.search($scope.searchForm);
        }

        $scope.sumLoad();
        $scope.searchLoad();


    });