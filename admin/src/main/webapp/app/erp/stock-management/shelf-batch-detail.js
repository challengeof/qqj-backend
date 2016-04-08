'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddBatchShelfCtrl
 * @description
 * # AddBatchShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddBatchShelfCtrl', function ($scope, $rootScope, $http, $state, $stateParams) {

        $scope.shelf = {};
        $scope.shelfAreas = [];
	    $scope.shelfRows = [];
	    $scope.shelfNumbers = [];
	    $scope.submitting = false;
        if ($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
            if ($scope.cities && $scope.cities.length == 1) {
               $scope.shelf.cityId = $scope.cities[0].id;
            }
        }

        $scope.$watch('shelf.cityId', function (newVal, oldVal) {
            if (typeof  newVal != "undefined") {
                $scope.shelf.cityId = newVal;
            }
            if (newVal != null && newVal != "") {
                $http.get("/admin/api/depot/list/" + newVal + "").success(function (data) {
                    $scope.depots = data;
                    if ($scope.depots && $scope.depots.length == 1) {
                       $scope.shelf.depotId = $scope.depots[0].id;
                    }
                });
                if (typeof oldVal != "undefined" && newVal != oldVal) {
                    $scope.shelf.depotId = null;
                }
            } else {
                $scope.depots = [];
                $scope.shelf.depotId = null;
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

        $scope.$watch('shelf.area', function (newVal, oldVal) {
            if (newVal == null && oldVal != null) {
                $scope.shelf.row = null;
                $scope.shelf.number = null;
            }
        });
        $scope.$watch('shelf.row', function (newVal, oldVal) {
            if ($scope.shelf.area == null && newVal != null) {
                $scope.shelf.row = null;
            } else if (newVal == null && oldVal != null) {
                $scope.shelf.number = null;
            }
        });
        $scope.$watch('shelf.number', function (newVal, oldVal) {
            if (($scope.shelf.area == null || $scope.shelf.row == null) && newVal != null) {
                $scope.shelf.number = null;
            }
        });

        /*添加货位 */
        $scope.createBatchShelf = function () {
            $scope.submitting = true;
            $http({
                method: 'POST',
                url: '/admin/api/batchShelf',
                data: $scope.shelf,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            })
            .success(function (data, status, headers, config) {
                alert("添加成功!");
                $scope.submitting = false;
                $state.go($state.current, null, {reload: true});
            })
            .error(function (data, status, headers, config) {
                var errMsg = '';
                if (data != null && data.errmsg != null) {
                    errMsg = data.errmsg + ',';
                }
                alert(errMsg + "添加失败!");
                $scope.submitting = false;
            });
        }

    });