/**
 * Created by challenge on 15/9/17.
 */
/**
 * Created by challenge on 15/9/16.
 */
'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListAllAdminUsersCtrl
 * @description
 * # ListAllAdminUsersCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('SalesmanCtrl', function ($scope, $http, $filter,$stateParams) {


        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.openEnd = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedEnd = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();


        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.adminUsers = [{id:0, realname:"未分配销售"}].concat(data);
            })

        $scope.restaurantSearchForm = {
            adminUserId : $stateParams.adminUserId,
            start : $stateParams.start,
            end : $stateParams.end
        };

        if($scope.restaurantSearchForm.start) {
            $scope.start = Date.parse($scope.restaurantSearchForm.start);
        }

        if($scope.restaurantSearchForm.end) {
            $scope.end = Date.parse($scope.restaurantSearchForm.end);
        }

        if($stateParams.adminUserId) {
            $scope.adminUserId = parseInt($stateParams.adminUserId);
        }

        $scope.$watch('start', function(d) {
            if(d){
                $scope.restaurantSearchForm.start = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('end', function(d) {
            if(d){
                $scope.restaurantSearchForm.end= $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.resetPageAndSearchOrderList = function () {

            $http({
                url: "/admin/api/salesman-statistics",
                method: "GET",
                params: $scope.restaurantSearchForm
            })
                .success(function (data, status, headers, config) {
                    $scope.salesmanStatisticses = data;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });

        }
        $scope.resetPageAndSearchOrderList();
    });
