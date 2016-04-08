'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:EditRestaurantCtrl
 * @description
 * # EditRestaurantCtrl
 * Controller of the sbAdminApp
 */
 angular.module('sbAdminApp')
 .controller('EditRestaurantCtrl', function($scope, $rootScope, $http, $stateParams) {

 	if($rootScope.user) {
		var data = $rootScope.user;
		 $scope.cities = data.cities;
	}

    $http.get("/admin/api/restaurant/status")
     .success(function(data) {
         $scope.availableStatus = data;
     });

 	$http.get("/admin/api/restaurantType/parent")
	  .success(function(data) {
		  $scope.restaurantType = data;
	  });

	 $http.get("/admin/api/restaurant/reasons")
		 .success(function(data) {
			 $scope.restaurantReasons = data;
		 });


 	/*修改餐馆-表单数据集*/
 	$scope.formData = {
 	};
    $scope.restaurant = {};


	$scope.$watch('formData.type', function(newVal, oldVal){
		if (newVal != null && newVal != '') {
			$http({
				method:"GET",
				url:"/admin/api/restaurantType/"+ newVal +"/child",
				params: {status:1}
			})
			.success(function(data, status, headers, config) {
				$scope.restaurantType2 = data;
				if (data.length == 1) {
					$scope.formData.type2 = data[0].id;
				}
			})
			.error(function(data, status, headers, config) {
				window.alert("加载失败！");
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.type2 = null;
			}
		} else {
			 $scope.restaurantType2 = [];
			 $scope.formData.type2 = null;
		}
	});

	$scope.$watch('formData.cityId', function(newVal, oldVal){

		if(newVal != null && newVal != '') {
			$http({
				method:"GET",
				url: "/admin/api/city/"+ newVal +"/blocks",
				params: {status:true},
				headers: {
					'Content-Type': 'application/json;charset=UTF-8'
				}
			})
			.success(function(data, status, headers, config) {
				$scope.blocks = data;
				if (data.length == 1) {
					$scope.formData.blockId = data[0].id;
				}
			})
			.error(function(data, status, headers, config) {
				window.alert("加载失败！");
			});

			if (typeof oldVal != 'undefined' && newVal != oldVal) {
				$scope.formData.blockId = null;
			}
		} else {
			$scope.blocks = [];
			$scope.formData.blockId = null;
		}
	});

 	/*餐馆编辑信息view*/
 	if ($stateParams.id != "") {
 		$http.get("/admin/api/restaurant/" + $stateParams.id)
 		.success(function(data, status, headers, config) {

            $scope.restaurant = data;

 			$scope.formData.name = data.name;

			if (data.customer) {
				$scope.formData.cityId = data.customer.cityId;
			}

			if (data.customer.block) {
				$scope.formData.blockId = data.customer.block.id;
				$scope.formData.cityId = data.customer.block.city.id;
			}

 			$scope.formData.contact = data.receiver;
 			$scope.formData.telephone = data.telephone;


			if (data.address) {
 				$scope.formData.address = data.address.address;
			    $scope.formData.streeNumer = data.address.streeNumer;

				if (data.address.wgs84Point) {
					$scope.formData.wgs84Point =  data.address.wgs84Point.longitude + "," + data.address.wgs84Point.latitude;
				};
			}

			if (data.status) {
 				$scope.formData.status = data.status.value;
			}
			if (data.type) {
 				$scope.formData.type = data.type.parentRestaurantTypeId;
 				$scope.formData.type2 = data.type.id;
			}
			if (data.restaurantReason) {
				$scope.formData.restaurantReason = data.restaurantReason.value;
			}
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("加载失败！");
 		});
 	}

 	/*修改餐馆-提交表单数据请求update*/
 	$scope.updateRestaurant = function() {
 		$http({
 			method: 'PUT',
 			url: '/admin/api/restaurant/' + $stateParams.id,
 			data: $scope.formData,
 			headers: {
 				'Content-Type': 'application/json;charset=UTF-8'
 			}
 		})
 		.success(function(data, status, headers, config) {
 			window.alert("提交成功!");
 		})
 		.error(function(data, status, headers, config) {
 			window.alert("提交失败！");
 		});
 	};
 })
