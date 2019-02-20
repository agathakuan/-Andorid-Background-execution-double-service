package com.agathakuannewgmail.doubleservicetest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Binder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Handler;

import android.content.Context;
import android.content.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;

import java.util.Timer;
import java.util.TimerTask;

//關於時鐘的部分　參考：https://blog.csdn.net/qq_31939617/article/details/80118302

public class outputService extends Service {
    public static final String TAG = "OUTPUT_SERVICE";
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private static int count = 0;
    private boolean isPause = false;
    private boolean isStop = true;
    private static int DELAY = 1000;  //1s
    private static int PERIOD = 1000;  //1s

    private IntentFilter mInputIntentFilter;
    private BroadcastReceiver mInputReceiver;

    public static final String mInputBroadcastStringAction = "com.agathakuannewgmail.doubleservicetest.inputstring";

    private NotificationHelper mNotificationHelper;
    private static final int    DEFAULT_ID = 1001;


    private outputBinder mBinder = new outputBinder();

    public outputService() {
    }

    class outputBinder extends Binder
    {
        public void startOutput()
        {
            Log.d(TAG,"OUTPUT START...");
            startTimer();
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();

        mInputIntentFilter = new IntentFilter();
        mInputIntentFilter.addAction(mInputBroadcastStringAction);
        mInputReceiver = new inputReceiver();

        //https://blog.csdn.net/jdsjlzx/article/details/84327815
        mNotificationHelper = new NotificationHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed by Alex");

        registerReceiver(mInputReceiver, mInputIntentFilter);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        stopTimer();
        unregisterReceiver(mInputReceiver);
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    private void startTimer()
    {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null)
        {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "COUNT = "+String.valueOf(count));

                    do{
                        try
                        {
                            Thread.sleep(1000);

                        }catch (InterruptedException e){}

                    }while (isPause);

                    count++;
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
        {
            mTimer.schedule(mTimerTask, DELAY, PERIOD);
        }

    }

    private void stopTimer()
    {
        if(mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
        if(mTimerTask != null)
        {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        count = 0;
    }

    public class inputReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(mInputBroadcastStringAction))
            {
                Log.d("in inputReceiver",intent.getStringExtra("Data"));

                showNotification("From outputService",intent.getStringExtra("Data") );
            }
        }
    }

    public void showNotification(String title, String content)
    {
        NotificationCompat.Builder builder = mNotificationHelper.getNotification(title, content);
        mNotificationHelper.notify(DEFAULT_ID, builder);
    }
}
