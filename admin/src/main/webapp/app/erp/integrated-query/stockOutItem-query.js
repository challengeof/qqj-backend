'use strict';

angular.module('sbAdminApp')
    .controller('StockOutItemQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutItemForm = {
            stockOutType: $scope.stockOutType
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutItemForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.stockOutItemForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.getTrackers = function (cityId, depotId) {
            $http({
                url: '/admin/api/accounting/tracker/list',
                method: "GET",
                params: {roleName: "LogisticsStaff", cityId: cityId, depotId: depotId},
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.trackers = data;
            })
        };
        $scope.$watch('stockOutItemForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutItemForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockOutItemForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutItemForm.depotId = null;
                    $scope.stockOutItemForm.sourceDepotId = null;
                    $scope.stockOutItemForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutItemForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockOutItemForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockOutItemForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutItemForm.cityId', function (newVal, oldVal) {
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
        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutItemForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutItemForm.cityId, newVal);
                }
            });
        }
        $http({
            url: '/admin/api/stockOutItem/query',
            method: "GET",
            params: $scope.stockOutItemForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOutItems = data.content;
            $scope.totalAmount = data.amount;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.searchStockOutItem = function () {
            $location.search($scope.stockOutItemForm);
        };
        $scope.resetForm = function () {
            $scope.stockOutItemForm = {
                stockOutType: $stateParams.stockOutType
            };
        };
        $scope.pageChanged = function () {
            $scope.stockOutItemForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutItemForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockOutItemForm) {
                if ($scope.stockOutItemForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutItemForm[p]));
                }
            }
            $window.open("/admin/api/stockOutItem/export/list?" + str.join("&"));
        };

    });