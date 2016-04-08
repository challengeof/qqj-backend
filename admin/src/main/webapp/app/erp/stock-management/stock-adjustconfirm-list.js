'use strict';

angular.module('sbAdminApp')
    .controller('StockAdjustConfirmListCtrl', function ($scope, $rootScope, $http, $stateParams, $location, $window) {

        $scope.page = {itemsPerPage : 100};
        $scope.searchForm = {
            status: 0
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

        $http.get("/admin/api/category")
        .success(function (data, status, headers, config) {
            $scope.categories = data;
        });

        $scope.isCheckedAll = false;
        $scope.formData.adjustIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.adjustIds = [];
                angular.forEach($scope.stockAdjusts, function(value, key){
                    $scope.formData.adjustIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.adjustIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.search = function (page) {
            $scope.stockAdjusts = [];
            $scope.formData.adjustIds = [];
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

        $scope.batchConfirm = function () {
            if ($scope.formData.adjustIds.length == 0) {
                alert("请选择调整单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/confirm",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("审核成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "审核失败...");
                $scope.submitting = false;
            });
        };

        $scope.batchReject = function () {
            if ($scope.formData.adjustIds.length == 0) {
                alert("请选择调整单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/stockAdjust/reject",
                method: "POST",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("审核成功...");
                $scope.submitting = false;
                $scope.search();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "审核失败...");
                $scope.submitting = false;
            });
        };
    });