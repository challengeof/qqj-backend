'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListCityCtrl', function ($scope, $http, $stateParams) {

        $http({
            url: '/admin/api/city',
            method: "GET",
        }).success(function (data, status, headers, congfig) {
            $scope.cities = data;
        });

    });
