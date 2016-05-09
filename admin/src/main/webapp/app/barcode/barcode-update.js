/**
 * Created by challenge on 16/4/26.
 */
'use strict';

angular.module('sbAdminApp')
    .controller('BarcodeUpdateCtrl', function ($scope, $http, $filter, $stateParams) {

        $scope.iNotify = new iNotify({
            audio:{

                file:'audio/audio.wav'
            }
        })
        $scope.expressNo = "";
        $scope.boxCode = "";
        $scope.barcode = {};
        document.getElementById('check-expressNo-input').onkeydown = function (e) {
            if (e.keyCode === 13) {
                $scope.addToCheckExpressNo();
                $("#submit").focus();
            }
        }

        document.getElementById('check-boxCode-input').onkeydown = function (e) {
            if (e.keyCode === 13) {
                $scope.searchBarcodeByBoxCode();
            }
        }
        $scope.searchBarcodeByBoxCode = function () {

            if ($scope.request.boxCode === "") {

                return;
            } else {
                $http.get("api/barcode/query/" + $scope.request.boxCode).success(function (data) {
                    if (data == null || data == undefined || data == "") {
                        $scope.iNotify.player();
                        alert("没有此箱码,请重新输入");
                        $scope.request.boxCode = "";

                    } else {
                        $scope.barcode = data;
                        $scope.request.id = data.id;
                        $("#check-expressNo-input").focus();
                    }

                });

            }
        }
        $scope.addToCheckExpressNo = function () {
            if ($scope.barcode.expressNo != '') {
                $scope.iNotify.player();
                var tag = confirm("此箱已经存在快递单号,确认要修改么?");
                if (tag == true) {
                    alert("You pressed OK!");
                }
                else {
                    $scope.init();
                    console.log($scope.request);
                }
            }
        }


        $scope.update = function () {
            if ($scope.request.id == '') {
                alert("xxxxx");
                $("#check-expressNo-input").focus();

            }else{

                $http({
                    url: "/api/barcode/update",
                    method: "PUT",
                    data: $scope.request
                }).success(function (data) {
                    $scope.init();
                    alert("success")
                    $scope.barcode = data;
                    $("#check-boxCode-input").focus();
                }).error(function (data) {
                    alert("error")
                })
            }

        };
        $scope.init = function () {
            $scope.request = {
                id : '',
                boxCode: '',
                expressNo: ''
            }
            $("#check-boxCode-input").focus();
        };

        $scope.playonline = function(url){

        }

    });
