<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <link type="text/css" rel="stylesheet" href="<%=path %>/css/resetcss.css">
    <link type="text/css" rel="stylesheet" href="<%=path %>/css/index.css">
    <title>新手任务</title>
</head>
<body>
    <!--头部开始-->
    <header class="header"><img src="<%=path %>/img/banner.png" alt="新手答题" /></header>
    <!--头部结束-->

    <!--题目开始-->
    <div class="question">
            <div class="q-title">
                <span class="q-num">1</span><span>用91赚零钱怎么赚钱呢？</span>
            </div>
            <div class="q-option" if="true">
                <i class="btn-left"></i><span>完成任务就能赚到钱，每天都有新任务</span>
            </div>
            <div class="q-option">
                <i class="btn-left"></i><span>双手合十虔诚跪拜老天掉馅饼</span>
            </div>
            <div class="btn-right"></div>
    </div>

    <div class="question">
        <div class="q-title">
            <span class="q-num">2</span><span>赚到的零钱该如何提现？</span>
        </div>
        <div class="q-option" if="true">
            <i class="btn-left"></i><span>首页点击“马上提现”，绑定手机和微信后即可提现</span>
        </div>
        <div class="q-option">
            <i class="btn-left"></i><span>摇晃手机大喊“急急如律令”</span>
        </div>
        <div class="btn-right"></div>
    </div>

    <div class="question">
        <div class="q-title">
            <span class="q-num">3</span><span>赚到的零钱该怎么花？</span>
        </div>
        <div class="q-option" if="true">
            <i class="btn-left"></i><span>可以提现，也可以在91特惠用余额买东西</span>
        </div>
        <div class="q-option">
            <i class="btn-left"></i><span>就着咸菜啃馒头我才不花呢</span>
        </div>
        <div class="btn-right"></div>
    </div>

    <div class="question">
        <div class="q-title">
            <span class="q-num">4</span><span>赚到的每一笔钱要怎么查看？</span>
        </div>
        <div class="q-option" if="true">
            <i class="btn-left"></i><span>点击收支记录所有明细一目了然</span>
        </div>
        <div class="q-option">
            <i class="btn-left"></i><span>歪脖摊手我也母鸡呀</span>
        </div>
        <div class="btn-right"></div>
    </div>


    <div class="question">
        <div class="q-title">
            <span class="q-num">5</span><span>这么好的事情怎能一人独享？</span>
        </div>
        <div class="q-option" if="true">
            <i class="btn-left"></i><span>收徒可以获取大量奖励，邀请朋友一起来赚钱</span>
        </div>
        <div class="q-option">
            <i class="btn-left"></i><span>默默蹲在角落里画圈圈</span>
        </div>
        <div class="btn-right"></div>
    </div>
    <!--题目结束-->

    <!--页脚开始-->
    <footer class="footer">

        <img src="<%=path %>/img/footer.png" alt="新手答题" />
        <div class="f-sub"></div>

    </footer>
    <!--页脚结束-->

    <!--弹窗开始-->
    <div class="popup">
        <div class="p-text">
            <p>题目没做完哟！</p>
            <div class="p-btn">继续做题</div>
        </div>
        <div class="p-text">
            <p>哎呀，答错了！！</p>
            <div class="p-btn">再看看</div>
        </div>
        <div class="p-text">
            <p>真棒！全对耶！</p>
            <div class="p-btn">去领奖</div>
        </div>
    </div>
    <!--弹窗结束-->


    <!--js部分-->
    <script type="text/javascript" src="<%=path %>/js/jquery.min.js"></script>
    <script type="text/javascript" src="<%=path %>/js/firstQA.js"></script>
    <script type="text/javascript">
    function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }
   	$(".popup").find(".p-btn").last().click(function(){
   		var flag=GetQueryString("flag");
   		$.ajax({
			url: "submitFirst.do",
			type: 'GET',
			data:{flag:flag},
			dataType: 'json',
		    cache:false,
		    timeout:"60000",
		    success:function(data){				    	    					    	    	
		    	if(data.code==200){
		    		window.location="successfirst://";
		    	}
		    }
		});
   	});
    </script>
</body>
</html>