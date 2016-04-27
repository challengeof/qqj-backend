/**
 * Created by challenge on 16/4/27.
 */

'use strict';

angular.module('sbAdminApp')
    .controller('BarcodeListCtrl', function ($scope, $http, $stateParams, $location) {
        $scope.barcodeList = {};

        $scope.barcodeListSearchForm = {
            page: $stateParams.page,
            pageSize: $stateParams.pageSize
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.submitDateFormat = "yyyy-MM-dd";

        $scope.page = {
            itemsPerPage: 50
        };

        $scope.pageChanged = function() {
            $scope.barcodeListSearchForm.page = $scope.page.currentPage - 1;
            $scope.barcodeListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchBarcodeList();
        }

        $http({
            url: '/api/barcode/query',
            method: "GET",
            params: $scope.barcodeListSearchForm
        }).success(function (data) {
            $scope.barcodeList = data.content;

            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            window.alert("搜索失败...");
        });

        $scope.searchBarcodeList = function () {
            $scope.barcodeListSearchForm.page = 0;
            $scope.barcodeListSearchForm.pageSize = 50;
            $scope.searchBarcodeList();
        }

        $scope.searchBarcodeList = function () {
            $location.search($scope.barcodeListSearchForm);
        }



    });
