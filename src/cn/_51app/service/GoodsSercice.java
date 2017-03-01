package cn._51app.service;

import java.util.List;
import java.util.Map;

public interface GoodsSercice {

	String getBanner() throws Exception;

	String nice(Integer page) throws Exception;

	String youGoods(int parseInt) throws Exception;

	String getGoodsDetails(String id) throws Exception;

	String getGoodsBuyParam(String id) throws Exception;

	List<Map<String, Object>> getOrderComm(String orderNo);

	boolean saveComment(String orderNo, String info_id, String commentArea, String starNum, String deviceNo);

	String getIdfa(String memberId);

	boolean deleteShop(String deviceNo, String string, String shopNo);

	boolean updateShop(String deviceNo, String string, String shopNo, String total_number);

	List<Map<String, Object>> shopBuy(String shopNo, String deviceNo, String app);

	List<Map<String, Object>> getAdress(String deviceNo, String app);

	String getOrderList(String deviceNo, String app,String page,String state) throws Exception;

	public boolean updateOrder(String orderNo, String deviceNo, String app, String flag) ;

	String confirmOrder(String orderNo, String deviceNo, String app) throws Exception;

	String getIdfaByOrderNo(String orderNo);

	String getMemberId(String deviceNo);

	String formOrderOne(String memberId,String deviceNo, String app, String couponId, String orderNo, String addressId,
			String num, String payId,String remark,  double balance) throws Exception ;

	int getShopNum(String deviceNo, String app);

	String createOrder(String infoId, String textureIds, String deviceNo, String app, String num);

	String getEvalTypeNum(Integer goodsId) throws Exception;

	String findAllEval(Integer page, Integer goodsId, Integer evalType) throws Exception;

	String findEvalPic(Integer page, Integer goodsId) throws Exception;

	boolean addShop(String infoId, String textureIds, String deviceNo, String num, String app);

	String updateAdress(String deviceNo, String app, String addressId, String name, String mobile, String province,
			String area, String isDefault);

	String getDeviceNoByShop(String shopNo);

	String getOrderInfo(String orderNo, String deviceNo, String app) throws Exception;

	String getOrderInfoByOrderNo(String orderNo) throws Exception;

	int checkOrderIsPayed(String trade_no);

	boolean insertPayRecord(Map<String, Object> paramMap);

	boolean changeOrderStatus(Map<String, Object> paramMap);

	void separateOrder(String out_trade_no);

	boolean deleteAdress(String deviceNo, String app, String addressId) throws Exception;

	String shopList(String deviceNo, String app, String page) throws Exception;

	void insertDiyUser(String deviceNo, String string);

	String couponCenter(String deviceNo) throws Exception;

	double getCouponMoney(String homeId);

	/**
	 * tengh 2016年10月12日 下午12:03:50
	 * @return
	 * TODO 代金券中心
	 */
	String couponList() throws Exception;

	/**
	 * tengh 2016年10月12日 下午2:08:22
	 * @param memberIdFromFlag
	 * @param acticeCode
	 * @return 激活码兑换代金券
	 * TODO
	 */
	String activeCode(String memberIdFromFlag, String acticeCode) throws Exception;

	/**
	 * tengh 2016年10月12日 下午2:46:33
	 * @param memberIdFromFlag
	 * @param couponId
	 * @return
	 * TODO 兑换代金券
	 */
	String exchangeCoupon(String memberIdFromFlag, String couponId) throws Exception;

	/**
	 * tengh 2016年10月12日 下午6:03:37
	 * @param memberId
	 * @return
	 * TODO 查询可用优惠券
	 */
	List<Map<String, Object>> getAvailCoupon(String memberId);

	/**
	 * tengh 2016年10月12日 下午8:23:50
	 * @param memberId
	 * @param deviceNo
	 * @param string
	 * @param shopNos
	 * @param payId
	 * @param addressId
	 * @param couponId
	 * @param remark
	 * @param balance
	 * @return
	 * @throws Exception
	 * TODO 购物车生成订单
	 */
	String createOrderByShops(String memberId, String deviceNo, String string, String shopNos, String payId,
			String addressId, String couponId, String remark, double balance) throws Exception;

}
