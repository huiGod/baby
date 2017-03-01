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
    <title>ios10 提示</title>

    <link type="text/css" rel="stylesheet" href="<%=path %>/css/UUMall/swiper-3.3.1.min.css"/>
    <link type="text/css" rel="stylesheet" href="<%=path %>/css/UUMall/hd-ios10tips.css"/>

</head>
<body>
    <img class="hd-ios10-bg" src="../images/hd-ios10-bg.png"/>
    <div class="hd-ios10">
        <div class="hd-ios10-main">

            <div class="swiper-container">
                <div class="swiper-wrapper">
                    <div class="swiper-slide"><img src="../images/hd-ios10-1.png"/></div>
                    <div class="swiper-slide"><img src="../images/hd-ios10-2.png"/></div>
                    <div class="swiper-slide"><img src="../images/hd-ios10-3.png"/></div>
                    <div class="swiper-slide"><img src="../images/hd-ios10-4.png"/></div>
                    <div class="swiper-slide"><img src="../images/hd-ios10-5.png"/></div>
                    <div class="swiper-slide"><img src="../images/hd-ios10-6.png"/><div class="hd-ios10-btn" onclick="ios10Location();">确定</div></div>
                </div>
                <!-- 如果需要分页器 -->
                <div class="swiper-pagination"></div>

            </div>
        </div>
    </div>

    <script type="text/javascript" src="<%=path %>/js/UUMall/zepto.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/UUMall/swiper.min.js"></script>
    <script type="text/javascript">
        var mySwiper = new Swiper ('.swiper-container', {
            loop: false,
            // 如果需要分页器
            pagination: '.swiper-pagination',

        })
        function ios10Location(){
        	document.location="clickIos://";
        }
    </script>
</body>
</html>