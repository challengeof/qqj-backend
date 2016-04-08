'use strict';

angular.module('sbAdminApp')
    .controller('OrderLimitListCtrl', function ($scope, $http, $stateParams) {
        $http({
            url: '/admin/api/conf/orderLimit/list',
            method: "GET",
        }).success(function (data, status, headers, congfig) {
            $scope.orderLimitList = data;
        });

        $scope.checkLimit = function(data) {
            var REGEX = /^\-?\d+(.\d+)?$/
            if (!data || !REGEX.test(data)) {
                return "请输入数字";
            }
        }

        $scope.saveOrderLimit = function(orderLimit) {
            var postData = {};
            postData.name = 'order_limit';
            postData.key = orderLimit.city.id;
            postData.value = orderLimit.limit;
            $http({
                url: "/admin/api/conf/save",
                method: "POST",
                data: postData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("保存成功...");
            })
            .error(function (data, status, headers, config) {
                alert("保存失败...");
            });
        }
    });
