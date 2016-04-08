'use strict';

angular.module('sbAdminApp')
    .controller('AccountReceivableWriteoffCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $window) {

        $scope.type = $stateParams.type;
        $scope.accountReceivableForm = {
            type: $scope.type,
            startWriteoffDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
            endWriteoffDate: $filter('date')(new Date(), 'yyyy-MM-dd')
        };
        if ($scope.type == "cancel") {
            $scope.accountReceivableForm.accountReceivableWriteoffStatus = 1;
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
                url: '/admin/api/accountReceivableWriteoff/status/list',
                method: "GET",
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.accountReceivableWriteoffStatus = data;
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
            $scope.accountReceivables = [];
            $scope.accountReceivableForm.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/accounting/receivableWriteoff/list',
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

        $scope.writeoffCancel = function (accountReceivableWriteoffId, cancelDate) {
            if (cancelDate == null) {
                alert("请输入核销取消时间!");
                return;
            }
            if (window.confirm("确认取消核销?") == true) {
                $scope.submitting = true;
                $scope.accountReceivableForm.accountReceivableWriteoffId = accountReceivableWriteoffId;
                $scope.accountReceivableForm.cancelDate = cancelDate;
                $http({
                    url: "/admin/api/accounting/receivableWriteoff/cancel",
                    method: "PUT",
                    data: $scope.accountReceivableForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("核销取消成功...");
                    $scope.submitting = false;
                    $scope.SearchAccountReceivable();
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "核销取消失败...");
                    $scope.submitting = false;
                });
            }
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
            $window.open("/admin/api/accounting/receivableWriteoff/export?" + str.join("&"));
        };
    });