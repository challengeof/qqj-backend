'use strict';
angular.module('sbAdminApp')
    .controller('WXPushCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.push = {
            openid : "0",
            mediaid : "0",
            cityid: 0
        };
        $scope.pushMediaArray = [];

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });

        $scope.createWXPush = function () {
            if($scope.push.mediaid == "0"){
                alert("请填多媒体ID");
                return;
            }

            var mIsPreview = false;
            if($scope.pushTest == true){
                mIsPreview = true;
                $scope.pushTest = false;
            }

            $scope.push.isPreview = mIsPreview;
            $scope.push.openid = $scope.push.openid;

            $http({
                method: 'POST',
                url: '/admin/api/push/wxPushCreate/'+$scope.push.cityid,
                params: $scope.push,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                if(data == true)
                    alert("发送成功!");
                else
                    alert("发送失败");
            })
            .error(function (data, status, headers, config) {
                alert("发送失败!");
            })
        }

        $scope.createWXPushTest = function(){
            $scope.pushTest = true;
            $scope.createWXPush();
        }

        $scope.getWXPushMediaList = function (){
            $scope.pushMediaArray = [];
            $http({
                method: 'GET',
                url: '/admin/api/push/wxPushMediaList'
            })
            .success(function (data, status, headers, config) {
                if(data == null || data == "null"){
                    alert("获取微信多媒体资源失败");
                    return;
                }
                for(var key in data){
                    var pushMedia = {key : "",title : ""};
                    pushMedia.key = key;
                    pushMedia.title = data[key];
                    $scope.pushMediaArray.push(pushMedia);
                }
            })
            .error(function (data, status, headers, config) {
                alert("获取微信多媒体资源失败");
            })

        }
        $scope.getWXPushMediaList();
    })
