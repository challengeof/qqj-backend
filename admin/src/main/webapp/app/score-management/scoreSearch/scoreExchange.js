'use strict';
angular.module('sbAdminApp')
    .controller('scoreExchangeCtrl', function ($scope, $http, $stateParams, $filter, $location, $rootScope, $window){

        $scope.inParam={
            customerId: $stateParams.customerId
        }

        //$scope.loadCustomer=function(){
        //    $http({
        //        url: '/admin/api/score/exchange-coupon/'+ $scope.inParam.customerId,
        //        method: "GET"
        //    }).success(function (data, status, headers, config) {
        //
        //    }).error(function (data, status, headers, config) {
        //        window.alert("搜索失败...");
        //    });
        //}


        $scope.loadCoupon=function(){
            $http({
                url: '/admin/api/score/exchange-coupon/'+ $scope.inParam.customerId,
                method: "GET"
            }).success(function (data, status, headers, config) {
                $scope.couponData=data;
            }).error(function (data, status, headers, config) {
                window.alert("搜索失败...");
            });
        }

        $scope.$watch('inParam.couponId', function (newVal) {
            if(newVal!=null){
                for(var i=0;i<$scope.couponData.coupons.length;i++){
                    console.log($scope.couponData.coupons[i].id );
                    console.log(newVal);
                    if($scope.couponData.coupons[i].id == newVal){

                        $scope.currentCoupon = $scope.couponData.coupons[i];
                        console.log($scope.currentCoupon);
                        return;
                    }
                }
            }
        });
        //$scope.$watch('inParam.exchangeNum', function (newVal) {
        //    alert(newVal);
        //
        //});

        $scope.checkExchangeScore= function(){
            if($scope.currentCoupon==null || $scope.currentCoupon.score ==null){
                return false;
            }
            if(isNaN($scope.currentCoupon.score * $scope.inParam.count)){
                return false;
            }
            if($scope.currentCoupon.score * $scope.inParam.count > $scope.couponData.score.totalScore - $scope.couponData.score.exchangeScore){
                return false;
            }
            return true;
        }

        $scope.submitForm=function(){
            //var needScore = $scope.currentCoupon.score * $scope.inParam.count
            //var availableScore = $scope.couponData.score.totalScore - $scope.couponData.score.exchangeScore;
            //
            //if(isNaN(needScore)){
            //    alert("无法兑换");
            //}
            //if(needScore>availableScore){
            //    alert("积分不足");
            //}
            //兑换
            $http({
                url: '/admin/api/score/exchange',
                method: "PUT",
                data: $scope.inParam,
                headers: {'Content-Type': 'application/json;charset=UTF-8'}
            }).success(function (data, status, headers, config) {
                $scope.couponData = data;
                alert("执行成功");
                $location.search($scope.inParam);
            }).error(function (data, status, headers, config) {
                if(data!=null && data.errmsg!=null){
                    window.alert(data.errmsg);
                }else {
                    window.alert("操作失败");
                }
            });
            return ;
        }
        $scope.isNaN= function(intVal){
            return isNaN(intVal);
        }
        $scope.loadCoupon();

    });