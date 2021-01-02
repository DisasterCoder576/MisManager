package com.palanim.mismanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ReceiveSMS extends BroadcastReceiver {
    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    String MSG, phNum="";
    public void onReceive(Context context, Intent intent ){
        Log.i("MINE","HI");
        if( intent.getAction() == SMS_RECEIVED){
            Bundle dataBundle = intent.getExtras();
            if(dataBundle!=null){
                Object [] thispdu = (Object[])dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage [thispdu.length];
                for (int i=0; i<thispdu.length ; i++){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        String format = dataBundle.getString("format");
                        message[i]=SmsMessage.createFromPdu((byte[])thispdu[i], format);
                    }
                    else{
                        message[i]= SmsMessage.createFromPdu((byte[])thispdu[i]);
                    }
                    MSG = message[i].getMessageBody();
                    phNum = message[i].getOriginatingAddress();

                }

               Log.i("ll", MSG+phNum);

            }
        }
        context.sendBroadcast(new Intent("mmm").putExtra("incomingSms", MSG).putExtra("incomingPhoneNumber", phNum));

    }


}
