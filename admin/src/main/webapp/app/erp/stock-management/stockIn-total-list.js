'use strict';

angular.module('sbAdminApp')
    .controller('StockInTotalListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window, $state) {

        $scope.stockInTotalForm = {};

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInTotalForm.page = parseInt($stateParams.page);
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
                $scope.stockInTotalForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('stockInTotalForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.stockInTotalForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInTotalForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInTotalForm.depotId = null;
            }

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

        $http.get("/admin/api/stockIn/type/list").success(function (data) {
            $scope.type = data;
        });
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $http.get("/admin/api/purchase/order/types").success(function (data) {
            $scope.pType = data;
        });
        $http.get("/admin/api/sellReturn/type/list").success(function (data) {
            $scope.rType = data;
        });
        $http.get("/admin/api/stockPrint/status/list").success(function (data) {
            $scope.printStatus = data;
        });

        $scope.$watch('stockInTotalForm.stockInType', function (type) {
            if (type != null && type == 1) {
                $scope.isPurchase = true;
            } else {
                $scope.isPurchase = false;
                $scope.stockInTotalForm.purchaseOrderType = null;
            }
            if (type != null && type == 2) {
                $scope.isReturn = true;
            } else {
                $scope.isReturn = false;
                $scope.stockInTotalForm.sellReturnType = null;
            }
        });

        $http({
            url: '/admin/api/stockIn/query',
            method: "GET",
            params: $scope.stockInTotalForm,
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
            $location.search($scope.stockInTotalForm);
        };
        $scope.pageChanged = function () {
            $scope.stockInTotalForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInTotalForm);
        };

        $scope.isCheckedAll = false;
        $scope.stockInTotalForm.stockInIds = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.stockInTotalForm.stockInIds = [];
                angular.forEach($scope.stockIns, function (value, key) {
                    $scope.stockInTotalForm.stockInIds.push(value.stockInId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.stockInTotalForm.stockInIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.batchPrint = function () {
            if ($scope.stockInTotalForm.stockInIds.length == 0) {
                alert("请选择入库单");
                return;
            }
            var win = $window.open("/admin/api/stockIn/export/bills?stockInIds=" + $scope.stockInTotalForm.stockInIds);
            win.onunload = function () {
                $state.go($state.current, $scope.stockInTotalForm, {reload: true});
            }
        };
    });