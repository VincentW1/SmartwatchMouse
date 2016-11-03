package hci.smartwatchmouse;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.List;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class MainActivity extends WearableActivity
        implements SensorEventListener{

    private Sensor accSensor, gyroSensor;
    private SensorManager sensorManager;
    private TextView sensorX,sensorY, sensorZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            Log.d("MainActivity", sensor.toString());
        }

        setAmbientEnabled();
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);

        sensorManager.registerListener(this, accSensor, 100 * 1000);

        sensorX = (TextView) findViewById(R.id.sensor_X);
        sensorY = (TextView) findViewById(R.id.sensor_Y);
        sensorZ = (TextView) findViewById(R.id.sensor_Z);


//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                //mTextView = (TextView) stub.findViewById(R.id.sensor_Y);
//            }
//        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("MainActivity", "onSensorChanged: " + event);
        if (event.sensor == accSensor) {
            float[] vs = event.values;
            sensorY.setText(String.format("a: %.3f, %.3f, %.3f", vs[0], vs[1], vs[2]));
        }
        if (event.sensor == gyroSensor){
            float[] v = event.values;
            sensorX.setText(String.format("a: %.3f, %.3f, %.3f", v[0], v[1], v[2]));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { Log.d("MainActivity", "onAccuracyChanged: " + sensor + ", " + accuracy);
    }
}
