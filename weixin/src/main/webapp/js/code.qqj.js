
var qqj = {
	serverId: {
		'noMakeup': '',
		'makeup': ''
	},
	registered: false,
	getUrlParam : function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
		var r = window.location.search.substr(1).match(reg);  //匹配目标参数
		if (r != null) return unescape(r[2]); return null; //返回参数值
	},
	init: function() {
		var openId = qqj.getCookie('openId');//此处从cookie读取openId
		//如果没有openId，则拉取授权
		if (document.cookie.indexOf('openId=') == -1) {
			var code = qqj.getUrlParam('code');
			if (code == null || code == '') {//跳转至授权页面
				var codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx81aeb23b12ef998a&redirect_uri=http://www.boruifangzhou.com/index.html&response_type=code&scope=snsapi_base#wechat_redirect";
				window.location.href = codeUrl;
			} else {//从授权页面获取code
				//调用后台接口获取openId并保存
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
				if (data.id != null && data.id != '') {
					qqj.registered = true;
				} else {
					qqj.registered = false;
				}
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
			}
		})
	},
	wxReady: function(callback){
		wx.ready(function(){
			return callback();
		})
	},
	upload:function(){
		var self = qqj;
		self.wxReady(function(){
			self.click('noMakeup',function(thisD){
				wx.chooseImage({
					count: 1, // 默认9
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: function (res) {
						localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
						// alert(localIds+"////");
						$('.noMakeup img').attr('src',localIds[0]);
						wx.uploadImage({
							localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
							isShowProgressTips: 1, // 默认为1，显示进度提示
							success: function (res) {
								serverId = res.serverId; // 返回图片的服务器端ID
								var data = {};
								data.serverId = serverId;
								data.openId = qqj.getCookie('openId');
								data.type = 1;

								$.ajax({
									url: "http://www.boruifangzhou.com/api/weixin/user/upload-pic",
									type: "post",
									data: JSON.stringify(data),
									contentType: "application/json",
									dataType: "json",
									success: function(data) {
										// data.url;
										$('.noMakeup').attr('src',data.url);
									},
									error: function(res) {
										alert(JSON.stringify(res));
									}
								})
							}
						});
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
						wx.uploadImage({
							localId: localIds.toString(), // 需要上传的图片的本地ID，由chooseImage接口获得
							isShowProgressTips: 1, // 默认为1，显示进度提示
							success: function (res) {
								serverId = res.serverId; // 返回图片的服务器端ID
								var data = {};
								data.serverId = serverId;
								data.openId = qqj.getCookie('openId');
								data.type = 2;

								$.ajax({
									url: "http://www.boruifangzhou.com/api/weixin/user/upload-pic",
									type: "post",
									data: JSON.stringify(data),
									contentType: "application/json",
									dataType: "json",
									success: function(data) {
										// data.url;
										$('.makeup').attr('src',data.url);
									},
									error: function(res) {
										alert(JSON.stringify(res));
									}
								})
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
					errorPrompt+=$('.required').eq(i).attr('data-name')+'  ';
				}
			})
			if( isRequired == 1 ){
				var user = {};
				user.name = $('.input .userName').val();
				user.height = $('.input .userHeight').val();
				user.city = $('.input .userCity').val();
				user.telephone = $('.input .userTel').val();
				user.wechat = $('.input .userWechat').val();
				user.blog = $('.input .userBlog').val();
				user.userId = $('.input .userId').val();
				user.openId = qqj.getCookie('openId');

				$.ajax({
					url: "http://www.boruifangzhou.com/api/weixin/user/add",
					type: "post",
					data: JSON.stringify(user),
					contentType: "application/json",
					dataType: "json",
					success: function(data) {
						window.location.href = 'http://www.boruifangzhou.com/info.html';
					},
					error: function(res) {
						alert('error:' + JSON.stringify(res));
					}
				})
			} else {
				self.errorPrompt(errorPrompt+'未填写');
			}
		})
	},
	errorPrompt: function(text){
		$('#prompt').show().html(text);;
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
			var imgUrl = "http://7xtddo.com1.z0.glb.clouddn.com/image/share.png";
			var webpageUrl = "http://www.boruifangzhou.com/index.html";

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
			url: 'http://www.boruifangzhou.com/api/weixin/user/' + qqj.getCookie('openId'),
			type: "GET",
			dataType:'json',
			success: function(data) {
				var infoData = data;
				$('.userName').html(infoData.name);
				$('.userHeight').html(infoData.height);
				$('.userCity').html(infoData.city);
				$('.userWechat').html(infoData.wechat);
				$('.userTel').html(infoData.telephone);
				$('.userBlog').html(infoData.blog);
				$('.userId').html(infoData.userId);
				$('.noMakeup').attr('src',data.pics[0].smallPic);
				$('.makeup').attr('src',data.pics[1].smallPic);
			}
		})
	},
	isIn: function(){ //判断是否上传过图片
		if(qqj.registered){
			$('.joinBtn a').html('个人信息').attr('href','http://www.boruifangzhou.com/info.html');
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
	($('#index')[0]) && qqj.init();
	qqj.wxConfig();
	($('#upload')[0]) && qqj.upload();
	qqj.wxShare();
	($('#index')[0]) && qqj.isIn();
	($('#info')[0]) && qqj.htmlShare();
	($('#info')[0]) && qqj.infoShow();
	($('#upload')[0]) && qqj.subMit();
});
