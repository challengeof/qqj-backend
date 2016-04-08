'use strict';

angular.module('sbAdminApp')
    .directive('back', ['$window', function ($window) {
        return {
            restrict: 'A',

            link: function (scope, element, attrs) {
                element.bind('click', function () {
                    $window.history.back();
                });
            }
        };
    }]);