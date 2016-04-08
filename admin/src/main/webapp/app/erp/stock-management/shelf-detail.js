'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddOrUpdateShelfCtrl
 * @description
 * # AddOrUpdateShelfCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('AddOrUpdateShelfCtrl', function ($scope, $rootScope, $http, $stateParams) {

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

        $scope.generalCodeName = function () {
            $scope.shelf.shelfCode = '';
            $scope.shelf.name = '';
            if ($scope.shelf.area != null) {
                $scope.shelf.shelfCode += $scope.shelf.area;
                $scope.shelf.name += $scope.shelf.area + '区';
            }
            if ($scope.shelf.row != null) {
                $scope.shelf.shelfCode += $scope.shelf.row;
                $scope.shelf.name += $scope.shelf.row + '排';
            }
            if ($scope.shelf.number != null) {
                $scope.shelf.shelfCode += $scope.shelf.number;
                $scope.shelf.name += $scope.shelf.number + '号';
            }
        };

        $scope.$watch('shelf.area', function (newVal, oldVal) {
            if (newVal == null && oldVal != null) {
                $scope.shelf.row = null;
                $scope.shelf.number = null;
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });
        $scope.$watch('shelf.row', function (newVal, oldVal) {
            if ($scope.shelf.area == null && newVal != null) {
                $scope.shelf.row = null;
                return;
            }
            if (newVal == null && oldVal != null) {
                $scope.shelf.number = null;
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });
        $scope.$watch('shelf.number', function (newVal, oldVal) {
            if (($scope.shelf.area == null || $scope.shelf.row == null) && newVal != null) {
                $scope.shelf.number = null;
                return;
            }
            if (newVal == null && oldVal != null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal == null) {
                $scope.generalCodeName();
            } else if (newVal != null && oldVal != null && newVal != oldVal) {
                $scope.generalCodeName();
            }
        });

        $scope.isEdit = false;

        /*根据id获取货位信息*/
        if ($stateParams.id != null && $stateParams.id != '') {
            $scope.isEdit = true;
            $http.get("/admin/api/shelf/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.shelf = data;
                    $scope.shelf.cityId = data.depot.city.id;
                    $scope.shelf.depotId = data.depot.id;
                });
        }

        /*添加/编辑货位 */
        $scope.createShelf = function () {

            $scope.submitting = true;
            if ($scope.isEdit) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/shelf/' + $stateParams.id,
                    data: $scope.shelf,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("修改成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "修改失败!");
                    $scope.submitting = false;
                })
            } else {
                $http({
                    method: 'POST',
                    url: '/admin/api/shelf',
                    data: $scope.shelf,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                .success(function (data, status, headers, config) {
                    alert("添加成功!");
                    $scope.submitting = false;
                })
                .error(function (data, status, headers, config) {
                    var errMsg = '';
                    if (data != null && data.errmsg != null) {
                        errMsg = data.errmsg + ',';
                    }
                    alert(errMsg + "添加失败!");
                    $scope.submitting = false;
                })
            }
        }

    });