package cn._51app.utils;

import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.stream.JsonReader;

import cn._51app.BaByConstant;

/**
 * @author Mr.yu
 * 全局工具类
 */
public class CommonUtil {
	
	public static String subStr(String str){
		try {
			if(StringUtils.isNotBlank(str)){
				return str.substring(0, str.length()-1);
			}
		} catch (Exception e) {
			
		}
		return ""; 
	}
	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str){
		if(str == null || "".equals(str)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断集合是否为空
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection){
		if(collection == null) return true;
		return collection.isEmpty();
	}
	
	
	/**
	 * String convert int
	 * @param value 
	 * @return
	 */
	public static int String2Int(String value){
		if(isBlank(value)){
			return 0;
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * 判断 listMap is empty
	 * @param beans
	 * @return
	 */
	public static boolean listMapIsEmpty(List<Map<String, Object>> beans){
		if(beans == null || beans.isEmpty() || beans.get(0).isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * 生成json
	 * 
	 * @param msgCode
	 * @param body
	 * @return
	 */
	public static ResponseEntity<String> toJson(int msgCode, String body) {
		HttpHeaders responseHeaders = new HttpHeaders();
		MediaType mediaType = new MediaType("text", "html",
				Charset.forName("UTF-8"));
		responseHeaders.setContentType(mediaType);
		String value = "{\"msgCode\":" + msgCode + ",\"body\":" + body + "}";
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(
				value, responseHeaders, HttpStatus.CREATED);
		return responseEntity;
	}
	
	
	/**
	 * 对结果进行处理
	 * @param json
	 * @return
	 */
	public static ResponseEntity<String> judgeResult(String json){
		if(json == null || json.isEmpty()){
			return toJson(BaByConstant.MSG_SUCCESS, "\"\"");
		}else{
			return toJson(BaByConstant.MSG_SUCCESS, json);
		}
	}
	
	/**
	 * 判断对象是否为NULL 或者为空
	 * @param object
	 * @return
	 */
	public static boolean isValid(Object object){
		if(object == null){
			return false;
		}
		if(object instanceof List){
			return !((List<?>) object).isEmpty();
		}
		
		if(object instanceof Map){
			return !((Map<?, ?>) object).isEmpty();
		}
		if(object instanceof String){
			return !((String) object).isEmpty();
		}
		
		return true;
	}
	
	
	
	/**
	 * tengh 下午3:07:49
	 * TODO 解析获取到的下拉框结果集
	 * @param json
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String, Object>> ParseSuggestJson(String json) throws Exception {
		List<Map<String, Object>> result=new ArrayList<Map<String,Object>>();
		JsonReader reader=new JsonReader(new StringReader(json));
		reader.beginObject();
			while (reader.hasNext()) {
				String temp=reader.nextName();
				if("result".equals(temp)){
					reader.beginObject();
					while (reader.hasNext()) {
						String temp2=reader.nextName();
						if("items".equals(temp2)){
							reader.beginArray();
							while (reader.hasNext()) {
								Map<String, Object> map=new HashMap<String, Object>();
								reader.beginObject();
								while (reader.hasNext()) {
									String temp3=reader.nextName();
									if("id".equals(temp3)){
										map.put("id", reader.nextString());
									}else if("title".equals(temp3)){
										map.put("title", reader.nextString());
									}else if("url".equals(temp3)){
										map.put("url", reader.nextString());
									}else{
										reader.skipValue();
									}
								}
								reader.endObject();
								result.add(map);
							}
							reader.endArray();
						}else{
							reader.skipValue();
						}
					}
					reader.endObject();
				}else{
					reader.skipValue();
				}
		}
		reader.endObject();
		reader.close();
		return result;
	}

	/**
	 * tengh 2015年9月16日 下午5:39:57
	 * @return 随机产生订单号（S+12位数）
	 * TODO
	 */
	public static String createOrderNo(String flag,int length) {
	    Random random = new Random();
	    char[] digits = new char[length];
	    digits[0] = (char) (random.nextInt(9) + '1');
	    for (int i = 1; i < length; i++) {
	        digits[i] = (char) (random.nextInt(10) + '0');
	    }
	    String currentTime=System.currentTimeMillis()+"";
	    return flag.concat(Long.parseLong(currentTime.substring(6))+"").concat(new String(digits));
	}


	public static BigInteger createSceneId() {
		String currentTime=System.currentTimeMillis()+"";
		return (new BigInteger(currentTime.concat((createOrderNo("a", 12).substring(1)))));
	}
	
	public static String post(String url, String json)
    {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        try
        {
            StringEntity s = new StringEntity(json);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            post.setEntity(s);
 
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == org.apache.commons.httpclient.HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                return EntityUtils.toString(entity, "utf-8");
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
		return null;
    }
	
	public static String getIp(HttpServletRequest request){
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	public static String getQueryStringValue(String queryStr,String key){
		try {
			String[] queryStrs=queryStr.split("&");
			for (int i = 0; i < queryStrs.length; i++) {
				String[] keyvalues=queryStrs[i].split("=");
				if(keyvalues[0].equalsIgnoreCase(key)){
					return keyvalues[1];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCookie(HttpServletRequest request,String key){
		try {
			Cookie[] cookies= request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				if(key.equals(cookies[i].getName())){
					return cookies[i].getValue();
				}
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.err.println(getQueryStringValue("sdd=213&sdfasd=11", "sdfasd"));
	}
}
