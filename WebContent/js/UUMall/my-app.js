function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    }
var myApp = new Framework7({
    pushState: true,
    pushStateSeparator:'#subPage#',
    animatePages:false,
    onAjaxStart: function (xhr) {
        myApp.showIndicator();
    },
    onAjaxComplete: function (xhr) {
        myApp.hideIndicator();
    }
});
var $$ = Dom7;
var mainView = myApp.addView('.view-main', {
    dynamicNavbar: false,
    domCache:true
});
// 弹窗模块
var toastWarning = function() {
    var fn = {}, $$ = Framework7.$, body = $$('body'), handle
      , bubble = $$('<div class="warn-bubble">' +
                        '<span>91特惠</span>' +
                    '</div>');

    fn.say = function(str) {
        var str = arguments[0]?arguments[0]:"已过活动时间";
        bubble.detach().find('span').text(str).parent().appendTo(body);

        clearTimeout(handle);
        handle = setTimeout(function() {
            bubble.detach();
        }, 1700);
    };
    return fn;
}();
if($$('body').hasClass('indexBody-kwj')){
        /* 校正页面数字样式
        ==================================================*/
	 var handleSpecialFont = new (function() {
         var Method = function(i, d) {
             var that = $$(d);
             var arr = (parseFloat(that.text()).toFixed(2) + '').split(".");
             var str = $$('<span class="special-font-large"></span>').text(arr[0])[0].outerHTML+ 
                  '.' +$$('<span class="special-font-small"></span>').text(arr[1])[0].outerHTML;
             that.html(str);
         };
         $$('.special-num').each(Method);
     })();

    var contentObj = $$("#index-content")[0];
    var parentEle = $$("body");
    overscroll(contentObj,parentEle);
    // 通过页面回调
    myApp.onPageInit('themeGoodsPage', function(page){
        var goodsListScroll = $$('.page.page-on-right').find(".goodsListScroll")[0];
        overscroll(goodsListScroll,parentEle);
    });
    myApp.onPageBeforeAnimation('themePage', function(page){
        var goodsListScroll = $$('.page.page-on-right').find('.themeListScroll')[0];
        overscroll(goodsListScroll,parentEle);
    });
    // 读取cookie
    var getCookie = document.cookie.substring(5);
    if(getCookie.indexOf("animation-expires")==-1){
        // 添加cookie
        var dateObj = new Date();
        dateObj.setMinutes(dateObj.getMinutes()+30);
        document.cookie = "name=animation-expires;expires="+dateObj;
        // 提示动画
        window.onload=function(){
            $$('.tip-right').css("display","block");
            setTimeout(function(){$$('.tip-right').addClass('tip-right-animation');},300);
        };
        document.getElementById("tip-right").addEventListener("webkitTransitionEnd", function(e){e.target.style.display = "none";});
        document.getElementById("tip-right").addEventListener("transitionend", function(e){e.target.style.display = "none";});
    }
    // 导航条定位
    var statusChange = new (function(){
        var specialId = location.href.split('special_ids=')[1];
        var handle = function(i,d){
            var that = d;
            var titleSpecialId = that.href.split('=')[1];
            if(titleSpecialId==specialId){
                $$(that).find('span').addClass('active').closest(".swiper-slide").siblings().find('span').removeClass('active');
                var offset_left = $$(that).offset().left;
                var window_width = $$(window).width();
                if(offset_left+30>window_width){
                    var translatex = (offset_left-window_width) + (window_width/2) + (($$(that).width())/2);
                    var translatex_last =  (offset_left-window_width) + ($$(that).width()+30);
                    $$(that).closest(".swiper-slide").is(":last-child")?($$('#themeTitleList').css("-webkit-transform","translate3d(-"+translatex_last+"px, 0px, 0px)")):
                                                                        ($$('#themeTitleList').css("-webkit-transform","translate3d(-"+translatex+"px, 0px, 0px)"));
                }
            }
        }
        $$('#themeTitleList .switchTheme').each(handle);
    })();
}

//banner轮播初始化
var mySwiper = myApp.swiper('.swiper-container.banner', {
    speed: 300,
    spaceBetween: 10,
    autoplayDisableOnInteraction : false,
    observer:true,
    autoplay:3000,
    pagination:'.banner-pagination',
    loop:true

});
// var mySwiper2 = myApp.swiper('.tips', {
//     height: 40,
//     direction: 'vertical',
//     autoplay:5000,
//     speed: 300,
//     observer:true,
//     noSwiping : true,
//     autoplayDisableOnInteraction : false
// });
var mySwiper3 = myApp.swiper('.customGoods', {
    speed: 300,
    slidesPerView: "auto",
    observer:true
});
var mySwiper4 = myApp.swiper('.themeTitleList', {
    speed: 300,
    slidesPerView: "auto",
    observer:true
});
//切换页面，停止swiper，回到主页的时候重启swiper
$$(document).on('pageBeforeInit',function(e){
    mySwiper.stopAutoplay();
});
$$(".page[data-page='index']").on('pageReinit',function(e){
    mySwiper.startAutoplay();
});

// 切换主题
$$(document).on('click',".switchTheme",function(){
    $$(this).find('span').addClass('active').parent().parent().siblings().find('span').removeClass('active');
    if($$(this).closest('.swiper-slide').index()==0){
        mainView.router.loadPage({
            pageName: 'index',
            force: true
        });
    }
});

// 商品详情tab
var goodsInfoTabClickEvent = new (function(){
    var $li=$$(".tab2nav li");
    $li.on("click",function(){
        var tabIndex = $$(this).index();
        $$(this).addClass("clicked").siblings().removeClass('clicked');
        $$(".wrapper .details").hide().eq(tabIndex).show();
    });
})();

// 商品页面点击事件  商品属性暂支持2个选择
if($$('body').hasClass('goodsInfoBody-kwj')){
    var goodsClickEvent = new (function(){
        var that = this;
        var $ = Dom7;
        var $num=$(".input-num");
        var $rdcBtn=$(".reduce");
        var texture_ids = $(".defaultMetarialIds").text().split("_");

        $.each(texture_ids,addSelectStyle);

        $(document).on("click",".swicthTab",function(){
             myApp.showTab('#tab2');
        });
        $(document).on("click",".addCar,.buyNow",function(){
            if($(this).hasClass('addCar')){
                $(".ensureBtn").text("加入购物车");
            }else if($(this).hasClass('buyNow')){
                $(".ensureBtn").text("立即购买");
            }            
            myApp.popup('.popup-material');
        });
        $(document).on("click",".open-popup",function(){
            $(".ensureBtn").text("确定");
            myApp.popup('.popup-material');
        });    
        $(document).on("click",".closePopBtn,.ensureBtn,.clickToExit",function(){
            myApp.closeModal('.popup-material');
            transferParams();
        });
        $(document).on("click",".reduce",function(){
            var num=parseInt($num.val());
            (num==2)?($rdcBtn.removeClass('reduce2'),$num.val(num-1)):((num==1)?(i=1):($num.val(num-1)));  
        });
        $(document).on("click",".add",function(){
            var num=parseInt($num.val());
            (num==1)?($rdcBtn.addClass('reduce2'),$num.val(num+1)):$num.val(num+1); 
        });
        
        var contentObj = $("#pop-GoodsContent")[0];
        var parentEle = $(".popup-material");
        overscroll(contentObj,parentEle);

        // 修改商品属性切换小图片
        $(document).on("click",".pop-selects p span",function(){ 
            $(this).addClass('pop-btn-selected').siblings().removeClass('pop-btn-selected');  //先改样式再处理

            var ids =  that.getSelectedIds();
            var pre_url = $(ids).find(".pre_url").text();
            var now_price = $(ids).find(".now_price").text();
            var box_url = $(ids).find(".box_url").text();
                  
            $("#gNowPrice").html(now_price);
            $("#gImg").attr("src",pre_url);
            $("#box_url").attr("src",pre_url);
        });

        function transferParams(obj){   //弹窗传参商品页
            var popPrice = $('#gNowPrice');
            var popMaterial = $(".pop-selects li .pop-btn-selected");
            var popNum = $(".input-num");
            var selectedParam = $(".selectedParam");
            var nowPrice = $("#nowPrice");
            var str = "";
            var handle = function(i,d){
                var obj = $(d);
                str += (obj.text()+" ");
            }
            var num = popNum.val();        

            nowPrice.text(popPrice.text());
            popMaterial.each(handle);
            str = str + num + "件";
            selectedParam.text(str);
        };
        function addSelectStyle(i,d){ //初始化弹窗默认选择样式
            var id = "#_" + texture_ids[i];
            $(".pop-selects").find(id).addClass("pop-btn-selected");
        }; 
        this.getSelectedIds = function(){   //获得被选元素的id来切换弹窗预览小图
            var checked = $(".pop-btn-selected");
            var checkedIds ="";
            $.each(checked, function(i){//循环得到不同的id的值
                checkedIds += checked.eq(i).attr("id");
            });
            return "."+checkedIds;
        };
    })();
    // 溢出滚动
    var contentObj = $$("#tab1contain")[0], 
        contentObjD1 = $$('.detail-1')[0];
    var parentEle = $$("#tab1"),parentEle2 = $$('#tab2');
    overscroll(contentObj,parentEle);
    overscroll(contentObjD1,parentEle2);
    $$('.navbar')[0].addEventListener('touchmove', function(e){e.preventDefault();});
    $$('.toolbar')[0].addEventListener('touchmove', function(e){e.preventDefault();});

    // 识别电话号码
    $$('.detail-3 div').eq(0).html("<a href='tel:0755-86715250' class='external'>0755-86715250</a>");
    // 分享单件商品回退主页
    // $$('#no-history').on('click',function(){
    //     console.log(history);
    //     alert(history.length,history)
    //     if(history.length == 1){
    //         window.location.assign("http://test.diy.51app.cn/diyMall2/UGoods/tohome.do");
    //     }
    // });
}


// 加入购物车 发送数据请求    2.立即购买
var addToShopCar = new (function(){
	var $ = Dom7;
	var ensureBtn = $(".ensureBtn");
	$(document).on("click",".ensureBtn",function(){
		var pathname = window.location.pathname;
		var popNum = $(".input-num").val();
		var goodsId = $("#goodsId").val();
		var texture_ids = goodsClickEvent.getSelectedIds().substr(2);
		if(ensureBtn.text()=="加入购物车"){
        	var url = pathname.substring(0, pathname.indexOf('/',1))+"/goods/addShop.do";
			$.ajax({
        		url: url,
        		data:{
        			infoId:goodsId,
        			textureIds:texture_ids,
        			num:popNum
        		},
                type: 'POST',
                dataType: 'json',
                cache:false,
                async: false,
                timeout:"60000",
                success:function(data){
                    if(data.code==200){
                        var num;
                        ($("#shopCarNum").text()=='')?(num=1):(num = parseInt($("#shopCarNum").text())+1);
                        $("#shopCarNum").text(num);
                    	// myApp.alert("加入购物车成功",'提示');
                        toastWarning.say('加入购物车成功'); 
                    }else if(data.code==300){
                        // myApp.alert(data.message,'提示');
                        toastWarning.say(data.message); 
                    }else if(data.code==401){
                        // myApp.alert(data.message,'提示');
                        toastWarning.say(data.message); 
                    	window.location.href = pathname.substring(0, pathname.indexOf('/',1))+"/goods/tonice.do";
                    }
                }
        	});
		}else if(ensureBtn.text()=="立即购买"){
            window.location.href=pathname.substring(0, pathname.indexOf('/',1))+"/goods/createOrder.do?infoId="+goodsId+"&textureIds="+texture_ids+"&num="+popNum;
        }
	});
})();

// 初始化商品页面
var initShopCar = new (function(){
    var $ = Dom7;
    var pathname = window.location.pathname;
    var url = pathname.substring(0, pathname.indexOf('/',1))+"/goods/shopNum.do";
    $.ajax({
        url: url,
        data:{},
        type: 'POST',
        dataType: 'json',
        cache:false,
        async: false,
        timeout:"60000",
        success:function(data){
            if(data.code==200){
                if(data.data){
                    $("#shopCarNum").text(data.data);
                }
            }
        }
    });
})();

// 发表评论
if($$(".commentList").length>0){
    var publicComment = new (function(){
        var $ = Dom7;
        $(document).on("click",".star",function(){
            $(this).add($(this).prevAll()).removeClass("star-no").nextAll().addClass("star-no");
            $('.starNum').val($(this).index());
        });
        $('.commentArea').on("input",function(){
            var wordNum = 4;
            var input_num = $('.commentArea').length;
            var commented_num = 0 ;
            var num = $(this).val().length;
            var handle = function(i,d){
                if($(d).val().length>wordNum){ commented_num ++;}
            };
            $('.commentArea').each(handle);
            // console.log(commented_num,input_num);
            (commented_num==input_num)?($(".submitCommentBtn").addClass("active")):($(".submitCommentBtn").removeClass("active"));
        });
        $(".submitCommentBtn").click(function(){
            // console.log(1)
            var $this = $(".submitCommentBtn");
            if($this.hasClass("active")){
                //提交ajax
                $('#commentForm').submit();
            }else{
               toastWarning.say('客官，评论不要少于5个字呦'); 
            }
            
        });
    })();
}
//溢出滚动
function overscroll(el,parentEle) { 
    el.addEventListener('touchstart', function() {
        var top = el.scrollTop,
            totalScroll = el.scrollHeight,
            currentScroll = top + el.offsetHeight;
        if(top === 0) {
          el.scrollTop = 1;
        } else if((currentScroll+1) === totalScroll) {
          el.scrollTop = top - 1;
        }else if(currentScroll==totalScroll){
            el.scrollTop = top - 1;
        }else if((currentScroll-1)==totalScroll){
            el.scrollTop = top - 1;
        }
    })
    el.addEventListener('touchmove', function(e) {
        if(el.offsetHeight < el.scrollHeight)
          e._isScroller = true;
    })
    parentEle.on('touchmove',function(e) {
      if(!e._isScroller) { e.preventDefault(); }
    })
};
// 兼容安卓position:absolute
var androidPosition = new (function(){
    if($$('html').hasClass('android')){
        $$('.tel,.v-code,.send-Vcode,.loginBtn').focus(function(){
            $$('.quickLogin').css("position","static");
        }).blur(function(){
            $$('.quickLogin').css("position","absolute");
        });
    }
})();