'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:StockAdjustSurplusCtrl
 * @description
 * # StockAdjustSurplusCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('StockAdjustSurplusCtrl', function ($scope, $rootScope, $http, $stateParams, $state) {

        $scope.adjust = {};
	    $scope.submitting = false;
	    $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };
        $scope.skuSearchForm = {
            pageSize: 20,
            showLoader: false
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.adjust.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('adjust.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.adjust.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.adjust.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.adjust.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.adjust.depotId = null;
            }
        });

        $scope.candidateSkus = [];

        $scope.funcAsyncSkus = function (name) {
            if (name && name !== "") {
                $scope.candidateSkus = [];
                $http({
                    url:"/admin/api/stockAdjust/defaultOrganization",
                    method:'GET',
                    params:{showLoader:false}
                }).success(function (organization) {
                    $scope.skuSearchForm.organizationId = organization == null ? null : organization.id;
                    $scope.skuSearchForm.name = name;
                    $http({
                        url:"/admin/api/sku/candidates",
                        method:'GET',
                        params:$scope.skuSearchForm
                    }).success(function (data) {
                        $scope.candidateSkus = data;
                    });
                });
            }
        }

        $scope.resetCandidateSkus = function () {
            $scope.candidateSkus = [];
        }

        $scope.searchSku = function(adjust) {
			$scope.candidateSkus = [];
			if (adjust.skuId == null) {
			    return;
			}
			$http({
				url:"/admin/api/stockAdjust/sku/" + adjust.skuId,
				method:'GET',
			}).success(function (data, status, headers, config) {
			    if (data != null && data.id != null) {
			        $scope.candidateSkus.push(data);
			        adjust.skuId = data.id;
			    } else {
			        alert('sku不存在');
                    adjust.skuId = null;
			    }
			});
		};

        $scope.createAdjust = function () {

            if (!angular.isNumber($scope.adjust.avgCost)) {
                alert('请输入有效的平均成本');
                return;
            }
            if (!angular.isNumber($scope.adjust.adjustQuantity)) {
                alert('请输入有效的调整后数量');
                return;
            }
            if ($scope.adjust.adjustQuantity <= 0) {
                alert('调整后数量应该大于0');
                return;
            }

            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/stockAdjust/createAdjust',
                data: $scope.adjust,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert('商品盘盈单创建成功...')
                $scope.submitting = false;
                $state.go("oam.stock-adjust-list");
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "商品盘盈单创建失败...");
                $scope.submitting = false;
            });
        }

    });