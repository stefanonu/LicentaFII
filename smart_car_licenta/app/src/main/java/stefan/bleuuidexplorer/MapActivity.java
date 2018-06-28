package stefan.bleuuidexplorer;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.util.List;


public class MapActivity extends Activity {
    private static byte[] readValue;
    private List<String> stringList;
    private CanvasView canvasView;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    public float [] fvalues = {-1.0f,-1.0f};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        canvasView = findViewById(R.id.canvas);


        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mNotifyCharacteristic = BluetoothLeService.mTargetCharacteristic;
                DeviceControlActivity.mBluetoothLeService.readCharacteristic(BluetoothLeService.mTargetCharacteristic);
                DeviceControlActivity.mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                handler.postDelayed(this, 1000);

            }

        }, 2000);  //the time is in miliseconds*/


    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                try {
                    displayRxData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            Log.d("TAG", "Broadcast type "+action);
        }

    };




    public void clearCanvas(View v){
        canvasView.clearCanvas();
    }
    public void showAngle(View v) { canvasView.clearCanvas(); }





    private static void displayRxData(byte[] data) throws UnsupportedEncodingException {
        if (data != null) {
            StringBuilder strData = new StringBuilder();
            for(byte byteChar : data)
                strData.append(byteChar & (0xFF));
            Log.d("tag","VALUE DATA => " + strData.toString());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


    }

    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    Handler handler = new Handler();
    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        CanvasView.fvalues[0] = -1.0f;
        CanvasView.fvalues[1] = -1.0f;
        super.onBackPressed();
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(DeviceScanActivity.DEVICE_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.RSSI_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        return intentFilter;
    }
}
