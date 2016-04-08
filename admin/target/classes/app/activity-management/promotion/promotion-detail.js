/**
 * Created by challenge on 15/10/26.
 */
'use strict';
//var sbAdminAppModel = angular.module('sbAdminApp', ['ngMessages','ui.map']);
var sbAdminAppModel = angular.module('sbAdminApp');
sbAdminAppModel
    .controller('CreatePromotionCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {
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

        $http.get("/admin/api/brand")
            .success(function (data) {
                $scope.brands = data;
            })

        $scope.categories = {
            url: '/admin/api/category/treeJson'
        }

        $scope.treeConfig = {
            'plugins': ["wholerow", "checkbox"],
        }

        $scope.promotion = {
            categoryIds: []
        };

        $scope.promotionPatterns = [
            {
                "name" : "请选择活动方式"
            },
            {
                "id": 1,
                "name": "满足条件就可以参加活动"
            },
            {
                "id": 2,
                "name": "今日首单"
            },
            {
                "id": 3,
                "name": "餐馆首单"
            }
        ]

        $scope.skuUnit = [
            {
                "name" : "请选择sku单位"
            },
            {
                "id" : true,
                "name" : "打包"
            },
            {
                "id" : false,
                "name" : "单品"
            }
        ]

        $scope.promotionTypeValidation = [

            {
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": true,
                "skuId": false,
                "quantity": false,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : false,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : false
            },

            {
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : true,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : false
            },
            {//买一赠一
                "promotionType": true,
                "cityId": true,
                "warehouseId": false,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "promotionPattern": true,
                "skuUnit" : true,
                "buySkuUnit" : true,
                "buyQuantity" : true,
                "buySkuId" : true,
                "limited" : false,
                "brandId" : false
            }
        ];

        $scope.promotionTypeShowArray = [
            {//满减活动
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": true,
                "skuId": false,
                "quantity": false,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : false,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : false,
                "brandId" : true
            },
            {//满赠活动(赠物品)
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : true,
                "buySkuUnit" : false,
                "buyQuantity" : false,
                "buySkuId" : false,
                "limited" : true,
                "brandId" : true
            },
            {//买一赠一
                "promotionType": true,
                "cityId": true,
                "warehouseId": true,
                "discount": false,
                "skuId": true,
                "quantity": true,
                "useRestrictionsTotalMin": true,
                "useRestrictionsTotalMax": true,
                "start": true,
                "end": true,
                "description": true,
                "organizationId": true,
                "useRestrictionsCategories": true,
                "skuUnit" : true,
                "buySkuUnit" : true,
                "buyQuantity" : true,
                "buySkuId" : true,
                "limited" : true,
                "brandId" : false
            }
        ];

        $scope.changePromotionType = function () {
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeShow = $scope.promotionTypeShowArray[promotionType - 1];
            for (var key in promotionTypeShow) {
                if (promotionTypeShow[key]) {
                    angular.element("[name=" + key + "]").parent().removeClass('ng-hide');
                } else {
                    angular.element("[name=" + key + "]").parent().addClass('ng-hide');
                }
            }
        }

        $scope.disableByName = function (name) {
            angular.element("[name=" + name + "]").attr("disabled", "disabled");
        }

        $scope.tableInvalid = function () {
            if (!$scope.promotion) {
                return true;
            }
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeValidation = $scope.promotionTypeValidation[promotionType - 1];
            for (var key in promotionTypeValidation) {
                if (promotionTypeValidation[key] && $scope.addPromotionForm[key].$invalid) {
                    return true;
                }
            }

            return false;
        }

        $scope.sendInvalid = function () {
            if (!$scope.promotion) {
                return true;
            }
            return $scope.addPromotionForm["restaurants"].$invalid;
        }

        $scope.disabledPromotion = function () {
            var promotionType = $scope.promotion.promotionType;
            var promotionTypeValidation = $scope.promotionTypeValidation[promotionType - 1];
            for (var key in promotionTypeValidation) {
                angular.element("[name=" + key + "]").attr("disabled", "disabled");
            }
            angular.element("[type=submit]").attr("disabled", "disabled");
        }

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });

        $scope.$watch('promotion.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                });
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                });

            } else {
                $scope.warehouses = [];
                $scope.organizations = [];
            }
        });

        $scope.readyCB = function () {
            $scope.useRestrictionsTreeInstance.jstree(true).check_node($scope.useRestrictionsCategoryIds);
        };

        $http.get("/admin/api/promotion/promotionEnums")
            .success(function (data, status, headers, config) {
                $scope.promotionTypes = data;
            });

        if ($stateParams.id) {
            $http.get("/admin/api/promotion/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.promotion = data;
                    //$scope.promotion.start = new Date(data.start).toISOString();
                    //$scope.promotion.end = new Date(data.end).toISOString();
                    $scope.useRestrictionsCategoryIds = data.useRestrictionsCategoryIds;
                    $scope.promotion.promotionPattern = data.promotionPattern;
                    $scope.readyCB();
                    $scope.changePromotionType();
                })
                .error(function (data, status) {
                    window.alert("获取无忧券信息失败...");
                    return;
                });
        } else {
            $scope.addPromotion = true;
        }

        $scope.createPromotion = function () {
            $scope.promotion.useRestrictionsCategoryIds = $scope.useRestrictionsTreeInstance.jstree(true).get_top_selected();
            if ($stateParams.id != '') {
                $http({
                    method: 'PUT',
                    url: '/admin/api/promotion/edit/' + $stateParams.id,
                    data: $scope.promotion,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                        $state.go("oam.promotionManagement");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/promotion/create',
                    data: $scope.promotion,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                        $state.go("oam.promotionManagement");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }

    });