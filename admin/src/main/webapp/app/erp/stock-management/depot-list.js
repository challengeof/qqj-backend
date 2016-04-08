'use strict';
/**
 * @ngdoc function
 * @name sbAdminApp.controller:ListDepotCtrl
 * @description
 * # ListDepotCtrl
 * Controller of the sbAdminApp
 */
angular.module('sbAdminApp')
	.controller('ListDepotCtrl', function($scope, $rootScope, $http, $stateParams) {

	    $scope.depots = {};
	    $scope.formData = {
            cityId : $stateParams.cityId
        };

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

		$scope.searchForm = function() {
			$http({
				url : "/admin/api/depot/list",
				method : 'GET',
				params: $scope.formData,
				headers: {'Content-Type': 'application/json;charset=UTF-8'}
			}).success(function(data){
				$scope.depots = data;
			}).error(function(data){
                window.alert("加载失败...");
			});
		}

		$scope.updateDepot = function (depot, isMain) {
			$http({
				url : "/admin/api/depot/main/" + depot.id,
				method : 'PUT',
			}).success(function(data){
				$scope.depots = data;
			}).error(function(data){
				window.alert("加载失败...");
			});
		}

		$scope.searchForm();
	});