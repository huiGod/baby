
package cn._51app.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn._51app.service.AllianceSercice;
import cn._51app.utils.MD5;
import cn._51app.utils.ThreeDESede;


/**
 * @author Administrator
 * 联盟任务回调
 */
@Controller
@RequestMapping("/active")
public class ActiveController{
	@Autowired
	private AllianceSercice allianceSercice;
	private Logger activeLog=Logger.getLogger("activeClickLogger");
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * tengh 2016年7月18日 下午3:16:54
	 * @param param
	 * @return 
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 * TODO 万普api回调   ok
	 */
	@RequestMapping(value = "/wpapi", method = { RequestMethod.GET })
	public void wpapi(
			HttpServletResponse response,HttpServletRequest request
			) throws UnsupportedEncodingException {
		activeLog.info("万普账号标识："+request.getQueryString());
		String adv_id = request.getParameter("adv_id");//被下载应用ID
		String app_id = request.getParameter("app_id");//万普平台注册应用id
		String key = request.getParameter("key");//调用积分墙时传入的key或者用户id
		String udid = request.getParameter("udid");//设备唯码
		String openudid = request.getParameter("open_udid");	//设备open_udid
		String bill = request.getParameter("bill");//价格
		String uid = request.getParameter("uid");//账号保留参数
		if(bill == null){bill = "";}
		String points = request.getParameter("points");//积分
		if(points == null){points = "";}
//		String ad_name = new String(request.getParameter("ad_name").getBytes("iso8859-1"),"utf-8");//下载的应用名称
		String ad_name=URLDecoder.decode(request.getParameter("ad_name"));
		String status = request.getParameter("status");//状态
		if(status == null){status = "";}
		String activate_time = request.getParameter("activate_time");//激活时间
		//========以下参数为旧版验证数据的参数，可以废弃此方法加密
		String order_id = request.getParameter("order_id");				//订单号
		String random_code = request.getParameter("random_code");//随机串
		String secret_key = request.getParameter("secret_key");//验证密钥
		//========新版验证
		String wapskey = request.getParameter("wapskey");//数据加密串
		String callBackKey = "wpszwcl2016";		//回调密钥
		//当满足价格、积分不为空且不为0和状态码为1时才为有效数据
		//验证订单是否已经处理过
		boolean checkOrder=this.allianceSercice.checkOrder(order_id);
		if(!bill.equals("0") && !points.equals("0") && status.equals("1") && !checkOrder){
			activate_time = URLEncoder.encode(activate_time, "UTF-8");//激活时间在传输时会自动解码，加密是要用url编码过的，如果没有自动解码请忽略转码
			//加密并判断密钥
			String plaintext = adv_id+app_id+key+udid+bill+points+activate_time+order_id+callBackKey;
			String keys = MD5(plaintext);
			//判断和wapskey是否相同
			if(keys.equalsIgnoreCase(wapskey)){
				//调价回调记录
				this.allianceSercice.insertRecord(app_id,URLDecoder.decode(activate_time),String.valueOf(Double.valueOf(points)/100),order_id,"wp",key,ad_name);
				//加钱
				this.allianceSercice.updateUser(key,String.valueOf(Double.valueOf(points)/100));
				response.setStatus(200);
			}else{
				activeLog.info("wanpu callback sign error!");
			}

		}else{
			activeLog.info("wanpu callback no data!");
		}
	}
	
	/**
	 * tengh 2016年7月18日 下午8:26:28
	 * @param response
	 * TODO 趣米回调   ok
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/qmapi",method=RequestMethod.GET)
	public void qmapi(
			HttpServletResponse response,HttpServletRequest request
			) throws UnsupportedEncodingException{
		activeLog.info("趣米"+request.getQueryString());
		String point_callback_secret="9c054facb024abd9";
		String order = URLDecoder.decode(request.getParameter("order"));
		String app = URLDecoder.decode(request.getParameter("app"));
//		String ad =new String(request.getParameter("ad").getBytes("iso8859-1"),"utf-8");
		String ad = URLDecoder.decode(request.getParameter("ad"));
		String user = URLDecoder.decode(request.getParameter("user"));
		String device = URLDecoder.decode(request.getParameter("device"));
		String points = URLDecoder.decode(request.getParameter("points"));
		String time = URLDecoder.decode(request.getParameter("time"));
		String sig = URLDecoder.decode(request.getParameter("sig"));
		String sig2 = URLDecoder.decode(request.getParameter("sig2"));
//		StringBuffer str_temp1=new StringBuffer();
//		str_temp1.append(point_callback_secret).append("||").append(order).append("||").append(app).append("||").append(ad).append("||").append(user).append("||").append(device).append("||").append(points).append("||").append(time);
		StringBuffer str_temp2=new StringBuffer();
//		activeLog.info("str_temp1:"+str_temp1);
		str_temp2.append(point_callback_secret).append("||").append(order).append("||").append(app).append("||").append(user).append("||").append(device).append("||").append(points).append("||").append(time);
		activeLog.info("str_temp2:"+str_temp2);
		boolean checkOrder=this.allianceSercice.checkOrder(order);
//		String sig_=MD5(str_temp1.toString()).substring(8, 24);
		String sig2_=MD5(str_temp2.toString()).substring(8, 24);
		if(!checkOrder){
			if(sig2.equalsIgnoreCase(sig2_)){
				String memberId="";
				try {
					System.err.println("qumi-->user:"+user);
					String temp=ThreeDESede.decryptMode(user);
					memberId=temp.split(":")[1];
				} catch (Exception e) {
					e.printStackTrace();
				}
				//调价回调记录
				this.allianceSercice.insertRecord("",format.format(new Date(Long.valueOf(time+"000"))),String.valueOf((Double.valueOf(points)/100)),order,"qm",memberId,ad);
				//加钱
				this.allianceSercice.updateUser(memberId,String.valueOf((Double.valueOf(points)/100)));
				response.setStatus(200);
			}else{
				activeLog.info("qumi callback sign failed!");
			}
		}else{
			response.setStatus(403);
		}
	}
	
	/**
	 * tengh 2016年7月19日 上午11:12:29
	 * @param response
	 * TODO 掌上互动回调  ok
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/zshdapi",method=RequestMethod.GET)
	public @ResponseBody String zshdapi(
			HttpServletResponse response,HttpServletRequest request
			) throws UnsupportedEncodingException{
		activeLog.info("掌上互动"+request.getQueryString());
		String appsecret="yqkj1234567890";
		String hashid = request.getParameter("hashid");
		String appid = request.getParameter("appid");
		String adid = request.getParameter("adid");
//		String adname = new String(request.getParameter("adname").getBytes("iso8859-1"),"utf-8");
		String adname = URLDecoder.decode(request.getParameter("adname"),"utf-8");
//		String adname = request.getParameter("adname");
		String userid = URLDecoder.decode(request.getParameter("userid"));
		String deviceid = request.getParameter("deviceid");
		String source = request.getParameter("source");
		String point = request.getParameter("point");
		String time = request.getParameter("time");
		String checksum = request.getParameter("checksum");
		StringBuffer str_temp=new StringBuffer();
		str_temp.append("?hashid=").append(hashid).append("&appid=").append(appid).append("&adid=").append(adid).append("&adname=").append(adname).append("&userid=").append(userid).append("&deviceid=").append(deviceid).append("&source=").append(source).append("&point=").append(point).append("&time=").append(time).append("&appsecret=").append(appsecret);
		String checksing=MD5.MD5Encode(str_temp.toString());
		activeLog.info("checksing:"+checksing);
		activeLog.info("checksum:"+checksum);
		if(checksing.equalsIgnoreCase(checksum)){
			String memberId="";
			try {
				String temp=ThreeDESede.decryptMode(userid);
				memberId=temp.split(":")[1];
			} catch (Exception e) {
				e.printStackTrace();
			}
			//调价回调记录
			this.allianceSercice.insertRecord(adid,format.format(new Date(Long.valueOf(time+"000"))),String.valueOf((Double.valueOf(point)/100)),String.valueOf(System.currentTimeMillis()+userid),"zshd",memberId,adname);
			//加钱
			this.allianceSercice.updateUser(memberId,String.valueOf((Double.valueOf(point)/100)));
			return "{\"status\":\"msg\":\"\"}";
		}else{
			activeLog.info("zshd callback sign failed!");
		}
		return "";
	}
	
	/**
	 * tengh 2016年7月19日 下午8:59:20
	 * @param response
	 * TODO 有米  ok
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/ymapi",method=RequestMethod.GET)
	public void ymapi(
			HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException{
		activeLog.info("有米"+request.getQueryString());
		String dev_server_secret="0a8630610ad331b7";
		String order = request.getParameter("order");
		String app = request.getParameter("app");
		String ad = request.getParameter("ad");
//		ad = new String(ad.getBytes("iso8859-1"),"utf-8");
		ad=URLDecoder.decode(ad);
		String adid = request.getParameter("adid");
		String user = request.getParameter("user");
		String device = request.getParameter("device");
		String chn = request.getParameter("chn");
		String price = request.getParameter("price");
		String points = request.getParameter("points");
		String time = request.getParameter("time");
		String storeid = request.getParameter("storeid");
		String sig = request.getParameter("sig");
		String sign = request.getParameter("sign");
		StringBuffer url_temp=new StringBuffer();
		url_temp.append("http://api.baby.51app.cn/baby/active/ymapi.do").append("?order=").append(order).append("&app=").append(app).append("&ad=").append(ad).append("&adid=").append(adid).append("&user=").append(user).append("&chn=").append(chn).append("&points=").append(points).append("&price=").append(price).append("&time=").append(time).append("&device=").append(device).append("&storeid=").append(storeid).append("&sig=").append(sig);
		boolean checkOrder=this.allianceSercice.checkOrder(URLDecoder.decode(order));
		if(!checkOrder){
			try {
				String url_signed=getUrlSignature(url_temp.toString(), dev_server_secret);
				if(checkUrlSignature(url_signed, dev_server_secret)){
					String memberId="";
					try {
						String temp=ThreeDESede.decryptMode(user);
						memberId=temp.split(":")[1];
					} catch (Exception e) {
					}
					//调价回调记录
					this.allianceSercice.insertRecord(adid,format.format(new Date(Long.valueOf(time+"000"))),String.valueOf(Double.valueOf(price)/100),URLDecoder.decode(order),"ym",memberId,ad);
					//加钱
					this.allianceSercice.updateUser(memberId,String.valueOf(Double.valueOf(price)/100));
					response.setStatus(200);
				}else{
					activeLog.info("ym callback sign failed!");
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setStatus(403);
	}
	
	
	public static void main(String[] args) {
//		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String time="1469016554";
//		Date date=new Date(Long.valueOf(time+"000"));
//		System.err.println(format.format(date));
		String a="80";
		System.err.println(Double.valueOf(a)/100);
	}
	
	
	/**
	 * 	
	 * tengh 2016年7月20日 下午5:49:29
	 * @param s
	 * @return
	 * TODO 万普的md5
	 */
	public static String MD5(String s) {
	        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
	        try {
	            byte[] btInput = s.getBytes();
	            // 获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // 使用指定的字节更新摘要
	            mdInst.update(btInput);
	            // 获得密文
	            byte[] md = mdInst.digest();
	            // 把密文转换成十六进制的字符串形式
	            int j = md.length;
	            char str[] = new char[j * 2];
	            int k = 0;
	            for (int i = 0; i < j; i++) {
	                byte byte0 = md[i];
	                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
	                str[k++] = hexDigits[byte0 & 0xf];
	            }
	            return new String(str);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
	private static String MD52(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("MD5(" + sourceStr + ",32) = " + result);
            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }


	    /**
	     * 有米的签名算法
	     */
	
	    /**
	     * 签名生成算法
	     *
	     * @param HashMap<String,String> params 请求参数集，所有参数必须已转换为字符串类型
	     * @param String                 dev_server_secret 开发者在有米后台设置的密钥
	     * @return String
	     * @throws IOException
	     */
	    public String getSignature(HashMap<String, String> params, String dev_server_secret) throws IOException {
	        // 先将参数以其参数名的字典序升序进行排序
	        Map<String, String> sortedParams = new TreeMap<String, String>(params);
	 
	        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
	        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
	        StringBuilder basestring = new StringBuilder();
	        for (Map.Entry<String, String> param : entrys) {
	            basestring.append(param.getKey()).append("=").append(param.getValue());
	        }
	        basestring.append(dev_server_secret);
	        //System.out.println(basestring.toString());
	        // 使用MD5对待签名串求签
	        byte[] bytes = null;
	        try {
	            MessageDigest md5 = MessageDigest.getInstance("MD5");
	            bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
	        } catch (GeneralSecurityException ex) {
	            throw new IOException(ex);
	        }
	        // 将MD5输出的二进制结果转换为小写的十六进制
	        StringBuilder sign = new StringBuilder();
	        for (int i = 0; i < bytes.length; i++) {
	            String hex = Integer.toHexString(bytes[i] & 0xFF);
	            if (hex.length() == 1) {
	                sign.append("0");
	            }
	            sign.append(hex);
	        }
	        return sign.toString();
	    }
	 
	    /**
	     * 对一条完整的未签名的URL做签名，并将签名结果添加到URL的末尾
	     * 
	     * @param String url 未做签名的完整URL
	     * @param String dev_server_secret 签名秘钥
	     * @return String
	     * @throws IOException, MalformedURLException
	     */
	    public String getUrlSignature(String url, String dev_server_secret) throws IOException, MalformedURLException {
	        try {
	            URL urlObj = new URL(url);
	            String query = urlObj.getQuery();
	            String[] params = query.split("&");
	            Map<String, String> map = new HashMap<String, String>();
	            for (String each : params) {
	                String name = each.split("=")[0];
	                String value;
	                try {
	                    value = URLDecoder.decode(each.split("=")[1], "UTF-8");
	                } catch (UnsupportedEncodingException e) {
	                    value = "";
	                }
	                map.put(name, value);
	            }
	            String signature = getSignature((HashMap<String, String>) map, dev_server_secret);
	            return url + "&sign=" + signature;
	        } catch (MalformedURLException e) {
	            throw e;
	        } catch (IOException e) {
	            throw e;
	        }
	    }
	 
	    /**
	     * 检查一条完整的包含签名参数的URL，其签名是否正确
	     * 
	     * @param String url 已经签名的完整URL
	     * @param String dev_server_secret 签名秘钥
	     * @return boolean
	     */
	    public boolean checkUrlSignature(String signedUrl, String dev_server_secret) {
	        String urlSign = "";
	        try {
	            URL urlObj = new URL(signedUrl);
	            String query = urlObj.getQuery();
	            String[] params = query.split("&");
	            Map<String, String> map = new HashMap<String, String>();
	            for (String each : params) {
	                String name = each.split("=")[0];
	                String value;
	                try {
	                    value = URLDecoder.decode(each.split("=")[1], "UTF-8");
	                } catch (UnsupportedEncodingException e) {
	                    value = "";
	                }
	                if ("sign".equals(name)) {
	                    urlSign = value;
	                } else {
	                    map.put(name, value);
	                }
	            }
	            if ("".equals(urlSign)) {
	                return false;
	            } else {
	                String signature = getSignature((HashMap<String, String>) map, dev_server_secret);
	                return urlSign.equals(signature);
	            }
	        } catch (MalformedURLException e) {
	            return false;
	        } catch (IOException e) {
	            return false;
	        }
	    }
}

