'use strict';

angular.module('sbAdminApp')
    .controller('StockInListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.stockInType = $stateParams.stockInType;
        $scope.stockInForm = {
            stockInType: $scope.stockInType,
            stockInStatus: 0
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInForm.page = parseInt($stateParams.page);
        }

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockInForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    /*if ($scope.sourceDepots && $scope.sourceDepots.length == 1) {
                     $scope.stockInForm.sourceDepotId = $scope.sourceDepots[0].id;
                     }
                     if ($scope.targetDepots && $scope.targetDepots.length == 1) {
                     $scope.stockInForm.targetDepotId = $scope.targetDepots[0].id;
                     }*/
                });
            } else {
                $scope.depots = [];
                $scope.stockInForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockInForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockInForm.targetDepotId = null;
            }
        });
        if ($scope.stockInType == 1) {
            $http.get("/admin/api/purchase/order/types").success(function (data) {
                $scope.purchaseOrderTypes = data;
            });
            $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
                if (newVal) {
                    $http({
                        url: "/admin/api/vendor",
                        method: 'GET',
                        params: {cityId: newVal}
                    }).success(function (data) {
                        $scope.vendors = data.vendors;
                    });
                } else {
                    $scope.vendors = [];
                }
            });
        }

        $http({
            url: '/admin/api/stockIn/query',
            method: "GET",
            params: $scope.stockInForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockIns = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockInForm);
        };
        $scope.pageChanged = function () {
            $scope.stockInForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInForm);
        }

    });