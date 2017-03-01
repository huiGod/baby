package cn._51app.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn._51app.dao.GoodsDao;
import cn._51app.utils.PropertiesUtil;
import net.rubyeye.xmemcached.MemcachedClient;

@Repository
public class GoodsDaoImpl implements GoodsDao{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private JdbcTemplate diyTemplate; 
	@Autowired
	protected NamedParameterJdbcTemplate npjt;
	@Autowired
	private MemcachedClient mc;
	private DecimalFormat df =new DecimalFormat("######0.00");
	private ObjectMapper mapper=new ObjectMapper();
	private final String SYSPATH =PropertiesUtil.getValue("diy.goods.url");
	
	public String q(String k) throws Exception{
		return mc.get(k);
	};
	
	public boolean add(String k, String v, int time) throws Exception{
		return mc.set(k,time,v);
	};
	
	public boolean del(String k) throws Exception{
		return mc.delete(k);
	};
	
	public String isCacheNull(String cacheKey) throws Exception{
		
		try {
			String json =q(cacheKey);
			if (json == null || json.equals("")){
				return "a";
			}else{
				return "b";
			}
		} catch (Exception e) {
			//缓存异常
			return "c";
		}
	}
	
	@Override
	public List<Map<String, Object>> getBanner() {
		return this.jdbcTemplate.queryForList("SELECT `id`,`img_url` AS `imgUrl`,`title`,`url`,`appleid`  FROM `by_banner` WHERE `status`=1 ORDER BY `sort` ASC");
	}
	
	@Override
	public String nice(Map<String, Object> paramMap) throws Exception{
		String cacheKey =paramMap.get("cacheKey").toString();
		String sql = "SELECT dgi.id,dgi.name,dgi.icoUrl,tex.baby_now_price,ROUND(dgi.transportfee,2) as transportfee,sell"
				+ " FROM diy_goods_info dgi INNER JOIN diy_info_texture tex ON dgi.id=tex.info_id"
				+ " WHERE dgi.isBoutique=1 AND tex.isdefault=1 AND dgi.baby_show=1 ORDER BY dgi.baby_sort LIMIT :page,:pagesize";
		if(isCacheNull(cacheKey).equals("a")){
			
			List<Map<String,Object>> lm =npjt.queryForList(sql, paramMap);
			 if(!lm.isEmpty()){
				String dgurl =paramMap.get("dgurl").toString();
				for(Map<String,Object> m:lm){
					Object obj =m.get("icoUrl");
					if(obj!=null){
						m.put("icoUrl", dgurl+obj);
					}
					//格式化价格
					m.put("now_price",df.format(m.get("baby_now_price")));
					
					String transportfee=(null==m.get("transportfee")?"":m.get("transportfee").toString());
					if(transportfee.equals("0.0")){
						m.put("transportfee","免运费");
					}else{
						m.put("transportfee","运费  ￥"+transportfee);
					}
				} 
				int cacheTime =new Integer(paramMap.get("cacheTime").toString());
				String json =saveAndGet(lm, cacheKey, cacheTime);
				if(json!=null){
					return json;
				}
				return mapper.writeValueAsString(lm);
			 
			 }
			
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}else{
			List<Map<String,Object>> lm =npjt.queryForList(sql, paramMap);
			 if(!lm.isEmpty()){
				String dgurl =paramMap.get("dgurl").toString();
				for(Map<String,Object> m:lm){
					Object obj =m.get("icoUrl");
					if(obj!=null){
						m.put("icoUrl", dgurl+obj);
					}
					String transportfee=(null==m.get("transportfee")?"":m.get("transportfee").toString());
					if(transportfee.equals("0.0")){
						m.put("transportfee","免运费");
					}else{
						m.put("transportfee","运费  ￥"+transportfee);
					}
				} 
				return mapper.writeValueAsString(lm);
			 }
		}
		return "";
	}
	
	protected String saveAndGet(Object data,String cacheKey,int cacheTime) throws Exception{
		String json = mapper.writeValueAsString(data);
		if(!add(cacheKey, json,cacheTime)){
			throw new RuntimeException();
		}
		return q(cacheKey);
	}
	
	@Override
	public String youGoods(Map<String, Object> paramMap) throws Exception{
		String cacheKey = paramMap.get("cacheKey").toString();
		String sql = "SELECT dgi.id,dgi.title,dgi.`previewImgUrl`,dgi.transportfee,dgi.recommend,tex.org_price,tex.baby_now_price,tex.texture_ids,dgi.isBoutique,dgi.h5url,tex.`pre_url` " +
				"FROM diy_goods_info dgi INNER JOIN diy_info_texture tex ON dgi.id =tex.info_id " +
				"WHERE dgi.id=? AND tex.isdefault=1 AND dgi.baby_show=1 ";
		if(isCacheNull(cacheKey).equals("a")||isCacheNull(cacheKey).equals("c")){
			int cacheTime = new Integer(paramMap.get("cacheTime").toString());
			Map<String, Object> map = diyTemplate.queryForMap(sql,new Object[]{paramMap.get("id")});
			if(!map.isEmpty()){
				String dgurl =paramMap.get("dgurl").toString();
				//推荐商品，用逗号隔开
				String recommend=map.get("recommend")==null?"":map.get("recommend").toString();
					Object obj =map.get("previewImgUrl");
					if(obj!=null){
						map.put("previewImgUrl", dgurl+obj);
					}
					Object pre_url =map.get("pre_url");
					if(pre_url!=null){
						map.put("pre_url", dgurl+pre_url);
					}
					Object h5url=map.get("h5url");
					if(h5url!=null){
						map.put("h5url", dgurl+h5url);
					}
					//格式化价格
					map.put("now_price",df.format(map.get("baby_now_price")));
					map.put("org_price",df.format(map.get("org_price")));
					String transportfee=(null==map.get("transportfee")?"":map.get("transportfee").toString());
					if(transportfee.equals("0.0")){
						map.put("transportfee","免运费");
					}else{
						map.put("transportfee","运费  ￥"+transportfee);
					}
					//查询材质
					List<String> lt = new ArrayList<>();
					String textureIds=map.get("texture_ids")!=null?map.get("texture_ids").toString():"未选择";
					String arrIds[]=textureIds.split("_");
					for(int i=0;i<arrIds.length;i++){
						Map<String,Object> tmap=new HashMap<String,Object>();
						tmap.put("id",arrIds[i]);
						String param=getSelectTexture(tmap);
						if(i==arrIds.length-1){
							lt.add(param);
						}else{
							lt.add(param+",");
						}
					}
					map.put("paramList", lt);
					map.put("recommend",getGoods(recommend,dgurl));
				}
			if(isCacheNull(cacheKey).equals("a"))
				return saveAndGet(map, cacheKey, cacheTime);
			else
				return mapper.writeValueAsString(map);
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}
		return "";
		
	}
	
	/**
	 * 返回材质属性
	 */
	@Override
	public String getSelectTexture(Map<String, Object> paramMap) throws Exception {
		String sql="SELECT name FROM diy_goods_texture WHERE id=:id";
		return npjt.queryForObject(sql, paramMap,String.class);
	}
	
	//推荐商品详情查询以及属性
	private List<Map<String,Object>>getGoods(String recommend,String dgurl){
		String arr[]=recommend.split(",");
		String sql="SELECT dgi.id,dgi.title,dgi.`previewImgUrl`,dgi.transportfee,dgi.recommend,tex.org_price,tex.baby_now_price,tex.texture_ids ,dgi.good_id,dgi.isBoutique "
				+ "FROM diy_goods_info dgi INNER JOIN diy_info_texture tex ON dgi.id =tex.info_id  "
				+ "WHERE dgi.id=? AND tex.isdefault=1";
		List<Map<String,Object>> reList=new ArrayList<Map<String,Object>>();
		if(arr[0].equals("")){
			 return null;
		}
		for(int i=0;i<arr.length;i++){
			if(diyTemplate.queryForList(sql,arr[i]).isEmpty()){
				continue;
			}
			Map<String,Object>map=diyTemplate.queryForList(sql,arr[i]).get(0);
			//改变图片前缀
			Object obj =map.get("previewImgUrl");
			if(obj!=null){
				map.put("previewImgUrl", dgurl+obj);
			}
			//改变价格
			map.put("now_price",df.format(map.get("baby_now_price")));
			String transportfee=(null==map.get("transportfee")?"":map.get("transportfee").toString());
			if(transportfee.equals("0.0")){
				map.put("transportfee","免运费");
			}else{
				map.put("transportfee","运费  ￥"+transportfee);
			}
			reList.add(map);
		}
		return reList;
	}
	
	@Override
	public String getGoodsShow(Map<String, Object> paramMap) throws Exception{
		String cacheKey =paramMap.get("cacheKey").toString();
		Map<String,Object> result = new HashMap<String, Object>();
		if(isCacheNull(cacheKey).equals("a")||isCacheNull(cacheKey).equals("c")){
			int cacheTime = new Integer(paramMap.get("cacheTime").toString());
			String dgurl =paramMap.get("dgurl").toString();
			int id = new Integer(paramMap.get("id").toString());
			String infosql = "SELECT texture_ids,org_price,baby_now_price,pre_url,box_url,save_size FROM `diy_info_texture` WHERE info_id = ? ";
			String infoend = " ORDER BY sort";
			//查询商品的所有可能的属性
			List<Map<String,Object>> info = diyTemplate.queryForList(infosql+infoend,new Object[]{id});
			String first = "";
			String second = "";
			int index = 0;
			for (Map<String, Object> map : info) {
				String pre_url = null==map.get("pre_url")?"":map.get("pre_url").toString();
				String box_url = null==map.get("box_url")?"":map.get("box_url").toString();
				String saveSize = null==map.get("save_size")?"":map.get("save_size").toString();
				//价格格式化
				map.put("org_price",df.format(map.get("org_price")));
				map.put("now_price",df.format(map.get("baby_now_price")));
				map.put("save_size", saveSize);
				if(StringUtils.isNotEmpty(pre_url))
					map.put("pre_url", dgurl+pre_url);
				
				if(StringUtils.isNotEmpty(box_url))
					map.put("box_url",dgurl+box_url);
				//获取材质
				String texture_ids = null==map.get("texture_ids")?"":map.get("texture_ids").toString();
				String[] f = texture_ids.split("_");
				index = f.length;
				//根据属性长度决定层级
				if(index==1){
					first+=f[0]+",";
				}else if(index==2){
					first+=f[0]+",";
					second+=f[1]+",";
				}
			}
			String gsql = "SELECT gt.id,gt.name AS gname,g.`name` AS title FROM `diy_goods_texture` gt INNER JOIN `diy_good` g ON gt.`texture_type`=g.`id` ";
			String end = " ORDER BY gt.`sort` ";
			Map<String,Object> tempMap = null;
			//查出一级属性列表
			if(index==1){
				String firstsql = gsql+" AND gt.id in ("+first.substring(0, first.length()-1)+")";
				List<Map<String,Object>> fristList = diyTemplate.queryForList(firstsql+end);
				//查出属性类型（款式\型号...）
				String title = fristList.get(0).get("title").toString();
				tempMap = new HashMap<String, Object>();
				tempMap.put("title", title);	
				tempMap.put("list", fristList);
				result.put("listA", tempMap);
			}else if(index==2){
				String firstsql =  gsql+" AND gt.id in ("+first.substring(0, first.length()-1)+")";
				List<Map<String,Object>> fristList = diyTemplate.queryForList(firstsql+end);
				String title = fristList.get(0).get("title").toString();
				tempMap = new HashMap<String, Object>();
				tempMap.put("title", title);
				tempMap.put("list", fristList);
				result.put("listA", tempMap);
				
				String secondsql =  gsql+" AND gt.id in ("+second.substring(0, second.length()-1)+")";
				List<Map<String,Object>> secondList = diyTemplate.queryForList(secondsql+end);
				String titleB = secondList.get(0).get("title").toString();
				tempMap = new HashMap<String, Object>();
				tempMap.put("title", titleB);
				tempMap.put("list", secondList);
				result.put("listB", tempMap);
				
			}
			result.put("texture", info);
			
			infosql+=" and isdefault=1";
			Map<String,Object> defaul = diyTemplate.queryForList(infosql+infoend,new Object[]{id}).get(0);//默认显示
			String storesql = "select user_id,goodsType from diy_goods_info where id="+id;
			Map<String,Object> store = diyTemplate.queryForMap(storesql);
			result.put("now_price",df.format(defaul.get("baby_now_price")));
			result.put("org_price",df.format(defaul.get("org_price")));
			result.put("pre_url", dgurl+defaul.get("pre_url"));
			result.put("save_size", null==defaul.get("save_size")?"":defaul.get("save_size").toString());
			result.put("id", id);
			result.put("storeId", null==store.get("user_id")?"":store.get("user_id").toString());
			result.put("goodsType", null==store.get("goodsType")?"":store.get("goodsType").toString());
			if(isCacheNull(cacheKey).equals("a"))
				return saveAndGet(result, cacheKey, cacheTime);
			else{
				return mapper.writeValueAsString(result);
			}
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}
		return "";
	}
	
	@Override
	public String getGoodsDetails(Map<String, Object> paramMap) throws Exception{
		//获取缓存名称
				String cacheKey =paramMap.get("cacheKey").toString();
				//sql语句
				String select ="SELECT id,introduce,parameter,packAfterSale,priceNote";
				String from =" FROM diy_goods_info";
				String where =" WHERE id=:id";
				String sql =select+from+where;
				//设置查询sql返回容器
				Map<String,Object> m =null;
				//使用isCacheNull查看缓存空为a,异常为c,正常为b
				if(isCacheNull(cacheKey).equals("a")){
					
					try {
						//查询数据库获取数据
						m =npjt.queryForMap(sql, paramMap);
						
					//无数据空查询异常
					} catch (EmptyResultDataAccessException e) {
						return null;
					}
					//获取到数据执行
					if(m!=null){
						//设置返回的Map类型容器
						Map<String,Object> responseMap =new HashMap<String,Object>();
						//获取多张定制介绍图片，逗号分割
						String[] introduceArr =(m.get("introduce").toString()).split(",");
						//以&分开，表示每一行的数据，获取商品参数
						String[] parameterArr1 =(m.get("parameter").toString()).split("&");
						//设置lmTmp2的List来存放每一行数据
						List<Map<String,Object>> lmTmp2 =new ArrayList<Map<String,Object>>();
						//循环放数据到lmTmp2
						for(int i=0;i<parameterArr1.length;i++){
							//再次拆分数据，以#分开
							String[] parameterArr1_1Arr =(parameterArr1[i]).split("#");
							//如果长度不是2个长度，就不读取这条数据
							if(parameterArr1_1Arr.length!=2)break;
							//设置title作为#分开的数据1
							String title =parameterArr1_1Arr[0];
							//设置txt作为#分开的数据2
							String txt =parameterArr1_1Arr[1];
							//封装到Map
							Map<String,Object> mTmp =new HashMap<String,Object>();
							//放到map
							mTmp.put("title", title);
							mTmp.put("txt", txt);
							//添加到List
							lmTmp2.add(mTmp);
						}
						//获取包装售后
						String[] packAfterSaleArr1 =(m.get("packAfterSale").toString()).split("&");
						List<Map<String,Object>> lmTmp3 =new ArrayList<Map<String,Object>>();
						//设置lmTmp3的List来存放每一行数据
						for(int i=0;i<packAfterSaleArr1.length;i++){
							//再次拆分数据，以#分开
							String[] packAfterSaleArr1_1Arr =(packAfterSaleArr1[i]).split("#");
							//如果长度不是2个长度，就不读取这条数据
							if(packAfterSaleArr1_1Arr.length!=2)break;
							//设置title作为#分开的数据1
							String title =packAfterSaleArr1_1Arr[0];
							//设置txt作为#分开的数据2
							String txt =packAfterSaleArr1_1Arr[1];
							//封装到Map
							Map<String,Object> mTmp =new HashMap<String,Object>();
							//放到map
							mTmp.put("title", title);
							mTmp.put("txt", txt);
							//添加到List
							lmTmp3.add(mTmp);
						}
						//设置返回id
						responseMap.put("id", paramMap.get("id").toString());
						//以数组形式创建list
						List<String> lstr=Arrays.asList(introduceArr);
						String dgurl =paramMap.get("dgurl").toString();
						//设置介绍图片返回容器
						List<String> lsTmp =new ArrayList<String>();
						//为每个图片url加上前缀
						for(int i=0;i<lstr.size();i++){
							lsTmp.add(dgurl+lstr.get(i));
						}
						//价格说明or申明
						String[] priceNoteArr =m.get("priceNote").toString().split("#");
						//设置关于我们说明
						Map<String,Object> mTmp =new HashMap<String,Object>();
						//返回title
						mTmp.put("title", priceNoteArr[0]);
						//返回txt
						if(priceNoteArr.length>1){
							mTmp.put("txt", priceNoteArr[1]);
						}
						//封装各个map类型数据设置为返回数据
						responseMap.put("priceNote", mTmp);
						responseMap.put("introduceList",  lsTmp);
						responseMap.put("parameterList", lmTmp2);
						responseMap.put("packAfterSaleList", lmTmp3);
						//获取缓存名称
						int cacheTime =new Integer(paramMap.get("cacheTime").toString());
						//总数据放入缓存
						return saveAndGet(responseMap, cacheKey, cacheTime);
						
					}
				//缓存有数据，读取缓存
				}else if(isCacheNull(cacheKey).equals("b")){
					return q(cacheKey);
				//缓存异常，同第一次读取缓存一样操作
				}else{
					try {
						m =npjt.queryForMap(sql, paramMap);
					} catch (EmptyResultDataAccessException e) {
						return null;
					}
					if(m!=null){
						Map<String,Object> responseMap =new HashMap<String,Object>();
						
						String[] introduceArr =(m.get("introduce").toString()).split(",");
						
						String[] parameterArr1 =(m.get("parameter").toString()).split("&");
						List<Map<String,Object>> lmTmp2 =new ArrayList<Map<String,Object>>();
						for(int i=0;i<parameterArr1.length;i++){
							String[] parameterArr1_1Arr =(parameterArr1[i]).split("#");
							if(parameterArr1_1Arr.length!=2)break;
							String title =parameterArr1_1Arr[0];
							String txt =parameterArr1_1Arr[1];
							Map<String,Object> mTmp =new HashMap<String,Object>();
							mTmp.put("title", title);
							mTmp.put("txt", txt);
							lmTmp2.add(mTmp);
						}
						String[] packAfterSaleArr1 =(m.get("packAfterSale").toString()).split("&");
						List<Map<String,Object>> lmTmp3 =new ArrayList<Map<String,Object>>();
						for(int i=0;i<packAfterSaleArr1.length;i++){
							String[] packAfterSaleArr1_1Arr =(packAfterSaleArr1[i]).split("#");
							if(packAfterSaleArr1_1Arr.length!=2)break;
							String title =packAfterSaleArr1_1Arr[0];
							String txt =packAfterSaleArr1_1Arr[1];
							Map<String,Object> mTmp =new HashMap<String,Object>();
							mTmp.put("title", title);
							mTmp.put("txt", txt);
							lmTmp3.add(mTmp);
						}
						responseMap.put("id", paramMap.get("id").toString());
						List<String> lstr=Arrays.asList(introduceArr);
						String dgurl =paramMap.get("dgurl").toString();
						
						List<String> lsTmp =new ArrayList<String>();
						for(int i=0;i<lstr.size();i++){
							lsTmp.add(dgurl+lstr.get(i));
						}
						String[] priceNoteArr =m.get("priceNote").toString().split("#");
						Map<String,Object> mTmp =new HashMap<String,Object>();
						mTmp.put("title", priceNoteArr[0]);
						mTmp.put("txt", priceNoteArr[1]);
						
						responseMap.put("priceNote", mTmp);
						responseMap.put("introduceList",  lsTmp);
						responseMap.put("parameterList", lmTmp2);
						responseMap.put("packAfterSaleList", lmTmp3);
						
						mapper.writeValueAsString(responseMap);
					}
					
					
				}
				//缓存无数据，又查不出数据，也没有异常，那就返回空字符啦
				return "";
	}
	
	@Override
	public Map<String, Object> getOrderComm(String orderNo) {
		String sql = "SELECT info_ids,img_url,file_type FROM `diy_orders` WHERE order_no=? AND `status`=4";
		return diyTemplate.queryForMap(sql,new Object[]{orderNo});
	}
	
	@Override
	public Map<String, Object> getMobile4Order(String orderNo) {
		String sql = "SELECT mobile,texture_names FROM diy_orders WHERE order_no = ?";
		return diyTemplate.queryForList(sql, new Object[]{orderNo}).get(0);
	}
	
	@Override
	public Integer saveComment(String goodsId,Integer evalType,String content,String deviceNo,String mobile,String texture) throws Exception{
		String sql = "insert into diy_evaluation(goodsId,evalType,content,cime,imgUrl,imgNull,deviceNo,mobile,texture,state) values (?,?,?,NOW(),?,1,?,?,?,0)";
		return diyTemplate.update(sql, new Object[]{goodsId,evalType,content,"",deviceNo,mobile,texture});
	}
	
	@Override
	public void updateEvaStatus(String orderNo) {
		String sql = "UPDATE `diy_orders` SET `status`=7 WHERE `order_no`=?";
		int flag = diyTemplate.update(sql,new Object[]{orderNo});
		if(flag<1){
			throw new RuntimeException();
		}
	}
	
	@Override
	public String getIdfa(String memberId) {
		return this.jdbcTemplate.queryForObject("SELECT `idfa` FROM `by_user` WHERE `id`=? LIMIT 1",new Object[]{memberId}, String.class);
	}
	
	@Override
	public List<Map<String, Object>> queryShopList(String idfa, String app) {
		List<Map<String, Object>> list=this.diyTemplate.queryForList("SELECT `ds`.`shop_no` AS `shopNo`,`ds`.`texture_name` AS `textureName`,`ds`.`img_url` AS `imgUrl`,`ds`.`file_type` AS `fileType`,`ds`.`num`,`dgi`.`name`,`dgi`.`transportfee`,`dit`.`baby_now_price` AS `nowPrice`,dgi.`isBoutique` FROM `diy_shopcart` `ds` INNER JOIN `diy_goods_info` `dgi` ON `ds`.`info_id`=`dgi`.`id` INNER JOIN `diy_info_texture` `dit` ON `dit`.`texture_ids`=`ds`.`texture_ids` WHERE `dit`.`info_id`=`ds`.`info_id` AND `ds`.`device_no`=? AND `ds`.`app`=? AND `ds`.`status`=1 ORDER BY `ds`.`creat_time` DESC",new Object[]{idfa,app});
		return list==null||list.size()==0?null:list;
	}
	
	@Override
	public int deleteShop(String deviceNo, String app, String shopNo) {
		String sql="UPDATE `diy_shopcart` SET `status`=0,`num`=0 WHERE `device_no`=? AND `app`=? AND `status`!=0 ";
		if(!"('-1')".equals(shopNo)){
			sql+=" AND `shop_no` IN "+shopNo;
		}
		return this.diyTemplate.update(sql,new Object[]{deviceNo,app});
	}
	
	@Override
	public int updateShopOrderNum(String deviceNumber, String app, String flag,int num) {
		if("order".equalsIgnoreCase(flag)){
			return this.diyTemplate.update("UPDATE `diy_device_user` SET `order_num`=`order_num`+? WHERE `device_no`=? AND `app`=? AND `status`=1  LIMIT 1",new Object[]{num,deviceNumber,app});
		}else if("shop".equalsIgnoreCase(flag)){
			return this.diyTemplate.update("UPDATE `diy_device_user` SET `shop_num`=`shop_num`+? WHERE `device_no`=? AND `app`=? AND `status`=1  LIMIT 1",new Object[]{num,deviceNumber,app});
		}
		return 0;
	}
	
	@Override
	public int updateShop(final String deviceNo, final String app, String shopNos, String nums) {
		final String[] shopNo=shopNos.split(",");
		final String[] num=nums.split(",");
		int result[]=this.diyTemplate.batchUpdate("UPDATE `diy_shopcart` SET `num`=? WHERE `shop_no`=? AND `device_no`=? AND `app`=?", new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1, Integer.parseInt(num[i]));
				ps.setString(2, shopNo[i]);
				ps.setString(3, deviceNo);
				ps.setString(4, app);
			}
			
			@Override
			public int getBatchSize() {
				return shopNo.length;
			}
		});
		return result.length;
	}
	
	@Override
	public List<Map<String, Object>> queryAddress(String deviceNo, String app) {
		List<Map<String, Object>> list= this.diyTemplate.queryForList("SELECT `id`,`name`,`mobile`,`province`,`area` FROM `diy_user_address` WHERE `status`=1 AND `device_no`=? AND `app`=? ORDER BY `is_default` DESC,`ctime` DESC",new Object[]{deviceNo,app});
		return (list==null || list.size()==0)?null:list;
	}
	
	@Override
	public List<Map<String, Object>> shopBuy(String shopNos, String deviceNo, String app) {
		String sql = "SELECT s.shop_no,g.`name`,s.`texture_name`,s.`num`,s.`img_url`,s.`file_type`,t.`baby_now_price` AS `now_price`,g.`isBoutique` " +
				"FROM `diy_shopcart` s ,`diy_goods_info` g ,`diy_info_texture` t " +
				"WHERE s.`info_id`=g.`id` AND s.`texture_ids`=t.`texture_ids` AND s.`device_no`=? AND s.`app`=? AND s.info_id=t.`info_id` AND s.`shop_no` in "+shopNos ;
		return diyTemplate.queryForList(sql,new Object[]{deviceNo,app});
	}
	
	@Override
	public List<Map<String, Object>> getOrderList(String deviceNo, String app, int page, int number, Integer state) {
		String sql = "SELECT `do`.`price`,`do`.`fee_transport` AS `transportfee`,`do`.`name`,`do`.`user_ids` AS `companyId`,`do`.`sort_name` AS `sortName`,`do`.`order_no` AS `orderNo`,`do`.`creat_time`,`do`.`pay_type` AS `payType`,`do`.`fee_total` AS `feeTotal`,`do`.`img_url` AS `imgUrl`,`do`.`file_type` AS `fileType`,`do`.`num`,`do`.`texture_names` AS `textureNames`,`do`.`status`,`do`.`express_no` AS `expressNo`,`de`.`code`,`do`.`info_ids` as `infoIds` FROM `diy_orders` `do`  LEFT JOIN `diy_express` `de` ON `de`.`id`=`do`.`express_id` LEFT JOIN `diy_trade` `dt` ON `dt`.`order_no`=`do`.`order_no` WHERE `do`.`device_no`=? AND `do`.`app`=? AND `do`.`status`!=-1 AND `do`.`status`!=0 AND `do`.`status`!=6 AND LOCATE(`do`.`file_type`,'xxx' )>=0 ";
		String end = " ORDER BY `status` ASC ,`creat_time` DESC LIMIT ?,?";
		if(state!=null&&state!=0)
			sql += " and `do`.`status`="+state;
		List<Map<String, Object>> list=this.diyTemplate.queryForList(sql+end,new Object[]{deviceNo,app,page*number,number});
		return (list==null || list.size()==0)?null:list;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean updateOrder(String orderNo, String deviceNo, String app, String flag) {
		int status=0;
		if("delete".equalsIgnoreCase(flag)){
			status=6;
		}else if("cancel".equalsIgnoreCase(flag)){
			status=5;
		}else if("confirm".equalsIgnoreCase(flag)){
			status=4;
		}else if("paidCancel".equalsIgnoreCase(flag)){
			status=9;
		}
		int result=this.diyTemplate.update("UPDATE `diy_orders` SET `status`=? WHERE `order_no`=? AND `device_no`=? AND `app`=? LIMIT 1",new Object[]{status,orderNo,deviceNo,app});
		if(result>0){
			if(status==6){
				//删除订单的时候更新订单数
				this.updateShopOrderNum(deviceNo, app, "order", -1);
			}
			if(status==6 || status==5){
				//查询订单绑定的购物车代金券
				String couponId=this.diyTemplate.queryForObject("SELECT `coupon_id` FROM `diy_orders` WHERE `order_no`=?",new Object[]{orderNo}, String.class);
				//将代金券激活还回去 (如果代金券还在有效期内)
				this.jdbcTemplate.update("UPDATE `by_user_coupon`  SET `status`=1 WHERE `id`=? AND `valid`>=DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s')",new Object[]{couponId});
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Map<String, Object> queryOrderInfo(String orderNo, String deviceNo, String app) {
		Map<String, Object> map=new HashMap<>();
		try {
			map=this.diyTemplate.queryForMap("SELECT `do`.`shop_no` AS `shopNo`,`do`.`user_ids` AS `userIds`,`do`.`info_ids` AS `infoIds`,`do`.`price`,`do`.`fee_transport` AS `transportfee`,`do`.`order_no` AS `orderNo`,`do`.`province`,`do`.`area`,`do`.`mobile`,`do`.`pay_type` AS `payType`,`do`.`consignee`,`do`.`fee_total` AS `feeTotal`,`de`.`code`,DATE_FORMAT(`do`.`creat_time`,'%Y-%m-%d %H:%i:%s') AS `creatTime`,`do`.`img_url` AS `imgUrl`,`do`.`file_type` AS `fileType`,`do`.`num`,`do`.`texture_ids` AS `textureIds`,`do`.`texture_names` AS `textureNames`,`do`.`status`,`do`.`express_no` AS `expressNo`,`dgi`.`name`,`dt`.`prepay_id` AS `prepayId`,do.info_ids as goodsIds FROM `diy_orders` `do` INNER JOIN `diy_goods_info` `dgi` ON `do`.`info_ids`=`dgi`.`id` LEFT JOIN `diy_trade` `dt` ON `do`.`order_no`=`dt`.`order_no` LEFT JOIN `diy_express` `de` ON `de`.`id`=`do`.`express_id`  WHERE `do`.`device_no`=? AND `do`.`app`=? AND `do`.`order_no`=? LIMIT 1",new Object[]{deviceNo,app,orderNo});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	@Override
	public int countActiveShop(String deviceNo, String app, String shopNo) {
		try {
			return this.diyTemplate.queryForObject("SELECT COUNT(*) FROM `diy_shopcart` WHERE `device_no`=? AND `app`=? AND `shop_no` IN "+shopNo+" AND `status`=0",new Object[]{deviceNo,app}, Integer.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public int addShopByOrder(String prices, String imgUrls, String fileTypes, String nums, String textureNamess,
			String infoIdss, String userIdss, String shopNo, String deviceNo, String app, String textureId) {
		return this.diyTemplate.update("INSERT INTO `diy_shopcart` (`shop_no`,`info_id`,`texture_ids`,`texture_name`,`user_id`,`img_url`,`file_type`,`num`,`device_no`,`app`,`creat_time`,`status`) VALUES(?,?,?,?,?,?,?,?,?,?,NOW(),1) ON DUPLICATE KEY UPDATE `num`=`num`+?,`status`=1",new Object[]{shopNo,infoIdss,textureId,textureNamess,userIdss,imgUrls,fileTypes,nums,deviceNo,app,nums});
	}
	
	@Override
	public Map<String, Object> confirmOrderNumAndPay(String orderNo, String deviceNo, String app) {
		try {
			return this.diyTemplate.queryForMap("SELECT `do`.`num`,`do`.`info_ids` AS `infoIds`,`do`.`pay_type` AS `payType`,DATE_FORMAT(`do`.`creat_time`,'%Y-%m-%d %H:%i:%s') AS `creatTime`,`dt`.`prepay_id` AS `prepayId`,TIMESTAMPDIFF(MINUTE,`dt`.`time`,NOW()) AS `time`,`do`.`fee_total` AS `feeTotal` FROM `diy_orders` `do` LEFT JOIN `diy_trade` `dt` ON `do`.`order_no`=`dt`.`order_no` WHERE `do`.`order_no`=? AND `do`.`device_no`=? AND `app`=? ",new Object[]{orderNo,deviceNo,app});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean checkOrderNum(Integer num, String infoid) {
		try {
			this.diyTemplate.queryForObject("SELECT `id` FROM `diy_goods_info` WHERE `stock`>=? AND `id`=?",new Object[]{num,infoid}, Integer.class);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int boundPrepayNo(String orderNo, String prepayId, String payNo) {
		return this.diyTemplate.update("UPDATE  `diy_trade` SET `prepay_id`=?,`pay_no`=?,`time`=NOW() WHERE `order_no`=?",new Object[]{prepayId,payNo,orderNo});
	}
	
	@Override
	public Map<String, Object> getWxPay(String app) {
		try {
			return this.diyTemplate.queryForMap("SELECT `app`,`appid`,`key`,`app_id` AS `appIDS`,`mch_id` AS `mchId` FROM `wx_pay` WHERE `app`=? AND `status`=1",new Object[]{app});
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String getOpenId(String deviceNo) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `wx_openid` FROM `by_user` WHERE `idfa`=?",new Object[]{deviceNo},String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public String getIdfaByOrderNo(String orderNo) {
		return this.diyTemplate.queryForObject("SELECT `device_no` FROM `diy_orders` WHERE `order_no`=?",new Object[]{orderNo}, String.class);
	}
	
	@Override
	public String getMemberId(String deviceNo) {
		return this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user` WHERE `idfa`=?",new Object[]{deviceNo}, String.class);
	}
	
	@Override
	public String getAdress(String deviceNo, String app) throws Exception{
		List<Map<String, Object>> list= this.diyTemplate.queryForList("SELECT `id`,`name`,`mobile`,`province`,`area` FROM `diy_user_address` WHERE `status`=1 AND `device_no`=? AND `app`=? ORDER BY `is_default` DESC,`ctime` DESC",new Object[]{deviceNo,app});
		return (list==null || list.size()==0)?null:mapper.writeValueAsString(list);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public double activeOrder(String memberId,String orderNo, String couponId, String addressId, String num, String payId,
			double totalFee, double orgPrice, double desPrice, String deviceNo, String app,Map<String, Object> addressMap,String remark,double balance) {
		try {
			double couponMoney=0.00;
			if(!StringUtils.isBlank(couponId)){
				//将代金券失效  绑定到订单
				int result1=this.jdbcTemplate.update("UPDATE `by_user_coupon` SET `status`=0 WHERE `id`=? AND `member_id`=?",new Object[]{couponId,memberId});
				if(result1>0){
					//代金券钱查出来
					couponMoney=this.jdbcTemplate.queryForObject("SELECT `now_price` FROM `by_user_coupon` WHERE `id`=?",new Object[]{couponId}, Double.class);
				}else{
					couponId=null;
				}
			}else{
				couponId=null;
			}
			int result=this.diyTemplate.update("UPDATE `diy_orders` SET `consignee`=?,`province`=?,`area`=?,`mobile`=?,`pay_type`=?,`num`=?,`fee_total`=?,`creat_time`=NOW(),`coupon`=?,`coupon_id`=?,`org_privilege`=?,`des_privilege`=?,`status`=1,`remark`=?,`balance`=?  WHERE `order_no`=? AND `device_no`=? AND `app`=?",
					new Object[]{addressMap.get("name"),addressMap.get("province"),addressMap.get("area"),addressMap.get("mobile"),payId,num,(totalFee-couponMoney)<0?0:(totalFee-couponMoney),couponMoney,couponId,orgPrice,desPrice,remark,balance,orderNo,deviceNo,app});
			if(result>0){
				return totalFee-couponMoney;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	@Override
	public int boundOrderPrepay(String orderNo, String prepayId, String payNo) {
		return this.diyTemplate.update("INSERT INTO `diy_trade` (`order_no`,`pay_no`,`prepay_id`,`time`,`type`) VALUES(?,?,?,NOW(),2)",new Object[]{orderNo,payNo,prepayId});
	}
	
	@Override
	public List<Map<String, Object>> getShopInfoByShopNos(String shopNo) {
		List<Map<String, Object>> list=this.diyTemplate.queryForList("SELECT `ds`.`shop_no` AS `shopNo`,`ds`.`info_id` AS `infoId`,`ds`.`texture_ids` AS `textureIds`,`ds`.`texture_name` AS `textureName`,`ds`.`user_id` AS `userId`,`ds`.`img_url` AS `imgUrl`,`ds`.`file_type` AS `fileType`,`ds`.`num`,`dgi`.`name`,`dgi`.`transportfee`,`dit`.`baby_now_price` AS `nowPrice`,dgi.`isBoutique` FROM `diy_shopcart` `ds` INNER JOIN `diy_goods_info` `dgi` ON `dgi`.`id`=`ds`.`info_id` INNER JOIN `diy_info_texture` `dit` ON `dit`.`info_id`=`ds`.`info_id` WHERE `dit`.`texture_ids`=`ds`.`texture_ids` AND `ds`.`status`=1 AND `ds`.`shop_no` IN "+shopNo+"");
		return (list==null||list.size()<0)?null:list;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public double createOrderByShops(String memberId,String orderNo, String shopNo, String infoId, String textureIds, String textureName,
			String userId, String imgUrl, String fileType, String num, String name, String nowPrice,String payId,double orgPrice,
			double desPrice,String couponId,double totalFee,Map<String, Object> addressMap,String deviceNo,String app,String transportFee,String remark,double balance) {
		try {
			double couponMoney=0.00;
			if(!StringUtils.isBlank(couponId)){
				//将代金券失效  绑定到订单
				int result1=this.jdbcTemplate.update("UPDATE `by_user_coupon` SET `status`=0 WHERE `id`=?",new Object[]{couponId});
				if(result1>0){
				//代金券钱查出来
				couponMoney=this.jdbcTemplate.queryForObject("SELECT `now_price` FROM `by_user_coupon` WHERE `id`=?",new Object[]{couponId}, Double.class);
				}else{
					couponId=null;
				}
			}else{
				couponId=null;
			}
			int results= this.diyTemplate.update("INSERT INTO `diy_orders` (`order_no`,`shop_no`,`info_ids`,`user_ids`,`texture_ids`,`texture_names`,`img_url`,`file_type`,`pay_type`,`consignee`,`province`,`area`,`mobile`,`num`,`fee_transport`,`fee_total`,`price`,`coupon`,`coupon_id`,`des_privilege`,`org_privilege`,`creat_time`,`device_no`,`app`,`status`,`remark`,`name`,`balance`) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,1,?,?,?)",new Object[]{orderNo,shopNo,infoId,userId,textureIds,textureName,imgUrl,fileType,payId,addressMap.get("name"),addressMap.get("province"),addressMap.get("area"),addressMap.get("mobile"),num,transportFee,(totalFee-couponMoney)<0?0:(totalFee-couponMoney),nowPrice,couponMoney,couponId,desPrice,orgPrice,deviceNo,app,remark,name,balance});
			if(results>0){
				return totalFee-couponMoney;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}
	
	@Override
	public void invalidShop(String shopNo, String deviceNo, String app) {
		this.diyTemplate.update("UPDATE `diy_shopcart` SET `status`=0,`num`=0 WHERE `device_no`=? AND `app`=?  AND `shop_no` IN "+shopNo+"",new Object[]{deviceNo,app});
	}
	
	@Override
	public int getShopNum(String deviceNo, String app) {
		String sql = "SELECT COUNT(*) FROM `diy_shopcart` WHERE device_no=? AND app=? AND `status`=1";
		return diyTemplate.queryForObject(sql, Integer.class, new Object[]{deviceNo,app});
	}
	
	@Override
	public String queryPreUrl(String infoId, String textureIds) {
		try {
			return this.diyTemplate.queryForObject("SELECT `pre_url` FROM `diy_info_texture` WHERE `info_id`=? AND `texture_ids`=?",new Object[]{infoId,textureIds}, String.class);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public Map<String, Object> createOrder(String resultPath, String infoId, String textureIds, String deviceNumber,
			String app, String orderNo, String sufFormat, String num) {
		Map<String, Object> result=new HashMap<>();
		try {
			String textureNames=queryTextureById(textureIds.split("_"));
			//查询价格 和运费
			Map<String, Object> fees=this.diyTemplate.queryForMap("SELECT `dgi`.`transportfee`,`dit`.`baby_now_price` AS `price`,`dgi`.`user_id`,`dgi`.`goodsType`,`dgi`.`name` FROM `diy_goods_info` `dgi` INNER JOIN `diy_info_texture` `dit` ON `dgi`.`id`=`dit`.`info_id` WHERE `dit`.`info_id`=? AND `dit`.`texture_ids`=? LIMIT 1",new Object[]{infoId,textureIds});
			int index= this.diyTemplate.update("INSERT INTO `diy_orders` (`order_no`,`fee_transport`,`fee_total`,`status`,`device_no`,`app`,`info_ids`,`texture_ids`,`user_ids`,`price`,`img_url`,`file_type`,`num`,`texture_names`,`name`) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{orderNo,fees.get("transportfee"),((double)fees.get("transportfee")+(double)fees.get("price")*Integer.parseInt(num)),-1,deviceNumber,app,infoId,textureIds,fees.get("user_id"),fees.get("price"),resultPath,sufFormat,num,textureNames,fees.get("name")});
			result.put("textureNames", textureNames);
			result.put("name", (String)fees.get("name"));
			result.put("orgPrice", (Double)fees.get("price"));
			if(index>0){
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String queryTextureById(String[] tempTextureId) {
		StringBuilder sql=new StringBuilder();
		String textureNames="";
		sql.append("SELECT `name` FROM `diy_goods_texture` WHERE `id`="+tempTextureId[0]);
		for (int i = 1; i < tempTextureId.length; i++) {
			sql.append(" OR `id`="+tempTextureId[i]);
		}
		List<Map<String, Object>> list=this.diyTemplate.queryForList(sql.toString());
		if(list!=null && list.size()>0){
			for (int j = 0; j < list.size(); j++) {
				textureNames+=(String)list.get(j).get("name")+",";
			}
		}
		return textureNames.substring(0,textureNames.length()-1);
	}
	
	@Override
	public Integer isBoutique(String id) {
		return diyTemplate.queryForObject("SELECT isBoutique FROM `diy_goods_info` WHERE id="+id, Integer.class);
	}
	
	@Override
	public String getEvalTypeNum(Map<String, Object> paramMap) throws Exception{
		String cacheKey =paramMap.get("cacheKey").toString();
		del(cacheKey);
		String sql = "SELECT e.evalType,COUNT(*) AS number FROM diy_evaluation e , diy_goods_info i " +
				"WHERE e.`goodsId`=:goodsId AND e.`goodsId`=i.`id` AND e.`state`=1 GROUP BY e.evalType;";
		List<Map<String,Object>> m =null;
		if(isCacheNull(cacheKey).equals("a")||isCacheNull(cacheKey).equals("c")){
			//String sql ="SELECT evalType,COUNT(evalType) AS number FROM diy_evaluation where goodsId=:goodsId GROUP BY evalType";
			
			try {
				m=npjt.queryForList(sql,paramMap);
				Integer sum = 0;
				for (int i = 0; i < m.size(); i++) {
					Map<String,Object> map = m.get(i);
					sum += null==map.get("number") ? 0 : Integer.valueOf(map.get("number").toString());
				}
				Map<String,Object> newm = new HashMap<String, Object>();
				newm.put("evalType", "0");
				newm.put("number", sum);
				m.add(0, newm);
				if(m.size()<2){
					Map<String,Object> bu1 = new HashMap<String, Object>();
					bu1.put("evalType", "1");
					bu1.put("number", 0);
					m.add(1, bu1);
				}
				if(m.size()<3){
					Map<String,Object> bu2 = new HashMap<String, Object>();
					bu2.put("evalType", "2");
					bu2.put("number", 0);
					m.add(2, bu2);
				}
				if(m.size()<4){
					Map<String,Object> bu3 = new HashMap<String, Object>();
					bu3.put("evalType", "3");
					bu3.put("number", 0);
					m.add(3, bu3);
				}
				
				Map<String,Object> newm2 = new HashMap<String, Object>();
				newm2.put("evalType", "4");
				newm2.put("number", this.getEvalHavePic((int)paramMap.get("goodsId")));
				m.add(newm2);
			} catch (EmptyResultDataAccessException e) {}
			if(m!=null){
				if(isCacheNull(cacheKey).equals("a")){
					int cacheTime =new Integer(paramMap.get("cacheTime").toString());
					return saveAndGet(m, cacheKey, cacheTime);
				}else{
					return mapper.writeValueAsString(m);
				}
			}
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}
		return "";
	}
	
	/**
	 * 查询图片个数
	 * @param goodsId
	 * @return
	 */
	public int getEvalHavePic(int goodsId){
		int res = 0;
		String sql = "SELECT imgUrl FROM diy_evaluation WHERE imgNull=0 AND `state`=1 AND goodsId="+goodsId;
		List<Map<String,Object>> list = diyTemplate.queryForList(sql);
		for (Map<String, Object> map : list) {
			String imgUrl = null==map.get("imgUrl")?"":map.get("imgUrl").toString();
			String[] imgarr = imgUrl.split(",");
			res +=imgarr.length;
		}
		return res;
	}
	
	@Override
	public String findAllEval(Map<String, Object> paramMap) throws Exception{
String cacheKey =paramMap.get("cacheKey").toString();
		
		String sql = "SELECT e.id as id,e.`mobile` AS mobile, e.evalType as evalType, " +
				"e.`content` as content ,e.`cime` AS creatTime,e.`imgUrl` as imgUrl,e.imgNull as imgNull,e.texture as texture "+
				"FROM diy_evaluation e , diy_goods_info i ";
		String where = "WHERE e.`goodsId`=:goodsId AND e.`goodsId`=i.`id`  and e.`state`=1 ";
		if(null!=paramMap.get("evalType")&&0!=(int)paramMap.get("evalType")){
			where += " and evalType = :evalType ";
		}
		String end = "ORDER BY e.`cime` DESC limit :page,:pagesize ";
		List<Map<String,Object>> list =null;
		
		if(isCacheNull(cacheKey).equals("a")||isCacheNull(cacheKey).equals("c")){
			
			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
			try {
				list = npjt.queryForList(sql+where+end,paramMap);
				for (int i = 0; i < list.size(); i++) {
					Map<String,Object> map = list.get(i);
					if(!(boolean)map.get("imgNull")&&null!=map.get("imgUrl")){
						String img = (String) map.get("imgUrl");
						String imgs[]=img.split(",");
						String newimgs = "";
						for (int j = 0; j < imgs.length; j++) {
							String temp = SYSPATH + imgs[j];
							newimgs+=temp+",";
						}
						map.put("imgUrl", newimgs.substring(0, newimgs.length()-1));
					}else
						map.remove("imgUrl");
					Object old = map.get("mobile");
					if(null!=old&&
							old.toString().length()>10){
						String oldstr = old.toString();
						String mobile = oldstr.substring(0, 3)+"****"+oldstr.substring(7, 11);
						map.put("mobile", mobile);
					}else{
						map.put("mobile", "匿名");
					}
					result.add(map);
				}
			} catch (EmptyResultDataAccessException e) {}
			if(result!=null){
				if(isCacheNull(cacheKey).equals("a")){
					int cacheTime =new Integer(paramMap.get("cacheTime").toString());
					return saveAndGet(result, cacheKey, cacheTime);
				}else{
					return mapper.writeValueAsString(result);
				}
			}
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}
		return "";
	}
	
	@Override
	public String findEvalPic(Map<String, Object> paramMap) throws Exception{
		String cacheKey =paramMap.get("cacheKey").toString();
		String sql = "SELECT imgUrl FROM diy_evaluation WHERE goodsId=? and `state`=1 AND imgNull=0 ORDER BY cime desc LIMIT ?,?";
		List<Map<String,Object>> list =null;
		List<Map<String,Object>> result = null;
		if(isCacheNull(cacheKey).equals("a")||isCacheNull(cacheKey).equals("c")){
			
			try {
				list=diyTemplate.queryForList(sql,new Object[]{paramMap.get("goodsId"),paramMap.get("page"),paramMap.get("pagesize")});
				result=new ArrayList<>();
				if(list!=null && list.size()>0){
					for (int i = 0; i < list.size(); i++) {
						String imgUrls=(String)list.get(i).get("imgUrl");
						String temps[]=imgUrls.split(",");
						for (String temp : temps) {
							Map<String,Object> map = new HashMap<>();
							map.put("imgUrl", SYSPATH+temp);
							result.add(map);
						}
					}
				}
			} catch (EmptyResultDataAccessException e) {}
			if(result!=null){
				if(isCacheNull(cacheKey).equals("a")){
					int cacheTime =new Integer(paramMap.get("cacheTime").toString());
					return saveAndGet(result, cacheKey, cacheTime);
				}else{
					return mapper.writeValueAsString(result);
				}
			}
		}else if(isCacheNull(cacheKey).equals("b")){
			return q(cacheKey);
		}
		return "";
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public int createShop(String resultPath, String infoId, String textureIds, String deviceNo, String app,
			String shopNo, String sufFormat, String num) {
		String textureName=queryTextureById(textureIds.split("_"));
		this.diyTemplate.update("INSERT INTO `diy_shopcart` (`shop_no`,`info_id`,`texture_ids`,`texture_name`,`user_id`,`img_url`,`file_type`,`num`,`creat_time`,`status`,`device_no`,`app`)VALUES(?,?,?,?,(SELECT `user_id` FROM `diy_goods_info` WHERE `id`=?),?,?,?,NOW(),?,?,?)",new Object[]{shopNo,infoId,textureIds,textureName,infoId,resultPath,sufFormat,num,1,deviceNo,app});
		int result=updateShopOrderNum(deviceNo, app, "shop", 1);
		if(result<=0){
			throw new RuntimeException();
		}
		return result;
	}
	
	@Override
	public Integer mergeShop(String infoId,String textureIds, String deviceNo, String app,int num){
		try {
			String sql = "UPDATE `diy_shopcart` SET num=num+? WHERE info_id=? AND texture_ids=? AND device_no=? AND app=? AND `status`=1";
			return diyTemplate.update(sql, new Object[]{num,infoId,textureIds,deviceNo,app});
		} catch (DataAccessException e) {
			return 0;
		}
	}
	
	@Override
	public int updateAdress(final String deviceNo, final String app, String addressId, final String name, final String mobile, final String province,
			final String area, final String isDefault) {
		if(StringUtils.isBlank(addressId)){
			if("1".equalsIgnoreCase(isDefault)){
				this.diyTemplate.update("UPDATE `diy_user_address` SET `is_default`=0 WHERE `device_no`=? AND `app`=? AND `status`=1",new Object[]{deviceNo,app});
			}
			
			final String sql="INSERT INTO `diy_user_address` (`name`,`mobile`,`province`,`area`,`is_default`,`ctime`,`status`,`device_no`,`app`) VALUES(?,?,?,?,?,NOW(),1,?,?)";
			int lastNum = 0;
			KeyHolder keyHolder = new GeneratedKeyHolder(); // 创建一个主健拥有者
			PreparedStatementCreator p = new PreparedStatementCreator() {			
				public PreparedStatement createPreparedStatement(Connection conn) {
					try {
						PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
						ps.setString(1, name);
						ps.setString(2, mobile);
						ps.setString(3, province);
						ps.setString(4, area);
						ps.setString(5, isDefault);
						ps.setString(6, deviceNo);
						ps.setString(7, app);
						return ps;
					} catch (SQLException e) {
						e.printStackTrace();
					};
					return null;
				}
			};
			
			this.diyTemplate.update(p, keyHolder);
			lastNum = keyHolder.getKey().intValue();
			return lastNum;
		}else{
			this.diyTemplate.update("UPDATE `diy_user_address` SET `name`=?,`mobile`=?,`province`=?,`area`=?,`is_default`=? WHERE `id`=? AND `device_no`=? AND `app`=? AND `status`=1 LIMIT 1",new Object[]{name,mobile,province,area,isDefault,addressId,deviceNo,app});
			this.diyTemplate.update("UPDATE `diy_user_address` SET `is_default`=0 WHERE `id`!=? AND `device_no`=? AND `app`=? AND `status`=1",new Object[]{addressId,deviceNo,app});
		}
		return Integer.parseInt(addressId);
	}
	
	@Override
	public String getDeviceNoByShop(String shopNo) {
		return this.diyTemplate.queryForObject("SELECT `device_no` FROM `diy_shopcart` WHERE `shop_no`=?",new Object[]{shopNo}, String.class);
	}
	
	@Override
	public Map<String, Object> queryOrderInfoByOrderNo(String orderNo) {
		Map<String, Object> map=new HashMap<>();
		try {
			map=this.diyTemplate.queryForMap("SELECT `do`.`shop_no` AS `shopNo`,`do`.`user_ids` AS `userIds`,`do`.`info_ids` AS `infoIds`,`do`.`price`,`do`.`fee_transport` AS `transportfee`,`do`.`order_no` AS `orderNo`,`do`.`province`,`do`.`area`,`do`.`mobile`,`do`.`pay_type` AS `payType`,`do`.`consignee`,`do`.`fee_total` AS `feeTotal`,`de`.`code`,DATE_FORMAT(`do`.`creat_time`,'%Y-%m-%d %H:%i:%s') AS `creatTime`,`do`.`img_url` AS `imgUrl`,`do`.`file_type` AS `fileType`,`do`.`num`,`do`.`texture_ids` AS `textureIds`,`do`.`texture_names` AS `textureNames`,`do`.`status`,`do`.`express_no` AS `expressNo`,`dgi`.`name`,`dt`.`prepay_id` AS `prepayId`,do.info_ids as goodsIds FROM `diy_orders` `do` INNER JOIN `diy_goods_info` `dgi` ON `do`.`info_ids`=`dgi`.`id` LEFT JOIN `diy_trade` `dt` ON `do`.`order_no`=`dt`.`order_no` LEFT JOIN `diy_express` `de` ON `de`.`id`=`do`.`express_id`  WHERE `do`.`order_no`=? LIMIT 1",new Object[]{orderNo});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	@Override
	public int checkOrderIsPayed(String trade_no) {
		try {
			return this.diyTemplate.queryForObject("SELECT COUNT(*) FROM `diy_trade` WHERE `trade_no`=?",new Object[]{trade_no}, Integer.class);
		} catch (Exception e) {
		}
		return 0;
	}
	
	@Override
	public boolean insertPayRecord(Map<String, Object> paramMap) {
		try {
			this.diyTemplate.update("INSERT INTO `diy_trade` (`order_no`,`email`,`trade_no`,`price`,`time`,`type`) VALUES(?,?,?,?,NOW(),?)",new Object[]{paramMap.get("order_no"),paramMap.get("email"),paramMap.get("trade_no"),paramMap.get("price"),paramMap.get("type")});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean changeOrderStatus(Map<String, Object> paramMap) {
		try {
			int result=this.diyTemplate.update("UPDATE `diy_orders` SET `status`=2,`paytime`=NOW() WHERE `order_no`=? ",new Object[]{paramMap.get("order_no")});
			if(result>0){
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	@Override
	public Map<String, Object> queryOrders(String out_trade_no) {
		Map<String, Object> map=new HashMap<>();
		try {
			map = this.diyTemplate.queryForMap("select * from diy_orders where order_no=?",new Object[]{out_trade_no});
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	@Override
	public void separateOrder(Map<String, Object> tempMap) {
		String sql = "INSERT INTO `diy_orders` (`order_no`, `porder_no`, `shop_no`, `info_ids`, `user_ids`, `texture_ids`, `texture_names`, `img_url`, `file_type`, `pay_type`, `consignee`, `province`, `area`, `mobile`, `sys`, `num`, `fee_transport`, `fee_total`, `price`, `coupon`, `coupon_id`, `org_privilege`, `des_privilege`, `express_id`, `express_no`, `express_start`, `express_end`, `paytime`, `creat_time`, `device_no`, `app`, `remark`, `gType`, `status`,`name`) " +
				"VALUES (:order_no, :porder_no, :shop_no, :info_ids, :user_ids, :texture_ids, :texture_names, :img_url, :file_type, :pay_type, :consignee, :province, :area, :mobile, :sys, :num, :fee_transport, :fee_total, :price, :coupon, :coupon_id, :org_privilege, :des_privilege, :express_id, :express_no, :express_start, :express_end, :paytime, :creat_time, :device_no, :app, :remark, :gType, :status,:name)";
		this.npjt.update(sql, tempMap);
	}
	
	@Override
	public void update0status(String out_trade_no) {
		this.diyTemplate.update("update diy_orders set status=0 where order_no=?",new Object[]{out_trade_no});
	}
	
	@Override
	public boolean deleteAdress(String deviceNo, String app, String addressId) {
		int result=this.diyTemplate.update("UPDATE `diy_user_address` SET `status`=0 WHERE `id`=?  AND `device_no`=? AND `app`=?",new Object[]{addressId,deviceNo,app});
		if(result>0){
			return true;
		}
		return false;
	}
	
	@Override
	public List<Map<String, Object>> queryShopList(String deviceNo, String app, int page, int number) {
		List<Map<String, Object>> list=this.diyTemplate.queryForList("SELECT `ds`.`shop_no` AS `shopNo`,`ds`.`texture_name` AS `textureName`,`ds`.`img_url` AS `imgUrl`,`ds`.`file_type` AS `fileType`,`ds`.`num`,`dgi`.`name`,`dgi`.`transportfee`,`dit`.`baby_now_price` AS `nowPrice` FROM `diy_shopcart` `ds` INNER JOIN `diy_goods_info` `dgi` ON `ds`.`info_id`=`dgi`.`id` INNER JOIN `diy_info_texture` `dit` ON `dit`.`texture_ids`=`ds`.`texture_ids` WHERE `dit`.`info_id`=`ds`.`info_id` AND `ds`.`device_no`=? AND `ds`.`app`=? AND `ds`.`status`=1 ORDER BY `ds`.`creat_time` DESC LIMIT ?,?",new Object[]{deviceNo,app,page*number,number});
		return list==null||list.size()==0?null:list;
	}
	
	@Override
	public String queryOrderCompanyName(String companyId) {
		String sql="SELECT `name` FROM `diy_sys_user` WHERE `id`=?";
		return this.diyTemplate.queryForObject(sql,String.class,companyId);
	}
	
	@Override
	public void insertDiyUser(String deviceNo, String app) {
		try {
			this.diyTemplate.update("INSERT INTO `diy_device_user` (`device_no`,`app`,`ctime`,`status`) VALUES(?,?,NOW(),1)",new Object[]{deviceNo,app});
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public String couponCenter(String memberId) throws Exception{
		String sql="SELECT 1 AS `flag`,`org_price` AS `orgPrice`,`now_price` AS `nowPrice`,`about`,`type`,DATE_FORMAT(`valid`,'%Y-%m-%d %H:%i:%s') AS `valid`,DATE_FORMAT(`creatime`,'%Y-%m-%d %H:%i:%s') AS `creatime` FROM `by_user_coupon` WHERE `status`=1 AND `valid`>=DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') AND `member_id`=? UNION SELECT 2 AS `flag`,`org_price` AS `orgPrice`,`now_price` AS `nowPrice`,`about`,`type`,DATE_FORMAT(`valid`,'%Y-%m-%d %H:%i:%s') AS `valid`,DATE_FORMAT(`creatime`,'%Y-%m-%d %H:%i:%s') AS `creatime` FROM `by_user_coupon` WHERE `status`=1 AND `valid`<DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') AND `member_id`=? ORDER BY `creatime` DESC";
		List<Map<String,Object>>list=this.jdbcTemplate.queryForList(sql,new Object[]{memberId,memberId});
		List<Map<String, Object>> validList=new ArrayList<>();
		List<Map<String, Object>> invalidList=new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).put("nowPrice",((Double)list.get(i).get("nowPrice")).intValue());
			list.get(i).put("orgPrice",((Double)list.get(i).get("orgPrice")).intValue());
			long flag=(long)list.get(i).get("flag");
			if(flag==1){
				validList.add(list.get(i));
			}else{ 
				invalidList.add(list.get(i));
			}
		}
		Map<String, Object> result=new HashMap<>();
		result.put("validList", validList);
		result.put("invalidList", invalidList);
		return mapper.writeValueAsString(result);
	}
	
	@Override
	public double getCouponMoney(String homeId) {
		try {
			return this.jdbcTemplate.queryForObject("SELECT `bc`.`now_price` FROM `by_coupon` `bc` INNER JOIN `by_home` `bh` ON `bc`.`id`=`bh`.`coupon_id` WHERE `bh`.`id`=?",new Object[]{homeId}, Double.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public void updateBalance0(String orderNo) {
		this.diyTemplate.update("UPDATE `diy_orders` SET `balance`=0 WHERE `order_no`=?",new Object[]{orderNo});
	}
	
	@Override
	public void desUserBalance(String memberId, double balance) {
		this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`-?,`canfetch_money`=`canfetch_money`-? WHERE `id`=?",new Object[]{balance,balance,memberId});
	}
	
	@Override
	public List<Map<String, Object>> couponList() {
		return this.jdbcTemplate.queryForList("SELECT `id`,`now_price` AS `nowPrice`,`money`,`about` FROM `by_coupon` WHERE `status`=1 ORDER BY `now_price` ASC");
	}
	
	@Override
	public Map<String, Object> activeCode(String memberId, String acticeCode) {
		Map<String, Object> result=new HashMap<>();
		String message="";
		boolean flag=false;
		Map<String, Object> coupon=new HashMap<>();
		try {
			coupon=this.jdbcTemplate.queryForMap("SELECT `id`,`now_price` AS `nowPrice`,`about` FROM `by_coupon` WHERE `code`=? LIMIT 1",new Object[]{acticeCode});
			try {
				this.jdbcTemplate.queryForObject("SELECT `id` FROM `by_user_coupon` WHERE `code`=? AND `member_id`=? LIMIT 1",new Object[]{acticeCode,memberId}, Integer.class);
				message="已经激活过该激活码~";
			} catch (Exception e) {
				//成功领取
				this.jdbcTemplate.update("INSERT INTO `by_user_coupon` (`now_price`,`type`,`about`,`valid`,`member_id`,`code`,`creatime`,`coupon_id`) VALUES(?,2,?,'2020-10-10 00:00:00',?,?,NOW(),?)",new Object[]{coupon.get("nowPrice"),coupon.get("about"),memberId,acticeCode,coupon.get("id")});
				//数量+1
				this.jdbcTemplate.update("UPDATE `by_coupon` SET `num`=`num`+1 WHERE `code`=?",new Object[]{acticeCode});
				flag=true;
				message="激活成功~";
			}
		} catch (Exception e) {
			message="无效的激活码~";
		}
		result.put("message", message);
		result.put("flag", flag);
		return result;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void exchangeCoupon(String memberId, String couponId) {
		Map<String, Object> coupon=this.jdbcTemplate.queryForMap("SELECT `now_price`,`about`,`money` FROM `by_coupon` WHERE `id`=?",new Object[]{couponId});
		int result1=this.jdbcTemplate.update("UPDATE `by_user` SET `money`=`money`-?,`canfetch_money`=`canfetch_money`-? WHERE `id`=? AND `money`>=?",new Object[]{coupon.get("money"),coupon.get("money"),memberId,coupon.get("money")});
		int result2=this.jdbcTemplate.update("INSERT INTO `by_user_coupon` (`now_price`,`about`,`type`,`creatime`,`valid`,`member_id`,`money`,`coupon_id`) VALUES(?,?,2,NOW(),'2020-10-10 00:00:00',?,?,?) ",new Object[]{coupon.get("now_price"),coupon.get("about"),memberId,coupon.get("money"),couponId});
		if(result1<=0 || result2<=0){
			throw new RuntimeException();
		}
	}
	
	@Override
	public List<Map<String, Object>> getAvailCoupon(String memberId) {
		return this.jdbcTemplate.queryForList("SELECT `id`,`org_price` AS `orgPrice`,`now_price` AS `nowPrice`,DATE_FORMAT(`valid`,'%Y-%m-%d %H:%i:%s') AS `valid`,`about`,`type` FROM `by_user_coupon` WHERE `status`=1 AND `member_id`=? AND `valid`>=DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') ORDER BY `creatime` DESC",new Object[]{memberId});
	}
}
