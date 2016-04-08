'use strict';
angular.module('sbAdminApp')
    .controller('spikeItemListCtrl', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {
            spikeId : $stateParams.id
        };

        if($scope.searchForm.spikeId!=null) {
            $http({
                method: 'GET',
                url: '/admin/api/spike/query/'+$scope.searchForm.spikeId
                //params: $scope.searchForm
            }).success(function (data, status, headers, config) {
                $scope.data = data;

            }).error(function (data, status, headers, config) {
                alert("查询失败");
            })
        }

    });