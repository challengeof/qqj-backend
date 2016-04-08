'use strict';
angular.module('sbAdminApp')
    .controller('TicketsListCtrl', function ($rootScope,$scope,$http,$state,$location,$stateParams) {

        $scope.tabs = [
            {title : '我的工单' ,content : 'crm.ticketsList.my-tickets'},
            {title : '我的任务' ,content : 'crm.ticketsList.my-history'},
            {title : '已完成' ,content : 'crm.ticketsList.my-stocks'}
        ];

        var titleFlag = '';
        var flag = '';
        $scope.tabClick = function(title,$event){
            console.log($event.currentTarget.className);
            flag = $event;
            titleFlag = title;
            if (titleFlag == '我的工单' || titleFlag == ''){
                $state.go('crm.ticketsList.my-tickets');
            }else if (titleFlag == '我的任务'){
                $state.go('crm.ticketsList.my-history');
            }else {
                $state.go('crm.ticketsList.my-stocks');
            }

        }

        $state.go('crm.ticketsList.my-tickets');

        //来源
        $scope.pSources=[];
        $http({
            url: $rootScope.rootPath+"/api/work-ticket/problemSources",
            method: 'GET'
        }).success(function(data) {
            $scope.pSources = data;
            console.log(data);
        })

        //流程
        $scope.processes=[];
        $http({
            url: $rootScope.rootPath+"/api/work-ticket/process",
            method: 'GET'
        }).success(function(data) {
            $scope.processes = data;
            console.log(data);
        })

        //状态
        $scope.stateTypes = [
            {'name':'等待处理','type':0},
            {'name':'已回复','type':1},
            {'name':'完成','type':2}
        ];

        //待跟进人
        $http.get("/crm/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.newAdminUsers = data;
            });

        $scope.resetCandidateRestaurants = function () {
            $scope.candidateRestaurants = [];
        }

        $scope.searchRestaurant = function(restaurant) {
            $scope.candidateRestaurants = [];
            if(restaurant.restaurantId != undefined){
                $http.get("/crm/api/restaurant/" + restaurant.restaurantId)
                    .success(function (data) {
                        if (!data) {
                            alert('餐馆不存在或已失效');
                            restaurant.restaurantId = '';
                            return;
                        }
                        $scope.candidateRestaurants.push(data);
                        $scope.formData.restaurantId = data.id;
                    }).error(function (data) {
                    alert('餐馆不存在或已失效');
                    $scope.formData.restaurantId = '';
                    return;
                });
            }else{
                $scope.formData.restaurantId = '';
                return;
            }
        }

        //输入餐馆名称
        $scope.funcAsyncRestaurant = function (name) {
            if (name && name !== "") {
                $scope.candidateRestaurants = [];
                $http({
                    url: "/crm/api/restaurant/candidates",
                    method: 'GET',
                    params: {page: 0, pageSize: 20, name: name, showLoader:false}
                }).then(
                    function (data) {
                        $scope.candidateRestaurants = data.data;
                    }
                )
            }
        }

        //默认按创建时间降序排序
        $scope.title = 'createTime';
        $scope.desc = 0;
    })
;
