
package cn._51app.controller;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn._51app.dao.TaskDao;

/**
 * @author Administrator
 * 任务操作
 */
@Controller
@RequestMapping("/check")
public class checkController extends BaseController {
	
	@Autowired
	private TaskDao taskDao;
	
	@RequestMapping(value = "/idfa")
	public ResponseEntity<String> checkidfa(@RequestParam("idfa") String idfa,@RequestParam("appid") String appid) throws Exception {
		int resultCode=this.taskDao.checkIdfa(idfa,appid);
		HttpHeaders responseHeaders = new HttpHeaders();
		MediaType mediaType = new MediaType("text", "html",
				Charset.forName("UTF-8"));
		responseHeaders.setContentType(mediaType);
		String value = "{\"" + idfa + "\":"+resultCode+"}";
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(
				value, responseHeaders, HttpStatus.CREATED);
		return responseEntity;
	}
	
}
