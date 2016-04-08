var app = angular.module("callerApp", ['oc.lazyLoad', 'ui.router', 'ui.bootstrap',
    'angular-loading-bar', 'checklist-model', 'angularFileUpload',
    'ui.select','xeditable','ui.map', 'ngMessages','ngJsTree', 'wt.responsive','ngResource']);

app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/list.html');
    // $urlRouterProvider.when("", "/page1.html1");

    //restaurant:$stateParams.restaurant,
    //    company:$stateParams.company,
    //    contactPerson:$stateParams.contactPerson

    $stateProvider.state("list", {
        url: "/list.html?callerId&name&gender&phone&createDate&modifyDate&page&pageSize&sortField&asc&company&receiver",
        templateUrl: "/admin/caller/list/listcontent.html",
        controller: "list",
        resolve: {
            loadMyFiles: function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'callerApp',
                    files: [ '/admin/caller/list/listcontent.js']
                })
            }
        }
    } ).state("add", {
        url: '/add.html',
        templateUrl: "/admin/caller/list/add.html",
        controller: "add",
        resolve: {
            loadMyFiles: function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'callerApp',
                    files: [ '/admin/caller/list/add.js']
                })
            }
        }
    }).state("sendSms", {
        url: '/sendSms.html?hotline&enterpriseId&cno&pwd&mobile&customerName',
        templateUrl: "/admin/caller/list/sendSms.html",
        controller: "sendSms",
        resolve: {
            loadMyFiles: function ($ocLazyLoad) {
                return $ocLazyLoad.load({
                    name: 'callerApp',
                    files: [ '/admin/caller/list/sendSms.js']
                })
            }
        }
    })

});

//app.controller('current',function($scope, $http, $rootScope, $location, $window, $stateParams,$state) {
//});