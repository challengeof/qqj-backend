'use strict';
angular.module('sbAdminApp')
    .controller('CreatePushCtrl', function ($scope, $http, $stateParams, $upload) {



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



        $scope.date = new Date().toLocaleDateString();

        $scope.push = {

        }

        $http.get("/admin/api/admin-user/me")
            .success(function (data, status, headers, config) {
                $scope.cities = data.cities;
            });


        $scope.$watch('push.cityId', function (newVal) {
            if (newVal) {
                $http.get("/admin/api/city/" + newVal + "/warehouses").success(function (data) {
                    $scope.warehouses = data;
                    $scope.warehouses.push({
                        "id": 0,
                        "name": "全城",
                        "city": {},
                        "displayName": "全城"
                    });
                });
            } else {
                $scope.warehouses = [];
            }
        })


        if ($stateParams.id) {
            $scope.isEditPush = true;

            $http.get("/admin/api/push/" + $stateParams.id)
                .success(function (data, status) {
                    $scope.push = data;

                })
                .error(function (data, status) {
                    window.alert("获取push信息失败...");
                });

        }

        $scope.createPush = function () {


            if ($stateParams.id != '' && $stateParams.id != undefined) {
                $http({
                    method: 'PUT',
                    url: '/admin/api/push/' + $stateParams.id,
                    data: $scope.push,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("修改成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("修改失败!");
                    })
            } else {


                $http({
                    method: 'POST',
                    url: '/admin/api/push/create',
                    data: $scope.push,
                    headers: {'Content-Type': 'application/json;charset=UTF-8'}
                })
                    .success(function (data, status, headers, config) {
                        alert("添加成功!");
                    })
                    .error(function (data, status, headers, config) {
                        alert("添加失败!");
                    })
            }
        }

    })
