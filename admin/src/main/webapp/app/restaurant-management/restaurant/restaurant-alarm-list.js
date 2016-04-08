'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AlarmRestaurantCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location) {

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd 00:00';

        $scope.restaurantSearchForm = {
            orderDate : $stateParams.orderDate
        }

        $scope.$watch('orderDate', function(newVal) {
            if(newVal){
                $scope.startDate = $filter('date')(newVal, $scope.format);
                var date = $filter('date')(newVal, $scope.format);
                date = new Date(date);
                date = date.setDate(date.getDate() + 1);
                $scope.endDate = $filter('date')(date, $scope.format);
                $scope.restaurantSearchForm.orderDate = $filter('date')(newVal, $scope.format);
            }
        });

        if($scope.restaurantSearchForm.orderDate) {
            $scope.orderDate = Date.parse($scope.restaurantSearchForm.orderDate);
        }

        $http({
                url: "/admin/api/restaurant/alarm",
                method: "GET",
                params: $scope.restaurantSearchForm
            })
            .success(function (data, status, headers, config) {
                $scope.restaurants = data.restaurants;
                $scope.alarmCount = data.alarmCount;
            })
            .error(function (data, status, headers, config) {
                alert("加载失败...");
            });

        $scope.resetPageAndSearchForm = function() {
            $location.search($scope.restaurantSearchForm);
        }
    });
