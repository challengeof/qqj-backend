'use strict';

angular.module('sbAdminApp')
    .controller('SaleVisitDetailCtrl', function ($scope, $rootScope, $http, $stateParams) {

        $scope.saleVisitForm = {};
        //$scope.saleVisitForm.saleVisitPurposes=[];
        //$scope.saleVisitForm.saleVisitIntentionProductions=[];
        //$scope.saleVisitForm.saleVisitTroubles=[];
        //$scope.saleVisitForm.saleVisitSolutions=[];
        if ($stateParams.id) {
            $http.get($rootScope.rootPath + "/api/saleVisit/" + $stateParams.id).success(function (data) {
                $scope.saleVisitForm = data;
            })
        }

        $http.get($rootScope.rootPath + "/api/saleVisit/purpose/list").success(function (data) {
            $scope.visitPurposes = data;
        });
        $http.get($rootScope.rootPath + "/api/saleVisit/stage/list").success(function (data) {
            $scope.visitStages = data;
        });
        $http.get($rootScope.rootPath + "/api/saleVisit/intentionProduction/list").success(function (data) {
            $scope.intentionProductions = data;
        });
        $http.get($rootScope.rootPath + "/api/saleVisit/trouble/list").success(function (data) {
            $scope.visitTroubles = data;
        });
        $http.get($rootScope.rootPath + "/api/saleVisit/solution/list").success(function (data) {
            $scope.visitSolutions = data;
        });

        $scope.$watch('saleVisitForm.restaurantId', function (val) {
            $http.get($rootScope.rootPath + "/api/restaurant/" + val).success(function (data) {
                console.log(data)
                $scope.saleVisitForm.restaurant = data;
            })
        });

        $scope.submitDateFormat = "yyyy-MM-dd";
        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd HH:mm:ss',
            startingDay: 1
        };
        $scope.timeOptions = {
            showMeridian: false
        };
        $scope.isVisitTimeOpen = false;
        $scope.isNextVisitTimeOpen = false;
        $scope.visitTimeOpen = function (e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isVisitTimeOpen = true;
        };

        $scope.nextVisitTimeOpen = function (e) {
            e.preventDefault();
            e.stopPropagation();
            $scope.isNextVisitTimeOpen = true;
        };

        $scope.update = function () {
            $http({
                method: 'POST',
                url: $rootScope.rootPath + '/api/saleVisit/update',
                data: $scope.saleVisitForm,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function () {
                alert("操作成功!");
            }).error(function () {
                alert("操作失败...");
            })
        }

    });