(function($){
    $.fn.imgReSize = function(options){
        var defaults = {
            imgW: 2700, //图片宽度  （必设）
            imgH: 1450, //图片高度  （必设）
            minW: 635,  //设置最小宽度    （可以不设，默认635）
            minH: 417   //设置最小高度    （可以不设，默认417）  
        }
        var options = $.extend(defaults, options);
        var scaling = options.imgW/options.imgH,
            winW = document.body.clientWidth,
            winH = document.body.clientHeight;

        if( winW > options.minW && winH > options.minH ){
            //console.log(1);
            if(winW/scaling<winH && winH*scaling<winW){
                //console.log(11);
                $("."+$(this).attr('class')).height("100%");
                $("."+$(this).attr('class')).width($("."+$(this).attr('class')).height()*scaling);
                $("."+$(this).attr('class')).css({
                    left: (winW-$("."+$(this).attr('class')).width())/2
                });
            } else if(winH*scaling<winW){
                //console.log(12);
                $("."+$(this).attr('class')).width(winW).height(winW/scaling);
                $("."+$(this).attr('class')).css({
                    top: (winH-$("."+$(this).attr('class')).height())/2,
                    left: 0
                });
            } else if(winH*scaling>=winW){
                //console.log(13);
                $("."+$(this).attr('class')).width(winH*scaling).height(winH);
                $("."+$(this).attr('class')).css({
                    left: (winW-$("."+$(this).attr('class')).width())/2,
                    top: 0
                });
            }
        } else if( winW < options.minW && winH < options.minH ){
            //console.log(2);
            $("."+$(this).attr('class')).width(options.minH*scaling).height(options.minH);
            $("."+$(this).attr('class')).css({
                left: (-($("."+$(this).attr('class')).width()-winW))/2,
                top: (-($("."+$(this).attr('class')).height()-winH))/2
            });
        } else if( winW < options.minW ){
            //console.log(3);
            $("."+$(this).attr('class')).width(winH*scaling).height(winH);
            $("."+$(this).attr('class')).css({
                left: (winW-$("."+$(this).attr('class')).width())/2,
                top: 0
            });
        } else if(winH < options.minH){
            //console.log(4);
            $("."+$(this).attr('class')).width(winW).height(winW/scaling);
            $("."+$(this).attr('class')).css({
                top: (winH-$("."+$(this).attr('class')).height())/2,
                left: 0
            });
        } 
    };
})(jQuery);
