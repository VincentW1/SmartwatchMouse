package hci.smartwatchmouse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends WearableActivity
        implements SensorEventListener {

    private static final int SCAN_PERIOD = 5;
    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothManager bm;
    BluetoothAdapter ba;
    BluetoothLeScanner bs;
    Handler handler = new Handler();
    Handler mHandler = new Handler();
    boolean mScanning = true;
    private Sensor accSensor, gyroSensor, accCleanSensor, gameRotationSensor;

    // Bluetooth LE ------------------------------------------------------------
    private SensorManager sensorManager;
    private TextView sensorX, sensorY, sensorZ;
    private boolean isBluetooth_LE = true;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            Log.d("MainActivity", sensor.toString());
        }

        setAmbientEnabled();
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        accCleanSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gameRotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);

        sensorManager.registerListener(this, accSensor, 100 * 1000);
        sensorManager.registerListener(this, gyroSensor, 100 * 1000);
        sensorManager.registerListener(this, accCleanSensor, 100 * 1000);
        sensorManager.registerListener(this, gameRotationSensor, 100 * 1000);


        sensorX = (TextView) findViewById(R.id.sensor_X);
        sensorY = (TextView) findViewById(R.id.sensor_Y);
        sensorZ = (TextView) findViewById(R.id.sensor_Z);

        // final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        //  stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
        //  @Override
        //      public void onLayoutInflated(WatchViewStub stub) {
        //          mTextView = (TextView) stub.findViewById(R.id.sensor_Y);
        //      }
        //  });

        // Bluetooth LE ------------------------------------------------------------

        // Bluetooth Check: Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            isBluetooth_LE = false;
            finish();
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


    }

   @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("MainActivity", "onSensorChanged: " + event);
        if (event.sensor == gyroSensor) {
            float[] v = event.values;
            sensorX.setText(String.format("a: %.3f, %.3f, %.3f", v[0], v[1], v[2]));
        }
        if (event.sensor == accSensor) {
            float[] vs = event.values;
            sensorY.setText(String.format("a: %.3f, %.3f, %.3f", vs[0], vs[1], vs[2]));
        }
        if (event.sensor == gameRotationSensor) {
            float[] vx = event.values;
            sensorZ.setText(String.format("a: %.3f, %.3f, %.3f", vx[0], vx[1], vx[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("MainActivity", "onAccuracyChanged: " + sensor + ", " + accuracy);
    }

    /*
    public void bluetoothLE() {
        bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ba = bm.getAdapter();
        bs = ba.getBluetoothLeScanner(); // API 21
        ...
        Log.d("MainActivity", "version: " + android.os.Build.VERSION.SDK_INT);
        handler.postDelayed(new Runnable() {
            public void run() {...}
        }, 10000); // turn scanning off after 10 seconds
        if (android.os.Build.VERSION.SDK_INT < 21) {
            ba.startLeScan(bleStartScan);
        } else {
            bs.startScan(sc);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void scanLeDevice(final boolean enable) {
        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }*/
}