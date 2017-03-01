<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String path=request.getContextPath();
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <title>91特惠- 优惠券</title>
    <link type="text/css" rel="stylesheet" href="<%=path %>/css/custom2-hdcoupons.css"/>

</head>
<body class="_js-coupons body-bg pos-body">
    <main class="getCoupons hd-getCoupons" data-url="${url1 }" >

        <a href="javascript:void(0);" onclick="toCoupon()" class="hd-moreCoupons">兑换代金券</a>

        <div class="hd-noCoupons">
            <img src="../images/noCoupons.png" alt=""/>
            <span>您还没有优惠券</span>
            <div class="hd-coupontitle">
                <p>* 优惠券可用在91特惠中使用</p>
                <p>* 任务失败可领取系统赠送的优惠券（限每天一次）</p>
                <p>* 点击兑换优惠券，更多优惠券等着你</p>
            </div>
        </div>

        <div class="hd-hasCoupons">

            <div class="hd-myCoupons">
                <!--优惠券-->
                <div class="hd-myYellow"></div>
                <!--代金券-->
                <div class="hd-myRed"></div>
            </div>
            <div class="hd-coupontitle">
                <p>* 优惠券可用在91特惠中使用</p>
                <p>* 任务失败可领取系统赠送的优惠券（限每天一次）</p>
                <p>* 点击兑换优惠券，更多优惠券等着你</p>
            </div>

        </div>

        <div class="hd-loseline"><span>已过期优惠券</span></div>

        <div class="hd-loseCoupons"></div>
    </main>



    <script type="text/javascript" src="<%=path %>/js/UUMall/zepto.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/UUMall/touch.js"></script>
    <script type="text/javascript" src="<%=path %>/js/UUMall/myCustom.js"></script>
    <script type="text/javascript">
    function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }
   	function toCoupon(){
   		var flag=GetQueryString("flag");
   		location.href="/baby/goods/activeCoupon.do?flag="+flag;
   	}
    </script>
</body>
</html>