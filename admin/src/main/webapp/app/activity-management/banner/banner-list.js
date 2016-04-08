'use strict';

angular.module('sbAdminApp')
	.controller('BannerListCtrl', function($scope, $http) {
		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

		$scope.date = new Date().toLocaleDateString();

		$scope.cityId = 0;

		$scope.$watch('cityId',function(){
			$http.get("/admin/api/banners/"+$scope.cityId)
				.success(function(data){
					$scope.banners = data;
				})
				.error(function(data){

				});
		});


		//获取城市
		$http.get("/admin/api/admin-user/me")
			.success(function (data, status, headers, config) {
				$scope.cities = data.cities;
			});

		/*$http({
			method:'POST',
			url:'/admin/api/banner',
			data:$scope.banner,
			headers: {'Content-Type': 'application/json;charset=UTF-8'}


		});*/
	});