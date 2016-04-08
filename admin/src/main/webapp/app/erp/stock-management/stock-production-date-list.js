'use strict';

angular.module('sbAdminApp')
    .controller('StockProductionDateListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window, $state) {

        $scope.page = {itemsPerPage : 30};
        $scope.stockSearchForm = {
            status: 0,
            productionDate: "noDate",
            sortField: "skuId",
            asc:true
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockSearchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockSearchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockSearchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockSearchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockSearchForm.depotId = null;
            }
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });
        $scope.isCheckedAll = false;
        $scope.stockSearchForm.selectStocks = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.stockSearchForm.selectStocks = [];
                angular.forEach($scope.stocks, function(value, key){
                    $scope.stockSearchForm.selectStocks.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.stockSearchForm.selectStocks = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.stocks = [];
            $scope.stockSearchForm.selectStocks = [];
            $scope.stockSearchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockShelf/list',
                method: "GET",
                params: $scope.stockSearchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stocks = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.stockSearchForm.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.batchProductionDate = function() {

            if ($scope.stockSearchForm.selectStocks.length == 0) {
                alert('请选择要录入生产日期的商品');
                return;
            }

            $state.go('oam.batch-production-date', {
                stocks: $scope.stockSearchForm.selectStocks
            });
        };
    });