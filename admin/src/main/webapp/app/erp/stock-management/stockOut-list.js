'use strict';

angular.module('sbAdminApp')
    .controller('StockOutListCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location, $window, $state, $timeout) {

        $scope.stockOutType = $stateParams.stockOutType;
        $scope.stockOutForm = {
            stockOutType: $scope.stockOutType,
            stockOutStatus: 0
        };

        $scope.page = {};
        if ($stateParams.page) {
            $scope.stockOutForm.page = parseInt($stateParams.page);
        }

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
                    $scope.sourceDepots = data;
                    $scope.targetDepots = data;
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.stockOutForm.depotId = null;
                    $scope.stockOutForm.sourceDepotId = null;
                    $scope.stockOutForm.targetDepotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.sourceDepots = [];
                $scope.targetDepots = [];
                $scope.stockOutForm.depotId = null;
                $scope.stockOutForm.sourceDepotId = null;
                $scope.stockOutForm.targetDepotId = null;
            }
        });
        if ($scope.stockOutType == 1) {
            $scope.getTrackers(null, null);
            $scope.$watch('stockOutForm.depotId', function (newVal, oldVal) {
                if (typeof  newVal != "undefined") {
                    $scope.getTrackers($scope.stockOutForm.cityId, newVal);
                }
            });
        }
        if ($scope.stockOutType == 3) {
            $scope.$watch('stockOutForm.cityId', function (newVal, oldVal) {
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
        }

        $http.get("/admin/api/stockPrint/status/list").success(function (data) {
            $scope.printStatus = data;
        });
        $http({
            url: '/admin/api/stockOut/query',
            method: "GET",
            params: $scope.stockOutForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.stockOuts = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
            $scope.totalAmount = data.amount;
        }).error(function (data) {
            alert("加载失败...");
        });

        $scope.search = function () {
            $location.search($scope.stockOutForm);
            $scope.batchForm.stockOutIds = [];
        };
        $scope.pageChanged = function () {
            $scope.stockOutForm.page = $scope.page.currentPage - 1;
            $location.search($scope.stockOutForm);
            $scope.batchForm.stockOutIds = [];
        }

        $scope.excelSkuExport = function () {

            if ($scope.stockOutType != 2 && $scope.stockOutForm.depotId == null) {
                alert('请选择仓库');
                return;
            } else if ($scope.stockOutType == 2 && $scope.stockOutForm.sourceDepotId == null) {
                alert('请选择调出仓库');
                return;
            }
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            var win = $window.open("/admin/api/stockOut/excel-sku-pick?" + str.join("&"));
            win.onunload = function () {
                $state.go($state.current, $scope.stockOutForm, {reload: true});
            }
        };

        $scope.excelTrackerExport = function () {

            if ($scope.stockOutType != 2 && $scope.stockOutForm.depotId == null) {
                alert('请选择仓库');
                return;
            } else if ($scope.stockOutType == 2 && $scope.stockOutForm.sourceDepotId == null) {
                alert('请选择调出仓库');
                return;
            }
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }

            var win = $window.open("/admin/api/stockOut/excel-tracker-pick?" + str.join("&"));
            win.onunload = function () {
                $state.go($state.current, $scope.stockOutForm, {reload: true});
            }
        };

        $scope.excelAssociateExport = function () {
            var str = [];
            for (var p in $scope.stockOutForm) {
                if ($scope.stockOutForm[p] != null) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.stockOutForm[p]));
                }
            }
            $window.open("/admin/api/stockOut/excel-associate?" + str.join("&"));
        };

        $scope.submitting = false;
        $scope.isCheckedAll = false;
        $scope.batchForm = {};
        $scope.batchForm.stockOutIds = [];

        $scope.checkAll = function () {
            if (!($scope.isCheckedAll)) {
                $scope.batchForm.stockOutIds = [];
                angular.forEach($scope.stockOuts, function (value, key) {
                    $scope.batchForm.stockOutIds.push(value.stockOutId);
                });
                $scope.isCheckedAll = true;
            } else {
                $scope.batchForm.stockOutIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.batchOut = function () {
            if ($scope.batchForm.stockOutIds.length == 0) {
                alert("请选择出库单");
                return;
            }

            $scope.submitting = true;
            $scope.skuName = "";
            $http({
                url: "/admin/api/stockOut/send/before-add-all",
                method: "POST",
                data: $scope.batchForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                if (data.length == 0) {
                    $scope.batchOutConfirm();
                } else {
                    $scope.skuName = '【' + data.join('】,【') + '】';
                    angular.element('#befoeOutModal').modal({
                        backdrop : 'static'
                    });
                }

            }).error(function (data, status, headers, config) {
                alert("查询未配货品失败...");
                $scope.submitting = false;
            });
        };

        $scope.batchOutConfirm = function () {

            angular.element('#befoeOutModal').modal('hide');
            $timeout(function (){

                $http({
                    url: "/admin/api/stockOut/send/add-all",
                    method: "POST",
                    data: $scope.batchForm,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                }).success(function (data, status, headers, config) {
                    alert("出库成功...");
                    $scope.submitting = false;
                    $state.go($state.current, $scope.stockOutForm, {reload: true});
                    $scope.batchForm.stockOutIds = [];
                }).error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "出库失败...");
                    $scope.submitting = false;
                });

            }, 100);
        };
        angular.element('#befoeOutModal').on('hidden.bs.modal', function () {
            $scope.$apply(function () {
                $scope.submitting = false;
            });
        });

    });