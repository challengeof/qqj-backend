'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('HomeCtrl', function($scope,$http,$filter, $state) {

    var today = new Date().setHours(0,0,0,0);
    var yesterday = new Date(today - 24 * 60 * 60 * 1000);
    var tomorrow = new Date(today + 24 * 60 * 60 * 1000);
    $scope.dateTimeFormat = "yyyy-MM-dd";
    var todayOrderSearchForm = {
            page: 0,
            pageSize: 1,
            start: $filter('date')(today, $scope.dateTimeFormat),
            end: $filter('date')(tomorrow, $scope.dateTimeFormat)
        };

    $scope.today = $filter('date')(today, $scope.dateTimeFormat);


    $http({
        url: '/admin/api/order',
        method: "GET",
        params: todayOrderSearchForm
    }).success(function (data, status, headers, congfig) {
        $scope.todayOrderCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var todayDeliverOrderForm = {
                page: 0,
                pageSize: 1,
                start: $filter('date')(yesterday, $scope.dateTimeFormat),
                end: $filter('date')(today, $scope.dateTimeFormat)
        };

    $http({
        url: '/admin/api/order',
        method: "GET",
        params: todayDeliverOrderForm
    }).success(function (data, status, headers, congfig) {
        $scope.todayDeliverOrderCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var uncheckedRestaurantForm = {
        page : 0,
        pageSize : 1,
        status: 1
    };

    $http({
        url: '/admin/api/restaurant',
        method: "GET",
        params: uncheckedRestaurantForm
    }).success(function (data, status, headers, congfig) {
        $scope.uncheckedRestaurantCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    var unassignedRestaurantForm = {
        page : 0,
        pageSize : 1,
        adminUserId: 0
    };

    $http({
        url: '/admin/api/restaurant',
        method: "GET",
        params: unassignedRestaurantForm
    }).success(function (data, status, headers, congfig) {
        $scope.unassignedRestaurantCount = data.total;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    $http({
        url: '/admin/api/restaurant/alarm',
        method: 'GET',
    }).success(function (data, status, headers, congfig) {
        $scope.alarmRestaurantCount = data.restaurants.length;
    }).error(function (data, status, headers, config) {
        //window.alert("搜索失败...");
    });

    $scope.viewTodayOrder = function() {
        $state.go('oam.orderList', {start: $filter('date')(today, $scope.dateTimeFormat),
                                                end: $filter('date')(tomorrow, $scope.dateTimeFormat)});
        };

    $scope.viewTodayDeliverOrder = function() {
            $state.go('oam.orderList', {start: $filter('date')(yesterday, $scope.dateTimeFormat),
                                                    end: $filter('date')(today, $scope.dateTimeFormat)});
        };

    $scope.viewUncheckedRestaurant = function() {
            $state.go('oam.restaurantList', {status: 1});
        };

    $scope.viewUnassignedRestaurant = function() {
            $state.go('oam.restaurantList', {adminUserId: 0});
        };

});

