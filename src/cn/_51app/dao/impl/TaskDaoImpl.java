package cn._51app.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn._51app.dao.TaskDao;
import cn._51app.utils.DateUtil;

@Repository
public class TaskDaoImpl implements TaskDao{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate diyTemplate;
	@Autowired
	private JdbcTemplate statsTemplate;
	
	@Override
	public List<Map<String, Object>> queryBetter(String bundleIds, Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`key`,"
				+ "(`bh`.`money` +`bh`.`open_money` ) AS `money`,`bh`.`remain_num` AS `remainNum`,`bh`.`time` FROM `by_home` `bh`  WHERE `bh`.`status`=1 AND `bh`.`date`<=NOW() AND NOW()<=`bh`.`end_date` AND  `bh`.`remain_num`!=0 AND `bh`.`type`!=5 AND `bh`.`first_task`=0 ");
		//手机上有的包名不会显示
		if(StringUtils.isNotBlank(bundleIds)){
			sql.append(" AND LOCATE(bh.`app_flag`,'"+bundleIds+"') =0 ");
		}
		//只显示可以接的任务
		if(memberId!=null){
			sql.append(" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id` = "+memberId+" AND `bh`.`app_id`=`app_id` AND (`status`=5 OR `status`=1 OR `status`=2 OR `status`=7 OR `status`=8 OR `status`=6)) ");
		}
		sql.append(" ORDER BY `bh`.`sort` ASC,`bh`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> queryFirstBetter(String bundleIds, Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`key`,"
				+ "(`bh`.`money` +`bh`.`open_money` ) AS `money`,`bh`.`remain_num` AS `remainNum`,`bh`.`time` FROM `by_home` `bh`  WHERE `bh`.`status`=1 AND `bh`.`date`<=NOW() AND NOW()<=`bh`.`end_date` AND  `bh`.`remain_num`!=0 AND `bh`.`type`=1 AND `bh`.`first_task`=1 ");
		//手机上有的包名不会显示
		if(StringUtils.isNotBlank(bundleIds)){
			sql.append(" AND LOCATE(bh.`app_flag`,'"+bundleIds+"') =0 ");
		}
		//只显示可以接的任务
		if(memberId!=null){
			sql.append(" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id` = "+memberId+" AND `bh`.`app_id`=`app_id` AND (`status`=5 OR `status`=1)) ");
		}
		sql.append(" ORDER BY `bh`.`sort` ASC,`bh`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> queryDirePic(String bundleIds,Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`key`,"
				+ "(`bh`.`money` +`bh`.`open_money` ) AS `money`,`bh`.`remain_num` AS `remainNum`,`bh`.`time`,`bh`.`app_flag` AS `bundleId` FROM `by_home` `bh`  WHERE `bh`.`status`=1 AND `bh`.`date`<=NOW() AND NOW()<=`bh`.`end_date` AND  `bh`.`remain_num`!=0 AND `bh`.`type`=5 ");
		//手机上有的包名不会显示
		if(StringUtils.isNotBlank(bundleIds)){
			sql.append(" AND LOCATE(bh.`app_flag`,'"+bundleIds+"') =0 ");
		}
		//只显示可以接的任务
		if(memberId!=null){
			sql.append(" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id` = "+memberId+" AND `bh`.`app_id`=`app_id` AND (`status`=5 OR `status`=1 OR `status`=2 OR `status`=7 OR `status`=8 OR `status`=6)) ");
		}
		sql.append(" ORDER BY `bh`.`sort` ASC,`bh`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> queryTankNumIs0() {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `app_flag` AS `bundleId`,`id`,`info`,`img_url` AS `imgUrl`,`type`,`key`,"
				+ "`remain_num` AS `remainNum` FROM `by_home` WHERE `status`=1 AND `remain_num`=0 AND `first_task`=0 ORDER BY `by_home`.`date` DESC LIMIT 8");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> queryTasked(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bt`.`status`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bt`.`type`,`bh`.`key`,"
				+ "`bh`.`remain_num` AS `remainNum`,`bh`.`time` FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id`  WHERE `bt`.`is_follow`=0 AND `bh`.`status`=1  AND (`bt`.`status`=1 OR `bt`.`status`=6 OR (`bt`.`status`=7 AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`bt`.`downloadtime`,'%Y-%m-%d'),CURDATE())<=2)) AND `bh`.`first_task`=0 AND `bt`.`member_id` =? ORDER BY `bt`.`status` ASC");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryFirstTasked(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bt`.`status`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`key`,"
				+ "`bh`.`remain_num` AS `remainNum`,`bh`.`time` FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id`  WHERE `bt`.`is_follow`=0 AND `bh`.`status`=1  AND `bt`.`status`=1 AND `bh`.`first_task`=1 AND `bt`.`member_id` =? ORDER BY `bt`.`status` ASC");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryFirstfinishedTask(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bt`.`status`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`title` AS `key`,"
				+ "`bh`.`remain_num` AS `remainNum` FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id`  WHERE  `bt`.`status`=5 AND `bt`.`member_id`=? AND `bh`.`first_task`=1 ORDER BY `bt`.`overtime` DESC LIMIT 8");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryTaskedF(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		//后续已经完成的任务
		//OR (`bt`.`status`=5 AND `bt`.`is_follow`=1 AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`bt`.`overtime`,'%Y-%m-%d'),CURDATE())<=1 ))
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bt`.`status`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bt`.`type`,`bh`.`title` AS `key` "
				+ " FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id`  WHERE `bt`.`is_follow`=1 AND `bh`.`status`=1  AND (`bt`.`status`=6 OR `bt`.`status`=7 OR (`bt`.`status`=8 AND `bt`.`type`!=3) OR (`bt`.`status`=8 AND `bt`.`type`=3 AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`bt`.`overtime`,'%Y-%m-%d'),CURDATE())<=1) )AND `bt`.`member_id` =? AND `bh`.`first_task`=0 ORDER BY `bt`.`status` ASC");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryfinishedTask(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`app_flag` AS `bundleId`,`bt`.`status`,`bh`.`id`,`bh`.`info`,`bh`.`img_url` AS `imgUrl`,`bh`.`type`,`bh`.`title` AS `key`,"
				+ "`bh`.`remain_num` AS `remainNum` FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id`  WHERE  `bt`.`status`=5 AND `bt`.`member_id`=?  ORDER BY `bt`.`overtime` DESC LIMIT 8");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryLatestTask() {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `app_flag` AS `bundleId`,`id`,`info`,`img_url` AS `imgUrl`,`type`,`key`,"
				+ "`remain_num` AS `remainNum`,(`money` +`open_money` ) AS `money`,`color`,DATE_FORMAT(`date`,'%d_%H:%i') AS `date`,TIMESTAMPDIFF(DAY,CURDATE(),DATE_FORMAT(`date`,'%Y-%m-%d')) AS `inteval` FROM `by_home`  WHERE `status`=1 AND `date`>NOW() AND `remain_num`!=0  ORDER BY `by_home`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public boolean checkHasTasking(Integer memberId) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_task` WHERE `is_follow`=0 AND `status`=1 AND `member_id`=? LIMIT 1 ",new Object[]{memberId}, Integer.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public void giveUpAllTask(int memberId, int homeId) {
		Map<String, Object> map=new HashMap<>();
		try {
			map=this.jdbcTemplate.queryForMap("SELECT `id`,`home_id` FROM `by_task` WHERE `is_follow`=0 AND `member_id`=? AND `status`=1 LIMIT 1", new Object[]{memberId});
		} catch (Exception e) {
			map=null;
		}
		if(map!=null){
			Integer home_id=(Integer)map.get("home_id");
			if(home_id!=homeId){
				Integer id=(Integer)map.get("id");
				//加任务数
				int ready1=this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`+1 WHERE `id`=? LIMIT 1",new Object[]{home_id});
				//改变任务状态
				int ready2=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=3,`overtime`=NOW() WHERE `id`=? LIMIT 1", new Object[]{id});
				if(ready1<=0 || ready2<=0){
					throw new RuntimeException();
				}
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String getTask(int memberId, int homeId, int statusflag, Integer type) {
		//如果之前有任务  放弃掉
		String date=DateUtil.date2String(new Date(),DateUtil.FORMAT_DATETIME);
		this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`-1 WHERE `id`=? AND `remain_num`>0 AND `type`!=5",new Object[]{homeId});
		int result2=this.jdbcTemplate.update("INSERT INTO `by_task` (`home_id`,`app_id`,`member_id`,`creatime`,`status`,`flag`,`is_follow`,`type`) SELECT "+homeId+",`app_id`,"+memberId+",'"+date+"',1,"+statusflag+",0,"+type+" FROM `by_home` WHERE `id`="+homeId+" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id`="+memberId+" AND `status`=1)");
		if(result2<=0){
			throw new RuntimeException();
		}
		return date;
	}
	
	@Override
	public void downTask(Integer memberId, int flag, String ip) {
		Integer type=this.jdbcTemplate.queryForObject("SELECT `type` FROM `by_task` WHERE `member_id`=? AND `status`=1 LIMIT 1", new Object[]{memberId},Integer.class);
		if(type==1){
			this.jdbcTemplate.update("UPDATE `by_task` SET `downloadtime`=NOW(),`flag`=?,`ip`=? WHERE `status`=1 AND `member_id`=? LIMIT 1",new Object[]{flag,ip,memberId});
		}else if(type==2 || type==3 ||type==4){
			this.jdbcTemplate.update("UPDATE `by_task` SET `downloadtime`=NOW(),`flag`=?,`ip`=?,`status`=6 WHERE `status`=1 AND `member_id`=? LIMIT 1",new Object[]{flag,ip,memberId});
		}else if(type==5){
			this.jdbcTemplate.update("UPDATE `by_task` SET `downloadtime`=NOW(),`flag`=?,`ip`=?,`status`=8,`is_follow`=1 WHERE `status`=1 AND `member_id`=? LIMIT 1",new Object[]{flag,ip,memberId});
		}
	}
	
	@Override
	public boolean checkOnlyAppleId(Integer memberId) {
		try {
			Integer result=this.jdbcTemplate.queryForObject("SELECT `isfirst` FROM `by_home` `bh` INNER JOIN `by_task` `bt` ON `bh`.`id`=`bt`.`home_id` WHERE `bt`.`member_id`=? AND `bt`.`status`=1 LIMIT 1",new Object[]{memberId}, Integer.class);
			if(result==1){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	/**
	 * tengh 2016年3月16日 下午5:07:37
	 * @param memberId 账号
	 * @param limitMoney 邀请人获得奖励的限制
	 * @param orgMoney 给一级邀请人赚的钱
	 * @param oneinvite 比例
	 * TODO 处理一级邀请人
	 */
	private void updateOneInvite(Integer memberId,double limitMoney,double orgMoney,double oneinvite,Integer homeId) {
		Map<String, Object> oneInfo=null;
		try {
			oneInfo=this.jdbcTemplate.queryForMap("SELECT `id`,`member_id`,`money` FROM `by_invite_log` WHERE `invite_id`=? AND `type`=1",new Object[]{memberId});
		} catch (Exception e) {
			
		}
		if(oneInfo!=null){
			Integer id=(Integer)oneInfo.get("id");
			Integer oneMemberId=(Integer)oneInfo.get("member_id");
			double oneMoney=(double)oneInfo.get("money");
			//计算钱的限制
			double raiseMoney=0;
			if(oneMoney>=limitMoney){//超出限制不用处理
				return;
			}else if(oneMoney+orgMoney*oneinvite>=limitMoney){//最后一次超出
				raiseMoney=limitMoney-oneMoney;
			}else{//正常
				raiseMoney=oneinvite*orgMoney;
			}
			//更新一级邀请记录
			this.jdbcTemplate.update("UPDATE `by_invite_log` SET `money`=`money`+ ? WHERE `id`=?",new Object[]{raiseMoney,id});
			//被一级邀请人加钱
			this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`canfetch_money`=`canfetch_money`+?,`invite_money`=`invite_money`+?,`today_money`=`today_money`+? WHERE `id` = ?",new Object[]{raiseMoney,raiseMoney,raiseMoney,raiseMoney,oneMemberId});
			//一级任务详情
			this.jdbcTemplate.update("INSERT `by_task_info` (`invite_id`,`member_id`,`flag`,`money`,`app_id`,`time`) VALUES(?,?,1,?,(SELECT `app_id` FROM `by_home` WHERE `id`=?),NOW())",new Object[]{memberId,oneMemberId,raiseMoney,homeId});
		}
	}
	
	/**
	 * tengh 2016年3月16日 下午5:24:04
	 * @param memberId
	 * @param limitMoney
	 * @param orgMoney
	 * @param twoinvite
	 * @param homeId
	 * TODO 处理二级邀请人
	 */
	private void updateTwoInvite(Integer memberId, double limitMoney, double orgMoney, double twoinvite,
			Integer homeId) {
		Map<String, Object> twoInfo=null;
		try {
			twoInfo=this.jdbcTemplate.queryForMap("SELECT `id`,`member_id`,`money` FROM `by_invite_log` WHERE `second_id`=? AND `type`=2",new Object[]{memberId});
		} catch (Exception e) {
			
		}
		if(twoInfo!=null){
			Integer id=(Integer)twoInfo.get("id");
			Integer twoMemberId=(Integer)twoInfo.get("member_id");
			double twoMoney=(double)twoInfo.get("money");
			//计算钱的限制
			double raiseMoney=0;
			if(twoMoney>=limitMoney){//超出限制不用处理
				return;
			}else if(twoMoney+orgMoney*twoinvite>=limitMoney){//最后一次超出
				raiseMoney=limitMoney-twoMoney;
			}else{//正常
				raiseMoney=twoinvite*orgMoney;
			}
			//更新一级邀请记录
			this.jdbcTemplate.update("UPDATE `by_invite_log` SET `money`=`money`+ ? WHERE `id`=?",new Object[]{raiseMoney,id});
			//被一级邀请人加钱
			this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`canfetch_money`=`canfetch_money`+?,`invite_money`=`invite_money`+?,`today_money`=`today_money`+? WHERE `id` = ?",new Object[]{raiseMoney,raiseMoney,raiseMoney,raiseMoney,twoMemberId});
			//一级任务详情
			this.jdbcTemplate.update("INSERT `by_task_info` (`invite_id`,`member_id`,`flag`,`money`,`app_id`,`time`) VALUES(?,?,1,?,(SELECT `app_id` FROM `by_home` WHERE `id`=?),NOW())",new Object[]{memberId,twoMemberId,raiseMoney,homeId});
		}
	}
	
	@Override
	public Map<String, Object> getByHomeById(int homeId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `id`,`title`,`key`,`money`,`open_money` AS `openMoney`,`app_id` AS `appId`,`download_url` AS `downloadUrl`,`img_url` AS `imgUrl`,`location`,`download_param` AS `downloadParam`,`infolist`,`type`,`app_flag` AS `bundleId` FROM `by_home` WHERE `id`=?",new Object[]{homeId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Map<String, Object> getFollowInfo(int homeId, String memberId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `bh`.`id`,`bh`.`preview_url` AS `previewUrl`,`bh`.`title`,`bh`.`key`,`bh`.`money`,`bh`.`open_money` AS `openMoney`,`bh`.`app_id` AS `appId`,`bh`.`img_url` AS `imgUrl`,`bh`.`location`,`bh`.`infolist`,`bh`.`type`,`bh`.`app_flag` AS `bundleId`,`bt`.`overtime` FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bh`.`id`=`bt`.`home_id` WHERE `bh`.`id`=? AND `bt`.`is_follow`=1 AND (`bt`.`status`=6 OR `bt`.`status`=7 OR `bt`.`status`=8) AND `bt`.`member_id`=?",new Object[]{homeId,memberId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public int openFollowTask(Integer memberId, Integer homeId) {
		int result=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=5 WHERE `member_id`=? AND `home_id`=? AND `is_follow`=1 AND `type`=3 AND `status`=8 AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`overtime`,'%Y-%m-%d'),CURDATE())=1 LIMIT 1",new Object[]{memberId,homeId});
		if(result>0){
			//加钱  和 试玩记录
			try {
				double money=this.jdbcTemplate.queryForObject("SELECT `open_money` AS `openMoney` FROM `by_home` WHERE `id`=?",new Object[]{homeId}, Double.class);
				this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`today_money`=`today_money`+?,`canfetch_money`=`canfetch_money`+? WHERE `id`=?",new Object[]{money,money,money,memberId});
				this.jdbcTemplate.update("INSERT INTO `by_try_log` (`app_id`,`date`,`member_id`,`name`,`money`) SELECT `app_id`,NOW(),?,CONCAT(`title`,'(后续)'),? FROM `by_home` WHERE `id`=? LIMIT 1",new Object[]{memberId,money,homeId});
			} catch (Exception e) {
				e.printStackTrace();
				result=0;
			}
		}
		return result;
	}
	
	@Override
	public int uploadFollowTask(Integer homeId, Integer memberId, String path) {
		return this.jdbcTemplate.update("UPDATE `by_task` SET `img_urls`=? ,`status`=6,`overtime`=NOW() WHERE `home_id`=? AND `member_id`=? AND `is_follow`=1 AND (`status`=8 OR `status`=7) AND (`type`=4 OR `type`=5)",new Object[]{path,homeId,memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryFirstInfo() {
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `title`,`content`,`money`,`url`,`img_url` AS `imgUrl`,`key` FROM `by_invite_info` WHERE `key`='firstTask' LIMIT 1");
		return (list==null||list.size()<=0)?null:list;
	}
	
	@Override
	public Map<String, Object> queryInviteScale(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT (SELECT `money` FROM `by_invite_info` WHERE `key`='invite' LIMIT 1) AS `directMoney`,(SELECT `money` FROM `by_invite_info` WHERE `key`='oneinvite' LIMIT 1)AS `oneInviteRate`,(SELECT `money` FROM `by_invite_info` WHERE `key`='twoinvite' LIMIT 1) AS `twoInviteRate`,(SELECT COUNT(*) FROM `by_invite_log` WHERE `type`=1 AND `member_id`=?) AS `oneInviteNum`,(SELECT COUNT(*) FROM `by_invite_log` WHERE `type`=2 AND `member_id`=?) AS `twoInviteNum`,(SELECT IFNULL(SUM(`money`+`invite_money`),0) FROM `by_invite_log` WHERE `member_id`=?) AS `allInviteMoney`,(SELECT IFNULL(SUM(`money`+`hadfetch_money`),0) FROM `by_user` WHERE `id`=?) AS `allMoney`",new Object[]{memberId,memberId,memberId,memberId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> queryNewIcons() {
		return this.jdbcTemplate.queryForList("SELECT `is_recommend` AS `isRecommend`,`img_url` AS `imgUrl`,`info`,`title`,`key` FROM `by_icons_new` WHERE `status`=1 ORDER BY `sort` ASC");
	}
	
	@Override
	public Long checkFirstTasked(Integer memberId) {
		return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `by_home` `bh` LEFT JOIN `by_task` `bt` ON `bh`.`id`=`bt`.`home_id` WHERE `bh`.`first_task`=1 AND `bt`.`status`=5 AND `bt`.`member_id`=?",new Object[]{memberId}, Long.class);
	}
	
	@Override
	public Long getFirstTaskCount(Integer memberId) {
		return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `by_home` WHERE `first_task`=1", Long.class);
	}
	
	@Override
	public boolean getCoupon(Integer memberId) {
		try {
			String idfa=this.jdbcTemplate.queryForObject("SELECT `idfa` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, String.class);
			//查看当天是否领券
//			List<Map<String, Object>> list=this.diyTemplate.queryForList("SELECT `id` FROM `diy_coupon_user` WHERE TIMESTAMPDIFF(DAY,DATE_FORMAT(`creatime`,'%Y-%m-%d'),CURDATE())=0 AND `deviceNo`=? AND `app`='com.91luo.BestRing'",new Object[]{idfa});
//			if(list.size()<=0){
				int result=this.diyTemplate.update("INSERT INTO `diy_coupon_user` (`deviceNo`,`valid`,`coupon_id`,`app`,`sys`,`org_price`,`des_price`,`creatime`,`status`) SELECT ?,`valid`,`id`,'com.91luo.BestRing','1',`org_price`,`des_price`,`creatime`,'1' FROM `diy_coupon` WHERE `type`=4 AND `status`=1 LIMIT 1",new Object[]{idfa});
				if(result>0){
					return true;
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean getCouponN(Integer memberId, String homeId) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_task` WHERE `status`=2 AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`downloadtime`,'%Y-%m-%d'),CURDATE())=0 AND `member_id`=?",new Object[]{memberId}, Integer.class);
		} catch (Exception e) {
			int result=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=2  WHERE `member_id`=? AND `home_id`=? AND `status`=7 AND (`type`=2 OR `type`=3 OR `type`=4) AND `is_follow`=0 LIMIT 1",new Object[]{memberId,homeId});
			if(result>0){
				Map<String, Object> coupon=this.jdbcTemplate.queryForMap("SELECT `bc`.`id`,`bc`.`org_price`,`bc`.`now_price`,`bc`.`about`,`bc`.`valid` FROM `by_coupon` `bc` INNER JOIN `by_home` `bh` ON `bh`.`coupon_id`=`bc`.`id` WHERE `bh`.`id`=?",new Object[]{homeId});
				this.jdbcTemplate.update("INSERT INTO `by_user_coupon` (`org_price`,`now_price`,`about`,`type`,`creatime`,`valid`,`member_id`,`coupon_id`,`home_id`) VALUES(?,?,?,1,NOW(),?,?,?,?)",new Object[]{coupon.get("org_price"),coupon.get("now_price"),coupon.get("about"),coupon.get("valid"),memberId,coupon.get("id"),homeId});
				return true;
			}
		}
		throw new RuntimeException();
	}
	
	@Override
	public Map<String, Object> getFirstStatus(Integer memberId) {
		return this.jdbcTemplate.queryForMap("SELECT `first_status`,`first_top` FROM `by_user` WHERE `id`=?",new Object[]{memberId});
	}
	
	@Override
	public void updateFirstStatus(Integer memberId) {
		this.jdbcTemplate.update("UPDATE `by_user` SET `first_status`=0 WHERE `id`=?",new Object[]{memberId});
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int finishTask(Integer homeId, Integer memberId, Integer flag, Integer firstTask) {
		if(flag==1){//有效任务
			//更新任务状态
			int result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=5,`overtime`=NOW(),`flag`=? WHERE `home_id`=? AND `member_id`=? AND `status`=1 AND `downloadtime` IS NOT NULL LIMIT 1",new Object[]{flag,homeId,memberId});
			if(result1>0){
				//查询邀请的限制
				List<Map<String, Object>> inviteNum=this.jdbcTemplate.queryForList("SELECT `key`,`money` FROM `by_invite_info` WHERE `key`='oneinvite' OR `key`='twoinvite' OR `key`='limit'");
				//查询任务的奖励金额  (只处理的前置奖励)
				double orgMoney=this.jdbcTemplate.queryForObject("SELECT `money` FROM `by_home` WHERE `id`=?",new Object[]{homeId}, Double.class).doubleValue();
				double oneinvite=0,twoinvite=0,limitMoney=0;
				for (Map<String, Object> map : inviteNum) {
					String key=(String)map.get("key");
					double money=(double)map.get("money");
					if("oneinvite".equals(key)){
						oneinvite=money;
					}else if("twoinvite".equals(key)){
						twoinvite=money;
					}else if("limit".equals(key)){
						limitMoney=money;
					}
				}
				//用户加钱
				this.jdbcTemplate.update("UPDATE `by_user` `bu` SET `bu`.`money` = `bu`.`money` + ?,`bu`.`today_money`=`bu`.`today_money`+?,`bu`.`canfetch_money` = `bu`.`canfetch_money` + ?,`bu`.`try_money` = `bu`.`try_money` + ? WHERE `bu`.`id`=? ",new Object[]{orgMoney,orgMoney,orgMoney,orgMoney,memberId});
				if(firstTask==0){
					//处理一级邀请人
					this.updateOneInvite(memberId,limitMoney,orgMoney,oneinvite,homeId);
					//处理二级邀请人
					this.updateTwoInvite(memberId,limitMoney,orgMoney,twoinvite,homeId);
				}
				//试玩记录
		        this.jdbcTemplate.update("INSERT INTO `by_try_log` (`app_id`,`date`,`member_id`,`name`,`money`) SELECT `app_id`,NOW(),?,`title`,? FROM `by_home` WHERE `id`=? LIMIT 1",new Object[]{memberId,orgMoney,homeId});
		        return 1;
			}
		}
		return 0;
	}
	
	@Override
	public List<Map<String, Object>> fontFollowTasks() {
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `id`,`is_follow`,TIMESTAMPDIFF(DAY,DATE_FORMAT(`downloadtime`,'%Y-%m-%d'),CURDATE()) AS `downloadtime`,TIMESTAMPDIFF(DAY,DATE_FORMAT(`overtime`,'%Y-%m-%d'),CURDATE()) AS `overtime`,`type`,`status` FROM `by_task` WHERE (`is_follow`=0 AND `status`=6) OR (`is_follow`=1 AND `type`=3 AND `status`=8)");
		return (list==null || list.size()<=0)?null:list;
	}
	
	@Override
	public void updateStatus(Integer id,int status) {
		this.jdbcTemplate.update("UPDATE `by_task` SET `status`=?,`downloadtime`=NOW() WHERE `id`=?",new Object[]{status,id});
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void submitFirst(String memberId) throws Exception{
		double money=this.jdbcTemplate.queryForObject("SELECT `money` FROM `by_invite_info` WHERE `key`='firstTask' LIMIT 1", Double.class);
		int result1= this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`today_money`=`today_money`+?,`canfetch_money`=`canfetch_money`+?,`try_money`=`try_money`+?,`first_top`=0 WHERE `id`=?",new Object[]{money,money,money,money,memberId});
		int result2=this.jdbcTemplate.update("INSERT INTO `by_try_log`(`app_id`,`money`,`name`,`member_id`,`date`) VALUES(?,?,?,?,NOW())",new Object[]{0,money,"新手任务",memberId});
		if(result1<=0 || result2<=0){
			throw new RuntimeException();
		}
	}
	
	@Override
	public Integer queryFirstTop(Integer memberId) {
		return this.jdbcTemplate.queryForObject("SELECT `first_top` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, Integer.class);
	}
	
	@Override
	public double getBalance(String memberId) {
		return this.jdbcTemplate.queryForObject("SELECT FLOOR(`money`) FROM `by_user` WHERE `id`=?",new Object[]{memberId}, Double.class);
	}
	
	@Override
	public String switchOpen(String flag) {
		if("open".equals(flag)){
			this.jdbcTemplate.update("UPDATE `by_sys_hub` SET `version`='1.2.0',`ismajor`=1,`is_dialog`=1 WHERE `id`=3");
			this.jdbcTemplate.update("UPDATE `by_sys_hub` SET `ismajor`=0 WHERE `id`=11");
			return "true";
		}else if("close".equals(flag)){
			this.jdbcTemplate.update("UPDATE `by_sys_hub` SET `version`='1.1.3',`ismajor`=0,`is_dialog`=0 WHERE `id`=3");
			this.jdbcTemplate.update("UPDATE `by_sys_hub` SET `ismajor`=1 WHERE `id`=11");
			return "true";
		}
		return null;
	}
	
	@Override
	public int checkIdfa(String idfa, String appid) {
		try {
			this.statsTemplate.queryForObject("SELECT `ci`.`id` FROM `cpa_idfa` `ci` INNER JOIN `cpa_appid` `ca` ON `ci`.`appid`=`ca`.`id` WHERE `ca`.`appleid`=? AND `ci`.`idfa`=? AND `ci`.`status`=1",new Object[]{appid,idfa}, Integer.class);
			return 1;
		} catch (Exception e) {
			return 0;
		}
		
	}
}
