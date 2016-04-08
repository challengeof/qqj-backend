'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:updateCustomerPassCtrl
 * @description
 * # updateCustomerPassCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('updateCustomerPassCtrl', function($scope, $http, $stateParams) {

 	$scope.updateCustomerPass = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/restaurant/updatePassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
 			if (data) {
 				window.alert("修改成功!");
 			} else {
 				window.alert("用户不存在!");
			}
 		})
 		.error(function(data) {
 			window.alert("修改失败!");
 		});
 	};
 });
