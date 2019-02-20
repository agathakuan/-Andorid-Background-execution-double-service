package com.agathakuannewgmail.doubleservicetest;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;
import android.content.BroadcastReceiver;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "Main Activity";

    private TextView mActivityInfo;
    private TextView mServiceInfo;
    private inputService.inputBinder inputBinder;
    private outputService.outputBinder outputBinder;

    private Button mInputStartService;
    private Button mInputStopService;
    private Button mInputBindService;
    private Button mInputUnbindService;

    private Button mOutputStartService;
    private Button mOutputStopService;
    private Button mOutputBindService;
    private Button mOutputUnbindService;

    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;

    //https://www.truiton.com/2014/09/android-service-broadcastreceiver-example/
    private IntentFilter mInputIntentFilter;
    private BroadcastReceiver mInputReceiver;

    public static final String mInputBroadcastStringAction = "com.agathakuannewgmail.doubleservicetest.inputstring";
    public static final String mInputBroadcastIntAction = "com.agathakuannewgmail.doubleservicetest.inputint";
    public static final String mInputBroadcastArrayListAction = "com.agathakuannewgmail.doubleservicetest.inputarraylist";

    private ServiceConnection inputConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inputBinder =(inputService.inputBinder) service;
            inputBinder.startInput();

            printInputServiceInfo("onInputServiceConnected ...");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            printInputServiceInfo("onInputServiceDisconnected ...");

        }
    };

    private ServiceConnection outputConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            outputBinder = (outputService.outputBinder) service;
            outputBinder.startOutput();

            printOutputServiceInfo("onOutputServiceConnected ...");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            printOutputServiceInfo("onOutputServiceDisconnected ...");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivityInfo = (TextView)findViewById(R.id.activity_info);
        mServiceInfo = (TextView)findViewById(R.id.service_info);

        mInputStartService = (Button)findViewById(R.id.start_service);
        mInputStopService = (Button)findViewById(R.id.stop_service);
        mInputBindService = (Button)findViewById(R.id.bind_service);
        mInputUnbindService = (Button)findViewById(R.id.unbind_service);



        mInputStartService.setOnClickListener(this);
        mInputStopService.setOnClickListener(this);
        mInputBindService.setOnClickListener(this);
        mInputUnbindService.setOnClickListener(this);


        mOutputStartService = (Button)findViewById(R.id.start_service_2);
        mOutputStopService = (Button)findViewById(R.id.stop_service_2);
        mOutputBindService = (Button)findViewById(R.id.bind_service_2);
        mOutputUnbindService = (Button)findViewById(R.id.unbind_service_2);

        mOutputStartService.setOnClickListener(this);
        mOutputStopService.setOnClickListener(this);
        mOutputBindService.setOnClickListener(this);
        mOutputUnbindService.setOnClickListener(this);
        /*
        */


        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.agathakuannewgmail.doubleservicetest");
        localReceiver  = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

        mInputIntentFilter = new IntentFilter();
        mInputIntentFilter.addAction(mInputBroadcastStringAction);
        mInputIntentFilter.addAction(mInputBroadcastIntAction);
        mInputIntentFilter.addAction(mInputBroadcastArrayListAction);
        mInputReceiver = new inputserviceReceiver();

    }

    @Override
    public void onClick(View v)
    {

        switch(v.getId())
        {
            case R.id.start_service:
                Intent mInputService_start = new Intent(this, inputService.class);
                startService(mInputService_start);

                printInputServiceInfo("start service in activity");

                break;

            case R.id.stop_service:
                Intent mInputService_stop = new Intent(this, inputService.class);
                stopService(mInputService_stop);

                printInputServiceInfo("stop service in activity");

                break;

            case R.id.bind_service:
                Intent mInputService_bind = new Intent(this, inputService.class);
                bindService(mInputService_bind, inputConnect , BIND_AUTO_CREATE);

                printInputServiceInfo("bind service in activity");

                Intent intent = new Intent("com.agathakuannewgmail.doubleservicetest");
                localBroadcastManager.sendBroadcast(intent);
                break;

            case R.id.unbind_service:
                unbindService(inputConnect);

                printInputServiceInfo("unbind service in activity");
                break;

            case R.id.start_service_2:
                Intent mOutputService_start = new Intent(this, outputService.class);
                startService(mOutputService_start);

                printOutputServiceInfo("start output service in activity");

                break;

            case R.id.stop_service_2:
                Intent mOutputService_stop = new Intent(this, outputService.class);
                stopService(mOutputService_stop);

                printOutputServiceInfo("stop output service in activity");

                break;

            case R.id.bind_service_2:
                Intent mOutputService_bind = new Intent(this, outputService.class);
                bindService(mOutputService_bind, outputConnect , BIND_AUTO_CREATE);

                printOutputServiceInfo("bind  output service in activity");
                break;

            case R.id.unbind_service_2:
                unbindService(outputConnect);

                printOutputServiceInfo("unbind output service in activity");
                break;

            default:
                break;

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mInputReceiver);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mInputReceiver, mInputIntentFilter);

    }

    @Override
    protected void onDestroy() {
        Intent mInputService_stop = new Intent(this, inputService.class);
        stopService(mInputService_stop);

        Intent mOutputService_stop = new Intent(this, outputService.class);
        stopService(mOutputService_stop);

        localBroadcastManager.unregisterReceiver(localReceiver);
        super.onDestroy();
    }

    private void printOutputServiceInfo(String info)
    {
        mActivityInfo.setText(info);
    }

    private void printInputServiceInfo(String info)
    {
        mServiceInfo.setText(info);
    }

    class LocalReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
        }
    }

    class inputserviceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
           if(intent.getAction().equals(mInputBroadcastIntAction))
            {
                Toast.makeText(context, "received:"+String.valueOf(intent.getIntExtra("Data", 0)), Toast.LENGTH_SHORT).show();

            }

        }
    }

}
