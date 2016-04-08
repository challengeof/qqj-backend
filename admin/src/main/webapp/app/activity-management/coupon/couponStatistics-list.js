'use strict';

angular.module('sbAdminApp')
    .controller('couponStatisticsListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {pageSize: 20};
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
        //加载优惠券类别
        $http.get("/admin/api/coupon/couponEnums").success(function (data, status, headers, config) {
            $scope.couponTypes = data;
        });
        //加载优惠券状态
        $http.get("/admin/api/coupon/couponStatus").success(function (data, status, headers, config) {
            $scope.couponStatus = data;
        });
        //alert(222);
        //获取数据
        console.log($scope.searchForm);
        $http({
            method: 'GET',
            url: '/admin/api/coupon/statistics/provide',
            params: $scope.searchForm
        })
        .success(function (data, status, headers, config) {
            $scope.listData = data.content;
            $scope.lineTotal = data.lineTotal;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        })
        .error(function (data, status, headers, config) {
            alert("查询失败");
        })

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
        $scope.export = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/coupon/statistics/search/export?" + str.join("&"));
        };

        $scope.setCanceled=function(couponStatistics){
            if(!confirm("确认作废吗？（优惠券id:"+couponStatistics.couponId+", 餐馆id:"+couponStatistics.restaurantId+"）")){
                return ;
            }
            var ccid= couponStatistics.customerCouponId;
            $http.get("/admin/api/coupon/customer/cancelled/"+ccid).success(function (data) {
                console.log(data);
                console.log(couponStatistics);
                couponStatistics.couponStatus = data.status.value;
                couponStatistics.couponStatusDesc = data.status.name;
                couponStatistics.operater = data.operater;
                couponStatistics.operateTime =data.operateTime;

            }).error(function (data, status, headers, config) {
                alert("操作失败");
            });

        }

    });