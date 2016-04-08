'use strict';

angular.module('sbAdminApp')
    .controller('StockInItemQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockInItemForm = {};
        $scope.stockInItemForm.saleReturn = $stateParams.saleReturn;
        if ($scope.stockInItemForm.saleReturn == 1) {
            $scope.stockInItemForm.stockInType = 2;
        }
        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInItemForm.page = parseInt($stateParams.page);
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
               $scope.stockInItemForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('stockInItemForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockInItemForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInItemForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInItemForm.depotId = null;
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
        $scope.filterSellReturn = function(t) {
            if ($scope.stockInItemForm.saleReturn == 1) {
                return t.value == 2;
            } else {
                return t.value != 2;
            }
        }
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $scope.$watch('stockInItemForm.stockInType', function (newVal, oldVal) {
            if (newVal == 2) {
                $http.get("/admin/api/stockIn/sellReturnType/list").success(function (data) {
                    $scope.returnType = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInItemForm.sellReturnType = null;
                }
            } else {
                $scope.returnType = [];
                $scope.stockInItemForm.sellReturnType = null;
            }
        });
        $http({
            url: '/admin/api/stockInItem/query',
            method: "GET",
            params: $scope.stockInItemForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockInItems = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.totalCost = data.amount[0];
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.searchStockInItem = function () {
            $location.search($scope.stockInItemForm);
        };
        $scope.resetForm = function () {
            $scope.stockInItemForm = {
                saleReturn:$scope.stockInItemForm.saleReturn
            };
            if ($scope.stockInItemForm.saleReturn == 1) {
                $scope.stockInItemForm.stockInType = 2;
            }
        };
        $scope.pageChanged = function () {
            $scope.stockInItemForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInItemForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockInItemForm) {
                if ($scope.stockInItemForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockInItemForm[p]));
                }
            }
            $window.open("/admin/api/stockInItem/export/list?" + str.join("&"));
        };

    });