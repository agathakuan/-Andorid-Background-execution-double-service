package com.agathakuannewgmail.doubleservicetest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ShakeListener implements SensorEventListener {
    private static final int SPEED_THERSHOLD = 3000;
    private static final int UPDATE_INTERVAL = 70;
    private SensorManager sensorManager;
    private Sensor sensor;
    private OnShakeListener onShakeListener;
    private Context mContext;

    private float last_x, last_y, last_z;
    private long update_time;



    public ShakeListener(Context c)
    {
        mContext = c;
        start();
    }

    public void start()
    {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager != null)
        {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if(sensor != null)
        {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);
    }

    public void setOnShakeListener(OnShakeListener listener)
    {
        onShakeListener = listener;
    }



    @Override
    public void onSensorChanged(SensorEvent event)
    {
        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - update_time;

        if(timeInterval <UPDATE_INTERVAL)return;

        update_time = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x -last_x;
        float deltaY = x -last_y;
        float deltaZ = x -last_z;

        last_x = x;
        last_y = y;
        last_z = z;

        double speed = Math.sqrt(deltaX*deltaX+ deltaY*deltaY+ deltaZ*deltaZ)/timeInterval*10000;

        Log.d("ShakeListener", String.valueOf(x)+String.valueOf(y)+String.valueOf(z));

        if(speed >= SPEED_THERSHOLD)
        {
            onShakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnShakeListener
    {
        public void onShake();
    }

}
