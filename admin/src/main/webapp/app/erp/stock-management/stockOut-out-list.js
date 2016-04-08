'use strict';

angular.module('sbAdminApp')
    .controller('StockOutOutListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window) {

        $scope.formData = {
            stockOutType: 1,
            stockOutStatus: 2,
            startReceiveDate: $filter('date')(new Date(new Date().getFullYear(), new Date().getMonth(), 1), 'yyyy-MM-dd 00:00'),
            endReceiveDate: $filter('date')(new Date().setDate(new Date().getDate() + 1), 'yyyy-MM-dd 00:00')
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage: 100}
        $scope.totalAmount = [0, 0, 0];

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.formData.cityId = $scope.cities[0].id;
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

        $scope.openStartOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartOrderDate = true;
        };
        $scope.openEndOrderDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndOrderDate = true;
        };
        $scope.openStartReceiveDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStartReceiveDate = true;
        };
        $scope.openEndReceiveDate = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEndReceiveDate = true;
        };
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.formData.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.getTrackers(null, null);
        $scope.$watch('formData.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.formData.cityId, newVal);
            }
        });

        $scope.SearchStockOutOrders = function (page) {
            $scope.stockOuts = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOut/query',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOuts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount;
            }).error(function (data) {
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.SearchStockOutOrders();

        $scope.pageChanged = function () {
            $scope.SearchStockOutOrders($scope.page.currentPage - 1);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/stockOut/out/export?" + str.join("&"));
        };
        $scope.exportIncomeDailyReport = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/incomeDailyReport/export?" + str.join("&"));
        };
    });