package cn._51app.service.impl;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import cn._51app.service.AllianceSercice;

/**
 * @author Administrator 用户管理
 */
@Controller
public class AllianceServiceImpl implements AllianceSercice {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Value("#{pValue['baby.url']}")
	private String url;// 图片显示根目录
	private ObjectMapper mapper=new ObjectMapper();
	
	@Override
	public boolean checkOrder(String order_id) {
		try {
			this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_alliance` WHERE `order_id`=?",new Object[]{order_id}, Integer.class);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public void insertRecord(String app_id, String activate_time, String bill, String order_id, String flag,String memberId,String adname) {
		this.jdbcTemplate.update("INSERT INTO `by_alliance` (`member_id`,`appleid`,`time`,`money`,`order_id`,`channel`,`adname`) VALUES(?,?,?,?,?,?,?)",new Object[]{memberId,app_id,activate_time,bill,order_id,flag,adname});
	}
	
	@Override
	public void updateUser(String memberId, String bill) {
		this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`+?,`today_money`=`today_money`+?,`canfetch_money`=`canfetch_money`+?,`try_money`=`try_money`+? WHERE `id`=?",new Object[]{bill,bill,bill,bill,memberId});
	}
	
	@Override
	public String allianceList() throws Exception{
		List<Map<String, Object>> list=this.jdbcTemplate.queryForList("SELECT `name`,`info`,`key`,`img_url` AS `imgUrl` FROM `by_alliance_list` WHERE `status`=1 ORDER BY `sort` ASC");
		if(list==null || list.size()<=0){
			return null;
		}
		for (int i = 0; i < list.size(); i++) {
			String imgUrl=(String)list.get(i).get("imgUrl");
			list.get(i).put("imgUrl", (imgUrl==null)?"":url+imgUrl);
		}
		return mapper.writeValueAsString(list);
	}
}

