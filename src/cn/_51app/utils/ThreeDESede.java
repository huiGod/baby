package cn._51app.utils;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class ThreeDESede {
	
	private static final String PASSWORD_CRYPT_KEY = "201551app1234567890321stats";
	
	public static String encryptMode(String src)
			throws Exception {
		DESedeKeySpec dks = new DESedeKeySpec(PASSWORD_CRYPT_KEY.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, securekey);
		byte[] b = cipher.doFinal(src.getBytes());
		return BASE64.encode(b).replaceAll("\r", "").replaceAll("\n", "");

	}

	// 3DESECB解密,key必须是长度大于等于 3*8 = 24 位
	public static String decryptMode(String src)
			throws Exception {
		// --通过base64,将字符串转成byte数组
		byte[] bytesrc = BASE64.decode(src);
		// --解密的key
		DESedeKeySpec dks = new DESedeKeySpec(PASSWORD_CRYPT_KEY.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// --Chipher对象解密
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, securekey);
		byte[] retByte = cipher.doFinal(bytesrc);

		return new String(retByte);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,Object> DecodeToMap(String str) throws Exception{
    	ObjectMapper objectMapper=new ObjectMapper();
    	try {
    		if(StringUtils.isBlank(str)){
    			return new HashMap<>();
    		}
			return objectMapper.readValue(ThreeDESede.decryptMode(str),Map.class);
		} catch (Exception e) {
			return new HashMap<>();
		}
    }
	
	public static void main(String[] args) {
		try {
//			System.err.println(ThreeDESede.encryptMode("{\"memberId\":75012,\"returnflag\":\"M3Slo0lDLHFikeASHOR9SQ==\"}"));
//			System.err.println(ThreeDESede.encryptMode("memberId:1"));
//			System.err.println(ThreeDESede.decryptMode("jyw3eLfye9ZFjybN2WcjsvybxzsbTsY+uy0nFenp4EIQww13xwkXuRDHB9ZW07EvkUhBI6IYANXtt+ZJ6N6pOHz3yLi51ab9"));
			System.err.println(ThreeDESede.decryptMode("M3Slo0lDLHH0rlaqEk+tTg==")); //105554
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}