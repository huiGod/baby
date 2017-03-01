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
	font-family: "Microsoft yahei"
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

.main .main-1-left img {
	width: 70px;
	height: 70px;
	margin: 15px 10px 15px 15px;
	border-radius: 9px;
	-webkit-border-radius: 9px;
	-moz-border-radius: 9px;
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

.main-1-right{
	padding-left: 10px;
	color: #3497e4;
}

.main-1-right .rightinfo{
	padding-right: 20px;
	float: right;
}

.main-1-right .left{
	color: #3497e4;
}

.main-b {
	clear: both;
	text-align: left;
	padding: 15px;
	border-top: 1px solid #F7F9F8;
	padding-left: 30px;
}

.main-b ol li {
	margin-bottom: 7px;
	color: #000;
	font-size: 13px;
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

a.openBtn{
	background-color: #378CEF;
	margin-bottom: 15px;
}

.main-b img{
	width:100%;
}

</style>
<title>后续任务详情</title>
</head>
<body>
	<script type="text/javascript">
		function openTask(){
			document.location = "openTask://?url=${detail.downLoadUrl}";
		}
		function uploadTask() {
			document.location = "uploadTask://";
		}
	</script>
	<div class="main">
		<div class="main-a">
		<input type="hidden" id="overtime" value=${detail.overtime }>
			<img src=${detail.imgUrl }>
			<div class="main-1-right">
				<div class="top">
				<p class="font_bold">${detail.title }</p>
				<p class="rightinfo"><span class="currency">+</span><span class="money_font">${detail.openMoney }</span><span class="currency">元</span></p>
				</div>
				<div class="left">${detail.message }</div>
			</div>
		</div>
		<div class="main-b">
			<ol>
				<!-- <diy>倒计时:<span id="time" style="visibility: hidden;"></span></diy> -->
				<li>点击复制关键字按钮</li>
				<li>在AppStore中粘贴关键词并搜索</li>
				<li>找到应用图标<span>${detail.location}左右</span></li>
				<li><span>试玩3分钟</span>（不能切换其他应用或锁屏）,获得奖励</li>
				<li>只有<span>首次安装</span>此APP才能完成任务获得奖励</li>
				<c:forEach var="value" items="${detail.infolist }">
					<li>${value}</li>
				</c:forEach>
			</ol>
			<img alt="" src="${detail.previewUrl }">
		</div>
	</div>
		<a class="button openBtn" onClick="openTask();">打开${detail.title }</a>
	<c:if test="${detail.type==4 || detail.type==5 }">
		<a class="button openBtn" onClick="uploadTask();">上传截图</a>
	</c:if>
</body>
</html>