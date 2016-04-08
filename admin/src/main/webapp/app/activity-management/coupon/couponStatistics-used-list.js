'use strict';

angular.module('sbAdminApp')
.controller('couponStatisticsUsedListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            pageSize: 20,
            listType: $stateParams.listType == null?  "TJ" : $stateParams.listType
        };
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
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
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        //$scope.$watch("searchForm.listType",function(newVal, oldVal){
        //    alert(newVal);
        //});

        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/warehouse/city/" + newVal + "").success(function (data) {
                    $scope.warehouses = data;
                    if ($scope.warehouses && $scope.warehouses.length == 1) {
                        $scope.searchForm.warehouseId = $scope.warehouses[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.searchForm.warehouseId = null;
                }
            } else {
                $scope.warehouses = [];
                $scope.searchForm.warehouseId = null;
            }
        });

        $scope.listTypeChange=function () {
            $scope.searchForm.orderId = null;
            $scope.searchForm.sendFront = null;
            $scope.searchForm.sendBack = null;

            $scope.searchForm.orderDateFront = null;
            $scope.searchForm.orderDateBack = null;
            $scope.searchForm.stockoutDateFront = null;
            $scope.searchForm.stockoutDateBack = null;

            if ($scope.searchForm.listType != listTypeContent.SYMX.key && $scope.searchForm.listType != listTypeContent.TJ.key) {
                $scope.searchForm.useFront = null;
                $scope.searchForm.useBack = null;
            }


        };

        //加载优惠券类别
        $http.get("/admin/api/coupon/couponEnums").success(function (data, status, headers, config) {
            $scope.couponTypes = data;
        });
        //加载优惠券状态
        $http.get("/admin/api/coupon/couponStatus").success(function (data, status, headers, config) {
            $scope.couponStatus = data;
        });
        $scope.listTypeContent={
            "TJ":{
                key:"TJ",
                url:"/admin/api/coupon/statistics/used",
                exportUrl:"/admin/api/coupon/statistics/used/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            },
            "FFMX":{
                key:"FFMX",
                url:"/admin/api/coupon/statistics/provide",
                exportUrl:"/admin/api/coupon/statistics/provide/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            },
            "SYMX":{
                key:"SYMX",
                url:"/admin/api/coupon/statistics/usedDetail",
                exportUrl:"/admin/api/coupon/statistics/usedDetail/export",
                success:function(data, status, headers, config){
                    $scope.listData = data.content;
                    $scope.lineTotal= data.lineTotal;
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                }
            }
        };
        $scope.listType=$scope.searchForm.listType;
        $scope.loadData=function(){
            var cTypeUrl=$scope.listTypeContent[$scope.searchForm.listType];
            if(cTypeUrl!=null) {
                $http({
                    method: 'GET',
                    url: cTypeUrl.url,
                    params: $scope.searchForm
                }).success(cTypeUrl.success).error(function (data, status, headers, config) {
                        alert("查询失败");
                 })
            }
        };

        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            var cTypeUrl=$scope.listTypeContent[$scope.searchForm.listType];
            $window.open(cTypeUrl.exportUrl+"?" + str.join("&"));
        };

        $scope.loadData();
        $scope.search = function () {
            $scope.searchForm.page = 0;
            $location.search($scope.searchForm);
        };
        $scope.reset = function () {
            $scope.searchForm = {};
        };
        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };
});