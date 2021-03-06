package cn._51app.controller;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.util.AlipayNotify;

import cn._51app.service.GoodsSercice;
import cn._51app.utils.SMTPMailUtil;

@Controller
public class AlipayController {

	@Autowired
	private GoodsSercice goodsSercice;
	
	
	@RequestMapping("/notify")
	public void notify(
			HttpServletRequest request, 
			HttpServletResponse response
			) {
		Map<String, String> params = new HashMap<String, String>();
		String msg="";
		PrintWriter out = null;
		String trade_no="",trade_status="",out_trade_no="",total_fee="",buyer_email="";
		try {
			out = response.getWriter();
			Map<?, ?> requestParams = request.getParameterMap();
			// 交易号
			trade_no = request.getParameter("trade_no");
			// 交易状态
			trade_status = request.getParameter("trade_status");
			// 订单号
			out_trade_no = request.getParameter("out_trade_no");
			// 总金额
			total_fee = request.getParameter("total_fee");
			// 买家手机号
			buyer_email = request.getParameter("buyer_email");
			
			//验证交易是否处理过
			int ids = this.goodsSercice.checkOrderIsPayed(trade_no);
			if (ids > 0) {
				out.println("success");
				return;
			}
			
			for (Iterator<?> iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
				}
				// 如果出现乱码
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"),
				// "UTF-8");
				params.put(name, valueStr);
				// System.out.println(valueStr);
			}
			if (AlipayNotify.verify(params)) {// 验证签名
				if (trade_status.equals("TRADE_SUCCESS")) {// 支付成功
					Map<String,Object> paramMap =new HashMap<String,Object>();
					paramMap.put("order_no", out_trade_no);
					paramMap.put("email", buyer_email);
					paramMap.put("trade_no", trade_no);
					paramMap.put("price", String.valueOf(Double.valueOf(total_fee)));
					paramMap.put("type", 1);
					//记录交易记录
					boolean checkInsert=this.goodsSercice.insertPayRecord(paramMap);
					boolean resultflag=true;
					if(checkInsert){
						// 改变订单状态
						boolean b=false;
							b=goodsSercice.changeOrderStatus(paramMap);
						System.err.println("b:"+b);
						if(b){
							//拆订单(减掉账户余额)
							this.goodsSercice.separateOrder(out_trade_no);
						}else{
							resultflag=false;
						}
					}else{
						//有问题发邮件
						resultflag=false;
					}
					if(!resultflag){
						if(StringUtils.isNotBlank(trade_no)){
							SMTPMailUtil.sendEmail("783878156@qq.com", "DIY订制商城订单异常通知", "异常订单!!! 订单号:"+out_trade_no+"*******"+"交易方式:支付宝*******交易号:"+trade_no+"."+msg);
						}
					}
					out.println("success");
				} else {
					out.println("fail");
				}
			} else {
				out.println("fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 订单交易号出现异常
			if(StringUtils.isNotBlank(trade_no)){
				SMTPMailUtil.sendEmail("783878156@qq.com", "DIY订制商城订单异常通知", "异常订单!!! 订单号:"+out_trade_no+"*******"+"交易方式:支付宝*******交易号:"+trade_no+"."+msg);
			}
		}finally {
			out.close();
		}
	}
	
}
