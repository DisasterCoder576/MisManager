
package com.palanim.mismanager;

        import android.Manifest;
        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.CallLog;
        import android.telephony.PhoneStateListener;
        import android.telephony.SmsManager;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.widget.Toast;

        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.loader.app.LoaderManager;

        import java.util.Date;

public class MissCall1 extends BroadcastReceiver {
/*
    static boolean ring = false;
    static boolean callReceived = false;
int a=0;
    public boolean missed =false;
    public String number1;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                Log.i("incomingNumber : ", incomingNumber);
                number1=incomingNumber;
                a=1;
                Bundle bundle1 = intent.getExtras();

                if (state ==0)
                    return;

                String callerPhoneNumber5 = bundle1.getString("incoming_number");

                // If phone state "Ringing"
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    ring = true;
                    // Get the Caller's Phone Number
                    //  Bundle bundle2 = intent.getExtras();
                    //  String callerPhoneNumber1 = intent.getStringExtra("incoming_number");



                    //   Log.i("MINE11", callerPhoneNumber5);
                    //  Log.i("MINE", callerPhoneNumber1);
                    // mine=callerPhoneNumber1;

                }


                // If incoming call is received
                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    a=0;
                    callReceived = true;

                }


                // If phone is Idle
                if (state==(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
                    if (ring == true && callReceived == false && a==1) {



                        // String callerPhoneNumber2 = bundle1.getString("incoming_number");
                        // String callerPhoneNumber3 = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        //  String callerPhoneNumber4 = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        //  Toast.makeText(mContext, "It was A MISSED CALL from : " + callerPhoneNumber3, Toast.LENGTH_LONG).show();
                        missed =true;
                        String message="HI from android studio";
                        Log.i("Idle", number1);
                        mContext.sendBroadcast(new Intent("INTERNET_LOST"));
                        a=0;
                        //     SmsManager smsManager = SmsManager.getDefault();
                        //    smsManager.sendTextMessage("9445636554", null, message, null, null);




                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);


        // Get the current Phone State
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

       // String state1= getCallState();
        Bundle bundle1 = intent.getExtras();

        if (state == null)
            return;

        String callerPhoneNumber5 = bundle1.getString("incoming_number");

        // If phone state "Ringing"
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            ring = true;
            // Get the Caller's Phone Number
          //  Bundle bundle2 = intent.getExtras();
          //  String callerPhoneNumber1 = intent.getStringExtra("incoming_number");



         //   Log.i("MINE11", callerPhoneNumber5);
           //  Log.i("MINE", callerPhoneNumber1);
            // mine=callerPhoneNumber1;

        }


        // If incoming call is received
        if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            a=0;
            callReceived = true;

        }


        // If phone is Idle
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            // If phone was ringing(ring=true) and not received(callReceived=false) , then it is a missed call
            if (ring == true && callReceived == false && a==1) {



                // String callerPhoneNumber2 = bundle1.getString("incoming_number");
               // String callerPhoneNumber3 = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
              //  String callerPhoneNumber4 = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //  Toast.makeText(mContext, "It was A MISSED CALL from : " + callerPhoneNumber3, Toast.LENGTH_LONG).show();
                missed =true;
                String message="HI from android studio";
                Log.i("Idle", number1);
                mContext.sendBroadcast(new Intent("INTERNET_LOST"));
                a=0;
                //     SmsManager smsManager = SmsManager.getDefault();
                //    smsManager.sendTextMessage("9445636554", null, message, null, null);




            }
        }


    }
*/



    private static final String TAG = "BroadcastReceiver";
    Context mContext;
    String incoming_nr;
    private int prev_state;
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); //TelephonyManager object
        CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager
        Bundle bundle = intent.getExtras();
        String phoneNumberr= bundle.getString("incoming_number");
        Log.v(TAG, "phone Number: "+phoneNumberr);
        mContext=context;
    }
    /* Custom PhoneStateListener */
    public class CustomPhoneStateListener extends PhoneStateListener {



        @Override
        public void onCallStateChanged(int state, String incomingNumber){

            if(incomingNumber!=null&&incomingNumber.length()>0) incoming_nr=incomingNumber;
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:

                    prev_state=state;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    prev_state=state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    if((prev_state==TelephonyManager.CALL_STATE_OFFHOOK)){
                        prev_state=state;
                        //Answered Call which is ended
                        Toast.makeText(mContext, "answered call end", Toast.LENGTH_LONG).show();
                    }
                    if((prev_state==TelephonyManager.CALL_STATE_RINGING)){
                        prev_state=state;
                        //Rejected or Missed call
                        Toast.makeText(mContext, " missed call end"+incomingNumber, Toast.LENGTH_LONG).show();
                        String message="Hey, I don't have my phone now. Hence the missed call.-----------This is an automated message.";
                         //   SmsManager smsManager = SmsManager.getDefault();
                        //   smsManager.sendTextMessage(incomingNumber, null, message, null, null);
                        mContext.sendBroadcast(new Intent("INTERNET_LOST"));


                    }
                    break;
            }
        }

    }

}

