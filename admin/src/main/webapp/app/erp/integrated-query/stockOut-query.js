'use strict';

angular.module('sbAdminApp')
    .controller('StockOutQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutForm.page = parseInt($stateParams.page);
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
                $scope.stockOutForm.cityId = $scope.cities[0].id;
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
        $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockOutForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                    $scope.stockOutForm.sourceDepotId = null;
                    $scope.stockOutForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutForm.depotId = null;
                $scope.sourceDepots = [];
                $scope.stockOutForm.sourceDepotId = null;
                $scope.targetDepots = [];
                $scope.stockOutForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
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
            $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutForm.cityId, newVal);
                }
            });
        }
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.searchStockOut = function () {
            $location.search($scope.stockOutForm);
        };
        $scope.resetForm = function () {
            $scope.stockOutForm = {
                stockOutType: $stateParams.stockOutType
            };
        };
        $scope.pageChanged = function () {
            $scope.stockOutForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/export/list?" + str.join("&"));
        };

    });