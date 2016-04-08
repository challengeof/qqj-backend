'use strict';
angular.module('sbAdminApp')
    .controller('MyTickteCtrl', function ($rootScope,$scope,$http,$state,$location,$stateParams) {

        //每页条目数
        $scope.formData = {
            pageSize: 10
        };
        $scope.page = {};

        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.pageChanged = function () {
            $scope.formData.page = $scope.page.currentPage - 1;
            $scope.formData.pageSize = $scope.page.itemsPerPage;
            $scope.submit();
        }

        //搜索
        $scope.submit=function() {
            //工单列表
            $http({
                url: $rootScope.rootPath+"/api/work-ticket/list",
                method: 'GET',
                params:$scope.formData
            }).success(function(data) {
                $scope.workTickes = data.content;
                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })

        }

        $scope.submit();
    });
