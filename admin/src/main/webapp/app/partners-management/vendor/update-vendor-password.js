'use strict';
 angular.module('sbAdminApp')
 .controller('updateVendorPasswordCtrl', function($scope, $http, $stateParams) {

 	$scope.updateVendorPassword = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/vendor/updateVendorPassword',
 			params: $scope.formData,
 			headers: {
 				'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
 			}
 		})
 		.success(function(data) {
			alert("修改成功!");
 		})
 		.error(function() {
 			alert("修改失败!");
 		});
 	};
 });
