'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:SalesDistributeCtrl
 * @description
 * # SalesDistributeCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('SalesDistributeCtrl', function($scope, $http, $stateParams) {
 	/*分配销售表单数据集*/
 	$scope.adminUserId = 0;
 	$scope.formData = {};

 	/*获取销售注册号*/
 	if ($stateParams.id != "") {
 		$http.get("/admin/api/restaurant/" + $stateParams.id)
 		.success(function(data, status, headers, config) {
 			$scope.telephone = data.telephone;

 			if (data.customer.adminUser) {
 				$scope.formData.adminUserId = data.customer.adminUser.id;
 			}
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("获取失败！");
 		});
 	}

 	/*获取销售*/
 	$http.get("/admin/api/admin-user/global")
 	.success(function(data) {
 		$scope.adminUsers = data;
 	})

 	/*分配销售表单提交请求*/
 	$scope.assignAdminUser = function() {
 		$http({
 			method: 'PUT',
 			url: '/admin/api/restaurant/' + $stateParams.id + '/admin-user',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("分配成功!");
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("分配失败！");
 		});
 	};
 });
