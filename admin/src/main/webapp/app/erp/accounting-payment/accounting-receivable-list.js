'use strict';

angular.module('sbAdminApp')
    .controller('AccountReceivableListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $window) {

        $scope.type = $stateParams.type;
        $scope.accountReceivableForm = {
            type: $scope.type,
            startOrderDate: $filter('date')(new Date().setDate(new Date().getDate() - 2), 'yyyy-MM-dd'),
            endOrderDate: $filter('date')(new Date().setDate(new Date().getDate() - 1), 'yyyy-MM-dd')
        };
        if ($scope.type == "writeoff") {
            $scope.accountReceivableForm.accountReceivableStatus = 0;
        }
        $scope.trackers = [];
        $scope.page = {itemsPerPage: 100};
        $scope.totalAmount = [0, 0, 0];

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.format = 'yyyy-MM-dd';
        $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.accountReceivableForm.cityId = $scope.cities[0].id;
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
        $scope.$watch('accountReceivableForm.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers(newVal, null);
                $scope.accountReceivableForm.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                        $scope.accountReceivableForm.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.accountReceivableForm.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.accountReceivableForm.depotId = null;
            }
        });
        $scope.getTrackers(null, null);
        $scope.$watch('accountReceivableForm.depotId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.getTrackers($scope.accountReceivableForm.cityId, newVal);
            }
        });
        if ($scope.type == "list") {
            $http({
                url: '/admin/api/accountReceivable/status/list',
                method: "GET",
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivableStatus = data;
            });
        }
        $http({
            url: '/admin/api/accountReceivable/type/list',
            method: "GET",
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.accountReceivableType = data;
        });

        $scope.SearchAccountReceivable = function (page) {
            $scope.accountReceivableForm.accountReceivableIds = [];
            $scope.accountReceivables = [];
            $scope.accountReceivableForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/accounting/receivable/list',
                method: "GET",
                params: $scope.accountReceivableForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivables = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
                $scope.totalAmount = data.amount;
            });

        };

        $scope.pageChanged = function () {
            $scope.SearchAccountReceivable($scope.page.currentPage - 1);
        };

        $scope.isCheckedAll = false;
        $scope.accountReceivableForm.accountReceivableIds = [];
        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.accountReceivableForm.accountReceivableIds = [];
                angular.forEach($scope.accountReceivables, function (value) {
                    $scope.accountReceivableForm.accountReceivableIds.push(value.accountReceivableId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.accountReceivableForm.accountReceivableIds = [];
                $scope.isCheckedAll = false;
            }
        };
        $scope.writeoff = function (accountReceivableId) {
            $scope.accountReceivableForm.accountReceivableIds = [accountReceivableId];
            $scope.batchWriteoff();
        };
        $scope.batchWriteoff = function () {
            if ($scope.accountReceivableForm.accountReceivableIds.length == 0) {
                alert("请选择应收单");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/accounting/receivable/writeoff",
                method: "PUT",
                data: $scope.accountReceivableForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("核销成功...");
                $scope.submitting = false;
                $scope.SearchAccountReceivable();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "核销失败...");
                $scope.submitting = false;
            });
        };

        $scope.accountReceivableForm.pageSize = $scope.page.itemsPerPage;
        $scope.SearchAccountReceivable();

        $scope.export = function () {
            var str = [];
            for (var p in $scope.accountReceivableForm) {
                if ($scope.accountReceivableForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.accountReceivableForm[p]));
                }
            }
            $window.open("/admin/api/accounting/receivable/export?" + str.join("&"));
        };
    });