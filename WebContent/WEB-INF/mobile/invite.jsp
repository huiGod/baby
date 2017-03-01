<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<script src="../js/jquery.min.js"></script>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style type="text/css">
        *{
            -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
        }
        body,
        div,
        p,
        ul,
        li,
        h1,
        h2,
        h3,
        h4,
        h5,
        h6{
            margin: 0;
            padding: 0;
        }
        li {
            list-style: none;
        }
        html,
        body {
            text-align: center;
            -webkit-user-select: none;
            user-select: none;
            font:12px/1.5 'Ping Fang',Helvetica,sans-serif;
        }
        html{
            background:#F7F9F8;
        }
        body{
            padding: 10px 5px 10px 5px;
        }

        .main-a{
            padding: 15px;
            text-align: left;
            background-color: #FFF;
            margin-bottom: 15px;
        }
        .main-a-1 img{
            width: 16px;
            height: 16px;
            margin-bottom: -2px;

        }
        .main-a-1 h1{
            padding-left: 5px;
            color: #000;
            font-size: 15px;
            font-weight: bold;

        }

        .main-a-2{
            font-size: 13px;
            color: #000;
        }
        tr th{
            vertical-align: text-top;
        }
        td span{
            color: #FF0000;
        }
        .main-b{
            margin: auto;
            height: 168px;
            width: 220px;
            background-color: #FFF;

        }
        .main-b img{
            width: 123px;
            height: 123px;

        }
        .main-b h1{
            font-size: 11px;
            color: #747474;
            padding-bottom: 7px;
            padding-top: 8px;
        }
        a.button{
            font-size: 14px;
            color: #FFF;
            padding: 10px 27% 10px 27%;
            background-color: #378CEF;
            border-radius: 5px;
            margin-top: 15px;
        }
    </style>
    
    <title></title>
</head>
<body>
<script type="text/javascript">
    	function Share(){
    		document.location="Share://";
    	}
</script>
<div class="main-a">
    <div class="main-a-1">
    	<img src="/baby/images/3.png"><h1 style="display: inline">如何躺着赚钱</h1>
    </div>
    <input type="hidden" id="ShareMessage" value=${invite.ShareMessage }>
    <input type="hidden" id="ShareTitle" value=${invite.ShareTitle }>
    <input type="hidden" id="ShareUrl" value=${invite.ShareUrl }>
    <div class="main-a-2">
        <table>
            <tr>
                <th>1.</th><td>邀请好友您将获得好友试玩收入的<span>${invite.oneinvite }</span>现金奖励,每个好友都能为您提供<span>${invite.oneMoney }</span>元的收益。</td>
            </tr>
            <tr>
                <th>2.</th><td>您所邀请的好友,好友再邀请TA的好友您将还能获得该好友的试玩收入<span>${invite.twoinvite }</span>奖励。</td>
            </tr>
        </table>
    </div>

</div>

<div class="main-b">
    <h1>我的邀请二维码</h1>
    <img src="${invite.ticket }">
</div>
<div style="height: 20px"></div>
<a class="button" onClick="Share();">喊TA一起赚钱</a>
</body>
</html>