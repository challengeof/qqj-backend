'use strict';

angular.module('sbAdminApp')
    .controller('SaleVisitListCtrl', function ($scope, $rootScope, $http, $stateParams, $location,$window) {

        $scope.searchForm = {
            page:$stateParams.page,
            pageSize:$stateParams.pageSize,
            restaurantId: $stateParams.restaurantId,
            restaurantName: $stateParams.restaurantName,
            activeType: $stateParams.activeType,
            visitId: $stateParams.visitId,
            visitStage: $stateParams.visitStage,
            visitPurpose: $stateParams.visitPurpose,
            startVisitTime: $stateParams.startVisitTime,
            endVisitTime: $stateParams.endVisitTime,
            sellerName: $stateParams.sellerName

        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.list = function(){
            $http({
                url: $rootScope.rootPath + "/api/saleVisit/list",
                method: 'GET',
                params: $scope.searchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.searchForm = data.queryRequest;
                $scope.saleVisits = data.content;
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
                alert("加载失败...");
            });
        }

        $scope.list();

        $scope.delete = function(id){
            $http.delete($rootScope.rootPath + '/api/saleVisit/' + id)
            .success(function(){
                $scope.list();
            }).error(function(){
                alert("删除失败...");
            })
        }

        $scope.clear = function(){
            $scope.searchForm.restaurantId = "";
            $scope.searchForm.restaurantName = "";
            $scope.searchForm.activeType = "";
            $scope.searchForm.visitId = "";
            $scope.searchForm.visitStage = "";
            $scope.searchForm.visitPurpose = "";
            $scope.searchForm.startVisitTime = "";
            $scope.searchForm.endVisitTime = "";
            $scope.searchForm.sellerName = "";
        }

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;
            $location.search($scope.searchForm);
        };

        $http.get($rootScope.rootPath + "/api/admin-user")
            .success(function (data, status, headers, config) {
                $scope.visitors = data.adminUsers;
            });

        $http.get($rootScope.rootPath + "/api/saleVisit/stage/list")
            .success(function(data, status, headers, config){
                $scope.visitStages = data;
            });

        $http.get($rootScope.rootPath + "/api/saleVisit/activeType/list")
            .success(function(data, status, headers, config){
                $scope.activeTypes = data;
            });

        $http.get($rootScope.rootPath + "/api/saleVisit/purpose/list")
            .success(function(data, status, headers, config){
                $scope.visitPurposes = data;
            });

        $scope.submitDateFormat = "yyyy-MM-dd";
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm:ss',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        };
        $scope.isVisitTimeOpen = false;
        $scope.isNextVisitTimeOpen = false;
        $scope.visitTimeOpen = function (e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isVisitTimeOpen = true;
        };
        $scope.nextVisitTimeOpen = function (e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isNextVisitTimeOpen = true;
        };


        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open($rootScope.rootPath+"/api/saleVisit/export/excel?" + str.join("&"));
        };
    });