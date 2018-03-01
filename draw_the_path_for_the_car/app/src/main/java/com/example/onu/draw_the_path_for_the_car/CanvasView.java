package com.example.onu.draw_the_path_for_the_car;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Onu on 2/6/2018.
 */

public class CanvasView extends View  {

    Paint paint;
    Paint mPaint;

    private static final float TOLERANCE = 5;
    Bitmap bm_car;
    int bm_car_centerX, bm_car_centerY;


    Path carToMovePath;
    Path mPath;
    private float mX,mY;


    PathMeasure pathMeasure;
    float pathLength;

    float step;
    float distance;
    float curX, curY;

    float currentAngle;
    float targetAngle;
    float angleEveryStep;

    float[] pos = {0,0};
    float[] tan = {0,0};

    Matrix car_matrix;

    Path inviziblePath;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
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
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(carToMovePath, paint);
        moveCar(canvas);
    }
    protected void moveCar(Canvas canvas) {
        car_matrix.reset();

        if ((targetAngle - currentAngle) > angleEveryStep) {
            currentAngle += angleEveryStep;
            car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
            car_matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm_car, car_matrix, null);

            invalidate();
        } else if ((currentAngle - targetAngle) > angleEveryStep) {
            currentAngle -= angleEveryStep;
            car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
            car_matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm_car, car_matrix, null);

            invalidate();
        }
        else {
            currentAngle = targetAngle;
            if (distance < pathLength) {
                pathMeasure.getPosTan(distance, pos, tan);

                targetAngle = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);

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
                car_matrix.postRotate(currentAngle, bm_car_centerX, bm_car_centerY);
                car_matrix.postTranslate(curX, curY);
                canvas.drawBitmap(bm_car, car_matrix, null);
            }
        }
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
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);

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

                resetValues();
                invalidate();
                break;
        }
        return true;
    }

}
