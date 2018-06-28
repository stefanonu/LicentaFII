package stefan.bleuuidexplorer;

import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class CanvasView extends View  {

    Paint paint;
    Paint mPaint;

    private static final float TOLERANCE = 5;
    Bitmap bm_car;
    int bm_car_centerX, bm_car_centerY;
    static byte [] readValue;
    public boolean stopFlag;

    public static float[] fvalues = {-1.0f,-1.0f};

    Path carToMovePath;
    Path mPath;
    private float mX,mY;


    PathMeasure pathMeasure;
    float pathLength;
    int index =0;
    float step;
    float distance;
    float curX, curY;

    float currentAngle;
    float targetAngle;
    float angleEveryStep;

    float[] pos = {0,0};
    float[] tan = {0,0};

    int counterSpeed = 4;
    boolean flag = false;
    Matrix car_matrix;
    float [] values = {-1.0f,-1.0f,-1.0f};
    Path inviziblePath;
    private int counter =0;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
        flag = true;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fvalues[0] != -1.0f && fvalues[1] != -1.0f) {
                    if (stopFlag){
                        flag = false;
                        fvalues[0] = 0.50f;
                    }
                    parseValueToSend(flag, fvalues[0]);
                }
                handler.postDelayed(this, 300);

            }

        }, 200);  //the time is in miliseconds

    }


    public void initMyView(){
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(0.0001f);
        paint.setStyle(Paint.Style.STROKE);

        bm_car = BitmapFactory.decodeResource(getResources(), R.drawable.carx);
        bm_car_centerX = bm_car.getWidth()/2;
        bm_car_centerY = bm_car.getHeight()/2;

        carToMovePath = new Path();
        mPath = new Path();


        pos = new float[2];
        tan = new float[2];

        car_matrix = new Matrix();

        inviziblePath = new Path();
    }


    @Override
    protected void onDraw (final Canvas canvas) {

        if (carToMovePath.isEmpty()) {
            return;
        }
        /*if (DeviceControlActivity.mBluetoothLeService != null) {
            final boolean result = DeviceControlActivity.mBluetoothLeService.connect(DeviceControlActivity.mDeviceAddress);
            Log.d("tag", "Connect request result=" + result);
            DeviceControlActivity.mBluetoothLeService.readCharacteristic(BluetoothLeService.mTargetCharacteristic);
        }*/

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(carToMovePath, paint);
        moveCar(canvas);
    }


    protected void moveCar(Canvas canvas) {
        car_matrix.reset();

        if ((targetAngle - currentAngle) > angleEveryStep) {
            //Log.d("Tag","current angle " + targetAngle);
            currentAngle += angleEveryStep;
            if(180f - abs(targetAngle) > 0.3) {
                convertAngleEeveryStep(currentAngle * (-1));
            }
            car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
            car_matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm_car, car_matrix, null);

            invalidate();
        } else if ((currentAngle - targetAngle) > angleEveryStep) {
            // Log.d("Tag","current angle " + targetAngle);
            currentAngle -= angleEveryStep;
            if(180f - abs(targetAngle) > 0.3f) {
                convertAngleEeveryStep(currentAngle * (-1));

            }
            car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
            car_matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm_car, car_matrix, null);

            invalidate();
        }
        else {
            //Log.d("Tag","current angle tangent " + currentAngle);
            currentAngle = targetAngle;
            if (distance < pathLength) {
                pathMeasure.getPosTan(distance, pos, tan);

                targetAngle = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);

                if(180f - abs(targetAngle) > 0.2) {
                    convertAngleEeveryStep(currentAngle * (-1));
                }
                car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
                curX = pos[0] - bm_car_centerX;
                curY = pos[1] - bm_car_centerY;
                car_matrix.postTranslate(curX, curY);
                canvas.drawBitmap(bm_car, car_matrix, null);

                distance += step;
                car_matrix.reset();
                invalidate();
            }
            else{
                if(180f - abs(targetAngle) > 0.2) {
                    convertAngleEeveryStep(currentAngle * (-1));
                    // Log.d("tag","current angle " + currentAngle);
                }
                car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
                car_matrix.postTranslate(curX, curY);

                canvas.drawBitmap(bm_car, car_matrix, null);
            }
        }
    }

    /*public void convertAngleEeveryStep(float myAngleToConvert)
    {
        if ((180.0 - myAngleToConvert) > 0.3f) {

            values[index] = myAngleToConvert;
            if (index == 2) {
                if (values[0] != -1.0f && values[1] != -1.0f && values[2] != -1.0f) {
                    if ((abs(values[2] - values[0]) < 0.1f) || abs(values[2]-values[1]) < 0.1f ) {
                        fvalues[0] = 0.5f;//1.00f;
                        fvalues[1] = 1.65f;
                    } else {
                        values[0] = values[1];
                        values[1] = values[2];
                        values[2] = -1.0f;
                        index = 0;
                        fvalues[0] = (abs(myAngleToConvert) + values[1] ) / 180.0f;
                        fvalues[1] = 1.65f;
                    }

                }
            }
            else {
                if(abs(abs(values[1]) - abs(values[0])) > 0.45f) {
                    fvalues[0] = (abs(myAngleToConvert) + 45) / 180;
                    fvalues[1] = 1.65f;
                }
                else
                {
                    if ((abs(values[1] - values[0]) < 0.25f)){
                        fvalues[0] = 0.5f;//1.00f;
                        fvalues[1] = 1.65f;
                    }
                    else {
                        fvalues[0] = (abs(myAngleToConvert) / 180f);
                        fvalues[1] = 1.65f;
                    }
                }
                index++;
            }
        }
        if ( (pathLength - distance) < 1  && pathLength != 0)
        {
            fvalues[0] = 1.00f;
            fvalues[1] = 1.00f;
            this.stopFlag = true;

        }
    }*/
    public void convertAngleEeveryStep(float myAngleToConvert){
        if(myAngleToConvert < 0 && abs(myAngleToConvert) < 180){
            myAngleToConvert = myAngleToConvert *(-1);
        }
        fvalues[0] = myAngleToConvert / 180f;
        fvalues[1] = 1.00f;
        Log.d("tag","value " + fvalues[0]);

        if ( (pathLength - distance) < 1  && pathLength != 0)
        {
            fvalues[0] = 1.00f;
            fvalues[1] = 1.00f;
            this.stopFlag = true;
        }

    }


    public static void parseValueToSend(boolean value_1, float value_2) {
        //String string_value_1 = String.format("%.2f", value_1);
        String string_value_2="";
        if(value_1){
            string_value_2 = String.format("%.2f",0.26);
        }
        else {
            string_value_2 = String.format("%.2f", 0.00);
        }
        //Log.d("tag","value " + string_value_1);
        String string_value_1 = String.format("%.2f",value_2);
        String string_value = string_value_1.trim() + "$" + string_value_2.trim() + "#";
        byte[] byte_value;
        byte_value = string_value.trim().getBytes();
        DeviceControlActivity.mBluetoothLeService.writeRXCharacteristic(byte_value);
    }




    public void resetValues(){
        curX = 0;
        curY = 0;
        angleEveryStep = 1;
        currentAngle = 0;
        targetAngle = 0;
        step = 1;
        distance = 0;

    }

    public void clearCanvas(){
        inviziblePath.reset();
        carToMovePath.reset();
        mPath.reset();
        fvalues[0] = 1.00f;
        counterSpeed = 4;
        this.flag = false;
        fvalues[1] = 1.00f;
        this.stopFlag = true;
        invalidate();

    }

    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        inviziblePath.moveTo(x,y);
        mX = x;
        mY = y;
    }

    private void upTouch() {
        mPath.lineTo(mX, mY);
        inviziblePath.lineTo(mX,mY);
    }

    private void moveTouch(float x, float y){
        float dx = abs(x-mX);
        float dy = abs(y-mY);

        if(dx >= TOLERANCE || dy >= TOLERANCE){
            mPath.quadTo(mX,mY,(x + mX) / 2,(y + mY) / 2);
            inviziblePath.quadTo(mX,mY,(x + mX) / 2,(y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                inviziblePath.reset();
                startTouch(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_UP:
                upTouch();

                carToMovePath = new Path(inviziblePath);
                pathMeasure = new PathMeasure(carToMovePath, false);
                pathLength = pathMeasure.getLength();
                Log.d("tag", "distance : " + pathLength);
                resetValues();
                invalidate();
                break;
        }
        return true;
    }


}