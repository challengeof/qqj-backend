'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:RestaurantManagementCtrl
 * @description
 * # RestaurantManagementCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('saleVisitManagementCtrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location) {

        $http.get("/admin/api/saleVisit/status")
            .success(function(data) {
                $scope.saleVisitStatus = data;
            });

        $scope.saleVisitForm = {
            restaurantId : $stateParams.restaurantId
        };

        $scope.$watch('saleVisitForm.status', function(newVal, oldVal){

            if(newVal != null && newVal != '') {
                $http.get("/admin/api/saleVisit/status/" + newVal + "/reason")
                    .success(function (data, status, headers, config) {
                        if (data) {
                            $scope.reasons = data;
                        } else {
                            $scope.reasons = [{name : "无",value : -1}];
                            $scope.saleVisitForm.reasonId = -1;
                        }
                    });

                if (typeof oldVal != 'undefined' && newVal != oldVal) {
                    $scope.saleVisitForm.reasonId = null;
                }
            } else {
                $scope.reasons = [];
                $scope.saleVisitForm.reasonId = null;
            }
        });






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

        /*销售combobox*/
        $http.get("/admin/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.adminUsers = data;
            })

        $scope.page = {
            itemsPerPage : 100
        };

        $scope.saleVisitForm = {
            page : $stateParams.page,
            pageSize : $stateParams.pageSize,
            restaurantId : $stateParams.restaurantId,
            status : $stateParams.status,
            reasonId : $stateParams.reasonId
        }

        $scope.$watch('startDate', function(newVal) {
            if(newVal){
                $scope.saleVisitForm.start = $filter('date')(newVal, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('endDate', function(newVal) {
            if(newVal){
                $scope.saleVisitForm.end = $filter('date')(newVal, 'yyyy-MM-dd');
            }
        });


        $scope.searchSaleVisit = function () {
            $http({
                url: "/admin/api/saleVisit",
                method: "GET",
                params: $scope.saleVisitForm
            })
                .success(function (data, status, headers, config) {
                    $scope.saleVisits = data.saleVisits;
                    /*分页数据*/
                    $scope.page.itemsPerPage = data.pageSize;
                    $scope.page.totalItems = data.total;
                    $scope.page.currentPage = data.page + 1;
                })
                .error(function (data, status, headers, config) {
                    alert("加载失败...");
                });

        }

        $scope.searchSaleVisit();

        $scope.pageChanged = function() {
            $scope.saleVisitForm.page = $scope.page.currentPage - 1;
            $scope.saleVisitForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchSaleVisit();
        }

        $scope.resetPageAndSearchSaleVisit = function() {
            $scope.saleVisitForm.page = 0;
            $scope.saleVisitForm.pageSize = 100;

            $scope.searchSaleVisit();
        }
    });
