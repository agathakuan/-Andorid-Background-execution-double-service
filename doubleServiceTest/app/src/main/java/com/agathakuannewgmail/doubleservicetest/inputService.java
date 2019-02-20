package com.agathakuannewgmail.doubleservicetest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.os.Handler;

//https://blog.csdn.net/guolin_blog/article/details/11952435

public class inputService extends Service {
    public static final String TAG = "INPUT_SERVICE";
    Handler inputHandler ;
    ShakeListener mShakeListener = null;

    private inputBinder mBinder = new inputBinder();

    public inputService() {
    }

    class inputBinder extends Binder
    {
        public void startInput()
        {
            Log.d(TAG,"INPUT START...");
            inputHandler.post(updateMsgThread);
        }
    }

    Runnable updateMsgThread = new Runnable()
    {
        int i = 1;
        @Override
        public void run()
        {
            i = i+1;
            if (i!= 0)
            {
                Log.d(TAG,"updateMsgThread..."+String.valueOf(i));

                Intent broadcast2Main = new Intent();
                broadcast2Main.setAction(MainActivity.mInputBroadcastIntAction);
                broadcast2Main.putExtra("Data", i);
                sendBroadcast(broadcast2Main);

                /*
                Intent broadcast2OutputService = new Intent();
                broadcast2OutputService.setAction(outputService.mInputBroadcastStringAction);
                broadcast2OutputService.putExtra("Data","this is from inputService");
                sendBroadcast(broadcast2OutputService);
                */

            }

            inputHandler.postDelayed(this, 1000);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        inputHandler = new Handler();
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener()
        {
            public void onShake()
            {
                mShakeListener.stop();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent broadcast2OutputService = new Intent();
                        broadcast2OutputService.setAction(outputService.mInputBroadcastStringAction);
                        broadcast2OutputService.putExtra("Data","shaking!!");
                        sendBroadcast(broadcast2OutputService);

                        mShakeListener.start();

                    }
                },600);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed by Alex");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mShakeListener != null)
        {
            mShakeListener.stop();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
