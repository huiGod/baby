package cn._51app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn._51app.dao.UserDao;
import cn._51app.utils.DateUtil;
import cn._51app.utils.JpushUtil;

@Repository
public class UserDaoImpl implements UserDao{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate statsTemplate; 
	
	@Override
	public Integer insertUserByIdfa(final String idfa) {
		KeyHolder keyHolder=new GeneratedKeyHolder();
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				int i = 0;
				PreparedStatement ps=connection.prepareStatement("INSERT INTO `by_user` (`create_date`,`idfa`) VALUES(NOW(),?)",Statement.RETURN_GENERATED_KEYS);
				ps.setString(++i, idfa);
				return ps;
			}
		},keyHolder);
		this.jdbcTemplate.update("UPDATE `by_user` SET `device_token`=(SELECT `device_token` FROM `by_device_token` WHERE `idfa`=? ORDER BY `creatime` DESC LIMIT 1 ) WHERE `idfa`=?",new Object[]{idfa,idfa});
		return keyHolder.getKey().intValue();
	}
	
	@Override
	public List<Map<String, Object>> queryInviteInfo(int memberId) {
		return this.jdbcTemplate.queryForList("SELECT DISTINCT(`date`) AS `date` FROM `by_invite_log` bi  WHERE `bi`.`member_id`=? AND `bi`.`type`=1 ORDER BY `date` desc",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryInviteByDate(String dateTemp, int memberId,int type) {
		return this.jdbcTemplate.queryForList("SELECT `bi`.`invite_id` AS `inviteId`,`bu`.`name`,(`bi`.`money`+`bi`.`invite_money`) AS `money` FROM `by_invite_log` bi LEFT JOIN `by_user` `bu` ON `bi`.`invite_id`=`bu`.`id` WHERE `bi`.`member_id`=? AND `date`=? AND `type`=?  ORDER BY `date` DESC ",new Object[]{memberId,dateTemp,type});
	}
	
	@Override
	public List<Map<String, Object>> queryInviteByPerson( int memberId, int inviteId, int type) {
		return this.jdbcTemplate.queryForList("SELECT `bi`.`second_id` AS `secondId`,`bu`.`name`,`bi`.`money` FROM `by_invite_log` bi LEFT JOIN `by_user` `bu` ON `bi`.`second_id`=`bu`.`id` WHERE `bi`.`member_id`=?  AND `type`=? AND `invite_id`=?  ORDER BY `date` DESC ",new Object[]{memberId,type,inviteId});
	}
	
	@Override
	public List<Map<String, Object>> queryDeposit(int memberId, int page) {
		return this.jdbcTemplate.queryForList("SELECT DISTINCT(DATE_FORMAT(`date`,'%Y-%m-%d')) AS `date` FROM `by_money_log2`  WHERE `member_id`=? ORDER BY `date` desc LIMIT "+page*7+",7",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryDeposiyByDate(int memberId, String dateTemp) {
		return this.jdbcTemplate.queryForList("SELECT `type`,`money`,`status` FROM `by_money_log2` WHERE `member_id`=? AND `date`<='"+dateTemp+" 23:59:59' AND `date`>='"+dateTemp+" 00:00:00' ORDER BY `status` ASC",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryBetter(String appFlags,Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`id`,`bh`.`app_id` AS `appId`,`bh`.`title`,`bh`.`key`,`bh`.`img_url` AS `imgUrl`,`bc`.`name`,"
				+ "`bh`.`money`,`bh`.`app_flag` AS `appFlag`,`bh`.`remain_num` AS `remainNum`,`bh`.`time`,DATE_FORMAT(`bh`.`date`,'%d日%H:%i') AS `date`,`bh`.`color` FROM `by_home` `bh` LEFT JOIN `by_cat` `bc` ON `bh`.`cat`=`bc`.`id` WHERE `bh`.`status`=1 AND `bh`.`date`<=NOW() AND `bh`.`remain_num`!=0 AND `bh`.`first_task`=0 ");
		//手机上有的包名不会显示
		if(StringUtils.isNotBlank(appFlags)){
			sql.append(" AND LOCATE(bh.`app_flag`,'"+appFlags+"') =0 ");
		}
		//完成过的任务全部不查询出来
		if(memberId!=null){
			sql.append(" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id` = "+memberId+" AND `bh`.`app_id`=`app_id` AND (`status`=5 OR `status`=1 OR `status`=2 OR `status`=6 OR `status`=7 OR `status`=8)) ");
		}
		sql.append(" ORDER BY `bh`.`sort` ASC,`bh`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> queryTankNumIs0() {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`id`,`bh`.`app_id` AS `appId`,`bh`.`title`,`bh`.`img_url` AS `imgUrl`,`bc`.`name`,"
				+ "`bh`.`money`,`bh`.`app_flag` AS `appFlag`,`bh`.`remain_num` AS `remainNum`,`bh`.`time`,DATE_FORMAT(`bh`.`date`,'%d日%H:%i') AS `date`,`bh`.`color` FROM `by_home` `bh` LEFT JOIN `by_cat` `bc` ON `bh`.`cat`=`bc`.`id` WHERE `bh`.`status`=1 AND `bh`.`date`<=NOW() AND `bh`.`remain_num`=0 AND `bh`.`first_task`=0 ORDER BY `bh`.`date` DESC LIMIT 8");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public void insertBlackList(String idfa) {
		this.jdbcTemplate.update("INSERT INTO `by_blacklist` (`idfa`,`time`) VALUES (?,NOW()) ON DUPLICATE KEY UPDATE `time`=NOW()",new Object[]{idfa});
	}
	
	@Override
	public boolean checkIsBlacklist(String idfa) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_blacklist` WHERE `idfa`=?", new Object[]{idfa}, Integer.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateLoginNum(Integer memberId) {
		String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		//初始化登录次数
		this.jdbcTemplate.update("UPDATE `by_user` SET `login_num`=`login_num`+1,`login_day`=`login_day`+(CASE WHEN `init_date`='"+date+"' THEN 0 ELSE 1 END),init_date='"+date+"'  WHERE id=?",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryTryDate(Integer memberId, int page) {
		return this.jdbcTemplate.queryForList("SELECT DISTINCT(DATE_FORMAT(`date`,'%Y-%m-%d')) AS `date` FROM `by_try_log`  WHERE `member_id`=? ORDER BY `date` desc LIMIT "+page*7+",7",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryTryLogByDate(int memberId, String dateTemp) {
		return this.jdbcTemplate.queryForList("SELECT `bh`.`id`,`bt`.`name` AS `title`,`bh`.`img_url` AS `imgUrl`,`bh`.`money` FROM `by_try_log` `bt` LEFT JOIN `by_home` `bh` ON `bh`.`app_id` =`bt`.`app_id` WHERE `bt`.`member_id`=? AND '"+dateTemp+" 00:00:00' <=`bt`.`date` AND  `bt`.`date`<='"+dateTemp+" 23:59:59' ORDER BY `bt`.`date` DESC ",new Object[]{memberId});
	}
	
	@Override
	public void insertBarCode(int memberId, String ticket) {
		this.jdbcTemplate.update("INSERT INTO `by_barcode` (`ticket`,`member_id`,`date`)VALUES (?,?,NOW()) ON DUPLICATE KEY UPDATE `ticket`=?,`date`=NOW()",new Object[]{ticket,memberId,ticket});
	}
	
//	@Override
//	public Integer queryInviteId(String opentime,String unionId) {
//		if(StringUtils.isBlank(opentime)){
//			return null;
//		}else{
//			String sql="SELECT `member_id` AS `memberId` FROM `by_invite` WHERE `union_id`='"+unionId+"' AND DATE_FORMAT(`time`,'%Y-%m-%d %H:%i:%s')<='"+opentime+"' ORDER BY `time` DESC LIMIT 1";
//			System.err.println(sql);
//			List<Map<String, Object>> list=this.jdbcTemplate.queryForList(sql);
//			System.err.println("查询结果list:"+list.size());
//			return (list==null || list.size()<=0)?null:(Integer)list.get(0).get("memberId");
//		}
//	}
	
	@Override
	public List<Map<String, Object>> queryInvite() {
		return this.jdbcTemplate.queryForList("SELECT `id`,`content`,`img_url` AS `imgUrl`,`url`,`title`,`money`,`key` FROM `by_invite_info` WHERE `key`='invite' OR `key`='oneinvite' ORDER BY `SORT` ASC");
	}
	
	
	@Override
	public List<Map<String, Object>> queryTasked(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bt`.`status`,`bh`.`id`,`bh`.`app_id` AS `appId`,`bh`.`title`,`bh`.`key`,`bh`.`img_url` AS `imgUrl`,`bc`.`name`,"
				+ "`bh`.`money`,`bh`.`app_flag` AS `appFlag`,DATE_FORMAT(`bt`.`creatime`,'%Y-%m-%d %H:%i:%s') AS `createTime`,`bh`.`remain_num` AS `remainNum`,`bh`.`time`,DATE_FORMAT(`bh`.`date`,'%d日%H:%i') AS `date`,`bh`.`color` FROM `by_home` `bh` LEFT JOIN `by_cat` `bc` ON `bh`.`cat`=`bc`.`id` LEFT JOIN `by_task` `bt` ON `bt`.`home_id` =`bh`.`id` WHERE `bh`.`status`=1  AND (`bt`.`status`=1 OR `bt`.`status`=2) AND `bt`.`member_id` ="+memberId+" AND `bh`.`first_task`=0 ORDER BY `bh`.`sort` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public List<Map<String, Object>> getByHomeById(int id) {
		String select ="SELECT bh.id AS bhid,bh.about, bh.money,`bh`.`app_id` AS `appId`,bh.`key`,`bh`.`download_url` AS `downloadUrl`,bh.img_url AS imgUrl,bh.location,bh.title,bh.time,bh.isfirst,`bh`.`download_param` AS `downloadParam`";
		String from =" FROM by_home bh ";
		String where =" WHERE bh.id="+id;
		String sql =select+from+where;
		return this.jdbcTemplate.queryForList(sql);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void getMoney(int memberId, String money, int type,String zfbNo,String zfbName) {
		int result1=this.jdbcTemplate.update("update `by_user` SET `money` =`money`-?,`canfetch_money` =`canfetch_money` -?,`hadfetch_money`=`hadfetch_money`+? where `money` >=? and `canfetch_money` >=? and `id` =?",new Object[]{money,money,money,money,money,memberId});
		if(1==type){
			money=String.valueOf((Double.valueOf(money)-1));
		}
		int result2=this.jdbcTemplate.update("INSERT INTO `by_money_log2` (`type`,`account_number`,`account_name`,`money`,`date`,`status`,`member_id`) SELECT ?,"
				+ "(CASE ? WHEN 1  THEN ? ELSE `bu`.`wx_openid` END),?,?,NOW(),1,? FROM `by_user` `bu` WHERE `bu`.`id`=?",new Object[]{type,type,zfbNo,zfbName,money,memberId,memberId});
//		int result3=this.jdbcTemplate.update("UPDATE `by_user` SET `zfb_no`=?,`zfb_name`=? WHERE `id`=?",new Object[]{zfbNo,zfbName,memberId});
		if(result1<=0 || result2<=0){
			throw new RuntimeException();
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String getTask(int memberId, int homeId,int statusflag) {
		//如果之前有任务  放弃掉
		String date=DateUtil.date2String(new Date(),DateUtil.FORMAT_DATETIME);
		int result1=this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`-1 WHERE `id`=? AND `remain_num`>0",new Object[]{homeId});
		int result2=this.jdbcTemplate.update("INSERT INTO `by_task` (`home_id`,`app_id`,`member_id`,`creatime`,`status`,`flag`) SELECT "+homeId+",`app_id`,"+memberId+",'"+date+"',1,"+statusflag+" FROM `by_home` WHERE `id`="+homeId+" AND NOT EXISTS (SELECT 1 FROM `by_task` WHERE `member_id`="+memberId+" AND `status`=1)");
		if(result1<=0 || result2<=0){
			throw new RuntimeException();
		}
		return date;
	}
	
	@Override
	public void giveUpAllTask(int memberId,int homeId) {
		Map<String, Object> map=new HashMap<>();
		try {
			map=this.jdbcTemplate.queryForMap("SELECT `id`,`home_id` FROM `by_task` WHERE `member_id`=? AND `status`=1 LIMIT 1", new Object[]{memberId});
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
	public void getMoneyByLogin(Integer memberId) {
		double directMoney=0;
		try {
			directMoney=this.jdbcTemplate.queryForObject("SELECT `money` FROM `by_invite_info` WHERE `key`='directMoney' LIMIT 1",Double.class);
		} catch (Exception e) {
		}
		if(directMoney!=0){
			this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`canfetch_money`=`canfetch_money`+?,`today_money`=`today_money`+? WHERE `id`=?",new Object[]{directMoney,directMoney,directMoney,memberId});
		}
	}
	
	@Override
	public void insertInvite(Integer inviteId, Integer memberId) {
		double inviteMoney=0,oneMoney=0,twoMoney=0,directMoney=0;  //邀请成功获得的金额 
		int count=0,oneCount=0,twoCount=0; //获取邀请金额的人数
		String key="";
		//增加邀请金额
		try {
			List<Map<String, Object>> map=this.jdbcTemplate.queryForList("SELECT `money`,`count`,`key` FROM `by_invite_info`");
			if(map!=null && map.size()>0){
				for (int i = 0; i < map.size(); i++) {
					key=(String)map.get(i).get("key");
					if("invite".equals(key)){  //邀请成功的钱
						inviteMoney=(double)map.get(i).get("money");
						count=(int)map.get(i).get("count");
					}else if("oneinvite".equals(key)){
						oneMoney=(double)map.get(i).get("money");
						oneCount=(int)map.get(i).get("count");
					}else if("twoinvite".equals(key)){
						twoMoney=(double)map.get(i).get("money");
						twoCount=(int)map.get(i).get("count");
					}else if("directMoney".equals(key)){
						directMoney=(double)map.get(i).get("money");
//						if(directMoney!=0){//登录奖励
//							this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`canfetch_money`=`canfetch_money`+? WHERE `id`=?",new Object[]{directMoney,directMoney,memberId});
//						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(inviteMoney!=0){
			//并且人数没有超过限制
			int firstNum = 0;
			try {
				firstNum=this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `by_invite_log` WHERE `member_id`=? AND `invite_money` !=0 AND `type`=1",new Object[]{inviteId}, Integer.class);
			} catch (Exception e) {
			
			}
			if(firstNum>=count){//无效
				inviteMoney=0;
			}else{
				//有效加钱
				this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`canfetch_money`=`canfetch_money`+?,`invite_money`=`invite_money`+?,`today_money`=`today_money`+? WHERE `id`=?",new Object[]{inviteMoney,inviteMoney,inviteMoney,inviteMoney,inviteId});
				//插入任务详情
				try {
					this.jdbcTemplate.update("INSERT INTO `by_task_info` (`member_id`,`invite_id`,`flag`,`money`,`time`) VALUES (?,?,2,?,NOW())",new Object[]{inviteId,memberId,inviteMoney});
				} catch (Exception e) {
					
				}
			}
		}
		
		/**
		 * 邀请人人数限制
		 */
		//插入一级邀请人
		this.jdbcTemplate.update("INSERT INTO `by_invite_log` (`member_id`,`invite_id`,`type`,`date`,`invite_money`) SELECT ?,?,1,NOW(),? FROM `by_user` WHERE (SELECT COUNT(*) FROM `by_invite_log` WHERE `member_id`=? AND `type`=1)<? LIMIT 1",new Object[]{inviteId,memberId,inviteMoney,inviteId,oneCount});
		try {
			Integer userId=this.jdbcTemplate.queryForObject("SELECT `member_id` FROM `by_invite_log` WHERE `invite_id`=? AND `type`=1 LIMIT 1",new Object[]{inviteId},Integer.class);
			//插入二级邀请人
			this.jdbcTemplate.update("INSERT INTO `by_invite_log` (`member_id`,`invite_id`,`second_id`,`type`,`date`) SELECT ?,?,?,2,NOW() FROM `by_user` WHERE (SELECT COUNT(*) FROM `by_invite_log` WHERE `member_id`=? AND `invite_id`=? AND `type`=2)<? LIMIT 1",new Object[]{userId,inviteId,memberId,inviteId,memberId,twoCount});
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void updateUserDefault(Integer memberId, Integer type, String money) {
		this.jdbcTemplate.update("UPDATE `by_user` SET `fetch_type`=? ,`fetch_money`=? WHERE `id`=?",new Object[]{type,money,memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryTryLogByPage(int memberId, int page, int number) {
		List<Map<String, Object>> result= this.jdbcTemplate.queryForList("SELECT `bt`.`app_id` AS `appId`,`bt`.`name` AS `title`,(SELECT `bh`.`img_url` FROM `by_home` `bh` WHERE `bh`.`app_id`=`bt`.`app_id` LIMIT 1) AS `imgUrl`,`bt`.`money`,DATE_FORMAT(`bt`.`date`,'%Y-%m-%d %H:%i:%s') AS `date` FROM `by_try_log` `bt`  WHERE `bt`.`member_id`=?  UNION SELECT '1' AS  `appId`,`adname` AS `title`,`channel` AS `imgUrl`,`money`,`time` AS `date` FROM `by_alliance` WHERE `member_id`=? ORDER BY `date` DESC LIMIT ?,?",new Object[]{memberId,memberId,page*number,number});
		return (result==null||result.size()<1)?null:result;
	}
	
	@Override
	public List<Map<String, Object>> queryDeposiyByPage(int memberId, int page, int depositNumber) {
		List<Map<String, Object>> result= this.jdbcTemplate.queryForList("SELECT `type`,`money`,`status`,DATE_FORMAT(`date`,'%Y-%m-%d %H:%i:%s') AS `date` FROM `by_money_log2` WHERE `member_id`=? ORDER BY `date` DESC LIMIT ?,?",new Object[]{memberId,page*depositNumber,depositNumber});
		return (result==null||result.size()<1)?null:result;
	}
	
	@Override
	public Map<String, Object> checkTask(Integer memberId) {
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `id`,`home_id` AS `homeId` FROM `by_task` WHERE `member_id`=? AND `status`=1", new Object[]{memberId});
		return (list==null || list.size()<=0)?null:list.get(0);
	}
	
	@Override
	public void boundDeviceToken(String idfa, String deviceToken,String version,String appId) {
		this.jdbcTemplate.update("UPDATE `by_user` SET `device_token`=?,`version`=?,`app_id`=? WHERE `idfa`=? ",new Object[]{deviceToken,version,appId,idfa});
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int giveUpTask(Integer memberId, Integer homeId) {
		int result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=3,`overtime`=NOW() WHERE `member_id`=? AND `home_id`=? AND `status`=1 LIMIT 1",new Object[]{memberId,homeId});
		if(result1>0){
			int result2=this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`+1 WHERE `id`=? AND `type`!=5 LIMIT 1",new Object[]{homeId});
			if(result1<=0){
				throw new RuntimeException();
			}
		}
		return result1;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int finishTask(Integer homeId, Integer memberId, Integer flag) {
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
					//处理一级邀请人
					this.updateOneInvite(memberId,limitMoney,orgMoney,oneinvite,homeId);
					//处理二级邀请人
					this.updateTwoInvite(memberId,limitMoney,orgMoney,twoinvite,homeId);
					//试玩记录
			        this.jdbcTemplate.update("INSERT INTO `by_try_log` (`app_id`,`date`,`member_id`,`name`,`money`) SELECT `app_id`,NOW(),?,`title`,? FROM `by_home` WHERE `id`=? LIMIT 1",new Object[]{memberId,orgMoney,homeId});
			        return 1;
				}
			}
		return 0;
	};
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int finishTask2(Integer homeId, Integer memberId, Integer flag) {
			if(flag==1){//有效任务
				//更新任务状态
//				int result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=5,`overtime`=NOW(),`flag`=? WHERE `home_id`=? AND `member_id`=? AND `status`!=5 AND `downloadtime` IS NOT NULL LIMIT 1",new Object[]{flag,homeId,memberId});
				//排除任务downloadTime是空  和已经完成的任务状态
				Map<String, Object> followMap=this.jdbcTemplate.queryForMap("SELECT `type`,`id` FROM `by_task` WHERE `home_id`=? AND `member_id`=? AND (`status`=1 OR `status`=6) AND NOT EXISTS (SELECT 1 FROM by_task WHERE (`status`=5 OR `status`=8 OR `status`=7) AND `home_id`=? AND member_id=?) ORDER BY `status` DESC LIMIT 1", new Object[]{homeId,memberId,homeId,memberId});
				int result1=0;
				Integer type=(Integer)followMap.get("type");
				Integer task_id=(Integer)followMap.get("id");
				if(type==2){//没有后续直接完成   回调任务
					result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=5,`overtime`=NOW() WHERE `id`=?",new Object[]{task_id});
				}else if(type==3 || type==4){//有后续 进入后续状态
					result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=8,`overtime`=NOW(),`is_follow`=1 WHERE `id`=? ",new Object[]{task_id});
				}
				if(result1>0){
					//查询邀请的限制
					List<Map<String, Object>> inviteNum=this.jdbcTemplate.queryForList("SELECT `key`,`money` FROM `by_invite_info` WHERE `key`='oneinvite' OR `key`='twoinvite' OR `key`='limit'");
					//查询任务的奖励金额
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
					//处理一级邀请人
					this.updateOneInvite(memberId,limitMoney,orgMoney,oneinvite,homeId);
					//处理二级邀请人
					this.updateTwoInvite(memberId,limitMoney,orgMoney,twoinvite,homeId);
					//试玩记录
			        this.jdbcTemplate.update("INSERT INTO `by_try_log` (`app_id`,`date`,`member_id`,`name`,`money`) SELECT `app_id`,NOW(),?,`title`,? FROM `by_home` WHERE `id`=? LIMIT 1",new Object[]{memberId,orgMoney,homeId});
			        if(type==2){
			        	JpushUtil.pushMessage(memberId+"", "您的奖励已到账!");
			        }else if(type==3 || type==4){
			        	JpushUtil.pushMessage(memberId+"", "您的奖励已到账,记得还有后续奖励哦~");
			        }
			        return 1;
				}
			}
		return 0;
	};
	
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
	public Map<String, Object> queryMoneyAndTime(int memberId) {
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `canfetch_money` AS `canfetchMoney`,DATE_FORMAT(`create_date`,'%Y-%m-%d') AS `createDate`,TIMESTAMPDIFF(DAY,`create_date`,NOW()) AS `dayDiff`,`zfb_no` AS `zfbNo`,`zfb_name` AS `zfbName` FROM `by_user` WHERE `id`=?",new Object[]{memberId});
		return (list==null || list.size()<=0)?null:list.get(0);
	}
	
	@Override
	public Map<String, Object> querySimInfo(Integer memberId) {
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `money`,`invite_money` AS `inviteMoney`,`today_money` AS `todayMoney`,`try_money` AS `tryMoney`,(SELECT COUNT(*) FROM `by_invite_log` WHERE `member_id`=?)AS `inviteNum`,`hadfetch_money` AS `hadfetchMoney`,`app_id` AS `appId`,`version` FROM `by_user` WHERE `id`=?",new Object[]{memberId,memberId});
		return (list==null || list.size()<=0)?null:list.get(0);
	}
	
	@Override
	public List<Map<String, Object>> queryIcons() {
		return this.jdbcTemplate.queryForList("SELECT `id`,`img_url` AS `imgUrl`,`title`,`info` FROM `by_icons` WHERE `status`=1 ORDER BY `sort` ASC");
	}
	
	@Override
	public Map<String, Object> queryDefault(int memberId) {
		List<Map<String, Object>> list= this.jdbcTemplate.queryForList("SELECT `fetch_type` AS `fetchType`,`fetch_money` AS `fetchMoney`,`canfetch_money` AS `canfetchMoney`,DATE_FORMAT(`create_date`,'%Y-%m-%d') AS `createDate`,`zfb_no` AS `zfbNo`,`zfb_name` AS `zfbName`  FROM `by_user` WHERE `id`=?",new Object[]{memberId});
		return (list==null || list.size()<=0)?null:list.get(0);
	}
	
	@Override
	public List<Map<String, Object>> queryInviteRecord(int memberId,int page,int number) {
		return this.jdbcTemplate.queryForList("SELECT  `bu`.`id`,`bu`.`name`,`bi`.`money` ,`bi`.`invite_money` AS `inviteMoney`,`bi`.`date` FROM  `by_invite_log` `bi`  INNER JOIN `by_user` `bu`  ON `bi`.`invite_id` = `bu`.`id` WHERE `bi`.`member_id` = ?  AND `bi`.`type` = 1   UNION ALL SELECT  `bu`.`id`,`bu`.`name`,`bi`.`money` ,`bi`.`invite_money` AS `inviteMoney`,`bi`.`date` FROM  `by_invite_log` `bi`  INNER JOIN `by_user` `bu`  ON `bi`.`second_id` = `bu`.`id` WHERE `bi`.`member_id` = ?  AND `bi`.`type` = 2  ORDER BY `date` DESC LIMIT ?,?",new Object[]{memberId,memberId,page*number,number});
	}
	
	@Override
	public List<Map<String, Object>> queryTaskInfo(int memberId,int inviteId, int page, int number) {
//		return this.jdbcTemplate.queryForList("SELECT `by`.`id` AS `id`,`by`.`title`,`bti`.`money`,DATE_FORMAT(`bti`.`time`,'%Y-%m-%d %H:%i:%s') AS `date`,`bti`.`flag` FROM `by_home` `by` INNER JOIN `by_task_info` `bti` ON `by`.`app_id`=`bti`.`app_id` WHERE `bti`.`flag`=1 AND `member_id`=? AND `invite_id`=? UNION ALL SELECT `bu`.`id` AS `id`,`bu`.`name` AS `title`,`bti`.`money`,DATE_FORMAT(`bti`.`time`,'%Y-%m-%d %H:%i:%s') AS `date`,`bti`.`flag` FROM `by_task_info` `bti` INNER JOIN `by_user` `bu` ON `bu`.`id`=`bti`.`invite_id` WHERE `bti`.`flag`=2 AND `member_id`=? AND `invite_id`=? ORDER BY `date` DESC LIMIT ?,?",new Object[]{memberId,inviteId,memberId,inviteId,page*number,number});
		return this.jdbcTemplate.queryForList("SELECT `bti`.`invite_id` AS `id`,`bti`.`money`,(SELECT `title` FROM `by_home` WHERE `app_id` =`bti`.`app_id` LIMIT 1)as `title`,DATE_FORMAT(`bti`.`time`,'%Y-%m-%d %H:%i:%s') AS `date`,`bti`.`flag` FROM  `by_task_info` `bti` WHERE `bti`.`flag`=1 AND `bti`.`member_id`=? AND `bti`.`invite_id`=? UNION ALL SELECT `bti`.`invite_id` AS `id`,`bu`.`name` AS `title`,`bti`.`money`,DATE_FORMAT(`bti`.`time`,'%Y-%m-%d %H:%i:%s') AS `date`,`bti`.`flag` FROM `by_task_info` `bti` INNER JOIN `by_user` `bu` ON `bu`.`id`=`bti`.`invite_id` WHERE `bti`.`flag`=2 AND `member_id`=? AND `invite_id`=? ORDER BY `date` DESC LIMIT ?,?",new Object[]{memberId,inviteId,memberId,inviteId,page*number,number});
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void OverDueTask(Integer memberId, Integer homeId) {
		int result1=this.jdbcTemplate.update("UPDATE `by_task` SET `status`=4,`overtime`=NOW() WHERE `member_id`=? AND `home_id`=? AND `status`=1 LIMIT 1",new Object[]{memberId,homeId});
		this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`+1 WHERE `id`=? AND `type`!=5 LIMIT 1",new Object[]{homeId});
		if(result1<=0){
			throw new RuntimeException();
		}
	}
	
	@Override
	public Map<String, Object> queryMessageInfo(Integer homeId, Integer memberId) {
		try {
			String deviceToken =  this.jdbcTemplate.queryForObject("SELECT `device_token` AS `deviceToken` FROM `by_user` WHERE `id`=?",new Object[]{memberId},String.class);
			Map<String, Object> homeInfo= this.jdbcTemplate.queryForMap("SELECT `title`,`money` FROM `by_home` WHERE `id`=?",new Object[]{homeId});
			if(StringUtils.isNotBlank(deviceToken) && homeInfo!=null && homeInfo.size()>0){
				homeInfo.put("deviceToken", deviceToken);
				return homeInfo;
			}
		} catch (Exception e) {
			
		}
		return null;
	}
	
	@Override
	public boolean isCanFinish(Integer homeId, Integer memberId) {
		try {
			//接任务的时间   需要打开的时间
			int timeDiff=this.jdbcTemplate.queryForObject("SELECT `time` FROM `by_home` WHERE `id`=?",new Object[]{homeId}, Integer.class);
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_task` WHERE `home_id`=? AND `member_id`=? AND `status`=1 AND TIMESTAMPDIFF(SECOND,`downloadtime`,NOW())>=?",new Object[]{homeId,memberId,timeDiff-30}, Integer.class);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public List<Map<String, Object>> queryForInviteMoney(int memberId) {
		return this.jdbcTemplate.queryForList("SELECT `bu`.`name`,`bi`.`invite_money` FROM `by_invite_log` `bi` INNER JOIN `by_user` `bu` ON `bi`.`invite_id`=`bu`.`id` WHERE `bi`.`member_id`=? AND `bi`.`type`=1 AND `bi`.`invite_money`!=0",new Object[]{memberId});
	}
	
	@Override
	public List<Map<String, Object>> queryInviteScale(Integer memberId) {
		return this.jdbcTemplate.queryForList("SELECT `key`,`money` FROM `by_invite_info` WHERE `key`='oneinvite' OR `key`='twoinvite' OR `key`='invite' OR `key`='limit'");
	}
	
	@Override
	public String queryTicket(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `ticket` FROM `by_barcode` WHERE `member_id`=? AND TIMESTAMPDIFF(DAY,`date`,NOW())<=6",new Object[]{memberId}, String.class);
		} catch (Exception e) {
			return null;	
		}
	}
	
	@Override
	public List<Map<String, Object>> queryOverDueTask(int time) {
		return this.jdbcTemplate.queryForList("SELECT `member_id` AS `memberId`,`home_id` AS `homeId` FROM `by_task` WHERE TIMESTAMPDIFF(SECOND,`creatime`,NOW())>=? AND `status`=1 AND `type`!=5",new Object[]{time});
	}
	
	@Override
	public void downTask(Integer memberId,Integer flag,String ip) throws Exception {
		this.jdbcTemplate.update("UPDATE `by_task` SET `downloadtime`=NOW(),`flag`=?,`ip`=? WHERE `status`=1 AND `member_id`=? LIMIT 1",new Object[]{flag,ip,memberId});
	}
	
	@Override
	public Double queryForInviteDire() {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `money` FROM `by_invite_info` WHERE `key`='invite' AND `count`>0", Double.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void insertIdfaBlackList(String idfa) {
		this.jdbcTemplate.update("INSERT INTO `by_blacklist` (`idfa`,`time`) VALUES (?,NOW()) ON DUPLICATE KEY UPDATE `time`=NOW()",new Object[]{idfa});
	}
	
	@Override
	public int boundUdid(String memberId, String udid) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `udid`=?",new Object[]{udid}, Integer.class);
			return 0;
		} catch (Exception e) {
			return this.jdbcTemplate.update("UPDATE `by_user` SET `udid`=? WHERE `id`=? ",new Object[]{udid,memberId});
		}
	}
	
	@Override
	public String queryUdid(String memberId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `udid` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean checkMobile(String mobile) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `mobile`=?",new Object[]{mobile}, Integer.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean boundMobile(String mobile, Integer memberId) {
		try {
			int num= this.jdbcTemplate.update("UPDATE `by_user` SET `mobile`=?,`create_mobile`=NOW() WHERE `id`=? ",new Object[]{mobile,memberId});
			if(num>0){
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public String checkMobileAndMember(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `mobile` FROM `by_user` WHERE `id`=? LIMIT 1",new Object[]{memberId}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String queryDeviceToken(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `device_token` AS `deviceToken` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Map<String, Object> getUserInfoByIdfa(String idfa) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `id`,`img_url` AS `imgUrl`,`name`"
				+ " FROM `by_user` WHERE `status`=1 AND `idfa`=? LIMIT 1");
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList(sql.toString(),new Object[]{idfa});
		return (list==null||list.size()<=0)?null:list.get(0);
	}
	
	@Override
	public Map<String, Object> information(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `img_url` AS `imgUrl`,`wx_openid` AS `wxOpenid`,`mobile`,`zfb_no` AS `zfbNo`,`udid`,`name`,`sex`,`profession`,DATE_FORMAT(`birthday`,'%Y-%m-%d') AS `birthday` FROM `by_user` WHERE `id`=? LIMIT 1",new Object[]{memberId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public int editInfo(Integer memberId, String name, Integer sex, String birthday, String profession,String path) {
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("UPDATE `by_user` SET `name`=?,`sex`=?,`birthday`=?,`profession`=?");
			if(StringUtils.isNotBlank(path)){
				sql.append(",`img_url`='"+path+"'");
			}
			sql.append(" WHERE `id`=?");
			return this.jdbcTemplate.update(sql.toString(),new Object[]{name,sex,birthday,profession,memberId});
		} catch (Exception e) {
			return 0;
		}
		
	}
	
	@Override
	public boolean checkInfo(Integer memberId) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `id`=? AND `mobile` IS NOT NULL AND `mobile` !='' AND `udid` IS NOT NULL AND `udid` !='' AND `wx_openid` IS NOT NULL AND `wx_openid` !=''",new Object[]{memberId}, Integer.class);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkIsBoundWx(Integer memberId) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `wx_openid` IS NOT NULL AND `wx_openid` !='' WHERE `id`=?",new Object[]{memberId}, Integer.class);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public Integer updateWxByCode(Integer memberId, String code) {
		try {
			int result1=this.jdbcTemplate.update("UPDATE `by_user` `bu` INNER JOIN (SELECT `openid`,`unionid` FROM `by_wxunionid` WHERE `status`=1 AND TIMESTAMPDIFF(MINUTE,`time`,NOW())<=3 AND `code`=?) `bx` SET `bu`.`wx_openid`=`bx`.`openid`,`bu`.`union_id`=`bx`.`unionid` WHERE `bu`.`id`=?",new Object[]{code,memberId});
			if(result1>0){
				return this.jdbcTemplate.update("UPDATE `by_wxunionid` SET `status`=0 WHERE `code`=? LIMIT 1",new Object[]{code});
			}
		} catch (Exception e) {
		}
		return 0;
	}
	
	@Override
	public boolean checkMemberById(Integer memberId) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `id`=? LIMIT 1",new Object[]{memberId}, Integer.class);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkOnlyAppleId(Integer memberId) {
		try {
			Integer result=this.jdbcTemplate.queryForObject("SELECT `isapple_id` FROM `by_home` `bh` INNER JOIN `by_task` `bt` ON `bh`.`id`=`bt`.`home_id` WHERE `bt`.`member_id`=? AND `bt`.`status`=1 LIMIT 1",new Object[]{memberId}, Integer.class);
			if(result==1){
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	
	@Override
	public Map<String, Object> queryTaskFlagStatus(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `bh`.`first_task` AS `firstTask`,`bt`.`type`,`bh`.`is_active` AS `isActive`,`bt`.`flag`,`bt`.`home_id` AS `homeId`,`bh`.`active_url` AS `activeUrl`,`bh`.`isapple_id` AS `isappleId`,`bu`.`idfa`,`bt`.`status` FROM `by_task` `bt` LEFT JOIN `by_user` `bu` ON `bu`.`id`=`bt`.`member_id` LEFT JOIN `by_home` `bh` ON `bt`.`home_id`=`bh`.`id` WHERE `bt`.`member_id`=? AND `bt`.`status`=1 LIMIT 1",new Object[]{memberId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> queryLatestTask(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`title`,`bh`.`key`,`bh`.`img_url` AS `imgUrl`,`bc`.`name`,`bh`.`money`,DATE_FORMAT(`bh`.`date`,'%Y-%m-%d %H:%i') AS `date`,`bh`.`remain_num` AS `remainNum`,`bh`.`color`  FROM `by_home` `bh` LEFT JOIN `by_cat` `bc` ON `bh`.`cat`=`bc`.`id` WHERE `bh`.`status`=1 AND `bh`.`date`>NOW() AND `bh`.`remain_num`!=0 AND `bh`.`first_task`=0 ORDER BY `bh`.`date` ASC");
		return this.jdbcTemplate.queryForList(sql.toString());
	}
	
	@Override
	public Map<String, Object> queryIdfaUrl(Integer homeId) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `bh`.`repeat_url` AS `repeatUrl`,`bh`.`app_id` AS `appId`,`bh`.`repeat_result` AS `repeatResult`,`bh`.`repeat_param` AS `repeatParam`,`bh`.`is_active` AS `isActive`,`bh`.`type` FROM `by_home` `bh`  WHERE  `bh`.`id`=? LIMIT 1 ",new Object[]{homeId});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public List<Map<String, Object>> queryfinishedTask(Integer memberId) {
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT `bh`.`id`,`bh`.`title`,`bh`.`img_url` AS `imgUrl`,`bc`.`name`,`bh`.`money`,DATE_FORMAT(`bh`.`date`,'%Y-%m-%d %H:%i') AS `date`,`bh`.`remain_num` AS `remainNum`  FROM `by_home` `bh` LEFT JOIN `by_cat` `bc` ON `bh`.`cat`=`bc`.`id` INNER JOIN `by_task` `bt` ON `bh`.`id`=`bt`.`home_id` WHERE `bt`.`status`=5 AND `bt`.`member_id`=? AND `bh`.`first_task`=0 ORDER BY `bt`.`overtime` DESC LIMIT 8");
		return this.jdbcTemplate.queryForList(sql.toString(),new Object[]{memberId});
	}
	
	@Override
	public void clearTodayMoney() {
		this.jdbcTemplate.update("UPDATE `by_user` SET `today_money`=0");
	}
	
	@Override
	public boolean isActive(String idfa) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `idfa`=? LIMIT 1",new Object[]{idfa}, Integer.class);
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	@Override
	public Map<String, Object> getHub(String appleid) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `scheme_url` AS `schemeUrl`,`weixin`,`version`,`level`,`apple_id` AS `appleId`,`name`,`download_url` AS `downloadUrl`,`is_dialog` AS `isDialog` FROM `by_sys_hub` WHERE `apple_id`=? LIMIT 1",new Object[]{appleid});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String getSchemeUrl(String appleid) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `scheme_url` AS `schemeUrl` FROM `by_sys_hub` WHERE `apple_id`=? LIMIT 1",new Object[]{appleid}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Map<String, Object> getHubBydefault() {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `scheme_url` AS `schemeUrl`,`download_url` AS `downloadUrl` FROM `by_sys_hub` WHERE `ismajor`=1 LIMIT 1");
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public boolean checkMemberAndIdfa(int memberId, String idfa) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `id`=? AND `idfa`=? LIMIT 1",new Object[]{memberId,idfa}, Integer.class);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public boolean checkActive(Integer memberId, Integer homeId) {
		//idfa  appleid
		try {
			Map<String, Object> map=this.jdbcTemplate.queryForMap("SELECT `bu`.`idfa`,`bt`.`app_id` AS `appId` FROM `by_user` `bu` INNER JOIN `by_task` `bt` ON `bt`.`member_id`=`bu`.`id` WHERE `bt`.`status`=1 AND `bt`.`member_id`=?",new Object[]{memberId});
			String idfa=(String)map.get("idfa");
			String appId=String.valueOf(map.get("appId"));
			int status=0;
			if(appId.equals("1013473179") || appId.equals("1086983070") || appId.equals("1039727598") || appId.equals("990918949") || appId.equals("1015752631")
					|| appId.equals("1088544614") || appId.equals("1088543974")|| appId.equals("1088544360")|| appId.equals("1085440949")|| appId.equals("1105250240") || appId.equals("1130823748")){
				try {
					this.statsTemplate.queryForObject("SELECT `id` FROM `t_idfa` WHERE `idfa`=? AND `app_id`=? LIMIT 1",new Object[]{idfa,appId}, Integer.class);
					status=1;
				} catch (Exception e) {
					status=0;
				}
			}else{
				status=this.statsTemplate.queryForObject("SELECT `ci`.`status` FROM `cpa_idfa` `ci` INNER JOIN `cpa_appid` `ca` ON `ca`.`id`=`ci`.`appid` WHERE `ci`.`idfa`=? AND `ca`.`appleid`=? LIMIT 1",new Object[]{idfa,appId}, Integer.class);
			}
			if(status==1){
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public void updateStatus(Integer memberId, Integer homeId) {
		Map<String, Object> map=this.jdbcTemplate.queryForMap("SELECT `bu`.`idfa`,`bt`.`app_id` AS `appId` FROM `by_user` `bu` INNER JOIN `by_task` `bt` ON `bt`.`member_id`=`bu`.`id` WHERE `bt`.`status`=1 AND `bt`.`member_id`=?",new Object[]{memberId});
		String idfa=(String)map.get("idfa");
		String appId=String.valueOf(map.get("appId"));
		this.jdbcTemplate.update("UPDATE `cpa_idfa` `ci` SET `ci`.`status`=1 INNER JOIN `cpa_appid` `ca` ON `ca`.`id`=`ci`.`appid` WHERE `ci`.`idfa`=? AND `ca`.`appleid`=? LIMIT 1",new Object[]{idfa,appId});
	}
	
	@Override
	public boolean checkZfb(Integer memberId, String zfbName, String zfbNo) {
		try {
			int result=this.jdbcTemplate.update("UPDATE `by_user` SET `zfb_no`=?,`zfb_name`=? WHERE `id`=? AND `mobile`=?",new Object[]{zfbNo.trim(),zfbName,memberId,zfbNo.trim()});
			if(result>0){
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public int queryActiveStatus(Integer homeId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `is_active` AS `isActive` FROM `by_home` WHERE `id`=? LIMIT 1",new Object[]{homeId}, Integer.class);
		} catch (Exception e) {
		}
		return 0;
	}
	
	@Override
	public int queryStatus(Integer memberId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `status` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, Integer.class);
		} catch (Exception e) {
		}
		return 0;
	}
	
	@Override
	public String getIdfa(String memberId) {
		return this.jdbcTemplate.queryForObject("SELECT `idfa` FROM `by_user` WHERE `id`=?",new Object[]{memberId}, String.class);
	}
	
	@Override
	public String getDeviceToken(int i) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `device_token` FROM `by_user` WHERE `id`=?",new Object[]{i}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Map<String, Object> getHub(String appleid, String version) {
		try {
			return this.jdbcTemplate.queryForMap("SELECT `level`,`is_dialog` AS `isDialog`,`version`,`download_url` AS `downloadUrl` FROM `by_sys_hub` WHERE `apple_id`=? AND `version`=? LIMIT 1",new Object[]{appleid,version});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Integer queryMemberIdByidfa(String idfa) {
		return this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `idfa`=? LIMIT 1",new Object[]{idfa}, Integer.class);
	}
	
	@Override
	public Integer queryHomeIdByAppleid(String appleid) {
		return this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_home` WHERE `app_id`=? ORDER BY `date` DESC LIMIT 1 ",new Object[]{appleid}, Integer.class);
	}
	
	@Override
	public void desHomeNum(Integer homeId) {
		this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`-1 WHERE `id`=? AND `remain_num`>0",new Object[]{homeId});
	}

	@Override
	public List<Map<String, Object>> queryDelayTask() {
		List<Map<String, Object>> list= this.jdbcTemplate.queryForList("SELECT DISTINCT (`bt`.`member_id`) AS `memberId`, `bt`.`home_id` AS `homeId`,`bu`.`idfa`,`bt`.`app_id` AS `appId` FROM `by_task` `bt`  INNER JOIN `by_user` `bu` ON `bu`.`id`=`bt`.`member_id` WHERE `bt`.`status` =6  AND TIMESTAMPDIFF(DAY,DATE_FORMAT(`bt`.`downloadtime`,'%Y-%m-%d'),CURDATE())<=2");
		return (list==null || list.size()<=0)?null:list;
	}
	
	@Override
	public Integer queryCpaStatus(String idfa, Integer appId) {
		try {
			return this.statsTemplate.queryForObject("SELECT `ci`.`status` FROM `cpa_idfa` `ci` INNER JOIN `cpa_appid` `ca` ON `ca`.`id`=`ci`.`appid` WHERE `ci`.`idfa`=? AND `ca`.`appleid`=? LIMIT 1",new Object[]{idfa,appId}, Integer.class);
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	public String desHomeNum(String appleid) {
		int result=this.jdbcTemplate.update("UPDATE `by_home` SET `remain_num`=`remain_num`-1 WHERE `app_id`=? AND `status`=1 AND `remain_num`>=1 ORDER BY `date` DESC LIMIT 1",new Object[]{appleid});
		System.err.println("desHomeNum:"+result+",数量减少");
		if(result==0){
			return String.valueOf(this.jdbcTemplate.update("UPDATE `by_task` SET `status`=3 WHERE `app_id`=? AND `status`=1 ORDER BY `creatime` ASC LIMIT 1",new Object[]{appleid}));
		}else{
			return String.valueOf(result);
		}
	}
	
	@Override
	public boolean isWall(String appleid, String idfa) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `bt`.`id` FROM `by_task` `bt` INNER JOIN `by_user` `bu` ON `bt`.`member_id`=`bu`.`id` WHERE `bt`.`app_id`=? AND `bu`.`idfa`=? LIMIT 1",new Object[]{appleid,idfa}, Integer.class);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
