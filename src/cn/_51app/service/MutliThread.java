package cn._51app.service;

import org.apache.commons.lang.StringUtils;

import cn._51app.utils.NotnoopAPNS;

public class MutliThread extends Thread{
	private String deviceToken;
	private String message;
	
	
	
	public MutliThread(String deviceToken, String message) {
		super();
		this.deviceToken = deviceToken;
		this.message = message;
	}
	public String getDeviceToken() {
		return deviceToken;
	}
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void run(){
		if(StringUtils.isNotBlank(deviceToken)){
			NotnoopAPNS.sendMessage(new String[]{deviceToken}, message);
		}
    }
}
