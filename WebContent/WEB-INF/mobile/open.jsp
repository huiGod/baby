<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8">
<meta
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"
	name="viewport">
<title>下载应用</title>
<style>
  img{
  	width: 100%;
  }
</style>
<script src="../js/jquery.min.js"></script>
<script type="text/javascript">
	function setCookie(name,value)
	{
		var Days = 300;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days*24*60*60*1000);
		document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
	}
	function getCookie(name)
	{
		var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
		if(arr=document.cookie.match(reg))
		return unescape(arr[2]);
		else
		return null;
	}
	$(document).ready(function(){
		var schemeUrl='${schemeUrl}';
		$(".open").click(function(){
	    	var url = schemeUrl;
	    	var id=getCookie("inviteEx");
            if (id != null && id != '') {
                url = url + "?id=" + id;
            }
            window.location = url;
	    });		
	});
</script>
</head>
<body class="downloadBody">
	<img src="../images/open.jpg" class="open">
</body>
</html>