package cn._51app.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn._51app.BaByConstant;
import cn._51app.dao.UserDao;
import cn._51app.service.MutliThread;
import cn._51app.service.UserSercice;
import cn._51app.utils.CommonUtil;
import cn._51app.utils.DateUtil;
import cn._51app.utils.HttpUtils;
import cn._51app.utils.NotnoopAPNS;
import cn._51app.utils.ThreeDESede;
import net.sf.json.JSONObject;

/**
 * @author Administrator 用户管理
 */
@Controller
public class UserServiceImpl implements UserSercice {

	@Autowired
	private UserDao userDao;
	@Value("#{pValue['userImg.path']}")
	private String userImgPath;// 账号头像路径
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	@Value("#{pValue['downloadImg.url']}")
	private String downloadUrl;// 图片上传路径
	@Value("#{pValue['income.page']}")
	private String incomePage;
	@Value("#{pValue['income.number']}")
	private String incomeNumber;
	@Value("#{pValue['latestInvite.number']}") // 最近邀请人
	private String latestInviteNumber;
	@Value("#{pValue['fetch.range']}") // 申请提现金额范围变化
	private String fetchRange;
	@Value("#{pValue['fetch.maxmoney']}") // 申请提现最大金额
	private String fetchMaxmoney;
	@Value("#{pValue['fetch.minmoney']}") // 提现最低金额
	private String fetchMinmoney;
	@Value("#{pValue['try.number']}")//试玩每页条数
	private String tryNumber;
	@Value("#{pValue['deposit.number']}") //提现记录每页条数
	private String depositNumber;
	@Value("#{pValue['fetch.minday']}") //提现 至少所需天数
	private String fetchMinDay;
	@Value("#{pValue['download.wait.time']}") //接任务后的时间
	private String downloadWaitTime;
	@Value("#{pValue['invite.number']}") //邀请好友记录
	private String inviteNumber;
	@Value("#{pValue['task.info.number']}") //任务详情显示条数
	private String taskInfoNumber;
	@Value("#{pValue['sys.version']}")//应用版本号
	private String sysVersion;
	@Value("#{pValue['sys.version.serious']}")//是否需要强制升级
	private String sysVersionSerious;
	@Value("#{pValue['baby.api']}")//接口地址
	private String babyApi;
	@Value("#{pValue['fresh.message']}")//消息通知
	private String freshMessage;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private DecimalFormat df= new DecimalFormat("######0.00");  


//	/**
//	 * tengh 2015年11月5日 下午2:03:22
//	 * 
//	 * @param imgUrl
//	 * @param name
//	 * @param wxOpenid
//	 * @param sex
//	 * @param idfa
//	 * @param unionid
//	 * @return TODO 微信授权登录
//	 */
//	@Override
//	@Transactional(rollbackFor = Exception.class)
//	public String wxlogin(String imgUrl, String name, String wxOpenid, int sex, String idfa,String unionid)
//			throws Exception {
//		//wxOpenid  idfa确定唯一账号
//		//一致登录成功
//		if(StringUtils.isBlank(unionid) || StringUtils.isBlank(idfa) || StringUtils.isBlank(wxOpenid)){
//			return null;
//		}
//		Map<String, Object> map = userDao.getUserInfoByIdfaAndWx(idfa, wxOpenid);
//		Integer memberId=null;
//		Integer homeStatus=0;//做任务状态
//		Integer moneyStatus=0;//提现状态
//		if(map==null){ //账号不匹配
//			boolean wxOpenidInfo=userDao.checkisWxOpenid(wxOpenid);//微信号是否存在
//			boolean idfaInfo=userDao.checkisIdfa(idfa);//idfa是否存在
//			
//			if(wxOpenidInfo && !idfaInfo){//微信存在  idfa不存在
//				homeStatus=1;
//			}else if(idfaInfo && !wxOpenidInfo){//idfa存在 微信不存在
//				
//			}else if(idfaInfo && wxOpenidInfo){//微信存在 idfa存在
//				
//			}else{ //都不存在
//				homeStatus=1;
//				moneyStatus=1;
//			}
//			//生成账号
//			memberId = userDao.insertUserByWX(imgUrl, name, wxOpenid, sex, idfa,unionid,homeStatus,moneyStatus);
//			//查询这个idfa打开的时间
//            String openTime=this.userDao.queryOpenTime(idfa);
//            // 检查微信是否受邀请 (二维码)
//            Integer inviteId=userDao.queryInviteId(openTime,unionid);
//            // 检测是否有对应(ip)
//            if(inviteId==null){
//                //查询打开激活的ip
//                Map<String, Object> ipAndTime=this.userDao.queryForIp(idfa);
//                //通过ip查找时候 有邀请源id
//                if(ipAndTime!=null){
//                    inviteId=this.userDao.queryInviteMemberId((String)ipAndTime.get("ip"),(String)ipAndTime.get("creatime"));
//                }
//            }
//            if(memberId!=null && inviteId!=null && memberId!=0 && inviteId!=0){ //邀请成功
//                //插入邀请人(一级，二级)   如果是新的正常用户需要判断是否有邀请金额
//            	boolean temp=false;
//            	if(moneyStatus==1){
//            		temp=true;
//            	}
//                userDao.insertInvite(inviteId,memberId,temp);
//            }
//		}else{
//			memberId=(Integer)map.get("id");
//		}
//		String returnflag="";
//		if(memberId!=null && memberId!=0){
//			 returnflag=ThreeDESede.encryptMode("memberId:"+memberId);
//		}
//        if(map==null){
//        	map=new HashMap<String,Object>();
//        	if(StringUtils.isNotBlank(imgUrl)){
//	            if(imgUrl.startsWith("http://")){
//	                map.put("imgUrl",imgUrl);
//	            }else{
//	                map.put("imgUrl",url+imgUrl);
//	            }
//	        }else{
//	            map.put("imgUrl", "");
//	        }
//        	map.put("name", name == null ? "" : name);
//        	map.put("count",df.format(0));
//        	map.put("inviteMoney", "您的好友已为你赚了");
//        }else{
//	        String imgUrl_ = (String) map.get("imgUrl");
//	        if(StringUtils.isNotBlank(imgUrl_)){
//	            if(imgUrl_.startsWith("http://")){
//	                map.put("imgUrl",imgUrl_);
//	            }else{
//	                map.put("imgUrl",url+imgUrl_);
//	            }
//	        }else{
//	            map.put("imgUrl", "");
//	        }
//	        String name_ = (String) map.get("name");
//	        map.put("name", name_ == null ? "" : name_);
//	        double inviteMoney=(double)map.get("inviteMoney");
//	        map.put("count",df.format(inviteMoney));
//	        map.put("inviteMoney", "您的好友已为你赚了");
//        }
//        map.put("id", memberId);
//        map.put("result", returnflag);
//        map.put("wxOpenid", wxOpenid);
//        map.put("ShareMessage", "下载应用能赚钱，现金多多赚得快");
//        map.put("ShareTitle", "91赚零钱");
//        map.put("ShareUrl", babyApi+"baby/baby.html?id="+memberId);
//        return objectMapper.writeValueAsString(map);
//	}

	/**
	 * tengh 2016年4月5日 上午10:15:52
	 * @param idfa
	 * @return
	 * TODO 默认登录账号
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String login(String idfa,String inviteId) throws Exception{
		if(StringUtils.isBlank(idfa)){
			return null;
		}
		Integer memberId=null;
		//查询账号信息
		Map<String, Object> map=this.userDao.getUserInfoByIdfa(idfa);
		if(map==null){
			map=new HashMap<>();
			memberId=this.userDao.insertUserByIdfa(idfa);
			//调起激活接口
			HttpUtils.get("http://ios.api.51app.cn/ios_appActive.action?appid=1112871938&idfa="+idfa, null, 5, "utf-8");
			this.userDao.getMoneyByLogin(memberId);
        	map.put("imgUrl", "");
        	if(StringUtils.isNotBlank(inviteId) && !"null".equals(inviteId)){
				if(!memberId.equals((Integer.parseInt(inviteId)-10000))){
					//传入的id是否存在
					boolean checkInviteId=this.userDao.checkMemberById(Integer.parseInt(inviteId)-10000);
					//生成默认账号
		        	if(StringUtils.isNotBlank(inviteId) && checkInviteId){//邀请成功
		        		userDao.insertInvite((Integer.parseInt(inviteId)-10000),memberId);
		    		}
				}
        	}
        	//登录成功获取奖励
		}else{
	        String imgUrl_ = (String) map.get("imgUrl");
	        if(StringUtils.isNotBlank(imgUrl_)){
	            if(imgUrl_.startsWith("http://")){
	                map.put("imgUrl",imgUrl_);
	            }else{ 
	                map.put("imgUrl",url+imgUrl_);
	            }
	        }else{
	            map.put("imgUrl", "");
	        }
	        memberId=(Integer)map.get("id");
		}
		map.put("name", ("ID:"+(memberId+10000)));
		map.put("id", memberId);
		String returnflag=""; 
		if(memberId!=null && memberId!=0 &&StringUtils.isNotBlank(idfa)){
			 returnflag=ThreeDESede.encryptMode("memberId:"+memberId);
		}
        map.put("result", returnflag);
        return objectMapper.writeValueAsString(map);
	}

	/**
	 * tengh 2016年4月5日 下午4:11:38
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 查询个人资料
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@Override
	public String information(Integer memberId, String returnflag) throws Exception {
		boolean checkFlag=checkReturnFlag(memberId, returnflag);
		Map<String, Object> info=new HashMap<>();
		if(checkFlag){
			info=this.userDao.information(memberId);
		}
		for (Entry<String, Object> pinfo : info.entrySet()) {
			String pinfoKey=pinfo.getKey();
			if("imgUrl".equals(pinfoKey)){
				info.put(pinfoKey, url+pinfo.getValue());
			}
			if(pinfo.getValue()==null){
				info.put(pinfoKey, "");
			}
		}
		if(StringUtils.isNotBlank((String)info.get("wxOpenid"))){
			info.put("wxOpenid",new Boolean(true));
			info.put("bindUrl", "");
		}else{
			info.put("wxOpenid",new Boolean(false));
			String param=ThreeDESede.encryptMode("{\"memberId\":"+memberId+",\"returnflag\":\""+returnflag+"\"}");
			info.put("bindUrl", babyApi+"baby/u/attention.do?token="+param);
		}
		if(StringUtils.isNotBlank((String)info.get("udid"))){
			info.put("udid",new Boolean(true));
		}else{
			info.put("udid",new Boolean(false));
		}
		if(StringUtils.isNotBlank((String)info.get("mobile"))){
			info.put("mobile",new Boolean(true));
		}else{
			info.put("mobile",new Boolean(false));
		}
		return objectMapper.writeValueAsString(info);
	}
	
	@Override
	public String editInfo(Integer memberId, String returnflag, String name, Integer sex, String birthday,
			String profession,HttpServletRequest request) throws Exception{
		Map<String, Object> result=new HashMap<>();
//		@RequestParam(value="imgUrl",required=false)MultipartFile file
		boolean flag=false;
		String message="",temPath="";
		String path=downloadUrl+"baby/headImg/";
		String time=System.currentTimeMillis()+"";
		boolean checkFlag=checkReturnFlag(memberId, returnflag);
		if(checkFlag){
			if(StringUtils.isBlank(birthday)){
				birthday=null;
			}
			try {
				MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest)request;
				MultipartFile multipartFile=multipartRequest.getFile("imgUrl");
				if(multipartFile!=null && !multipartFile.isEmpty()){
					File resultPath=new File(path+time+".jpg");
					if(!resultPath.exists()){
						resultPath.mkdirs();
					}
					multipartFile.transferTo(resultPath);
					temPath="baby/headImg/"+time+".jpg";
				}
			} catch (Exception e) {
				
			}
			int resultNum=this.userDao.editInfo(memberId,name,sex,birthday,profession,temPath);
			if(resultNum>0){
				flag=true;
			}else{
				message="网络繁忙,请稍后尝试";
			}
		}else{
			message="账号异常!";
		}
		result.put("flag", flag);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2015年11月18日 下午5:09:48
	 * 
	 * @param memberId
	 * @return TODO 邀请记录
	 */
	@Override
	public String invite(int memberId) throws Exception {
		// 日期
		List<Map<String, Object>> dateList = userDao.queryInviteInfo(memberId);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < dateList.size(); i++) {
			Map<String, Object> people = new HashMap<String, Object>();
			String dateTemp = dateList.get(i).get("date").toString();
			people.put("date", dateTemp);
			int type_1 = 1;// 一级线人
			List<Map<String, Object>> personFirstList = userDao.queryInviteByDate(dateTemp, memberId, type_1);
			if(personFirstList==null || personFirstList.size()<=0){
				continue;
			}
			people.put("personFirstSize", personFirstList.size());
			for (int j = 0; j < personFirstList.size(); j++) {
				String name_=(String)personFirstList.get(j).get("name");
				Integer id_=(Integer)personFirstList.get(j).get("inviteId");
				int type_2 = 2;// 二级线人
				List<Map<String, Object>> personSecondList = userDao.queryInviteByPerson( memberId,
						(int) personFirstList.get(j).get("inviteId"), type_2);
				Map<String, Object> secondPeople = new HashMap<String, Object>();
				if (personSecondList == null || personSecondList.size() <= 0) {
					secondPeople.put("personSecond", new ArrayList<>());
				} else {
					for (int k = 0; k < personSecondList.size(); k++) {
						String name__=(String)personSecondList.get(k).get("name");
						Integer secondId__=(Integer)personSecondList.get(k).get("secondId");
						if(StringUtils.isBlank(name__)){
							personSecondList.get(k).put("name", "ID:"+(secondId__+10000));
						}
					}
					secondPeople.put("personSecond", personSecondList);
				}
				if(StringUtils.isBlank(name_)){
					personFirstList.get(j).put("name", "ID:"+(id_+10000));
				}
				personFirstList.get(j).putAll(secondPeople);
			}
			people.put("person", personFirstList);
			result.add(people);
		}
		return objectMapper.writeValueAsString(result);
	}

	/**
	 * tengh 2015年11月19日 下午4:35:50
	 * 
	 * @param memberId
	 * @return TODO 提现记录
	 */
	@Override
	public String deposit(int memberId, int page) throws Exception {
		List<Map<String, Object>> depositList = userDao.queryDeposiyByPage(memberId, page,CommonUtil.String2Int(depositNumber));
		if(depositList!=null && depositList.size()>0){
			for (int i = 0; i < depositList.size(); i++) {
				int type=(Integer)depositList.get(i).get("type");
				String moneyType="";
				String statusMessage="";
				if(type==1){
					moneyType="支付宝余额";
				}else if(type==2){
					moneyType="微信钱包";
				}
				depositList.get(i).put("getType", moneyType);
				int status=(Integer)depositList.get(i).get("status");
//				if(status==1 || status==2){
//					statusMessage="提现中...";
//				}else if(status==3){
//					statusMessage="提现失败";
//				}else if(status==4){
//					statusMessage="已入账";
//				}
				if(status==1 || status==2){
					depositList.get(i).put("status", 1);
					statusMessage="处理中";
				}else if(status==3){
					depositList.get(i).put("status", 2);
					statusMessage="到账失败";
				}else if(status==4){
					depositList.get(i).put("status", 3);
					statusMessage="已入账";
				}
				depositList.get(i).put("statusMessage", statusMessage);
			}
		}
		if(depositList!=null){
			return objectMapper.writeValueAsString(depositList);
		}else{
			return null;
		}
	}
	
	/**
	 * tengh 2016年2月23日 下午6:29:55
	 * @param memberId
	 * @param page
	 * @return
	 * @throws Exception
	 * TODO 任务详情
	 */
	@Override
	public String taskInfo(int memberId,int inviteId, int page) throws Exception{
		List<Map<String,Object>> list=this.userDao.queryTaskInfo(memberId,inviteId,page,CommonUtil.String2Int(taskInfoNumber));
		List<Map<String, Object>> result=new ArrayList<>();
		if(list!=null && list.size()>0){
			Map<String, Object> temp=null;
			for (int i = 0; i < list.size(); i++) {
				temp=new HashMap<>();
				Integer id=(Integer)list.get(i).get("id");
				String title=(String)list.get(i).get("title");
				double money=Double.valueOf(list.get(i).get("money").toString());
				String date=(String)list.get(i).get("date");
				String flag=list.get(i).get("flag").toString();
				if("1".equals(flag)){
					temp.put("title", "试玩《"+title+"》成功,奖励"+df.format(money)+"元;");
				}else{
					if(StringUtils.isBlank(title)){
						title="ID:"+(id+10000);
					}
					temp.put("title", "邀请_"+title+"_成功,奖励"+df.format(money)+"元;");
				}
				temp.put("flag", flag);
				temp.put("date", date);
				result.add(temp);
			}
		}
		return objectMapper.writeValueAsString(result);
	}

	/**
	 * tengh 2015年11月20日 下午1:47:09
	 * 
	 * @param memberId
	 * @param returnflag
	 * @return
	 * @throws Exception
	 * TODO 首页   status 0.可接  1.已经领取还未完成  2.完成但是可以继续play 3.完成  4.冻结（有任务未完成，不能接）
	 */
	@Override
	public String home(Integer memberId, String returnflag,String appIds) throws Exception {
		if(memberId==null || StringUtils.isBlank(returnflag)){
			throw new RuntimeException();
		}
		try {
			//查询邀请的信息
			List<Map<String, Object>> invite = userDao.queryInvite();
			List<Map<String, Object>> inviteResult=new ArrayList<>();
			Map<String, Object> tempMap=new HashMap<>();
			double rate=0;
			if(invite!=null){
				for (int i = 0; i < invite.size(); i++) {
					String key=(String)invite.get(i).get("key");
					if("invite".equals(key)){
						String imgUrl=(String)invite.get(i).get("imgUrl");
						tempMap.put("imgUrl", imgUrl==null?"":url+imgUrl);
						String content=(String)invite.get(i).get("content");
						tempMap.put("content", content==null?"":content);
						String title=(String)invite.get(i).get("title");
						tempMap.put("title", title==null?"":title);
						String url=(String)invite.get(i).get("url");
						tempMap.put("url", url==null?"":url);
						double money=(double)invite.get(i).get("money");
						tempMap.put("money", df.format(money));
					}else if("oneinvite".equals(key)){
						rate=(double)invite.get(i).get("money");
					}
				}
				if("0.00".equals((String)tempMap.get("money"))){
					tempMap.put("money","获得好友_"+(rate*100)+"%_收益");
				}
				inviteResult.add(tempMap);
			}
			//验证手机上没有装的应用 任务数>0 (并且  正在做的   和  完成过的任务也不会显示)
			List<Map<String, Object>> rankList = userDao.queryBetter(appIds,memberId);
			if(rankList!=null && rankList.size()>0){
				for (int i = 0;i < rankList.size(); i++) {
					rankList.get(i).put("status", 0);
					String imgUrl=(String)rankList.get(i).get("imgUrl");
					rankList.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl);
					String appFlag=(String)rankList.get(i).get("appFlag");
					rankList.get(i).put("appFlag", appFlag==null ?"":appFlag);
					String key=(String)rankList.get(i).get("key");
					rankList.get(i).put("title", key==null ?"":key);
					rankList.get(i).put("createTime", "");
					Integer temid=(Integer)rankList.get(i).get("id");
					double money=(double)rankList.get(i).get("money");
					if(temid==146){
						money=15.50;
					}
					rankList.get(i).put("money", df.format(money));
				}
			}
			//查询任务数为0的任务
			List<Map<String, Object>> taskNumIs0=this.userDao.queryTankNumIs0();
			if(taskNumIs0!=null && taskNumIs0.size()>0){
				for (int i = 0;i < taskNumIs0.size(); i++) {
					taskNumIs0.get(i).put("status", 0);
					String imgUrl=(String)taskNumIs0.get(i).get("imgUrl");
					taskNumIs0.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl);
					String appFlag=(String)taskNumIs0.get(i).get("appFlag");
					taskNumIs0.get(i).put("appFlag", appFlag==null ?"":appFlag);
					taskNumIs0.get(i).put("createTime", "");
					double money=(double)taskNumIs0.get(i).get("money");
					taskNumIs0.get(i).put("money", df.format(money));
				}
			}
			//加上 未完成的任务
			List<Map<String, Object>> tasked=userDao.queryTasked(memberId);
			if(tasked!=null){
				for (int i = 0; i < tasked.size(); i++) {
					String imgUrl=(String)tasked.get(i).get("imgUrl");
					tasked.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl);
					String key=(String)tasked.get(i).get("key");
					tasked.get(i).put("title", key==null ?"":key);
					String createTime=(String)tasked.get(i).get("createTime");
					tasked.get(i).put("createTime", createTime==null ?"":createTime);
					String appFlag=(String)tasked.get(i).get("appFlag");
					tasked.get(i).put("appFlag", appFlag==null ?"":appFlag);
					double money=(double)tasked.get(i).get("money");
					tasked.get(i).put("money", df.format(money));
				} 
			}
			//加上 已完成的任务
			List<Map<String, Object>> finishedTask=userDao.queryfinishedTask(memberId);
			if(finishedTask!=null){
				for (int i = 0; i < finishedTask.size(); i++) {
					String imgUrl=(String)finishedTask.get(i).get("imgUrl");
					finishedTask.get(i).put("imgUrl", imgUrl==null ?"":url+imgUrl);
					double money=(double)finishedTask.get(i).get("money");
					finishedTask.get(i).put("money", df.format(money));
				} 
			}
			//游客不需要验证  可以看到所有试玩应用
			Map<String, Object> map = new HashMap<String, Object>();
			//即将开始的应用
			List<Map<String, Object>> preTaskList=this.userDao.queryLatestTask(memberId);
			double startTime=0.00;
			String tempTime="";
			if(preTaskList!=null && preTaskList.size()>0){
				tempTime=(String)preTaskList.get(0).get("date");
				for (int i = 0; i < preTaskList.size(); i++) {
					String key=(String)preTaskList.get(i).get("key");
					preTaskList.get(i).put("title", key==null ?"":key);
					String dateTemp=(String)preTaskList.get(i).get("date");
					if((dateTemp.substring(0,10)).equals(DateUtil.date2StringDefault(new Date()))){
						preTaskList.get(i).put("date", "今日"+dateTemp.substring(11));
					}else if(dateTemp.substring(0,10).equals(DateUtil.cutDay(new Date(),1))){
						preTaskList.get(i).put("date", "明日"+dateTemp.substring(11));
					}else{
						preTaskList.get(i).put("date", dateTemp.substring(8,10)+"日"+dateTemp.substring(11));
					}
					double tempMoney=(double)preTaskList.get(i).get("money");
					preTaskList.get(i).put("money",df.format(tempMoney));
					startTime+=tempMoney;
				}
			}else{
				preTaskList=new ArrayList<>();
			}
			try {
				if((tempTime.substring(0,10)).equals(DateUtil.date2StringDefault(new Date()))){
					map.put("startTime","一大波任务将于今日_"+tempTime.substring(11)+"_开始,总共_"+df.format(startTime)+"_元");
				}else if(tempTime.substring(0,10).equals(DateUtil.cutDay(new Date(),1))){
					map.put("startTime","一大波任务将于明日_"+tempTime.substring(11)+"_开始,总共_"+df.format(startTime)+"_元");
				}else{
					map.put("startTime","一大波任务将于"+tempTime.substring(8,10)+"日_"+tempTime.substring(11)+"_开始,总共_"+df.format(startTime)+"_元");
				}
//				map.put("startTime","新任务即将于_"+tempTime.substring(11)+"_开始,总共_"+startTime+"_元");
			} catch (Exception e) {
				map.put("startTime","一大波任务即将到来，敬请期待");
			}
			map.put("inviteInfos", inviteResult);
			tasked.addAll(rankList);
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
	

	/**
	 * tengh 2015年11月20日 下午5:13:42
	 * 
	 * @param memberId
	 *            TODO 记录登录次数  
	 */
	@Override
	public void loginLog(Integer memberId) throws Exception{
		try {
			if (memberId != null) {
				userDao.updateLoginNum(memberId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * tengh 2015年11月23日 上午9:29:45
	 * 
	 * @param memberId
	 * @return TODO 试玩记录
	 */
	@Override
	public String tryLog(int memberId,int page) throws Exception{
		List<Map<String, Object>> tryLogList =new ArrayList<>();
		tryLogList=userDao.queryTryLogByPage(memberId, page,CommonUtil.String2Int(tryNumber));
		if(tryLogList!=null && tryLogList.size()>0){
			for (int i = 0; i < tryLogList.size(); i++) {
				String imgUrl=(String)tryLogList.get(i).get("imgUrl");
				String appId=(String)tryLogList.get(i).get("appId");
				if(StringUtils.isBlank(imgUrl)){
					imgUrl="";
				}else{
					switch (imgUrl) {
					case "wp":
						imgUrl=url+"baby/alliance/WanPu.png";
						break;
					case "qm":
						imgUrl=url+"baby/alliance/QuMi.png";
						break;
					case "zshd":
						imgUrl=url+"baby/alliance/ZhangShang.png";
						break;
					case "ym":
						imgUrl=url+"baby/alliance/YouMi.png";
						break;
					default:
						imgUrl=(imgUrl==null)?"":(url+imgUrl+"@120,120.jpg");
						break;
					}
				}
				if("0".equals(appId)){
					imgUrl=url+"baby/invite/firstTop@120,120.png";
				}
				tryLogList.get(i).put("imgUrl", imgUrl);
				double money=(Double)tryLogList.get(i).get("money");
				tryLogList.get(i).put("money", df.format(money));
			}
		}
		if(tryLogList!=null){
			return objectMapper.writeValueAsString(tryLogList);
		}else{
			return null;
		}
	}
	
	/**
	 * tengh 2015年12月5日 下午3:26:36
	 * @param memberId
	 * @param money
	 * @return
	 * TODO 申请提现
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String getAccount(String returnflag,Integer memberId, String money,int type,String zfbName,String zfbNo) throws Exception{
		Map<String, Object> result=new HashMap<>();
		boolean checkFlag=checkReturnFlag(memberId, returnflag);
		//查询 资料是否完善
		boolean infoCheck=this.userDao.checkInfo(memberId);
		if(!infoCheck){
			result.put("message", "请先完善个人资料再提现！");
			result.put("flag", new Boolean(false));
		}else{
			if(checkFlag){
				try {
					//钱和时间都检查一次
					Map<String, Object> moneyAndTime=userDao.queryMoneyAndTime(memberId);
					double canFetchMoney=(Double)moneyAndTime.get("canfetchMoney");
					double maxmoney=Double.valueOf(fetchMaxmoney).doubleValue();
					double minmoney=Double.valueOf(fetchMinmoney).doubleValue();
					Integer dayDiff=Integer.parseInt(moneyAndTime.get("dayDiff").toString());
					if(dayDiff<=CommonUtil.String2Int(fetchMinDay)){
						result.put("message", "您现在还不能提现哦,预计"+(CommonUtil.String2Int(fetchMinDay)-dayDiff+1)+"天后才可以提现");
						result.put("flag", new Boolean(false));
					}else if(canFetchMoney<minmoney){
						result.put("message", "最低提现金额"+df.format(minmoney)+"元,"+"请努力做任务哦！");
						result.put("flag", new Boolean(false));
					}else if(Double.parseDouble(money)>maxmoney){
						result.put("message", "最高提现金额"+df.format(maxmoney)+"元！");
						result.put("flag", new Boolean(false));
					}
					else{
						if(type==1 && StringUtils.isBlank(zfbName) && StringUtils.isBlank(zfbNo)){//支付宝
							zfbName=(String)moneyAndTime.get("zfbName");
							zfbNo=(String)moneyAndTime.get("zfbNo");
							if(StringUtils.isBlank(zfbName) || StringUtils.isBlank(zfbNo)){
								result.put("message","请输入支付宝账号!");
								result.put("flag", new Boolean(false));
							}else{
								userDao.getMoney(memberId,money,type,zfbName,zfbNo);
								result.put("message","提现成功!");
								result.put("flag", new Boolean(true));
							}
						}else{
							if(type==2){
								zfbName=null;
								zfbNo=null;
							}
							userDao.getMoney(memberId,money,type,zfbName,zfbNo);
							result.put("message","提现成功!");
							result.put("flag", new Boolean(true));
						}
					}
				}catch (Exception e) {
					result.put("message","网络异常!");
					result.put("flag", new Boolean(false));
				}
			}else{
				result.put("message","账号异常!");
				result.put("flag", new Boolean(false));
			}
		}
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2015年12月27日 下午3:51:08
	 * @param memberId
	 * @param returnflag
	 * @param appId
	 * TODO 领取任务
	 */
	@Override
	public String getTask(int memberId, String returnflag, int homeId,String idfa,String ip) throws Exception{
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
			if(StringUtils.isNotBlank(idfa)  && StringUtils.isNotBlank(repeatResult)){
				String resultResp=HttpUtils.get(repeatUrl+idfa, null, 5, "utf-8");
				try {
					if(StringUtils.isBlank(resultResp)){
						flag=false;
						message="任务超时,稍后重试~";
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
					message="任务失败,请联系客服~";
				}
			}
		if(flag){//排重过了  (或者不需要排重)
			HttpUtils.get("http://api.app.51app.cn/a/addApplicationNum/"+appId+".do?idfa="+idfa+"&ip="+ip+"&channel=10", null,5,"utf-8");
			this.userDao.giveUpAllTask(memberId,homeId);
			//验证memberid  和 idfa是否对应
			boolean checkMember=this.userDao.checkMemberAndIdfa(memberId,idfa);
			int flagStatus=0;
			if(checkMember){
				flagStatus=1;
			}
			createTime=userDao.getTask(memberId,homeId,flagStatus);
		}
		} catch (Exception e) {
			e.printStackTrace();
			flag=false;
			createTime="";
			message="任务已被抢完~";
		}
		result.put("flag", flag);
		result.put("createTime", createTime);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2015年12月29日 上午10:29:32
	 * @param money
	 * @return
	 * TODO 提款金额范围
	 */
	@Override
	public String getMoneyRange(String money) {
//		int orgMoney=(int)Double.valueOf(money).doubleValue();
//		int baseMoney=Integer.parseInt(fetchRange);
//		int maxMonry=Integer.parseInt(fetchMaxmoney);
//		if(orgMoney<baseMoney){
//			return "{\"money\":\"0\"}";
//		}
//		String result="";
//		for (int i = baseMoney; i <=maxMonry && i<=orgMoney; i=i+baseMoney) {
//			result+=i+",";
//		}
		return "{\"money\":\""+fetchRange+"\"}";
	}

	@Override
	public List<Map<String,Object>> getByHomeById(int id) {
		List<Map<String,Object>> lm =this.userDao.getByHomeById(id);
		if(lm.isEmpty()){
			return null;
		}
		return lm;
	}
	
	/**
	 * tengh 2016年2月16日 上午10:55:57
	 * @param memberId
	 * @param type
	 * @param money
	 * @return
	 * TODO 设置用户默认的提现方式和金额
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String setDefault(Integer memberId, Integer type, String money) {
		if(memberId==null || type==null || StringUtils.isBlank(money)){
			return BaByConstant.MSG_FALSE;
		}
		try {
			userDao.updateUserDefault(memberId,type,money);
		} catch (Exception e) {
			e.printStackTrace();
			return BaByConstant.MSG_FALSE;
		}
		return BaByConstant.MSG_TRUE;
	}
	
	/**
	 * tengh 2016年2月16日 上午10:55:42
	 * @param memberId
	 * @param homeId
	 * @return
	 * @throws Exception
	 * TODO 接任务之前对任务的检测
	 */
	@Override
	public String checkTask(Integer memberId, Integer homeId) throws Exception{
		Map<String, Object> map=new HashMap<>();
		int flag=0;
		String message="";
		if(memberId==null || memberId==0){
			flag=0;
			message="请登录再领取任务";
		}else{
			Map<String, Object> result=userDao.checkTask(memberId);
			if(result==null){
				flag=1;
			}else{
				Integer home_id=(Integer)result.get("homeId");
				if(home_id!=homeId){
					flag=1;
					message="您要更换任务吗，点确认将取消进行中的任务";
				}else{
					flag=2;
					message="您已接受此任务";
				}
			}
		}
		map.put("flag", flag);
		map.put("message", message);
		return objectMapper.writeValueAsString(map);
	}
	
	/**
	 * tengh 2016年3月24日 上午9:43:24
	 * @param uuid
	 * @param deviceToken
	 * @return
	 * TODO  绑定推送标识
	 */
	@Override
	public String saveDevice(String idfa, String deviceToken,String version,String appId) {
		try {
			if(StringUtils.isBlank(idfa) || StringUtils.isBlank(deviceToken)){
				return BaByConstant.MSG_FALSE;
			}
			try {
				this.userDao.boundDeviceToken(idfa,deviceToken,version,appId);
			} catch (Exception e) {
				
			}
		} catch (Exception e) {
			return BaByConstant.MSG_FALSE;
		}
		return BaByConstant.MSG_TRUE;
	}
	
	/**
	 * tengh 2016年1月26日 下午3:14:12
	 * @param memberId
	 * @param homeId
	 * @return 放弃当前任务
	 * TODO
	 */
	@Override
	public String giveupTask(String returnflag,Integer memberId, Integer homeId) {
		try {
			if(homeId!=null && homeId!=0){
				int result=this.userDao.giveUpTask(memberId,homeId);
				if(result>0){
					return BaByConstant.MSG_TRUE;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BaByConstant.MSG_FALSE;
	}
	
	/**
	 * tengh 2016年2月24日 上午11:02:09
	 * @param returnflag
	 * @param homeId
	 * @param memberId
	 * @param appflag
	 * @return
	 * TODO 任务完成
	 */
	@Override
	public String finishTask(String returnflag, Integer homeId, Integer memberId, String appflag,String ip) throws Exception{
		Map<String, Object> result=new HashMap<>();
		result.put("msg", new Boolean(false));
		result.put("message", "您的任务还在审核之中，请稍后提交任务~");
		try {
			//查出之前的任务有效状态
			Integer flag=0;
			Map<String, Object> ttmap=this.userDao.queryTaskFlagStatus(memberId);
			if(ttmap!=null){
				flag=(Integer)ttmap.get("flag");
			}else{
				return objectMapper.writeValueAsString(result);
			}
			String activeUrl=(String)ttmap.get("activeUrl");
			String idfa=(String)ttmap.get("idfa");
			if(StringUtils.isNotBlank(activeUrl)){
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
			if(ttmap!=null){
				flag=(Integer)ttmap.get("flag");
			}
			boolean checkFlag=checkReturnFlag(memberId, returnflag);
			if(!checkFlag){
				flag=0;
			}
			//查询对接状态 (如果需要对接)
			int isActive=this.userDao.queryActiveStatus(homeId);
			if(isActive==1){
				boolean checkActive=this.userDao.checkActive(memberId,homeId);
				if(!checkActive){
					flag=0;
				}
			}else if(isActive==2){
				flag=0;
			}
			//检测时间是否满足完成条件
			boolean isCanFinish=this.userDao.isCanFinish(homeId,memberId);
			if(isCanFinish){
				//完成任务 (flag标识任务是否有效)
				int flagCheck=this.userDao.finishTask(homeId,memberId,flag);
				//消息推送
				Map<String, Object> messageInfo=this.userDao.queryMessageInfo(homeId,memberId);
				if(messageInfo!=null){
					if(flagCheck==1){
						try {
							NotnoopAPNS.sendMessage(new String[]{(String)messageInfo.get("deviceToken")}, "您试玩的<<"+(String)messageInfo.get("title")+">>成功,获得奖励"+(double)messageInfo.get("money")+"元");
						} catch (Exception e) {
						}
						result.put("msg", new Boolean(true));
					}else{
						try {
//							NotnoopAPNS.sendMessage(new String[]{(String)messageInfo.get("deviceToken")}, "您的任务还在审核之中，请稍后提交任务~");
						} catch (Exception e) {
						}
//						result.put("message", "您的任务还在审核之中，请稍后提交任务~");
					}
				}
			}else{
				//让任务过期
//				taskOverDue(returnflag, memberId, homeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	/**
	 * tengh 2016年2月22日 下午1:52:12
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 登录刷新钱数
	 */
	@Override
	public String freshMoney(Integer memberId, String returnflag) throws Exception{
		Map<String, Object> resMap=new HashMap<>();
		//查询邀请是否可直接获得金额
		Double inviteDirMoney=(Double)this.userDao.queryForInviteDire();
		if(memberId==null || memberId==0 || StringUtils.isBlank(returnflag)){
			resMap.put("money", 0);
			if(inviteDirMoney==null || inviteDirMoney==0){
				resMap.put("inviteMoney", "细水长流慢慢赚,靠谱平台更保障");
			}else{
				resMap.put("inviteMoney", "细水长流慢慢赚,靠谱平台更保障");
			}
			resMap.put("count", 0); //邀请的金额
			return objectMapper.writeValueAsString(resMap);
		}
		resMap=this.userDao.querySimInfo(memberId);
		double count=0;
		try {
			count=(double)resMap.get("inviteMoney"); //邀请的金额
		} catch (Exception e) {
		}
		resMap.put("inviteMoney", "您的好友已为你赚了");
		resMap.put("count", df.format(count)); 
		if(count==0){
			if(inviteDirMoney==null || inviteDirMoney==0){
				resMap.put("inviteMoney", "细水长流慢慢赚,靠谱平台更保障");
			}else{
				resMap.put("inviteMoney", "细水长流慢慢赚,靠谱平台更保障");
			}
		}
		resMap.put("url", "");
		if(StringUtils.isNotBlank(freshMessage)){
			resMap.put("inviteMoney",freshMessage);
			resMap.put("url", babyApi+"baby/u/tip.do");
		}
		double todayMoney=(double)resMap.get("todayMoney");
		double tryMoney=(double)resMap.get("tryMoney"); 
		double hadfetchMoney=(double)resMap.get("hadfetchMoney"); 
		double money=(double)resMap.get("money"); 
		
		resMap.put("tryMoney", df.format(tryMoney));
		resMap.put("allMoney", df.format(hadfetchMoney+money));
		String version=(String)resMap.get("version");
		String appId=(String)resMap.get("appId");
		if(todayMoney==0){
			resMap.put("todayMoney", "竟然没有");
		}else{
			resMap.put("todayMoney", df.format(todayMoney));
		}
		if("1112871938".equals(appId)){
			resMap.put("todayMoney", "右键更新→");
		}
		return objectMapper.writeValueAsString(resMap);
	}
	
	/**
	 * tengh 2016年2月23日 上午11:02:40
	 * @return
	 * @throws Exception
	 * TODO 获取图标
	 */
	@Override
	public String icons() throws Exception{
		List<Map<String, Object>> list=this.userDao.queryIcons();
		if(list!=null && list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				String imgUrl=(String)list.get(i).get("imgUrl");
				list.get(i).put("imgUrl", url+imgUrl);
			}
		}
		return objectMapper.writeValueAsString(list);
	}
	
	/**
	 * tengh 2016年3月24日 上午11:29:00
	 * @param memberId
	 * @param returnflag
	 * @param udid
	 * TODO 绑定udid
	 */
	@Override
	public Map<String, Object> boundUdid(String memberId, String returnflag,String udid) {
		Map<String, Object> result=new HashMap<>();
//		System.err.println("1.memeberId:"+memberId+",returnflag:"+returnflag+",udid:"+udid);
//		boolean flag=checkReturnFlag(Integer.parseInt(memberId), returnflag);
//		System.err.println("2.flag:"+flag);
		String message="";
		int resFlag=0;
		boolean flag=true;
		if(flag){
			//验证原来的udid是否是这个
			String tempudid=this.userDao.queryUdid(memberId);
			if(StringUtils.isNotBlank(tempudid)){
				if(tempudid.equals(udid)){//原设备
					resFlag=1;
				}else{
					message="设备无法绑定，请联系客服帮助!";
				}
			}else{
				resFlag=this.userDao.boundUdid(memberId,udid);
				if(resFlag==0){
					message="设备无法绑定，请联系客服帮助!";
				}else{
					resFlag=1;
				}
			}
		}else{
			message="设备无法绑定，请联系客服帮助!";
		}
		result.put("resFlag", resFlag);
		result.put("message", message);
		return result;
	}
	
	/**
	 * tengh 2016年2月23日 下午2:57:04
	 * @param returnflag
	 * @param memberId
	 * @return
	 * @throws Exception
	 * TODO 检测一键提现
	 */
	@Override
	public String checkAccount(String returnflag, Integer memberId,String appleid) throws Exception{
		boolean flag=checkReturnFlag(memberId, returnflag);
		Map<String, Object> result=new HashMap<>();
		String message="";
		String error="";
		//检测账号资料完整性
		boolean infoCheck=this.userDao.checkInfo(memberId);
		if(!infoCheck){
			error="infoerror";
			result.put("message", "请先完善个人资料再提现！");
			result.put("flag", new Boolean(false));
			result.put("error", error);
			return objectMapper.writeValueAsString(result);
		}
		//查询账号状态
		int checkAccount=this.userDao.queryStatus(memberId);
		if(!flag && checkAccount!=1){ 
			error="accounterror";
			message="系统检测到您的账号存在异常信息，本账号不可继续使用 ";
			result.put("flag", new Boolean(false));
			result.put("message", message);
			result.put("error", error);
			return objectMapper.writeValueAsString(result);
		}
		result=this.userDao.queryDefault(memberId);
		String zfbName=(String)result.get("zfbName");
		result.put("zfbName", zfbName==null?"":zfbName);
		String zfbNo=(String)result.get("zfbNo");
		result.put("zfbNo", zfbNo==null?"":zfbNo);
		result.put("message", "");
		//最低取款额度
		result.put("minMoney", Integer.parseInt(fetchMinmoney));
		//账号申请提现至少的天数
		result.put("minFetchDay", CommonUtil.String2Int(fetchMinDay));
		String createDate=(String)result.get("createDate");
		String temDate=DateUtil.operDay(createDate, CommonUtil.String2Int(fetchMinDay));
		long tdate=new SimpleDateFormat("yyyy-MM-dd").parse(temDate).getTime();
		if(tdate<=new Date().getTime()){
			result.put("canFetch", new Boolean(true));
		}else{
			result.put("canFetch", new Boolean(false));
		}
		result.put("error", error);
		Map<String, Object> resMap=this.userDao.getHub(appleid);
		String attentionName="91赚零钱";
		if(resMap!=null){
			attentionName=(String)resMap.get("weixin");
		}
		result.put("attention", attentionName);
		result.put("messages", "温馨提示:\n1.双休日及节假日不办理提现业务\n2.单次提现金额最少为"+fetchMinmoney+"元\n3.首次提现需注册满"+(Integer.parseInt(fetchMinDay)+1)*24+"小时\n4.支付宝提现每笔需扣除手续费1元");
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年2月23日 下午4:47:31
	 * @param memberId
	 * @return
	 * TODO 邀请好友记录
	 */
	@Override
	public String inviteRecord(int memberId,int page) throws Exception{
		if(page<0){
			page=0;
		}
		//邀请的一级好友赚的钱
		List<Map<String, Object>> list=this.userDao.queryInviteRecord(memberId,page,CommonUtil.String2Int(inviteNumber));
		for (int i = 0; i < list.size(); i++) {
			double money=(double)list.get(i).get("money");
			double inviteMoney=(double)list.get(i).get("inviteMoney");
			list.get(i).put("money", df.format(money+inviteMoney));
			String name=(String)list.get(i).get("name");
			Integer id=(Integer)list.get(i).get("id");
			if(StringUtils.isBlank(name)){
				list.get(i).put("name", "ID:"+id);
			}
		}
		return objectMapper.writeValueAsString(list);
	}
	
	/**
	 * tengh 2016年2月23日 下午7:58:51
	 * @param returnflag
	 * @param memberId
	 * @param homeId
	 * @return
	 * TODO 时间到任务过期
	 */
	@Override
	public String taskOverDue(String returnflag, Integer memberId, Integer homeId) {
		try {
			this.userDao.OverDueTask(memberId,homeId);
		} catch (Exception e) {
			return BaByConstant.MSG_FALSE;
		}
		return BaByConstant.MSG_TRUE;
	}
	
	/**
	 * tengh 2016年2月29日 下午4:07:17
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 查询邀请获取的金额比例
	 */
	@Override
	public Map<String, String> queryInviteScale(Integer memberId) {
		List<Map<String, Object>> list=this.userDao.queryInviteScale(memberId);
		Map<String, String> resMap=new HashMap<>();
		for (Map<String, Object> map : list) {
			String key=(String)map.get("key");
			double money=(double)map.get("money");
			if("oneinvite".equals(key)){
				resMap.put("oneinvite", (int)(money*100)+"%");
			}else if("twoinvite".equals(key)){
				resMap.put("twoinvite", (int)(money*100)+"%");
			}else if("invite".equals(key)){
				resMap.put("inviteDir", money+"");
			}else if("limit".equals(key)){
				resMap.put("oneMoney", money+"");
			}
		}
		//ticket获取二维码图片
		//https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=
		//二维码
//		String ticket=null;
//		try {
//			//查询数据库 是否过期
//			ticket=this.userDao.queryTicket(memberId);
//			if(StringUtils.isBlank(ticket)){
//				String token=HttpClientUtil.get("http://api.app.51app.cn/wx/getToken.do", null);
//				Long sceneId=Long.valueOf((System.currentTimeMillis()+"").concat(memberId+""));
//				String json=CommonUtil.post("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+token, "{\"expire_seconds\": 604800, \"action_name\":\"QR_SCENE\", \"action_info\":{\"scene\": {\"scene_id\": "+sceneId+"}}}");
//				Map<String, Object> map=objectMapper.readValue(json, Map.class);
//				ticket=(String)map.get("ticket");
//				userDao.insertBarCode(memberId,ticket);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		resMap.put("ticket", "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket);
		return resMap;
	}
	
	/**
	 * tengh 2016年3月5日 下午3:23:54
	 * @param version
	 * @return
	 * @throws Exception
	 * TODO 版本更新
	 */
	@Override
	public String updateVersion(String version,String appleid) throws Exception{
		Map<String, Object> map=new HashMap<>();
		String downloadUrl="https://itunes.apple.com/cn/app/id"+appleid;
		String message="";
		boolean flag=true,important=true;
		Map<String, Object> resMap=this.userDao.getHub(appleid,version);
		if(resMap!=null){
			String level=(String)resMap.get("level");
			downloadUrl=((String)resMap.get("downloadUrl"))==null?downloadUrl:((String)resMap.get("downloadUrl"));	
			if("0".equals(level)){
				flag=false;
				important=false;
			}
		}
		if(important){
//			message="为了您更稳定的赚钱！当前版本已停用！请卸载当前应用！同时打开APPstore搜索（91铃声）下载安装！您之前的任务收入有效！";
			message="\ue022立即更新有惊喜!!  任务多，价格高，提现快!!!";
		}
		map.put("flag", flag);
		map.put("important", important);
		map.put("message", message);
		map.put("url", downloadUrl);
		return objectMapper.writeValueAsString(map);
//		if(resMap!=null){
//			String tversion=(String)resMap.get("version");
//			String tlevel=(String)resMap.get("level");
//			tdownloadUrl=(String)resMap.get("downloadUrl");
//			if(StringUtils.isBlank(version)){
//				message="版本号异常!";
//			}
//			if(StringUtils.isNotBlank(tdownloadUrl)){
//				boolean tempFlag = true;
//				String[] versions=version.split("\\.");//传入的版本号
//				String[] tversions=tversion.split("\\.");//数据库最高版本号
//				// 始终不升级
//				for (int i = 0; i < versions.length; i++) {
//					if(Integer.parseInt(tversions[i])<Integer.parseInt(versions[i])){
//						tempFlag=false;
//						break;
//					}
//				}
//				//当前版本号是否升级
//				if(version.equals(tversion)){
//					if("1".equals(tlevel)){
//						tempFlag=true;
//					}else{
//						tempFlag=false;
//					}
//				}
//				if(tempFlag){//正常版本号
//					map.put("flag", true);
//					map.put("important", true);
//					message="为了您更稳定的赚钱！当前版本已停用！请卸载当前应用！同时打开APPstore搜索（最美铃声）下载安装！您之前的任务收入有效！";
//				}else{//审核版本号
//					map.put("flag", false);
//					map.put("important", false);
//				}
//			}else{
//				map.put("flag", false);
//				map.put("important", false);
//			}
//		}
	}
	
	/**
	 * tengh 2016年6月2日 下午6:02:15
	 * @param string
	 * @param integer
	 * @param b
	 * @return
	 * TODO 检测appStoreId账号是否重复下载
	 */
	@Override
	public String checkAppStore(String returnflag, Integer memberId, boolean isPurchasedReDownload,Integer homeId) throws Exception{
		boolean flag=true;
		Map<String, Object> result=new HashMap<>();
		String message="";
		boolean onlyAppleId=this.userDao.checkOnlyAppleId(memberId);
		if(onlyAppleId){//应用需要检测账号是否首次
			if(isPurchasedReDownload){
				flag=false; //账号之前下载过
				//放弃任务
				giveupTask(returnflag, memberId, homeId);
			}
		}
		if(!flag){
			message="检测到您的账号已经下载过此应用,任务失败 _好的";
			String deviceToken=this.userDao.queryDeviceToken(memberId);
			MutliThread mutliThread=new MutliThread(deviceToken, message.split("_")[0]);
			mutliThread.start();
		}
		result.put("message", message);
		result.put("flag", flag);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年3月5日 下午3:24:20
	 * @param returnflag
	 * @param memberId
	 * @return
	 * TODO 是否是appStore下载
	 */
	@Override
	public String downTask(String returnflag, Integer memberId,String ip,String isAppstore) throws Exception{
		Map<String, Object> result=new HashMap<>();
		String message="";
		boolean msg=false;
		//消息推送
		String deviceToken=this.userDao.queryDeviceToken(memberId);
		Integer flag=0;
		Map<String, Object> ttmap=this.userDao.queryTaskFlagStatus(memberId);
		if(ttmap!=null){
			flag=(Integer)ttmap.get("flag");
		}else{
			result.put("msg", false);
			result.put("message", "");
			return objectMapper.writeValueAsString(result);
		}
		Integer homeId=(Integer)ttmap.get("homeId");
//		//查询已接任务  是否需要对账号唯一做限定
//		boolean onlyAppleId=this.userDao.checkOnlyAppleId(memberId);
//		if(onlyAppleId){
//			if(isPurchasedReDownload){
//				flag=0;
//			}
//		}
		if(StringUtils.isNotBlank(isAppstore)){
			if(!"1".equals(isAppstore)){
			giveupTask(returnflag, memberId, homeId);
			message="通过非苹果官方下载~_我知道了";
			msg=false;
			flag=0;
			}
		}
		if(flag==1){
			message="开始计时!";
			msg=true;
			this.userDao.downTask(memberId,1,ip);
		}else{
			msg=false;
			if(StringUtils.isBlank(message)){
				message="通过非苹果官方下载_我知道了";
			}
		}
		MutliThread m1=new MutliThread(deviceToken,message.split("_")[0]);
		m1.start();
		result.put("msg", msg);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年3月24日 下午5:08:28
	 * @param mobile
	 * @return
	 * @throws Exception
	 * TODO 查询手机号是否存在
	 */
	@Override
	public String checkMobile(String mobile,Integer memberId,String returnflag) throws Exception {
		Map<String, Object> result=new HashMap<>();
		boolean flag=false;
		String message="";
		boolean isflag=false;
		if(memberId!=null && memberId!=0){
			isflag=checkReturnFlag(memberId, returnflag);
		}
		if(isflag){
			//手机号是否是之前已经绑定的
			String tempMobile=this.userDao.checkMobileAndMember(memberId);
			if(StringUtils.isNotBlank(tempMobile)){
				if(!tempMobile.equals(mobile)){
					message="系统检测到您的账号已绑定其它手机，绑定失败!";
				}else{
					flag=true;
				}
			}else{
				flag=this.userDao.checkMobile(mobile);
				if(!flag){
					flag=true;
				}else{
					message="系统检测到您的手机已绑定其它账号，绑定失败!";
					flag=false;
				}
				result.put("flag", flag);
			}
		}else{
			message="账号异常!";
		}
		result.put("flag", flag);
		result.put("message", message);
		return objectMapper.writeValueAsString(result);
	}
	
	
	
	/**
	 * tengh 2016年3月24日 下午5:55:39
	 * @param mobile
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 账号绑定手机号
	 */
	@Override
	public String boundMobile(String mobile, Integer memberId, String returnflag) throws Exception{
		Map<String, Object> result2=new HashMap<>();
		String json=null;
		String message="";
		boolean flag=false;
		try {
			json=checkMobile(mobile, memberId, returnflag);
			if(StringUtils.isNotBlank(json)){
				Map<String, Object> result=new HashMap<>();
				result=objectMapper.readValue(json, Map.class);
				boolean tempFlag= (Boolean)result.get("flag");
				if(tempFlag){
					flag=this.userDao.boundMobile(mobile,memberId);
				}else{
					result2.put("flag", new Boolean(false));
					message="绑定失败，请确认号码是否为首次绑定或联系客服!";
				}
				result2.put("flag", flag);
				result2.put("message", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectMapper.writeValueAsString(result2);
	}
	
	/**
	 * tengh 2016年4月7日 上午10:02:05
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 绑定公众号验证码
	 */
	@Override
	public String boundCode(Integer memberId, String returnflag,String code) {
		boolean checkflag=this.checkReturnFlag(memberId, returnflag);
		String message="";
		if(checkflag){
			//账号已经绑定
			boolean isbound=this.userDao.checkIsBoundWx(memberId);
			if(isbound){
				message="账号已绑定微信!";
			}else{
				//验证码绑定
				Integer result=this.userDao.updateWxByCode(memberId,code);
				if(result<1){
					message="验证码错误,绑定失败!";
				}else{
					message="success";
				}
			}
		}
		return message;
	}
	
	/**
	 * tengh 2016年5月4日 下午8:13:35
	 * @param string
	 * @return
	 * TODO 是否直接打开应用
	 */
	@Override
	public String isActive(String idfa) {
		//对比该用户是否之前已经激活过
		return "{\"flag\":"+this.userDao.isActive(idfa)+"}";
	}
	
	/**
	 * tengh 2016年5月9日 下午3:12:09
	 * @param appleid
	 * @return
	 * TODO 获取套壳信息
	 */
	@Override
	public Map<String, Object> getHub(String appleid) {
		return this.userDao.getHub(appleid);
	}
	
	/**
	 * tengh 2016年5月9日 下午8:22:11
	 * @param appleid
	 * @return
	 * TODO 获取打开scheme
	 */
	@Override
	public String getSchemeUrl(String appleid) {
		return this.userDao.getSchemeUrl(appleid);
	}
	
	/**
	 * tengh 2016年5月10日 下午7:29:17
	 * @return
	 * TODO 获取激活的壳
	 */
	@Override
	public Map<String, Object> getHubBydefault() {
		return this.userDao.getHubBydefault();
	}
	
	/**
	 * tengh 2016年5月25日 下午1:55:01
	 * @param zfbName
	 * @param zfbNo
	 * @param memberId
	 * @param returnflag
	 * @return
	 * TODO 绑定支付宝
	 */
	@Override
	public String checkZfb(String zfbName, String zfbNo, Integer memberId, String returnflag) throws Exception{
		Map<String, Object> map=new HashMap<>();
		boolean checkMember=checkReturnFlag(memberId, returnflag);
		boolean flag=false;
		String message="";
		if(checkMember){
			 flag=this.userDao.checkZfb(memberId,zfbName,zfbNo);
		}
		if(!false){
			message="请绑定和手机号相同的支付宝账号";
		}
		map.put("flag", flag);
		map.put("message", message);
		return objectMapper.writeValueAsString(map);
	}
	
	@Override
	public String getIdfa(String memberId) {
		return this.userDao.getIdfa(memberId);
	}
	
	@Override
	public String pushMessage() {
		for (int i = 1; i < 80000; i++) {
			String deviceToken=getDevice(i);
			if(StringUtils.isNotBlank(deviceToken)){
//				MutliThread mutliThread=new MutliThread(deviceToken, "最多任务的一天，赶紧来抢吧~");
//				mutliThread.start();
				NotnoopAPNS.sendMessage(new String[]{deviceToken}, "最多任务的一天，赶紧来抢吧~");
			}
		}
		return null;
	}

	private String getDevice(int i) {
		return this.userDao.getDeviceToken(i);
	}
	
	@Override
	public String submitTask(String returnflag, Integer memberId,Integer homeId,String appFlag) throws Exception{
		Map<String, Object> result=new HashMap<>();
		result.put("msg", new Boolean(false));
		result.put("message", "您的任务还在审核之中，请稍后提交任务~");
		try {
			//查出之前的任务有效状态
			Integer flag=0;
			Map<String, Object> ttmap=this.userDao.queryTaskFlagStatus(memberId);
			if(ttmap!=null){
				flag=(Integer)ttmap.get("flag");
			}else{
				return objectMapper.writeValueAsString(result);
			}
			boolean checkFlag=checkReturnFlag(memberId, returnflag);
			if(!checkFlag){
				flag=0;
			}
			//查询对接状态 (如果需要对接)
			int isActive=this.userDao.queryActiveStatus(homeId);
			if(isActive==1){
				boolean checkActive=this.userDao.checkActive(memberId,homeId);
				if(!checkActive){
					flag=0;
				}
			}else if(isActive==2){
				flag=0;
			}
			//检测时间是否满足完成条件
			boolean isCanFinish=this.userDao.isCanFinish(homeId,memberId);
			if(isCanFinish){
				//完成任务 (flag标识任务是否有效)
				int flagCheck=this.userDao.finishTask(homeId,memberId,flag);
				//消息推送
				Map<String, Object> messageInfo=this.userDao.queryMessageInfo(homeId,memberId);
				if(messageInfo!=null){
					if(flagCheck==1){
						try {
							NotnoopAPNS.sendMessage(new String[]{(String)messageInfo.get("deviceToken")}, "您试玩的<<"+(String)messageInfo.get("title")+">>成功,获得奖励"+(double)messageInfo.get("money")+"元");
						} catch (Exception e) {
						}
						result.put("msg", new Boolean(true));
						result.put("message", "任务完成!");
					}else{
						try {
	//						NotnoopAPNS.sendMessage(new String[]{(String)messageInfo.get("deviceToken")}, "任务超时,请重新重试!");
						} catch (Exception e) {
						}
	//					result.put("message", "任务超时,请重新重试!");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectMapper.writeValueAsString(result);
	}
	
	/**
	 * tengh 2016年6月21日 上午10:01:34
	 * @param appleid
	 * @param version
	 * @return
	 * TODO 获取套壳信息
	 */
	@Override
	public Map<String, Object> getHub(String appleid, String version) {
		return this.userDao.getHub(appleid,version);
	}
	
	@Override
	public String directFinish(String appleid, String idfa) {
		/**
		 * 是否存在wall
		 */
//		boolean isWall=this.userDao.isWall(appleid,idfa);
//		if(isWall){
		Integer memberId=this.userDao.queryMemberIdByidfa(idfa);
		Integer homeId=this.userDao.queryHomeIdByAppleid(appleid);
		int result=0;
		if(memberId>0 && homeId>0){
			result=this.userDao.finishTask2(homeId, memberId, 1);
		}
//			if(1==result){
//				//任务数量-1
//				this.userDao.desHomeNum(homeId);
//			}
	    return String.valueOf(result);
//		}
//		return "0";
	}
	
	@Override
	public String desHomeNum(String appleid,String idfa) {
		/**
		 * 是否存在wall
		 */
//		boolean isWall=this.userDao.isWall(appleid,idfa);
//		System.err.println("desHomeNum-->isWall:"+isWall);
//		if(isWall){
			return this.userDao.desHomeNum(appleid);
//		}
//		return "0";
	}
}

