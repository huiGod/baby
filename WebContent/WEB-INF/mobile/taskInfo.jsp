<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1.0,user-scalable=no">
<style type="text/css">
* {
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
	html{font-size:10px}/*移动端用响应式单位：rem vw*/
	@media screen and (min-width:321px) and (max-width:375px){html{font-size:11px}}
	@media screen and (min-width:376px) and (max-width:414px){html{font-size:12px}}
	@media screen and (min-width:415px) and (max-width:639px){html{font-size:15px}}
	@media screen and (min-width:640px) and (max-width:719px){html{font-size:20px}}
}

body, div, p, ul,ol, li, h1, h2, h3, h4, h5, h6 {
	margin: 0;
	padding: 0;
}

html, body {
	text-align: center;
	-webkit-user-select: none;
	user-select: none;
	font: 12px/1.5 'Microsoft yahei', Helvetica, sans-serif;
}

html {
	background: #F7F9F9;
	font-family: "Microsoft yahei";
	font-size:10px;
}

.giveUpTask {
	position: absolute;
	height: 100%;
	top: 0;
	left: 0;
	background-color: #fff;
}

.main {
	width: 95%;
	background-color: #FFFFFF;
	margin: 10px auto 15px auto;
}

.left {
	text-align: left;
}

.main-a {
	overflow: hidden;
	position: relative;
	padding-left: 26vw;
	min-height: 22vw;
	padding-top: 10px;
}

.main-a img {
	position: absolute;
	left: 0;
	top: 12px;
	width: 19vw;
	height: 19vw;
	margin-left: 2vw;
	border-radius: 15px;
}

.main-1-right{
	padding-right: 30px;
}

.main-1-right .top {
	color: #000;
	font-size: 17px;
	text-align: left;
	padding-top: 16px;
}

.font_bold{
	font-weight: bold;	
}

.main-1-right div {
	color: #000;
	font-size: 12px;
}

.main-1-right div p{
	display: inline;
}

.main-1-right .centerinfo{
	padding-left: 10px;
	color: #3497e4;
}

.main-1-right .rightinfo{
	padding-right: 20px;
	float: right;
}

.main-b {
	clear: both;
	text-align: left;
	padding: 15px;
	border-top: 1px solid #F7F9F8;
	padding-left: 30px;
}

.main-c{
	clear: both;
	text-align: left;
	padding: 15px;
	border-top: 2px solid #F7F9F8;
	font-size: 13px;
	color: #323232;
}

.main-c p{
	display: inline;
}

.main-c-right{
	float: right;
}

.main-b ol li {
	margin-bottom: 7px;
	color: #000;
	font-size: 1.3rem;
}

.main-b ol li span.title {
	border: dashed 1px #FF0000;
	border-radius: 5px;
	width: 28px;
	padding: 7px 17px 7px 17px;
	margin-left: 17px;
	font-weight: bold;
}

a.button {
	box-sizing: border-box;
	display: block;
	width: 90%;
	margin: 0 auto;
	font-size: 14px;
	color: #FFF;
	padding: 10px;
	border-radius: 5px;
	font-family: "Microsoft yahei";
	margin-top: 15px;
}

span {
	color: red;
}

.currency{
	font-size: 11px;
	color: #9f9f9f;
}

.money_font{
	font-size: 16px;
}

a.copyBtn{
	background-color: #378CEF;
}

a.submitBtn{
	background-color: #e43445;
}

a.cancelBtn {
	color: #3497e4;
	font-family: "Microsoft yahei";
	border:1px;
	border-color: #3497e4;
	border-style: solid;
}
</style>
<title>任务详情</title>
</head>
<body>
	<script type="text/javascript">
		function copyTask(){
			document.location = "copyTask://";
		}
		function giveUpTask() {
			document.location = "giveUpTask://";
		}
		function submitTask() {
			document.location = "submitTask://";
		}
	</script>
	<div class="main">
		<input type="hidden" id="downloadUrl" value=${detail.downloadUrl }>
		<input type="hidden" id="key" value=${detail.key }>
		<div class="main-a">
			<img src=${detail.imgUrl }>
			<div class="main-1-right">
				<div class="top">
				<p class="font_bold">搜索：${detail.key }</p>
				<p class="rightinfo"><span class="currency">+</span><span class="money_font">${detail.money }</span><span class="currency">元</span></p>
				</div>
				<div class="left">
					<p>${detail.message }</p>
					<p class="centerinfo">
						<c:if test="${detail.type!=1 && detail.type!=2}">
							有后续
						</c:if>
					</p>
					<p class="rightinfo">后续<span>${detail.openMoney }</span><span class="currency">元</span></p>
				</div>
			</div>
		</div>
		<div class="main-b">
			<ol>
				<diy>倒计时:<span id="time" style="visibility: hidden;">13</span></diy>
				<li>点击复制关键字按钮</li>
				<li>在AppStore中粘贴关键词并搜索</li>
				<li>找到应用图标<span>${detail.location}左右</span></li>
				<li><span>试玩3分钟</span>（不能切换其他应用或锁屏）,获得奖励</li>
				<li>只有<span>首次安装</span>此APP才能完成任务获得奖励</li>
				<c:forEach var="value" items="${detail.infolist }">
					<li>${value}</li>
				</c:forEach>
			</ol>
		</div>
		<c:if test="${detail.type!=1 && detail.type!=2}">
			<div class="main-c">
				<p>后续任务:
					<c:if test="${detail.type==3 }">需次日打开应用</c:if>
					<c:if test="${detail.type==4 || detail.type==5}">需截图上传</c:if>
				</p>
				<p class="main-c-right">
					<span class="currency">+</span><span>${detail.openMoney }</span><span class="currency">元</span>
				</p>
			</div>
		</c:if>
	</div>
	<a class="button copyBtn" onClick="copyTask();">复制关键词:${detail.key }</a>
	<a class="button submitBtn" onClick="submitTask();">提交任务</a>
	<a class="button cancelBtn" onClick="giveUpTask();">放弃任务</a>
</body>
</html>