'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SystemEmailListCtrl
 * @description
 * # SystemEmailListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('SystemEmailListCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.systemEmails = {};
	    $scope.formData = {};
	    $scope.page = {itemsPerPage : 100};
	    $scope.submitting = false;

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.formData.cityId = $scope.cities[0].id;
            }
        }

        $http.get("/admin/api/systemEmail/type/list").success(function (data) {
            $scope.type = data;
        });

        $scope.isCheckedAll = false;
        $scope.formData.systemEmailIds = [];

        $scope.checkAll = function() {
            if(!($scope.isCheckedAll)){
                $scope.formData.systemEmailIds = [];
                angular.forEach($scope.systemEmails, function(value, key){
                    $scope.formData.systemEmailIds.push(value.id);
                });
                $scope.isCheckedAll = true;
            }else{
                $scope.formData.systemEmailIds = [];
                $scope.isCheckedAll = false;
            }
        };

        $scope.searchForm = function (page) {
            $scope.systemEmails = [];
            $scope.formData.systemEmailIds = [];
            $scope.formData.page = page == null ? 0 : page;
            $http({
                url: '/admin/api/systemEmail/list',
                method: "GET",
                params: $scope.formData,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data) {
                $scope.systemEmails = data.content;
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
            if ($scope.formData.systemEmailIds.length == 0) {
                alert("请选择要删除的设置");
                return;
            }
            $scope.submitting = true;
            $http({
                url: "/admin/api/systemEmail/del",
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