'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:AddMemoCtrl
 * @description
 * # AddMemoCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('AddMemoCtrl',function($scope, $http, $stateParams){
		/*添加备注表单数据集*/
 		$scope.addMemoForm = {
 			id: $stateParams.id
 		};

 		/*根据订单id获取已添加的备注信息*/
 		$http.get("/admin/api/order/" + $stateParams.id)
 		.success(function(data,status,headers,config){
			$scope.addMemoForm.memo = data.memo;
 		})
 		.error(function(data,status,headers,config){
 			window.alert("获取失败...");
 		})

 		/*添加备注表单提交请求*/
 		$scope.addMemoFun = function(){
		    if($stateParams.id != ""){
				$http({
					method: 'PUT',
					url: '/admin/api/order/' + $stateParams.id,
					data: $scope.addMemoForm,
					headers: {
						'Content-Type': 'application/json;charset=UTF-8'
					}
				})
				.success(function(data,status,headers,config){
					window.alert("添加成功!");
				})
				.error(function(data,status,headers,config){
					window.alert("添加失败...");
				});
			}
		};

	});