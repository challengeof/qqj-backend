'use strict';

angular.module('sbAdminApp')
    .controller('StockOutTotalExportCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockOutTotalForm = {};

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutTotalForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.stockOutTotalForm.cityId = $scope.cities[0].id;
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
        $scope.$watch('stockOutTotalForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.stockOutTotalForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockOutTotalForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutTotalForm.depotId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.stockOutTotalForm.cityId, newVal);
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/depot/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.stockOutTotalForm.warehouseId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.warehouseId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/block/warehouse/" + newVal + "").success(function (data) {
                    $scope.blocks = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutTotalForm.blockId = null;
                }
            } else {
                $scope.blocks = [];
                $scope.stockOutTotalForm.blockId = null;
            }
        });
        $scope.$watch('stockOutTotalForm.stockOutType', function (type) {
            if (type != null && type == 1) {
                $scope.isOrder = true;
                $scope.getTrackers(null, null);
            } else {
                $scope.isOrder = false;
                $scope.stockOutTotalForm.warehouseId = null;
                $scope.stockOutTotalForm.blockId = null;
                $scope.stockOutTotalForm.trackerId = null;
                $scope.stockOutTotalForm.startOrderDate = null;
                $scope.stockOutTotalForm.endOrderDate = null;
            }
        });
        $http.get("/admin/api/stockOut/type/list").success(function (data) {
            $scope.type = data;
        });
        $http.get("/admin/api/stockOut/status/list").success(function (data) {
            $scope.status = data;
        });
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutTotalForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockOutTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockOutTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutTotalForm);
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.stockOutTotalForm) {
                if ($scope.stockOutTotalForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutTotalForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/total/export?" + str.join("&"));
        };

    });
