package cn._51app;

public class BaByConstant {
	//成功
	public final static int MSG_SUCCESS     	=0;
	//参数异常
	public final static int MSG_PARAM      		=2;
	//后台参数接收处理失败
	public final static int MSG_KEYVALUE      	=3;
	//程序异常
	public final static int MSG_EXCEPTION      	=4;
	
	
	//操作成功
	public final static String MSG_TRUE      	="{\"msg\":"+new Boolean(true)+"}";
	//操作失败
	public final static String MSG_FALSE      	="{\"msg\":"+new Boolean(false)+"}";
	//手机号
	public final static String MSG_MOBILE      ="mobile";
	//微信唯一标示
	public final static String MSG_OPENID      ="openId";
	//最低级用户标识
	public final static String MSG_FATAL       ="fatal";
	//正常用户
	public final static String MSG_NORMAL      ="normal";
	
}
