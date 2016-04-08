'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderDetailCtrl
 * @description
 * # OrderDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SellReturnDetailCtrl', function ($scope, $http, $stateParams, $state) {
        $http.get("/admin/api/sellReturn/" + $stateParams.id)
            .success(function (data, status, headers, config) {
                console.log(data);
                $scope.sellReturn = data;
                $scope.order = data.order;
            });

        $scope.sellReturnForm = {};

        $scope.refundObj = {
            reasonId : null,
            skuRefundRequests : []
        };

        $scope.audit = function (status) {
            if (status) {
                $scope.sellReturnForm.status = 1;
            } else {
                $scope.sellReturnForm.status = 2;
            }
            $http({
                url: "/admin/api/sellReturn/" + $stateParams.id,
                method: "PUT",
                params: $scope.sellReturnForm,
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("审核成功...");
                $state.go("oam.sellReturn-list");
            })
            .error(function (data, status, headers, config) {
                alert("操作失败" + ":" + data.errmsg);
            });
        }
    });

