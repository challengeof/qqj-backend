angular.module('sbAdminApp')
    .controller('ListBlockCtrl', function ($scope, $rootScope, $http, $stateParams, $filter, $location) {

        $scope.blockListSearchForm = {
                    page: $stateParams.page,
                    pageSize:$stateParams.pageSize,

                    blockId:$stateParams.blockId,
                    blockName:$stateParams.blockName,
                    warehouseId:$stateParams.warehouseId,
                    enable:$stateParams.status,
                    cityId:$stateParams.cityId
         };

       if($rootScope.user) {
            var data = $rootScope.user;
             $scope.availableCities = data.cities;  
        }

       $scope.page={
            blocksPerPage:100
       }

        $scope.resetPageAndSearchBlockList = function () {
            $scope.blockListSearchForm.page = 0;
            $scope.blockListSearchForm.pageSize = 100;
            $scope.SearchBlockList();
        }
          $scope.SearchBlockList = function () {
                     $http({
                         url: '/admin/api/block',
                         method: "GET",
                         params: $scope.blockListSearchForm
                     }).success(function (data, status, headers, config) {
                         $scope.blocks = data.blocks;
                         /*分页数据*/
                          $scope.page.blocksPerPage = data.pageSize;
                          $scope.page.totalBlocks = data.total;
                         $scope.page.currentPage = data.page + 1;
                     }).error(function (data, status, headers, config) {
                         window.alert("搜索失败...");
                     });
           }
            $scope.SearchBlockList();

                  $scope.updateBlock = function(block ,active){
                   	$http({
                           method: 'put',
                           url: '/admin/api/block/' + block.id,
                           data: 'active=' + active,
                           headers: {
                               'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                           }
                       })
                       .success(function(data) {
                       	window.alert("保存成功!");
                           block.active = data.active;
                       })
                       .error(function(data) {
                       	window.alert("保存失败!");
                       });
                   }

                   $scope.editSaveBlock = function(warehouse, block) {
                       var saveBlockWarehouse = function(){
                           $http({
                                  method: 'PUT',
                                  url: '/admin/api/block/' + block.id,
                                  data: 'warehouseId=' + warehouse.warehouseId,
                                  headers: {
                                      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                                  }
                              })
                              .success(function(data) {
                                   block.warehouse.id = data.warehouse.id;
                                   block.warehouse.name = data.warehouse.name;
                                   window.alert("修改成功!");
                              })
                              .error(function(data) {
                                   window.alert("修改失败!");
                              });
                       }

                       return saveBlockWarehouse();
                   };


        $scope.$watch('blockListSearchForm.cityId',function(newVal,oldVal){
                if(newVal != null && newVal != ""){
                    $http.get("/admin/api/city/"+newVal+"/warehouses").success(function(data) {
                        $scope.availableWarehouses = data;
                    });

                    if(typeof oldVal != 'undefined' && newVal != oldVal){
                        $scope.blockListSearchForm.warehouseId = null;
                    }
                }else{
                    $scope.availableWarehouses = [];
                    $scope.blockListSearchForm.warehouseId = null;
                }
                $scope.SearchBlockList();

        })


          /**分页数据*/
          $scope.pageChanged = function() {
                    $scope.blockListSearchForm.page = $scope.page.currentPage - 1;
                    $scope.blockListSearchForm.pageSize = $scope.page.blocksPerPage;

                    $scope.SearchBlockList();
           }

    });
