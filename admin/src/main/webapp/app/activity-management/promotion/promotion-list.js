/**
 * Created by challenge on 15/10/26.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('PromotionListCtrl', function($scope, $http,$stateParams,$location,$filter,$rootScope) {

        $scope.page = {
            itemsPerPage: 20
        };

        $scope.searchForm = {pageSize : $scope.page.itemsPerPage};

        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        //if($rootScope.user) {
        //    var data = $rootScope.user;
        //    $scope.cities = data.cities;
        //    if ($scope.cities && $scope.cities.length == 1) {
        //        $scope.searchForm.cityId = $scope.cities[0].id;
        //    }
        //}

        //$http.get("/admin/api/promotion")

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
            });

        $http({
            url: "/admin/api/promotion",
            method: "GET",
            params: $scope.searchForm
        }).success(function(data){
                $scope.promotions = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            })
            .error(function(data){
                alert("加载失败...");
            });

        $scope.pageChanged = function() {
            $scope.searchForm.page = $scope.page.currentPage - 1;

            $location.search($scope.searchForm);
        }

        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        }
    });