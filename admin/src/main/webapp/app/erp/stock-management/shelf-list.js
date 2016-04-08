'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ShelfListCtrl
 * @description
 * # ShelfListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('ShelfListCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.shelfs = {};
	    $scope.formData = {};
	    $scope.page = {itemsPerPage : 100};
	    $scope.shelfAreas = [];
	    $scope.shelfRows = [];
	    $scope.shelfNumbers = [];
	    $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('formData.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.formData.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.formData.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.formData.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.formData.depotId = null;
            }
        });

        $scope.initAreaRowNum = function () {
            for (var i = 1; i < 100; i++) {
                var obj = new Object();
                obj.code = (i < 10 ? '0'+i : i+'');
                $scope.shelfAreas.push(obj);
                $scope.shelfRows.push(obj);
                $scope.shelfNumbers.push(obj);
            }
        };
        $scope.initAreaRowNum();

        $scope.isCheckedAll = false;
        $scope.formData.shelfIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.shelfIds = [];
                angular.forEach($scope.shelfs, function(value, key){
                    $scope.formData.shelfIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.shelfIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.shelfs = [];
            $scope.formData.shelfIds = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/shelf/list',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.shelfs = data.content;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data) {
            });
        }

        $scope.formData.pageSize = $scope.page.itemsPerPage;
        $scope.searchForm();

        $scope.pageChanged = function () {
            $scope.searchForm($scope.page.currentPage - 1);
        }

        $scope.batchDelete = function () {
            if ($scope.formData.shelfIds.length == 0) {
                alert("请选择货位");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/shelf/del",
                method: "DELETE",
                data: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                alert("删除成功...");
                $scope.submitting = false;
                $scope.searchForm();
            }).error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "删除失败...");
                $scope.submitting = false;
            });
        };

	});