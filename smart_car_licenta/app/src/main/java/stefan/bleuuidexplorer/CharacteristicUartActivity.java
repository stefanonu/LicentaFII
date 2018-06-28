package stefan.bleuuidexplorer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;


public class CharacteristicUartActivity extends Activity {
    private final static String TAG = CharacteristicUartActivity.class.getSimpleName();

    public static int uartIndex=-1;
    TextView mRxData;
    EditText mTxData;
    Button btnSend, btnClear;
    RadioGroup mTxFormatGroup, mRxFormatGroup;

    @Override  protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.characteristic_uart);
        btnSend = (Button) findViewById(R.id.btnUartSend);
        btnClear = (Button) findViewById(R.id.btnUartClear);
        mRxData = (TextView) findViewById(R.id.UartRxData);
        mTxData = (EditText) findViewById(R.id.UartTxData);
        mRxFormatGroup = (RadioGroup) findViewById(R.id.UartRxFormatGroup);
        mTxFormatGroup = (RadioGroup) findViewById(R.id.UartTxFormatGroup);

        // Handler Send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EditText mTxData = (EditText) findViewById(R.id.NordicUartTxData);
                String message = mTxData.getText().toString();
                Log.d(TAG,"message="+message);
                byte[] value; //used for UTF8
                try {
                    //send data to service
                    switch (mTxFormatGroup.getCheckedRadioButtonId()) {
                        case R.id.UartTxFormat_String:
                            Log.d(TAG,"CharRW_WriteFormatString");
                            value = message.getBytes("UTF-8");
                            break;
                        case R.id.UartTxFormat_DEC:
                            Log.d(TAG,"CharRW_WriteFormatDEC");
                            value = new DataManager().decStringToByteArray(message);
                            break;
                        case R.id.UartTxFormat_HEX:
                            Log.d(TAG,"CharRW_WriteFormatHEX");
                            value = new DataManager().hexStringToByteArray(message);
                            break;
                        default:
                            value = null;
                            Log.d(TAG, "Tx Format ERROR");
                    }
                    //send data to service
                    Log.d(TAG,"value=" + new DataManager().byteArrayToHex(value));
                    if (value!=null) {
                        DeviceControlActivity.mBluetoothLeService.writeRXCharacteristic(value);
                        Toast.makeText(getApplication(),String.format("send %d byte(s)",value.length),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplication(), "Tx Data null", Toast.LENGTH_SHORT).show();
                    }

                    //Update the log with time stamp
                    //--String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    //--listAdapter.add("["+currentDateTimeString+"] TX: "+ message);
                    //--messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    //mTxData.setText("");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

         // Handler Clear button
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRxData.setText("");
            }
        });

        DeviceControlActivity.mBluetoothLeService.enableTXNotification();
    }
    @Override  protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (DeviceControlActivity.mBluetoothLeService != null) {
            final boolean result = DeviceControlActivity.mBluetoothLeService.connect(DeviceControlActivity.mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }
    @Override  protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    @Override  public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_characteristic_uart, menu);
        return true;
    }
    @Override  public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.menu_throughput_test100:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern100);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test200:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern200);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test500:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern500);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test1k:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern1K);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test2k:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern2K);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test5k:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern5K);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
            case R.id.menu_throughput_test10k:
                Log.d(TAG, "menu_throughput_test selected");
                mTxData.setText(DataManager.pattern10K);
                //sendMessage.setSelection(sendMessage.getText().length());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        //intentFilter.addAction(BluetoothLeService.RSSI_DATA_AVAILABLE); //ghosty
        return intentFilter;
    }

}
