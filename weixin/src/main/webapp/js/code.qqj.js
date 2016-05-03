function getUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
	var r = window.location.search.substr(1).match(reg);  //匹配目标参数
	if (r != null) return unescape(r[2]); return null; //返回参数值
}

var openId;
var registered;
//如果没有openId，则拉取授权
if (openId == null || openId == '') {
	var code = getUrlParam('code');
	if (code == null || code == '') {//跳转至授权页面
		var codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx81aeb23b12ef998a&redirect_uri=http://www.boruifangzhou.com/index.html&response_type=code&scope=snsapi_base#wechat_redirect";
		alert(codeUrl);
		window.location.href=codeUrl;
	} else {//从授权页面获取code
		var code = getUrlParam('code');
		//调用后台接口获取openId并保存
		alert('code:' + code);
		var rData = {};
		rData.code = code;
		$.ajax({
			url: "http://www.boruifangzhou.com/api/weixin/user/status",
			type: "post",
			data: JSON.stringify(rData),
			contentType: "application/json",
			dataType: "json",
			success: function(data) {
				alert(JSON.stringify(data))
				openId = data.openId;
				if (data.id == null || data.id == '') {
					registered = false;
				} else {
					registered = true;
				}
			},
			error: function(res) {
				alert(JSON.stringify(res));
			}
		})
	}
}

var imgUrl = "images/"; //CONFIGS.IMAGES
var kindleVoyageMo = {
	_node: {
		startX : 0, 
		startY : 0, 
		startX1 : 0, 
		startY1 : 0, 
		distance : 0,
		direction : "",
		top: 0,
		left: 0,
		left1: 0,
		moveT: 0,
		moveL: 0,
		moveL1: 0,
		audioNum: 0
	},
	click: function(Dom,callback){
		var doms = $("."+Dom);
		doms[0].addEventListener("touchend",function(event){
			event.stopPropagation();
			var thisDom = $(this);
			callback(thisDom);
		});
	},
	// 图片改变位置
	// toustartFun: function(evt){
	// 	var self = kindleVoyageMo;
	// 	var touch = evt.touches[0]; //获取第一个触点
 //        var x = Number(touch.pageX); //页面触点X坐标
 //        var y = Number(touch.pageY); //页面触点Y坐标
 //        //记录触点初始位置
 //        self._node.startX = x;
 //        self._node.startY = y;
 //        self._node.top = parseFloat($(".imgEdit").css("top"));
 //        self._node.left = parseFloat($(".imgEdit").css("left"));
 //        // console.log(self._node.top);
 //        // console.log(self._node.left);
 //        // console.log(self._node.startX);
 //        // console.log(self._node.startY+"------");
	// },
	// toumoveFun: function(evt){
	// 	evt.stopPropagation();
	// 	evt.preventDefault();
	// 	var self = kindleVoyageMo;
	// 	var touch = evt.touches[0]; //获取第一个触点
 //        var x = Number(touch.pageX); //页面触点X坐标
 //        var y = Number(touch.pageY); //页面触点Y坐标
 //        // //判断滑动方向
 //        // self._node.distance = x - self._node.startX;
 //        // if (self._node.distance >= 10 ) {
 //        //     // text += '<br/>左右滑动';
 //        //     self._node.direction="right";
 //        // } else if(self._node.distance <= -10){
 //        // 	self._node.direction="left";
 //        // }
 //        // console.log(top+"//"+self._node.startX+"//"+x);
 //        // console.log(left+"//"+self._node.startY+"//"+y);
 //        $(".imgEdit").css({
 //        	"left": self._node.left - (self._node.startX - x),
 //        	"top": self._node.top - (self._node.startY - y)
 //        });		
 //        self._node.moveT = parseFloat($(".imgEdit").css("top"));
 //        self._node.moveL = parseFloat($(".imgEdit").css("left"));

	// },
	// touendFun: function(evt){
	// 	var self = kindleVoyageMo;
	// 	var thisH = parseFloat($(".imgEdit").height()),
	// 		thisW = parseFloat($(".imgEdit").width()),
	// 		boxH = parseFloat($(".imgEditorBox").height()),
	// 		boxW = parseFloat($(".imgEditorBox").width());
	// 	if( self._node.moveT > 0 && self._node.moveL>0){
	// 		$(".imgEdit").css({
	//         	"left": 0,
	//         	"top": 0
	//         });	
	// 	} if( self._node.moveT<-(thisH-boxH) && self._node.moveL<-(thisW-boxW) ){
	// 		$(".imgEdit").css({
	//         	"left": -(thisW-boxW),
	//         	"top": -(thisH-boxH)
	//         });	
	// 	} else if( self._node.moveT > 0 ){
	// 		$(".imgEdit").css({
	//         	"top": 0,
	//         	"left": self._node.moveL
	//         });	
	// 	} else if( self._node.moveL > 0 ){
	// 		$(".imgEdit").css({
	//         	"left": 0,
	//         	"top": self._node.moveT
	//         });	
	// 	} else if( self._node.moveT<-(thisH-boxH) ){
	// 		$(".imgEdit").css({
	//         	"top": -(thisH-boxH),
	//         	"left": self._node.moveL
	//         });	
	// 	} else if( self._node.moveL<-(thisW-boxW) ){
	// 		$(".imgEdit").css({
	//         	"left": -(thisW-boxW),
	//         	"top": self._node.moveT
	//         });	
	// 	} else {
	// 		$(".imgEdit").css({
	//         	"left": self._node.moveL,
	//         	"top": self._node.moveT
	//         });	
	// 	}

	// 	self._node.distance = 0;
	// 	self._node.direction = "";
	// },
	toustartFun1: function(evt){
		var self = kindleVoyageMo;
		var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标
        //记录触点初始位置
        self._node.startX = x;
        self._node.startY = y;
        self._node.left1 = parseFloat($(".cursor").css("left"));
	},
	toumoveFun1: function(evt){
		evt.stopPropagation();
		evt.preventDefault();
		var self = kindleVoyageMo;
		var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标
        var minLeft = parseFloat($(".adjust").width())*0.02;
        var maxLeft =  parseFloat($(".adjust").width())*0.9;;
        var leftNum = (self._node.left1 - (self._node.startX1 - x))/2;
        // //判断滑动方向
        

        if(x!=self._node.startX&& leftNum >= minLeft && leftNum <= maxLeft ){
        	// console.log(self._node.left1+"///"+self._node.startX1+"///"+x);
        	// console.log(self._node.left1 - (self._node.startX1 - x));
        	$(".cursor").css({
        		"left": leftNum
        	});
        	var grayNum = (leftNum/1000)+0.8;
        	if(grayNum<=1){
        		$(".imgEdit").css({
        			"-webkit-filter": "grayscale("+grayNum+")"
        		})
        		// console.log($(".imgEdit").css("-webkit-filter")+"///"+leftNum/1000);
        	}
        	self._node.moveL1 = parseFloat($(".cursor").css("left"));
        }

	},
	touendFun1: function(evt){
		var self = kindleVoyageMo;
		var thisH = parseFloat($(".imgEdit").height()),
			thisW = parseFloat($(".imgEdit").width()),
			boxH = parseFloat($(".imgEditorBox").height()),
			boxW = parseFloat($(".imgEditorBox").width());
		
		self._node.distance = 0;
		self._node.direction = "";
	},
	adjustGray: function(Dom){
		var self = kindleVoyageMo;
		var thisD = $("."+Dom);
		thisD[0].addEventListener("touchstart", self.toustartFun1, false);
		thisD[0].addEventListener("touchmove", self.toumoveFun1, false);
		thisD[0].addEventListener("touchend", self.touendFun1, false);
	},
	imgChangePosition: function(Dom){
		var self = kindleVoyageMo;
		var thisD = $("."+Dom);
		thisD.onmousemove = function (e){
			var e = e || event;
			e.cancelBubble = true;
			e.returnValue = false;
		}

		// 防止触发浏览器的整体拖动
		thisD[0].addEventListener('touchmove', function (e){
			e.preventDefault();
		}, false); 

		thisD[0].addEventListener('touchstart', img_mousedown, false);
		thisD[0].addEventListener('touchend', img_mouseup, false);
		thisD[0].addEventListener('touchmove', img_mousemove, false);
		$(".next").bind("click",function(){
			var imgEdit = $("#imgEdit"),
				grayscale = imgEdit.css("-webkit-filter"),
				grayscaleNum = 0,
				data = Object;
				if(grayscale){
					grayscaleNum = grayscale.indexOf("(");
					grayscale = grayscale.slice((grayscaleNum+1));
					grayscaleNum = grayscale.indexOf(")");
					grayscale = grayscale.slice(0,grayscaleNum);
				}else{
					grayscale = 0.8;
				}
				data = {
					"imgW": imgEdit.width(),
					"imgH": imgEdit.height(),
					"x": -parseFloat(imgEdit.css("left")),
					"y": -parseFloat(imgEdit.css("top")),
					"boxW": imgEdit.parent(".imgEditorBox").height(),
					"grayscale": grayscale
				}
			kindleVoyageMo.location("action.php?act=cut_img",data);
		})
		
	},
	editSy: function(Dom,Dom1){
		kindleVoyageMo.click(Dom,function(thisDom){
			thisDom.addClass("selected");
			$("."+Dom1).removeClass("selected");
			thisDom.parent(".syBtn").animate({
				"marginLeft": "22%"
			},200,function(){
				$(".createBtn").addClass("selected");
				$(".edit_text").removeClass("dis_none");
				$(".edit_input").addClass("dis_none");
			});
			kindleVoyageMo.location("action.php?act=rand_word","",function(data){
				$(".cue").html(data.data.word).attr("sy_id",data.data.id);
				$(".authorName").html(data.data.author);
				$(".bookName").html(data.data.title);
			});
		});
		kindleVoyageMo.click(Dom1,function(thisDom){
			thisDom.addClass("selected");
			$("."+Dom).removeClass("selected");
			thisDom.parent(".syBtn").animate({
				"marginLeft": "-20%"
			},200,function(){
				$(".createBtn").removeClass("selected");
				$(".edit_text").addClass("dis_none");
				$(".edit_input").removeClass("dis_none");

			});
		});
		var f_click = $(".f_click");
		// console.log(f_click.length)
		f_click.each(function(i,v){
			f_click.eq(i).bind("click",function(){
				var thisDom = $(this);
				if( thisDom.html()=="作者名字" || thisDom.html()=="书集名字" ) {
					thisDom.html("");
				}
			});
			f_click.eq(i).bind("blur",function(){
				var thisDom = $(this);
				if( thisDom.html() == "" ) {
					thisDom.html(thisDom.attr("alt"));
					$(".createBtn").removeClass("selected");
				} else if(f_click.eq(0).html!="" && f_click.eq(0).html!="作者名字" && f_click.eq(1).html!="书集名字" &&f_click.eq(1).html!="" && $(".shuyu_input").val()!="请点击输入您的书语" &&$(".shuyu_input").val()!=""){
					$(".createBtn").addClass("selected");
				}
			});
		});
		$(".createBtn").bind("click",function(){
			if($(this).hasClass("selected")){
				if($(".cue").attr("sy_id")){
					var wordId = $(".cue").attr("sy_id");
				}else{
					var wordId = 0;
				}
				var data = Object,
					latitude = 0,longitude = 0;
				if (navigator.geolocation) {
					var options={
						enableHighAcuracy: true,// 指示浏览器获取高精度的位置，默认为false
						timeout: 5000, // 指定获取地理位置的超时时间，默认不限时，单位为毫秒
						};
					navigator.geolocation.getCurrentPosition(function(position) {
							latitude  = position.coords.latitude;
							longitude = position.coords.longitude;
						},function(error) {
							latitude  = 0;
							longitude = 0 ;
						},options); //位置请求
				}
				
				if($(".change_sy").hasClass("selected")){
					var syLen = $(".cue").html().length,
						syTop = "",
						syBottom = "";
					if(syLen>17){
						syTop = $(".cue").html().slice(0,17);
						syBottom = $(".cue").html().slice(17);
					} else {
						syTop = $(".cue").html();
					}
					data = {
						"syTop": syTop,
						"syBottom": syBottom,
						"authorName": $(".authorName").html(),
						"bookName":  $(".bookName").html(),
						"x": parseFloat($(".edit_text").css("bottom")),
						"y": parseFloat($(".edit_text").css("right")),
						"latitude": latitude,
						"longitude": longitude,
						"wordId": wordId
					}
				} else if($(".edits_sy").hasClass("selected")){
					var syLen = $(".shuyu_input").val().length,
						syTop = "",
						syBottom = "";
					if(syLen>17){
						syTop = $(".shuyu_input").val().slice(0,17);
						syBottom = $(".shuyu_input").val().slice(17);
					} else {
						syTop = $(".shuyu_input").val();
					}
					data = {
						"syTop": syTop,
						"syBottom": syBottom,
						"authorName": $(".f_authorName").html(),
						"bookName":  $(".f_bookName").html(),
						"x": parseFloat($(".edit_text").css("bottom")),
						"y": parseFloat($(".edit_text").css("right")),
						"latitude": latitude,
						"longitude": longitude,
						"wordId": wordId
					}
				}
				kindleVoyageMo.location("action.php?act=word",data,function(data){
					if(data.error == 0){
						$("#btnClose").attr("href","share.php?openid="+ CONFIGS.OPENID+"&data_id="+data.data);
						$(".popup").removeClass('dis_none').data('data-id',data.data);
					}else{
						//失败报错
						alert(data.msg);
					}	
				});
			}
		});
	},
	syKeyUp: function(){
		$(".shuyu_input").bind("keyup",function(){
			$(".shuyu div span").html(34-$(this).val().length)
		});
		$(".shuyu_input").bind("blur",function(){
			var str = $(this).val();
			if(str.length>34){
				str = str.slice(0,34);
				$(this).val(str);
				$(".shuyu div span").html(34-$(this).val().length)
			}
		});
	},
	location: function(url,datas,callback){
		$.ajax({
			type:'post',
			data: datas,
			dataType:'json',
			url: url,
			success:function(data){
				if(callback){
					return callback(data);
				} else{
					if(data.url){
						window.location.href = data.url;
					} 
				}
			}
		});
	},
	addZan: function(Dom){
		$(Dom).click(function(){
			var add1 = $(this).find(".add1"),
				numBox = $(this).find("b");
			var data = $(this).attr("data-imgId");
			kindleVoyageMo.location("action.php?act=img_log",{data_id:data},function(data){
				if( data.error == 0 ){
					add1.show().animate({
                        "bottom": "25px",
                        "opacity": 0
                    }, 300, function(){
                        add1.hide().css("opacity", 1);
                    });
					numBox.html(parseFloat(numBox.html())+1);
				}else{
					alert(data.msg);
				}
			});
		});
	},
	syPopup: function(){
		$(".subMit").bind("click",function(){
			var ipt = $(".popup").find("input");
			if(ipt.eq(0).val()==""){
				ipt.eq(0).val("请填写姓名！")
			} else if(ipt.eq(1).val()==""){
				ipt.eq(1).val("请填写电话！")
			} else {
                var datas = {
                    "name"	  : $(".name input").val(),
                    "tel"	  : $(".tel input").val(),
					"data-id" : $(".popup").data('data-id')
                }
                kindleVoyageMo.location("action.php?act=touch",datas,function(data){
					if(data.error == 0){
						window.location.href = data.data;
					}else{
						//失败报错
						alert(data.msg);
					}
				});
			}
			
		});
		$(".popup").find("input").bind("focus",function(){
			if( $(this).val() == "请填写姓名！" || $(this).val() == "请填写电话！" ){
				$(this).val("");
			}
		});
	},
	ajax_upload_img: function(obj) {
		var button = $(obj) ;
		new AjaxUpload(button,{
			action: 'action.php?act=make',
			data:{
				'buttoninfo':button.text()
			},
			name: 'userfile',
			onSubmit : function(file, ext){
				if (ext && /^(jpg|png|jpeg|gif)$/i.test(ext)){
					$(".uploadLodingBox").show();
				} else {
					 alert('请上传jpg|png|jpeg|gif格式的图片');
					return false;
				}				
			},
			onComplete: function(file, response){
				var res = $.parseJSON(response);
				if(res.error){
					alert(res.msg);
				}else{
					window.location.href = res.url;
				}
			}
		});
	},
	toustartFun2: function(evt){
		var self = kindleVoyageMo;
		var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标
        //记录触点初始位置
        self._node.startX = x;
        self._node.startY = y;
	},
	toumoveFun2: function(evt){
		evt.stopPropagation();
		evt.preventDefault();
		var self = kindleVoyageMo;
		var touch = evt.touches[0]; //获取第一个触点
        var x = Number(touch.pageX); //页面触点X坐标
        var y = Number(touch.pageY); //页面触点Y坐标
 		x = self._node.startX - x;
        // //判断滑动方向
        if(x>10){
        	// left
        	self._node.direction ="left";
        } else if(x<-10){
        	// right;
        	self._node.direction ="right";
        }

	},
	touendFun2: function(evt){
		var self = kindleVoyageMo;
		var thisBox = $(this);
		
		if(self._node.direction == "left"){
			
			thisBox.animate({
				"marginLeft": "-95%"
			},200,function(){
				thisBox.css({
					"marginLeft": "-40%"
				})
				thisBox.find("li").eq(0).appendTo(thisBox);
				var thisClass = thisBox.find("li").eq(1).attr("class"),
					thisLinks= thisBox.find("li").eq(1).attr("links");
				$(".productInfo li").removeClass("selected");
				$(".productInfo ."+thisClass).addClass("selected");
				$(".productSliderTag li").removeClass("selected");
				$(".productSliderTag ."+thisClass).addClass("selected");
				$(".productBtn li").eq(1).find("a").attr("href",thisLinks);
				
			});
		} else if(self._node.direction == "right"){
			thisBox.animate({
				"marginLeft": "17%"
			},200,function(){
				thisBox.css({
					"marginLeft": "-40%"
				})
				thisBox.find("li").eq(2).prependTo(thisBox);
				var thisClass = thisBox.find("li").eq(1).attr("class"),
					thisLinks= thisBox.find("li").eq(1).attr("links");
				$(".productInfo li").removeClass("selected");
				$(".productInfo ."+thisClass).addClass("selected");
				$(".productSliderTag li").removeClass("selected");
				$(".productSliderTag ."+thisClass).addClass("selected");
				$(".productBtn li").eq(1).find("a").attr("href",thisLinks);
			});
		}
		self._node.distance = 0;
		self._node.direction = "";
	},
	productSlider: function(Dom){
		var self = kindleVoyageMo;
		var thisD = $("."+Dom);
		thisD[0].addEventListener("touchstart", self.toustartFun2, false);
		thisD[0].addEventListener("touchmove", self.toumoveFun2, false);
		thisD[0].addEventListener("touchend", self.touendFun2, false);
	},
	mapFun: function(){
		//add by jlzhang -s
		//map下初始页数
		var mapPage = 0,
			dialogImgArr = [];
		//add by jlzhang -e
        
		var reader_small_arr = [
            {
                "desc": "开始，就是未来迎风飞扬1",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 0,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬2",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 10,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬3",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 20,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬4",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 30,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬5",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 40,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬6",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 50,
                "place": "BEIJING",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            }
        ];
        var reader_cover_arr = [
            {
                "desc": "开始，就是未来迎风飞扬1",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 110,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"test.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬2",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 0,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬3",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 20,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬4",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 30,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬5",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 40,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            },
            {
                "desc": "开始，就是未来迎风飞扬6",
                "booker": "——张德芬《遇见未知的自己》",
                "zanLen": 50,
                "place": "北京 西直门 金贸大厦",
                "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
            }
        ];
		
        function offDialog(){
            $("#dialog_reader").hide();
            $("#map #dialog_reader .dialog_reader_top ul li").remove(); 
        }
        function showDialogCont( data_map, type){
            var usualLiW = $(window).width()*0.9;
            if(type == "map"){
            	var _lis = "";
            	$("#map #dialog_reader .dialog_reader_top ul").width(usualLiW + 15);

            	_lis += '<li style="width: '+usualLiW+'px;"><div class="cont">'+
	                '    <img src="'+data_map.imgUrl+'">'+
	                '     <div class="close">'+
	                '        <img class="offlog" src="'+imgUrl+'close_icon.png">'+
	                '    </div>'+
	                '    <div class="tips">'+
					/*
	                '        <p class="name">chouray</p>'+
	                '        <p class="location">'+
	                '            <span>'+cdata.place+'</span>'+
	                '            <img src="'+imgUrl+'icon_localmark.png">'+
	                '        </p>'+
	                '        <p class="line"><span></span></p>'+
	                '        <h4 class="text">'+cdata.desc+'</h4>'+
	                '        <p class="readerName">'+cdata.booker+'</p>'+
					*/
	                '    </div>'+
	                '</div></li>';

	            $("#map #dialog_reader .dialog_reader_top ul").html(_lis);
	            $("#map #dialog_reader .dialog_reader_btm .top b").html(data_map.zanLen);

	            $("#map #dialog_reader .dialog_reader_top .btn_right, #map #dialog_reader .dialog_reader_top .btn_left").hide();
	            //展示并定位位置
	            $("#dialog_reader").css("top", -Number($("#map .contentBox").attr("tdy"))).fadeIn();
	            $("#map #dialog_reader .dialog_reader_top .cont").height($(window).width()*0.9)
	            var _dis = ($(window).height() - $("#map .dialog_reader_box").height())/2;
	            $("#map .dialog_reader_box").css("margin-top", _dis);

	            for(var i = 0; i < $("#map .dialog_reader_box .offlog").length; i++){
	                $("#map .dialog_reader_box .offlog").eq(i)[0].addEventListener('touchstart',offDialog, false);
	            }

	            $ulBox[0].removeEventListener( 'touchstart', startMoveBox, false );
		        $ulBox[0].removeEventListener( 'touchmove', moveMoveBox, false );
		        $ulBox[0].removeEventListener( 'touchend', endMoveBox, false );
            }else{
            	var _cIdx = $(this).index();
            	var usualLiW = $(window).width()*0.9;
	        	$("#map #dialog_reader .dialog_reader_top ul").width(usualLiW*dialogImgArr.length + 15);
	        	$("#map #dialog_reader .dialog_reader_top ul").attr("data-idx", _cIdx)
				var _lis = "";
				// console.log(dialogImgArr)
	        	for(var i = 0; i < dialogImgArr.length; i++){				
		            var cdata = dialogImgArr[i];

		            _lis += '<li style="width: '+usualLiW+'px;"><div class="cont">'+
		                '    <img src="'+cdata.imgUrl+'">'+
		                '     <div class="close">'+
		                '        <img class="offlog" src="'+imgUrl+'close_icon.png">'+
		                '    </div>'+
		                '    <div class="tips">'+
						/*
		                '        <p class="name">chouray</p>'+
		                '        <p class="location">'+
		                '            <span>'+cdata.place+'</span>'+
		                '            <img src="'+imgUrl+'icon_localmark.png">'+
		                '        </p>'+
		                '        <p class="line"><span></span></p>'+
		                '        <h4 class="text">'+cdata.desc+'</h4>'+
		                '        <p class="readerName">'+cdata.booker+'</p>'+
						*/
		                '    </div>'+
		                '</div></li>';
	            }

				$("#map #dialog_reader .dialog_reader_top ul").html(_lis);
				$("#map #dialog_reader .dialog_reader_btm .top b").html(dialogImgArr[_cIdx].zanLen);
				
	            $("#map #dialog_reader .dialog_reader_top ul .close").hide();
	            $("#map #dialog_reader .dialog_reader_top ul li").eq(_cIdx).find(".close").show();
	            // $("#map #dialog_reader .dialog_reader_top ul").css("margin-left", - usualLiW*_cIdx)
	            $("#map #dialog_reader .dialog_reader_top ul")[0].style.webkitTransform = 'translate3d('+ (- usualLiW*_cIdx) +'px, 0, 0)';
	            $("#map #dialog_reader .dialog_reader_top ul").attr("data-distance", (- usualLiW*_cIdx));

	            $("#map #dialog_reader .dialog_reader_top .btn_right, #map #dialog_reader .dialog_reader_top .btn_left").show();
	            //展示并定位位置
	            $("#dialog_reader").css("top", -Number($("#map .contentBox").attr("tdy"))).fadeIn();
	            $("#map #dialog_reader .dialog_reader_top .cont").height($(window).width()*0.9)
	            var _dis = ($(window).height() - $("#map .dialog_reader_box").height())/2;
	            $("#map .dialog_reader_box").css("margin-top", _dis);

	            for(var i = 0; i < $("#map .dialog_reader_box .offlog").length; i++){
	                $("#map .dialog_reader_box .offlog").eq(i)[0].addEventListener('touchend',offDialog, false);
	            }

	            $ulBox[0].removeEventListener( 'touchstart', startMoveBox, false );
		        $ulBox[0].removeEventListener( 'touchmove', moveMoveBox, false );
		        $ulBox[0].removeEventListener( 'touchend', endMoveBox, false );

	            $ulBox[0].addEventListener( 'touchstart', startMoveBox, false );
		        $ulBox[0].addEventListener( 'touchmove', moveMoveBox, false );
		        $ulBox[0].addEventListener( 'touchend', endMoveBox, false );
            }
        }

        //添加touch事件
        var $ulBox = $("#dialog_reader .dialog_reader_top ul");
        var _u_baseY, _u_startX, _u_dispos;

        function startMoveBox(e){
        	e.preventDefault();
            _u_dispos = 0;
            _u_startX = e.touches[0].clientX;
            _u_baseY = $ulBox.attr("data-distance") ? Number($ulBox.attr("data-distance")) : 0;
        }

        function moveMoveBox(e){
        	e.preventDefault();
        	_u_dispos = e.touches[0].clientX - _u_startX;

            $ulBox[0].style.webkitTransition = 'none';
            $ulBox[0].style.webkitTransform = 'translate3d('+ (_u_baseY + _u_dispos) +'px, 0, 0)';
            $ulBox.attr("data-distance", (_u_baseY + _u_dispos));
        }

        function endMoveBox(e){
        	e.preventDefault();
        	var _liW = $ulBox.find("li").width();
    		var _idx = Number($ulBox.attr("data-idx"));

        	if(Math.abs(_u_dispos) > 50){
        		if(_idx >= 0 && _idx <= (dialogImgArr.length - 1)){

        			if(_u_dispos < 0){
            			if(!(_idx == (dialogImgArr.length - 1))){
            				_idx++;
            			}
            		}else{
            			if(!(_idx == 0)){
            				_idx--;
            			}
            		}
            	}
        	}

    		$ulBox[0].style.webkitTransition = '-webkit-transform 0.2s ease-out';
    		$ulBox[0].style.webkitTransform = 'translate3d(-'+ (_liW*_idx) +'px, 0, 0)';
    		$ulBox.attr("data-idx", _idx);
    		$ulBox.attr("data-distance", - _liW*_idx);

    		$ulBox.find(".close").hide();
            $ulBox.find("li").eq(_idx).find(".close").show();
            $("#map #dialog_reader .dialog_reader_btm .top b").html(dialogImgArr[_idx].zanLen);
        }

        function rendering_dialog(num){
        	var $ul = $("#map #dialog_reader .dialog_reader_top ul");
        	var $li = $ul.find("li");

        	if(num >= 0 && num < $li.length){
        		var usualLiW = $(window).width()*0.9;

	        	$ul.find(".close").hide();
	            $li.eq(num).find(".close").show();
	        	// $ul.animate({"margin-left": - usualLiW*num}, 500, function(){
	        	// 	$ul.attr("data-idx", num);
	        	// });

	        	$ul[0].style.webkitTransition = '-webkit-transform 0.2s ease-out';
	    		$ul[0].style.webkitTransform = 'translate3d(-'+ (usualLiW*num) +'px, 0, 0)';
	    		$ul.attr("data-idx", num);
	    		$ul.attr("data-distance", - usualLiW*num);
	    		$("#map #dialog_reader .dialog_reader_btm .top b").html(dialogImgArr[num].zanLen);
        	}
        }

        $("#map #dialog_reader .dialog_reader_top .btn_left")[0].addEventListener("touchend", function(){
        	var _idx = Number($("#map #dialog_reader .dialog_reader_top ul").attr("data-idx")) - 1;

        	rendering_dialog(_idx);
        })

        $("#map #dialog_reader .dialog_reader_top .btn_right")[0].addEventListener("touchend", function(){
        	var _idx = Number($("#map #dialog_reader .dialog_reader_top ul").attr("data-idx")) + 1;

        	rendering_dialog(_idx);
        })

        //添加点击事件
        for(var i = 0; i < $("#map .watchReaderCont ul li").length; i++){
            $("#map .watchReaderCont ul li").eq(i)[0].addEventListener('touchstart',showDialogCont, false);
        }

        function rendering_small(data){
            var cdata = data.imgs;
            var cdataLen = data.imgs.length;

            for(var i = 0; i < cdataLen; i++){
                var $cli = $("<li></li>");

                $cli.html(
                    '<img src="'+cdata[i].imgUrl+'">'+
                    '<div class="tips">'+
					/*
                    '    <h4 class="text">'+cdata[i].desc+'</h4>'+
                    '    <p class="name">'+cdata[i].booker+'</p>'+
					*/
                    '    <div class="zan">'+ 
                    '        <span class="left">'+
                    '            <b><img src="'+imgUrl +'icon_zan.png"></b>'+
                    '            <a>'+cdata[i].zanLen+'</a>'+
                    '        </span>'+
                    '        <span class="right">'+
                    '            '+cdata[i].place+
                    '        </span>'+
                    '    </div>'+
                    '</div>'
                ).data(cdata[i]);

                $("#map .watchReader .watchReaderCont ul").append($cli);
            }

            var $cLi = $("#map .watchReader .watchReaderCont ul li");
            $cLi.removeClass("center");

            for(var i = 0; i < $cLi.length; i++){
                if(i%3 == 1){
                    $cLi.eq(i).addClass("center");
                }
            }

            //解绑原来的事件，并且重新绑定
            for(var i = 0; i < $("#map .watchReaderCont ul li").length; i++){
                $("#map .watchReaderCont ul li").eq(i)[0].removeEventListener('touchstart',showDialogCont, false);

                $("#map .watchReaderCont ul li").eq(i)[0].addEventListener('touchstart',showDialogCont, false);
            }
        }
        
		//add by jlzhang -s
		//加载图片改为通过ajax 加载 
		kindleVoyageMo.location("action.php?act=get_data",{page_id:mapPage},function(data){
			if(data.error == 0){
				rendering_small(data.data);
				var imgs = data.data.imgs;
				for(var i in imgs){
					dialogImgArr.push(imgs[i]);
				}
				mapPage++;
			}else{
				//alert(data.msg);
			}
		});
		//add by jlzhang -e

        var $obj = $("#map .contentBox");
        var $box = $("#map .watchReader");
        var touchStartY = 0, diffPX;
        var baseY = 0;

        function startHandler(e) {
            e.preventDefault();
            diffPX = 0;
            touchStartY = e.touches[0].clientY;
            baseY = $obj.attr("tdy") ? Number($obj.attr("tdy")) : 0;
        }

        function moveHandler(e) {
            e.preventDefault();
            diffPX = e.touches[0].clientY - touchStartY;

            $obj[0].style.webkitTransition = 'none';
            $obj[0].style.webkitTransform = 'translate3d(0,'+ (baseY + diffPX) +'px,0)';
            $obj.attr("tdy", (baseY + diffPX));
        }

        function endHandler(e) {
            e.preventDefault();
            var totalH = $("#map .contentBox").height();
            var clientH = $(window).height();

            var _tdy = Number($obj.attr("tdy"));
            var _dis = Math.abs(_tdy) - (totalH - clientH);

            if(diffPX >= 0 && _tdy > 0){
                $obj[0].style.webkitTransition = '-webkit-transform 0.2s ease-out';
                $obj[0].style.webkitTransform = 'translate3d(0, 0, 0)';

                $obj.attr("tdy", 0);
            }else if(diffPX < 0 && _dis > 0){
                if(_dis > 50){
                    //console.log("加载更多")
					kindleVoyageMo.location("action.php?act=get_data",{page_id:mapPage},function(data){
						if(data.error == 0){
							rendering_small(data.data);
							var imgs = data.data.imgs;
							for(var i in imgs){
								dialogImgArr.push(imgs[i]);
							}

							mapPage++;
						}else{
							alert(data.msg);
						}
					});
                }
                $obj[0].style.webkitTransition = '-webkit-transform 0.2s ease-out';
                $obj[0].style.webkitTransform = 'translate3d(0, '+(clientH - totalH)+'px, 0)';

                $obj.attr("tdy", (clientH - totalH));
            }
        }

        $box[0].addEventListener( 'touchstart', startHandler, false );
        $box[0].addEventListener( 'touchmove', moveHandler, false );
        $box[0].addEventListener( 'touchend', endHandler, false );

        function stopDefaultScroll(e){
            e.preventDefault();
        }
        $(".smallDoc")[0].addEventListener( 'touchstart', stopDefaultScroll, false );
        $(".smallDoc")[0].addEventListener( 'touchmove', stopDefaultScroll, false );
        $(".smallDoc")[0].addEventListener( 'touchend', stopDefaultScroll, false );

        function asynLoadQQMap(callback){
            var script = document.createElement("script");
            var callback = callback ? ("&callback=" + callback) : "";

            script.type = "text/javascript";
            script.src = "http://map.qq.com/api/js?v=2.exp&" + callback;

            document.body.appendChild( script );
        }

        asynLoadQQMap("afterFun");

        window.afterFun = function(){
        	var ajaxData = {
            	"total": 0,
            	"distance": 0,
            	"slat": 39.909604,
            	"slng": 116.397228,
            	"dlat": 39.93960,
            	"dlng": 116.427228,
            	"works": {
            		"imgId": 1,
            		"imgUrl": 1,
            		"vote": 0,
            	}
            };
            var road_label_arr = [];
            $('#road_map').height($(window).height()/3);
            $('#map .watchReader .watchReaderCont ul li').height($(window).height()/6);

            $.ajax({
				type:'post',
				dataType:'json',
				url: "_ajax_nearby.php",
				success:function(data){
					ajaxData = data;

					var local_position = new qq.maps.LatLng(ajaxData.slat, ajaxData.slng);
		            var close_person = new qq.maps.LatLng(ajaxData.dlat, ajaxData.dlng);

		            var road_map = new qq.maps.Map($('#road_map')[0],
		                {
		                    center: local_position,
		                    zoom: 13,
		                    disableDefaultUI: true
		                }
		            );
		            
		            //信息窗口类
		            var Label = function(opts){
		                qq.maps.Overlay.call(this, opts);
		            };

		            //继承Overlay基类
		            Label.prototype = new qq.maps.Overlay();
		            Label.prototype.construct = function() {
		                this.dom = document.createElement('div');
		                this.dom.style.cssText = 'position:relative;display: block;width: 202px;height: 75px;background-color: #096eb2;color: white;border-radius: 6px';

		                $(this.dom).html('<span style="display: block;width: 90px;float: left;padding-left: 10px;font-size: 12px;margin-top: 5px;border-right: 2px solid white;"><b style="display: block;font-size: 20px;padding: 5px 0;">'+this.get("manlength")+'个</b>附近读书的人</span><span style="display: block;width: 90px;float: left;height: 50px;padding-left: 10px;font-size: 12px;"><b style="display: block;font-size: 20px;padding: 5px 0;">'+this.get("faraway")+' km</b>最近读书的人</span><div style="position: absolute;left: 50%;bottom: -15px;margin-left: -20px;width: 0;height: 0;border-left: 20px solid transparent;border-right: 20px solid transparent;border-top: 15px solid #096eb2;"></div>')

		                //将dom添加到覆盖物层
		                this.getPanes().overlayLayer.appendChild(this.dom);
		            };

		            Label.prototype.draw = function() {
		                //获取地理经纬度坐标
		                var position = this.get('position');
		                if (position) {
		                    var pixel = this.getProjection().fromLatLngToDivPixel(position);
		                    this.dom.style.left = pixel.getX() - 102+ 'px';
		                    this.dom.style.top = pixel.getY() - 120 + 'px';
		                }
		            };

		            Label.prototype.destroy = function() {
		                //移除dom
		                this.dom.parentNode.removeChild(this.dom);
		            };

		            //添加当前位置标记 size origin anchor scaleSize
		            var road_icon = new qq.maps.MarkerImage(
		                imgUrl + "local_mark.png", 
		                new qq.maps.Size(34, 41),
		                new qq.maps.Point(0, 0),
		                new qq.maps.Point(12, 26),
		                new qq.maps.Size(22, 27)
		            );

		            var road_marker = new qq.maps.Marker({
		                map: road_map,
		                position: local_position
		            });

		            road_marker.setIcon(road_icon);

		            //当前位置标记添加监听事件
		            qq.maps.event.addListener(road_marker, 'click', function() {
		                //清楚原来的overlay
		                for(var i = 0; i < road_label_arr.length; i++){
		                    road_label_arr[i].destroy();
		                    road_label_arr.length = 0;
		                }

		                //添加当前标记的信息窗口
		                var road_label = new Label({
		                    map: road_map,
		                    manlength: ajaxData.total,
		                    faraway: ajaxData.distance,
		                    position: local_position
		                });

		                road_label_arr.push(road_label);
		            });

		            //添加最近读书人位置标记 size origin anchor scaleSize
		            var near_icon = new qq.maps.MarkerImage(
		                imgUrl + "close_person.png", 
		                new qq.maps.Size(21, 21),
		                new qq.maps.Point(0, 0),
		                new qq.maps.Point(10, 10),
		                new qq.maps.Size(21, 21)
		            );

		            var near_marker = new qq.maps.Marker({
		                map: road_map,
		                position: close_person
		            });

		            near_marker.setIcon(near_icon);

		            //最近读书人位置标记添加监听事件
		            qq.maps.event.addListener(near_marker, 'click', function() {
		                //清楚原来的overlay ooo
		             //    showDialogCont({
			            //     "desc": "开始，就是未来迎风飞扬6MAP",
			            //     "booker": "——张德芬《遇见未知的自己MAP》",
			            //     "zanLen": 158,
			            //     "place": "北京 西直门 金贸大厦MAP",
			            //     "imgUrl": imgUrl+"watchReaderListImg_1.jpg"
			            // }, "map");
		                showDialogCont({
			                "zanLen": ajaxData.works.vote,
			                "imgUrl": ajaxData.works.imgUrl
			            }, "map");
		            });

		            //添加标线，链接当前位置和最近的读书人
		            var polyline = new qq.maps.Polyline({
		                path: [
		                    local_position,
		                    close_person
		                ],
		                strokeColor: '#096eb2',
		                strokeWeight: 3,
		                strokeDashStyle: 'dash',
		                editable: false,
		                map: road_map
		            });
				}
			});
        }
	},
	loading: function(){
		var timer, timer2, timer3;
		var baseTime = 2000;
		var siTimeEnough = false;
		var imgPath = imgUrl+"ani/";
		var $imgArr = $("img[data-src]");
    	var $imgArrLen = $imgArr.length;

		var sourceArr = [
				"a1.jpg",
				"a2.jpg",
				"a3.jpg",
				"a4.jpg",
				"a5.jpg",
				"a6.jpg",
				"a7.jpg",
				"a8.jpg",
				"a9.jpg",
				"a10.jpg",
				"b1.jpg",
				"b2.jpg",
				"b3.jpg",
				"b4.jpg",
				"b5.jpg",
				"b6.jpg",
				"b7.jpg",
				"b8.jpg",
				"c1.jpg",
				"c2.jpg",
				"c3.jpg",
				"c4.jpg",
				"c5.jpg",
				"c6.jpg",
				"c7.jpg",
				"c8.jpg",
				"c9.jpg"
		    ];

		for ( var i = 0; i < sourceArr.length; i++ ) {
		    sourceArr[i] = imgPath + sourceArr[i];
		}

		for(var i = 0; i < $imgArrLen; i++){
	        var $cImgUrl = $imgArr.eq(i).attr("data-src");
	        sourceArr.push($cImgUrl)
	    }
	    // console.log(sourceArr)
		var loadImage = function ( path, callback ) {
		    var img = new Image();
		    img.onload = function () {
		        img.onload = null;
		        callback( path );

		        //图片预加载完成之后把值赋给页面相应的结构
	            var $img = $("img[data-src='"+ path +"']");
	            var $imgLen = $img.length;
	            for(var i = 0; i < $imgLen; i++){
	                $img.eq(i)[0].src = path;
	            }
		    };
		    img.src = path;
		};
		var imgLoader = function ( imgs, callback ) {
		    var len = imgs.length, i = 0;
		    while ( imgs.length ) {
		        loadImage( imgs.shift(), function ( path ) {
		            callback( path, ++i, len );
		        });
		    }
		};

		//loading 之后的动画
		var $img = $("#index .l_animate img");
		var $noAni = $("#index .l_animate .noneAni");

		var idArr = ["a", "b", "c"];
		var idLen = [10, 8, 9];

		var i = -1;
		var idx = 1;
		var timerAni = null;
		var timerAniFun = null;
		var timerAniFunLang = 3000;
		var baseUrl  = imgUrl+"ani/";
		var $aniTips = $("#index .loading .l_animate .tips");
		var tipsStyle = [{
			"left": "10px",
			"right": "initial",
			"top": "20px",
			"bottom": "initial",
			"t1": "︽沙漏︾饶雪漫",
			"t2": "又仿佛一线来自天堂的烟尘。",
			"t3": "白色的沙砾，缓缓地滴落下来。就像一串无尽头的泪水，"
		},{
			"left": "20px",
			"right": "initial",
			"top": "20px",
			"bottom": "initial",
			"t1": "︽万历十五年︾黄仁宇",
			"t2": "这种想象可以突破人世间的任何阻隔。",
			"t3": "富有诗意的哲学家说，生命不过是一种想象，"
		},{
			"left": "initial",
			"right": "20px",
			"top": "60px",
			"bottom": "initial",
			"t1": "︽谈美︾朱光潜",
			"t2": "每个人的生命史就是他自己的作品。",
			"t3": "人生本来就是一种较广泛的艺术，"
		},{
			"left": "20px",
			"right": "initial",
			"top": "initial",
			"bottom": "110px",
			"t1": "︽英国病人︾迈克·翁达杰",
			"t2": "是带着柔情，借口或是刀子来到他面前",
			"t3": "他觉得这样最安全。一言不发——不管他们"
		}]

		imgLoader( sourceArr, function ( path, curNum, total ) {
		    var percent = curNum / total;
		    $("#index .loading .loading_box .loading_percent span").width(percent*100 + "%")
		    if ( percent == 1) {
		        //展示页面=
		        if(!siTimeEnough){
		        	timer3 = setInterval(function(){
		        		if(siTimeEnough){
		        			//清除定时器
		        			clearInterval(timer)
		        			clearTimeout(timer2)
		        			clearInterval(timer3)

		        			//隐藏进度条
				        	$(".loading .loading_box").hide();
				        	$(".loading .l_animate").fadeIn();

				        	var runPageAni = function(){
				        		if(i < idArr.length - 1){
									timerAni = setInterval(function(){
										$img[0].src = baseUrl + idArr[i] +(idx++)+".jpg";

										if(idx > idLen[i]){
											clearInterval(timerAni);
											idx = 1;
										}
									}, 80)
									// debugger;
									i++;

									$aniTips.fadeOut();

									var delatTips = setTimeout(function(){
										$aniTips.css({
											"left": tipsStyle[i+1]["left"],
											"right": tipsStyle[i+1]["right"],
											"top": tipsStyle[i+1]["top"],
											"bottom": tipsStyle[i+1]["bottom"]
										})

										$aniTips.find("li").eq(0).html(tipsStyle[i+1]["t1"]);
										$aniTips.find("li").eq(1).html(tipsStyle[i+1]["t2"]);
										$aniTips.find("li").eq(2).html(tipsStyle[i+1]["t3"]);

										$aniTips.fadeIn();
										clearTimeout(delatTips);
									}, (80*idLen[i]))
									
									
								}else if(i == (idArr.length - 1)){
									//显示页面
									$(".loading").fadeOut();
									$("[tag-name='indexCont']").fadeIn();
									clearInterval(timerAniFun);
								}
				        	}
				        	
				        	timerAniFun = setInterval(runPageAni, timerAniFunLang);
		        		}
		        	}, 500)
		        }else{
		        	//清除定时器
        			clearInterval(timer)
        			clearTimeout(timer2)
        			clearInterval(timer3)

		        	//隐藏进度条
		        	$(".loading .loading_box").hide();
		        	$(".loading .l_animate").fadeIn();

		        	var runPageAni = function(){
		        		if(i < idArr.length - 1){
							timerAni = setInterval(function(){
								$img[0].src = baseUrl + idArr[i] +(idx++)+".jpg";

								if(idx > idLen[i]){
									clearInterval(timerAni);
									idx = 1;
								}
							}, 80)
							// debugger;
							i++;

							$aniTips.fadeOut();

							var delatTips = setTimeout(function(){
								$aniTips.css({
									"left": tipsStyle[i+1]["left"],
									"right": tipsStyle[i+1]["right"],
									"top": tipsStyle[i+1]["top"],
									"bottom": tipsStyle[i+1]["bottom"]
								})

								$aniTips.find("li").eq(0).html(tipsStyle[i+1]["t1"]);
								$aniTips.find("li").eq(1).html(tipsStyle[i+1]["t2"]);
								$aniTips.find("li").eq(2).html(tipsStyle[i+1]["t3"]);

								$aniTips.fadeIn();
								clearTimeout(delatTips);
							}, (80*idLen[i]))
							
							
						}else if(i == (idArr.length - 1)){
							//显示页面
							$(".loading").fadeOut();
							$("[tag-name='indexCont']").fadeIn();
							clearInterval(timerAniFun);
						}
		        	}
		        	
		        	timerAniFun = setInterval(runPageAni, timerAniFunLang);
		        }
		    }
		});

		$noAni.bind("click", function(){
			$("#index .l_animate").remove();
			clearInterval(timer);
			clearTimeout(timer2);
			clearInterval(timer3);
			$(".loading").fadeOut();
			$("[tag-name='indexCont']").fadeIn();
			clearInterval(timerAniFun);

		})

		//轮播loading tips
		$tips = $("#index .loading .loading_box .loading_tips ul");
		$tipsH = $("#index .loading .loading_box .loading_tips ul li").height();

		timer = setInterval(function(){
			$tips.animate({"marginTop": -$tipsH}, 400, function(){
				$tips.children().eq(0).appendTo($tips);
				$tips.css({"marginTop": 0})
			})
		}, baseTime)

		timer2 = setTimeout(function(){
			siTimeEnough = true;
		}, 5*baseTime);
	},
	closeDialog_1: function(){
		$("[ data-tag='dialog']").hide();
		if(document.getElementsByTagName("audio")[0]){
			var Media = document.getElementsByTagName("audio")[0];
			Media.play();
		}
	},
	dialogUsualFun: function(){
		$(".resize").imgReSize({
            imgH: 1008,
            imgW: 640,
            minW: 0,
            minH: 0
        });
		
	    $(".menu")[0].addEventListener("touchend", function () {

	        $(".navToggle11").toggleClass("open");
	        // alert($(" [data-tag='dialog']").length)
	        $("[ data-tag='dialog']").hide();

	        $("#dialog_menu").show();

	    });
	

		for(var i = 0; i < $("[data-tag='closeDialog']").length; i++){
		    $("[data-tag='closeDialog']").eq(i)[0].removeEventListener("touchend", kindleVoyageMo.closeDialog_1, false);
			$("[data-tag='closeDialog']").eq(i)[0].addEventListener("touchend", kindleVoyageMo.closeDialog_1, false);
		}

		$(".c")[0].addEventListener("touchend", function () {
			$(".navToggle11").removeClass("open");
			$("[ data-tag='dialog']").hide();
			$("#dialog_actionDetail").show();
		});
		$(".activityBtn")[0] && $(".activityBtn")[0].addEventListener("touchend", function () {
			$(".navToggle11").removeClass("open");
			$("[ data-tag='dialog']").hide();
			$("#dialog_actionDetail").show();
		});
		$(".d")[0].addEventListener("touchend", function () {
			$(".navToggle11").removeClass("open");
			$("[ data-tag='dialog']").hide();
			$("#dialog_rankingList").show();
		});
		$(".g")[0].addEventListener("touchend", function () {
			$(".navToggle11").removeClass("open");
			$("[ data-tag='dialog']").hide();
			$(".sp_alt").show();

			for(var i = 0; i < $("[data-tag='closeDialog']").length; i++){
			    $("[data-tag='closeDialog']").eq(i)[0].removeEventListener("touchend", kindleVoyageMo.closeDialog_1, false);

			    $("[data-tag='closeDialog']").eq(i)[0].addEventListener("touchend", kindleVoyageMo.closeDialog_1, false);
			}

			var Media = document.getElementsByTagName("audio")[0];
			Media.pause();
		});
	},
	audioSwitch: function(){
		var self = kindleVoyageMo;
		var Media = document.getElementsByTagName("audio")[0];
		$("body")[0].addEventListener("touchstart", function(){
			//alert(2);
			if(self._node.audioNum==0){
				Media.play();
				//alert(1);
				self._node.audioNum++;
			}
		}, false);
		$(".audioBtn")[0].addEventListener("touchend", function () {
			if($(this).hasClass("play")){
				Media.pause();
				$(this).removeClass("play");
				$(this).removeClass("windmill");
			} else {
				Media.play();
				$(this).addClass("play");
				$(this).addClass("windmill");
			}
		});
	},
	getPosition: function(){
		var latitude = 0,longitude = 0;
		if (navigator.geolocation) {
			var options={
				enableHighAcuracy: true,// 指示浏览器获取高精度的位置，默认为false
				timeout: 5000, // 指定获取地理位置的超时时间，默认不限时，单位为毫秒
				};
			navigator.geolocation.getCurrentPosition(function(position) {
					latitude  = position.coords.latitude;
					longitude = position.coords.longitude;
				}, function(position) {
					latitude  = 0;
					longitude = 0 ;
				},options); //位置请求
		}
		kindleVoyageMo.location("action.php?act=update_user_location",{latitude: latitude,longitude: longitude});
	},
	selectStartMake: function(){
		$(".selectStartMake")[0].addEventListener("touchend", function () {
			$(".blackBox").removeClass("dis_none");
		},false);
		$(".selectImg img").bind("click",function(){
			$(".selectImg img").each(function(i,v){
				$(".selectImg img").eq(i).removeClass("selected");
			});
			$(this).addClass("selected");
		});
		$(".selectImgBox .btn").bind("click",function(){
			// alert(1)
			var data = "";
			$(".selectImg img").each(function(i,v){
				if($(".selectImg img").eq(i).hasClass("selected")){
					data = $(".selectImg img").eq(i).attr("data-id");
				}
			});
			kindleVoyageMo.location("action.php?act=select_tpl",{data_id:data});
		});
		$(".selectImgBox .btnC").bind("click",function(){
			$(".blackBox").addClass("dis_none");
		});
	}
}
$(function(){
	($('.audioBtn')[0]) && kindleVoyageMo.audioSwitch();
	($('.clickStartMake')[0]) && kindleVoyageMo.ajax_upload_img(".clickStartMake");
	($('.selectStartMake')[0]) && kindleVoyageMo.selectStartMake();
	($('.menu')[0]) && kindleVoyageMo.getPosition();
	($('#index .menu')[0]) && kindleVoyageMo.loading();
	($('.making')[0]) && kindleVoyageMo.imgChangePosition("imgEditorBox");
	($('#making')[0]) && kindleVoyageMo.addZan(".zan");
	($('.making')[0]) && kindleVoyageMo.adjustGray("cursor");
	($('.edit_sy')[0]) && kindleVoyageMo.editSy("change_sy","edits_sy");
	($('.edit_sy')[0]) && kindleVoyageMo.syKeyUp();
	($('.edit_sy')[0]) && kindleVoyageMo.syPopup();
	($('#product')[0]) && kindleVoyageMo.productSlider("product ul");
	($('#map')[0]) && kindleVoyageMo.mapFun();
	($('#map')[0]) && kindleVoyageMo.addZan(".circle");
	//通用弹出框点击事件
	kindleVoyageMo.dialogUsualFun();
});


var userAgent = navigator.userAgent.toLowerCase();
// var isIpad = userAgent.match(/ipad/i) == "ipad";

// if (!isIpad) alert('不是IPAD！');

var oldX, oldY, startX, startY, startWidth, startHeight,moveT,moveL;
var moveD;
var isMove = false;
var isZoom = false;
var lastClickTime = 0;

// 获取两点之间的距离
function get_distance(x1, y1, x2, y2){
	return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2), 2);
}

function img_mousedown(e){
	if (e.target.id != 'imgEdit') return;

	if (e.touches.length == 1)
	{
		var nowTime = Math.round(new Date().getTime() / 1000);
		x = $('#imgEdit').position().left;
		y = $('#imgEdit').position().top;
		if (nowTime - lastClickTime < 1 && Math.abs(x - startX) < 20 && Math.abs(y - startY) < 20)
		{
			// 在1秒内连续点击同一地方。
			//alert('双击事件');
		}
		lastClickTime = nowTime;
	}
	else if (e.touches.length >= 2)
	{
		isMove = false;
		isZoom = true;
		x1 = e.touches[0].pageX;
		y1 = e.touches[0].pageY;
		x2 = e.touches[1].pageX;
		y2 = e.touches[1].pageY;

		startX = $('#imgEdit').position().left;
		startY = $('#imgEdit').position().top;
		startWidth = $('#imgEdit').width();
		startHeight = $('#imgEdit').height();
		// alert(startHeight);
		moveD = get_distance(x1, y1, x2, y2);

		return;
	}

	isMove = true;
	oldX = e.touches[0].pageX;
	oldY = e.touches[0].pageY;
	startX = $('#imgEdit').position().left;
	startY = $('#imgEdit').position().top; 
	e.preventDefault();
	e.stopPropagation();
	return false;
}

function img_mouseup(e){
	if (e.target.id != 'imgEdit') return;
	var startScaling = startWidth/startHeight,
		thisW =  $("#imgEdit").width(),
		thisH = $("#imgEdit").height(),
		thisScaling = thisW/thisH,
		boxH = $(".imgEditorBox").height(),
		boxW = $(".imgEditorBox").width();
	if(thisW/thisH<1){
		$("#imgEdit").css({
			"min-width": boxH,
			"min-heihgt": boxH/thisScaling
		})
	} else if(thisW/thisH>1){
		$("#imgEdit").css({
			"min-height": boxH,
			"min-width": boxH*thisScaling
		})
	};
	// console.log(moveT+"///"+moveL);
	if( moveT > 0 && moveL > 0 ){
		$("#imgEdit").css({
        	"left": 0,
        	"top": 0
        });	
        // alert(1);
	} if( moveT<-(thisH-boxH) && moveL<-(thisW-boxW) ){
		$("#imgEdit").css({
        	"left": -(thisW-boxW),
        	"top": -(thisH-boxH)
        });	
        // alert(11);
	}else if( moveT > 0 && moveL < 0 && moveL<-(thisW-boxW) ){
		$("#imgEdit").css({
        	"top": 0,
        	"left": -(thisW-boxW)
        });	
        // alert(12);
	} else if( moveL > 0 && moveT < 0 && moveT<-(thisH-boxH) ){
		$("#imgEdit").css({
        	"left": 0,
        	"top": -(thisH-boxH)
        });	
        // alert(13);
	} else if( moveT > 0 ){
		$("#imgEdit").css({
        	"top": 0
        });	
        // alert(112);
	} else if( moveL > 0 ){
		$("#imgEdit").css({
        	"left": 0
        });	
        // alert(113);
	} else if( moveT<-(thisH-boxH) ){
		$("#imgEdit").css({
        	"top": -(thisH-boxH),
        	"left": moveL
        });	
        // alert(14);
	} else if( moveL<-(thisW-boxW) ){
		$("#imgEdit").css({
        	"left": -(thisW-boxW),
        	"top": moveT
        });	
        // alert(15);
	}
	isZoom = false;
	isMove = false;
}

function img_mousemove(e){
	if (isZoom){
		//targetTouches changedTouches touches
		if (e.touches.length >= 2){
			var x1, y1, x2, y2, d1;
			x1 = e.touches[0].pageX;
			y1 = e.touches[0].pageY;
			x2 = e.touches[1].pageX;
			y2 = e.touches[1].pageY;
			d1 = get_distance(x1, y1, x2, y2);
			var rate = d1 / moveD;
			var w = startWidth * rate;
			var h = startHeight * rate;
			var thisW =  parseFloat($("#imgEdit").width()),
				thisH = parseFloat($("#imgEdit").height()),
				thisScaling = thisW/thisH;
			if(w>=284){
				if(w/h == thisScaling){
					$('#imgEdit').width(w);
					$('#imgEdit').height(h);
				} else {
					$('#imgEdit').width(w);
					$('#imgEdit').height(w/thisScaling);
				}
				// $('#imgEdit').width(w);
				// $('#imgEdit').height(h);
				$('#imgEdit').css('left', 0);
				$('#imgEdit').css('top', 0);
			}
			
		}

		return;
	}

	if (!isMove) return;
	x = e.changedTouches[0].pageX - oldX;
	y = e.changedTouches[0].pageY - oldY;

	$('#imgEdit').css('top', y + startY + 'px');
	$('#imgEdit').css('left', x + startX + 'px');
	moveT = y + startY;
	moveL = x + startX;
}
