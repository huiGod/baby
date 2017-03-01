package cn._51app.dao;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 */
public interface GoodsDao {

	/**
	 * tengh 2016年9月14日 下午3:15:59
	 * @return
	 * TODO 
	 */
	List<Map<String, Object>> getBanner();

	/**
	 * tengh 2016年9月14日 下午5:45:56
	 * @param paramMap
	 * @return
	 * TODO 精品会
	 */
	String nice(Map<String, Object> paramMap) throws Exception;

	String youGoods(Map<String, Object> paramMap) throws Exception;
	
	String getSelectTexture(Map<String,Object>paramMap)throws Exception;

	String getGoodsDetails(Map<String, Object> paramMap) throws Exception;

	String getGoodsShow(Map<String, Object> paramMap) throws Exception;

	Map<String, Object> getOrderComm(String orderNo);

	Map<String, Object> getMobile4Order(String orderNo);

    Integer saveComment(String goodsId,Integer evalType,String content,String deviceNo,String mobile,String texture) throws Exception;

	void updateEvaStatus(String orderNo);

	String getIdfa(String memberId);

	List<Map<String, Object>> queryShopList(String idfa, String app);

    int deleteShop(String deviceNo, String app, String shopNo);

	int updateShopOrderNum(String deviceNo, String app, String string, int i);

	int updateShop(String deviceNo, String app, String shopNos, String nums);

	List<Map<String, Object>> queryAddress(String deviceNo, String app);

	List<Map<String, Object>> shopBuy(String shopNos, String deviceNo, String app);

	List<Map<String, Object>> getOrderList(String deviceNo, String app,int page,int number,Integer state) ;

	boolean updateOrder(String orderNo, String deviceNo, String app, String flag);

	Map<String, Object> queryOrderInfo(String orderNo, String deviceNo, String app);

	int countActiveShop(String deviceNo, String app, String shopNo);

	int addShopByOrder(String prices, String imgUrls, String fileTypes, String nums, String textureNamess,
			String infoIdss, String userIdss ,String shopNo,String deviceNo,String app,String textureId) ;

	Map<String, Object> confirmOrderNumAndPay(String orderNo, String deviceNo, String app);

	boolean checkOrderNum(Integer integer, String key);

	int boundPrepayNo(String orderNo, String prepayId, String payNo);

	Map<String, Object> getWxPay(String app);

	String getOpenId(String deviceNo);

	String getIdfaByOrderNo(String orderNo);

	String getMemberId(String deviceNo);

	String getAdress(String deviceNo, String app) throws Exception;

	double activeOrder(String memberId,String orderNo, String couponId, String addressId, String num, String payId,
			 double totalFee, double orgPrice, double desPrice, String deviceNo, String app,Map<String, Object> addressMap,String remark, double balance) throws Exception;

	int boundOrderPrepay(String orderNo, String prepayId, String payNo);

	List<Map<String, Object>> getShopInfoByShopNos(String shopNo);

	double createOrderByShops(String memberId,String orderNo, String shopNo, String infoId, String textureIds, String textureName,
			String userId, String imgUrl, String fileType, String num, String name, String nowPrice,String payId, double orgPrice,
			double desPrice,String couponId,double totalFee,Map<String, Object> addressMap,String deviceNo,String app,String transportFee,String remark, double balance);

	void invalidShop(String shopNo, String deviceNo, String app);

	int getShopNum(String deviceNo, String app);

	String queryPreUrl(String infoId, String textureIds);

	Map<String, Object> createOrder(String resultPath, String infoId, String textureIds, String deviceNo, String app,
			String orderNo, String sufFormat, String num);

	Integer isBoutique(String infoId);

	String getEvalTypeNum(Map<String, Object> paramMap) throws Exception;

	String findAllEval(Map<String, Object> paramMap) throws Exception;

	String findEvalPic(Map<String, Object> paramMap) throws Exception;

	int createShop(String resultPath, String infoId, String textureIds, String deviceNo, String app, String orderNo,
			String sufFormat, String num);

	Integer mergeShop(String infoId, String textureIds, String deviceNo, String app, int parseInt);

	int updateAdress(String deviceNo, String app, String addressId, String name, String mobile, String province,
			String area, String isDefault);

	String getDeviceNoByShop(String shopNo);

	Map<String, Object> queryOrderInfoByOrderNo(String orderNo);

	int checkOrderIsPayed(String trade_no);

	boolean insertPayRecord(Map<String, Object> paramMap);

	boolean changeOrderStatus(Map<String, Object> paramMap);

	Map<String, Object> queryOrders(String out_trade_no);

	void separateOrder(Map<String, Object> tempMap);

	void update0status(String out_trade_no);

	boolean deleteAdress(String deviceNo, String app, String addressId);

	List<Map<String, Object>> queryShopList(String deviceNo, String app, int page, int number);

	String queryOrderCompanyName(String string);

	void insertDiyUser(String deviceNo, String app);

	double getCouponMoney(String homeId);

	void updateBalance0(String orderNo);

	void desUserBalance(String memberId, double balance);

	String couponCenter(String memberId) throws Exception;

	List<Map<String, Object>> couponList();

	Map<String, Object> activeCode(String memberId, String acticeCode);

	void exchangeCoupon(String memberId, String couponId);

	List<Map<String, Object>> getAvailCoupon(String memberId);

}
