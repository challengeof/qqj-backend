'use strict';
angular.module('callerApp') .controller('add',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {

        $scope.addCallerFormData ={
            phone:$stateParams.phone,
            name:$stateParams.name,
            detail:$stateParams.detail
        };
        console.log($scope.addCallerFormData);
        //alert(123);
        $scope.add=function(){

            console.log($scope.addCallerFormData);
            $.ajax({
                url: "/admin/api/caller/add", data: $scope.addCallerFormData, async: false, cache: false,
                success: function (content) {
                    console.log(content);
                    if(content.errno==0){
                        alert("增加客户成功");
                    }else{
                        alert(content.errmsg);
                    }
                },
                error: function (content) {
                    alert("请求失败");
                }
            });
        }

    });