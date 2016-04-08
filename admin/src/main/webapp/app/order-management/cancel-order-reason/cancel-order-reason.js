/**
 * Created by challenge on 15/9/14.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddcancelOrderReasonCtrl', function($scope, $rootScope, $http, $stateParams, $state) {

        $http.get("/admin/api/order/reason")
            .success(function(data) {
                $scope.reasons = data;
            });

        $scope.formData = {
            orderId : $stateParams.orderId,
            reasonId : $stateParams.reasonId,
            remark : $stateParams.memo
        };

        $scope.cancelOrder = function() {
            $http({
                method: 'POST',
                url: '/admin/api/order/cancel/reason',
                data: $scope.formData,
                headers: {
                    'Content-Type': 'application/json;charset=UTF-8'
                }
            })
                .success(function(data, status, headers, config) {
                    window.alert("提交成功!");
                    $state.go("oam.orderList", {orderId:$scope.formData.orderId,status:-1});

                })
                .error(function(data, status, headers, config) {
                    window.alert("提交失败！");
                });
        };
    })

