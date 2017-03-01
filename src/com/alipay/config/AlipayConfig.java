package com.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	//赚零钱
//	public static String partner = "2088121619699130";
	// 商户的私钥
//	public static String privatse_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANRyr5ehtescqQWTY9QcMloLsI32XFyHTzkIsIe9JWQlrPB7pPJEtqERzk1rgbeTTZ26KSKJI+0/3fYgt6MdMnXPeeBA1FG2sHKU6droyztOUPukfNfD8I5dNemHN1uI9rWIGCC3PBHbxSMzbeUhniVp4DAy2Q72dG09Hg8Fpb2ZAgMBAAECgYEAtyqKKfnKlwMG1z/UejQ1VV1uw3b8+unObCYJzX5OlqHFETIJbLfBne10KVvfYx8ldC/k91m2F/9Qp7xdl8y8izRKZjhN4q5h4yM5BLinf7y9FS/hZ4yAwSubmo52IJMwkCYbjwHeorcAi8P2yMUa9pZMPW50ga1tAiOopHvyFAECQQDqJQNFf8N+yvJMrkySjijLW+1gIS7mowWOR4Ybachhsj75GrnHr4n3ppRAUdlMzZqRury8JBm0ZgxfSrxDzQCpAkEA6Ec5P/XMCIoeuR4gCPsarwu8jD0bhIAt9UHMA/UWIz0y7lWQRFZSzXOKeJbS1hD4Oggl4v+x8SBk7EZUJmW7cQJAEGQ+8mKbYQj8JXoeSjRBjtsEpzIrgmHvRaKkNM8XBB/iYLYKQ3x6gCfdQRN5zvKl24XjKJiuY/6w5wroWybC4QJAIMNLE/Z7nQi+ZKYXJq0kimRBaBaGdx4NXXa2bCh5wuay9GMlQj5a15fghDnh60fhe24Cyg3+71/+XEVfQTtAQQJBAKA9wRL/eAo1KvOA0eyQSbKujkgc9ace7xy3+H+BpuJ1IJFWWGGiQW8W6jyDkhMkOASgQP28WRqDhlV6d8HVgoQ=";
	
	// 支付宝的公钥，无需修改该值
//	public static String alpublic_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUcq+XobXrHKkFk2PUHDJaC7CN9lxch085CLCHvSVkJazwe6TyRLahEc5Na4G3k02duikiiSPtP932ILejHTJ1z3ngQNRRtrBylOna6Ms7TlD7pHzXw/COXTXphzdbiPa1iBggtzwR28UjM23lIZ4laeAwMtkO9nRtPR4PBaW9mQIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	public static String partner = "2088221293913734";
	
	// 商户的私钥
	public static String privatse_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+Rpb9y5q6WGhJcKr4TFAG8UuAtRoEbBygudwYaXTrecmZwHXFcy8A9tVziEyEk9MR6oajwVkROJ+MlE4pvCnXH6JNKBN7sw1K/Uza8bEZVPxgVXJL0NfFG+74K37MkRUKHGKhLjt2ABVCfgHn2nWisO6LzWNpKYUiqjfnr7PPwwIDAQAB";
	
	// 支付宝的公钥，无需修改该值
	public static String alpublic_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "/data/log/alipay/";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";

}