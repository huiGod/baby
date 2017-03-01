<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
	<meta charset="UTF-8">
	<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport">
	<meta content="yes" name="apple-mobile-web-app-capable"/>
	<meta name="format-detection" content="telephone=no" />
	<title>关注公众号</title>
	<link rel="stylesheet" href="../css/download.css">
	<script src="../js/jquery.min.js"></script>
</head>
<body class="bandWX-body">
	<!-- <header class="headerTop fff">
		<a href="javascript:void(0);" class="back">返回</a>
		<nav class="band-nav text-center">绑定微信</nav>
	</header> -->
	<section class="text-center fff section1">
		<div><span style="font-weight: bold;letter-spacing:1px;">温馨提示:</span>为了您的账号安全及提现便捷请绑定微信。</div>
		<div class="input-box"><input type="text" placeholder="请输入微信验证码" id="code"></div>
		<button class="bindBtn">提交</button>
	</section>
		
	
	<footer class="guidePic-box fff">
		<img src="../images/guide-steps.png">
	</footer>
	
	<script type="text/javascript">
	function trim(str){ //删除左右两端的空格
		return str.replace(/(^\s*)|(\s*$)/g, "");
	}
	function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }
	$(".bindBtn").click(function(){
		var token=GetQueryString("token");
    	var url="http://api.baby.51app.cn/baby/u/boundCode.do";
    	var code=$("#code").val();
    	if(token=='' || token==null || trim(token)==''){
    		alert("页面异常,请返回重新进入!");
    		return;
    	}
    	if(code==null || code=='' || trim(code)==''){
    		alert("请输入正确的验证码!"); 
    		return;
    	}
   	   	$.ajax({
   	     url:url,
   	     type:'post',
   	     data:{'param':token,'code':code},
   	     success:function(data){
   	    	 var result=data.message;
   	    	 if(result=="success"){
   	    		 document.location="attentionsuccess://";
   	    	 }else{
   	    		alert("请输入正确的验证码!");
   	    	 }
   	     }
   	   });
	});
	</script>
</body>
</html>