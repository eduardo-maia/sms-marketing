package com.eduardomaia.smsmarketing;

import java.util.ArrayList;
import android.telephony.SmsManager;

public class SendSMS {

	public static void sendSMS(String telephone, String message) {
	    SmsManager smsManager = SmsManager.getDefault();
	    ArrayList<String> parts = smsManager.divideMessage(message); 
	    smsManager.sendMultipartTextMessage(telephone, null, parts, null, null);
	}
	
}
