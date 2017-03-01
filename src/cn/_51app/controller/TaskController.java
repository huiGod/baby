
package cn._51app.controller;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn._51app.service.TaskSercice;
import cn._51app.service.UserSercice;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.JpushUtil;
import cn._51app.utils.ThreeDESede;

/**
 * @author Administrator
 * 任务操作
 */
@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {
	@Autowired
	private TaskSercice taskSercice;
	@Autowired
	private UserSercice userSercice;
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	@Value("#{pValue['baby.api']}")//接口地址
	private String babyApi;
	private DecimalFormat df= new DecimalFormat("######0.00");
	private SimpleDateFormat datedf=new SimpleDateFormat("yyyy-mm-dd");
	
	
	@RequestMapping(value = "/whyInvite", method = { RequestMethod.GET })
	public String whyInvite() throws Exception {
		return "whyInvite";
	}
	
	@RequestMapping(value = "/firstQA", method = { RequestMethod.GET })
	public String firstQA() throws Exception {
		return "firstQA";
	}
	
	/**
	 * tengh 2016年9月12日 上午11:48:35
	 * @return
	 * @throws Exception
	 * TODO 获取首页图标
	 */
	@RequestMapping(value = "/icons", method = { RequestMethod.POST })
	public ResponseEntity<String> icons(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.icons((Integer) result.get("memberId"), (String) result.get("appIds"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月8日 下午5:34:48
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 新手任务
	 */
	@RequestMapping(value = "/firstHome", method = { RequestMethod.POST })
	public ResponseEntity<String> firstHome(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.firstHome((Integer) result.get("memberId"), (String) result.get("returnflag"),
				(String) result.get("appIds"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2015年11月20日 下午2:20:02
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 快速模块
	 */
	@RequestMapping(value = "/home", method = { RequestMethod.POST })
	public ResponseEntity<String> home(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.home((Integer) result.get("memberId"), (String) result.get("returnflag"),
				(String) result.get("appIds"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年8月31日 下午1:57:48
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 后续模块
	 */
	@RequestMapping(value = "/followHome", method = { RequestMethod.POST })
	public ResponseEntity<String> followHome(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.followHome((Integer) result.get("memberId"), (String) result.get("returnflag"),
				(String) result.get("appIds"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月1日 上午10:45:26
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 * TODO 接前置任务
	 */
	@RequestMapping(value = "/getTask", method = { RequestMethod.POST })
	public ResponseEntity<String> getTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.getTask((int) result.get("memberId"), (String) result.get("returnflag"),
				(int) result.get("taskId"),(String)result.get("idfa"),CommonUtil.getIp(request));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年8月31日 下午3:56:41
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 核查是否有前置任务正在进行
	 */
	@RequestMapping(value = "/checkTask", method = { RequestMethod.POST })
	public ResponseEntity<String> checkTask(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.checkTask((Integer) result.get("memberId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月1日 下午3:14:11
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 * TODO 是否是appStore下载   appstore下载完之后才会返回该结果
	 */
	@RequestMapping(value = "/downTask", method = { RequestMethod.POST })
	public ResponseEntity<String> downTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.downTask((String) result.get("returnflag"),(Integer)result.get("memberId"),CommonUtil.getIp(request),(String)result.get("isAppstore"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月1日 下午6:00:09
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 检测appStore账号是否是第一次下载应用   下载过程手机端会返回结果
	 */
	@RequestMapping(value = "/checkAppStore", method = { RequestMethod.POST })
	public ResponseEntity<String> checkAppStore(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.checkAppStore((String) result.get("returnflag"),(Integer)result.get("memberId"),(boolean)result.get("repeatBuy"),(Integer)result.get("homeId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月1日 下午6:57:21
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 * TODO 主动提交审核按钮看任务是否完成  (只有试玩3分钟才有这个)
	 */
	@RequestMapping(value = "/finishTask", method = { RequestMethod.POST })
	public ResponseEntity<String> finishTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.finishTask((String) result.get("returnflag"),(Integer)result.get("homeId"),(Integer)result.get("memberId"),(String)CommonUtil.getIp(request));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月6日 下午6:40:51
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception 
	 * TODO 后续任务次日成功打开
	 */
	@RequestMapping(value = "/openFollowTask", method = { RequestMethod.POST })
	public ResponseEntity<String> openFollowTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.openFollowTask((String) result.get("returnflag"),(Integer)result.get("homeId"),(Integer)result.get("memberId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年9月6日 下午8:35:45
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 * TODO 后续任务上传截图
	 */
	@RequestMapping(value = "/uploadFollowTask", method = { RequestMethod.POST })
	public ResponseEntity<String> uploadFollowTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.uploadFollowTask((String) result.get("returnflag"),(Integer)result.get("homeId"),(Integer)result.get("memberId"),request);
		return CommonUtil.judgeResult(json);
	}
	
	
	/**
	 * ysx 2015年12月5日 上午10:55
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 应用详情
	 */
	@RequestMapping(value = "/taskInfo/{homeId}", method = { RequestMethod.GET })
    public String taskInfo(
    		@PathVariable(value="homeId") int homeId,
    		HttpServletRequest request,
    		Model model
    		) {
	try {
		String returnflagParam=request.getQueryString();
		String returnflag=null;
		if(StringUtils.isNotBlank(returnflagParam)){
			returnflag=returnflagParam.substring(returnflagParam.indexOf("=")+1);
		}
		Map<String,Object> homeInfo =this.taskSercice.getByHomeById(homeId);
		if(homeInfo!=null){
			String downloadUrl=(String)homeInfo.get("downloadUrl");
			String downloadParam=(String)homeInfo.get("downloadParam");
			Integer appId=(Integer)homeInfo.get("appId");
			double money=(Double)homeInfo.get("money");
			double openMoney=(Double)homeInfo.get("openMoney");
			homeInfo.put("money", df.format(money));
			homeInfo.put("openMoney", df.format(openMoney));
			String imgUrl=(String)homeInfo.get("imgUrl");
			homeInfo.put("imgUrl", url+imgUrl+"@120,120.jpg");
			String temInfo=(String)homeInfo.get("infolist");
			if(StringUtils.isNotBlank(temInfo)){
				String[] infolist=temInfo.split("\\|");
				homeInfo.put("infolist", infolist);
			}
			Integer type=(Integer)homeInfo.get("type");
			String message=getRightMessage(type);
			if(type==5){
				message="需要截图";
			}
			homeInfo.put("message", message);
			if(StringUtils.isNotBlank(returnflag) && StringUtils.isNotBlank(downloadUrl)){
				//查出id  对应的idfa
				String memberId=ThreeDESede.decryptMode(returnflag).split(":")[1];
				String idfa=this.userSercice.getIdfa(memberId);
				String ip=CommonUtil.getIp(request);
				if("no".equals(downloadParam)){
					
				}else{
					downloadUrl+=idfa;
				}
				if("ip".equalsIgnoreCase(downloadParam)){
					downloadUrl+="&ip"+ip;
				}else if("callback".equalsIgnoreCase(downloadParam)){
					downloadUrl+="&ip="+ip;
					downloadUrl+="&callback=";
					String callbackUrl="http://ios.api.51app.cn/ios_appActive.action?appid="+appId+"&idfa="+idfa+"&rt=4";
					downloadUrl+=URLEncoder.encode(callbackUrl);
				}
				homeInfo.put("downloadUrl", downloadUrl);
			}
		}else{
			return "detail_err";
		}
		model.addAttribute("detail", homeInfo);
	} catch (Exception e) {
		e.printStackTrace();
	}
        return "taskInfo";
    }
	
	/**
	 * tengh 2016年9月9日 上午11:42:43
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 查询个人邀请信息
	 */
	@RequestMapping(value = "/inviteInfo", method = { RequestMethod.POST })
    public ResponseEntity<String> inviteInfo(@RequestParam("param") String param) throws Exception{
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = taskSercice.inviteInfo((String) result.get("returnflag"),(Integer)result.get("memberId"));
		return CommonUtil.judgeResult(json);
    }
	
	/**
	 * tengh 2016年9月13日 下午4:58:10
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 失败时候领取优惠券 一天一个
	 */
	@RequestMapping(value = "/getCoupon", method = { RequestMethod.GET })
    public ResponseEntity<String> getCoupon(@RequestParam("memberId") Integer memberId,@RequestParam("homeId") String homeId,Model model) throws Exception{
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			data= taskSercice.getCoupon(memberId,homeId);
			if(StringUtils.isBlank(data)){
				data="fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
    }
	
	/**
	 * tengh 2016年9月6日 下午2:28:46
	 * @param homeId
	 * @param request
	 * @param model
	 * @return
	 * TODO 后续打开页面
	 */
	@RequestMapping(value="followInfo/{homeId}")
	public String followInfo(@PathVariable(value="homeId") int homeId,HttpServletRequest request,Model model) {
		try {
			String returnflagParam=request.getQueryString();
			String returnflag=null;
			String memberId="";
			if(StringUtils.isNotBlank(returnflagParam)){
				returnflag=returnflagParam.substring(returnflagParam.indexOf("=")+1);
				if(StringUtils.isNotBlank(returnflag)){
					//查出id  对应的idfa
					memberId=ThreeDESede.decryptMode(returnflag).split(":")[1];
				}
			}
			if(StringUtils.isBlank(memberId)){
				return "detail_err"; 
			}
			Map<String,Object> homeInfo =this.taskSercice.getFollowInfo(homeId,memberId);
			if(homeInfo!=null){
				double money=(Double)homeInfo.get("money");
				double openMoney=(Double)homeInfo.get("openMoney");
				Integer appId=(Integer)homeInfo.get("appId");
				homeInfo.put("downLoadUrl", "https://itunes.apple.com/cn/app/id"+appId+"?mt=8");
				homeInfo.put("money", df.format(money));
				homeInfo.put("openMoney", df.format(openMoney));
				String imgUrl=(String)homeInfo.get("imgUrl");
				String previewUrl=(String)homeInfo.get("previewUrl");
				homeInfo.put("previewUrl",(previewUrl==null)?"": url+previewUrl);
				homeInfo.put("imgUrl", url+imgUrl+"@120,120.jpg");
				Integer type=(Integer)homeInfo.get("type");
				if(type==5 || type==4){
					homeInfo.put("overtime", "");
				}else{
					homeInfo.put("overtime", datedf.format(homeInfo.get("overtime")));
				}
				String temInfo=(String)homeInfo.get("infolist");
				if(StringUtils.isNotBlank(temInfo)){
					String[] infolist=temInfo.split("\\|");
					homeInfo.put("infolist", infolist);
				}
				String message=getRightMessage(type);
				if(type==5){
					message="需要截图";
				}
				homeInfo.put("message", message);
			}else{
				return "detail_err";
			}
			model.addAttribute("detail", homeInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return "followInfo";
	}
	
	public String getLeftMessage(Integer type){
		String leftMessage="";
		if(type==null){
			
		}else if(type==1){
			leftMessage="试玩3分钟";
		}else if(type==2){
			leftMessage="需审核";//回调任务
		}else if(type==3){
			leftMessage="有后续";
		}else if(type==4){
			leftMessage="有后续";
		}else if(type==5){
			leftMessage="高额任务";
		}
		return leftMessage;
	}
	
	public String getRightMessage(Integer type){
		String rightMessage="";
		if(type==null){
			
		}else	if(type==1){
			rightMessage="需首次安装";
		}else if(type==2){
			rightMessage="奖励有延时";
		}else if(type==3){
			rightMessage="次日打开";
		}else if(type==4){
			rightMessage="需要截图";
		}else if(type==5){
			rightMessage="需要截图";
		}
		return rightMessage;
	}
	
	/**
	 * 完成新人任务
	 * @author zhanglz
	 */
	@RequestMapping(value ="/submitFirst")
	public ResponseEntity<String> submitFirst(@RequestParam(value="flag") String flag){
		String data =null;
		String msg=null;
		int code =SUCESS;
		try {
			data= this.taskSercice.submitFirst(flag);
			if(StringUtils.isBlank(data)){
				data="fail";
			}
		} catch (Exception e) {
			e.printStackTrace();
			code =SERVER_ERR;
		}
		return super.resultInfo(data, code, msg);
	}
	
	/**
	 * 完成新人任务
	 * @author zhanglz
	 */
	@RequestMapping(value ="/checkIos10")
	public String checkIos10(){
		return "checkIos10";
	}
	
	/**
	 * tengh 2016年10月14日 下午7:03:52
	 * @param flag
	 * @return
	 * TODO 开关打开关闭
	 */
//	@RequestMapping(value ="/switchOpen")
//	public ResponseEntity<String> switchOpen(@RequestParam(value="flag") String flag){
//		String data =null;
//		String msg=null;
//		int code =SUCESS;
//		try {
//			data= this.taskSercice.switchOpen(flag);
//			if(StringUtils.isBlank(data)){
//				data="fail";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			code =SERVER_ERR;
//		}
//		return super.resultInfo(data, code, msg);
//	}
	
	/**
	 * tengh 2016年10月18日 下午2:40:05
	 * @param memberId
	 * @param content
	 * @throws Exception
	 * TODO 消息推送
	 */
	@RequestMapping(value = "/pushMessage", method = { RequestMethod.GET })
	public void pushMessage(@RequestParam("memberId") String memberId,@RequestParam("content") String content) throws Exception {
		JpushUtil.pushMessage(memberId, content);
	}
}
