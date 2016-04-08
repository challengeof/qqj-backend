<%@ page language="java" contentType="text/html; charset=utf8"   pageEncoding="utf8" %>


<!doctype html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <meta http-equiv="expires" content="0">

    <style>
        @media (min-width: 501px) and (max-width: 1199px) {
            .col-lg-4 {
                width: 50%;
                float: left;
            }
        }
        @media (max-width: 500px) {
            .col-lg-4 {
                width: 100%;
                float: left;
            }
        }
        .my-col-lg-4{
            float: left;
            min-width: 200px;
            height: 27px;
            line-height:27px;
            text-align: center;
        }
        .my-col-lg-4 .myradius{

            border-radius: 4px;
        }


    </style>

    <!--CSS-->
    <link rel="stylesheet" href="/admin/bower_components/bootstrap/dist/css/bootstrap.min.css?v=1434471202000">
    <link rel="stylesheet" href="/admin/styles/main.css?v=1440774315000">
    <link rel="stylesheet" href="/admin/styles/sb-admin-2.css?v=1439835454000">
    <link rel="stylesheet" href="/admin/styles/timeline.css?v=1426497590000">
    <link rel="stylesheet" href="/admin/bower_components/metisMenu/dist/metisMenu.min.css?v=1414854793000">
    <link rel="stylesheet" href="/admin/bower_components/angular-loading-bar/build/loading-bar.min.css?v=1424801518000">
    <link rel="stylesheet" href="/admin/bower_components/font-awesome/css/font-awesome.min.css?v=1421950182000">
    <link rel="stylesheet" href="/admin/bower_components/angular-ui-select/dist/select.min.css?v=1424231408000">
    <link rel="stylesheet" href="/admin/bower_components/angular-xeditable/dist/css/xeditable.css?v=1427408717000">
    <link rel="stylesheet" href="/admin/bower_components/jstree/dist/themes/default/style.min.css?v=1430719767000">

    <!-- JS -->
    <script src="/admin/bower_components/jquery/dist/jquery.min.js?v=1430236984000"></script>
    <script src="/admin/bower_components/angular/angular.js?v=1439492126000"></script>
    <script src="/admin/bower_components/angular-resource/angular-resource.min.js?v=1426598111000"></script>
    <script src="/admin/bower_components/bootstrap/dist/js/bootstrap.min.js?v=1434471202000"></script>
    <script src="/admin/bower_components/angular-ui-router/release/angular-ui-router.min.js?v=1432040388000"></script>
    <script src="/admin/bower_components/json3/lib/json3.min.js?v=1403472126000"></script>
    <script src="/admin/bower_components/oclazyload/dist/ocLazyLoad.min.js?v=1419937085000"></script>
    <script src="/admin/bower_components/angular-loading-bar/build/loading-bar.min.js?v=1424801518000"></script>
    <script src="/admin/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js?v=1416172654000"></script>
    <script src="/admin/bower_components/metisMenu/dist/metisMenu.min.js?v=1414854793000"></script>
    <script src="/admin/bower_components/angular-sanitize/angular-sanitize.min.js?v=1426598111000"></script>
    <script src="/admin/bower_components/angular-i18n/angular-locale_zh-cn.js?v=1439937586000"></script>
    <script src="/admin/bower_components/angular-ui-select/dist/select.min.js?v=1424231408000"></script>
    <script src="/admin/bower_components/checklist-model/checklist-model.js?v=1391696576000"></script>
    <script src="/admin/bower_components/angular-xeditable/dist/js/xeditable.js?v=1427408717000"></script>
    <script src="/admin/bower_components/angular-ui-utils/event.js?v=1388284515000"></script>
    <script src="/admin/bower_components/moment/min/moment.min.js?v=1438058390000"></script>
    <script src="/admin/bower_components/angular-messages/angular-messages.js?v=1439492126000"></script>
    <script src="/admin/bower_components/angular-ui-map-baidu/ui-map.js?v=1423663262000"></script>
    <script src="/admin/bower_components/ng-file-upload/angular-file-upload.js?v=1426145487000"></script>
    <script src="/admin/bower_components/ng-js-tree/dist/ngJsTree.js?v=1436679913000"></script>
    <script src="/admin/bower_components/jstree/dist/jstree.js?v=1430719767000"></script>
    <script src="/admin/bower_components/angular-responsive-tables/release/angular-responsive-tables.min.js?v=1433994534000"></script>
    <script src="/admin/caller/callerpop.js" ></script>
</head>
<body>


<div ng-app="callerApp"  ng-init="phone='${phone}';user.realname='${userRealname}'; iswatch='${iswatch}';">
    <!-- 全局页面 -->
    <div ng-controller="callerInfo"  class="container-fluid">
        <!-- 页面标题 -->
        <div  class="panel panel-default row " style="margin-bottom:0px;">
            <div class="panel-body " style=" padding-bottom:0px;" >

                <div class="col-lg-42 " >

                    <form name="callerInfoForm" rol="form" novalidate>
                        <h4>
                            <div class="my-col-lg-4">电话：{{phone}}
                                —
                                <input type="text"  ng-class="{'has-error':callerInfoForm.name.$invalid }" class="myradius ng-valid ng-touched" ng-maxlength="50"  name="name" placeholder="来电客户名称" ng-model="caller.name" ng-bind="caller.name">
                                <span ng-class="{'has-error':callerInfoForm.name.$invalid }" ng-show="callerInfoForm.name.$invalid " >名称最多50位</span>
                            </div>
                            <div class="my-col-lg-4">
                                备注：
                                <input type="text" class="myradius ng-valid ng-touched" style=" min-width: 500px;"   ng-class="{'has-error':callerInfoForm.detail.$invalid }"
                                       name="detail"  placeholder="可以写一些简单的备注（最多255字符）" ng-maxlength="255" ng-model="caller.detail" validate-null   />
                                <%--<textarea class="form-control" style="width: 500px;" ng-class="{'has-error':callerInfoForm.detail.$invalid }"--%>
                                <%--name="detail"  placeholder="可以写一些简单的备注（最多255字符）" ng-maxlength="255" ng-model="caller.detail" validate-null >--%>
                                <%--</textarea>--%>
                                <span ng-show="callerInfoForm.detail.$invalid " ng-class="{'has-error':callerInfoForm.detail.$invalid }" >描述长度最多255位</span>
                            </div>
                            <div style="clear:both; line-height: 50px;">
                                <span><button class="btn btn-xs btn-primary" ng-click="callerSave();" ng-disabled="callerInfoForm.$invalid">来电人信息保存</button></span>
                                <span><button class="btn btn-xs btn-primary" ng-click="newTicketByPhone(caller.name,phone);" >发起工单（此号）</button></span>
                                <span><button class="btn btn-xs btn-primary" ng-click="goBack();" ng-if="iswatch=='true'" >返回</button></span>
                            </div>
                        </h4>
                        <%--<span>--%>
                        <%--<textarea class="form-control" style="width: 500px;" ng-class="{'has-error':callerInfoForm.detail.$invalid }"--%>
                        <%--name="detail"  placeholder="可以写一些简单的备注（最多255字符）" ng-maxlength="255" ng-model="caller.detail" validate-null >--%>
                        <%--</textarea>--%>
                        <%--</span>--%>

                    </form>
                </div>


            </div>
        </div>

        <div class="panel-body row" >
            <div class="col-lg-42 ">

                <form name="phoneSearchForm" rol="form" novalidate>
                    <input type="text" class="ng-pristine ng-valid ng-touched" name="phone" placeholder="手机号" ng-model="searchPhoneVal" required>
                    <button class="btn btn-xs btn-primary" ng-click="searchPhone=searchPhoneVal;" ng-disabled="phoneSearchForm.$invalid" >查询此手机号对应信息</button>
                    <button class="btn btn-xs btn-primary" ng-click="resetLoad(phone);" >重置</button>
                </form>
            </div>
        </div>
        <div  class="panel panel-default row">
            <div class="panel-body" >

                <div ng-if="restaurant==null || restaurant.length==0" >
                    未找到{{searchPhone}} 对应的商户信息
                </div>
                <div ng-repeat="rtt in restaurant" >
                    <!-- 明细信息 -->
                    <div>
                        <div class="row">
                            <div class="col-lg-4">
                                <label>联系人：</label>
                                <!--<a class="btn btn-xs btn-default" ui-sref="oam.orderList({restaurantId:order.rtt.id})">{{phone}}</a>-->
                                <span ng-bind="rtt.receiver" ></span>
                            </div>
                            <div class="col-lg-4">
                                <label>注册电话：</label>
                                <span ng-bind="rtt.customer.username" class="text-danger"></span>
                            </div>
                            <div class="col-lg-4">
                                <label>联系电话：</label>
                                <span ng-bind="rtt.telephone" class="text-danger"></span>
                            </div>

                        </div>
                        <div class="row">

                            <div class="col-lg-4">
                                <label>商户id：</label>
                                <a class="btn btn-xs btn-default" ng-click="goRestaurantInfo(rtt.id);"  ng-bind="rtt.id"></a>

                            </div>
                            <div class="col-lg-4">
                                <label>店名：</label>
                                <a class="btn btn-xs btn-default" ng-click="goRestaurantInfo(rtt.id);" >{{rtt.name}}</a>
                            </div>
                            <div class="col-lg-4">
                                <label>地址：</label>
                                <span ng-bind="rtt.address.address"></span>
                            </div>

                        </div>
                        <div class="row">
                            <div class="col-lg-4">
                                <label>状态：</label>
                                <span ng-bind="rtt.status.name"></span>
                            </div>

                            <div class="col-lg-4">
                                <label>销售顾问：</label>
                                <span ng-bind="rtt.customer.adminUser.realname"></span>
                            </div>

                            <div class="col-lg-4">
                                <label>销售顾问电话：</label>
                                <span ng-bind="rtt.customer.adminUser.telephone"></span>
                            </div>
                        </div>
                        <!--
                        <div class="row">
                            <div class="col-lg-4">
                                <span><button class="btn btn-xs btn-primary" ng-click="relationRestaurant(rtt);" >关联到此商户</button></span>
                            </div>
                        </div>
                        -->

                        <hr ng-if="$index != restaurant.length-1 ;"/>

                    </div>

                </div>
                <hr/>

                <style>

                    .tab-content.tab-bordered {
                        border: 1px solid lightgray;
                        border-top: none;
                        padding: 15px;
                        border-radius: 0 0 4px 4px;
                    }

                </style>
                <div>
                    <ul class="nav nav-tabs" ng-init="activeTab = 1"  >
                        <!--
                        <li ng-class="{active: activeTab == 1}" ui-sref="businessList({activeTab:1})" ><a href="javascript:;" ng-click="activeTab = 1">工单</a></li>
                        <li ng-class="{active: activeTab == 2}" ui-sref="orderlist({telephone:phone,activeTab:2})"><a href="javascript:;" ng-click="activeTab = 2">订单</a></li>
                        -->
                        <li ng-class="{active: activeTab == 1}"  ><a href="javascript:;" ng-click="activeTab = 1">工单</a></li>
                        <li ng-class="{active: activeTab == 2}"  ><a href="javascript:;" ng-click="activeTab = 2">订单</a></li>
                        <!--
                            <li ng-class="{active: activeTab == 3}" ui-sref="backcall({activeTab:3})"><a href="javascript:;" ng-click="activeTab = 3 ">回访</a></li>
                        -->
                    </ul>
                    <!--<div class="tab-content tab-bordered">-->
                    <!--<div class="tab-panel" ng-if="activeTab == 1">-->
                    <!--<iframe frameborder=0 width=100% height=2000 marginheight=0 marginwidth=0 scrolling=no src="http://www.canguanwuyou.cn/ticket/login&樊博文"></iframe>-->
                    <!--</div>-->
                    <!--<div class="tab-panel" ng-if="activeTab == 2">-->
                    <!--标签2的内容-->
                    <!--</div>-->
                    <!--<div class="tab-panel" ng-if="activeTab == 3">-->
                    <!--标签3的内容-->
                    <!--</div>-->
                    <!--</div>-->

                    <%--<div ng-show="activeTab == 1" class="tab-content tab-bordered">--%>
                    <%--<iframe  name="businesslistFrame" id="businesslistFrame" frameborder=0 width=100% height=2000 marginheight=0 marginwidth=0 scrolling=no ></iframe>--%>
                    <%--</div>--%>

                    <div ng-show="activeTab == 1" ng-include="'/admin/caller/businessList.html'" class="tab-content tab-bordered">
                    </div>

                    <div ng-show="activeTab == 2" ng-include="'/admin/caller/orderlist.html'" class="tab-content tab-bordered">
                    </div>

                    <!--
                    <div ui-view="" class="tab-content tab-bordered">
                    </div>
                    -->
                </div>

            </div>
        </div>
    </div>

</body>
</html>