'use strict';
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel.controller('feedbackDetailCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

    $http({
        method: 'GET',
        url: "/admin/api/feedback/"+$stateParams.feedbackId
    }).success(function (data, status, headers, config) {
        $scope.data = data;
    }).error(function (data, status, headers, config) {
        alert("查询失败");
    })



});