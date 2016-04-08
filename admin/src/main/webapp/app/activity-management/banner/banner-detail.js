'use strict';
angular.module('sbAdminApp')
    .controller('CreateBannerCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.isEditBanner = false;

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



        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });


        $scope.banner = {
            bannerUrl:{
                imgUrl:null,
                redirectUrl:null
            }
        }


        $scope.$watch('banner.cityId', function (newVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                    $scope.warehouses.push({
                        "id": 0,
                        "name": "全城",
                        "city": {},
                        "displayName": "全城"
                    });
                });
            } else {
                $scope.warehouses = [];
            }
        })



        if ($stateParams.id) {
            $scope.isEditBanner = true;

            $http.get("/admin/api/banner/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.banner = data;
                })
                .error(function (data, status) {
                    window.alert("获取banner信息失败...");
                });

        }


        /*添加/编辑banner*/
        $scope.createBanner = function () {


            if ($stateParams.id != '' && $stateParams.id != undefined) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/banner/' + $stateParams.id,
                    data: $scope.banner,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {

                $http({
                    method: 'POST',
                    url: '/admin/api/banner/create',
                    data: $scope.banner,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }
        //上传图片
        $scope.$watch('media', function (files) {
            if (files != null) {
                for (var i = 0; i < files.length; i++) {
                    $upload.upload({
                        url: '/admin/api/media',
                        method: 'POST',
                        file: files[i]
                    }).progress(function (evt) {
                        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                        $scope.uploadProgress = ('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                    }).success(function (data) {
                        //$scope.banner.imgUrl = data.url;
                        $scope.banner.bannerUrl.imgUrl = data.url;
                       $scope.banner.mediaFileId = data.id;
                    })
                }
            }
        })

    });