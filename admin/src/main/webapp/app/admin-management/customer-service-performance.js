'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('PerformanceCtrl', function ($scope, $http, $filter) {

        $scope.month = new Date();

        $scope.queryPerformance = function() {
            $http({
                url: "/admin/api/performance",
                method: "GET",
                params: {month: $filter('date')($scope.month, 'yyyy-MM-dd')}
            }).success(function (data) {
                $scope.performances = data;
            })
        }

        $scope.queryPerformance();

        $scope.$watch('month', function() {
            $scope.queryPerformance();

            var firstDate = $scope.month.setDate(1);
            var endDate = new Date(firstDate);
            endDate.setMonth($scope.month.getMonth()+1);
            endDate.setDate(0);

            $scope.firstDate = $filter('date')(firstDate,'yyyy-MM-dd');
            $scope.endtDate = $filter('date')(endDate,'yyyy-MM-dd');
        })

    });