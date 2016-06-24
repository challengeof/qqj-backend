'use strict';
angular.module('sbAdminApp')
    .controller('TmpCustomerDetailCtrl', function($scope, $state, $stateParams, $http) {

        $scope.iForm = {};

        $http({
            url: "/org/customer/register-task/" + $stateParams.id,
            method: "GET",
        })
        .success(function (data) {
            $scope.data = data;
        });

        if ($stateParams.type == 2) {
            $scope.canAudit = true;
        }

        $scope.audit = function (result) {
            var auditRequest = {};
            auditRequest.result = result;
            auditRequest.type = 2;
            auditRequest.tmpCustomerId = $scope.data.id;
            $http({
                method: 'POST',
                url: '/org/customer/audit',
                data: auditRequest,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            }).success(function(data) {
                if (data.success) {
                    alert("审核成功!");
                    $state.go("oam.tmp-customer-list");
                } else {
                    alert(data.msg);
                }
            }).error(function(data) {
                alert("审核失败!");
            });
        }

    });
