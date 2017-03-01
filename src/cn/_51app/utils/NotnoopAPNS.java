package cn._51app.utils;

import java.util.List;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

public class NotnoopAPNS {
//	private static final String certPath="D:/test.p12";
	private static final String certPath="/data/resource/cert/51baby.p12";
	//91铃声
//	private static final String certPassword="91Ring";
	//最美铃声BestRing
	private static final String certPassword="BestRing";
			
	public static void sendMessage(String[] devices,String message) {
//		String[] devices = {"4a71da50e3b0611391b0a5e211a1eb43943a9721c82b349ae6dd304d0df57aa6"};

		try {
//			List<PushedNotification> notifications = Push.alert("恭喜成功试玩"+name+"应用,获得奖励"+money+"元", certPath, certPassword,
//					false, devices);
			List<PushedNotification> notifications = Push.alert(message, certPath, certPassword,
					true, devices);
			for (PushedNotification notification : notifications) {
				if (notification.isSuccessful()) {
					/* Apple accepted the notification and should deliver it */
					System.out
							.println("Push notification sent successfully to: " + notification.getDevice().getToken());
					/* Still need to query the Feedback Service regularly */
				} else {
					String invalidToken = notification.getDevice().getToken();
					/*
					 * Add code here to remove invalidToken from your database
					 */

					/* Find out more about what the problem was */
					Exception theProblem = notification.getException();
//					theProblem.printStackTrace();

					/*
					 * If the problem was an error-response packet returned by
					 * Apple, get it
					 */
					ResponsePacket theErrorResponse = notification.getResponse();
					if (theErrorResponse != null) {
//						System.out.println(theErrorResponse.getMessage());
					}
				}
			}

		} catch (KeystoreException e) {
			/* A critical problem occurred while trying to use your keystore */
//			e.printStackTrace();

		} catch (CommunicationException e) {
			/*
			 * A critical communication error occurred while trying to contact-
			 * Apple servers
			 */
//			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		NotnoopAPNS.sendMessage(new String[]{"2143435c213ff2eaab9c7f6b48a251b9100d6cd0b8b5a92de9cfe8f849e1fe94"}, "测试推送");
	}
}
