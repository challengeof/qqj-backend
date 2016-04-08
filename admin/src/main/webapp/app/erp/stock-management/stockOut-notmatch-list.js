'use strict';

angular.module('sbAdminApp')
    .controller('StockOutNotMatchListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window, $state) {

        $scope.stockOutForm = {
            stockOutType: 1,
            stockOutStatus: 0,
            stockOutItemStatus: 0
        };
        $scope.trackers = [];
        $scope.page = {itemsPerPage : 100};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.timeOptions = {
            showMeridian:false
        }
        $scope.submitDateFormat = "yyyy-MM-dd HH:mm";

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
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockOutForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockOutForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.stockOutForm.cityId, newVal);
            }
        });

        $scope.search = function (page) {
            $scope.stockOutItems = [];
            $scope.stockOutForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockOutItem/query',
                method: "GET",
                params: $scope.stockOutForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.stockOutItems = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
                alert("加载失败...");
            });
        }

        $scope.stockOutForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.excelStockNotMatchExport = function(){
            var str = [];
            for(var p in $scope.stockOutForm) {
                if($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            $window.open("/admin/api/stockOut/excel-notmatch?" + str.join("&"));
        };

    });