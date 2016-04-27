/**
 * Created by challenge on 16/4/27.
 */

'use strict';

angular.module('sbAdminApp')
    .controller('BarcodeItemListCtrl', function ($scope, $http,$stateParams,$location) {
        $scope.barcodeList = {};
        $scope.barcodeItems = {};

        $scope.barcodeItemsSearchForm = {
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
            $scope.barcodeItemsSearchForm.page = $scope.page.currentPage - 1;
            $scope.barcodeItemsSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchBarcodeItems();
        }

        $http({
            url: '/api/barcode/query-item',
            method: "GET",
            params: $scope.barcodeItemsSearchForm
        }).success(function (data) {
            $scope.barcodeItems = data.content;

            /*分页数据*/
            $scope.page.itemsPerPage = data.pageSize;
            $scope.page.totalItems = data.total;
            $scope.page.currentPage = data.page + 1;
        }).error(function (data) {
            window.alert("搜索失败...");
        });


        $scope.searchBarcodeItems = function () {
            $scope.barcodeItemsSearchForm.page = 0;
            $scope.barcodeItemsSearchForm.pageSize = 50;
            $scope.searchBarcodeItems();
        }

        $scope.searchBarcodeItems = function () {
            $location.search($scope.barcodeItemsSearchForm);
        }



    });
