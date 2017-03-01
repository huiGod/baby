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
html {
	font-size: 10px;
}

@media screen and (min-width:321px) and (max-width:375px) {
	html {
		font-size: 11px
	}
}

@media screen and (min-width:376px) and (max-width:414px) {
	html {
		font-size: 12px
	}
}

@media screen and (min-width:415px) and (max-width:639px) {
	html {
		font-size: 13px
	}
}

@media screen and (min-width:640px) and (max-width:719px) {
	html {
		font-size: 14px
	}
}

* {
	margin: 0;
	padding: 0;
}

button {
	outline: none 0;
	border: none 0;
}

.downloadBody {
	background: #fff;
	position: absolute;
	left: 0;
	top: 0;
	height: 100%;
	width: 100%;
	text-align: center;
}

.downloadBanner {
	width: 90%;
}

.downloadSection {
	width: 80%;
	margin: 10px auto;
	font-family: "Microsoft yahei";
	font-weight: 700;
	color: #4f4f4f;
	font-size: 1.6rem;
	line-height: 2.1rem;
	text-align: left;
	white-space: nowrap;
}

.downloadAppBtn, .beginTaskBtn {
	margin: 10px 0 20px 0;
	width: 100%;
	height: 75px;
	background: url(../images/Btn_1.png) no-repeat center top;
	background-size: 95%;
}

.beginTaskBtn {
	background-image: url(../images/Btn_2.png);
}
.useBrowserOpen{position:fixed;width:100%;height:100%;top:0;left:0;}
.useBrowserOpen img{width:100%;}
.hide{display: none;}
@media ( max-width :350px) {
	.downloadAppBtn, .beginTaskBtn {
		margin: 10px 0 0 0;
	}
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
    var handle = function() {
        var temp = navigator.userAgent;
        var rel = temp.indexOf("Safari") > 0;
        return rel;
    }
    function GetQueryString(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null)
            return unescape(r[2]);
        return null;
    }
    $(document).ready(function() {
    	var schemeUrl='${result.schemeUrl}';
    	var downloadUrl='${result.downloadUrl}';
        $(".downloadAppBtn").click(function() {
            if (handle()) {
            	setCookie("inviteEx",GetQueryString("id"));
                document.location=downloadUrl;
            } else {
                $(".useBrowserOpen").show();
            }
        });
        $(".beginTaskBtn").click(function() {
            if (handle()) {
                var id = GetQueryString("id");
                var url = schemeUrl;
                if (id != null && id != '') {
                    url = url + "?id=" + id;
                }
                window.location = url;
            } else {
                $(".useBrowserOpen").show();
            }
        });
    });
</script>
</head>
<body class="downloadBody">
	<header>
		<nav>
			<img src="../images/layoutBg.png" class="downloadBanner">
		</nav>
	</header>
	<section class="downloadSection">
		<p>最轻松的赚钱APP,日赚百元</p>
		<p>无需任何本金!只需两步马上赚钱</p>
		<button class="downloadAppBtn"></button>
		<p>下载完成后：</p>
		<button class="beginTaskBtn"></button>
	</section>
	<nav class="useBrowserOpen hide">
        <img src="../images/no_safari.png">
    </nav>
</body>
</html>