'use strict';
angular.module('callerApp') .controller('list',
    function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window, $state) {


        $scope.format='yyyy-MM-dd';

        $scope.createDateOpen = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.createdateOptions = true;
            $scope.modifydateOptions=false;
        };

        $scope.modifyDateOpen = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.createdateOptions = false;
            $scope.modifydateOptions= true;
        };

        $scope.dateOptions = {
            dateFormat: 'yyyy-MM-dd',
            formatYear: 'yyyy',
            startingDay: 1,
            startWeek: 1
        };

        $scope.format = 'yyyy-MM-dd';
        $scope.date = new Date().toLocaleDateString();



        $scope.goSearch = function () {
            console.log($scope.searchForm);
            $state.go("list",$scope.searchForm);
        }

        $scope.pageChanged = function() {
            $scope.searchForm.page = $scope.page.currentPage - 1;
            $scope.searchForm.pageSize = $scope.page.itemsPerPage;

            $scope.goSearch();
        }

        $scope.addCaller=function(){
            $state.go("add");
        }

        $scope.callPhone=function(phone){

            var param = {
                //enterpriseId:curren,
                //hotline:60645156,
                //cno:2000,
                //pwd:"fc4b1829cc5f5cc0fea84e45dab603ea",
                customerNumber:phone
            };

            param = angular.extend(param,$rootScope.current);
            console.log(param);
            var responseStatus={
                "0":	"sync=1时表示座席已接听，sync=0时表示发起呼叫请求成功",
                "1":	"呼叫座席失败",
                "2":	"参数不正确",
                "3":	"用户验证没有通过",
                "4":	"账号被停用",
                "5":	"资费不足",
                "6":	"指定的业务尚未开通",
                "7":	"电话号码不正确",
                "8":	"座席工号（cno）不存在",
                "9":	"座席状态不为空闲，可能未登录或忙",
                "10":	"其他错误",
                "11":	"电话号码为黑名单",
                "12":	"座席不在线",
                "13":	"座席正在通话/呼叫中",
                "14":	"外显号码不正确"
            };

            $.ajax({
                url: "/admin/api/caller/outcall", data: param, async: true, cache: false,
                success: function (data) {

                    console.log(data);
                    if(data.res=="0"){
                        console.log("回拨状态成功 data.res="+data.res);
                        return;
                    }else{
                        alert(responseStatus[data.res]);
                    }
                },
                error: function (content) {
                    console.log(content);
                    alert("外呼请求失败");
                }
            });

        };
        $scope.sendSms=function(item){

            var param={
                mobile:item.phone,
                customerName:item.name
            };

            param = angular.extend(param,$rootScope.current);
            $state.go("sendSms",param);

        }

        $scope.goDel=function(item){
            if (confirm(item.phone+" 确认要删除 ？")) {
                $.ajax({
                    url: "/admin/api/caller/delete/"+item.id,
                    async: true, cache: false,
                    success: function (content) {
                        if(content.errno==0){
                            alert("客户来电 "+item.phone+" 删除成功");
                            $state.reload();
                        }else{
                            alert("客户 "+item.phone+" 删除失败");
                        }
                    },
                    error: function (content) {
                        alert("请求失败");
                    }
                });
            }

        }
        $scope.goDetail =function(item){
            $window.location = "/admin/api/caller/pop/show?customerNumber="+ item.caller.phone+"&iswatch=true";
        }

        $scope.page = { itemsPerPage: 100  };
        $scope.searchForm = {
            callerId : $stateParams.callerId!=null?parseInt($stateParams.callerId):null,
            name : $stateParams.name,
            gender : $stateParams.gender,
            phone : $stateParams.phone,
            createDate : $stateParams.createDate,
            modifyDate : $stateParams.modifyDate,
            page :        $stateParams.page ==null?0 : $stateParams.page,
            pageSize :   $stateParams.pageSize ==null?50 : $stateParams.pageSize,
            sortField :$stateParams.sortField,
            asc : $stateParams.asc,
            company:$stateParams.company,
            receiver:$stateParams.receiver
        };



        $scope.$watch('searchForm.createDate', function(d) {
            if(d){
                $scope.searchForm.createDate = $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.$watch('searchForm.modifyDate', function(d) {
            if(d){
                $scope.searchForm.modifyDate= $filter('date')(d, 'yyyy-MM-dd');
            }
        });

        $scope.sort = function(field) {
            if($scope.searchForm.sortField !=null && $scope.searchForm.sortField == field){

                if($scope.searchForm.asc=="true"){
                    $scope.searchForm.asc=false;
                }else{
                    $scope.searchForm.asc=true;
                }
            }else{
                $scope.searchForm.sortField=field;
                $scope.searchForm.asc=true;
            }
            $scope.searchForm.page=0;
            $scope.goSearch();
        }


        $scope.goRestaurantInfo=function(restaurantId){
            $rootScope.goTabPage(null,"/admin/#/oam/restaurant-list/?id="+restaurantId,null);
        }

        $.ajax({
            url: "/admin/api/caller/list", data: $scope.searchForm, async: true, cache: false,
            success: function (content) {

                $scope.items = content.queryWrappers;

                console.log($scope.items);

                /*分页数据*/
                $scope.page.itemsPerPage = content.pageSize;
                $scope.page.totalItems = content.total;
                $scope.page.currentPage = content.page + 1;

                $scope.$apply();
            },
            error: function (content) {
                alert("数据加载失败");
            }
        });



    });