angular.module('sbAdminApp')
    .controller('AddBlockCtrl', function($scope, $rootScope, $state, $stateParams, $http) {

        if($rootScope.user) {
            var data = $rootScope.user;
             $scope.cities = data.cities;
        }
        $scope.formData = {

        };

         $scope.$watch('formData.cityId', function(cityId) {
             if(cityId != null && cityId != '') {
                 $http.get("/admin/api/city/"+cityId+"/warehouses").success(function(data) {
                     $scope.warehouses = data;
                 });
             } else {
                 $scope.warehouses = [];
             }
         });
        $scope.isEdit = false;
        if ($stateParams.id) {
            $scope.isEdit = true;
            /* 用户角色 */
            $http.get("/admin/api/block/" + $stateParams.id).success(function(data) {
                $scope.formData.blockName = data.name;
                $scope.formData.cityId = data.city.id;
                $scope.formData.warehouseId = data.warehouse.id;
                $scope.formData.active = data.active;
                $scope.formData.pointStr = data.pointStr;
            });
        }

        $scope.createBlock = function() {
            if($stateParams.id == "") {
                $http({
                    method: 'post',
                    url: '/admin/api/block',
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                })
                    .success(function (data) {
                        alert("保存成功!");
                    })
                    .error(function (data) {
                        alert("保存失败!");
                    });
            }else{
                $http({
                    method: 'put',
                    url: '/admin/api/block/' + $stateParams.id,
                    data: $scope.formData,
                    headers: {
                        'Content-Type': 'application/json;charset=UTF-8'
                    }
                }).success(function(data) {
                    alert("修改成功！");
                }).error(function(data) {
                    alert("修改失败!");
                });

            }
        }
        /*表单重置*/
        $scope.resetAdminForm = function(){
            $scope.formData = {
                blockIds: []
            };
            $scope.repeatPassword = null;
            $scope.isCheckedAll = false;
        }

    });
