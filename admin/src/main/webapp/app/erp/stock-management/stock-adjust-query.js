'use strict';

angular.module('sbAdminApp')
    .controller('StockAdjustQueryCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.searchForm = {};

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.formData = {};
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.searchForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/stockAdjust/status/list")
        .success(function (data) {
            $scope.status = data;
        });

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.search = function (page) {
            $scope.stockAdjusts = [];
            $scope.searchForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/stockAdjust/query',
                method: "GET",
                params: $scope.searchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.stockAdjusts = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status) {
                window.alert("加载失败...");
            });
        }

        $scope.searchForm.pageSize = $scope.page.itemsPerPage;
        $scope.search();

        $scope.pageChanged = function () {
            $scope.search($scope.page.currentPage - 1);
        }

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/stockAdjust/export/list?" + str.join("&"));
        };

        $scope.cancel = function (id) {
            $scope.formData.adjustIds = [];
            $scope.formData.adjustIds.push(id);
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/cancel",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("作废成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "作废失败...");
                $scope.submitting = false;
            });
        };
    });