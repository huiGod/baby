package cn._51app.controller;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import cn._51app.BaByConstant;
import cn._51app.utils.CommonUtil;

/**
 * @author Administrator 处理异常
 */
public abstract class BaseController {

	protected final int SUCESS = 200; // 操作成功
	protected final int FAIL = 300; // 操作失败
	protected final int EMPTY = 400; // 空数据
	protected final int SERVER_ERR = 500;// 服务器错误
	protected final int NO_LOGIN = 401; // 未登录
	private final String DEV = "1"; // 0 正式 1
																		// 开发

	@ExceptionHandler
	public ResponseEntity<String> exception(HttpServletRequest request, Exception e) {
		if (e instanceof IllegalArgumentException || e instanceof NumberFormatException
				|| e instanceof TypeMismatchException || e instanceof NullPointerException) {
			return CommonUtil.toJson(BaByConstant.MSG_PARAM, "\"\"");
		} else if (e instanceof JsonParseException || e instanceof JsonMappingException || e instanceof IOException) {
			return CommonUtil.toJson(BaByConstant.MSG_KEYVALUE, "\"\"");
		} else if (e instanceof RuntimeException) {
			if (StringUtils.isBlank(e.getMessage())) {
				return CommonUtil.toJson(BaByConstant.MSG_EXCEPTION, "\"\"");
			} else {
				return CommonUtil.toJson(BaByConstant.MSG_SUCCESS, e.getMessage());
			}
		} else {
			return CommonUtil.toJson(BaByConstant.MSG_EXCEPTION, "\"\"");
		}
	}
	
	/**>>Faster
	  * 
	  * @param data 数据
	  * @param code 错误码
	  * @param msg 错误信息
	  * @return json数据格式
	  */
	 protected ResponseEntity<String> resultInfo(String data, int code, String msg) {
		 	//设置httpHeaders请求头信息
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Access-Control-Allow-Origin", "*");//ajax跨域
			//设置MediaType格式，请求头配置
			MediaType mediaType = new MediaType("text", "html",Charset.forName("UTF-8"));
			//设置content-type请求头
			responseHeaders.setContentType(mediaType);
			
			//使用stringbuffer设置字符窜
			StringBuffer responseJson =new StringBuffer();
			responseJson.append("{\"code\":"+code);
			if(DEV.equals("1")){
				if(msg==null){
					switch(code){
						case 200:msg="操作成功";
						break;
						case 300:msg="操作失败";
						break;
						case 400:msg="无数据";
						break;
						case 401:msg="未登录";
						break;
						case 500:msg="服务器错误";
						break;
						default:msg="未定义";
					}
				}
				responseJson.append(",\"message\":\""+msg+ "\"");
			}
			//有数据返回，无数据就返回'}'
			if(data!=null){
				responseJson.append(",\"data\":"+data+ "}");
			}else{
				responseJson.append("}");
			}
			//1-任何数据，2-请求头对象。3-httpStatus
			ResponseEntity<String> responseEntity = new ResponseEntity<String>(responseJson.toString(), responseHeaders, HttpStatus.CREATED);
			return responseEntity;
		}
}
