package cn._51app.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import cn._51app.dao.GoodsDao;
import cn._51app.service.GoodsSercice;
import cn._51app.utils.BASE64;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.DateUtil;
import cn._51app.utils.OCSKey;
import cn._51app.utils.PropertiesUtil;
import cn._51app.utils.ThreeDESede;
import cn._51app.utils.WxPayUtil;
import net.rubyeye.xmemcached.MemcachedClient;

/**
 * @author Administrator 用户管理
 */
@Controller
public class GoodsServiceImpl implements GoodsSercice {
	@Autowired
	private GoodsDao goodsDao;
	@Autowired
	private MemcachedClient mc;
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	private final String diyRootPath =PropertiesUtil.getValue("diy.root.path");
	private final String updownloadRootDir =PropertiesUtil.getValue("uploadUrl.sys");
	private final String dgurl =PropertiesUtil.getValue("diy.goods.url");
	private final String orderNumer =PropertiesUtil.getValue("diy.order.page.size");
	private final String shopNumer =PropertiesUtil.getValue("diy.goods.page.size");
	//图片详情and介绍url
	private ObjectMapper mapper=new ObjectMapper();
	private DecimalFormat df= new DecimalFormat("######0.00");
	private final String EVALNUM =PropertiesUtil.getValue("evaluation.num");
	private final String EVALPICNUM =PropertiesUtil.getValue("evaluation.pic.num");
	
	public String q(String k) throws Exception{
		return mc.get(k);
	};
	
	public boolean add(String k, String v, int time) throws Exception{
		return mc.set(k,time,v);
	};
	
	public boolean del(String k) throws Exception{
		return mc.delete(k);
	};
	
	@Override
	public String getBanner() throws Exception{
		String json=null;
		List<Map<String, Object>> list=this.goodsDao.getBanner();
		if(list!=null && list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				String imgUrl=(String)list.get(i).get("imgUrl");
				list.get(i).put("imgUrl", url+imgUrl);
			}
			json= mapper.writeValueAsString(list);
		}
		return json;
	}
	
	@Override
	public String nice(Integer page) throws Exception{
		String key = OCSKey.DIY_NICE2_PAGE;
		page-=1;
		String cacheKey = key+page;
		Map<String,Object> paramMap =new HashMap<String,Object>();
		paramMap.put("page", page*50);
		paramMap.put("pagesize", 50);
		paramMap.put("dgurl", dgurl);
		paramMap.put("cacheKey", cacheKey);
		paramMap.put("cacheTime", 0);
		return this.goodsDao.nice(paramMap);
	}
	
	@Override
	public String youGoods(int id) throws Exception{
		String key =OCSKey.YOU_GOODSID;
		String cacheKey=key+id;
		Map<String,Object> paramMap =new HashMap<String,Object>();
		paramMap.put("id", id);
		paramMap.put("dgurl", dgurl);
		paramMap.put("cacheKey", cacheKey);
		paramMap.put("cacheTime", 0);
		return this.goodsDao.youGoods(paramMap);
	}
	
	@Override
	public String getGoodsDetails(String id) throws Exception{
		//获取缓存名称
		String key =OCSKey.DIY_GOODS_DETAILS_ID;
		//设置缓存格式
		String cacheKey=key+"_"+id;
		//设置传入Map
		Map<String,Object> paramMap =new HashMap<String,Object>();
		//设置图片前缀
		paramMap.put("dgurl", dgurl);
		//设置商品id
		paramMap.put("id", id);
		//设置缓存名称
		paramMap.put("cacheKey", cacheKey);
		//设置缓存时间
		paramMap.put("cacheTime", 0);
		//查询数据库，传入Map
		return this.goodsDao.getGoodsDetails(paramMap);
	}
	
	@Override
	public String getGoodsBuyParam(String id) throws Exception{
		//获取缓存名称
		String key =OCSKey.DIY_GOODS_BUYPARAM_ID;
		//设置缓存格式
		String cacheKey=key+id;
		//设置传入Map
		Map<String,Object> paramMap =new HashMap<String,Object>();
		//设置商品id
		paramMap.put("id", id);
		//设置缓存名称
		paramMap.put("cacheKey", cacheKey);
		//设置缓存时间
		paramMap.put("cacheTime", 0);
		//设置图片前缀
		paramMap.put("dgurl", dgurl);
		//查询数据库，传入Map
		return this.goodsDao.getGoodsShow(paramMap);
	}
	
	
	@Override
	public List<Map<String, Object>> getOrderComm(String orderNo) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = goodsDao.getOrderComm(orderNo);
		if(map.isEmpty()){
			return null;
		}
		String info_ids = null==map.get("info_ids")?"":map.get("info_ids").toString();
		String img_url = null==map.get("img_url")?"":map.get("img_url").toString();
		String file_type = null==map.get("file_type")?"":map.get("file_type").toString();
		String infoArr[] = info_ids.split(",");
		String imgArr[] = img_url.split(",");
		String fileArr[] = file_type.split(",");
		for (int i = 0; i < infoArr.length; i++) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("info_id", infoArr[i]);
			temp.put("img_url", dgurl+imgArr[i]);
			if(fileArr[i].equals("xxx"))
				temp.put("file_type", "");
			else
				temp.put("file_type", fileArr[i]);
			list.add(temp);
		}
		return list;
	}
	
	@Override
	public boolean saveComment(String orderNo, String info_id, String commentArea, String starNum, String deviceNo) {
		try {
			Map<String,Object> map = goodsDao.getMobile4Order(orderNo);
			String mobile = null==map.get("mobile")?"":map.get("mobile").toString();
			String texture_names = null==map.get("texture_names")?"":map.get("texture_names").toString();
			String[] textureArr = texture_names.split("\\|");
			
			String[] infoArr = info_id.split(",");
			String[] contentArr = commentArea.split(",");
			String[] starArr = starNum.split(",");
			for (int i = 0; i < infoArr.length; i++) {
				int star = Integer.parseInt(starArr[i]);
				int evalType = 1;
				if(star==1) evalType=3;
				else if(star==2||star==3) evalType=2;
				else evalType=1;
				String text = "";
				if(textureArr.length>i){
					text = textureArr[i];
				}
				goodsDao.saveComment(infoArr[i], evalType, contentArr[i], deviceNo, mobile, text);
			}
			goodsDao.updateEvaStatus(orderNo);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public String getIdfa(String memberId) {
		return this.goodsDao.getIdfa(memberId);
	}
	
	@Override
	public boolean deleteShop(String deviceNo, String app, String shopNos) {
		String[] shopNoss=shopNos.split(",");
		String shopNo="(";
		for (int i = 0; i < shopNoss.length; i++) {
			shopNo+=("'"+shopNoss[i]+"',");
		}
		shopNo=shopNo.substring(0,shopNo.length()-1);
		shopNo+=")";
		int result= this.goodsDao.deleteShop(deviceNo,app,shopNo);
		if(result>0){
			//更新购物车数量	
			this.goodsDao.updateShopOrderNum(deviceNo, app, "shop", -result);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateShop(String deviceNo, String app, String shopNos, String nums) {
		int result= this.goodsDao.updateShop(deviceNo,app,shopNos,nums);
		if(result>0){
			return true;
		}
		return false;
	}
	
	@Override
	public List<Map<String, Object>> getAdress(String deviceNo, String app) {
		return goodsDao.queryAddress(deviceNo, app);
	}
	
	@Override
	public List<Map<String, Object>> shopBuy(String shopNo, String deviceNo, String app) {
		String shopArr[] = shopNo.split(",");
		String shopNos = "(";
		for (int i = 0; i < shopArr.length; i++) {
			shopNos+="'"+shopArr[i]+"',";
		}
		shopNos = shopNos.substring(0,shopNos.length()-1)+")";
		List<Map<String, Object>> list = goodsDao.shopBuy(shopNos,deviceNo,app);
		for (Map<String, Object> map : list) {
			map.put("img_url", dgurl+map.get("img_url"));
		}
		return list;
	}
	
	@Override
	public String getOrderList(String deviceNo, String app,String page,String state) throws Exception{
		List<Map<String, Object>> result=new ArrayList<>();
		Map<String, Object> tempMap=null;
		if(StringUtils.isBlank(page)){
			page="0";
		}
		
		List<Map<String,Object>> list=this.goodsDao.getOrderList(deviceNo,app,Integer.parseInt(page),Integer.parseInt(orderNumer),Integer.parseInt(state));
		if(list!=null){
			for (Map<String, Object> map : list) {
				tempMap=new HashMap<>();
				Integer status=(Integer)map.get("status");
				String code=(String)map.get("code");
				String orderNo=(String)map.get("orderNo");
				String payType=(String)map.get("payType");
				double feeTotal=(Double)map.get("feeTotal");
				String expressNo=(String)map.get("expressNo");
				String transportfee=(String)map.get("transportfee");
				String[] prices=((String)map.get("price")).split(",");
				String[] names=((String)map.get("name")).split(",");
				String imgUrl=map.get("imgUrl")==null?"":map.get("imgUrl").toString();
				String[] imgUrls=imgUrl.split(",");
				String[] fileTypes=((String)map.get("fileType")).split(",");
				String[] nums=((String)map.get("num")).split(",");
				String[] textureNamess=((String)map.get("textureNames")).split("\\|");
				String[] infoIds=((String)map.get("infoIds")).split(",");
				String[] companyIds=((String)map.get("companyId")).split(",");
				List<Map<String, Object>> goodinfos=new ArrayList<>();
				Map<String, Object> tempMap2=null;
				for (int i = 0; i < prices.length; i++) {
					tempMap2=new HashMap<>();
					tempMap2.put("nowPrice", prices[i]);
					if(!imgUrl.equals("")){
					tempMap2.put("imgUrl", diyRootPath+imgUrls[i]);
					}else{
						tempMap2.put("imgUrl", null);
					}
					tempMap2.put("fileType", fileTypes[i]==null?"":fileTypes[i]);
					tempMap2.put("num", nums[i]);
					tempMap2.put("name", names[i]);
					tempMap2.put("textureName", textureNamess[i]);
					tempMap2.put("goodsId", infoIds[i]);
					goodinfos.add(tempMap2);
				}
				String merchant=this.goodsDao.queryOrderCompanyName(companyIds[0]);
				tempMap.put("companyName",merchant);
				tempMap.put("companyId",companyIds[0]);
				tempMap.put("goodinfo", goodinfos);
				tempMap.put("status", status);
				tempMap.put("code", code==null?"":code);
				tempMap.put("orderNo", orderNo);
				tempMap.put("payType", payType);
				tempMap.put("feeTotal", feeTotal);
				tempMap.put("expressNo", expressNo==null?"":expressNo.trim());
				tempMap.put("transportfee",transportfee);
				String expressMsg="";
				if(status==1){
					expressMsg="待付款";
				}else if(status==2 || status==8){
					expressMsg="待发货";
				}else if(status==3){
					expressMsg="卖家已发货";
				}else if(status==4){
					expressMsg="交易成功";
				}else if(status==5){
					expressMsg="交易关闭";
				}else if(status==6){
					expressMsg="交易关闭";
				}else if(status==7){
					expressMsg="交易成功";
				}
				tempMap.put("expressMsg", expressMsg);
				result.add(tempMap);
			}
		}
		if(result!=null && result.size()>0){
			return mapper.writeValueAsString(result);
		}
		return null;
	}
	
	@Override
	public boolean updateOrder(String orderNo, String deviceNo, String app, String flag) {
		if("delete".equalsIgnoreCase(flag) || "confirm".equalsIgnoreCase(flag) || "cancel".equalsIgnoreCase(flag) || "padiCancel".equals(flag)){
			return this.goodsDao.updateOrder(orderNo,deviceNo,app,flag);
		}else if("addShop".equalsIgnoreCase(flag)){
			//订单添加到购物车
			Map<String, Object> map=this.goodsDao.queryOrderInfo(orderNo, deviceNo, app);
			if(map!=null){
				String[] prices=((String)map.get("price")).split(",");
				String[] imgUrls=((String)map.get("imgUrl")).split(",");
				String[] fileTypes=((String)map.get("fileType")).split(",");
				String[] nums=((String)map.get("num")).split(",");
				String[] textureNamess=((String)map.get("textureNames")).split("\\|");
				String[] infoIdss=((String)map.get("infoIds")).split(",");
				String[] userIdss=((String)map.get("userIds")).split(",");
				String[] textureIdss=((String)map.get("textureIds")).split("\\|");
				String temShopNo= (String)map.get("shopNo");
				String[] shopNos=new String[]{};
				int index=0;
				if(StringUtils.isNotBlank(temShopNo)){
					shopNos=temShopNo.split(",");
					String shopNo="(";
					for (int i = 0; i < shopNos.length; i++) {
						shopNo+=("'"+shopNos[i]+"',");
					}
					shopNo=shopNo.substring(0,shopNo.length()-1);
					shopNo+=")";
					index=this.goodsDao.countActiveShop(deviceNo,app,shopNo);
				}else{
					shopNos=new String[prices.length];
					index=prices.length;
				}
				for (int i = 0; i < prices.length; i++) {
					this.goodsDao.addShopByOrder(prices[i],imgUrls[i],fileTypes[i],nums[i],textureNamess[i],infoIdss[i],userIdss[i],((shopNos[i]==null)?CommonUtil.createOrderNo("G", 3):shopNos[i]),deviceNo,app,textureIdss[i]);
				}
				//更新购物车数量
				this.goodsDao.updateShopOrderNum(deviceNo, app, "shop", index);
			}
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public String confirmOrder(String orderNo, String deviceNo, String app) throws Exception {
		//验证订单的商品是否都充足
		Map<String, Object> orderGoodNumAndPay=this.goodsDao.confirmOrderNumAndPay(orderNo,deviceNo,app);
		String payType=(String)orderGoodNumAndPay.get("payType");
		String prepayId=(String)orderGoodNumAndPay.get("prepayId");
		double feeTotal=(Double)orderGoodNumAndPay.get("feeTotal");
		Long time=(Long)orderGoodNumAndPay.get("time");
		String[] infoIdss=((String)orderGoodNumAndPay.get("infoIds")).split(",");
		String[] nums=((String)orderGoodNumAndPay.get("num")).split(",");
		//商品 数量合并然后去检测 每种数量是否充足
		Map<String, Integer> infoNum=new HashMap<>();
		for (int i = 0; i < infoIdss.length; i++) {
			Integer value=infoNum.get(infoIdss[i]);
			if(value==null || value==0){
				infoNum.put(infoIdss[i], Integer.parseInt(nums[i]));
			}else{
				infoNum.put(infoIdss[i], value+Integer.parseInt(nums[i]));
			}
		}
		boolean flag=true;
		for (String key:infoNum.keySet()) {
			flag=this.goodsDao.checkOrderNum(infoNum.get(key),key);
			if(!flag){
				break;
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		//如果是微信  验证prepayId是否过期  过期重新生成
		if("2".equals(payType)){
//			app="com.91luo.BestRing";
//			if(time==null||time>118){
//				Map<String,Object> mapres=WxPayUtil.H5unifiedorder(feeTotal, orderNo,getWxPay(app, "key"),getWxPay(app, "appid"),getWxPay(app, "appIDS"),getWxPay(app, "mchId"), goodsDao.getOpenId(deviceNo));
//				prepayId=(String)mapres.get("prepayId");
//				int result=this.goodsDao.boundPrepayNo(orderNo,(String)mapres.get("prepayId"),(String)mapres.get("payNo"));
//				if(result>0){
//					Map<String, Object> h5Map = WxPayUtil.returnH5Pay(getWxPay(app,"appIDS"), getWxPay(app,"key"), prepayId);//H5支付返回给页面的参数
//					map.put("prepayId", prepayId);
//					map.putAll(h5Map);
//				}
//			}else{
//				Map<String, Object> h5Map = WxPayUtil.returnH5Pay(getWxPay(app,"appIDS"), getWxPay(app,"key"), prepayId);//H5支付返回给页面的参数
//				map.put("prepayId", prepayId);
//				map.putAll(h5Map);
//			}
		}
		//余额变成0
		this.goodsDao.updateBalance0(orderNo);
		map.put("orderNo", orderNo);
		if(feeTotal<=0){
			feeTotal=0.01;
		}
		map.put("totalFee", df.format(feeTotal));
		map.put("payType", payType);
		map.put("creatTime", DateUtil.date2String(new Date(), DateUtil.FORMAT_DATETIME));
		if(flag){
			return mapper.writeValueAsString(map);
		}else{
			return null;
		}
	}
	
	/*
	 * 获取微信配置
	 */
	private String getWxPay(String app, String key) throws Exception{
		String json=q(OCSKey.DIY_WX_APP_+app.split("V")[0]);
		if(StringUtils.isBlank(json)){
			Map<String, Object> teMap=this.goodsDao.getWxPay(app.split("V")[0]);
			if(teMap!=null){
				json=mapper.writeValueAsString(teMap);
				add(OCSKey.DIY_WX_APP_+app.split("V")[0], json, 0);
			}
		}
		if(StringUtils.isNotBlank(json)){
			Map<String, Object> map=mapper.readValue(json, HashMap.class);
			return (String)map.get(key);
		}
		return null;
	}
	
	@Override
	public String getIdfaByOrderNo(String orderNo) {
		return this.goodsDao.getIdfaByOrderNo(orderNo);
	}
	
	@Override
	public String getMemberId(String deviceNo) {
		return this.goodsDao.getMemberId(deviceNo);
	}
	
	@Override
	public String formOrderOne(String memberId,String deviceNo, String app, String couponId, String orderNo, String addressId,
			String num, String payId,String remark,double balance) throws Exception {
		/*********/
		//验证订单商品的数量是够满足
		boolean flag=true;
		Map<String, Object> orderGoodNumAndPay=this.goodsDao.confirmOrderNumAndPay(orderNo,deviceNo,app);
		double totalFee=0;
		String[] infoIdss=((String)orderGoodNumAndPay.get("infoIds")).split(",");
		String[] nums=((String)orderGoodNumAndPay.get("num")).split(",");
		//商品 数量合并然后去检测 每种数量是否充足
		Map<String, Integer> infoNum=new HashMap<>();
		for (int i = 0; i < infoIdss.length; i++) {
			Integer value=infoNum.get(infoIdss[i]);
			if(value==null || value==0){
				infoNum.put(infoIdss[i], Integer.parseInt(nums[i]));
			}else{
				infoNum.put(infoIdss[i], value+Integer.parseInt(nums[i]));
			}
		}
		for (String key:infoNum.keySet()) {
			flag=this.goodsDao.checkOrderNum(infoNum.get(key),key);
			if(!flag){
				break;
			}
		}
		if(flag){ //商品数量充足继续处理
			/*********/
			Map<String, Object> resultMap=new HashMap<>();
			//查询订单总价  单价*数量 +运费     price    transportfee
			Map<String, Object> orderInfo=this.goodsDao.queryOrderInfo(orderNo,deviceNo,app);
			if(orderInfo==null){
				return null;
			}
			double price=Double.valueOf((String)orderInfo.get("price"));
			double transportfee=Double.valueOf((String)orderInfo.get("transportfee"));
			totalFee=price*Integer.parseInt(num)+transportfee;
			double orgPrice=0.00,desPrice=0.00;
			
			//检验地址信息
			try {
			String addressJson=goodsDao.getAdress(deviceNo, "com.91luo.BestRing");
			List<Map<String, Object>> addressList=mapper.readValue(addressJson, new TypeReference<List<Map<String,Object>>>() {});
			Map<String, Object> addressMap=new HashMap<>();
			for (Map<String, Object> map : addressList) {
				Integer id=(Integer)map.get("id");
				if(id.equals(Integer.parseInt(addressId))){
					addressMap=map;
					break;
				}
			}
			flag=true;
			//绑定订单需要的信息
			if(flag){
				double totalFee_=this.goodsDao.activeOrder(memberId,orderNo,couponId,addressId,num,payId,totalFee,orgPrice,desPrice,deviceNo,app,addressMap,remark,balance);
				if("2".equals(payId)){//微信
//					//请求微信生成prepayId
//					app="com.91luo.BestRing";
//					//String openid = iuuMallDao.getOpenId(deviceNo, app);
//					Map<String, Object> paraMap= WxPayUtil.H5unifiedorder(totalFee_, orderNo,getWxPay(app,"key"),getWxPay(app,"appid"),getWxPay(app, "appIDS"),getWxPay(app, "mchId"),goodsDao.getOpenId(deviceNo));
//					//WxPayUtil.H5unifiedorder(totalFee_, orderNo,getWxPay(app,"key"),getWxPay(app,"appid"),getWxPay(app, "appIDS"),getWxPay(app, "mchId"),iuuMallDao.getOpenId(deviceNo, app));
//					String prepayId=(String)paraMap.get("prepayId");
//					String payNo=(String)paraMap.get("payNo");
//					if(StringUtils.isBlank(prepayId)){
//						return null;
//					}
//					//将prepayid payNo绑定到订单  到支付信息表
//					int result=this.goodsDao.boundOrderPrepay(orderNo,prepayId,payNo);
//					if(result>0){
//						Map<String, Object> h5Map = WxPayUtil.returnH5Pay("wx204459d3f9148e3b", "54dfs2u32016hrfhasklijfhgdfgsdkl", prepayId);//H5支付返回给页面的参数
//						resultMap.put("prepayId", prepayId);
//						resultMap.putAll(h5Map);
//					}
				}else if("1".equals(payId)){
					
				}
				if(StringUtils.isNotBlank(orderNo)){
					//更新订单数
					this.goodsDao.updateShopOrderNum(deviceNo,app,"order",1);
				}
				resultMap.put("orderNo", orderNo);
				double allMoney=totalFee_-balance;
				if(allMoney<=0){
					allMoney=0.01;
				}
				resultMap.put("totalFee", df.format(allMoney));
				resultMap.put("creatTime", DateUtil.date2String(new Date(), DateUtil.FORMAT_DATETIME));
			}else{
				return null;
			}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return mapper.writeValueAsString(resultMap);
		}else{
			return null;
		}
	}
	
	@Override
	public String createOrderByShops(String memberId, String deviceNo, String app, String shopNos, String payId,
			String addressId, String couponId, String remark, double balance) throws Exception {
		String orderNo=CommonUtil.createOrderNo("U", 5);
		//查询多个购物车信息
		String[] shopNoss=shopNos.split(",");
		String shopNo="(";
		for (int i = 0; i < shopNoss.length; i++) {
			shopNo+=("'"+shopNoss[i]+"',");
		}
		shopNo=shopNo.substring(0,shopNo.length()-1);
		shopNo+=")";
		List<Map<String, Object>> list=this.goodsDao.getShopInfoByShopNos(shopNo);
		String shopNo_="",infoId_="",textureIds_="",temTextureName_="",userId_="",imgUrl_="",fileType_="",num_="",name_="",nowPrice_="",transportfee_="",isBoutique_="";
		double totalFee=0.00,orgPrice=0.00,desPrice=0.00;
		for (Map<String, Object> map : list) {
			shopNo_+=(String)map.get("shopNo")+",";
			infoId_+=String.valueOf(map.get("infoId"))+",";
			textureIds_+=(String)map.get("textureIds")+"|";
			temTextureName_+=(String)map.get("textureName")+"|";
			userId_+=String.valueOf(map.get("userId"))+",";
			imgUrl_+=(String)map.get("imgUrl")+",";
			fileType_+=(String)map.get("fileType")+",";
			String temNum=String.valueOf(map.get("num"));
			num_+=temNum+",";
			name_+=(String)map.get("name")+",";
			String temNowPrice=String.valueOf(map.get("nowPrice"));
			nowPrice_+=temNowPrice+",";
			String temTransportfee=String.valueOf(map.get("transportfee"));
			transportfee_=temTransportfee+",";
			totalFee+=Double.valueOf(temNowPrice)*Integer.parseInt(temNum)+Double.valueOf(temTransportfee);
			
			isBoutique_+=String.valueOf(map.get("isBoutique"))+",";
		}
		/*********/
		//验证订单商品的数量是够满足
		boolean flag=true;
		String[] infoIdss=CommonUtil.subStr(infoId_).split(",");
		String[] nums=CommonUtil.subStr(num_).split(",");
		//商品 数量合并然后去检测 每种数量是否充足
		Map<String, Integer> infoNum=new HashMap<>();
		for (int i = 0; i < infoIdss.length; i++) {
			Integer value=infoNum.get(infoIdss[i]);
			if(value==null || value==0){
				infoNum.put(infoIdss[i], Integer.parseInt(nums[i]));
			}else{
				infoNum.put(infoIdss[i], value+Integer.parseInt(nums[i]));
			}
		}
		for (String key:infoNum.keySet()) {
			flag=this.goodsDao.checkOrderNum(infoNum.get(key),key);
			if(!flag){
				break;
			}
		}
		/*********/
		if(flag){ //商品数量充足继续处理
			//检验地址信息
			String addressJson=goodsDao.getAdress(deviceNo, app);
			List<Map<String, Object>> addressList=mapper.readValue(addressJson, new TypeReference<List<Map<String,Object>>>() {});
			Map<String, Object> addressMap=new HashMap<>();
			for (Map<String, Object> map : addressList) {
				Integer id=(Integer)map.get("id");
				if(id.equals(Integer.parseInt(addressId))){
					addressMap=map;
					break;
				}
			}
			//生成订单
			double totalFee_=this.goodsDao.createOrderByShops(memberId,orderNo,CommonUtil.subStr(shopNo_),CommonUtil.subStr(infoId_),CommonUtil.subStr(textureIds_),CommonUtil.subStr(temTextureName_),CommonUtil.subStr(userId_),CommonUtil.subStr(imgUrl_),CommonUtil.subStr(fileType_),CommonUtil.subStr(num_),CommonUtil.subStr(name_),CommonUtil.subStr(nowPrice_),payId,orgPrice,desPrice,couponId,totalFee,addressMap,deviceNo,app,CommonUtil.subStr(transportfee_),remark,balance);
			Map<String, Object> resultMap=new HashMap<>();
			this.goodsDao.updateShopOrderNum(deviceNo, app, "order", 1);
			if("2".equals(payId)){//微信
//				//请求微信生成prepayId
//				app="com.91luo.BestRing";
//				Map<String, Object> paraMap= WxPayUtil.H5unifiedorder(totalFee_, orderNo,getWxPay(app,"key"),getWxPay(app,"appid"),getWxPay(app, "appIDS"),getWxPay(app, "mchId"),goodsDao.getOpenId(deviceNo));
//				String prepayId=(String)paraMap.get("prepayId");
//				String payNo=(String)paraMap.get("payNo");
//				if(StringUtils.isBlank(prepayId)){
//					return null;
//				}
//				//将prepayid payNo绑定到订单  到支付信息表
//				int result3=this.goodsDao.boundOrderPrepay(orderNo,prepayId,payNo);
//				if(result3>0){
//					Map<String, Object> h5Map = WxPayUtil.returnH5Pay(getWxPay(app,"appIDS"), getWxPay(app,"key"), prepayId);//H5支付返回给页面的参数
//					resultMap.put("prepayId", prepayId);
//					resultMap.putAll(h5Map);
//				}
			}
			resultMap.put("orderNo", orderNo);
			double allMoney=totalFee_-balance;
			if(allMoney<=0){
				allMoney=0.01;
			}
			resultMap.put("totalFee", df.format(allMoney));
			resultMap.put("creatTime", DateUtil.date2String(new Date(), DateUtil.FORMAT_DATETIME));
			this.invalidShops(shopNos,deviceNo,app);
			return mapper.writeValueAsString(resultMap);
		}else{
			return null;
		}
	}
	
	public void invalidShops(String shopNos,String deviceNo,String app){
		//查询多个购物车信息
		String[] shopNoss=shopNos.split(",");
		String shopNo="(";
		for (int i = 0; i < shopNoss.length; i++) {
			shopNo+=("'"+shopNoss[i]+"',");
		}
		shopNo=shopNo.substring(0,shopNo.length()-1);
		shopNo+=")";
		//购物车失效
		this.goodsDao.invalidShop(shopNo,deviceNo,app);
		//购物车数量减少
		this.goodsDao.updateShopOrderNum(deviceNo, app, "shop", -(shopNoss.length));
	}
	
	@Override
	public int getShopNum(String deviceNo, String app) {
		return goodsDao.getShopNum(deviceNo, app);
	}
	
	@Override
	public String createOrder(String infoId, String textureIds, String deviceNo, String app, String num) {
		Map<String, Object> resultMap=new HashMap<>();
		String orderNo=CommonUtil.createOrderNo("U", 5);
		String resultPath=null;
		try {
			String sufFormat="xxx";
			//查询出精品会商品图片  (预览图足够)
			resultPath=this.goodsDao.queryPreUrl(infoId,textureIds);
			//插入到订单表
			Map<String, Object> returnMap=this.goodsDao.createOrder(resultPath,infoId,textureIds,deviceNo,app,orderNo,sufFormat,num);
			Integer isBoutique = goodsDao.isBoutique(infoId);
			if(resultMap!=null){
				resultMap.put("orderNo", orderNo);
				resultMap.put("sufFormat", sufFormat);
				resultMap.put("imgUrl", diyRootPath+resultPath);
				resultMap.put("num", num);
				resultMap.put("name", returnMap.get("name"));
				resultMap.put("textureName", returnMap.get("textureNames"));
				resultMap.put("nowPrice", returnMap.get("orgPrice"));
				resultMap.put("isBoutique", isBoutique);
				return mapper.writeValueAsString(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String getEvalTypeNum(Integer goodsId) throws Exception{
		String cacheKey = OCSKey.DIY_EVALTYPENUM+goodsId;
		del(cacheKey);
		Map<String,Object> paramMap =new HashMap<String,Object>();
		paramMap.put("cacheKey", cacheKey);
		paramMap.put("cacheTime",10);
		paramMap.put("goodsId", goodsId);
		return goodsDao.getEvalTypeNum(paramMap);
	}
	
	@Override
	public String findAllEval(Integer page, Integer goodsId, Integer evalType) throws Exception{
		page-=1;
		String cacheKey = OCSKey.DIY_EVALPIC+goodsId+page+evalType;
		del(cacheKey);
		Map<String,Object> paramMap =new HashMap<String,Object>();
		paramMap.put("cacheKey", cacheKey);
		paramMap.put("cacheTime", 10);
		paramMap.put("page", page*(Integer.parseInt(EVALNUM)));
		paramMap.put("pagesize", Integer.parseInt(EVALNUM));
		paramMap.put("goodsId", goodsId);
		paramMap.put("evalType", evalType);
		return goodsDao.findAllEval(paramMap);
	}
	
	@Override
	public String findEvalPic(Integer page, Integer goodsId) throws Exception{
		page-=1;
		String cacheKey = OCSKey.DIY_EVALPIC+goodsId+page;
		Map<String,Object> paramMap =new HashMap<String,Object>();
		paramMap.put("cacheKey", cacheKey);
		paramMap.put("cacheTime", 10);
		paramMap.put("page", page*(Integer.parseInt(EVALPICNUM)));
		paramMap.put("pagesize", Integer.parseInt(EVALPICNUM));
		paramMap.put("goodsId", goodsId);
		return goodsDao.findEvalPic(paramMap);
	}
	
	@Override
	public boolean addShop(String infoId, String textureIds, String deviceNo, String num, String app) {
		if(goodsDao.mergeShop(infoId, textureIds, deviceNo, app, Integer.parseInt(num))>0){
			return true;
		}else{
			String orderNo=CommonUtil.createOrderNo("U", 3);
			try {
				String sufFormat="xxx";
				//查询出精品会商品图片 
				String resultPath=this.goodsDao.queryPreUrl(infoId,textureIds);
				int result=this.goodsDao.createShop(resultPath,infoId,textureIds,deviceNo,app,orderNo,sufFormat,num);
				if(result>0){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@Override
	public String updateAdress(String deviceNo, String app, String addressId, String name, String mobile,
			String province, String area, String isDefault) {
			int id=0;
			try {
				id=this.goodsDao.updateAdress(deviceNo,app,addressId,name,mobile,province,area,isDefault);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "{\"addressId\":"+id+"}";
	}
	
	@Override
	public String getDeviceNoByShop(String shopNo) {
		return goodsDao.getDeviceNoByShop(shopNo);
	}
	
	@Override
	public String getOrderInfo(String orderNo, String deviceNo, String app) throws Exception{
		Map<String, Object> result=new HashMap<>();
		Map<String, Object> map=this.goodsDao.queryOrderInfo(orderNo, deviceNo, app);
		if(map!=null){
			String transportfee=(String)map.get("transportfee");
			String province=(String)map.get("province");
			String area=(String)map.get("area");
			String mobile=(String)map.get("mobile");
			String payType=(String)map.get("payType");
			String code=(String)map.get("code");
			String consignee=(String)map.get("consignee");
			Integer status=(Integer)map.get("status");
			String expressNo=(String)map.get("expressNo");
			double feeTotal=(Double)map.get("feeTotal");
			String creatTime=(String)map.get("creatTime");
			String[] prices=((String)map.get("price")).split(",");
			String[] imgUrls=((String)map.get("imgUrl")).split(",");
			String[] fileTypes=((String)map.get("fileType")).split(",");
			String[] nums=((String)map.get("num")).split(",");
			String names=(String)map.get("name");
			String[] textureNamess=((String)map.get("textureNames")).split("\\|");
			String[] infoIds=((String)map.get("infoIds")).split(",");
			result.put("transportfee", transportfee==null?"":transportfee);
			result.put("province", province==null?"":province);
			result.put("area", area==null?"":area);
			result.put("mobile", mobile==null?"":mobile);
			result.put("payType", payType==null?"":payType);
			result.put("consignee", consignee==null?"":consignee);
			result.put("feeTotal", feeTotal);
			result.put("creatTime", creatTime==null?"":creatTime);
			result.put("orderNo", orderNo);
			result.put("status", status);
			result.put("code", code==null?"":code);
			result.put("expressNo", expressNo==null?"":expressNo.trim());
			String expressMsg="";
			if(status==1){
				expressMsg="待付款";
			}else if(status==2 || status==8){
				expressMsg="待发货";
			}else if(status==3){
				expressMsg="卖家已发货";
			}else if(status==4){
				expressMsg="交易成功";
			}else if(status==5){
				expressMsg="交易关闭";
			}else if(status==6){
				expressMsg="交易关闭";
			}else if(status==7){
				expressMsg="交易成功";
			}
			result.put("expressMsg", expressMsg);
			List<Map<String, Object>> goodinfos=new ArrayList<>();
			Map<String, Object> tempMap=null;
			for (int i = 0; i < prices.length; i++) {
				tempMap=new HashMap<>();
				tempMap.put("nowPrice", prices[i]);
				tempMap.put("imgUrl", diyRootPath+imgUrls[i]);
				tempMap.put("fileType", fileTypes[i]==null?"":fileTypes[i]);
				tempMap.put("num", nums[i]);
				tempMap.put("name", names);
				tempMap.put("textureName", textureNamess[i]);
				tempMap.put("goodsId", infoIds[i]);
				goodinfos.add(tempMap);
			}
			result.put("goodinfos", goodinfos);
			if(result!=null && result.size()>0){
				return mapper.writeValueAsString(result);
			}
		}
		return null;
	}
	
	@Override
	public String getOrderInfoByOrderNo(String orderNo) throws Exception{
		Map<String, Object> result=new HashMap<>();
		Map<String, Object> map=this.goodsDao.queryOrderInfoByOrderNo(orderNo);
		if(map!=null){
			String transportfee=(String)map.get("transportfee");
			String province=(String)map.get("province");
			String area=(String)map.get("area");
			String mobile=(String)map.get("mobile");
			String payType=(String)map.get("payType");
			String code=(String)map.get("code");
			String consignee=(String)map.get("consignee");
			Integer status=(Integer)map.get("status");
			String expressNo=(String)map.get("expressNo");
			double feeTotal=(Double)map.get("feeTotal");
			String creatTime=(String)map.get("creatTime");
			String[] prices=((String)map.get("price")).split(",");
			String[] imgUrls=((String)map.get("imgUrl")).split(",");
			String[] fileTypes=((String)map.get("fileType")).split(",");
			String[] nums=((String)map.get("num")).split(",");
			String names=(String)map.get("name");
			String[] textureNamess=((String)map.get("textureNames")).split("\\|");
			String[] infoIds=((String)map.get("infoIds")).split(",");
			result.put("transportfee", transportfee==null?"":transportfee);
			result.put("province", province==null?"":province);
			result.put("area", area==null?"":area);
			result.put("mobile", mobile==null?"":mobile);
			result.put("payType", payType==null?"":payType);
			result.put("consignee", consignee==null?"":consignee);
			result.put("feeTotal", feeTotal);
			result.put("creatTime", creatTime==null?"":creatTime);
			result.put("orderNo", orderNo);
			result.put("status", status);
			result.put("code", code==null?"":code);
			result.put("expressNo", expressNo==null?"":expressNo.trim());
			String expressMsg="";
			if(status==1){
				expressMsg="待付款";
			}else if(status==2 || status==8){
				expressMsg="待发货";
			}else if(status==3){
				expressMsg="卖家已发货";
			}else if(status==4){
				expressMsg="交易成功";
			}else if(status==5){
				expressMsg="交易关闭";
			}else if(status==6){
				expressMsg="交易关闭";
			}else if(status==7){
				expressMsg="交易成功";
			}
			result.put("expressMsg", expressMsg);
			List<Map<String, Object>> goodinfos=new ArrayList<>();
			Map<String, Object> tempMap=null;
			for (int i = 0; i < prices.length; i++) {
				tempMap=new HashMap<>();
				tempMap.put("nowPrice", prices[i]);
				tempMap.put("imgUrl", diyRootPath+imgUrls[i]);
				tempMap.put("fileType", fileTypes[i]==null?"":fileTypes[i]);
				tempMap.put("num", nums[i]);
				tempMap.put("name", names);
				tempMap.put("textureName", textureNamess[i]);
				tempMap.put("goodsId", infoIds[i]);
				goodinfos.add(tempMap);
			}
			result.put("goodinfos", goodinfos);
			if(result!=null && result.size()>0){
//				String memberId=goodsDao.getMeberByOrderNo(orderNo);
				String deviceNo=goodsDao.getIdfaByOrderNo(orderNo);
				String memberId=goodsDao.getMemberId(deviceNo);
				result.put("flag", BASE64.encode(ThreeDESede.encryptMode("memberId:"+memberId)));
				return mapper.writeValueAsString(result);
			}
		}
		return null;
	}
	
	@Override
	public int checkOrderIsPayed(String trade_no) {
		return this.goodsDao.checkOrderIsPayed(trade_no);
	}
	
	@Override
	public boolean insertPayRecord(Map<String, Object> paramMap) {
		return this.goodsDao.insertPayRecord(paramMap);
	}
	
	@Override
	public boolean changeOrderStatus(Map<String, Object> paramMap) {
		return goodsDao.changeOrderStatus(paramMap);
	}
	
	@Override
	public void separateOrder(String out_trade_no) {
		Map<String, Object> map=this.goodsDao.queryOrders(out_trade_no);
		if(map!=null){
			Integer status=(Integer)map.get("status");
			if(status==2){
				double balance=(double)map.get("balance");
				String device_no=(String)map.get("device_no");
				String memberId=goodsDao.getMemberId(device_no);
				if(balance!=0){
					this.goodsDao.desUserBalance(memberId,balance);
				}
				String userIds=(String)map.get("user_ids");
				String[] userIdArr = userIds.split(",");
				String[] norepeat = array_unique(userIdArr);
				if(norepeat.length>1){
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("porder_no", map.get("order_no"));
					tempMap.put("pay_type", map.get("pay_type"));
					tempMap.put("consignee", map.get("consignee"));
					tempMap.put("province", map.get("province"));
					tempMap.put("area", map.get("area"));
					tempMap.put("mobile", map.get("mobile"));
					tempMap.put("sys", map.get("sys"));
					tempMap.put("fee_transport", map.get("fee_transport"));
					tempMap.put("coupon", map.get("coupon"));
					tempMap.put("coupon_id", map.get("coupon_id"));
					tempMap.put("org_privilege", map.get("org_privilege"));
					tempMap.put("des_privilege", map.get("des_privilege"));
					tempMap.put("express_id", map.get("express_id"));
					tempMap.put("express_no", map.get("express_no"));
					tempMap.put("express_start", map.get("express_start"));
					tempMap.put("express_end", map.get("express_end"));
					tempMap.put("paytime", map.get("paytime"));
					tempMap.put("device_no", map.get("device_no"));
					tempMap.put("creat_time", map.get("creat_time"));
					tempMap.put("app", map.get("app"));
					tempMap.put("remark", map.get("remark"));
					tempMap.put("gType", map.get("gType"));
					tempMap.put("name", map.get("name"));
					
					String shop_no = null==map.get("shop_no")?"":map.get("shop_no").toString();
					String[] shop_nos = shop_no.split(",");
					String[] info_ids=((String)map.get("info_ids")).split(",");
					String[] texture_ids=((String)map.get("texture_ids")).split("\\|");
					String[] texture_names=((String)map.get("texture_names")).split("\\|");
					String[] img_url=((String)map.get("img_url")).split(",");
					String file_type=null==map.get("file_type")?"":map.get("file_type").toString();
					String[] file_types = file_type.split(",");
					String[] price=((String)map.get("price")).split(",");
					String[] num=((String)map.get("num")).split(",");
					String[] name=((String)map.get("price")).split(",");
					
					Map<String, String> indexMap = new HashMap<String, String>();
					for (int i = 0; i < userIdArr.length; i++) {
						String index = "";
						for (int j = 0; j < userIdArr.length; j++) {
							if(userIdArr[i].equals(userIdArr[j])){
								index+=j+",";
							}
						}
						indexMap.put(userIdArr[i], index.substring(0,index.length()-1));
					}
					for (int i = 0; i < norepeat.length; i++) {
						String user_ids_res = "";
						String shop_no_res = "";
						String info_ids_res = "";
						String texture_ids_res = "";
						String texture_names_res = "";
						String img_url_res = "";
						String file_types_res = "";
						String price_res = "";
						String num_res = "";
						String name_res = "";
						double fee_total = 0.00;
						String indexArr[] = ((String)indexMap.get(norepeat[i])).split(",");
						for (int j = 0; j < indexArr.length; j++) {
							int index = Integer.parseInt(indexArr[j]);
							user_ids_res += userIdArr[index]+",";
							shop_no_res += shop_nos[index]+",";
							info_ids_res += info_ids[index]+",";
							texture_ids_res += texture_ids[index]+"|";
							texture_names_res += texture_names[index]+"|";
							img_url_res += img_url[index]+",";
							file_types_res += file_types[index]+",";
							price_res += price[index]+",";
							num_res += num[index]+",";
							name_res += name[index]+",";
							double total = Double.valueOf(price[index])*Integer.parseInt(num[index]);
							fee_total +=total;
						}
						tempMap.put("user_ids", user_ids_res.substring(0, user_ids_res.length()-1));
						tempMap.put("shop_no", shop_no_res.substring(0, shop_no_res.length()-1));
						tempMap.put("info_ids", info_ids_res.substring(0, info_ids_res.length()-1));
						tempMap.put("texture_ids", texture_ids_res.substring(0, texture_ids_res.length()-1));
						tempMap.put("texture_names", texture_names_res.substring(0, texture_names_res.length()-1));
						tempMap.put("img_url", img_url_res.substring(0, img_url_res.length()-1));
						tempMap.put("file_type", file_types_res.substring(0, file_types_res.length()-1));
						tempMap.put("price", price_res.substring(0, price_res.length()-1));
						tempMap.put("num", num_res.substring(0, num_res.length()-1));
						tempMap.put("fee_total", fee_total);
						tempMap.put("name", name_res.substring(0, num_res.length()-1));
						String order_no = "";
						if(out_trade_no.indexOf("U")!=0)
							order_no = CommonUtil.createOrderNo("F", 5);
						else
							order_no = CommonUtil.createOrderNo("V", 5);
						tempMap.put("order_no", order_no);
						tempMap.put("status", 2);
						goodsDao.separateOrder(tempMap);
					}
					goodsDao.update0status(out_trade_no);
				}
			}
		}
	}
	
	//去除数组中重复的记录
	public static String[] array_unique(String[] a) {
	    List<String> list = new LinkedList<String>();
	    for(int i = 0; i < a.length; i++) {
	        if(!list.contains(a[i])) {
	            list.add(a[i]);
	        }
	    }
	    return (String[])list.toArray(new String[list.size()]);
	}
	
	@Override
	public boolean deleteAdress(String deviceNo, String app, String addressId) throws Exception{
		boolean flag=false;
		try {
			flag=this.goodsDao.deleteAdress(deviceNo,app,addressId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	@Override
	public String shopList(String deviceNo, String app, String page) throws Exception{
		if(StringUtils.isBlank(page)){
			page="0";
		}
		List<Map<String, Object>> list=this.goodsDao.queryShopList(deviceNo,app,Integer.parseInt(page),Integer.parseInt(shopNumer));
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				String fileType=(String)list.get(i).get("fileType");
				String imgUrl=(String)list.get(i).get("imgUrl");
				list.get(i).put("fileType", fileType==null?"":fileType);
				list.get(i).put("imgUrl", imgUrl==null?"":(diyRootPath+imgUrl));
				File file=new File(updownloadRootDir+imgUrl+"@pb"+fileType);
				if(file.exists()){
					list.get(i).put("previewBack", diyRootPath+imgUrl+"@pb"+fileType);
				}else{
					list.get(i).put("previewBack", "");
				}
			}
			return mapper.writeValueAsString(list);
		}
		return null;
	}
	
	@Override
	public void insertDiyUser(String deviceNo, String app) {
		this.goodsDao.insertDiyUser(deviceNo,app);
	}
	
	@Override
	public String couponCenter(String memberId) throws Exception{
		return this.goodsDao.couponCenter(memberId);
	}
	
	@Override
	public double getCouponMoney(String homeId) {
		return this.goodsDao.getCouponMoney(homeId);
	}
	
	@Override
	public String couponList() throws Exception{
		List<Map<String, Object>> list= this.goodsDao.couponList();
		if(list==null || list.size()<=0){
			return null;
		}
		return mapper.writeValueAsString(list);
	}
	
	@Override
	public String activeCode(String memberId, String acticeCode) throws Exception{
		Map<String, Object> result=this.goodsDao.activeCode(memberId,acticeCode);
		return mapper.writeValueAsString(result);
	}
	
	@Override
	public String exchangeCoupon(String memberId, String couponId) throws Exception{
		Map<String, Object> result=new HashMap<>();
		boolean flag=false;
		String message="";
		try {
			this.goodsDao.exchangeCoupon(memberId,couponId);
			message="兑换成功~";
			flag=true;
		} catch (Exception e) {
			message="兑换失败~";
		}
		result.put("flag", flag);
		result.put("message", message);
		return mapper.writeValueAsString(result);
	}
	
	@Override
	public List<Map<String, Object>> getAvailCoupon(String memberId) {
		List<Map<String, Object>> list=this.goodsDao.getAvailCoupon(memberId);
		if(list==null || list.size()<=0){
			return null;
		}
		for (int i = 0; i < list.size(); i++) {
			double orgPrice=(double)list.get(i).get("orgPrice");
			double nowPrice=(double)list.get(i).get("nowPrice");
			list.get(i).put("orgPrice", Double.valueOf(orgPrice).intValue());
			list.get(i).put("nowPrice", Double.valueOf(nowPrice).intValue());
		}
		return list;
	}
}

