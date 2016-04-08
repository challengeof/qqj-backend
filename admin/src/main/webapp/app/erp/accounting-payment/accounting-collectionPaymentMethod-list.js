'use strict';
angular.module('sbAdminApp')
    .controller('AccountCollectionPaymentMethodListCtrl', function ($scope, $rootScope, $http, $stateParams, $location) {

        $scope.formData = {};
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }
        $http({
            url: "/admin/api/account/collectionPaymentMethod/list",
            method: 'GET',
            params: $scope.formData,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.collectionPaymentMethods = data;
        }).error(function (data) {
            window.alert("加载失败...");
        });
        $scope.search = function () {
            $location.search($scope.formData);
        };
    });