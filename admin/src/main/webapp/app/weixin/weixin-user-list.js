'use strict';
angular.module('sbAdminApp')
    .controller('WeixinUserListCtrl', function ($scope, $http, $rootScope, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage: 20};

        $scope.iForm = {pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.iForm.page = parseInt($stateParams.page);
        }

        $http.get("/weixin/user/groups")
            .success(function (data) {
                $scope.groups = data;
            })

        $http.get("/weixin/user/statuses")
            .success(function (data) {
                $scope.statuses = data;
            })

        $http({
            url: "/weixin/user/list",
            method: "GET",
            params: $scope.iForm
        })
        .success(function (data) {
            $scope.items = data.content;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.pageChanged = function() {
            $scope.iForm.page = $scope.page.currentPage - 1;
            $location.search($scope.iForm);
        }

        $scope.search = function () {
            $scope.iForm.page = 0;
            $location.search($scope.iForm);
        }

        $scope.showPic = function (picUrl) {
            $window.open(picUrl);
        }

        $scope.showPic = function (picUrl) {
            $window.open(picUrl);
        }

        $scope.auditWeixinUser = function (id, status, index) {
            $http({
                method: 'post',
                url: '/weixin/user/audit',
                data: {'id':id,'status':status},
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function(data) {
                alert("审核完成!");
                $scope.selectedRow = index;
            }).error(function(data) {
                alert("审核失败!");
            });
        }

    });