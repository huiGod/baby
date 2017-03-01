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
<body class="_js-hdexchange body-bg pos-body">
    <main class="hd-exchangeticket" data-url="/baby/goods/couponList.do" >
        <header class="hd-enableCoupons">
            <input type="text" maxlength="10" placeholder="输入您的代金券激活码">
            <button class="hd-enableBtn" data-url="/baby/goods/activeCode.do">激活</button>
        </header>
        <div class="hd-exchange"></div>
        <div class="hd-popup1">
            <div class="hd-popup">
                <div class="hd-popupText">
                    <div class="p-top">确定使用余额兑换<br/>代金券？</div>
                    <div class="p-btn">
                        <div class="p-btnNo">取消</div>
                        <div class="p-btnYes">确认</div>
                    </div>
                </div>
            </div>
        </div>

        <div class="hd-popup2">
            <div class="hd-popup">
                <div class="hd-popupText">
                    <div class="p-top"></div>
                    <div class="p-btn">
                        <div class="p-btnNo">取消</div>
                        <div class="p-btnYes">确认</div>
                    </div>
                </div>
            </div>
        </div>

    </main>



    <script type="text/javascript" src="<%=path %>/js/UUMall/zepto.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/UUMall/touch.js"></script>
    <script type="text/javascript" src="<%=path %>/js/UUMall/myCustom.js"></script>


</body>
</html>