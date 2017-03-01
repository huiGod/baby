<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1.0,user-scalable=no">
    <style type="text/css">
        *{-webkit-tap-highlight-color:rgba(0,0,0,0);}
        body,div,p,ul,li,h1,h2,h3,h4,h5,h6{margin:0;padding:0;}
        li{list-style:none;}
        html,body{text-align:center;-webkit-user-select:none;user-select:none;font:12px/1.5 'Ping Fang',Helvetica,sans-serif;}
        html{background:#F7F9F9;font-family:"Microsoft yahei"}
        .giveUpTask{position:absolute;height:100%;top:0;left:0;background-color:#fff;}
        .main{width:95%;background-color:#FFFFFF;margin:10px auto 15px auto;}
        .main .main-1-left img{width:70px;height:70px;margin:15px 10px 15px 15px;border-radius:9px;-webkit-border-radius:9px;-moz-border-radius:9px;}
        .left{text-align:left;}
        .main-a{overflow:hidden;position:relative;padding-left:26vw;min-height:22vw;padding-top:10px;}
        .main-a img{position:absolute;left:0;top:12px;width:19vw;height:19vw;margin-left:2vw;border-radius:15px;}
        .main-1-right h1{color:#000;font-size:17px;font-weight:bold;text-align:left;padding-top:8px;}
        .main-1-right p{color:#000;font-size:12px;}
        .main-b{clear:both;text-align:left;padding:15px;border-top:1px solid #F7F9F8;}
        .main-b ul li{margin-bottom:7px;color:#000;font-size:13px;}
        .main-b ul li span.title{border:dashed 1px #FF0000;border-radius:5px;width:28px;padding:7px 17px 7px 17px;margin-left:17px;font-weight:bold;}
        a.button{box-sizing:border-box;display:block;width:90%;margin:0 auto;font-size:14px;color:#FFF;padding:10px;background-color:#378CEF;border-radius:5px;font-family:"Microsoft yahei"}
        span{color:red;}
        a.submitBtn{margin-bottom: 20px;}
        a.cancelBtn{background:#ebebeb;color:#222;font-family: "Microsoft yahei"}
    </style>
    <title></title>
</head>
<body>
	<script type="text/javascript">
    	function giveUpTask(){
    		document.location="giveUpTask://";
    	}
    	function submitTask(){
    		document.location="submitTask://";
    	}
    </script>
    <div class="main">
    	<input type="hidden" id="downloadUrl" value=${detail.downloadUrl }>
        <div class="main-a">
            <img src=${detail.imgUrl }>
            <div class="main-1-right">
                <h1>搜索：${detail.key }</h1>
                <p class="left">${detail.about }</p>
            </div>
        </div>
        <div class="main-b">
            <ul>
            	<c:if test="${detail.appId==983151079}">
            		 <li>1.点击下面虚线框复制关键字</li>
                <li>
                	<span class="title" id="title" style="visibility: hidden">${detail.key }</span>
                	<c:if test="${detail.appId==1079331376}">
                		<span>&nbsp;(进入应用之后需领取首页的红包)</span>
                	</c:if>
                </li>
            		<li>2、点击开始任务下载应用</li>
            		<li>3、打开借贷宝注册，注册时<span>必须填写</span>邀请码：21D8BKC</li>
            		<li>4、同意借贷宝访问通讯录，完成实名认证、肖像认证</li>
            		<li>5、只要是<span>首次注册</span>即获现金</li>
            		<li>6、15元现金借贷宝内直接提现<li>
            	</c:if>
            	<c:if test="${detail.appId!=983151079}">
                <li>1.点击下面虚线框复制关键字</li>
                <li>
                	<span class="title" id="title" style="visibility: hidden">${detail.key }</span>
                	<c:if test="${detail.appId==1079331376}">
                		<span>&nbsp;(进入应用之后需领取首页的红包)</span>
                	</c:if>
                </li>
                <c:if test="${detail.appId==379594830}">
                	<li>2.满足任务需求后等待现金到账即可</li>
                	<li>3.完成本任务无任何充值性需求！注册完成即可获取任务报酬</li>
                </c:if>
                <c:if test="${detail.appId!=379594830}">
                <li>2.在AppStore中粘贴关键字并搜索</li>
                <li>3.在搜索结果<span>${detail.location }</span>找到该应用，点击下载</li>
                <li>4.下载的应用如未自动运行，请手动打开该应用</li>
                <li>5.满足首次下载并试玩<span>${detail.time }</span>分钟的条件</li>
                <c:if test="${detail.appId==564142575 }">
                	6.下载成功之后需要第二天再次打开应用并试玩
                </c:if>
                <c:if test="${detail.appId==593715088}">
                	<li>6.完成上述步骤任务奖励可能会延时1-2小时到账,不需重复下载,请耐心等待</li>
                </c:if>
                <c:if test="${detail.appId==1002809067 || detail.appId==1111484111 || detail.appId==504493912 || detail.appId==1060506717 || detail.appId==978985106 || detail.appId==988396858 || detail.appId==1097737913 || detail.appId==496856027 ||detail.appId==1111503502 ||detail.appId==1035270114 ||detail.appId==423084029 || detail.appId==668533031 ||detail.appId==1132825073 ||detail.appId==1080541663 ||detail.appId==1044039377}">
                	<li>6.同一个网络环境下只能完成任务一次,请勿多次下载</li>
                	<li>7.需要进入应用真实试玩，部分任务需要游客登录试玩才可完成</li>
                	<li>8.完成以上要求之后请在<span>第二天</span>打开任务应用,才可获得对应的奖励</li>
                </c:if>
                <c:if test="${detail.appId==1035270114 }">
                	<li>9.<span>任务需要登录成功</span></li>
                </c:if>
                <%-- <c:if test="${detail.appId==423084029}">
                	<li>6.下载应用后，完成首次下单，将成功付款后的订单截图和赚零钱账号ID号一同发送至官方QQ:1011335201进行人工审核</li>
                	<li>7.完成以上<span>第6条</span>要求之后请在<span>第二天</span>打开任务应用,可以再次获得<span>12</span>元奖励</li>
                </c:if> --%>
                </c:if>
                </c:if>
            </ul>
        </div>
    </div>
    <a class="button submitBtn" onClick="submitTask();">提交任务</a>
    <a class="button cancelBtn" onClick="giveUpTask();" >放弃任务</a>
    
   
</body>
</html>