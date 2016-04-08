<%@ page language="java" contentType="text/html; charset=utf8"   pageEncoding="utf8" %>
<!doctype html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <title></title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">

    <meta http-equiv="expires" content="0">

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
    <script src="/admin/caller/list/callerlist.js" ></script>

</head>
<body>

<div ng-app="callerApp"  ng-init="current.enterpriseId='${ccicuser.enterpriseId}'; current.hotline='${ccicuser.hotline}';    current.cno='${ccicuser.cno}';    current.pwd='${ccicuser.pwd}'; current.crmId='${ccicuser.crmId}'; " >

    <div ui-view="" ></div>

</div>

</body>
</html>