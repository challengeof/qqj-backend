'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListOrganizationCtrl
 * @description
 * # ListOrganizationCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('ListOrganizationCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }

        $scope.openStart = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.openedStart = true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();

        $scope.page = {
            itemsPerPage: 100
        };

        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.organizations = {};
        $scope.orderListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize,
            createDate: $stateParams.createDate,
            serviceAdminId: $stateParams.serviceAdminId,
            name: $stateParams.name,
        };

        if($scope.orderListSearchForm.createDate) {
            $scope.createDate = Date.parse($scope.orderListSearchForm.createDate);
        }

        if($stateParams.status) {
            $scope.orderListSearchForm.status = parseInt($stateParams.status);
        }

        if($stateParams.adminId) {
            $scope.orderListSearchForm.adminId = parseInt($stateParams.adminId);
        }

        if($stateParams.cityId) {
            $scope.orderListSearchForm.cityId = parseInt($stateParams.cityId);
        }

        if($stateParams.name) {
            $scope.orderListSearchForm.name = parseInt($stateParams.name);
        }

        $scope.$watch('createDate', function(d) {
            if(d){
                $scope.orderListSearchForm.createDate = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;

            $scope.searchOrganizationList();
        }

        $scope.searchOrganizationList = function () {
            $location.search($scope.orderListSearchForm);
            
            $http({
                url: '/admin/api/organization',
                method: "GET",
                params: $scope.orderListSearchForm
            }).success(function (data, status, headers, congfig) {
                $scope.organizations = data.organizations;
                $scope.count = data.total;

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.searchOrganizationList();

        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrganizationList();
        }
    });
