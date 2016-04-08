'use strict';

angular.module('sbAdminApp')
	.controller('PushListCtrl', function($scope, $http) {

		$scope.dateOptions = {
			dateFormat: 'yyyy-MM-dd',
			formatYear: 'yyyy',
			startingDay: 1,
			startWeek: 1
		};

		$scope.format = 'yyyy-MM-dd';

		$scope.date = new Date().toLocaleDateString();

		$http.get("/admin/api/pushes")
		.success(function(data){
			$scope.pushes = data;
		})
		.error(function(data){

		});
	});