'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:zoneListCtrl
 * @description
 * # zoneListCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('zoneListCtrl', function ($scope, $http, $stateParams, editableOptions) {
        editableOptions.theme = 'bs3';

    	$scope.zoneList = [];

        $http.get("/admin/api/zone")
            .success(function (data, status, headers, config) {
                $scope.zoneList = data;
            })
            .error(function (data, status) {
                alert("数据加载失败！");
            });

        $scope.updateZone = function(zone ,active){
        	$http({
                method: 'put',
                url: '/admin/api/zone/' + zone.id,
                data: 'active=' + active,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                }
            })
            .success(function(data) {
            	window.alert("保存成功!");
                zone.active = data.active;
            })
            .error(function(data) {
            	window.alert("保存失败!");
            });
        }

        /*商圈-市场*/
        $http.get("/admin/api/warehouse")
            .success(function (data, status, headers, config) {
                $scope.availableWarehouses = data;
            });

        $scope.editSaveZone = function(warehouse, zone) {
            var saveZoneWarehouse = function(){
                $http({
                       method: 'PUT',
                       url: '/admin/api/zone/' + zone.id,
                       data: 'warehouseId=' + warehouse.warehouseId,
                       headers: {
                           'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                       }
                   })
                   .success(function(data) {
                        zone.warehouse.id = data.warehouse.id;
                        zone.warehouse.name = data.warehouse.name;
                        window.alert("修改成功!");
                   })
                   .error(function(data) {
                        window.alert("修改失败!");
                   });
            }

            return saveZoneWarehouse();
        };
    })

