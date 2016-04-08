'use strict';
angular.module('sbAdminApp')
    .controller('spikeListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {
        $scope.page = {};
        $scope.searchForm = {
            pageSize: 20,
            page: $stateParams.page!=null ? parseInt($stateParams.page):0
        };

        $scope.showActiveState=function(state){
            if($scope.activeStates==null){
                return;
            }
            for(var i=0;i<$scope.activeStates.length ; i++){
                if($scope.activeStates[i].val == state){
                    return $scope.activeStates[i];
                }
            }
        }


        $http({
            method: 'GET',
            url: '/admin/api/spike/query',
            params: $scope.searchForm
        }).success(function (data, status, headers, config) {
            $scope.listData = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data, status, headers, config) {
            alert("查询失败");
        })

        $http({
            method: 'GET',
            url: '/admin/api/spike/activeState/query'
        }).success(function (data, status, headers, config) {
            $scope.activeStates = data;


        }).error(function (data, status, headers, config) {
            alert("查询失败");
        })

        //设置状态
        $scope.stateSet=function(index,line,state){
            if(!confirm("确认失效吗？秒杀id:"+ line.id )){
                return ;
            }
            $http({
                method: 'POST',
                url: '/admin/api/spike/state/change',
                params: { id:line.id, state:state }
            }).success(function (data, status, headers, config) {
                $scope.listData[index]=data;
            }).error(function (data, status, headers, config) {
                alert("操作失败");
            })
        }
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
});