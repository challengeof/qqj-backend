'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:admin-updatePassword
 * @description
 * # admin-updatePassword
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('updateAdminPassCtrl', function($scope, $http, $stateParams) {

 	$scope.updateAdminPass = function() {
 		$http({
 			method: 'POST',
 			url: '/api/admin-user/updateAdminPassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
 			if (data) {
 				window.alert("修改成功!");
 			} else {
 				window.alert("管理员不存在!");
			}
 		})
 		.error(function() {
 			window.alert("修改失败!");
 		});
 	};
 });
