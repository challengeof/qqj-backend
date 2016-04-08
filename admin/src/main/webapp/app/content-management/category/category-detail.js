'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('CategoryDetailCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {
            showSecond : false
        };

        /*分类状态list*/
        $http.get("/admin/api/category/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })



        $http.get("/admin/api/category")
            .success(function (data, status, headers, config) {
                $scope.availableParentCategories = [];

                angular.forEach(data, function (value, key) {
                    if (!$stateParams.id || $stateParams.id != value.id) {
                        this.push(value);
                    }
                }, $scope.availableParentCategories);
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            })

        if ($stateParams.id) {
            $http.get("/admin/api/category/" + $stateParams.id).success(function (data) {
                $scope.formData.name = data.name;
                $scope.formData.status = data.status.value;
                $scope.formData.showSecond = data.showSecond;
                if(data.parentCategoryId) {
                    $scope.formData.parentCategoryId = data.parentCategoryId;
                }

                $scope.formData.displayOrder = data.displayOrder;
                if(data.mediaFile) {
                    $scope.mediaUrl = data.mediaFile.url;
                    $scope.formData.mediaFileId = data.mediaFile.id;
                }

            });
        }

        $scope.$watch('media', function(files) {
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
                        $scope.mediaUrl = data.url;
                        $scope.formData.mediaFileId = data.id;
                    })
                }
            }
        });

        $scope.saveCategory = function () {
            if ($stateParams.id) {
                $http({
                    url: "/admin/api/category/" + $stateParams.id,
                    data: $scope.formData,
                    method: 'PUT'
                })
                    .success(function (data) {
                        alert("修改成功!");
                    })
                    .error(function (data) {
                        alert("修改失败!");
                    });
            } else {
                $http({
                    url: "/admin/api/category",
                    data: $scope.formData,
                    method: 'POST'
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败!");
                    });
            }
        }
    });
