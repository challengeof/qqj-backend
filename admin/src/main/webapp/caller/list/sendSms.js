'use strict';
angular.module('callerApp') .controller('sendSms',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window, $state) {

        $scope.sendSmsForm={
            hotline:$stateParams.hotline,
            enterpriseId:$stateParams.enterpriseId,
            cno:$stateParams.cno,
            userName:"admin",
            pwd:$stateParams.pwd,
            seed:"",
            type:12,
            mobile:$stateParams.mobile,
            customerName:$stateParams.customerName,
            msg:null
        };


        console.log($scope.sendSmsForm);

        $scope.send=function(){

            console.log($scope.sendSmsForm);

            $.ajax({
                url: "/admin/api/caller/sendSms", data: $scope.sendSmsForm, async: true, cache: false,
                success: function (data) {
                    console.log(data);
                    //{result:返回结果,error_code:错误码, msg:结果信息}
                    if(data.result=="success"){
                        alert("发送成功");
                        return;
                    }else{
                        console.log(data.error_code);
                        alert(data.msg);
                    }

                },
                error: function (content) {
                    console.log(content);
                    alert("请求失败");
                }
            });


        }

    });