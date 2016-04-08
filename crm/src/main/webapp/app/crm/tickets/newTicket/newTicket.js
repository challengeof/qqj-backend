'use strict';
angular.module('sbAdminApp')
    .controller('NewTicketCtrl', function ($rootScope,$scope,$http,$state) {
        $scope.candidateRestaurants = [];
        $scope.restaurant = {};

        $scope.formData = $scope.formData;

        //来源
        $scope.problemSources=[];
        $http({
            url: $rootScope.rootPath+"/api/work-ticket/problemSources",
            method: 'GET'
        }).success(function(data) {
            $scope.problemSources = data;
            console.log(data);
        })

        //流程
        $scope.processes=[];
        $http({
            url: $rootScope.rootPath+"/api/work-ticket/process",
            method: 'GET'
        }).success(function(data) {
            $scope.processes = data;
        })

        //提交
        $scope.submit=function() {

            //流程对应的详细信息
            $scope.formData.content = JSON.stringify($scope.formData.content);
            $scope.formData.status = 0;
            $scope.formData.restaurantId = $scope.restaurant.id;
            $scope.formData.restaurantTelephone = $scope.restaurant.telephone;
            $http.post(
                $rootScope.rootPath + "/api/work-ticket/create",
                $scope.formData
            ).then(
                function(){
                    alert('提交成功！');
                    $scope.formData = {};
                    $scope.formData.process=1;
                    $scope.restaurant={};
                    $scope.processChange(1);
                },
                function(){
                    alert('提交失败！');
                }
            );
        }

        $scope.processChange=function(process) {
            switch(process){
                case 1:
                    $state.go('crm.newTicket.consultType-one');
                    break;
                case 2:
                    $state.go('crm.newTicket.consultType-two');
                    break;
                case 3:
                    $state.go('crm.newTicket.consultType-three');
                    break;
                case 4:
                    $state.go('crm.newTicket.consultType-four');
                    break;
                case 5:
                    $state.go('crm.newTicket.consultType-five');
                    break;
                case 6:
                    $state.go('crm.newTicket.consultType-six');
                    break;
                case 7:
                    $state.go('crm.newTicket.consultType-seven');
                    break;
                case 8:
                    $state.go('crm.newTicket.consultType-eight');
                    break;
            }
        }

        $scope.resetCandidateRestaurants = function () {
            $scope.candidateRestaurants = [];
        }

        $scope.searchRestaurant = function(restaurant) {
            console.log(restaurant);
            $scope.candidateRestaurants = [];
            if(undefined != restaurant && undefined != restaurant.id){
                $http.get("/crm/api/restaurant/" + restaurant.id)
                    .success(function (data, status, headers, config) {
                        if (!data) {
                            alert('餐馆不存在或已失效');
                            restaurant.id = '';
                            return;
                        }
                        $scope.candidateRestaurants.push(data);
                        $scope.restaurant = data;
                        console.log(data);
                    }).error(function (data, status, headers, config) {
                    alert('餐馆不存在或已失效');
                    restaurant.id = '';
                    return;
                });
            }else{
                $scope.restaurant= {};
                return;
            }
        }

        //输入餐馆名称
        $scope.funcAsyncRestaurant = function (name) {
            if (name && name !== "") {
                $scope.candidateRestaurants = [];
                $http({
                    url: "/crm/api/restaurant/candidates",
                    method: 'GET',
                    params: {page: 0, pageSize: 20, name: name, showLoader:false}
                }).then(
                    //$http.get("/admin/api/restaurant/candidates?page=0&pageSize=20&name="+name).then(
                    function (data) {
                        $scope.candidateRestaurants = data.data;
                    }
                )
            }
        }

        //待跟进人
        $http.get("/crm/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.newAdminUsers = data;
            });

        //输入餐馆名称
        $scope.searchDai = function (adminUser) {
            console.log(adminUser);
            if(adminUser!=undefined){
                $scope.formData.daiphone = adminUser.telephone;
            }else{
                $scope.formData.daiphone = '';
            }
        }

        //所属市场
        $http.get("/crm/api/warehouse/")
            .success(function (data) {
                $scope.warehouses = data;
            });


        $scope.searchRestaurantByUsername = function(username) {
            $scope.candidateRestaurants = [];
            if(username == '' || username == undefined) {
                return;
            }
            $http.get("/crm/api/restaurant/username/" + username).success(function (data, status, headers, config) {
                if (!data) {
                    alert('餐馆不存在或已失效');
                    $scope.formData.username = '';
                    $scope.restaurant = '';
                    return;
                }
                $scope.candidateRestaurants.push(data);
                $scope.restaurant = data;
            }).error(function (data, status, headers, config) {
                alert('餐馆不存在或已失效');
                $scope.formData.username = '';
                $scope.restaurant = '';
                return;
            });
        };




    });
