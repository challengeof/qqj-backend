'use strict';

angular.module('sbAdminApp')
    .controller('StockInQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.stockInForm = {};
        $scope.stockInForm.saleReturn = $stateParams.saleReturn;
        if ($scope.stockInForm.saleReturn == 1) {
            $scope.stockInForm.stockInType = 2;
        }
        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockInForm.page = parseInt($stateParams.page);
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
               $scope.stockInForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('stockInForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.stockInForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.stockInForm.depotId = null;
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
            if ($scope.stockInForm.saleReturn == 1) {
                return t.value == 2;
            } else {
                return t.value != 2;
            }
        }
        $http.get("/admin/api/stockIn/status/list").success(function (data) {
            $scope.status = data;
        });
        $scope.$watch('stockInForm.stockInType', function (newVal, oldVal) {
            if (newVal == 2) {
                $http.get("/admin/api/stockIn/sellReturnType/list").success(function (data) {
                    $scope.returnType = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockInForm.sellReturnType = null;
                }
            } else {
                $scope.returnType = [];
                $scope.stockInForm.sellReturnType = null;
            }
        });
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
        });

        $scope.searchStockIn = function () {
            $location.search($scope.stockInForm);
        };
        $scope.resetForm = function () {
            $scope.stockInForm = {
                saleReturn:$scope.stockInForm.saleReturn
            };
            if ($scope.stockInForm.saleReturn == 1) {
                $scope.stockInForm.stockInType = 2;
            }
        };
        $scope.pageChanged = function () {
            $scope.stockInForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockInForm);
        };
        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.stockInForm) {
                if ($scope.stockInForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockInForm[p]));
                }
            }
            $window.open("/admin/api/stockIn/export/list?" + str.join("&"));
        };

    });