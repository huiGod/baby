package cn._51app.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface TaskSercice {

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
	 * tengh 2016年8月31日 下午1:58:03
	 * @param memberId
	 * @param returnflag
	 * @param appIds
	 * @return
	 * TODO 后续模块
	 */
	String followHome(Integer memberId, String returnflag, String appIds) throws Exception;

	/**
	 * tengh 2016年8月31日 下午3:58:02
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 检查是否有前置任务在进行中
	 */
	String checkTask(Integer memberId) throws Exception;

	/**
	 * tengh 2016年8月31日 下午4:36:28
	 * @param memberId
	 * @param returnflag
	 * @param homeId
	 * @param idfa
	 * @param ip
	 * @return
	 * TODO 领取任务
	 */
	String getTask(int memberId, String returnflag, int homeId, String idfa, String ip) throws Exception;

	/**
	 * tengh 2016年9月1日 下午3:14:47
	 * @param returnflag
	 * @param memberId
	 * @param ip
	 * @param isAppstore
	 * @return
	 * TODO 是否是appStore下载
	 */
	String downTask(String returnflag, Integer memberId, String ip, String isAppstore) throws Exception;	

	/**
	 * tengh 2016年9月1日 下午4:55:27
	 * @param returnflag
	 * @param memberId
	 * @param jomeId
	 * @return
	 * TODO 放弃任务
	 */
	String giveupTask(String returnflag, Integer memberId, Integer jomeId);

	/**
	 * tengh 2016年9月1日 下午6:01:05
	 * @param returnflag
	 * @param memberId
	 * @param isPurchasedReDownload
	 * @param homeId
	 * @return
	 * TODO 检测是否是在appStore第一次下载
	 */
	String checkAppStore(String returnflag, Integer memberId, boolean isPurchasedReDownload, Integer homeId) throws Exception;

	/**
	 * tengh 2016年9月2日 上午10:13:47
	 * @param returnflag
	 * @param homeId
	 * @param memberId
	 * @param appflag
	 * @param ip
	 * @return
	 * TODO 主动提交审核按钮看任务是否完成  (只有试玩3分钟才有这个)
	 */
	String finishTask(String returnflag, Integer homeId, Integer memberId,  String ip) throws Exception;

	/**
	 * tengh 2016年9月5日 下午4:40:40
	 * @param homeId
	 * @return
	 * TODO 查询任务详情页信息
	 */
	Map<String, Object> getByHomeById(int homeId);

	/**
	 * tengh 2016年9月6日 下午5:43:13
	 * @param homeId
	 * @param memberId
	 * @return
	 * TODO 后续任务打开详情
	 */
	Map<String, Object> getFollowInfo(int homeId, String memberId);

	/**
	 * tengh 2016年9月6日 下午6:43:00
	 * @param returnflag
	 * @param homeId
	 * @param memberId
	 * @return
	 * TODO 后续任务成功次日打开
	 */
	String openFollowTask(String returnflag, Integer homeId, Integer memberId) throws Exception;

	/**
	 * tengh 2016年9月6日 下午8:42:24
	 * @param returnflag
	 * @param homeId
	 * @param memberId
	 * @param request
	 * @return
	 * TODO
	 */
	String uploadFollowTask(String returnflag, Integer homeId, Integer memberId, HttpServletRequest request) throws Exception;

	/**
	 * tengh 2016年9月8日 下午5:35:28
	 * @param memberId
	 * @param returnflag
	 * @param appIds
	 * @return
	 * TODO 新手任务
	 */
	String firstHome(Integer memberId, String returnflag, String appIds) throws Exception;

	/**
	 * tengh 2016年9月9日 上午11:47:59
	 * @param returnflag
	 * @param memberId
	 * @return
	 * TODO
	 */
	String inviteInfo(String returnflag, Integer memberId) throws Exception;

	/**
	 * tengh 2016年9月12日 上午11:56:10
	 * @param memberId
	 * @param appIds 
	 * @return
	 * TODO 获取首页图标
	 */
	String icons(Integer memberId, String appIds) throws Exception;

	/**
	 * tengh 2016年9月21日 下午5:51:05
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 领取优惠券
	 */
	String getCoupon(Integer memberId, String homeId) throws Exception;

	/**
	 * tengh 2016年9月26日 下午8:48:39
	 * @param flag
	 * @return
	 * TODO 完成 新人任务
	 */
	String submitFirst(String flag) throws Exception;

	/**
	 * tengh 2016年9月27日 上午11:16:15
	 * @param memberId
	 * @return
	 * TODO 账户余额四舍五入
	 */
	double getBalance(String memberId);

	/**
	 * tengh 2016年10月14日 下午7:04:01
	 * @param flag
	 * @return
	 * TODO 开关打开关闭
	 */
	String switchOpen(String flag);

}
