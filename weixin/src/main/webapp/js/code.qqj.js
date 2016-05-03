
var qqj = {
	_node: {
		serverId: {
			'noMakeup': '',
			'makeup': ''
		}
	},
	registered: false,
	getUrlParam : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
		var r = window.location.search.substr(1).match(reg);  //匹配目标参数
		if (r != null) return unescape(r[2]); return null; //返回参数值
	},
	init: function() {
		var openId = qqj.getCookie('openId');//此处从cookie读取openId
		alert('cookie,openId:' + openId);
		//如果没有openId，则拉取授权
		if (openId == null || openId == '') {
			var code = qqj.getUrlParam('code');
			if (code == null || code == '') {//跳转至授权页面
				var codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx81aeb23b12ef998a&redirect_uri=http://www.boruifangzhou.com/index.html&response_type=code&scope=snsapi_base#wechat_redirect";
				alert(codeUrl);
				window.location.href = codeUrl;
			} else {//从授权页面获取code
				//调用后台接口获取openId并保存
				alert('code:' + code);
				var rData = {};
				rData.code = code;
				$.ajax({
					url: "http://www.boruifangzhou.com/api/weixin/user/openId",
					type: "post",
					data: JSON.stringify(rData),
					contentType: "application/json",
					dataType: "json",
					async: false,
					success: function (data) {
						alert('openId:' + data.openId);
						openId = data.openId;
						qqj.setCookie('openId', openId, 1);
					},
					error: function (res) {
						alert(JSON.stringify(res));
					}
				})
			}
		}
		var rStatusData = {};
		rStatusData.openId = openId;
		//通过openId查询用户是否已经上传信息
		$.ajax({
			url: "http://www.boruifangzhou.com/api/weixin/user/status",
			type: "post",
			data: JSON.stringify(rStatusData),
			contentType: "application/json",
			dataType: "json",
			async: false,
			success: function(data) {
				if (data && data.id) {
					qqj.registered = true;
				} else {
					qqj.registered = false;
				}
				alert('registered:' + qqj.registered);
			},
			error: function(res) {
				alert(JSON.stringify(res));
			}
		})
	},

	click: function(Dom,callback){
		var doms = $("."+Dom);
		doms[0].addEventListener("touchend",function(event){
			event.stopPropagation();
			var thisDom = $(this);
			callback(thisDom);
		});
	},
	wxConfig: function(){
		var self = qqj;
		$.ajax({
			url: "http://www.boruifangzhou.com/wechat?url="+window.location,
			type: "GET",
			async: false,
			success: function(data) {
				alert(JSON.stringify(data));
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
				}),
				error(function(res){
					alert('res:'+res);
				});
			}
		})
	},
	wxReady: function(callback){
		wx.ready(function(){
			return callback();
		}),
		error(function(res){
			alert('res:'+res);
		});
	},
	upload:function(){
		alert('upload-in');
		var self = qqj;
		self.wxReady(function(){
			self.click('noMakeup',function(thisD){
				alert('upload');
				wx.chooseImage({
					count: 1, // 默认9
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: function (res) {
						localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
						// alert(localIds+"////");
						$('.noMakeup img').attr(localIds);
						wx.uploadImage({
							localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
							isShowProgressTips: 1, // 默认为1，显示进度提示
							success: function (res) {
								serverId = res.serverId; // 返回图片的服务器端ID
								self.serverId.noMakeup = serverId;
							}
						});
					},
					fail: function(res) {
						alert('res:'+JSON.stringify(res));
					}
				});
			});
		});
		self.wxReady(function(){
			self.click('makeup',function(thisD){
				wx.chooseImage({
					count: 1, // 默认9
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: function (res) {
						localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
						// alert(localIds+"////");
						$('.makeup img').attr(localIds);
						wx.uploadImage({
							localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
							isShowProgressTips: 1, // 默认为1，显示进度提示
							success: function (res) {
								serverId = res.serverId; // 返回图片的服务器端ID
								self.serverId.makeup = serverId;
							}
						});
					}
				});
			});
		});
	},
	subMit: function(){
		var self = qqj;
		self.click('subBtn',function(thisD){
			var isRequired = 0,
				errorPrompt = '';
			$('.required').each(function(i,v){
				
				if( $('.required').eq(i).val() != '' ){
					isRequired = 1;
				} else {
					isRequired = 2;
					errorPrompt+=$('.required').eq(i).attr('data-name')+'&nbsp';
				}
			})
			if( isRequired == 1 ){
				var user = {};
				user.serverIds = self.serverId;
				user.name = $('.input .userName');
				user.height = $('.input .userHeight');
				user.city = $('.input .userCity');
				user.telephone = $('.input .userTel');
				user.wechat = $('.input .userWechat');
				user.blog = $('.input .userBlog');
				user.userId = $('.input .userId');
				user.openId = qqj.getCookie('openId');

				// alert(JSON.stringify(user));

				$.ajax({
					url: "http://www.boruifangzhou.com/api/weixin/user/add",
					type: "post",
					data: JSON.stringify(user),
					contentType: "application/json",
					dataType: "json",
					success: function(data) {
						alert(JSON.stringify(data))
						window.location.href = "/api/weixin/user/" + qqj.getCookie('openId');
					},
					error: function(res) {
						alert(JSON.stringify(res));
					}
				})
			} else {
				self.errorPrompt(errorPrompt+'未填写');
			}
		})
	},
	errorPrompt: function(text){
		$('#prompt').show();
		setTimeout(function(){
			$('#prompt').hide();
		},1000)
	},
	htmlShare: function(){
		var self = qqj;
		self.click('share',function(thisD){
			$('#info .fenxiang').removeClass('dis_none');
		});
		self.click('fenxiang',function(thisD){
			thisD.addClass('dis_none');
		});
	},
	wxShare: function(){
		var self = qqj;
		self.wxReady(function(){
			var title = "灰姑娘の童话";
			var description = "俏千金杯首届全国公开选美大赛";
			var imgUrl = "http://www.canguanwuyou.cn/www/img/logo_weixin_03.png";
			var webpageUrl = "http://www.canguanwuyou.cn/www/browser.html#/share-page?sharerId=" + sharerId;

			var shareData = {
				title: title,
				desc: description,
				link: webpageUrl,
				imgUrl: imgUrl
			};
			wx.showAllNonBaseMenuItem();
			wx.onMenuShareAppMessage(shareData);
			wx.onMenuShareTimeline(shareData);
			wx.onMenuShareQQ(shareData);
			wx.onMenuShareQZone(shareData);
		});
	},
	infoShow: function(){
		$.ajax({
			url: "/api/weixin/user/" + qqj.getCookie('openId'),
			type: "GET",
			dataType:'json',
			success: function(data) {
				var infoData = data;
				$('.userName').val(infoData.userName);
				$('.userHeight').val(infoData.userHeight);
				$('.userCity').val(infoData.userCity);
				$('.userWechat').val(infoData.userWechat);
				$('.userTel').val(infoData.userTel);
				$('.userBlog').val(infoData.userBlog);
				$('.userId').val(infoData.userId);
			}
		})
	},
	isIn: function(){ //判断是否上传过图片
		if(qqj.registered){
			$('.joinBtn a').html('个人信息');
		}
	},
	//cookie
	setCookie:function(name, value, iDay){
		var oDate=new Date();
		oDate.setDate(oDate.getDate()+iDay);
		document.cookie=name+'='+value+';expires='+oDate;
	},
	getCookie:function(name){
		var arr=document.cookie.split('; ');
		var i=0;
		for(i=0;i<arr.length;i++)
		{
			var arr2=arr[i].split('=');
			if(arr2[0]==name)
			{
				return arr2[1];
			}
		}
		return '';
	},
	removeCookie:function(name){
		setCookie(name, '1', -1);
	}
}
$(function(){
	alert(1);
	($('#index')[0]) && qqj.init();
	alert(2);
	qqj.wxConfig();
	alert(3);
	($('#upload')[0]) && qqj.upload();
	alert(4);


	qqj.wxShare();
	($('#index')[0]) && qqj.isIn();
	($('#info')[0]) && qqj.htmlShare();
	($('#info')[0]) && qqj.infoShow();
	($('#upload')[0]) && qqj.subMit();
});
