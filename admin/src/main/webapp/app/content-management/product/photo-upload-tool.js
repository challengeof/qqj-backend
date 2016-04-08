'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('photo-upload-tool', function ($scope, $http, $stateParams, $upload, $rootScope) {

        $scope.formData = {
            mediaFileIds:[],
            mediaFiles:[]
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
                        if($scope.formData.mediaFileIds.indexOf(data.id) == -1) {
                            $scope.formData.mediaFiles.push(data);
                            $scope.formData.mediaFileIds.push(data.id);
                        }
                        if ($scope.formData.mediaFileIds.length > 1) {
                            var defaultImg = $scope.formData.mediaFileIds.indexOf(7713);
                            if (defaultImg != -1) {
                                $scope.formData.mediaFileIds.splice(defaultImg, 1);
                                $scope.formData.mediaFiles.splice(defaultImg, 1);
                            }
                        }
                    })
                }
            }
        });

        $scope.deleteImg = function(id) {
            var index = $scope.formData.mediaFileIds.indexOf(id);
            if (index != -1) {
                $scope.formData.mediaFileIds.splice(index, 1);
                $scope.formData.mediaFiles.splice(index, 1);
            }
            if ($scope.formData.mediaFileIds.length < 1) {
                var defaultImg = 7713;
                var defaultImgUrl = "http://7xijms.com1.z0.glb.clouddn.com/default";
                $scope.formData.mediaFileIds.push(defaultImg);
                $scope.formData.mediaFiles.push({id:defaultImg,url:defaultImgUrl});
            }
        };
    });
