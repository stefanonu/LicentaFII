package stefan.bleuuidexplorer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import static java.lang.Math.abs;

public class ManualControlActivity extends Activity  {

    SeekBar seekLED1,seekLED2;
    public int seeker_value_1 = 0;
    public int seeker_value_2 = 0;
    int [] values = {-1,-1};
    boolean stop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        seekLED1 = (SeekBar) findViewById(R.id.seekled1);
        seekLED1.setMax(100);

        seekLED2 = (SeekBar) findViewById(R.id.seekled2);
        seekLED2.setMax(50);

        seekLED1.setProgress(50);
        seekLED2.setProgress(0);

        Button button_start = (Button)findViewById(R.id.bstart);
        Button button_stop = (Button)findViewById(R.id.bstop);

        button_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                values[0] = 50;
                values[1] = 20;
                seekLED2.setProgress(20);
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (values[0] != -1 && values[1] != -1) {
                    parseValueToSend(values[0], values[1]);
                }
                handler.postDelayed(this, 50);

            }

        }, 100);  //the time is in miliseconds
        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values[0] = 50;
                values[1] = 0;
            }
        });



        seekLED1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                seeker_value_1 = seekLED1.getProgress();
                values[0] = seeker_value_1;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekLED1.setProgress(50);
                values[0] = 50;

            }
        });

        seekLED2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                seeker_value_2 = i;
                values[1] = seeker_value_2;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekLED2.setProgress(0);
                values[1] = 0;

            }
        });

    }

    Handler handler = new Handler();
    @Override
    public void onBackPressed() {
        values[0] = -1;
        values[1] = -1;
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }



    public void parseValueToSend(int value_1 , int value_2) {
        float value1 = value_1 / 100f;
        float value2 = value_2/ 100f;
        String string_value_1 = String.format("%.2f", value1);
        String string_value_2 = String.format("%.2f",value2);
        String string_value = string_value_1.trim() + "$" + string_value_2.trim() + "#";
        byte[] byte_value;
        byte_value = string_value.getBytes();
        DeviceControlActivity.mBluetoothLeService.writeRXCharacteristic(byte_value);
    }

}

