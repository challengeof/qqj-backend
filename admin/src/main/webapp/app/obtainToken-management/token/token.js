/**
 * Created by challenge on 15/10/29.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('TokenManagementCtrl', function($scope, $http , $stateParams) {


        $scope.obtainToken = function () {
            $http.get("/admin/api/customer/token/" + $scope.username.token)
                .success(function(data){
                    alert("用户token为:"+data.token+"有效期为30分钟!");
                    $scope.username = data;
                })
                .error(function(data){
                    alert("获取失败");
                });
        }
    });