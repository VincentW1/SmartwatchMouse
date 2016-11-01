package hci.smartwatchmouse;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import java.util.List;
import android.util.Log;

public class MainActivity extends Activity
        implements SensorEventListener{

    private TextView mTextView;
    private Sensor accSensor;
    private TextView accView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); sensorManager.registerListener(this, accSensor, 100 * 1000);
        accView = (TextView) findViewById(R.id.line1);
    }

    @Override
    public void onSensorChanged(SensorEvent event) { Log.d("MainActivity", "onSensorChanged: " + event); if (event.sensor == accSensor) {
        float[] vs = event.values;
        accView.setText(String.format("a: %.3f, %.3f, %.3f", vs[0], vs[1], vs[2])); }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { Log.d("MainActivity", "onAccuracyChanged: " + sensor + ", " + accuracy);
    }
}
