/**
 * Created by challenge on 16/4/26.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('BarcodeCtrl', function ($scope, $http, $filter, $state) {


        $scope.bagCode = "";
        $scope.boxCode = "";
        document.getElementById('check-bagCode-input').onkeydown = function( e ) {
            if ( e.keyCode === 13 ) {
                $scope.addToCheckBagCode();
            }
        }

        document.getElementById('check-boxCode-input').onkeydown = function( e ) {
            if ( e.keyCode === 13 ) {
                $scope.addToCheckBoxCode();
            }
        }
        $scope.addToCheckBagCode = function () {

            if ($scope.bagCode === "") {

                return;
            } else {
                $scope.request.barcodeItems.push($scope.bagCode);
                $scope.bagCode = "";
                return;
            }
        }
        $scope.addToCheckBoxCode = function () {

            if ($scope.boxCode === "") {
                alert("空")
                return;
            } else {
                $scope.request.boxCode = $scope.boxCode;
                console.log($scope.request);
                $scope.boxCode = "";
            }
        }

        $scope.create = function () {

            $http({
                url: "/api/barcode/create",
                method: "POST",
                data: $scope.request
            }).success(function (data) {
                $scope.init();
                alert("success")
                $("#check-bagCode-input").focus();
            }).error(function (data) {
                alert("error")
            })

        };

        $scope.remove = function (index) {
            $scope.request.barcodeItems.splice(index, 1);
            if ($scope.request.barcodeItems.length == 0) {
                $scope.init();
            }
        }

        $scope.init = function () {
            $scope.request = {

                barcodeItems: [],
                boxCode: '',
                expressNo: ''
            }
            $("#check-bagCode-input").focus();
        };
    });
