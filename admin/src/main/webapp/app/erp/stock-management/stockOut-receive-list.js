'use strict';

angular.module('sbAdminApp')
    .controller('StockOutReceiveListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $state, $window) {

        $scope.formData = {
            stockOutType: 1,
            stockOutStatus:1
        };
        $scope.trackers = [];

        $scope.page = {itemsPerPage : 100}
        $scope.totalAmount = 0;

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

        $scope.isCheckedAll = false;

        $scope.formData.selectStockOuts = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.selectStockOuts = [];
                angular.forEach($scope.stockOuts, function(value, key){
                    $scope.formData.selectStockOuts.push(value);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.selectStockOuts = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.SearchStockOutOrders = function (page) {
            $scope.stockOuts = [];
            $scope.formData.selectStockOuts = [];
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
                $scope.totalAmount = data.amount[0];
            }).error(function (data) {
            });
        }

        $scope.batchReceive = function() {

            if ($scope.formData.selectStockOuts.length == 0) {
                alert('请选择出库单');
                return;
            }
            var cityId = null;
            var pass = true;
            angular.forEach($scope.formData.selectStockOuts, function(value, key){
                if (pass && cityId != null && value.cityId != cityId) {
                    alert('请选择同一城市的出库单');
                    pass = false;
                }
                if (pass) {
                    cityId = value.cityId;
                }
            });
            if (!pass) {
                return;
            }

            $state.go('oam.stockOut-all-receive', {
                stockOuts: $scope.formData.selectStockOuts
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.SearchStockOutOrders();

        $scope.pageChanged = function () {
            $scope.SearchStockOutOrders($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.formData) {
                if ($scope.formData[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.formData[p]));
                }
            }
            $window.open("/admin/api/stockOut/receive/export?" + str.join("&"));
        };

    });