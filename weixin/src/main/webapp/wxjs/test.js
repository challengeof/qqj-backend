$(document).ready(function(){
    var accessToken = '';
    $.ajax({
        url: "http://www.boruifangzhou.com/wechat",
        type: "GET",
        success: function(data) {
            accessToken = data.accessToken;
            //alert(accessToken);
            wx.config({
                debug: false,
                appId: data.appId,
                timestamp: data.timestamp,
                nonceStr: data.noncestr,
                signature: data.signature,
                jsApiList: [
                    'checkJsApi',
                    'onMenuShareTimeline',
                    'onMenuShareAppMessage',
                    'onMenuShareQQ',
                    'onMenuShareWeibo',
                    'hideMenuItems',
                    'showMenuItems',
                    'hideAllNonBaseMenuItem',
                    'showAllNonBaseMenuItem',
                    'translateVoice',
                    'startRecord',
                    'stopRecord',
                    'onRecordEnd',
                    'playVoice',
                    'pauseVoice',
                    'stopVoice',
                    'uploadVoice',
                    'downloadVoice',
                    'previewImage',
                    'uploadImage',
                    'downloadImage',
                    'getNetworkType',
                    'openLocation',
                    'getLocation',
                    'hideOptionMenu',
                    'showOptionMenu',
                    'closeWindow',
                    'scanQRCode',
                    'chooseWXPay',
                    'openProductSpecificView',
                    'addCard',
                    'chooseCard',
                    'openCard',
		            'chooseImage',
                    'previewImage',
                    'uploadImage',
                    'downloadImage'
                ]
            });
	
	wx.ready(function(){
        //alert(222)
        $('#uploadBtn').click(function(){
                    var localIds = [],
                        serverId =[];
                    //alert(12211);
            wx.chooseImage({
                count: 2, // 默认9
                sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                    //alert(localIds+"////");
                    $.each( res.localIds, function(i, n){
                        //alert(n);
                        $(this).html('<img src="'+n.toString()+'" /> <br />')
                        $(this).find('img').css({
                             'height':'100%',
                             'width': '100%'
                         })
                    });
                },
                fail: function(res){
                //alert(112)
                }
            });

            $('#btn').click(function(){
                //alert(localIds);
                wx.uploadImage({
                    localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
                    isShowProgressTips: 1, // 默认为1，显示进度提示
                    success: function (res) {
                        serverId = res.serverId; // 返回图片的服务器端ID
                        alert('serverId:'+serverId);
                        alert('accessToken:'+accessToken);
                    },
                    fail: function(res){
                        alert(JSON.stringify(res))
                    }
                });
            });


            $('#saveBtn').click(function(){
                var user = {};
                user.serverId = serverId;
                user.openId =  new Date().getTime();
                user.name = 'tName';
                user.telephone = '13756648000';
                user.birthday = '1986-03-07';
                alert(JSON.stringify(user))
                $.ajax({
                    url: "http://www.boruifangzhou.com/api/weixin/user/add",
                    type: "post",
                    data: JSON.stringify(user),
                    //headers: {'Content-Type': 'application/json'},
                    contentType: "application/json",
                    dataType: "json",
                    success: function(data) {
                        alert(JSON.stringify(data))
                        $(this).css('background','green');
                    },
                    error: function(res) {
                        alert(JSON.stringify(res));
                    }
                 })
            })
        })
	});
	wx.error(function(res){
		alert(JSON.stringify(res));
	})
        },
        error: function() {

        }
    });
});
