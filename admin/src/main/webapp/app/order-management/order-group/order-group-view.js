'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:OrderGroupDetailCtrl
 * @description
 * # OrderGroupDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('OrderGroupViewCtrl', function ($scope, $http, $filter, $stateParams) {
        $scope.totalVolume = 0;
        $scope.totalWight = 0;
        $scope.quantity = 0;

        if ($stateParams.id) {

           $http({
               url : '/admin/api/order-group/'+$stateParams.id,
               method:"GET"
           })
           .success(function(data) {
               $scope.orderGroupModel = data;
//               console.log(JSON.stringify(data.members));

               for(var i = 0; i < data.members.length; i++){
                    var orderItem = data.members[i];
                    $scope.quantity += orderItem.quantity;
                    $scope.totalVolume += orderItem.totalVolume;
                    $scope.totalWight += orderItem.totalWight;
               }
           });
        }
    });
