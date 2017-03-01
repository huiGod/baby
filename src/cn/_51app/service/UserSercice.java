package cn._51app.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

public interface UserSercice {



//	/**
//	 * tengh 2015年11月5日 下午2:02:20
//	 * @param uuid
//	 * @param imgUrl
//	 * @param name
//	 * @param wxOpenid
//	 * @param sex
//	 * @param idfa 
//	 * @param unionid 
//	 * @return
//	 * TODO
//	 */
//	String wxlogin(String imgUrl, String name, String wxOpenid, int sex, String idfa, String unionid) throws Exception;


	/**
	 * tengh 2015年11月18日 下午4:08:14
	 * @param memberId
	 * @return
	 * TODO
	 * @throws Exception 
	 */
	String invite(int memberId) throws Exception;

	/**
	 * tengh 2015年11月19日 下午4:35:22
	 * @param memberId
	 * @return 
	 * TODO 提现记录
	 */
	String deposit(int memberId,int page) throws Exception;

	/**
	 * tengh 2015年11月19日 下午8:35:10
	 * @param memberId 
	 * @param returnflag
	 * @param ids
	 * @return
	 * TODO 首页数据
	 */
	String home(Integer memberId, String returnflag, String ids) throws Exception;

	/**
	 * tengh 2015年11月20日 下午5:10:59
	 * @param memberId
	 * TODO 记录登录次数
	 */
	void loginLog(Integer memberId) throws Exception;

	/**
	 * tengh 2015年11月23日 上午9:29:02
	 * @param integer
	 * @param page 
	 * @return
	 * TODO 试玩记录
	 */
	String tryLog(int integer, int page) throws Exception;

	/**
	 * tengh 2015年12月5日 下午3:03:19
	 * @param returnflag 
	 * @param memberId
	 * @param money
	 * @param type  1支付宝2微信
	 * @param zfbNo 
	 * @param zfbName 
	 * @return
	 * TODO 申请金额
	 */
	String getAccount(String returnflag, Integer memberId, String money, int type, String zfbName, String zfbNo) throws Exception;

	/**
	 * tengh 2015年12月27日 下午3:49:38
	 * @param memberId
	 * @param returnflag 
	 * @param appId
	 * @param idfa 
	 * @param ip 
	 * @param ip 
	 * @return 领取任务
	 * TODO
	 */
	String getTask(int memberId, String returnflag, int appId, String idfa, String ip) throws Exception;

	/**
	 * tengh 2015年12月27日 下午6:00:04
	 * @param string
	 * @return
	 * TODO 得到取款范围
	 */
	String getMoneyRange(String money);
	
	
	List<Map<String,Object>> getByHomeById(int id);

	/**
	 * tengh 2016年1月7日 下午2:59:58
	 * @param memberId
	 * @param type
	 * @param money
	 * @return
	 * TODO 
	 */
	String setDefault(Integer memberId, Integer type, String money);

	/**
	 * tengh 2016年1月13日 上午11:07:26
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO
	 */
	String checkTask(Integer memberId, Integer homeId) throws Exception;

	/**
	 * tengh 2016年1月15日 下午6:21:37
	 * @param uuid 
	 * @param deviceToken 
	 * @param version
	 * @param appId
	 * @return 保存设备信息
	 * TODO
	 */
	String saveDevice(String uuid, String deviceToken, String version, String appId);

	/**
	 * tengh 2016年1月26日 下午2:59:52
	 * @param returnflag 
	 * @param memberId
	 * @param jomeId
	 * @return
	 * TODO 放弃任务
	 */
	String giveupTask(String returnflag, Integer memberId, Integer jomeId);


	/**
	 * tengh 2016年2月22日 下午1:49:33
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO
	 */
	String freshMoney(Integer memberId ,String returnflag) throws Exception;

	/**
	 * tengh 2016年2月22日 下午2:34:01
	 * @return
	 * TODO 首页图标
	 * @throws Exception 
	 */
	String icons() throws Exception;

	/**
	 * tengh 2016年2月23日 下午2:48:12
	 * @param returnflag
	 * @param memberId
	 * @param appleid 
	 * @return
	 * TODO
	 */
	String checkAccount(String returnflag, Integer memberId, String appleid) throws Exception;

	/**
	 * tengh 2016年2月23日 下午4:47:08
	 * @param memberId
	 * @param page 
	 * @return
	 * TODO
	 */
	String inviteRecord(int memberId, int page) throws Exception;

	/**
	 * tengh 2016年2月23日 下午6:02:07
	 * @param memberId
	 * @param page
	 * @param i 
	 * @return
	 * TODO
	 */
	String taskInfo(int memberId, int inviteId,int page) throws Exception;

	/**
	 * tengh 2016年2月23日 下午7:58:13
	 * @param returnflag
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO
	 */
	String taskOverDue(String returnflag, Integer memberId, Integer homeId);

	/**
	 * tengh 2016年2月24日 上午11:00:16
	 * @param returnflag
	 * @param homeId
	 * @param memberId
	 * @param appflag
	 * @param ip 
	 * @return
	 * TODO
	 */
	String finishTask(String returnflag, Integer homeId, Integer memberId, String appflag, String ip) throws Exception;


	/**
	 * tengh 2016年2月29日 下午4:06:00
	 * @param returnflag
	 * @return
	 * TODO 查询邀请获取的比例
	 */
	Map<String, String> queryInviteScale(Integer memberId);


	/**
	 * tengh 2016年3月4日 上午10:01:55
	 * @param version
	 * @param appleid 
	 * @return
	 * TODO 检测更新
	 */
	String updateVersion(String version, String appleid) throws Exception;


//	/**
//	 * tengh 2016年3月4日 下午2:45:26
//	 * @param id
//	 * @param ip
//	 * TODO 记录点击的ip
//	 */ 
//	void insertInviteClick(String id, String ip);


	/**
	 * tengh 2016年3月5日 下午3:19:19
	 * @param returnflag
	 * @param memberId
	 * @param ip 
	 * @param string 
	 * @param is 
	 * @return
	 * TODO
	 */
	String downTask(String returnflag, Integer memberId, String ip, String isAppstore) throws Exception;


	/**
	 * tengh 2016年3月24日 上午11:28:20
	 * @param memberId
	 * TODO 给账号绑定udid
	 * @param returnflag 
	 * @param udid 
	 */
	Map<String, Object> boundUdid(String memberId, String returnflag, String udid);


	/**
	 * tengh 2016年3月24日 下午2:00:30
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO
	 */
//	String checkUdid(Integer memberId, String returnflag) throws Exception;


	/**
	 * tengh 2016年3月24日 下午5:07:57
	 * @param mobile
	 * @param returnflag 
	 * @param memberId 
	 * @return
	 * TODO
	 */
	String checkMobile(String mobile, Integer memberId, String returnflag) throws Exception;


	/**
	 * tengh 2016年3月24日 下午5:54:03
	 * @param mobile 
	 * @param memberId 
	 * @param returnflag
	 * @return
	 * TODO 绑定账号 手机号
	 */
	String boundMobile(String mobile, Integer memberId, String returnflag) throws Exception;


	/**
	 * tengh 2016年4月1日 下午2:28:03
	 * @param idfa 
	 * @param cellNo
	 * @return
	 * TODO 默认登录做任务
	 */
	String login(String idfa, String inviteid) throws Exception;


	/**
	 * tengh 2016年4月5日 下午4:10:05
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 查询我的资料
	 */
	String information(Integer memberId, String returnflag) throws Exception;


	/**
	 * tengh 2016年4月5日 下午5:14:56
	 * @param memberId
	 * @param returnflag
	 * @param name
	 * @param sex
	 * @param birthday
	 * @param profession
	 * @param file 
	 * @return
	 * TODO
	 */
	String editInfo(Integer memberId, String returnflag, String name, Integer sex, String birthday, String profession, HttpServletRequest request) throws Exception;


	/**
	 * tengh 2016年4月7日 上午10:00:30
	 * @param memberId
	 * @param returnflag
	 * @param code 
	 * @return
	 * TODO
	 */
	String boundCode(Integer memberId, String returnflag, String code);

	/**
	 * tengh 2016年5月4日 下午8:13:16
	 * @param string
	 * @return
	 * TODO 是否直接打开应用
	 */
	String isActive(String idfa);

	/**
	 * tengh 2016年5月9日 下午3:10:28
	 * @param version
	 * @param appleid
	 * @return
	 * TODO 获取套壳信息
	 */
	Map<String, Object> getHub(String appleid);

	/**
	 * tengh 2016年5月9日 下午8:20:37
	 * @param appleid
	 * @return
	 * TODO 获取schemeurl
	 */
	String getSchemeUrl(String appleid);

	/**
	 * tengh 2016年5月10日 下午7:28:54
	 * @return
	 * TODO 获取默认推的应用
	 */
	Map<String, Object> getHubBydefault();

	/**
	 * tengh 2016年5月25日 下午1:52:52
	 * @param zfbName
	 * @param zfbNo
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO
	 */
	String checkZfb(String zfbName, String zfbNo, Integer memberId, String returnflag) throws Exception;

	/**
	 * tengh 2016年6月1日 上午10:42:46
	 * @param memberId
	 * @return
	 * TODO 查询账号idfa
	 */
	String getIdfa(String memberId);

	/**
	 * tengh 2016年6月2日 下午5:45:48
	 * @return
	 * TODO 
	 */
	String pushMessage();

	/**
	 * tengh 2016年6月2日 下午5:45:43
	 * @param string
	 * @param integer
	 * @param b
	 * @param homeId 
	 * @return
	 * TODO 检测appStore账号是否唯一
	 */
	String checkAppStore(String returnflag, Integer memberId, boolean b, Integer homeId) throws Exception;

	/**
	 * tengh 2016年6月13日 下午4:31:04
	 * @param returnflag
	 * @param memberId
	 * @param homeId 
	 * @param appflag 
	 * @return
	 * TODO 提交任务审核
	 * @throws Exception 
	 */
	String submitTask(String returnflag, Integer memberId, Integer homeId, String appflag) throws Exception;

	/**
	 * tengh 2016年6月21日 上午9:59:59
	 * @param appleid
	 * @param version
	 * @return
	 * TODO 获取套壳信息
	 */
	Map<String, Object> getHub(String appleid, String version);

	/**
	 * tengh 2016年6月29日 下午4:06:58
	 * @param appleid
	 * @param idfa
	 * @return
	 * TODO 直接完成任务
	 */
	String directFinish(String appleid, String idfa);

	/**
	 * tengh 2016年7月13日 上午11:33:51
	 * @param appleid
	 * @param idfa 
	 * @return
	 * TODO 减少任务数量
	 */
	String desHomeNum(String appleid, String idfa);

}
