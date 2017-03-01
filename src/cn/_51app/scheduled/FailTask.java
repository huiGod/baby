package cn._51app.scheduled;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import cn._51app.dao.TaskDao;
import cn._51app.dao.UserDao;
import cn._51app.utils.CommonUtil;

/**
 * @author Administrator
 *
 */
@Component
@RequestMapping("/task2")
public class FailTask {

	@Autowired
	private UserDao userDao;
	@Autowired
	private TaskDao taskDao;
	/**
	 * tengh 2015年12月29日 下午8:23:20
	 * TODO 任务超时过期
	 */
	@Value("#{pValue['download.wait.time']}")
	private String downloadWaitTime;
	@Scheduled(cron="0 0/2 * * * ?")
	public void checkTask(){
		try {
//			1.查询是否有过期的任务 
			List<Map<String, Object>> taskOverDueList=userDao.queryOverDueTask(CommonUtil.String2Int(downloadWaitTime));
			if(taskOverDueList!=null && taskOverDueList.size()>0){
				Integer memberId=null;
				Integer homeId=null;
				for (Map<String, Object> map : taskOverDueList) {
					memberId=(Integer)map.get("memberId");
					homeId=(Integer)map.get("homeId");
					//任务过期
					if(memberId!=null && homeId!=null){
						this.userDao.OverDueTask(memberId, homeId);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tengh 2016年5月6日 上午9:34:53
	 * TODO 初始化今日收入
	 */
	@Scheduled(cron="59 59 23 * * ?")
	public void clearTodayMoney(){
		try {
			this.userDao.clearTodayMoney();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * tengh 2016年9月23日 下午4:43:31
	 * TODO 前置审核中  和有后续的状态
	 */
	@Scheduled(cron="59 59 01 * * ?")
	@RequestMapping("/fontFollowTasks")
	public void fontFollowTasks(){
		try {
			List<Map<String, Object>> list=this.taskDao.fontFollowTasks();
			if(list!=null){
				for (Map<String, Object> map : list) {
					Integer type=(Integer)map.get("type");
					Integer id=(Integer)map.get("id");
					Long downloadtime=(Long)map.get("downloadtime");
					Long overtime=(Long)map.get("overtime");
					Integer status=(Integer)map.get("status");
					Integer isFollow=(Integer)map.get("is_follow");
					//前置审核中  3天变成失败状态
					if(status==6 && isFollow==0 && downloadtime!=null && downloadtime>=2){
						this.taskDao.updateStatus(id,7);
					}else if(isFollow==1 && status==8 && type==3 && overtime!=null && overtime>=2){
						//后续中 次日打开的变成功
						//截图的暂不处理
						this.taskDao.updateStatus(id,5);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	/**
//	 * tengh 2016年7月11日 下午3:54:23
//	 * TODO 延迟奖励 (当天的奖励第二天晚上10点到账)
//	 */
//	@Scheduled(cron="0 05 14 * * ?")
//	public void delayTask(){
//		try {
//			List<Map<String, Object>> list=this.userDao.queryDelayTask();
//			if(list!=null){
//				for (int i = 0; i < list.size(); i++) {
//					String idfa=(String)list.get(i).get("idfa");
//					Integer memberId=(Integer)list.get(i).get("memberId");
//					Integer homeId=(Integer)list.get(i).get("homeId");
//					Integer appId=(Integer)list.get(i).get("appId");
//					//查看是否激活
//					Integer status=this.userDao.queryCpaStatus(idfa,appId);
//					if(status==1){
//						this.userDao.finishTask3(homeId, memberId, 1);
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
