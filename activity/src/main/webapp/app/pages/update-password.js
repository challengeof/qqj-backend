'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:UpdatePwdCtrl
 * @description
 * # UpdatePwdCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
    .controller('UpdatePwdCtrl', function($scope, $http) {
    	$scope.reset = function(){
  			$scope.oldPassword = null;
	    	$scope.newPassword = null;
	    	$scope.repeatNewPassword = null;
  		}
  		
    	$scope.reset();

  		$scope.updatePassword = function(){
  			if($scope.newPassword != $scope.repeatNewPassword){
  				window.alert("请再次确认新密码！");
                return;
  			}else{
  				$http({
                        method: 'PUT',
                        url: '/api/admin-user/me/password',
                        params: {
                        	oldPassword: $scope.oldPassword,
                        	newPassword: $scope.newPassword
                        },
                        headers: {'Content-Type': 'application/json;charset=UTF-8'}
                    })
                    .success(function(data) {
                        window.alert("密码修改成功!");
                    })
                    .error(function(data) {
                        window.alert("密码修改失败!");
                    });
  			}
  		}
    });

