'use strict';

angular.module('sbAdminApp')
	.controller('ExcelExportTaskList', function($scope, $rootScope, $http, $filter, $stateParams, $location, $window, $interval) {

		$scope.searchForm = {};

		$scope.search = function() {
			$http({
				url: "/admin/api/task/excel/myTaskList",
				method: "GET",
				params: $scope.searchForm
			})
			.success(function (data, status, headers, config) {
				$scope.taskList = data;
			})
		}

		$scope.search();

		var timer = $interval(function() {
			$scope.search();
		}, 10 * 1000, 20);

		$scope.downLoad = function(taskId){
			$window.open("/admin/api/task/excel/download?taskId="+taskId);
		}

		$scope.$on('$destroy', function () { $interval.cancel(timer); });
	});