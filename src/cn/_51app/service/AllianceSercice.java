package cn._51app.service;

public interface AllianceSercice {

	/**
	 * tengh 2016年7月18日 下午4:58:22
	 * @param order_id
	 * @return
	 * TODO 验证订单是否已经处理过
	 */
	boolean checkOrder(String order_id);

	/**
	 * tengh 2016年7月18日 下午5:09:31
	 * @param app_id
	 * @param decode
	 * @param bill
	 * @param order_id
	 * @param flag
	 * TODO 记录回调记录
	 * @param memberId 
	 * @param adname 
	 */
	void insertRecord(String app_id, String activate_time, String bill, String order_id, String flag, String memberId, String adname);

	/**
	 * tengh 2016年7月18日 下午5:45:12
	 * @param memberId
	 * @param bill
	 * @param string
	 * TODO
	 */
	void updateUser(String memberId, String bill);

	/**
	 * tengh 2016年7月23日 下午4:25:07
	 * @return
	 * TODO 获取联盟列表
	 */
	String allianceList() throws Exception;

}
