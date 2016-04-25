'use strict';

/**
 * @ngdoc directive
 * @name izzyposWebApp.directive:adminPosHeader
 * @description
 * # adminPosHeader
 */

angular.module('sbAdminApp')
    .directive('sidebar', ['$location', function () {
        return {
            templateUrl: 'app/directives/sidebar/sidebar.html',
            restrict: 'E',
            replace: true,
            scope: {},
            controller: function ($scope, $state, $rootScope, $filter) {
                $scope.selectedMenu = 'dashboard';
                $scope.collapseVar = 0;
                $scope.multiCollapseVar = 0;

                $scope.check = function (x) {

                    if (x == $scope.collapseVar)
                        $scope.collapseVar = 0;
                    else
                        $scope.collapseVar = x;
                };

                $scope.multiCheck = function (y) {

                    if (y == $scope.multiCollapseVar)
                        $scope.multiCollapseVar = 0;
                    else
                        $scope.multiCollapseVar = y;
                };

                $scope.goTicket = function () {
                    var uId = $rootScope.user.realname;
                    window.open("http://bm.canguanwuyou.cn/ticket/login&" + uId);
                }

                $scope.newTicket = function () {
                    var uId = $rootScope.user.realname;
                    var arr = {
                        "username": uId
                    };
                    arr = JSON.stringify(arr);
                    // console.log(arr);
                    arr = encodeURIComponent(arr);
                    window.open("http://bm.canguanwuyou.cn/ticket/newTicket?data=" + arr);
                }

                $scope.hasPermission = $rootScope.hasPermission;
                $scope.hasRole = $rootScope.hasRole;
                $scope.hasGlobalManager = $rootScope.hasGlobalManager;

                var newDate = new Date();
                newDate.setDate(newDate.getDate() + 1);
                $scope.startDate = $filter('date')(new Date(), 'yyyy-MM-dd');
                $scope.startTime = $filter('date')(new Date(), 'yyyy-MM-dd 00:00');
                $scope.endDate = $filter('date')(newDate, 'yyyy-MM-dd');
                $scope.endTime = $filter('date')(newDate, 'yyyy-MM-dd 00:00');

                var now = new Date();

                $scope.today = $filter('date')(now, 'yyyy-MM-dd');
                $scope.yesterday = $filter('date')(new Date().setDate(now.getDate() - 1), 'yyyy-MM-dd');
                $scope.tomorrow = $filter('date')(new Date().setDate(now.getDate() + 1), 'yyyy-MM-dd');
                $scope.firstDayOfMonth = $filter('date')(new Date(now.getFullYear(), now.getMonth(), 1), 'yyyy-MM-dd');
                $scope.lastDayOfMonth = $filter('date')(new Date(now.getFullYear(), now.getMonth() + 1, 0), 'yyyy-MM-dd');


                var now2 = new Date();
                $scope.firstDateTimeOfMonth = $filter('date')(new Date(now2.getFullYear(), now2.getMonth(),1), 'yyyy-MM-dd')+ " 00:00";
                $scope.firstDateTimeOfNextMonth = $filter('date')(new Date(now2.getFullYear(), now2.getMonth()+1,1), 'yyyy-MM-dd')+" 00:00";


                $scope.yesterdayDatetime = $filter('date')(new Date().setDate(now.getDate() - 1), 'yyyy-MM-dd')+ " 00:00";
                $scope.tomorrowDatetime = $filter('date')(new Date().setDate(now.getDate() + 1), 'yyyy-MM-dd')+ " 00:00";

            }
        }
    }]);