'use strict';

angular.module('sbAdminApp')
    .controller('SellCancelItemListCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location, $window) {

        $scope.searchForm = {};
        $scope.page = {};
        if ($stateParams.page) {
            $scope.searchForm.page = parseInt($stateParams.page);
        }

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
                $scope.searchForm.cityId = $scope.cities[0].id;
            }
        }
        $scope.$watch('searchForm.cityId', function (newVal, oldVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/organizations").success(function (data) {
                    $scope.organizations = data;
                });
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                });
                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.searchForm.organizationId = null;
                    $scope.searchForm.depotId = null;
                }
            } else {
                $scope.organizations = [];
                $scope.depots = [];
                $scope.searchForm.organizationId = null;
                $scope.searchForm.depotId = null;
            }
        });

        $http.get("/admin/api/sellCancel/type/list").success(function (data) {
            $scope.type = data;
        });

        $http({
            url: '/admin/api/sellCancelItem/list',
            method: "GET",
            params: $scope.searchForm,
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        }).success(function (data) {
            $scope.sellCancelItems = data.content;
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        });

        $scope.search = function () {
            $location.search($scope.searchForm);
        };

        $scope.pageChanged = function () {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $location.search($scope.searchForm);
        };

        $scope.exportExcel = function () {
            var str = [];
            for (var p in $scope.searchForm) {
                if ($scope.searchForm[p]) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent($scope.searchForm[p]));
                }
            }
            $window.open("/admin/api/sellCancelItem/export/list?" + str.join("&"));
        };
    });