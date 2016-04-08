'use strict';

angular.module('sbAdminApp')
    .controller('AccountingPaymentDetailCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {
        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';

        $scope.payment = {
        };

        $scope.submitting = false;

        $http.get("/admin/api/accounting/payment/types")
            .success(function (data) {
                $scope.types = data;
            })

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

        $scope.$watch('payment.cityId', function(newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations")
                    .success(function(data) {
                        $scope.organizations = data;
                    });
                $http.get("/admin/api/accounting/payment/methods/" + newVal)
                    .success(function (data) {
                        $scope.methods = data;
                    })
            } else {
                $scope.organizations = [];
                $scope.methods = [];
            }
        });

        $scope.$watch('payment.organizationId', function(newVal, oldVal) {
            if(newVal) {
                $http({
                    url:"/admin/api/vendor",
                    method:'GET',
                    params:{cityId:$scope.payment.cityId,organizationId:newVal}
                }).success(function (data) {
                    $scope.vendors = data.vendors;
                });
            } else {
                $scope.vendors = [];
            }
        });

        if ($stateParams.id) {
            $http.get("/admin/api/coupon/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.coupon = data;
                    $scope.coupon.start = new Date(data.start).toISOString();
                    $scope.coupon.end = new Date(data.end).toISOString();
                })
                .error(function (data, status) {
                    window.alert("获取无忧券信息失败...");
                    return;
                });
        } else {
            $scope.addCoupon=true;
        }

        $scope.create = function () {
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/accounting/payment/add',
                data: $scope.payment,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("添加成功!");
                $scope.submitting = false;
                $state.go("oam.erp-accounting-payment-list");
            })
            .error(function (data, status, headers, config) {
                alert("添加失败!");
                $scope.submitting = false;
            })
        };
    });
