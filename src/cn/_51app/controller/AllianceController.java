
package cn._51app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn._51app.service.AllianceSercice;
import cn._51app.utils.CommonUtil;

/**
 * @author Administrator 
 * 联盟任务列表
 */
@Controller
@RequestMapping("/alliance")
public class AllianceController{
	@Autowired
	private AllianceSercice allianceSercice;
	
	/**
	 * tengh 2016年7月23日 下午4:36:29
	 * @return
	 * @throws Exception
	 * TODO 联盟列表
	 */
	@RequestMapping("/list")
	public ResponseEntity<String> allianceList() throws Exception{
		String json=this.allianceSercice.allianceList();
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年7月23日 下午4:36:36
	 * @return
	 * @throws Exception
	 * TODO 任务攻略
	 */
	@RequestMapping("/strategy")
	public String strategy() throws Exception{
		return "strategy";
	}
}

