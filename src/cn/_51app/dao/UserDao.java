package cn._51app.dao;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface UserDao {

	/**
	 * tengh 2016年4月5日 上午11:39:33
	 * @param idfa
	 * @return
	 * TODO 默认生成账号
	 */
	Integer insertUserByIdfa(String idfa);
	
	/**
	 * tengh 2015年11月18日 下午5:10:38
	 * @param memberId 
	 * @return
	 * TODO 
	 */
	public List<Map<String, Object>> queryInviteInfo(int memberId) throws Exception;

	/**
	 * tengh 2015年11月19日 下午1:55:42
	 * @param dateTemp
	 * @param memberId
	 * @param type 
	 * @param type 
	 * @return
	 * TODO 查询某一天的记录
	 */
	public List<Map<String, Object>> queryInviteByDate(String dateTemp, int memberId, int type) throws Exception;

	/**
	 * tengh 2015年11月19日 下午3:20:46
	 * @param dateTemp
	 * @param memberId
	 * @param inviteId
	 * @param type
	 * @return
	 * TODO 查询二级
	 */
	public List<Map<String, Object>> queryInviteByPerson( int memberId, int inviteId, int type) throws Exception;

	/**
	 * tengh 2015年11月19日 下午4:46:28
	 * @param memberId
	 * @param page
	 * @return
	 * TODO 分页查询提现记录日期
	 */
	public List<Map<String, Object>> queryDeposit(int memberId, int page) throws Exception;

	/**
	 * tengh 2015年11月19日 下午5:02:13
	 * @param memberId
	 * @param dateTemp
	 * @return
	 * TODO 查询一天提现记录
	 */
	public List<Map<String, Object>> queryDeposiyByDate(int memberId, String dateTemp) throws Exception;

	/**
	 * tengh 2015年11月20日 上午11:02:42
	 * @param appIds 
	 * @param memberId 
	 * @return
	 * TODO 首页任务列表
	 */
	public List<Map<String, Object>> queryBetter(String appIds, Integer memberId);

	/**
	 * tengh 2015年11月20日 下午4:09:07
	 * @param uuid
	 * TODO 拉入黑名单
	 */
	public void insertBlackList(String uuid);

	/**
	 * tengh 2015年11月20日 下午4:51:27
	 * @param idfa
	 * @return
	 * TODO 设备号是否在黑名单
	 */
	public boolean checkIsBlacklist(String idfa);

	/**
	 * tengh 2015年11月20日 下午5:15:07
	 * @param memberId
	 * TODO 更新登录次数
	 */
	public void updateLoginNum(Integer memberId);

	/**
	 * tengh 2015年11月23日 上午9:53:53
	 * @param memberId
	 * @param page
	 * @return
	 * TODO 查询试玩日期
	 */
	public List<Map<String, Object>> queryTryDate(Integer memberId, int page);

	/**
	 * tengh 2015年11月23日 上午10:00:58
	 * @param memberId
	 * @param dateTemp
	 * @return
	 * TODO 通过日期查询试玩记录
	 */
	public List<Map<String, Object>> queryTryLogByDate(int memberId, String dateTemp);


	/**
	 * tengh 2015年11月24日 上午10:25:55
	 * @param memberId
	 * @param ticket
	 * TODO 保存二维码参数
	 */
	public void insertBarCode(int memberId, String ticket);

//	/**
//	 * tengh 2015年11月24日 下午7:33:00
//	 * @param opentime
//	 * @param unionid 
//	 * @return
//	 * TODO 微信受邀人id
//	 */
//	public Integer queryInviteId(String opentime, String unionid);

	/**
	 * tengh 2015年12月27日 上午10:16:07
	 * @return
	 * TODO 任务首页邀请信息
	 */
	public List<Map<String, Object>> queryInvite();


	/**
	 * tengh 2015年12月28日 下午8:23:47
	 * @param memberId
	 * @return
	 * TODO 查询接的任务
	 */
	public List<Map<String, Object>> queryTasked(Integer memberId);

	/**
	 * ysx 2015年12月31日
	 * @param homeIds
	 * TODO 应用详情
	 */
	List<Map<String, Object>> getByHomeById(int id);

	/**
	 * tengh 2016年1月5日 上午10:17:36
	 * @param memberId
	 * @param money
	 * @param type
	 * TODO 申请提现
	 * @param zfbNo 
	 * @param zfbName 
	 */
	public void getMoney(int memberId, String money, int type, String zfbNo, String zfbName);

	/**
	 * tengh 2016年1月5日 上午10:23:06
	 * @param memberId
	 * @param returnflag
	 * @param homeId
	 * TODO 领取任务
	 * @param flagStatus 
	 */
	public String getTask(int memberId, int homeId, int flagStatus);

	/**
	 * tengh 2016年1月5日 下午8:26:41
	 * @param inviteId
	 * @param memberId
	 * TODO 插入邀请人
	 */
	public void insertInvite(Integer inviteId, Integer memberId);

	/**
	 * tengh 2016年1月7日 下午3:02:10
	 * @param memberId
	 * @param type
	 * @param money
	 * TODO 
	 */
	public void updateUserDefault(Integer memberId, Integer type, String money);

	/**
	 * tengh 2016年1月11日 下午2:59:29
	 * @param memberId
	 * @param page
	 * @param string2Int
	 * @return
	 * TODO 试玩记录
	 */
	public List<Map<String, Object>> queryTryLogByPage(int memberId, int page, int string2Int);

	/**
	 * tengh 2016年1月12日 下午5:29:45
	 * @param memberId
	 * @param page
	 * @param number
	 * @return
	 * TODO 分页查询提现记录
	 */
	public List<Map<String, Object>> queryDeposiyByPage(int memberId, int page, int number);

	/**
	 * tengh 2016年1月13日 上午11:13:52
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 核查时候有未完成的任务
	 */
	public Map<String, Object> checkTask(Integer memberId);

	/**
	 * tengh 2016年1月16日 上午9:37:55
	 * @param uuid
	 * @param deviceToken
	 * TODO 
	 * @param appId 
	 * @param version 
	 */
	public void boundDeviceToken(String idfa, String deviceToken, String version, String appId);

	/**
	 * tengh 2016年1月26日 下午3:22:46
	 * @param memberId
	 * @param homeId
	 * TODO 放弃当前任务
	 * @return 
	 */
	public int giveUpTask(Integer memberId, Integer homeId);


	/**
	 * tengh 2016年2月16日 下午3:54:35
	 * @param memberId
	 * @return
	 * TODO 查询账户金额和时间
	 */
	public Map<String, Object> queryMoneyAndTime(int memberId);

	/**
	 * tengh 2016年2月22日 下午1:58:53
	 * @param memberId
	 * @return
	 * TODO 查询个人信息
	 */
	public Map<String, Object> querySimInfo(Integer memberId);

	/**
	 * tengh 2016年2月22日 下午2:44:29
	 * @return
	 * TODO 查询首页图标
	 */
	public List<Map<String, Object>> queryIcons();

	/**
	 * tengh 2016年2月23日 下午3:31:26
	 * @param memberId
	 * @return
	 * TODO 查询默认交易方式金额
	 */
	public Map<String, Object> queryDefault(int memberId);

	/**
	 * tengh 2016年2月23日 下午4:49:35
	 * @param memberId
	 * @param number 
	 * @param page 
	 * @return
	 * TODO 查询最近邀请人
	 */
	public List<Map<String, Object>> queryInviteRecord(int memberId, int page, int number);

	/**
	 * tengh 2016年2月23日 下午6:07:31
	 * @param memberId
	 * @param page
	 * @param number
	 * @param i 
	 * @return
	 * TODO
	 */
	public List<Map<String, Object>> queryTaskInfo(int memberId, int inviteId,int page, int number);

	/**
	 * tengh 2016年2月23日 下午8:07:28
	 * @param memberId
	 * @param homeId
	 * TODO 任务过期
	 */
	public void OverDueTask(Integer memberId, Integer homeId);

	/**
	 * tengh 2016年2月24日 上午11:05:02
	 * @param homeId
	 * @param memberId
	 * @param flag
	 * TODO 任务完成
	 * @param firstTask 
	 */
	public int finishTask(Integer homeId, Integer memberId, Integer flag);

	/**
	 * tengh 2016年2月24日 下午2:15:16
	 * @param homeId
	 * @param memberId
	 * @return
	 * TODO 查询消息推送需要的信息
	 */
	public Map<String, Object> queryMessageInfo(Integer homeId, Integer memberId);

	/**
	 * tengh 2016年2月24日 下午2:54:00
	 * @param homeId
	 * @param memberId
	 * @return
	 * TODO 检验任务是否达到打开时间可以完成
	 */
	public boolean isCanFinish(Integer homeId, Integer memberId);

	/**
	 * tengh 2016年2月25日 下午4:09:21
	 * @param memberId
	 * @return
	 * TODO 邀请成功直接获取的奖励
	 */
	public List<Map<String, Object>> queryForInviteMoney(int memberId);

	/**
	 * tengh 2016年2月29日 下午4:09:37
	 * @param memberId
	 * @return
	 * TODO 查询受邀请获得的比例
	 */
	public List<Map<String, Object>> queryInviteScale(Integer memberId);

	/**
	 * tengh 2016年2月29日 下午5:35:17
	 * @param memberId
	 * @return
	 * TODO 查询邀请二维码ticket
	 */
	public String queryTicket(Integer memberId);

//	/**
//	 * tengh 2016年3月4日 下午2:49:23
//	 * @param id
//	 * @param ip
//	 * TODO 记录邀请的ip
//	 */
//	public void insertInviteClick(String id, String ip);

//	/**
//	 * tengh 2016年3月4日 下午4:52:09
//	 * @param idfa
//	 * @return
//	 * TODO 查询激活的ip
//	 */
//	public Map<String, Object> queryForIp(String idfa);

//	/**
//	 * tengh 2016年3月4日 下午5:09:31
//	 * @param ip
//	 * @param creatime 
//	 * @return
//	 * TODO 通过ip查找邀请源
//	 */
//	public Integer queryInviteMemberId(String ip, String creatime);

	/**
	 * tengh 2016年3月5日 下午2:29:00
	 * @param time
	 * @return
	 * TODO 查询过期的任务
	 */
	public List<Map<String, Object>> queryOverDueTask(int time);

	/**
	 * tengh 2016年3月5日 下午3:26:00
	 * @param memberId
	 * TODO 检测到任务被下载
	 * @param flag 
	 * @param ip 
	 */
	public void downTask(Integer memberId, Integer flag, String ip) throws Exception;

//	/**
//	 * tengh 2016年3月9日 下午3:47:23
//	 * @param idfa
//	 * @return
//	 * TODO 查询设备打开应用的时间
//	 */
//	public String queryOpenTime(String idfa);

	/**
	 * tengh 2016年3月11日 上午11:17:52
	 * @return
	 * TODO 查询邀请好友直接获取的金额
	 */
	public Double queryForInviteDire();

//	/**
//	 * tengh 2016年3月23日 下午4:50:34
//	 * @param idfa
//	 * @param wxOpenid
//	 * @return
//	 * TODO 通过idfa和微信 确认账号存在
//	 */
//	public Map<String, Object> getUserInfoByIdfaAndWx(String idfa, String wxOpenid);

//	/**
//	 * tengh 2016年3月23日 下午5:08:11
//	 * @param wxOpenid
//	 * @return
//	 * TODO 微信是否存在
//	 */
//	public boolean checkisWxOpenid(String wxOpenid);

//	/**
//	 * tengh 2016年3月23日 下午5:13:04
//	 * @param idfa
//	 * @return
//	 * TODO idfa是否存在
//	 */
//	public boolean checkisIdfa(String idfa);

	/**
	 * tengh 2016年3月23日 下午5:18:15
	 * @param idfa
	 * TODO idfa拉入黑名单
	 */
	public void insertIdfaBlackList(String idfa);

	/**
	 * tengh 2016年3月24日 上午11:47:35
	 * @param memberId
	 * @param udid
	 * TODO
	 */
	public int boundUdid(String memberId, String udid);

	/**
	 * tengh 2016年3月24日 下午5:15:36
	 * @param mobile
	 * @return
	 * TODO 查询手机号是否存在
	 */
	boolean checkMobile(String mobile);

	/**
	 * tengh 2016年3月24日 下午5:56:29
	 * @param mobile
	 * @param memberId
	 * @return
	 * TODO 账号绑定手机号
	 */
	boolean boundMobile(String mobile, Integer memberId);

	/**
	 * tengh 2016年3月24日 下午8:43:03
	 * @param memberId
	 * @param mobile
	 * @return
	 * TODO 核查账号是否是原绑定的手机号
	 */
	String checkMobileAndMember(Integer memberId);

	/**
	 * tengh 2016年3月25日 下午1:46:25
	 * @param memberId
	 * @return
	 * TODO 查询udid
	 */
	String queryUdid(String memberId);

	/**
	 * tengh 2016年3月31日 下午6:28:42
	 * @param memberId
	 * @return
	 * TODO 查询推送标识
	 */
	String queryDeviceToken(Integer memberId);

	/**
	 * tengh 2016年4月5日 上午10:34:43
	 * @param idfa
	 * @return
	 * TODO 默认登录
	 */
	Map<String, Object> getUserInfoByIdfa(String idfa);

	/**
	 * tengh 2016年4月5日 下午4:18:35
	 * @param memberId
	 * @return
	 * TODO 查询个人资料
	 */
	Map<String, Object> information(Integer memberId);

	/**
	 * tengh 2016年4月5日 下午5:26:25
	 * @param memberId
	 * @param name
	 * @param sex
	 * @param birthday
	 * @param profession
	 * @param temPath 
	 * @return
	 * TODO 保存账号
	 */
	int editInfo(Integer memberId, String name, Integer sex, String birthday, String profession, String temPath);

	/**
	 * tengh 2016年4月6日 上午10:23:17
	 * @param memberId
	 * @return
	 * TODO 验证账号完整性
	 */
	boolean checkInfo(Integer memberId);

	/**
	 * tengh 2016年4月7日 上午10:29:28
	 * @param memberId
	 * @return
	 * TODO 账号是否绑定微信
	 */
	boolean checkIsBoundWx(Integer memberId);

	/**
	 * tengh 2016年4月7日 上午10:34:25
	 * @param memberId
	 * @param code
	 * @return
	 * TODO 验证码绑定
	 */
	Integer updateWxByCode(Integer memberId, String code);

	/**
	 * tengh 2016年4月8日 下午5:17:33
	 * @param memberId
	 * @return
	 * TODO 账号是否正常存在
	 */
	boolean checkMemberById(Integer memberId);

	/**
	 * tengh 2016年4月9日 上午11:36:55
	 * @param memberId
	 * @return
	 * TODO 验证账号是否需要对appleID账号做限定
	 */
	boolean checkOnlyAppleId(Integer memberId);

	/**
	 * tengh 2016年4月9日 上午11:55:00
	 * @param memberId
	 * @return
	 * TODO 查询任务之前的有效状态
	 */
	Map<String, Object> queryTaskFlagStatus(Integer memberId);

	/**
	 * tengh 2016年4月14日 上午10:59:36
	 * @param memberId
	 * TODO 如果之前有任务，放弃掉
	 * @param homeId 
	 */
	void giveUpAllTask(int memberId, int homeId);

	/**
	 * tengh 2016年4月14日 下午1:56:34
	 * @param memberId 
	 * @return
	 * TODO 查询即将开始的任务
	 */
	List<Map<String, Object>> queryLatestTask(Integer memberId);

	/**
	 * tengh 2016年4月15日 下午4:06:47
	 * @param memberId
	 * TODO 登录成功获取奖励
	 */
	void getMoneyByLogin(Integer memberId);

	/**
	 * tengh 2016年4月25日 下午1:56:51
	 * @return
	 * TODO 查询任务数为0的
	 */
	List<Map<String, Object>> queryTankNumIs0();

	/**
	 * tengh 2016年4月26日 上午11:45:34
	 * @param memberId
	 * @return
	 * TODO 查询idfa对接接口
	 */
	Map<String, Object> queryIdfaUrl(Integer homeId);

	/**
	 * tengh 2016年4月26日 下午5:18:55
	 * @param memberId
	 * @return
	 * TODO 已完成的任务
	 */
	List<Map<String, Object>> queryfinishedTask(Integer memberId);

	/**
	 * tengh 2016年5月6日 上午9:35:07
	 * TODO 初始化今日收入
	 */
	void clearTodayMoney();

	/**
	 * tengh 2016年5月6日 下午5:56:54
	 * @param idfa
	 * @return
	 * TODO 是否直接激活
	 */
	boolean isActive(String idfa);

	/**
	 * tengh 2016年5月9日 下午3:13:35
	 * @param appleid
	 * @return
	 * TODO 获取套壳信息
	 */
	Map<String, Object> getHub(String appleid);

	/**
	 * tengh 2016年5月9日 下午8:22:27
	 * @param appleid
	 * @return
	 * TODO 获取schemeUrl
	 */
	String getSchemeUrl(String appleid);

	/**
	 * tengh 2016年5月10日 下午7:29:58
	 * @return
	 * TODO 获取默认推的壳
	 */
	Map<String, Object> getHubBydefault();

	/**
	 * tengh 2016年5月21日 下午5:54:40
	 * @param memberId
	 * @param idfa
	 * @return
	 * TODO 验证idfa和memberId是否一致
	 */
	boolean checkMemberAndIdfa(int memberId, String idfa);

	/**
	 * tengh 2016年5月24日 下午6:35:03
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 查询回调
	 */
	boolean checkActive(Integer memberId, Integer homeId);

	/**
	 * tengh 2016年5月25日 下午1:57:19
	 * @param memberId
	 * @param zfbName
	 * @param zfbNo
	 * @return
	 * TODO 检查支付宝
	 */
	boolean checkZfb(Integer memberId, String zfbName, String zfbNo);

	/**
	 * tengh 2016年5月25日 下午2:59:00
	 * @param homeId
	 * @return
	 * TODO 查询对接情况
	 */
	int queryActiveStatus(Integer homeId);

	/**
	 * tengh 2016年5月26日 上午10:37:40
	 * @param memberId
	 * @return
	 * TODO 查询账号状态
	 */
	int queryStatus(Integer memberId);

	/**
	 * tengh 2016年6月1日 上午10:43:37
	 * @param memberId
	 * @return
	 * TODO 查询idfa
	 */
	String getIdfa(String memberId);

	String getDeviceToken(int i);

	/**
	 * tengh 2016年6月21日 上午10:03:21
	 * @param appleid
	 * @param version
	 * @return
	 * TODO 获取套壳信息
	 */
	Map<String, Object> getHub(String appleid, String version);

	/**
	 * tengh 2016年6月29日 下午4:13:07
	 * @param idfa
	 * @return
	 * TODO
	 */
	Integer queryMemberIdByidfa(String idfa);

	/**
	 * tengh 2016年6月29日 下午4:14:16
	 * @param appleid
	 * @return
	 * TODO
	 */
	Integer queryHomeIdByAppleid(String appleid);

	/**
	 * tengh 2016年6月29日 下午4:44:08
	 * @param homeId
	 * TODO 任务数量-1
	 */
	void desHomeNum(Integer homeId);

	/**
	 * tengh 2016年7月9日 上午10:45:52
	 * @param homeId
	 * @param memberId
	 * @param i
	 * @return
	 * TODO 回调之后完成任务
	 */
	int finishTask2(Integer homeId, Integer memberId, Integer i);

	/**
	 * tengh 2016年7月9日 上午10:45:51
	 * @param memberId
	 * @param homeId
	 * TODO 更新cpa状态
	 */
	void updateStatus(Integer memberId, Integer homeId);

	/**
	 * tengh 2016年7月11日 下午4:26:51
	 * @return
	 * TODO 查询需要处理的延迟奖励任务
	 */
	List<Map<String, Object>> queryDelayTask();

	/**
	 * tengh 2016年7月12日 上午9:40:16
	 * @param idfa
	 * @param appId
	 * @return
	 * TODO 查询是否激活
	 */
	Integer queryCpaStatus(String idfa, Integer appId);

	/**
	 * tengh 2016年7月13日 上午11:35:02
	 * @param appleid
	 * @return
	 * TODO 减少任务数量
	 */
	String desHomeNum(String appleid);

	/**
	 * tengh 2016年7月13日 下午6:09:03
	 * @param appleid
	 * @param idfa
	 * @return
	 * TODO 是否属于wall
	 */
	boolean isWall(String appleid, String idfa);

}
