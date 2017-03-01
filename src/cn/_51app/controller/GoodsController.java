
package cn._51app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn._51app.service.GoodsSercice;
import cn._51app.service.TaskSercice;
import cn._51app.utils.BASE64;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.ThreeDESede;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 * 任务操作
 */
@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {
	@Autowired
	private GoodsSercice goodsSercice;
	@Autowired
	private TaskSercice taskSercice;
	@Value("#{pValue['baby.api']}")
	private String urlApi;
	private ObjectMapper mapper=new ObjectMapper();

	@RequestMapping("/getBanner")
	public ResponseEntity<String> getBanner(){
		String data =null;
		String msg =null;
		int code =SUCESS;
		try {
			data =goodsSercice.getBanner();
			if(StringUtils.isBlank(data)){
				code=EMPTY;
				msg="";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	@RequestMapping("/tonice")
	public String tonice(@RequestParam(value="flag",required=false) String flag,Model model,HttpServletResponse response){
		if(StringUtils.isNotBlank(flag)){
			Cookie cookie=new Cookie("flag", flag);
			cookie.setMaxAge(10*24*60*60*60000);
			response.addCookie(cookie);
		}
		return "nice-together";
	}
	
	@RequestMapping("/successCoupon")
	public String successCoupon(Model model,HttpServletRequest request){
		double money=this.goodsSercice.getCouponMoney(request.getParameter("homeId"));
		model.addAttribute("money", money);
		return "successCoupon";
	}
	
	/**
	 * 
	 * @author zhanglz
	 * @param page:当前页
	 * @return 精品汇
	 */
	@RequestMapping("/nice")
	public ResponseEntity<String> nice(@RequestParam(value="page")Integer page){
		String data =null;
		String msg=null;
		int code =SUCESS;
		if(page==null)
			page=1;
		try {
			String json =this.goodsSercice.nice(page);
			if(StringUtils.isBlank(json)){
				code =EMPTY;
			}else{
				data=json;
			}
		//数据异常错误码
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 进入商品详情
	 * @author zhanglz
	 */
	@RequestMapping("/goodsInfo")
	public String goodsInfo(Model model,String id,HttpServletRequest request){
		try {
			String json = this.goodsSercice.youGoods(Integer.parseInt(id));
			Map<String,Object> lm=JSONObject.fromObject(json);
			model.addAttribute("goods", lm);
			
			//商品详情数据
			String goodsDetailsStr = this.goodsSercice.getGoodsDetails(id);
			Map<String,Object> m=JSONObject.fromObject(goodsDetailsStr);
			model.addAttribute("goodsDetails", m);
			
			//材质数据
			String goodsBuyParamStr =this.goodsSercice.getGoodsBuyParam(id);
			Map<String,Object> showMap=JSONObject.fromObject(goodsBuyParamStr);
			model.addAttribute("json", goodsBuyParamStr);
			model.addAttribute("goodsBuyParam", showMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "goods-info";
	}
	
	/**
	 * 查看商品评价
	 * @author zhanglz
	 */
	@RequestMapping("/toevaluation")
	public String toevaluation(String id,
			Model model){
			model.addAttribute("id", new Integer(id));
		return "comment";
	}
	
	/**
	 * 进入评价页面
	 * @author zhanglz
	 */
	@RequestMapping("/releaseComment")
	public String releaseComment(Model model,String orderNo){
		List<Map<String, Object>> list = goodsSercice.getOrderComm(orderNo);
		model.addAttribute("com", list);
		model.addAttribute("orderNo", orderNo);
		return "release-Comment";
	}
	
	/**
	 * 订单详情
	 * @author zhanglz
	 */
	@RequestMapping("/orderDetail")
	public String orderDetail(Model model,String orderNo){
		try {
			String data =goodsSercice.getOrderInfoByOrderNo(orderNo);
			Map<String, Object> result=mapper.readValue(data, HashMap.class);
			model.addAttribute("order", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "order_detail";
	}
	
	/**
	 * 保存评价
	 * @author zhanglz
	 */
	@RequestMapping(value="/saveComment",method={RequestMethod.POST})
	public String saveComment(Model model,
			@RequestParam(value = "orderNo") String orderNo,
			@RequestParam(value = "info_id") String info_id,
			@RequestParam(value = "commentArea")String commentArea,
			@RequestParam(value = "starNum")String starNum) throws Exception{
		String deviceNo = goodsSercice.getIdfaByOrderNo(orderNo);
		goodsSercice.saveComment(orderNo, info_id, commentArea, starNum, deviceNo);
		return "redirect:/goods/toOrderList.do";
	}
	
	public String toOrderList(Model modelm,@RequestParam(value="flag") String flag,HttpServletResponse response,HttpServletRequest request){
		if(StringUtils.isNotBlank(flag) && StringUtils.isNotBlank(CommonUtil.getCookie(request, "flag"))){
			Cookie cookie=new Cookie("flag", flag);
			cookie.setMaxAge(10*24*60*60*60000);
			response.addCookie(cookie);
		}
		return "order_list";
	}
	
	@RequestMapping("/toOrderListInner")
	public String toOrderListInner(Model model,@RequestParam(value="flag") String flag,HttpServletResponse response,HttpServletRequest request){
		if(StringUtils.isNotBlank(flag) && StringUtils.isNotBlank(CommonUtil.getCookie(request, "flag"))){
			Cookie cookie=new Cookie("flag", flag);
			cookie.setMaxAge(10*24*60*60*60000);
			response.addCookie(cookie);
		}
		return "order_listInner";
	}
	
	/**
	 * 订单列表
	 * @author zhanglz
	 */
	@RequestMapping("/getOrderList")
	public ResponseEntity<String> getOrderList(@RequestParam(value="page")String page,
			@RequestParam(value="status")String status,
			HttpServletRequest request){
		String data =null;
		String msg =null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			if(StringUtils.isNotBlank(flag)){
				String memberId=getMemberIdFromFlag(flag);
				String idfa = goodsSercice.getIdfa(memberId);
				data =goodsSercice.getOrderList(idfa,"com.91luo.BestRing",page,status);
			}
			if(StringUtils.isBlank(data)){
				code=EMPTY;
				msg="您还没有订单";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 订单状态的改变
	 * @param operation  delete删除   confirm确认收货  cancel取消  addShop加入购物车
	 * @author zhanglz
	 */
	@RequestMapping("/updateOrder")
	public ResponseEntity<String> updateOrder(@RequestParam(value="orderNo") String orderNo,
			@RequestParam(value="operation") String operation,HttpServletRequest request){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			boolean result =goodsSercice.updateOrder(orderNo,deviceNo,"com.91luo.BestRing",operation);
			if(!result){
				code=FAIL;
				msg="订单操作失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	@RequestMapping("/toShop")
	public String toShop(Model model,HttpServletRequest request,HttpServletResponse response){
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo=goodsSercice.getIdfa(memberId);
			String data=goodsSercice.shopList(deviceNo, "com.91luo.BestRing",(String)request.getParameter("page"));
			List<Map<String, Object>> list=new ArrayList<>();
			if(StringUtils.isNotBlank(data)){
				 list=mapper.readValue(data, List.class);
			}
			model.addAttribute("shop", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "shoppingCar_index";
	}
	
	@RequestMapping("/toShopInner")
	public String toShopInner(Model model,HttpServletRequest request){
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String data=null;
			if(StringUtils.isNotBlank(flag)){
				String memberId=getMemberIdFromFlag(flag);
				String deviceNo=goodsSercice.getIdfa(memberId);
				data=goodsSercice.shopList(deviceNo, "com.91luo.BestRing",(String)request.getParameter("page"));
			}
			List<Map<String, Object>> list=new ArrayList<>();
			if(StringUtils.isNotBlank(data)){
				 list=mapper.readValue(data, List.class);
			} 
			model.addAttribute("shop", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "shoppingCar_indexInner";
	}
	
	/**
	 * 购物车列表
	 * @author zhanglz
	 */
	@RequestMapping("/shopList")
	public ResponseEntity<String> shopList(HttpServletRequest request,String page){
		String msg=null;
		int code =SERVER_ERR;
		String data = null;
		if(page==null){
			page="0";
		}
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			data = goodsSercice.shopList(deviceNo, app, page);
			code=SUCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.resultInfo(data, code, msg);
	}
	/**
	 * 加入购物车
	 * @author zhanglz
	 */
	@RequestMapping("/addShop")
	public ResponseEntity<String> addShop(HttpServletRequest request,
			@RequestParam(value = "infoId") String infoId,
			@RequestParam(value = "textureIds" )String textureIds,
			@RequestParam(value = "num") String num){
		String msg=null;
		int code =SUCESS;
		String data = null;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			boolean checkFlag = goodsSercice.addShop(infoId, textureIds, deviceNo, num, app);//(infoId, textureIds, deviceNo, num, app);
			if(!checkFlag){
				code =FAIL;
				msg="添加到购物车失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code=SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	@RequestMapping("/editShop")
	public ResponseEntity<String> editShop(@RequestParam(value = "shopNo")String shopNo,
			@RequestParam(value = "operation")String operation,
			@RequestParam(value = "total_number")String total_number,
			HttpServletRequest request){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String deviceNo=goodsSercice.getIdfa(getMemberIdFromFlag(flag));
			boolean checkflag = false;
			if(operation.contains("delete")){
				checkflag =this.goodsSercice.deleteShop(deviceNo,"com.91luo.BestRing",shopNo);
			}else if(operation.contains("edit")){
				checkflag =this.goodsSercice.updateShop(deviceNo,"com.91luo.BestRing",shopNo,total_number);
			}
			if(!checkflag){
				code =FAIL;
				msg="编辑购物车失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	@RequestMapping("/shopBuy")
	public String shopBuy(Model model,String shopNo){
		try {
			String deviceNo=goodsSercice.getDeviceNoByShop(shopNo.split(",")[0]);
			String memberId=goodsSercice.getMemberId(deviceNo);
			model.addAttribute("flag", BASE64.encode(ThreeDESede.encryptMode("memberId:"+memberId)));
			List<Map<String, Object>> list = goodsSercice.shopBuy(shopNo,deviceNo,"com.91luo.BestRing");
			model.addAttribute("shop", list);
			model.addAttribute("shopSize", list.size());
			model.addAttribute("shopNos", shopNo);
			//地址信息
			List<Map<String, Object>> adress = goodsSercice.getAdress(deviceNo, "com.91luo.BestRing");
			model.addAttribute("adress", adress);
			//优惠卷信息
			List<Map<String, Object>> coupon = goodsSercice.getAvailCoupon(memberId);
			double balance=(double)this.taskSercice.getBalance(memberId);
			model.addAttribute("coupon", coupon);
			model.addAttribute("isOne", 2);
			model.addAttribute("balance", balance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "shoppingCar_confirm";
	}
	
	private String getMemberIdFromFlag(String flag){
		try {
			if(StringUtils.isBlank(flag)){
				return null;
			}
			String temp=ThreeDESede.decryptMode(new String(BASE64.decode(flag)));
			if(StringUtils.isNotBlank(temp.split(":")[1])){
				return temp.split(":")[1];
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 订单列表立即支付
	 * @author zhanglz
	 */
	@RequestMapping("/orderPay")
	public void orderPay(Model model,
			@RequestParam(value = "orderNo")String orderNo,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String data =null;
		Map<String, Object> map=new HashMap<>();
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId= getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			data= goodsSercice.confirmOrder(orderNo,deviceNo, "com.91luo.BestRing");
			map=JSONObject.fromObject(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.sendRedirect("pay://?orderNo="+map.get("orderNo")+"&totalFee="+map.get("totalFee")+"&creatTime="+map.get("creatTime"));
	}
	
	/**
	 * 订单详情取消订单
	 * @param 
	 * @author zhanglz
	 */
	@RequestMapping("/cancel4Detail")
	public String cancel4Detail(HttpServletRequest request,
			@RequestParam(value="orderNo") String orderNo,
			Model model	){
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String operation = "cancel";
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			goodsSercice.updateOrder(orderNo,deviceNo,app,operation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "order_list";
	}
	
	/**
	 * 立即购买提交订单
	 * @author zhanglz
	 */
	@RequestMapping(value ="/formOrderOne",method = { RequestMethod.POST })
	public ResponseEntity<String> formOrderOne(Model model,HttpServletRequest request,
			@RequestParam(value = "orderNo")String orderNo,
			@RequestParam(value = "couponId",required=false)String couponId,
			@RequestParam(value = "addressId")String addressId,
			@RequestParam(value = "num")String num,
			@RequestParam(value = "payId")String payId,
			@RequestParam(value = "balance")double balance,
			@RequestParam(value = "remark",required=false)String remark) throws Exception{
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			data = goodsSercice.formOrderOne(memberId,deviceNo, "com.91luo.BestRing", couponId, orderNo, addressId, num, payId,remark,balance);
			if(StringUtils.isNotBlank(data)){
				code=SUCESS;
			}else{
				code =FAIL;
				msg="支付失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 购物车生成订单
	 * @author zhanglz
	 */
	@RequestMapping(value ="/createOrderByShops",method = { RequestMethod.POST })
	public ResponseEntity<String> createOrderByShops(Model model,HttpServletRequest request,
			@RequestParam(value = "shopNos") String shopNos,
			@RequestParam(value = "payId") String payId,
			@RequestParam(value = "addressId") String addressId,
			@RequestParam(value = "couponId",required=false) String couponId,
			@RequestParam(value = "balance")double balance,
			@RequestParam(value = "remark",required=false) String remark){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			data = goodsSercice.createOrderByShops(memberId,deviceNo,"com.91luo.BestRing",shopNos,payId,addressId,couponId,remark,balance);
			if(StringUtils.isNotBlank(data)){
				code=SUCESS;
			}else{
				code =FAIL;
				msg="支付失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 立即购买
	 * @author zhanglz
	 */
	@RequestMapping("/createOrder")
	public String createOrder(HttpServletRequest request,Model model,
			@RequestParam(value = "infoId") String infoId,
			@RequestParam(value = "textureIds" )String textureIds,
			@RequestParam(value = "num") String num){
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			String data=this.goodsSercice.createOrder(infoId,textureIds,deviceNo,app,num);
			Map<String,Object> m=JSONObject.fromObject(data);
			model.addAttribute("goods", m);
			//地址信息
			List<Map<String, Object>> adress = goodsSercice.getAdress(deviceNo, app);
			model.addAttribute("adress", adress);
			//优惠卷信息
			List<Map<String, Object>> coupon = goodsSercice.getAvailCoupon(memberId);
			//查询账户余额
			double balance=(double)this.taskSercice.getBalance(memberId);
			model.addAttribute("coupon", coupon);
			model.addAttribute("isOne", 1);
			model.addAttribute("balance", balance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "shoppingCar_confirm";
	}
	
	/**
	 * 保存修改地址
	 * @author zhanglz
	 */
	@RequestMapping(value ="/updateAdress")
	public ResponseEntity<String> updateAdress(HttpServletRequest request,
			@RequestParam(value = "addressId",required=false) String addressId,
			@RequestParam(value = "name") String name,
			@RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "province") String province,
			@RequestParam(value = "area") String area,
			@RequestParam(value = "isDefault",required=false) String isDefault){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			if("new".equals(addressId))
				addressId = "";
			if(isDefault==null||!"1".equals(isDefault))
				isDefault="0";
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			data =goodsSercice.updateAdress(deviceNo,app,addressId,name,mobile,province,area,isDefault);
			if(StringUtils.isNotBlank(data)){
				code=SUCESS;
			}else{
				code =FAIL;
				msg="修改地址失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * @author zhanglz
	 * @return 查询评论类型的数量
	 */
	@RequestMapping("/evalNum")
	public ResponseEntity<String> getEvalTypeNum(Integer goodsId){
		
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String json =this.goodsSercice.getEvalTypeNum(goodsId);
			if(json==null||json.equals("")||json.length()<=2){
				code =EMPTY;
			}else{
				data=json;
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 购物车数量
	 * @author zhanglz
	 */
	@RequestMapping("/shopNum")
	public ResponseEntity<String> shopNum(HttpServletRequest request){
		String msg=null;
		int code =SERVER_ERR;
		String data = null;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			data = goodsSercice.getShopNum(deviceNo, app)+"";
			code=SUCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * @author zhanglz
	 * @return 分页查询评论
	 */
	@RequestMapping("/evalPage")
	public ResponseEntity<String> findAllEval(Integer page,Integer goodsId,Integer evalType){
		if(page==null)
			page=1;
		if(goodsId==null)
			return super.resultInfo(null, FAIL, null);
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String json =this.goodsSercice.findAllEval(page, goodsId,evalType);
			if(json==null||json.equals("")||json.length()<=2){
				code =EMPTY;
			}else{
				data=json;
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * @author zhanglz
	 * @return 查询评论的图片
	 */
	@RequestMapping("/evalPic")
	public ResponseEntity<String> findEvalPic(Integer page,Integer goodsId){
		if(page==null)
			page=1;
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String json =this.goodsSercice.findEvalPic(page, goodsId);
			if(json==null||json.equals("")||json.length()<=2){
				code =EMPTY;
			}else{
				data=json;
			}
		} catch (Exception e) {
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 删除地址
	 * @author zhanglz
	 */
	@RequestMapping("/deleteAdress")
	public ResponseEntity<String> deleteAdress(HttpServletRequest request,
			@RequestParam(value="addressId")String addressId){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			String flag=CommonUtil.getCookie(request, "flag");
			String memberId=getMemberIdFromFlag(flag);
			String deviceNo = goodsSercice.getIdfa(memberId);
			String app = "com.91luo.BestRing";
			boolean checkFlag =goodsSercice.deleteAdress(deviceNo,app,addressId);
			if(checkFlag){
				code=SUCESS;
			}else{
				code =FAIL;
				msg="删除地址";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * tengh 2016年10月12日 下午8:25:00
	 * @param request
	 * @param model
	 * @param response
	 * @param flag
	 * @return
	 * TODO 购物车
	 */
	@RequestMapping("/getCenterPage")
	public String getCenterPage(HttpServletRequest request,Model model,HttpServletResponse response,@RequestParam(value="flag")String flag){
//		if(StringUtils.isNotBlank(flag) && StringUtils.isNotBlank(CommonUtil.getCookie(request, "flag"))){
//			Cookie cookie=new Cookie("flag", flag);
//			cookie.setMaxAge(10*24*60*60*60000);
//			response.addCookie(cookie);
//		}
////		String memberId=getMemberIdFromFlag(flag);
////		String deviceNo=goodsSercice.getIdfa(memberId);
////		String app="com.91luo.BestRing";
////		String html="http://api.diy.51app.cn/diyMall/coupon2/center.do?deviceNo="+deviceNo+"&app="+app;
//		String html=urlApi+"baby/goods/center.do?flag="+flag;
//		model.addAttribute("url1", html);
		return "loan";
	}
	
	/**
	 * TODO 领券中心优惠券
	 * @return
	 */
	@RequestMapping("/center")
	public ResponseEntity<String>center(@RequestParam("flag")String flag){
		int code=SUCESS;
		String data=null;
		String msg=null;
		try {
			data=goodsSercice.couponCenter(getMemberIdFromFlag(flag));
		} catch (Exception e) {
			e.printStackTrace();
			code=SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * TODO 代金券券中心
	 * @return
	 */
	@RequestMapping("/couponList")
	public ResponseEntity<String>couponList(){
		int code=SUCESS;
		String data=null;
		String msg=null;
		try {
			data=goodsSercice.couponList();
		} catch (Exception e) {
			e.printStackTrace();
			code=SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * tengh 2016年10月12日 下午8:25:10
	 * @param request
	 * @return
	 * TODO 激活码兑换
	 */
	@RequestMapping("/activeCode")
	public ResponseEntity<String>activeCode(HttpServletRequest request){
		int code=SUCESS;
		String data=null;
		String msg=null;
		try {
			String flag=request.getParameter("flag");
			String acticeCode=request.getParameter("code");
			data=goodsSercice.activeCode(getMemberIdFromFlag(flag),acticeCode);
		} catch (Exception e) {
			e.printStackTrace();
			code=SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * tengh 2016年10月12日 下午8:25:21
	 * @param request
	 * @return
	 * TODO 余额购买代金券
	 */
	@RequestMapping("/exchangeCoupon")
	public ResponseEntity<String>exchangeCoupon(HttpServletRequest request){
		int code=SUCESS;
		String data=null;
		String msg=null;
		try {
			String flag=request.getParameter("flag");
			String couponId=request.getParameter("id");
			data=goodsSercice.exchangeCoupon(getMemberIdFromFlag(flag),couponId);
		} catch (Exception e) {
			e.printStackTrace();
			code=SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * tengh 2016年10月12日 下午9:19:15
	 * @param model
	 * @return
	 * TODO 兑换激活码
	 */
	@RequestMapping("/activeCoupon")
	public String activeCoupon(Model model){
		return "getCoupons";
	}
	
}
