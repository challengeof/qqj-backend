'use strict';
angular.module('sbAdminApp')
    .controller('TmpCustomerDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.iForm = {};

        $http({
            url: "/org/api/tmp-customer/" + $stateParams.id,
            method: "GET",
        })
        .success(function (data) {
            $scope.data = data;
        });

        $scope.audit = function (result) {
            var auditRequest = {};
            auditRequest.result = result;
            auditRequest.type = 3;
            auditRequest.tmpCustomerId = $scope.data.id;
            $http({
                method: 'POST',
                url: '/org/api/customer/audit',
                data: auditRequest,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function(data) {
                alert("审核成功!");
                $state.go("oam.tmp-customer-list");
            }).error(function(data) {
                alert("审核失败!");
            });
        }

    });
