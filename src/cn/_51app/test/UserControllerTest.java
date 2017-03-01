package cn._51app.test;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import cn._51app.service.UserSercice;
import cn._51app.service.TaskSercice;

public class UserControllerTest extends JUnitActionBase{
	@Autowired  //自动注入,默认按名称
	private UserSercice userSercice;
	@Autowired  //自动注入,默认按名称
	private TaskSercice uuserSercice;
	@Test   //标明是测试方法
	@Transactional   //标明此方法需使用事务
	@Rollback(false)  //标明使用完此方法后事务不回滚,true时为回滚
	public void testInsertUserTest() {
		String result = null;
		try {
//			手机号注册
//			result = userSercice.register("33221111111", "", "dafsdfaa","dafsdfaa","dafsdfaa");
//			手机号登录
//			result = userSercice.logUser("15889644447", "asdsa", "9661a80c920e4961a6380761c546cc3c","9661a80c920e4961a6380761c546cc3c","sdfasdfasd");
			//caXTF7Zuxz9WHn7thHrLwAkG5nuRXb15   43
//			微信授权登录
//			result = userSercice.wxlogin( "4324", "zz2z23422", "234", 0,"1234232","oslxWcxxs2MBgkNwR6rgte4");
			//result  M3Slo0lDLHF+cBW3QkSqaQ==
//			result=userSercice.checkAccount("M3Slo0lDLHESbwE1em9NkQ==", 20);
//			result=userSercice.inviteRecord(44,0);
//			首页刷新余额和邀请获得的金额
//			result=userSercice.freshMoney(33, "asd");
//			result=userSercice.icons();
//			修改账号信息
//			result = userSercice.editUser(6, null, null, null, null, null,null,"去年","qq");
//			查询收入记录
//			result = userSercice.income(6, "H+p9Eg9GLpg9YRAe0hi/wADVKO8VQzIMFbQSalpo2KxbghA50YylfJ9ACpJlcvva");
//			查询邀请记录                          
//			result = userSercice.invite(1);
//			提现记录
//			result = userSercice.deposit(40, 0);
//			result=userSercice.taskInfo(1,4,0);
//			首页
//			LSApplicationProxy: com.apple.MobileSMSLSApplicationProxy: com.taobao.taobao4iphoneLSApplicationProxy: com.apple.iosdiagnosticsLSApplicationProxy: com.apple.weatherLSApplicationProxy: com.vipshop.iphoneLSApplicationProxy: com.yum.kfc.brandLSApplicationProxy: com.dianping.dpscopeLSApplicationProxy: com.51app.lifestyleLSApplicationProxy: com.iflytek.kuringeliteclientLSApplicationProxy: com.apple.stocksLSApplicationProxy: com.apple.mobilesms.composeLSApplicationProxy: UUSpeedTestLSApplicationProxy: com.apple.WebViewServiceLSApplicationProxy: com.wcl51app.rinLSApplicationProxy: com.apple.purplebuddyLSApplicationProxy: com.autohomeLSApplicationProxy: com.51app.HDwallpaperLSApplicationProxy: com.apple.nikeLSApplicationProxy: com.51app.ringtoneLSApplicationProxy: com.apple.cameraLSApplicationProxy: com.lantern.wifikey.mobileLSApplicationProxy: com.lovebizhiLSApplicationProxy: com.apple.WebContentFilter.remoteUI.WebContentAnalysisUILSApplicationProxy: com.freebox.YQDComicsLSApplicationProxy: com.apple.mobilecalLSApplicationProxy: us.orbe.ReKognition-DemoLSApplicationProxy: com.apple.WebSheetLSApplicationProxy: com.meitu.mtxxLSApplicationProxy: com.51app.AssistantLSApplicationProxy: com.apple.iad.iAdOptOutLSApplicationProxy: com.apple.mobilephoneLSApplicationProxy: com.tencent.xin.SDKSampleLSApplicationProxy: com.apple.MobileReplayerLSApplicationProxy: com.apple.facetimeLSApplicationProxy: com.51app.HelperLSApplicationProxy: com.meitu.bqgcLSApplicationProxy: com.apple.VoiceMemosLSApplicationProxy: com.meituan.imeituanLSApplicationProxy: com.360buy.jdmobileLSApplicationProxy: com.sypan.shoujilingshengLSApplicationProxy: com.netease.study901iphoneLSApplicationProxy: com.apple.gamecenter.GameCenterUIServiceLSApplicationProxy: com.51app.51babyLSApplicationProxy: com.apple.PreferencesLSApplicationProxy: com.cornapp.YMAssistantFor3LSApplicationProxy: com.apple.MobileAddressBookLSApplicationProxy: com.campmobile.linedecoLSApplicationProxy: com.apple.social.remoteui.SocialUIServiceLSApplicationProxy: -com.manboker.MoManXiangJiLSApplicationProxy: com.apple.fieldtestLSApplicationProxy: com.apple.MapsLSApplicationProxy: com.eo52sbk.ieso.b188LSApplicationProxy: com.apple.TencentWeiboAccountMigrationDialogLSApplicationProxy: com.apple.webappLSApplicationProxy: com.apple.mobiletimerLSApplicationProxy: com.51app.CLImageEditorDemoLSApplicationProxy: com.apple.ios.StoreKitUIServiceLSApplicationProxy: com.51app.cnLSApplicationProxy: com.apple.quicklook.quicklookdLSApplicationProxy: com.apple.TrustMeLSApplicationProxy: com.apple.SiriViewServiceLSApplicationProxy: com.apple.MailCompositionServiceLSApplicationProxy: com.tongbu.tui.G7KU46E4TTLSApplicationProxy: com.douban.DOUASDemoLSApplicationProxy: com.yang.chongLSApplicationProxy: com.apple.mobileslideshowLSApplicationProxy: X2JNK7LY8J.com.miantanLSApplicationProxy: com.apple.MusicLSApplicationProxy: SuningEMallLSApplicationProxy: com.netease.videoHDLSApplicationProxy: com.sina.weiboLSApplicationProxy: com.tencent.mqqsecureLSApplicationProxy: com.51app.babyLSApplicationProxy: com.51app.emotionsLSApplicationProxy: com.ywqc.emotionLSApplicationProxy: com.apple.videosLSApplicationProxy: com.apple.datadetectors.DDActionsServiceLSApplicationProxy: com.ucweb.iphone.lowversionLSApplicationProxy: com.apple.AdSheetPhoneLSApplicationProxy: com.apple.calculatorLSApplicationProxy: com.tencent.xinLSApplicationProxy: com.tencent.mqqLSApplicationProxy: com.moji.MojiWeatherLSApplicationProxy: com.baidu.netdiskLSApplicationProxy: com.apple.PassbookLSApplicationProxy: com.apple.MobileStoreLSApplicationProxy: com.letv.iphone.clientLSApplicationProxy: com.apple.AppStoreLSApplicationProxy: com.apple.CompassCalibrationViewServiceLSApplicationProxy: com.apple.appleaccount.AACredentialRecoveryDialogLSApplicationProxy: com.wangyin.walletLSApplicationProxy: com.apple.DemoAppLSApplicationProxy: com.tuikr.appredLSApplicationProxy: com.meitu.mtttLSApplicationProxy: com.netease.newsLSApplicationProxy: com.kugou.kugou1002LSApplicationProxy: com.apple.AccountAuthenticationDialogLSApplicationProxy: TomThorpe.UIScrollSlidingPagesLSApplicationProxy: com.apple.gamecenterLSApplicationProxy: com.apple.FacebookAccountMigrationDialogLSApplicationProxy: com.apple.PassbookUIServiceLSApplicationProxy: com.apple.MusicUIServiceLSApplicationProxy: com.51app.BouncerLSApplicationProxy: com.qiyi.iphoneLSApplicationProxy: com.51app.IMEILSApplicationProxy: com.apple.uikit.PrintStatusLSApplicationProxy: com.tencent.tiantianptuLSApplicationProxy: com.tencent.QQMusicLSApplicationProxy: com.apple.mobilenotesLSApplicationProxy: com.apple.mobilemailLSApplicationProxy: com.qunar.iphoneclient8LSApplicationProxy: com.ubercab.UberClientLSApplicationProxy: com.apple.compassLSApplicationProxy: com.apple.mobilesafariLSApplicationProxy: com.apple.reminders
//			result = userSercice.home(3, "M3Slo0lDLHESbwE1em9NkQ==","");
//			result=userSercice.finishTask("caXTF7Zuxz9WHn7thHrLwAkG5nuRXb15", 24, 35125, "M3Slo0lDLHFWMkE/nuvhxA==");
//			试玩记录
//			result = userSercice.tryLog(6, 1);
//			我的消息
//			result = userSercice.message(6, 0);
//			查询最新的余额和消息数
//			result = userSercice.latestNum(4, "nLAQTtFUHqZGWcjdpobSG4/3+Ff6iHX+zgYwpxlOQLo=");
//			生成二维码
//			result=userSercice.barCode(25);
//			最近邀请人
//			result=userSercice.latestInvite(6);
//			消息删除
//			result=userSercice.deleteMessage("4,5,6,7", 9);
//			申请退款
//			result=userSercice.getAccount("M3Slo0lDLHESbwE1em9NkQ==",20, "10", 1,"zfbno","zfbname");
//			申请退款的额度范围
//			result=userSercice.getMoneyRange("150");
//			领取任务
//			result=userSercice.getTask(43, "caXTF7Zuxz9WHn7thHrLwAkG5nuRXb15", 1);
//			检查任务
//			result=userSercice.checkTask(37, 1);
//			保存设备idfa
//			result=userSercice.saveDevice("78F654F3-8BC9-482E-A4F3-BB8DFFD4EDF8", "222222",null,null);
//			检测到应用被下载
//			result=userSercice.isDownload("caXTF7Zuxz9WHn7thHrLwAkG5nuRXb15",1, 43, "");
//			检测到应用被复制
//			result=userSercice.isPaste(1, 43);
//			刷新应用
//			result=userSercice.freshMoney(3, "fasdf");
//			result=userSercice.updateVersion("2.9.9","1109340002");
//			result=userSercice.checkUdid(7,"M3Slo0lDLHEWyB+HEL1M/g==");
//			result=userSercice.checkMobile("1234", 15, "M3Slo0lDLHF+cBW3QkSqaQ==");
//			result=userSercice.boundMobile("1234", 15, "M3Slo0lDLHF+cBW3QkSqaQ==");
//			result=userSercice.downTask("M3Slo0lDLHF+cBW3QkSqaQ==", 3388,false,"192.168.1.29");
//			result=userSercice.login("idfa100",null);
			/**
			 * M3Slo0lDLHFikeASHOR9SQ==  75012
			 */
//			result=userSercice.information(20, "M3Slo0lDLHESbwE1em9NkQ==");
//			result=userSercice.editInfo(20, "M3Slo0lDLHESbwE1em9NkQ==", "222", 1, "2016-03-24", "a");
//			result=userSercice.pushMessage();
//			result=uuserSercice.home(43, "caXTF7Zuxz9WHn7thHrLwAkG5nuRXb15", "");
//			result=uuserSercice.home(75012, "M3Slo0lDLHFikeASHOR9SQ==", "");
//			result=uuserSercice.firstHome(75012, "M3Slo0lDLHFikeASHOR9SQ==", "");
//			result=uuserSercice.checkTask(75012);
//			result=uuserSercice.getTask(75012, "M3Slo0lDLHFikeASHOR9SQ==", 21, "idfa100", "192.168.1.299");
//			result=uuserSercice.downTask("M3Slo0lDLHFikeASHOR9SQ==", 75012, "192.168.1.29", "1");
//			result=uuserSercice.finishTask("M3Slo0lDLHFikeASHOR9SQ==", 25, 75012, "192.168.1.29");
//			result=uuserSercice.openFollowTask("M3Slo0lDLHFikeASHOR9SQ==", 24, 75012);
//			result=uuserSercice.inviteInfo("M3Slo0lDLHFikeASHOR9SQ==", 75012);
			result=uuserSercice.icons(75012,"");
//			result=uuserSercice.couponList("M3Slo0lDLHFikeASHOR9SQ==", 75012);
//			result=uuserSercice.getCoupon("M3Slo0lDLHFikeASHOR9SQ==", 75012);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println(result);
	}

}
