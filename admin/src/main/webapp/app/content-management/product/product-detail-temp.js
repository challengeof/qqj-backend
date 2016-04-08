'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ProductDetailCtrl
 * @description
 * # ProductDetailCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ProductDetailTempCtrl', function ($scope, $http, $stateParams, $upload) {

        $scope.formData = {
        };

        if ($stateParams.id) {
            $http.get("/admin/api/product-temp/" + $stateParams.id).success(function (data) {
                $scope.formData.productVo = data.productVo;
                $scope.formData.details = data.productVo.details;
                $scope.formData.name = data.productVo.name;
                $scope.formData.discrete = data.productVo.discrete;
                $scope.formData.properties = data.productVo.properties;
                $scope.formData.categoryName = data.productVo.category.hierarchyName;
                if (data.productVo.brand) {
                    $scope.formData.brandName = data.productVo.brand.brandName;
                }
                if (data.productVo.mediaFiles) {
                    $scope.formData.mediaFiles = data.productVo.mediaFiles;
                }

                $scope.formData.capacityInBundle = data.productVo.capacityInBundle;
                $scope.formData.barCode = data.productVo.barCode;


                $scope.formData.organization = data.organization;

                var bundleFound = false;
                var unitFound = false;


                if(data.organization) {
                    $scope.formData.organizationId = data.organization.id;
                }

                if(data.productVo.skus) {
                    $scope.formData.skuRequest = data.productVo.skus[0];
                }
            });
        }

    });
