<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"
	name="viewport">
<meta content="yes" name="apple-mobile-web-app-capable" />
<title>领取优惠券</title>
<link rel="stylesheet"
	href="//g.alicdn.com/msui/sm/0.6.2/css/sm.min.css">
<link rel="stylesheet"
	href="//g.alicdn.com/msui/sm/0.6.2/css/sm-extend.min.css">
<link rel="stylesheet" href="<%=path %>/css/css.css">
<style type="text/css">
.banner {
	min-height: 32vw;
	margin-bottom: 5px;
	background-color: #fff;
}

.banner img {
	width: 100%;
	height: 32vw;
	outline: none 0;
	border: none 0;
}

.banner .swiper-slide {
	height: 32vw;
	overflow: hidden;
}

.banner .swiper-pagination-bullet-active {
	background-color: #ff9c00;
}
.coupon_top{
	text-align: center;
}
.coupon_top img{
	width:100%;
	padding-bottom: 17px;
}
.coupon_center{
	font-size: 16px;
	font-weight: bold;
	color: #f63571;
}
.coupon_bottom{
	display: inine;
}
.coupon_bottom img{
	width:25%;
}
.recommend{
	font-size: 14px;
	color:#5b5b5b;
	font-weight: bold;
}
</style>
</head>
<body>
	<div class="page-group">
		<!-- 首页：diy -->
		<div class="page page-current">
			<div class="content pull-to-refresh-content infinite-scroll-bottom "
				data-distance="0">
				<!-- 默认的下拉刷新层 -->
				<div class="pull-to-refresh-layer" style="height: 1.8rem;">
					<div class="preloader"></div>
					<div class="pull-to-refresh-arrow"></div>
				</div>
				<div class="coupon_top">
					<img src="../images/getCoupon.png"/>
					<span class="coupon_center">恭喜你获得${money}元现金券</span>
					<div class="coupon_bottom">
						<img src="../images/get_left.png"/>
						<span class="recommend">为您推荐</span>
						<img src="../images/get_right.png"/>
					</div>
				</div>

				<div class="diy-wrapper wall infinite-scroll" id="AddMore">
					<!-- 商品展示 -->
				</div>
			</div>
		</div>
	</div>
	<script type='text/javascript'
		src='//g.alicdn.com/sj/lib/zepto/zepto.min.js' charset='utf-8'></script>
	<script src="<%=path %>/js/jaliswall.js" type="text/javascript"
		charset="utf-8"></script>
	<script type='text/javascript'
		src='//g.alicdn.com/msui/sm/0.6.2/js/sm.min.js' charset='utf-8'></script>
	<script type='text/javascript'
		src='//g.alicdn.com/msui/sm/0.6.2/js/sm-extend.min.js' charset='utf-8'></script>
	<script>
	var _banner = $('#bannerSwiper');
    // 初始化首页
   	$.ajax({
           url: 'getBanner.do',
           data:{},
           type: 'POST',
           dataType: 'json',
           cache:false,
           async: false,
           timeout:"60000",
           success:function(data){
               if(data.code==200){
                   var data = data.data;
                   var goodsType15="";
                   $.each(data, function(index, val) {
                       var goods_type = 3;
                       //2精品专区3广告滚动4团体定制5图标按钮6广告
                       switch(goods_type){
                           case 3:
                           		goodsType15 += "<div class='swiper-slide'><a href='goodsInfo.do?id="+data[index].id+"' class='external link'><img src='"+data[index].imgUrl+"' /></a></div>";
                               break;
                           default :
                               break;
                       }
                   });
                   _banner.append(goodsType15);
               }else{
                   return false;
               }                   
           }
       });
	// 初始化布局
	$('.wall').jaliswall({ item: '.diy-content' });	
	// 初始化
	$.init()
	// 添加'refresh'监听器  下拉刷新
	$.initPullToRefresh(".pull-to-refresh-content"); 
	//查询页码
	var page=1;
	var loading = false;
	var queryData=new (function(){
		
		var showdata =function(e){
			var evt= e;
			var column0=$(".wall-column").eq(0);
			var column1=$(".wall-column").eq(1);
			if (loading) return;
			loading = true;
			//ajax请求精品汇数据
			$.ajax({
				url: "nice.do",
				type: 'POST',
				dataType: 'json',
			    cache:false,
			    timeout:"60000",
			    data:{page:page},
			    success:function(data){				    	    					    	    	
					//console.log(page);
			    	if(data.code==200){
			    		var p_name = column0.find(".P-Name").eq(0).text();
			    		var j_name = data.data[0].name;
			    		if(evt=="refresh"&& p_name==j_name){  return;}
			    		else if (evt=="refresh"){column0.empty();column1.empty();}
			    		
			    		//迭代商品
			    		$.each(data.data, function(index, val) {			    			
			        		var x=column0.height();
							var y=column1.height();
				        	var	str="<div class='diy-content linec nice-to' onclick='sending("+data.data[index].id+")'><div class='diy-goods'><div class='diy-pic-box'><img class='fix-w' src="+data.data[index].icoUrl+"></div><div class='diy-details'><h2 class='clearfix fc5 line-h22'><i class='fl fs12 line-h24 fb'>¥</i><span class='fl fs17 fb'>"+data.data[index].now_price+"</span> <i class='fr fs9 fc2'>已售<i class='fb fc5 fs12'>"+data.data[index].sell+"</i>件</i></h2><p class='fs12 fc-b ov-hidden P-Name'>"+data.data[index].name+"</p></div></div></div>"
				        	if(evt=="refresh"){
				        		var temp=(x>y)? column1.append(str):column0.append(str); 
				        		 loading = false;
				        	}else if(evt=="infinite"){
				        		var temp=(x>y)? column1.append(str):column0.append(str);
				        		loading = false;
				        	}else{loading = false;return false;}		        		       		
			       		});
			    	}else{
							loading=false;
			    			//$.toast("没有更多...",1000);
			    		 }
			    }
			});
		};
		var show=function(e){
			var evt=e.type; 
			if(evt=="refresh"){
				page=1;
        		setTimeout(function(){showdata(evt);$.pullToRefreshDone('.pull-to-refresh-content');},1000);
        	}else if(evt=="infinite"){
				page++;
        		 setTimeout(function(){showdata(evt); $.refreshScroller();},100);
        	}else{return false;}			
		}
		$(document).on('infinite', '.infinite-scroll-bottom',show);
		$(document).on('refresh', '.pull-to-refresh-content',show);	
		showdata("refresh");	
	});
	//banner轮播初始化
	/* var mySwiper = myApp.swiper('.swiper-container.banner', { */
	 $('.swiper-container.banner').swiper({ 
	    speed: 300,
	    spaceBetween: 10,
	    autoplayDisableOnInteraction : false,
	    observer:true,
	    autoplay:3000,
	    pagination:'.banner-pagination',
	    loop:true

	}) ;
	</script>
	<script type="text/javascript">
		function sending(id){
			location.href="<%=path%>/goods/goodsInfo.do?id="+id;
		}
	</script>
</body>
</html>