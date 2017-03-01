package cn._51app.service.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import cn._51app.BaByConstant;
import cn._51app.dao.TaskDao;
import cn._51app.dao.UserDao;
import cn._51app.service.GoodsSercice;
import cn._51app.service.TaskSercice;
import cn._51app.utils.BASE64;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.CreateBarcodeByZxing;
import cn._51app.utils.HttpUtils;
import cn._51app.utils.JpushUtil;
import cn._51app.utils.ThreeDESede;
import net.sf.json.JSONObject;

/**
 * @author Administrator 用户管理
 */
@Controller
public class TaskServiceImpl implements TaskSercice {

	@Autowired
	private UserDao userDao;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private GoodsSercice goodsSercice;
	@Value("#{pValue['userImg.path']}")
	private String userImgPath;// 账号头像路径
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	@Value("#{pValue['downloadImg.url']}")
	private String downloadUrl;// 图片上传路径
	@Value("#{pValue['download.wait.time']}") //接任务后的时间
	private String downloadWaitTime;
	@Value("#{pValue['baby.api']}")//接口地址
	private String babyApi;
	private ObjectMapper objectMapper = new ObjectMapper();
	private DecimalFormat df= new DecimalFormat("######0.00");  

	/**
	 * tengh 2016年9月8日 下午5:36:24
	 * @param memberId
	 * @param returnflag
	 * @param appIds
	 * @return
	 * TODO 新手任务都是快速任务type=1
	 */
	@Override
	public String firstHome(Integer memberId, String returnflag, String appIds) throws Exception{
		Integer firstTop=(Integer)this.taskDao.queryFirstTop(memberId);
		List<Map<String, Object>> inviteResult=new ArrayList<>();
		if(firstTop==1){
			inviteResult = taskDao.queryFirstInfo();
			for (int i = 0; i < inviteResult.size(); i++) {
				String key=(String)inviteResult.get(i).get("key");
				if("firstTask".equals(key)){
					String imgUrl=(String)inviteResult.get(i).get("imgUrl");
					inviteResult.get(i).put("imgUrl", imgUrl==null?"":url+imgUrl+"@120,120.png");
					String content=(String)inviteResult.get(i).get("content");
					inviteResult.get(i).put("content", content==null?"":content);
					String title=(String)inviteResult.get(i).get("title");
					inviteResult.get(i).put("title", title==null?"":title);
					String url_=(String)inviteResult.get(i).get("url");
					inviteResult.get(i).put("url", url_==null?"":babyApi+url_+"?flag="+BASE64.encode(returnflag));
					double money=(double)inviteResult.get(i).get("money");
					inviteResult.get(i).put("money", "+_"+df.format(money)+"_元");
					inviteResult.get(i).put("moneyUrl", url+"baby/invite/firstTopMoney");
				}
			}
		}
		List<Map<String, Object>> rankList = taskDao.queryFirstBetter(appIds,memberId);
		if(rankList!=null && rankList.size()>0){
			for (int i = 0;i < rankList.size(); i++) {
				Integer type=(Integer)rankList.get(i).get("type");
				String imgUrl=(String)rankList.get(i).get("imgUrl");
				rankList.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
				String info=(String)rankList.get(i).get("info");
				rankList.get(i).put("info", info==null ?"":info);
				double money=(double)rankList.get(i).get("money");
				rankList.get(i).put("money", df.format(money));
				rankList.get(i).put("leftMessage", getLeftMessage(type));
				rankList.get(i).put("rightMessage", getRightMessage(type));
				rankList.get(i).put("status", 0);
			}
		}
		//加上 未完成的任务
		List<Map<String, Object>> tasked=taskDao.queryFirstTasked(memberId);
		if(tasked!=null  && tasked.size()>0){
			for (int i = 0; i < tasked.size(); i++) {
				Integer type=(Integer)tasked.get(i).get("type");
				String imgUrl=(String)tasked.get(i).get("imgUrl");
				tasked.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
				String info=(String)tasked.get(i).get("info");
				tasked.get(i).put("info", info==null ?"":info);
				tasked.get(i).put("leftMessage", getLeftMessage(type));
				tasked.get(i).put("rightMessage", getRightMessage(type));
			} 
		}
		//加上 已完成的任务
		List<Map<String, Object>> finishedTask=taskDao.queryFirstfinishedTask(memberId);
		if(finishedTask!=null && finishedTask.size()>0){
			for (int i = 0; i < finishedTask.size(); i++) {
				Integer type=(Integer)finishedTask.get(i).get("type");
				String imgUrl=(String)finishedTask.get(i).get("imgUrl");
				finishedTask.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
				String info=(String)finishedTask.get(i).get("info");
				finishedTask.get(i).put("info", info==null ?"":info);
				finishedTask.get(i).put("leftMessage", getLeftMessage(type));
				finishedTask.get(i).put("rightMessage", getRightMessage(type));
			} 
		}
		Map<String, Object> result=new HashMap<>();
		result.put("inviteInfos", inviteResult);
		result.put("downloadWaitTime", CommonUtil.String2Int(downloadWaitTime));
		tasked.addAll(rankList);
		result.put("taskList", tasked);
		result.put("finishedTaskList", finishedTask);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年8月30日 下午3:46:05
	 * @param memberId
	 * @param returnflag
	 * @param appIds
	 * @return
	 * @throws Exception
	 * TODO   status=0可接  status=-1 数量0  status=1进行中 status=5已完成 status=6 审核中 status=7审核失败 status=8有后续
	 */
	@Override
	public String home(Integer memberId, String returnflag, String appIds) throws Exception {
		if(memberId==null || StringUtils.isBlank(returnflag)){
			throw new RuntimeException();
		}
		try {
			//查询邀请的信息
			List<Map<String, Object>> invite = userDao.queryInvite();
			List<Map<String, Object>> inviteResult=new ArrayList<>();
			Map<String, Object> tempMap=new HashMap<>();
			if(invite!=null){
				for (int i = 0; i < invite.size(); i++) {
					String key=(String)invite.get(i).get("key");
					if("invite".equals(key)){
						String imgUrl=(String)invite.get(i).get("imgUrl");
						tempMap.put("imgUrl", imgUrl==null?"":url+imgUrl+"@120,120.png");
						String content=(String)invite.get(i).get("content");
						tempMap.put("content", content==null?"":content);
						String title=(String)invite.get(i).get("title");
						tempMap.put("title", title==null?"":title);
						double money=(double)invite.get(i).get("money");
						tempMap.put("money", df.format(money));
						tempMap.put("moneyUrl", url+"baby/invite/inviteMoney");
						String url=(String)invite.get(i).get("url");
						tempMap.put("url", url==null?"":url);
					}
				}
				inviteResult.add(tempMap);
			}
			//高额直接截图任务
			List<Map<String, Object>> dirImgList = taskDao.queryDirePic(appIds,memberId);
			if(dirImgList!=null && dirImgList.size()>0){
				for (int i = 0;i < dirImgList.size(); i++) {
					Integer type=(Integer)dirImgList.get(i).get("type");
					String imgUrl=(String)dirImgList.get(i).get("imgUrl");
					dirImgList.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)dirImgList.get(i).get("info");
					dirImgList.get(i).put("info", info==null ?"":info);
					double money=(double)dirImgList.get(i).get("money");
					dirImgList.get(i).put("money", df.format(money));
					dirImgList.get(i).put("leftMessage", getLeftMessage(type));
					dirImgList.get(i).put("rightMessage", getRightMessage(type));
					dirImgList.get(i).put("status", 0);
					dirImgList.get(i).put("time", 0);
				}
			}
			//可接快速任务
			List<Map<String, Object>> rankList = taskDao.queryBetter(appIds,memberId);
			if(rankList!=null && rankList.size()>0){
				for (int i = 0;i < rankList.size(); i++) {
					Integer type=(Integer)rankList.get(i).get("type");
					String imgUrl=(String)rankList.get(i).get("imgUrl");
					rankList.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)rankList.get(i).get("info");
					rankList.get(i).put("info", info==null ?"":info);
					double money=(double)rankList.get(i).get("money");
					rankList.get(i).put("money", df.format(money));
					rankList.get(i).put("leftMessage", getLeftMessage(type));
					rankList.get(i).put("rightMessage", getRightMessage(type));
					rankList.get(i).put("status", 0);
				}
			}
			//查询任务数为0的任务
			List<Map<String, Object>> taskNumIs0=this.taskDao.queryTankNumIs0();
			if(taskNumIs0!=null && taskNumIs0.size()>0){
				for (int i = 0;i < taskNumIs0.size(); i++) {
					Integer type=(Integer)taskNumIs0.get(i).get("type");
					String imgUrl=(String)taskNumIs0.get(i).get("imgUrl");
					taskNumIs0.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)taskNumIs0.get(i).get("info");
					taskNumIs0.get(i).put("info", info==null ?"":info);
					taskNumIs0.get(i).put("leftMessage", getLeftMessage(type));
					taskNumIs0.get(i).put("rightMessage", getRightMessage(type));
					taskNumIs0.get(i).put("status", -1);
				}
			}
			//加上 未完成的任务
			List<Map<String, Object>> tasked=taskDao.queryTasked(memberId);
			if(tasked!=null  && tasked.size()>0){
				for (int i = 0; i < tasked.size(); i++) {
					Integer type=(Integer)tasked.get(i).get("type");
					String imgUrl=(String)tasked.get(i).get("imgUrl");
					tasked.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)tasked.get(i).get("info");
					tasked.get(i).put("info", info==null ?"":info);
					tasked.get(i).put("leftMessage", getLeftMessage(type));
					tasked.get(i).put("rightMessage", getRightMessage(type));
				} 
			}
			
			
			//加上 已完成的任务
			List<Map<String, Object>> finishedTask=taskDao.queryfinishedTask(memberId);
			if(finishedTask!=null && finishedTask.size()>0){
				for (int i = 0; i < finishedTask.size(); i++) {
					Integer type=(Integer)finishedTask.get(i).get("type");
					String imgUrl=(String)finishedTask.get(i).get("imgUrl");
					finishedTask.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)finishedTask.get(i).get("info");
					finishedTask.get(i).put("info", info==null ?"":info);
					finishedTask.get(i).put("leftMessage", getLeftMessage(type));
					finishedTask.get(i).put("rightMessage", getRightMessage(type));
				} 
			}
			//游客不需要验证  可以看到所有试玩应用
			Map<String, Object> map = new HashMap<String, Object>();
			//即将开始的应用
			List<Map<String, Object>> preTaskList=this.taskDao.queryLatestTask();
			double totalMoney=0;
			String tempTime="";
			long temInteval=0;
			if(preTaskList!=null && preTaskList.size()>0){
				tempTime=(String)preTaskList.get(0).get("date");
				temInteval=(Long)preTaskList.get(0).get("inteval");
				for (int i = 0; i < preTaskList.size(); i++) {
					String[] dateTemp=((String)preTaskList.get(i).get("date")).split("_");
					long inteval=(Long)preTaskList.get(i).get("inteval");
					if(0==inteval){
						preTaskList.get(i).put("date", "今日"+dateTemp[1]);
					}else if(1==inteval){
						preTaskList.get(i).put("date", "明日"+dateTemp[1]);
					}else if(2==inteval){
						preTaskList.get(i).put("date", "后天"+dateTemp[1]);
					}else{
						preTaskList.get(i).put("date", dateTemp[0]+"日"+dateTemp[1]);
					}
					double tempMoney=(double)preTaskList.get(i).get("money");
					preTaskList.get(i).put("money",df.format(tempMoney));
					totalMoney+=tempMoney;
					Integer type=(Integer)preTaskList.get(i).get("type");
					String imgUrl=(String)preTaskList.get(i).get("imgUrl");
					preTaskList.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
					String info=(String)preTaskList.get(i).get("info");
					preTaskList.get(i).put("info", info==null ?"":info);
					preTaskList.get(i).put("leftMessage", getLeftMessage(type));
					preTaskList.get(i).put("rightMessage", getRightMessage(type));
					preTaskList.get(i).put("key", "????");
				}
			}else{
				preTaskList=new ArrayList<>();
			}
			if(StringUtils.isBlank(tempTime)){
				map.put("startTime","一大波任务即将到来，敬请期待");
			}else{
				if(0==temInteval){
					map.put("startTime","一大波任务将于今日_"+tempTime.split("_")[1]+"_开始,总共_"+df.format(totalMoney)+"_元");
				}else if(1==temInteval){
					map.put("startTime","一大波任务将于明日_"+tempTime.split("_")[1]+"_开始,总共_"+df.format(totalMoney)+"_元");
				}else if(2==temInteval){
					map.put("startTime","一大波任务将于后天_"+tempTime.split("_")[1]+"_开始,总共_"+df.format(totalMoney)+"_元");
				}else{
					map.put("startTime","一大波任务将于"+tempTime.split("_")[0]+"日_"+tempTime.split("_")[1]+"_开始,总共_"+df.format(totalMoney)+"_元");
				}
			}
//			map.put("highMoneyList", dirImgList);
			map.put("inviteInfos", inviteResult);
			tasked.addAll(rankList);
			tasked.addAll(dirImgList);
			map.put("taskList", tasked);
			map.put("notaskList", taskNumIs0);
			map.put("downloadWaitTime", CommonUtil.String2Int(downloadWaitTime));
			map.put("preTaskList", preTaskList);
			map.put("finishedTaskList", finishedTask);
			return objectMapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(); 
		}
	}
	
	@Override
	public String followHome(Integer memberId, String returnflag, String appIds) throws Exception {
		if(memberId==null || StringUtils.isBlank(returnflag)){
			throw new RuntimeException();
		}
		//加上 未完成的任务
		List<Map<String, Object>> tasked=taskDao.queryTaskedF(memberId);
		if(tasked!=null  && tasked.size()>0){
			for (int i = 0; i < tasked.size(); i++) {
				Integer type=(Integer)tasked.get(i).get("type");
				String imgUrl=(String)tasked.get(i).get("imgUrl");
				tasked.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl+"@120,120.jpg");
				String info=(String)tasked.get(i).get("info");
				tasked.get(i).put("info", info==null ?"":info);
				tasked.get(i).put("leftMessage", getRightMessage(type));
			} 
		}else{
			return null;
		}
		return objectMapper.writeValueAsString(tasked);
	}
	
	@Override
	public String checkTask(Integer memberId) throws Exception{
		Map<String, Object> map=new HashMap<>();
		String message="";
		boolean flag=true;
		boolean hasTasking=this.taskDao.checkHasTasking(memberId);
		if(hasTasking){
			message="领取新任务,将会自动放弃进行中的其他任务,确定要更换任务吗？";
			flag=false;
		}
		map.put("message", message);
		map.put("flag", flag);
		return objectMapper.writeValueAsString(map);
	}
	
	@Override
	public String getTask(int memberId, String returnflag, int homeId, String idfa, String ip) throws Exception{
		String createTime="";
		String message="";
		Map<String, Object> result=new HashMap<>();
		boolean flag=true;
		try {
			//排重
			Map<String, Object> idfaUrls=this.userDao.queryIdfaUrl(homeId);
			String repeatUrl=(String)idfaUrls.get("repeatUrl");
			String repeatParam=(String)idfaUrls.get("repeatParam");
			String repeatResult=(String)idfaUrls.get("repeatResult");
			Integer appId=(Integer)idfaUrls.get("appId");
			Integer type=(Integer)idfaUrls.get("type");
			if(StringUtils.isNotBlank(idfa)  && StringUtils.isNotBlank(repeatResult)){
				try {
					String resultResp=HttpUtils.get(repeatUrl+idfa, null, 5, "utf-8");
					if(StringUtils.isBlank(resultResp)){
						flag=false;
						message="领取任务失败,请联系客服~";
					}else{
						//字符串结果直接比较
						if("string".equalsIgnoreCase(repeatParam)){
							if(!repeatResult.equals(resultResp)){
								flag=false;
								message="首次下载才可完成任务哦～";
							}
						}else{//json格式结果比较
							Map<String, Object> map=objectMapper.readValue(resultResp, HashMap.class);
							if("idfa".equalsIgnoreCase(repeatParam)){
								repeatParam=idfa;
							}
							String temResult=String.valueOf(map.get(repeatParam));
							if(!repeatResult.equalsIgnoreCase(temResult)){
								flag=false;
								message="首次下载才可完成任务哦～";
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					flag=false;
					message="领取任务失败,请联系客服~";
				}
			}
		if(flag){//排重过了  (或者不需要排重)
			HttpUtils.get("http://api.app.51app.cn/a/addApplicationNum/"+appId+".do?idfa="+idfa+"&ip="+ip+"&channel=10", null,5,"utf-8");
			this.taskDao.giveUpAllTask(memberId,homeId);
			//验证memberid  和 idfa是否对应
			boolean checkMember=this.userDao.checkMemberAndIdfa(memberId,idfa);
			int flagStatus=0;
			if(checkMember){
				flagStatus=1;
			}
			createTime=taskDao.getTask(memberId,homeId,flagStatus,type);
		}
		} catch (Exception e) {
			flag=false;
			createTime="";
			message="任务已被抢完~";
		}
		result.put("flag", flag);
		result.put("createTime", createTime);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
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
			
		}else if(type==1){
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
	
	@Override
	public String downTask(String returnflag, Integer memberId, String ip, String isAppstore) throws Exception{
		Map<String, Object> result=new HashMap<>();
		String message="";
		boolean msg=false;
		//消息推送
//		String deviceToken=this.userDao.queryDeviceToken(memberId);
		Integer flag=0;
		Map<String, Object> ttmap=this.userDao.queryTaskFlagStatus(memberId);
		if(ttmap!=null){
			flag=(Integer)ttmap.get("flag");
			Integer homeId=(Integer)ttmap.get("homeId");
			Integer isappleId=(Integer)ttmap.get("isappleId");
			if(1==isappleId){//需要限制 是否从appStore下载
				if(!"1".equals(isAppstore)){
					giveupTask(returnflag, memberId, homeId);
					message="通过非苹果官方下载_我知道了";
					msg=false;
					flag=0;
				}
			}
			if(flag==1){
				message="开始计时!";
				msg=true;
				this.taskDao.downTask(memberId,1,ip);
			}else{
				msg=false;
				if(StringUtils.isBlank(message)){
					message="任务异常,请重新尝试_我知道了";
				}
			}
//			MutliThread m1=new MutliThread(deviceToken,message.split("_")[0]);
//			m1.start();
			JpushUtil.pushMessage(memberId+"", "开始计时!");
		}else{
			msg=false;
			message="任务异常，请重新尝试!_我知道了";
		}
		result.put("msg", msg);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	@Override
	public String giveupTask(String returnflag,Integer memberId, Integer homeId) {
		try {
			if(homeId!=null && homeId!=0){
				this.userDao.giveUpTask(memberId,homeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return BaByConstant.MSG_FALSE;
		}
		return BaByConstant.MSG_TRUE;
	}
	
	@Override
	public String checkAppStore(String returnflag, Integer memberId, boolean isPurchasedReDownload, Integer homeId) throws Exception{
		boolean flag=true;
		Map<String, Object> result=new HashMap<>();
		String message="";
		boolean onlyAppleId=this.taskDao.checkOnlyAppleId(memberId);
		if(onlyAppleId){//应用需要检测账号是否首次
			if(isPurchasedReDownload){
				flag=false; //账号之前下载过
				//放弃任务
				giveupTask(returnflag, memberId, homeId);
			}
		}
		if(!flag){
			message="检测到您的账号已经下载过此应用,任务失败 _好的";
			JpushUtil.pushMessage(memberId+"", message);
//			String deviceToken=this.userDao.queryDeviceToken(memberId);
//			MutliThread mutliThread=new MutliThread(deviceToken, message.split("_")[0]);
//			mutliThread.start();
		}
		result.put("message", message);
		result.put("flag", flag);
		return objectMapper.writeValueAsString(result);
	}
	
	@Override
	public String finishTask(String returnflag, Integer homeId, Integer memberId,  String ip) throws Exception{
		Map<String, Object> result=new HashMap<>();
		boolean msg=false;
		String message="提交失败，请检查任务是否按要求完成~";
		try {
			//查出之前的任务有效状态
			Integer flag=0;
			Map<String, Object> ttmap=this.userDao.queryTaskFlagStatus(memberId);
			if(ttmap!=null){  //快速积分墙任务才会进入
				Integer type=(Integer)ttmap.get("type");
				if(type==1){
					flag=(Integer)ttmap.get("flag");
					String activeUrl=(String)ttmap.get("activeUrl");
					String idfa=(String)ttmap.get("idfa");
					Integer isActive=(Integer)ttmap.get("isActive");
					Integer status=(Integer)ttmap.get("status");
					Integer firstTask=(Integer)ttmap.get("firstTask");
					if(status==5){   //已经成功的给成功提醒   只能是3分钟快速任务
						msg=true;
						message="祝贺您,已完成此任务~";
					}else{
						if(StringUtils.isNotBlank(activeUrl)){  //小鱼的一种对接方式
							String temResult=HttpUtils.get(activeUrl+idfa+"&ip="+ip, null, 5, "utf-8");
							try {
								Map<String, Object> res=JSONObject.fromObject(temResult.trim());
								if("1".equals(String.valueOf(res.get("status")))){
									//激活成功  改变cpa状态为1
									this.userDao.updateStatus(memberId,homeId);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						boolean checkFlag=checkReturnFlag(memberId, returnflag);
						if(!checkFlag){
							flag=0;
						}
						//查询对接状态 (如果需要对接)
						if(isActive==1){
							boolean checkActive=this.userDao.checkActive(memberId,homeId);
							if(!checkActive){
								flag=0;
							}
						}
						//检测时间是否满足完成条件  (本地数据库时间有误差  测试不检测)
						boolean isCanFinish=this.userDao.isCanFinish(homeId,memberId);
//						boolean isCanFinish=true;
						if(isCanFinish){
							//完成任务 (flag标识任务是否有效)
							int flagCheck=this.taskDao.finishTask(homeId,memberId,flag,firstTask);
							//消息推送
							Map<String, Object> messageInfo=this.userDao.queryMessageInfo(homeId,memberId);
							if(messageInfo!=null){
								if(flagCheck==1){
									try {
//										NotnoopAPNS.sendMessage(new String[]{(String)messageInfo.get("deviceToken")}, "您试玩的<<"+(String)messageInfo.get("title")+">>成功,获得奖励"+(double)messageInfo.get("money")+"元");
									} catch (Exception e) {
									}
									JpushUtil.pushMessage(memberId+"", "您试玩的<<"+(String)messageInfo.get("title")+">>成功,获得奖励"+(double)messageInfo.get("money")+"元");
									msg=true;
									message="";
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("msg", msg);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年2月16日 上午11:18:23
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 验证账号的微信标识
	 */
	private boolean checkReturnFlag(Integer memberId,String returnflag){
		try {
			if(memberId==null || memberId==0 || StringUtils.isBlank(returnflag)){
				return false;
			}
			String temp=ThreeDESede.decryptMode(returnflag);
			if(temp.split(":")[1].equals(memberId.toString())){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public Map<String, Object> getByHomeById(int homeId) {
		return this.taskDao.getByHomeById(homeId);
	}
	
	@Override
	public Map<String, Object> getFollowInfo(int homeId, String memberId) {
		return this.taskDao.getFollowInfo(homeId,memberId);
	}
	
	@Override
	public String openFollowTask(String returnflag, Integer homeId, Integer memberId) throws Exception{
		boolean userCheck=checkReturnFlag(memberId, returnflag);
		Map<String, Object> map=new HashMap<>();
		boolean msg=false;
		String message="次日打开应用时间未到，请稍等~";
		if(userCheck){
			int result=this.taskDao.openFollowTask(memberId,homeId);
			if(result>0){
				msg= true;
				JpushUtil.pushMessage(memberId+"", "次日打开应用成功！");
				message="次日打开应用成功,奖励已到账";
			}
		}
		map.put("msg", msg);
		map.put("message", message);
		return objectMapper.writeValueAsString(map);
	}
	
	@Override
	public String uploadFollowTask(String returnflag, Integer homeId, Integer memberId, HttpServletRequest request) throws Exception {
		CommonsMultipartResolver multipartResolver =new CommonsMultipartResolver(request.getSession().getServletContext());
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest multiRequest =(MultipartHttpServletRequest)request;
			Iterator<String> iter=multiRequest.getFileNames();
			int index=0;
			String path=System.currentTimeMillis()+"";
			String uploadPath=downloadUrl+"baby/taskUpload/";
			String resultUploadPath="";
			while(iter.hasNext()){
				index++;
				MultipartFile file=multiRequest.getFile(iter.next());
				resultUploadPath=uploadPath+path+"_"+index+".jpg";
				File uploadFile=new File(resultUploadPath);
				if(!uploadFile.exists()){
					uploadFile.mkdirs();
				}
				file.transferTo(uploadFile);
			}
			int result=this.taskDao.uploadFollowTask(homeId,memberId,"baby/taskUpload/"+path+"_"+index);
//			int result=0;
			if(result>0){
				return BaByConstant.MSG_TRUE;
			}
		}
		return BaByConstant.MSG_FALSE;
	}
	
	
	@Override
	public String inviteInfo(String returnflag, Integer memberId) throws Exception{
		 String urlPath=babyApi+"baby/u/share.do?id="+(memberId+10000);
		 String path=downloadUrl+"baby/barCode/"+(memberId+10000)+".png";
		 if(!new File(path).exists()){
			 CreateBarcodeByZxing.CreateBarcode(urlPath, path);
		 }
		 //查询邀请的信息
		 Map<String, Object> map=this.taskDao.queryInviteScale(memberId);
		 map.put("directMoney", 1);
		 double oneInviteRate=(Double)map.get("oneInviteRate");
		 double twoInviteRate=(Double)map.get("twoInviteRate");
		 long oneInviteNum=((Long)map.get("oneInviteNum"))==null?0:((Long)map.get("oneInviteNum"));
		 long twoInviteNum=((Long)map.get("twoInviteNum"))==null?0:((Long)map.get("twoInviteNum"));
		 double allInviteMoney=(Double)map.get("allInviteMoney");
		 double allMoney=(Double)map.get("allMoney");
		 map.put("oneInviteRate", oneInviteRate*100+"%");
		 map.put("twoInviteRate", twoInviteRate*100+"%");
		 map.put("oneInviteNum", oneInviteNum);
		 map.put("twoInviteNum", twoInviteNum);
		 map.put("allInviteMoney", df.format(allInviteMoney));
		 map.put("allMoney", df.format(allMoney));
		 map.put("ticket",url+"baby/barCode/"+(memberId+10000)+".png");
		 map.put("barcodeBackImg", url+"baby/invite/share");
		 map.put("inviteUrl", babyApi+"baby/task/whyInvite.do");
		 String[] list=new String[3];
		 list[0]=url+"baby/invite/pic_1";
		 list[1]=url+"baby/invite/pic_2";
		 list[2]=url+"baby/invite/pic_3";
		 map.put("backImg", list);
		 map.put("ShareMessage", "无需本金,动动手指,试玩应用还能赚零钱哦!点我下载应用还有现金奖励!");
		 map.put("ShareTitle", "下载应用,月赚6000不是梦!");
		 map.put("ShareUrl", babyApi+"baby/u/share.do?id="+(memberId+10000));
		 return objectMapper.writeValueAsString(map);
	}
	
	@Override
	public String icons(Integer memberId,String appIds) throws Exception{
//		long checkFirstTasked=this.taskDao.checkFirstTasked(memberId);
//		long firstTaskCount=this.taskDao.getFirstTaskCount(memberId);
		this.goodsSercice.insertDiyUser(goodsSercice.getIdfa(memberId+""), "com.91luo.BestRing");
		Map<String, Object> resultMap=this.taskDao.getFirstStatus(memberId);
		Integer firstStatus=(Integer)resultMap.get("first_status");
		Integer firstTop=(Integer)resultMap.get("first_top");
		boolean checkFirstStatus=false;
		if(firstStatus==1 || firstTop==1){//有新人任务
			//新人可接任务
			List<Map<String, Object>> rankList = taskDao.queryFirstBetter(appIds,memberId);
			//加上 未完成的任务
			List<Map<String, Object>> tasked=taskDao.queryFirstTasked(memberId);
			if(rankList.size()<=0 && tasked.size()<=0 && firstTop==0){
				//新人任务做完
				this.taskDao.updateFirstStatus(memberId);
				checkFirstStatus=true;
			}
		}else{//没有新人任务，以后都不会有
			checkFirstStatus=true;
		}
		
		List<Map<String, Object>> list=this.taskDao.queryNewIcons();
		for (int i = 0; i < list.size(); i++) {
			String imgUrl=(String)list.get(i).get("imgUrl");
			list.get(i).put("imgUrl", url+imgUrl);
			String key=(String)list.get(i).get("key");
			if(checkFirstStatus){//做完了新手任务
				if("first".equals(key)){
					list.remove(i);
					i--;
				}
			}else{
				if("today".equals(key)){
					list.get(i).put("isRecommend", 0);
				}
				if("privilege".equals(key)){
					list.remove(i);
					i--;
				}
			}
		}
		return objectMapper.writeValueAsString(list);
	}
	
//	@Override
//	public boolean getCoupon(String returnflag, Integer memberId) {
//		boolean checkUser=this.checkReturnFlag(memberId, returnflag);
//		if(checkUser){
//			if(this.taskDao.getCoupon(memberId)){
//				return true;
//			}
//		}
//		return false;
//	}
	@Override
	public String getCoupon(Integer memberId, String homeId) throws Exception{
		Map<String, Object> result=new HashMap<>();
		boolean flag=false;
		String message="";
		String url="";
		try {
			flag=this.taskDao.getCouponN(memberId,homeId);
			url=babyApi+"baby/goods/successCoupon.do?homeId="+homeId;
			JpushUtil.pushMessage(memberId+"", "领取安慰奖成功!");
		} catch (Exception e) {
			message="每天只能领取一次安慰奖哦~";
		}
		result.put("flag", flag);
		result.put("message", message);
		result.put("url", url);
		return objectMapper.writeValueAsString(result);
	}

	@Override
	public String submitFirst(String flag) throws Exception{
		String memberId=getMemberIdFromFlag(flag);
		try {
			this.taskDao.submitFirst(memberId);
			return "true";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}
	
	@Override
	public double getBalance(String memberId) {
		return this.taskDao.getBalance(memberId);
	}
	
	@Override
	public String switchOpen(String flag) {
		return this.taskDao.switchOpen(flag);
		
	}
}

