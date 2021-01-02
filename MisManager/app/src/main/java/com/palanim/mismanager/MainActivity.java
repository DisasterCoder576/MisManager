

package com.palanim.mismanager;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.app.NotificationCompat;
        import androidx.core.content.ContextCompat;

        import android.Manifest;
        import android.app.Notification;
        import android.app.NotificationChannel;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.icu.util.Calendar;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Bundle;
        import android.provider.CallLog;
        import android.provider.ContactsContract;
        import android.telephony.PhoneNumberUtils;
        import android.telephony.PhoneStateListener;
        import android.telephony.SmsManager;
        import android.telephony.TelephonyManager;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStreamWriter;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashSet;
        import java.util.List;

// import static com.palanim.mismanager.MissCall1.callReceived;
      //  import static com.palanim.mismanager.MissCall1.ring;
class contactObj {
    String number;
    Date date;
}

public class MainActivity extends AppCompatActivity {

    public boolean missed = false;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS =0;
    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    HashSet<String> MissNums = new HashSet<String>();

    public String IncomingSms;
    public String phoneNumberReply;
    public boolean contactExists(Context context, String number, ContentResolver contentResolver) {
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.
                CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()){
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(PhoneNumberUtils.compare(number, phoneNumber)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_LOST"));

        registerReceiver(broadcastReceiver2, new IntentFilter("mmm"));

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALL_LOG)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CALL_LOG}, 1);

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            }
        }


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CALL_PHONE)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);

            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }


        //simpleList = (ListView) findViewById(R.id.simpleListView);
      //  int pic = 5000;

        ArrayList<String> NumsList = new ArrayList<String>();
         ListView lv  =(ListView)findViewById(R.id.simple_listview);


        //  StringBuffer sb = new StringBuffer();
        Cursor managedCursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        //  int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        //  sb.append("Call Details :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);

            Date callDayTime = new Date(Long.valueOf(callDate));

            Log.i("date", callDayTime.toString());

            // String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    NumsList.add(phNumber);
                    break;
            }


        }
        managedCursor.close();
        ArrayList<String> NumsList1= new ArrayList<String>();
        int myvar;
        for(myvar=NumsList.size()-1; myvar>=0; myvar--){
            NumsList1.add(NumsList.get(myvar));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, NumsList1);
        lv.setAdapter(arrayAdapter);
        TextView recents = findViewById(R.id.textView4);
        recents.setText("You have "+ NumsList.size()+" Missed Calls");
        recents.setGravity(Gravity.CENTER);

      //  CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), Nums, pic);
       // simpleList.setAdapter(customAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MINE", "tel:"+NumsList1.get(position));
                Intent intent4 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+NumsList1.get(position)));



                startActivity(intent4);
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("mine", "Permission: YES");

                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("mine", "Permission: YES");

                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("mine", "Permission: YES");

                    }
                    else {
                        Log.i("mine", "Permission: NO");
                    }
                    return;
                }
            }
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // SmsManager smsManager = SmsManager.getDefault();
                    // smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
           /*
                case SMS_PERMISSION_CODE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted, yay! Do the
                        // SMS related task you need to do.

                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }
            */

        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // internet lost alert dialog method call from here...
            Log.i("MINE", "from main");
            missed=true;
            Log.i("MINE", Boolean.toString(missed));
            //  Log.i("Main", getCallDetails(MainActivity.this));
            //  Intent intent1 =new Intent(MainActivity.this, MainActivity2.class);
            //  startActivity(intent1);
            Log.i("MINE", getLastNumber());
            String message="Hey, I don't have my phone now. Hence the missed call.-----------This is an automated message.";
           SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(getLastNumber(), null, message, null, null);

            MissNums.add(getLastNumber());

            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("myfile.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(MissNums.toString());
                outputStreamWriter.close();
                Log.i("MINE", "File success");
            }
            catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }


        }

    };

    BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


                //Extract your data - better to use constants...
                IncomingSms=intent.getStringExtra("incomingSms");//
                phoneNumberReply=intent.getStringExtra("incomingPhoneNumber");
                if(MissNums.contains(phoneNumberReply)){

                    Log.i("NOTIF", IncomingSms);

                    NotificationManager mNotificationManager;

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getBaseContext(), "notify_001");
                    Intent ii = new Intent(getBaseContext(), MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, ii, 0);

                    NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                    // bigText.bigText(verseurl);
                    bigText.setBigContentTitle(phoneNumberReply);
                    bigText.setSummaryText("Missed Call");

                    mBuilder.setContentIntent(pendingIntent);
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                    mBuilder.setContentTitle(phoneNumberReply);
                    mBuilder.setContentText(IncomingSms);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setStyle(bigText);

                    mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    {
                        String channelId = "Your_channel_id";
                        NotificationChannel channel = new NotificationChannel(
                                channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_HIGH);
                        mNotificationManager.createNotificationChannel(channel);
                        mBuilder.setChannelId(channelId);
                    }

                    mNotificationManager.notify(0, mBuilder.build());

                }

            }



    };


    BroadcastReceiver mServiceReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Extract your data - better to use constants...
            String IncomingSms=intent.getStringExtra("incomingSms");//
            String phoneNumber=intent.getStringExtra("incomingPhoneNumber");
            if(MissNums.contains(phoneNumber)){
                Log.i("NOTIF", IncomingSms);
                NotificationManager mNotificationManager;

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getBaseContext(), "notify_001");
                Intent ii = new Intent(getBaseContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, ii, 0);

                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                // bigText.bigText(verseurl);
                bigText.setBigContentTitle("Missed Call");
                bigText.setSummaryText("reply from");

                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
                mBuilder.setContentTitle(phoneNumber);
                mBuilder.setContentText(IncomingSms);
                mBuilder.setPriority(Notification.PRIORITY_MAX);
                mBuilder.setStyle(bigText);

                mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    String channelId = "Your_channel_id";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(channel);
                    mBuilder.setChannelId(channelId);
                }

                mNotificationManager.notify(0, mBuilder.build());
                /*
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.ic_launcher_background) //set icon for notification
                                .setContentTitle("Notifications Example") //set title of notification
                                .setContentText("This is a notification message")//this is notification message
                                .setAutoCancel(true) // makes auto cancel of notification
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


                Intent notificationIntent = new Intent(getApplicationContext(), NotificationView.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //notification message will get at NotificationView
                notificationIntent.putExtra("message", "This is a notification message");

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                // Add as notification
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());*/
                // Builds your notification

            }

        }
    };
    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(mServiceReceiver != null){
                unregisterReceiver(mServiceReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mServiceReceiver , filter);
    }*/




    public String getLastNumber() {

        //this help you to get recent call
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Context context = this;
        Cursor managedCursor = context.getContentResolver().query(contacts, null, null,
                null, android.provider.CallLog.Calls.DATE + " DESC limit 1;");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        //    int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        //   int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        //     int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        StringBuffer sb = new StringBuffer();
        managedCursor.moveToNext();
        String phNumber = managedCursor.getString(number);
        //   String callType = managedCursor.getString(type);
        //   String callDate = managedCursor.getString(date);
        //  String callDayTime = new Date(Long.valueOf(callDate)).toString();
        //  int callDuration = managedCursor.getInt(duration);
        managedCursor.close();

        // int dircode = Integer.parseInt(callType);
        /*
        sb.append("Phone Number:--- " + phNumber + " ,Call Date:--- " + callDayTime + " ,Call duration in sec :--- " + callDuration);
        sb.append("\n----------------------------------");
        Log.d("calllogs", "getLastNumber: "+"Phone Number:--- " + phNumber + " ,Call Date:--- " + callDayTime + " ,Call duration in sec :--- " + callDuration);*/
        return phNumber;
    }
}

