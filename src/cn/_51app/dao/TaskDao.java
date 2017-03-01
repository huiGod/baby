package cn._51app.dao;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface TaskDao {

	/**
	 * tengh 2016年8月17日 下午2:09:41
	 * @param appIds
	 * @param memberId
	 * @return
	 * TODO 可接任务
	 */
	List<Map<String, Object>> queryBetter(String appIds, Integer memberId);

	/**
	 * tengh 2016年8月29日 下午4:09:51
	 * @return
	 * TODO 查询任务数是0的任务
	 */
	List<Map<String, Object>> queryTankNumIs0();

	/**
	 * tengh 2016年8月29日 下午4:15:20
	 * @param memberId
	 * @return
	 * TODO 审核中已接未完成的
	 */
	List<Map<String, Object>> queryTasked(Integer memberId);

	/**
	 * tengh 2016年8月29日 下午4:28:05
	 * @param memberId
	 * @return
	 * TODO 加上已完成任务
	 */
	List<Map<String, Object>> queryfinishedTask(Integer memberId);

	/**
	 * tengh 2016年8月29日 下午5:28:15
	 * @return
	 * TODO 即将开始的应用
	 */
	List<Map<String, Object>> queryLatestTask();

	/**
	 * tengh 2016年8月31日 下午2:28:46
	 * @param memberId
	 * @return
	 * TODO 后续进行中的未完成的任务
	 */
	List<Map<String, Object>> queryTaskedF(Integer memberId);

	/**
	 * tengh 2016年8月31日 下午4:03:34
	 * @param memberId
	 * @return
	 * TODO 检查是否有前置任务正在进行中
	 */
	boolean checkHasTasking(Integer memberId);

	/**
	 * tengh 2016年8月31日 下午5:19:51
	 * @param memberId
	 * @param homeId
	 * TODO 放弃前置进行中的任务
	 */
	void giveUpAllTask(int memberId, int homeId);

	/**
	 * tengh 2016年9月1日 上午11:25:41
	 * @param appIds 
	 * @param memberId
	 * @return
	 * TODO 高额直接截图任务
	 */
	List<Map<String, Object>> queryDirePic(String appIds, Integer memberId);

	/**
	 * tengh 2016年9月1日 下午5:01:59
	 * @param memberId
	 * @param homeId
	 * @param flagStatus
	 * @param type
	 * @return
	 * TODO 接任务
	 */
	String getTask(int memberId, int homeId, int flagStatus, Integer type);

	/**
	 * tengh 2016年9月1日 下午5:55:41
	 * @param memberId
	 * @param flag
	 * @param ip
	 * TODO 任务下载完成
	 */
	void downTask(Integer memberId, int flag, String ip);

	/**
	 * tengh 2016年9月1日 下午6:18:55
	 * @param memberId
	 * @return
	 * TODO 检查appStore账号是否是第一次下载应用
	 */
	boolean checkOnlyAppleId(Integer memberId);

	/**
	 * tengh 2016年9月5日 下午4:43:59
	 * @param homeId
	 * @return
	 * TODO 查询任务详情页信息
	 */
	Map<String, Object> getByHomeById(int homeId);

	/**
	 * tengh 2016年9月6日 下午5:44:03
	 * @param homeId
	 * @param memberId
	 * @return
	 * TODO 后续任务详情
	 */
	Map<String, Object> getFollowInfo(int homeId, String memberId);

	/**
	 * tengh 2016年9月6日 下午6:44:42
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 后续任务次日成功打开
	 */
	int openFollowTask(Integer memberId, Integer homeId);

	/**
	 * tengh 2016年9月7日 下午2:16:25
	 * @param homeId
	 * @param memberId
	 * @param path
	 * @return
	 * TODO
	 */
	int uploadFollowTask(Integer homeId, Integer memberId, String path);

	/**
	 * tengh 2016年9月8日 下午6:26:59
	 * @return
	 * TODO 查询新手任务信息
	 */
	List<Map<String, Object>> queryFirstInfo();

	/**
	 * tengh 2016年9月9日 上午10:50:50
	 * @param appIds
	 * @param memberId
	 * @return
	 * TODO 查询快速任务可接
	 */
	List<Map<String, Object>> queryFirstBetter(String appIds, Integer memberId);

	/**
	 * tengh 2016年9月9日 上午11:58:19
	 * @param memberId
	 * @return
	 * TODO 查询个人邀请信息
	 */
	Map<String, Object> queryInviteScale(Integer memberId);

	/**
	 * tengh 2016年9月12日 下午1:47:41
	 * @return
	 * TODO 查询新版的图标
	 */
	List<Map<String, Object>> queryNewIcons();

	/**
	 * tengh 2016年9月12日 下午2:57:52
	 * @param memberId
	 * @return
	 * TODO 新人任务是否完成
	 */
	Long checkFirstTasked(Integer memberId);

	/**
	 * tengh 2016年9月12日 下午3:49:47
	 * @param memberId
	 * @return
	 * TODO 新人任务数量
	 */
	Long getFirstTaskCount(Integer memberId);

	/**
	 * tengh 2016年9月13日 下午5:04:28
	 * @param memberId
	 * @return
	 * TODO 领取安慰奖优惠券
	 */
	boolean getCoupon(Integer memberId);

	/**
	 * tengh 2016年9月14日 下午6:40:06
	 * @param memberId
	 * @return
	 * TODO 新手进行中
	 */
	List<Map<String, Object>> queryFirstTasked(Integer memberId);

	/**
	 * tengh 2016年9月14日 下午6:41:09
	 * @param memberId
	 * @return
	 * TODO 新人已完成
	 */
	List<Map<String, Object>> queryFirstfinishedTask(Integer memberId);

	/**
	 * tengh 2016年9月21日 下午5:52:41
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 领取优惠券
	 */
	boolean getCouponN(Integer memberId, String homeId);

	/**
	 * tengh 2016年9月22日 上午10:10:42
	 * @param memberId
	 * @return
	 * TODO 新人任务状态
	 */
	Map<String, Object> getFirstStatus(Integer memberId);

	/**
	 * tengh 2016年9月22日 上午10:20:05
	 * @param memberId
	 * TODO 新人任务做完
	 */
	void updateFirstStatus(Integer memberId);

	int finishTask(Integer homeId, Integer memberId, Integer flag, Integer firstTask);

	/**
	 * tengh 2016年9月23日 下午4:44:52
	 * TODO  前置审核中  和有后续
	 * @return 
	 */ 
	List<Map<String, Object>> fontFollowTasks();

	/**
	 * tengh 2016年9月23日 下午5:57:36
	 * @param id
	 * TODO 改变任务状态
	 */
	void updateStatus(Integer id, int status);

	/**
	 * tengh 2016年9月26日 下午8:53:24
	 * @param memberId
	 * @return
	 * TODO 完成新人步骤
	 */
	void submitFirst(String memberId) throws Exception;

	/**
	 * tengh 2016年9月26日 下午9:13:05
	 * @param memberId
	 * @return
	 * TODO 查询个人是否有新手任务
	 */
	Integer queryFirstTop(Integer memberId);

	/**
	 * tengh 2016年9月27日 上午11:16:57
	 * @param memberId
	 * @return
	 * TODO 获取账户余额四舍五入
	 */
	double getBalance(String memberId);

	/**
	 * tengh 2016年10月14日 下午7:04:34
	 * @param flag
	 * @return
	 * TODO 开关打开关闭
	 */
	String switchOpen(String flag);

	/**
	 * tengh 2016年11月8日 下午4:29:10
	 * @param idfa
	 * @param appid 
	 * @return
	 * TODO 排重
	 */
	int checkIdfa(String idfa, String appid);


}
