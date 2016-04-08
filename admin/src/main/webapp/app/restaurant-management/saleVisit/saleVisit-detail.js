'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('AddsaleVisitStaffCtrl', function($scope, $rootScope, $http, $stateParams, $state) {
 	
 	$http.get("/admin/api/saleVisit/status")
 	.success(function(data) {
 		$scope.saleVisitStatus = data;
 	});

 	$scope.formData = {
 		restaurantId : $stateParams.restaurantId
 	};

	$scope.$watch('formData.status', function(newVal, oldVal){

		if(newVal != null && newVal != '') {
			$http.get("/admin/api/saleVisit/status/" + newVal + "/reason")
			.success(function (data, status, headers, config) {
				if (data) {
					$scope.reasons = data;
				} else {
					$scope.reasons = [{name : "无",value : -1}];
					$scope.formData.reasonId = -1;
				}
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.reasonId = null;
			}
		} else {
			$scope.reasons = [];
			$scope.formData.reasonId = null;
		}
	});

 	$scope.createSaleVisit = function() {
 		$http({
 			method: 'POST',
 			url: '/admin/api/saleVisit',
 			data: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("提交成功!");
 			$state.go("oam.saleVisit-list", {"restaurantId":$scope.formData.restaurantId});

 		})
 		.error(function(data, status, headers, config) {
 			window.alert("提交失败！");
 		});
 	};
 })
