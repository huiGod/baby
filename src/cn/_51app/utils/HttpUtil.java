package cn._51app.utils;


import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class HttpUtil {
	public static String CHARSET_ENCODING = "UTF-8";
	private static void verifierHostname() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext sslContext = null;
		sslContext = SSLContext.getInstance("TLS");
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		X509TrustManager[] xtmArray = new X509TrustManager[] { xtm };
		sslContext.init(null, xtmArray, new java.security.SecureRandom());
		if (sslContext != null) {
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HostnameVerifier hnv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hnv);
	}

	/**
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String postHttp(String url, String postDataJson) throws Exception{
		String result = "";
		try {
			HttpClient httpClient = HttpClients.custom().build();
			HttpPost httpPost = new HttpPost(url);
			// 根据默认超时限制初始化requestConfig
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(30000).build();
			// 得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
			StringEntity postEntity = new StringEntity(postDataJson, "UTF-8");
			httpPost.addHeader("Content-Type", "text/html");
			httpPost.setEntity(postEntity);
			// 设置请求器的配置
			httpPost.setConfig(requestConfig);
			try {
				if(url.startsWith("https")){
					verifierHostname();
				}
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				httpPost.abort();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		
	}
	public static void main(String[] args) {
		try {
			HttpUtil.postHttp("http://127.0.0.1:8080/baby/app/check.do", "{\"idfa\":\"1\",\"appId\":\"1\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}