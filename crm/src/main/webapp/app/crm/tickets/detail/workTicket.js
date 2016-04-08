'use strict';
angular.module('sbAdminApp')
    .controller('workTicketContrl', function ($scope, $rootScope, $http, $filter, $stateParams, $location,$window,$state) {
debugger;
        $scope.formData = {};
        //问题类型
        $scope.processes = [{'name':$stateParams.processName,'type':parseInt($stateParams.processType)}];
        //问题来源
        $scope.problemSources = [{'name':$stateParams.problemSourceName,'type':parseInt($stateParams.problemSourceType)}];
        //工单详情
        $http({
            url: $rootScope.rootPath+"/api/work-ticket/view/"+$stateParams.workTicketId,
            method: 'GET',
        }).success(function(data) {
            debugger;
            $scope.formData = data;
            //问题类型对应的详情信息
            $scope.formData.content =eval('('+data.content+')');
                console.log(data);
        })

        //待跟进人
        $http.get("/crm/api/admin-user/global?role=CustomerService")
            .success(function (data) {
                $scope.newAdminUsers = data;
            });

        //输入餐馆名称
        $scope.searchDai = function (adminUser) {
            console.log(adminUser);
            if(adminUser!=undefined){
                $scope.formData.followUp.telephone = adminUser.telephone;
            }else{
                $scope.formData.followUp.telephone = '';
            }
        }

        //加载问题类型对应的详情
        $scope.processChange=function(process) {
            switch(process){
                case 1:
                    $state.go('crm.workTicket.consultType-one',{'unchange':'no'});
                    break;
                case 2:
                    $state.go('crm.workTicket.consultType-two');
                    break;
                case 3:
                    $state.go('crm.workTicket.consultType-three');
                    break;
                case 4:
                    $state.go('crm.workTicket.consultType-four');
                    break;
                case 5:
                    $state.go('crm.workTicket.consultType-five');
                    break;
                case 6:
                    $state.go('crm.workTicket.consultType-six');
                    break;
                case 7:
                    $state.go('crm.workTicket.consultType-seven');
                    break;
                case 8:
                    $state.go('crm.workTicket.consultType-eight');
                    break;
            }
        }

        //进入controller后执行，以便加载问题类型对应的详情
        $scope.processChange(parseInt($stateParams.processType));

        //提交
        $scope.submit=function() {
            $scope.updateFormData = $scope.formData;
            $scope.updateFormData.content = $scope.formData.content;

            $scope.updateFormData.restaurantId = $scope.formData.restaurant.id;
            $scope.updateFormData.restaurantTelephone = $scope.formData.restaurant.telephone;
            $scope.updateFormData.followUpId = $scope.formData.followUp.id;

            //回复人姓名
            $scope.formData.content.reply.replyer = $rootScope.user.realname;
            //回复时间
            $scope.formData.content.reply.replyTime = getNowFormatDate();

            if($scope.updateFormData.content.replys!= undefined){
                $scope.updateFormData.content.replys.push($scope.formData.content.reply);
            }else{
                var array = [];
                array.push($scope.formData.content.reply);
                $scope.updateFormData.content['replys'] = array;
            }

            delete  $scope.updateFormData.content.reply;
            //流程对应的详细信息
            $scope.updateFormData.content = JSON.stringify($scope.updateFormData.content);
            debugger;
            $scope.updateFormData.status = 1;
            $http.put(
                $rootScope.rootPath + "/api/work-ticket/update/"+$stateParams.workTicketId,
                $scope.updateFormData
            ).then(
                function(){
                    alert('提交成功！');
                },
                function(){
                    alert('提交失败！');
                }
            );
        }

        function getNowFormatDate() {
            var date = new Date();
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
                + " " + date.getHours() + seperator2 + date.getMinutes()
                + seperator2 + date.getSeconds();
            return currentdate;
        }

    });
