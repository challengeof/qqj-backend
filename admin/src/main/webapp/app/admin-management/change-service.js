'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddStaffCtrl
 * @description
 * # AddStaffCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ChangeServiceCtrl', function($scope, $state, $http) {

        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.oldAdminUsers = [{id:0, realname:"未分配销售"}].concat(data);
                $scope.newAdminUsers = data;
            });


        $scope.restaurantCountParam = {
            pageSize : 1
        }

        $scope.formData = {}

        $scope.getOldCount = function() {
            $http({
                'async' : false,
                url:'/admin/api/restaurant',
                method: 'GET',
                params:{pageSize:1,adminUserId:$scope.formData.oldAdminUserId}
            }).success(function (data){
                $scope.oldAdminUserCount = data.total;
            });
        }


        $scope.getNewCount = function() {
            $http({
                async : false,
                url:'/admin/api/restaurant',
                method: 'GET',
                params:{pageSize:1,adminUserId:$scope.formData.newAdminUserId}
            }).success(function (data){
                $scope.newAdminUserCount = data.total;
            });
        }

        $scope.$watch('formData.oldAdminUserId', function(newVal) {
            if(newVal || newVal == 0) {
                $scope.getOldCount();
            }
        });

        $scope.$watch('formData.newAdminUserId', function(newVal) {
            if(newVal || newVal == 0) {
                $scope.getNewCount();
            }
        });


        $scope.updateRestaurantBatch = function() {
            $http({
                url: '/admin/api/restaurant/changeAdminUserBatch',
                method: 'PUT',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                params:$scope.formData

            }).success(function() {
                alert("修改成功！");
                $scope.getOldCount();
                $scope.getNewCount();
            }).error(function(data) {
                alert("修改失败!");
            });
        }



    });
