'use strict';
angular.module('sbAdminApp')
    .controller('SellCancelDetailCtrl', function ($scope, $http, $stateParams, $state) {

        $http.get("/admin/api/sellCancel/" + $stateParams.id).success(function (data) {
            $scope.sellCancel = data;
        });
    });

