
package cn._51app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn._51app.service.UserSercice;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.CreateBarcodeByZxing;
import cn._51app.utils.ThreeDESede;
import cn._51app.vo.ByDetail;

/**
 * @author Administrator 对用户处理操作
 */
@Controller
@RequestMapping("/u")
public class UserController extends BaseController {
	@Autowired
	private UserSercice userSercice;
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	@Autowired
	@Value("#{pValue['time.id']}")
	private String timeId; //任务需要打开的时间
	@Value("#{pValue['sys.hub']}")
	private String sysHub; //套壳开关
	@Value("#{pValue['baby.api']}")//接口地址
	private String babyApi;
	private ObjectMapper mapper=new ObjectMapper();
	
	/**
	 * tengh 2016年4月1日 下午2:26:56
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 默认登录
	 */
	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public ResponseEntity<String> login(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.login(result.get("idfa").toString(),(String)result.get("inviteId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年4月1日 下午2:26:56
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 个人资料
	 */
	@RequestMapping(value = "/information", method = { RequestMethod.POST })
	public ResponseEntity<String> information(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.information((Integer)result.get("memberId"),(String)result.get("returnflag"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年4月5日 下午4:53:35
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 修改个人信息
	 */
	@RequestMapping(value = "/editInfo", method = { RequestMethod.POST })
	public ResponseEntity<String> editInfo(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.editInfo((Integer)result.get("memberId"),(String)result.get("returnflag"),(String)result.get("name"),(Integer)result.get("sex"),(String)result.get("birthday"),(String)result.get("profession"),request);
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年2月22日 下午2:33:31
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 获取首页图标
	 */
	@RequestMapping(value = "/icons", method = { RequestMethod.GET })
	public ResponseEntity<String> icons() throws Exception {
		String json = userSercice.icons();
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2016年2月22日 下午1:44:27
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 登录获取余额和好友赚取金额
	 */
	@RequestMapping(value = "/freshMoney", method = { RequestMethod.POST })
	public ResponseEntity<String> freshMoney(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.freshMoney((Integer)result.get("memberId"),(String)result.get("returnflag"));
		return CommonUtil.judgeResult(json);
	}
	

	/**
	 * tengh 2015年11月18日 下午4:07:39
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 受邀查询
	 */
	@RequestMapping(value = "/invite", method = { RequestMethod.POST })
	public ResponseEntity<String> invite(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.invite((int) result.get("memberId"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2015年11月19日 下午4:35:07
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 提现记录
	 */
	@RequestMapping(value = "/deposit", method = { RequestMethod.POST })
	public ResponseEntity<String> deposit(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.deposit((int) result.get("memberId"), (int) result.get("page"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2015年11月20日 下午2:20:02
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 首页信息
	 */
	@RequestMapping(value = "/home", method = { RequestMethod.POST })
	public ResponseEntity<String> home(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.home((Integer) result.get("memberId"), (String) result.get("returnflag"),
				(String) result.get("appIds"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2015年11月20日 下午5:09:45
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 登录记录 and 更新钱数
	 */
	@RequestMapping(value = "/loginLog", method = { RequestMethod.POST })
	public ResponseEntity<String> loginLog(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		userSercice.loginLog((Integer) result.get("memberId"));
		return CommonUtil.judgeResult(null);
	}

	/**
	 * tengh 2015年11月23日 上午9:28:48
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 试玩记录
	 */
	@RequestMapping(value = "/tryLog", method = { RequestMethod.POST })
	public ResponseEntity<String> tryLog(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.tryLog((int) result.get("memberId"), (int) result.get("page"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2016年2月23日 下午6:01:03
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 任务详情
	 */
	@RequestMapping(value = "/taskInfo", method = { RequestMethod.POST })
	public ResponseEntity<String> taskInfo(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.taskInfo((int) result.get("memberId"),(int)result.get("inviteId"),(int) result.get("page"));
		return CommonUtil.judgeResult(json); 
	}
	

	/**
	 * tengh 2015年11月25日 下午2:01:32
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 *             TODO 我的邀请好友记录
	 */
	@RequestMapping(value = "/inviteRecord", method = { RequestMethod.POST })
	public ResponseEntity<String> inviteRecord(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.inviteRecord((int) result.get("memberId"),(int)result.get("page"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * ysx 2015年12月5日 上午10:55
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 应用详情
	 */
	 @RequestMapping(value = "/detail/{homeId}", method = { RequestMethod.GET })
	    public String detail(
	    		@PathVariable(value = "homeId") int homeId,
	    		HttpServletRequest request,
	    		Model model
	    		) {
		try {
			String returnflagParam=request.getQueryString();
			String returnflag=null;
			if(StringUtils.isNotBlank(returnflagParam)){
				returnflag=returnflagParam.substring(returnflagParam.indexOf("=")+1);
			}
			List<Map<String,Object>> lm =this.userSercice.getByHomeById(homeId);
			if(lm!=null){
				ByDetail d =new ByDetail();
				Map<String,Object> result =lm.get(0);
				String downloadUrl=(String)result.get("downloadUrl");
				String downloadParam=(String)result.get("downloadParam");
				if(StringUtils.isNotBlank(returnflag)  && StringUtils.isNotBlank(downloadUrl)){
					//查出id  对应的idfa
					String memberId=ThreeDESede.decryptMode(returnflag).split(":")[1];
					String idfa=this.userSercice.getIdfa(memberId);
					String ip=CommonUtil.getIp(request);
					downloadUrl+=idfa;
					if("ip".equalsIgnoreCase(downloadParam)){
						downloadUrl+="&ip"+ip;
					}else if("callback".equalsIgnoreCase(downloadParam)){
						downloadUrl+="&ip="+ip;
						downloadUrl+="&callback=";
						String callbackUrl="http://ios.api.51app.cn/ios_appActive.action?appid="+result.get("appId")+"&idfa="+idfa+"&rt=4";
						downloadUrl+=URLEncoder.encode(callbackUrl);
					}
				}
				d.setDownloadUrl(downloadUrl==null?"":downloadUrl);
				Object ob =result.get("name");
				if(ob!=null)d.setName(ob.toString());
				ob=result.get("key");
				if(ob!=null)d.setKey(ob.toString());
				ob=result.get("about");
				if(ob!=null)d.setAbout(ob.toString());
				ob=result.get("imgUrl");
				if(ob!=null)d.setImgUrl(url+ob.toString()+"@120,120.jpg");
				ob=result.get("location");
				if(ob!=null)d.setLocation(ob.toString());
				ob=result.get("isfirst");
				if(ob!=null)d.setIsfirst((int)ob);
				ob=result.get("time");
				if(ob!=null)d.setTime(Integer.valueOf(ob.toString())/60);
				ob=result.get("appId");
				if(ob!=null)d.setAppId(Integer.valueOf(ob.toString()));
				model.addAttribute("detail", d);
			}else{
				return "detail_err";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	        return "detail";
	    }
	 
	 /**
		 * ysx 2015年12月5日 上午10:55
		 * @param param
		 * @return
		 * @throws Exception
		 * TODO 应用详情
		 */
		 @RequestMapping(value = "/detail2/{homeId}", method = { RequestMethod.GET })
		    public String detail2(
		    		@PathVariable(value = "homeId") int homeId,
		    		HttpServletRequest request,
		    		Model model
		    		) {
			try {
				String returnflagParam=request.getQueryString();
				String returnflag=null;
				if(StringUtils.isNotBlank(returnflagParam)){
					returnflag=returnflagParam.substring(returnflagParam.indexOf("=")+1);
				}
				List<Map<String,Object>> lm =this.userSercice.getByHomeById(homeId);
				if(lm!=null){
					ByDetail d =new ByDetail();
					Map<String,Object> result =lm.get(0);
					String downloadUrl=(String)result.get("downloadUrl");
					String downloadParam=(String)result.get("downloadParam");
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
							String callbackUrl="http://ios.api.51app.cn/ios_appActive.action?appid="+result.get("appId")+"&idfa="+idfa+"&rt=4";
							downloadUrl+=URLEncoder.encode(callbackUrl);
						}
					}
					d.setDownloadUrl(downloadUrl==null?"":downloadUrl);
					Object ob =result.get("name");
					if(ob!=null)d.setName(ob.toString());
					ob=result.get("key");
					if(ob!=null)d.setKey(ob.toString());
					ob=result.get("about");
					if(ob!=null)d.setAbout(ob.toString());
					ob=result.get("imgUrl");
					if(ob!=null)d.setImgUrl(url+ob.toString()+"@120,120.jpg");
					ob=result.get("location");
					if(ob!=null)d.setLocation(ob.toString());
					ob=result.get("isfirst");
					if(ob!=null)d.setIsfirst((int)ob);
					ob=result.get("time");
					if(ob!=null)d.setTime(Integer.valueOf(ob.toString())/60);
					ob=result.get("appId");
					if(ob!=null)d.setAppId(Integer.valueOf(ob.toString()));
					model.addAttribute("detail", d);
				}else{
					return "detail_err";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		        return "detail2";
		    }
	 
	/**
	 * tengh 2016年2月26日 下午6:05:00
	 * @return
	 * TODO 邀请页面
	 */
	@RequestMapping(value = "/invite", method = { RequestMethod.GET })
    public String invite(@RequestParam(value="memberId")int memberId,Model model) {
		 try {
			 String urlPath=babyApi+"baby/u/share.do?id="+(memberId+10000);
			 String path="/data/resource/baby/barCode/"+(memberId+10000)+".png";
			 if(!new File(path).exists()){
				 CreateBarcodeByZxing.CreateBarcode(urlPath, path);
			 }
			 Map<String, String> map=this.userSercice.queryInviteScale(memberId);
			 map.put("ticket",url+"baby/barCode/"+(memberId+10000)+".png");
			 map.put("ShareMessage", "无需本金,动动手指,试玩应用还能赚零钱哦!点我下载应用还有现金奖励!");
			 map.put("ShareTitle", "下载应用,月赚6000不是梦!");
			 map.put("ShareUrl", babyApi+"baby/u/share.do?id="+(memberId+10000));
			 model.addAttribute("invite", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return "invite";
    }
	
	@RequestMapping(value = "/invite2", method = { RequestMethod.GET })
    public ResponseEntity<String> inviteRecord(@RequestParam(value="memberId")int memberId) throws Exception{
		Map<String, Object> map=new HashMap<>();
		 try {
			 String urlPath=babyApi+"baby/u/share.do?id="+(memberId+10000);
			 String path="/data/resource/baby/barCode/"+(memberId+10000)+".png";
			 if(!new File(path).exists()){
				 CreateBarcodeByZxing.CreateBarcode(urlPath, path);
			 }
//			 Map<String, String> map=this.userSercice.queryInviteScale(memberId);
			 map.put("ticket",url+"baby/barCode/"+(memberId+10000)+".png");
//			 map.put("moneyImg", url+"baby/invite/money");
//			 map.put("peopleImg", url+"baby/invite/people");
			 map.put("barcodeBackImg", url+"baby/invite/barcodeBack.png");
			 map.put("inviteMessge","1.邀请好友您将获得好友试玩收入的_50%_现金奖励,每个好友都能为您提供_10.0_元的收益。\n2.您所邀请的好友,好友再邀请TA的好友您将还能获得该好友的试玩收入_10%_奖励");
			 map.put("ShareMessage", "无需本金,动动手指,试玩应用还能赚零钱哦!点我下载应用还有现金奖励!");
			 map.put("ShareTitle", "下载应用,月赚6000不是梦!");
			 map.put("ShareUrl", babyApi+"baby/u/share.do?id="+(memberId+10000));
		} catch (Exception e) {
			e.printStackTrace();
		}
        return CommonUtil.judgeResult(mapper.writeValueAsString(map));
    }
	
	/**
	 * tengh 2016年3月1日 上午11:45:08
	 * @return
	 * TODO 关于我们
	 */
	@RequestMapping(value = "/about", method = { RequestMethod.GET })
    public String about() {
        return "about";
    }
	
	/**
	 * tengh 2016年3月1日 上午11:45:08
	 * @return
	 * TODO 关于我们
	 */
	@RequestMapping(value = "/directopen", method = { RequestMethod.GET })
    public String directopen(@RequestParam(value="token",required=false)String token,Model model) {
		String schemeUrl="babyforring://";
		if(StringUtils.isNotBlank(token)){
			schemeUrl=this.userSercice.getSchemeUrl(token);
		}
		model.addAttribute("schemeUrl", schemeUrl);
        return "directopen";
    }
	
	/**
	 * tengh 2015年12月5日 下午3:03:07
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 申请提现
	 */
	@RequestMapping(value = "/getAccount", method = { RequestMethod.POST })
	public ResponseEntity<String> getAccount(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.getAccount((String) result.get("returnflag"),(Integer) result.get("memberId"), (String) result.get("money"),
				(int) result.get("type"),(String)result.get("zfbName"),(String)result.get("zfbNo"));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2016年2月23日 下午2:47:26
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 检测账号是否可以一键提现
	 */
	@RequestMapping(value = "/checkAccount", method = { RequestMethod.POST })
	public ResponseEntity<String> checkAccount(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.checkAccount((String) result.get("returnflag"),(Integer) result.get("memberId"),(String)result.get("appleid"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2015年12月27日 下午5:59:14
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 接任务
	 */
	@RequestMapping(value = "/getTask", method = { RequestMethod.POST })
	public ResponseEntity<String> getTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.getTask((int) result.get("memberId"), (String) result.get("returnflag"),
				(int) result.get("taskId"),(String)result.get("idfa"),CommonUtil.getIp(request));
		return CommonUtil.judgeResult(json);
	}

	/**
	 * tengh 2015年12月27日 下午5:59:27
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 得到取款范围
	 */
	@RequestMapping(value = "/getMoneyRange", method = { RequestMethod.POST })
	public ResponseEntity<String> getMoneyRange(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.getMoneyRange((String) result.get("money"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年1月7日 下午2:59:11
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 设置用户提现时的默认选项
	 */
	@RequestMapping(value = "/setDefault", method = { RequestMethod.POST })
	public ResponseEntity<String> setDefault(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.setDefault((Integer) result.get("memberId"),(Integer)result.get("type"),(String)result.get("money"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年1月13日 上午11:04:31
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 核查是否有任务
	 */
	@RequestMapping(value = "/checkTask", method = { RequestMethod.POST })
	public ResponseEntity<String> checkTask(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.checkTask((Integer) result.get("memberId"),(Integer)result.get("homeId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年1月15日 下午6:20:12
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 保存deviceToken
	 */
	@RequestMapping(value = "/saveDevice", method = { RequestMethod.POST })
	public ResponseEntity<String> saveDevice(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.saveDevice((String) result.get("idfa"),(String)result.get("deviceToken"),(String)result.get("version"),(String)result.get("appleid"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年3月5日 下午3:19:00
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 是否是appStore下载
	 */
	@RequestMapping(value = "/downTask", method = { RequestMethod.POST })
	public ResponseEntity<String> downTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.downTask((String) result.get("returnflag"),(Integer)result.get("memberId"),CommonUtil.getIp(request),(String)result.get("isAppstore"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年6月2日 下午5:45:28
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 检测appStoreId账号是否重复下载
	 */
	@RequestMapping(value = "/checkAppStore", method = { RequestMethod.POST })
	public ResponseEntity<String> checkAppStore(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.checkAppStore((String) result.get("returnflag"),(Integer)result.get("memberId"),(boolean)result.get("repeatBuy"),(Integer)result.get("homeId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年1月26日 下午2:53:18
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO  客户端检测出应用完成
	 */
	@RequestMapping(value = "/finishTask", method = { RequestMethod.POST })
	public ResponseEntity<String> finishTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.finishTask((String) result.get("returnflag"),(Integer)result.get("homeId"),(Integer)result.get("memberId"),(String)result.get("appflag"),(String)CommonUtil.getIp(request));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年6月29日 下午4:05:45
	 * @param appleid
	 * @param idfa
	 * @return
	 * @throws Exception
	 * TODO 直接完成任务
	 */
	@RequestMapping(value = "/directFinish", method = { RequestMethod.GET })
	public ResponseEntity<String> directFinish(@RequestParam("appleid") String appleid,@RequestParam(value="idfa")String idfa) throws Exception {
		String json = userSercice.directFinish(appleid,idfa);
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年6月13日 下午4:30:52
	 * @param param
	 * @param request
	 * @return
	 * @throws Exception
	 * TODO 提交任务
	 */
	@RequestMapping(value = "/submitTask", method = { RequestMethod.POST })
	public ResponseEntity<String> submitTask(@RequestParam("param") String param,HttpServletRequest request) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.submitTask((String) result.get("returnflag"),(Integer)result.get("memberId"),(Integer)result.get("homeId"),(String)result.get("appflag"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年1月26日 下午2:59:14
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 放弃任务
	 */
	@RequestMapping(value = "/giveupTask", method = { RequestMethod.POST })
	public ResponseEntity<String> giveupTask(@RequestParam String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.giveupTask((String) result.get("returnflag"),(Integer) result.get("memberId"),(Integer)result.get("homeId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年5月4日 下午8:13:04
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 是否直接打开应用
	 */
	@RequestMapping(value = "/isActive", method = { RequestMethod.POST })
	public ResponseEntity<String> isActive(@RequestParam String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.isActive((String) result.get("idfa"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年2月23日 下午7:57:58
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 时间到任务过期
	 */
	@RequestMapping(value = "/taskOverDue", method = { RequestMethod.POST })
	public ResponseEntity<String> taskOverDue(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.taskOverDue((String) result.get("returnflag"),(Integer) result.get("memberId"),(Integer)result.get("homeId"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年3月4日 上午10:00:45
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 检测更新
	 */
	@RequestMapping(value = "/updateVersion", method = { RequestMethod.POST })
	public ResponseEntity<String> updateVersion(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json = userSercice.updateVersion((String)result.get("version"),(String)result.get("appleid"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年3月17日 上午10:59:42
	 * @param version
	 * @return
	 * @throws Exception
	 * TODO 套壳开关
	 */
	@RequestMapping(value = "/getHub/{version}", method = { RequestMethod.GET })
	public ResponseEntity<String> getHub(@PathVariable(value="version") String version,@RequestParam(value="appleid",required=false) String appleid) throws Exception {
		Map<String, Object> map=new HashMap<String, Object>();
		String message="";
		boolean isHub=false;
		boolean switch_=false;
		//获取套壳信息
		Map<String, Object> resMap=this.userSercice.getHub(appleid,version);
		if(resMap!=null){
			String isDialog=(String)resMap.get("isDialog");
			if("1".equalsIgnoreCase(isDialog)){
				switch_=true;
			}
		}
		map.put("switch", switch_);
		map.put("isHub", isHub);
		message="动动手指，试玩应用，月赚6000不是梦!_激活应用";
//		map.put("url", babyApi+"baby/u/directopen.do?token="+appleid);
		map.put("url", babyApi+"baby/u/open.do?appleid="+appleid);
		map.put("message", message);
		return CommonUtil.judgeResult(new ObjectMapper().writeValueAsString(map));
	}
	
	/**
	 * tengh 2016年3月22日 下午2:31:03
	 * @param request
	 * @param response
	 * TODO 描述文件回回调
	 */
	@RequestMapping(value = "/receive", method = { RequestMethod.POST })
	public void receive(HttpServletRequest request,HttpServletResponse response) {
		String memberId=request.getParameter("memberId");
		String returnflag=request.getParameter("returnflag");
		String appleid=request.getParameter("appleid");
		Map<String, Object> tempMap=userSercice.getHub(appleid);
		response.setContentType("text/html;charset=UTF-8");
		boolean flag=false;
		String message="";
		try {
			request.setCharacterEncoding("UTF-8");
			InputStream stream = request.getInputStream();
		    byte[] buffer = new byte[512];
		    StringBuilder builder = new StringBuilder();
		    while (stream.read(buffer) != -1) {
		        builder.append(new String(buffer));
		    }
		    String udid=builder.substring(builder.indexOf("UDID")+20,builder.indexOf("VERSION")-16);
		    int result=-1; 
		    if(StringUtils.isNotBlank(memberId) && StringUtils.isNotBlank(returnflag)){
		    	System.err.println("memberId:"+memberId+",udid:"+udid);
		    	//绑定udid给账号
		    	Map<String, Object> resMap=this.userSercice.boundUdid(memberId,returnflag,udid);
		    	result=(Integer)resMap.get("resFlag");
		    	message=(String)resMap.get("message");
		    }
		    response.setStatus(301);
		    String locationUlr="zhuanlingqianapp://";
		    if(tempMap!=null){
		    	locationUlr=(String)tempMap.get("schemeUrl");
		    }
		    
		    Map<String, Object> temp=new HashMap<>();
	    	if(result>0){
	    		flag=true;
	    	}
	    	temp.put("memberId", memberId);
		    temp.put("flag", flag);
		    temp.put("message", message);
    		locationUlr=locationUlr+"?token="+ThreeDESede.encryptMode(new ObjectMapper().writeValueAsString(temp));
    		response.setHeader("Location",locationUlr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tengh 2016年3月23日 上午9:56:02
	 * @param request
	 * @param response
	 * TODO 下载描述文件
	 * @throws Exception 
	 */
	@RequestMapping(value = "/configfile")
	public void configfile(HttpServletRequest request,HttpServletResponse response) throws Exception {
		PrintWriter write=null;
		try {
			write=response.getWriter();
			Document listDoc = DocumentHelper.createDocument();     
			listDoc.setXMLEncoding("utf-8");  
			DocumentFactory documentFactory=new DocumentFactory();  
			DocumentType documentType=documentFactory.createDocType("plist", "-//Apple//DTD PLIST 1.0//EN", "http://www.apple.com/DTDs/PropertyList-1.0.dtd");
			String token=CommonUtil.getQueryStringValue(request.getQueryString(), "token");
			String appleid=CommonUtil.getQueryStringValue(request.getQueryString(), "appleid");
			Map<String, Object> tempMap=userSercice.getHub(appleid);
			String PayloadDisplayName="最美铃声";
			if(tempMap!=null){
				PayloadDisplayName=(String)tempMap.get("name");
			}
			listDoc.setDocType(documentType);  
			Element pB = listDoc.addElement("plist");  
			pB.addAttribute("version", "1.0.0");  
			Element di = pB.addElement("dict");  
			di.addElement("key").addText("PayloadContent");
			Element dict=di.addElement("dict");
			dict.addElement("key").addText("URL");
			Map<String, Object> result = ThreeDESede.DecodeToMap(token);
//			String url="http://www.91zhuanlingqian.com/baby/u/receive.do";
			String url=babyApi+"baby/u/receive.do";
			Integer memberId=null;
			String returnflag=null;
			if(result!=null && result.size()>0){
				memberId=(Integer)result.get("memberId");
				returnflag=(String)result.get("returnflag");
			}
			if(memberId!=null && StringUtils.isNotBlank(returnflag)){
				url=url+"?memberId="+memberId+"&returnflag="+returnflag;
			}
			if(StringUtils.isNotBlank(appleid)){
				url=url+"&appleid="+appleid;
			}
			dict.addElement("string").addText(url);
			dict.addElement("key").addText("DeviceAttributes");
			Element array= dict.addElement("array");
			array.addElement("string").addText("UDID");
			array.addElement("string").addText("IMEI");
			array.addElement("string").addText("ICCID");
			array.addElement("string").addText("VERSION");
			array.addElement("string").addText("PRODUCT");
			di.addElement("key").addText("PayloadOrganization");
			di.addElement("string").addText("最美铃声");
			di.addElement("key").addText("PayloadDisplayName");
			di.addElement("string").addText(PayloadDisplayName);
			di.addElement("key").addText("PayloadVersion");
			di.addElement("integer").addText("1");
			di.addElement("key").addText("PayloadUUID");
			di.addElement("string").addText("GC4DM0D2-E475-A1D2-9KJ8-IOPF8PU45A603");
			di.addElement("key").addText("PayloadIdentifier");
			di.addElement("string").addText("com.51app.51baby");
			di.addElement("key").addText("PayloadDescription");
			di.addElement("string").addText("本文件仅用来获取设备ID");
			di.addElement("key").addText("PayloadType");
			di.addElement("string").addText("Profile Service");
//			write.append(listDoc.asXML());
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			StringWriter temp = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(temp,format);
			try {
				xmlWriter.write(listDoc);
				xmlWriter.close();
				write.append(temp.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("application/x-apple-aspen-config");
	}
	
	/**
	 * tengh 2016年3月24日 下午5:07:42
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 查询手机号是否存在
	 */
	@RequestMapping(value = "/checkMobile", method = { RequestMethod.POST })
	public ResponseEntity<String> checkMobile(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json=this.userSercice.checkMobile((String)result.get("mobile"),(Integer)result.get("memberId"),(String)result.get("returnflag"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年5月25日 下午1:52:05
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 绑定支付宝
	 */
	@RequestMapping(value = "/checkZfb", method = { RequestMethod.POST })
	public ResponseEntity<String> checkZfb(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json=this.userSercice.checkZfb((String)result.get("zfbName"),(String)result.get("zfbNo"),(Integer)result.get("memberId"),(String)result.get("returnflag"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年4月7日 上午10:00:01
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 绑定公众号验证码
	 */
	@RequestMapping(value = "/boundCode", method = { RequestMethod.POST })
	public void boundCode(@RequestParam("param") String param,@RequestParam("code")String code,@RequestParam(value="appleid",required=false)String appleid,HttpServletResponse response) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json="";
		json=this.userSercice.boundCode((Integer)result.get("memberId"),(String)result.get("returnflag"),code);
		String schemeUrl=this.userSercice.getSchemeUrl(appleid);
		PrintWriter writer = response.getWriter();
		response.setContentType("application/json;charset=utf-8");
		writer.write("{\"message\":\""+json+"\",\"schemeUrl\":"+schemeUrl+"}");
		writer.close();
	}
	
	/**
	 * tengh 2016年4月7日 下午1:46:59
	 * @return
	 * @throws Exception
	 * TODO 跳转关注页面
	 */
	@RequestMapping(value = "/attention")
	public String attention() throws Exception {
		return "attention";
	}
	
	/**
	 * tengh 2016年4月8日 下午4:54:29
	 * @return
	 * @throws Exception
	 * TODO 跳转分享页面
	 */
	@RequestMapping(value = "/share")
	public String share(Model model) throws Exception {
		Map<String, Object> result=this.userSercice.getHubBydefault();
		if(result!=null){
			model.addAttribute("result", result);
		}
		return "earnMoney";
	}
	
	/**
	 * tengh 2016年6月21日 下午2:33:21
	 * @return
	 * @throws Exception
	 * TODO 在safari里面打开激活应用
	 */
	@RequestMapping(value = "/open")
	public String open(Model model,@RequestParam(value="appleid") String appleid) throws Exception {
		Map<String, Object> map=this.userSercice.getHub(appleid);
		String schemeUrl=(String)map.get("schemeUrl");
		model.addAttribute("schemeUrl", schemeUrl);
		return "open";
	}
	
	/**
	 * tengh 2016年4月8日 下午4:54:29
	 * @return
	 * @throws Exception
	 * TODO 常见问题页面
	 */
	@RequestMapping(value = "/question")
	public String question() throws Exception {
		return "question";
	}
	
	/**
	 * tengh 2016年6月24日 上午9:26:05
	 * @return
	 * @throws Exception
	 * TODO 常见问题
	 */
	@RequestMapping(value = "/tip")
	public String tip() throws Exception {
		return "tip";
	}
	
	/**
	 * tengh 2016年3月24日 下午5:47:21
	 * @param param
	 * @return
	 * @throws Exception
	 * TODO 绑定手机号
	 */
	@RequestMapping(value = "/boundMobile", method = { RequestMethod.POST })
	public ResponseEntity<String> boundMobile(@RequestParam("param") String param) throws Exception {
		Map<String, Object> result = ThreeDESede.DecodeToMap(param);
		String json=this.userSercice.boundMobile((String)result.get("mobile"),(Integer)result.get("memberId"),(String)result.get("returnflag"));
		return CommonUtil.judgeResult(json);
	}
	
	/**
	 * tengh 2016年7月13日 上午11:33:40
	 * @param appleid
	 * @return
	 * @throws Exception
	 * TODO 减少任务数量
	 */
	@RequestMapping(value = "/desHomeNum", method = { RequestMethod.GET })
	public ResponseEntity<String> desHomeNum(@RequestParam("appleid") String appleid,@RequestParam("idfa") String idfa) throws Exception {
		String json=this.userSercice.desHomeNum(appleid,idfa);
		return CommonUtil.judgeResult(json);
	}
	
}
