'use strict';
var app = angular.module("callerApp", ['oc.lazyLoad', 'ui.router', 'ui.bootstrap',
    'angular-loading-bar', 'checklist-model', 'angularFileUpload',
    'ui.select','xeditable','ui.map', 'ngMessages','ngJsTree', 'wt.responsive','ngResource']);


app.factory('UserService', ['$resource', function ($resource) {
    return $resource('/admin/api/admin-user/me', {}, {
        'profile': {
            method: 'GET'
        }
    });
}])



//app.config(function ($stateProvider, $urlRouterProvider) {
//        $urlRouterProvider.otherwise('/businessList.html');
//                    // $urlRouterProvider.when("", "/page1.html1");
//        $stateProvider.state("businessList", {
//                                url: "/businessList.html?activeTab",
//                                templateUrl: "/admin/caller/businessList.html",
//                                controller: "businessListController",
//                                resolve: {
//                                    loadMyFiles: function ($ocLazyLoad) {
//                                        return $ocLazyLoad.load({
//                                            name: 'callerApp',
//                                            files: [ '/admin/caller/businessList.js']
//                                        })
//                                    }
//                                }
//        } )
//        .state("orderlist", {
//                                url: '/orderlist.html?activeTab&page&pageSize&start&end&restaurantId&restaurantName&status&adminId&warehouseId&vendorName&cityId&organizationId&orderId&sortField&{asc:bool}&coordinateLabeled&refundsIsNotEmpty&telephone',
//                                templateUrl: "/admin/caller/orderlist.html",
//                                controller: "orderlistController",
//                                resolve: {
//                                    loadMyFiles: function ($ocLazyLoad) {
//                                        return $ocLazyLoad.load({
//                                            name: 'callerApp',
//                                            files: [ '/admin/caller/orderlist.js']
//                                        })
//                                    }
//                                }
//        })
//        .state("backcall", {
//            url: "/backcall.html?activeTab",
//            templateUrl: "/admin/caller/backcall.html"
//        })
//});

app.controller('callerInfo',function($scope, $http, $rootScope, $location, $window, $stateParams){
    $scope.searchPhoneVal=$scope.phone;
    $rootScope.searchPhone= $scope.searchPhoneVal;
    //$rootScope.activeTab=$stateParams.activeTab==null?1:$stateParams.activeTab;

    //$http.get(url).success(function(data, status, headers, config) {
    //        $scope.caller=data.caller;
    //
    //        $scope.restaurant=data.otherRestaurant;
    //        console.log(data);
    //
    //}).error(function(data, status, headers, config) {
    //        alert("数据加载失败");
    //});


    //来电人信息保存
    $scope.callerSave=function(){
        if (!!$scope.caller && typeof($scope.caller)!="undefined")
        {
            var param={id:$scope.caller.id,  phone:$rootScope.searchPhone,  name:$scope.caller.name,  detail:$scope.caller.detail};
            $http({
                url: "/admin/api/caller/update",
                method: "GET",
                params: param
            }).success(function (data, status, headers, config) {
                console.log(data);
                alert("保存成功");
            }).error(function (data, status, headers, config) {
                $window.alert("保存失败...");
            });
        }

    };

    //关联到此商户
    $scope.relationRestaurant=function(restaurant){


        $http({
            url: "/admin/api/caller/relationRestaurant/"+$rootScope.searchPhone+"/"+restaurant.id,
            method: "GET"
        }).success(function (data, status, headers, config) {
            $scope.caller=data.caller;

            $scope.restaurant=data.otherRestaurant;
            console.log(data);
        }).error(function (data, status, headers, config) {
            $window.alert("搜索失败...");
        });

        console.log(restaurant);
    };

    $scope.goRestaurantInfo=function(restaurantId){
        $rootScope.goTabPage(null,"/admin/#/oam/restaurant-list/?id="+restaurantId,null);
    }

    //弹出页到 呼叫中心页面的标签上
    $rootScope.goTabPage=function(name,url,titleimg){
        //titleimg=titleimg==null?"images/tab/bussniss.gif":titleimg;
//        if(window.parent.parent.parent.iframeAlerts!=null){
//            $window.parent.parent.parent.iframeAlerts(name, url,	titleimg);
//        }else{
//            $window.open(url);
//        }

        $window.open(url);
    };

    $scope.goBack=function(){

        $window.location.href=document.referrer;
    }
    $scope.resetLoad=function(phone,needResetCaller){

        $rootScope.searchPhone=phone;
        $scope.searchPhone=phone;
        $scope.searchPhoneVal=phone;

    }

    $scope.infoLoad=function(phone, needLoadCaller){
        var url='/admin/api/caller/'+phone;
        $.ajax({
            url: url, async: false,  cache: false,
            success: function(content) {
                if(!!needLoadCaller){
                    $rootScope.caller=content.caller;
                }

                $rootScope.restaurant=content.otherRestaurant;
                console.log(content);
            },
            error: function(content){
                alert("数据加载失败");
            }
        });
    };
    //$scope.$parent.$watch("searchPhone",function(newval){
    //    $scope.infoLoad(newval);
    //})

    $rootScope.newTicketByPhone = function(consultName,consultTel){


        var arr = {
            "username": $rootScope.user.realname,
            "tel": {consultName:consultName==null?"":consultName,consultTel:consultTel==null?"":consultTel}
        };
        console.log(arr);
        arr = JSON.stringify(arr);
        arr = encodeURIComponent(arr);

        $rootScope.goTabPage("发起工单","http://bm.canguanwuyou.cn/ticket/newTicket?data="+arr,null);

    }


    $rootScope.NewTicket = function(order){

        var arr = {
            "username": $rootScope.user.realname,
            "info": order
        };
        arr = JSON.stringify(arr);
        // console.log(arr);
        arr = encodeURIComponent(arr);
        console.log(arr)

        $rootScope.goTabPage("发起工单","http://bm.canguanwuyou.cn/ticket/newTicket?data="+arr,null);
    }
    $scope.$watch('searchPhone', function(newval) {
        $scope.infoLoad(newval);
    });

    $scope.infoLoad($rootScope.searchPhone,true);


}).run(function ($rootScope, $location, UserService) {
    UserService.profile(function (user) {
        if(user!=null && user.realname!=null){
            $rootScope.user = user;
        }
    });
});

'use strict';

angular.module('callerApp') .controller('businessListController',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window, $document) {
        //$("#businesslistFrame2").attr("src","http://www.canguanwuyou.cn/ticket/login&"+$rootScope.user.realname);
        //
        //$("#businesslistFrame2").load(function(){
        //    //在登陆后 请求form进行搜索
        //    $("#businesslistFrameForm").submit();
        //});
        //
        //$("#businesslistFrame").load(function(){
        //    $("#businesslistFrameloadingTip").hide();
        //});

        $scope.iframeGo=function(tel){
            //$scope.businessForm.restaurantTel.split(",").forEach(function(e){
            //    if(e!=null && $.trim(e).length!=0){
            //        restaurantTel.push($.trim(e));
            //        consultTel.push($.trim(e));
            //    }
            //})
            var consultTel =[$.trim(tel) ];
            var restaurantTel = [$.trim(tel) ];
            //if($.trim(tel)!=$.trim($rootScope.phone)){
            //    restaurantTel.push($.trim(tel));
            //    consultTel.push($.trim(tel));
            //}
            var arr = {
                "username": $rootScope.user.realname,
                "info": {restaurantTel:restaurantTel, consultTel: consultTel}
            };
            console.log(arr);
            arr = JSON.stringify(arr);
            arr = encodeURIComponent(arr);

            var src = "http://bm.canguanwuyou.cn/ticket/searchTel?data="+arr;
            $("#businesslistFrame").attr("src",src);
        }

        $scope.$watch('searchPhone', function(newval) {
            $scope.iframeGo(newval);
        });


    });

'use strict';
angular.module('callerApp') .controller('orderlistController',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window) {

        //$scope.openStart = function ($event) {
        //    $event.preventDefault();
        //    $event.stopPropagation();
        //    $scope.openedStart = true;
        //};
        //
        //$scope.openEnd = function ($event) {
        //    $event.preventDefault();
        //    $event.stopPropagation();
        //    $scope.openedEnd = true;
        //};
        //
        //$scope.dateOptions = {
        //    dateFormat: 'yyyy-MM-dd',
        //    formatYear: 'yyyy',
        //    startingDay: 1,
        //    startWeek: 1
        //};
        //
        //$scope.format = 'yyyy-MM-dd';
        //$scope.date = new Date().toLocaleDateString();
        //
        //$scope.coordinateLabeleds = [{key:0,value:"坐标缺失"},{key:1,value:"坐标已标注"}];
        //$scope.refundsIsNotEmptys = [{key:true,value:"有退货的订单"}];

        $scope.page = {
            itemsPerPage: 100
        };
        /*订单状态*/
        $http.get("/admin/api/order/status")
            .success(function (data, status, headers, config) {
                $scope.availableStatus = data;
            }).error(function (data, status) {
                alert("订单状态加载失败！");
            });

        if($rootScope.user) {
            var data = $rootScope.user;
            $scope.cities = data.cities;
        }

        $scope.$watch('orderListSearchForm.cityId',function(newVal,oldVal){
            if(newVal){
                $http.get("/admin/api/city/" + newVal+"/organizations").success(function(data) {
                    $scope.organizations = data;
                });
                $http.get("/admin/api/city/"+newVal+"/warehouses").success(function(data) {
                    $scope.availableWarehouses = data;
                });
                if(typeof oldVal != 'undefined' && newVal != oldVal){
                    $scope.orderListSearchForm.organizationId = null;
                    $scope.orderListSearchForm.warehouseId = null;
                }
            }else{
                $scope.organizations = [];
                $scope.availableWarehouses = [];
                $scope.orderListSearchForm.organizationId = null;
                $scope.orderListSearchForm.warehouseId = null;
            }
        });

        $scope.$watch('orderListSearchForm.organizationId',function(newVal,oldVal){
            //选择销售
            if(newVal){
                $http({
                    method:"GET",
                    url:"/admin/api/admin-user/global?role=CustomerService",
                    params:{organizationId:newVal}
                }).success(function(data){
                    $scope.adminUsers = data;
                })
            }

        });

        /*订单列表搜索表单*/
        $scope.order = {};
        $scope.orders = {};
        $scope.orderListSearchForm = {
            //page: $stateParams.page,
            //pageSize: $stateParams.pageSize,
            //start: $stateParams.start,
            //end: $stateParams.end,
            //customerId: $stateParams.customerId,
            //restaurantId: null,
            //restaurantName: $stateParams.restaurantName,
            //warehouseId: $stateParams.warehouseId,
            //vendorName:$stateParams.vendorName,
            //cityId:$stateParams.cityId,
            //organizationId:$stateParams.organizationId,
            //vendorId: $stateParams.vendorId,
            //orderId:$stateParams.orderId,d
            //coordinateLabeled:$stateParams.coordinateLabeled,
            //refundsIsNotEmpty:$stateParams.refundsIsNotEmpty,
            //telephone: $rootScope.searchPhone
        };




        $scope.resetPageAndSearchOrderList = function () {
            $scope.orderListSearchForm.page = 0;
            $scope.orderListSearchForm.pageSize = 100;
            $scope.searchOrderList();
        }


        $scope.pageChanged = function() {
            $scope.orderListSearchForm.page = $scope.page.currentPage - 1;
            $scope.orderListSearchForm.pageSize = $scope.page.itemsPerPage;

            $scope.searchOrderList();
        }

        $scope.deliverNow =  function(order) {
            $http.post("/admin/api/order/" + order.id + "/deliver")
                .success(function (data, status, headers, config) {
                    order.status.name = data.status.name;
                    order.status.value = data.status.value;
                    window.alert("修改成功");
                });
        }

        $scope.cancelOrder = function(order) {
            $http.post("/admin/api/order/" + order.id + "/cancel")
                .success(function (data) {
                    order.status.name = data.status.name;
                    order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }



        $scope.completeOrder = function(order) {
            $http.post("/admin/api/order/" + order.id + "/complete")
                .success(function (data) {
                    order.status.name = data.status.name;
                    order.status.value = data.status.value;
                    window.alert("操作成功!")
                })
        }

        $scope.searchOrderList = function (param) {
            $scope.loadOrder(param);
        }
        $scope.loadOrder=function(param){
            if(param!=null) {
                param = angular.extend(param, $scope.orderListSearchForm);
            }else{
                param = $scope.orderListSearchForm;
            }
            $http({
                url: '/admin/api/order',
                method: "GET",
                params: param
            }).success(function (data, status, headers, config) {
                $scope.orders = data.orders;
                $scope.count = data.total;
                $scope.orderStatistics = data.orderStatistics;
                console.log($scope.page.currentPage);

                /*分页数据*/
                $scope.page.itemsPerPage = data.pageSize;
                $scope.page.totalItems = data.total;
                $scope.page.currentPage = data.page + 1;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        };

        $scope.$watch('searchPhone', function(newVal) {
            $scope.page.currentPage=null;
            $scope.loadOrder({"telephone":newVal});
        });
        //$scope.loadOrder({"telephone":$scope.searchPhone});
    });
