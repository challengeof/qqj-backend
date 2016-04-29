$(document).ready(function(){
    var codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx81aeb23b12ef998a&redirect_uri=http://www.boruifangzhou.com/demo2.html&response_type=code&scope=snsapi_userinfo#wechat_redirect";
    alert(codeUrl);
    window.location.href=codeUrl;
    //var accessToken = '';
    //var appId = '';
    //$.ajax({
    //    url: "http://www.boruifangzhou.com/wechat",
    //    type: "GET",
    //    success: function(data) {
    //        accessToken = data.accessToken;
    //        appId = data.appId;
    //        //alert(accessToken);
    //        wx.config({
    //            debug: false,
    //            appId: data.appId,
    //            timestamp: data.timestamp,
    //            nonceStr: data.noncestr,
    //            signature: data.signature,
    //            jsApiList: [
    //                'checkJsApi',
    //                'onMenuShareTimeline',
    //                'onMenuShareAppMessage',
    //                'onMenuShareQQ',
    //                'onMenuShareWeibo',
    //                'hideMenuItems',
    //                'showMenuItems',
    //                'hideAllNonBaseMenuItem',
    //                'showAllNonBaseMenuItem',
    //                'translateVoice',
    //                'startRecord',
    //                'stopRecord',
    //                'onRecordEnd',
    //                'playVoice',
    //                'pauseVoice',
    //                'stopVoice',
    //                'uploadVoice',
    //                'downloadVoice',
    //                'previewImage',
    //                'uploadImage',
    //                'downloadImage',
    //                'getNetworkType',
    //                'openLocation',
    //                'getLocation',
    //                'hideOptionMenu',
    //                'showOptionMenu',
    //                'closeWindow',
    //                'scanQRCode',
    //                'chooseWXPay',
    //                'openProductSpecificView',
    //                'addCard',
    //                'chooseCard',
    //                'openCard',
		//            'chooseImage',
    //                'previewImage',
    //                'uploadImage',
    //                'downloadImage'
    //            ]
    //        });
    //
    //
    //    },
    //    error: function() {
    //
    //    }
    //});
});
