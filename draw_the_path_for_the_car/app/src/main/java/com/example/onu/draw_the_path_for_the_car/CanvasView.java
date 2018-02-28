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
    Bitmap bm;
    int bm_offsetX, bm_offsetY;

    Canvas canvasNext;
    Path animPath;
    Path mPath;
    private float mX,mY;
    private int setFlag = 0;

    PathMeasure pathMeasure;
    float pathLength;

    float step;			//distance each step
    float distance;		//distance moved
    float curX, curY;

    float currentAngle;		//current angle
    float targetAngle;	//target angle
    float stepAngle;	//angle each step

    float[] pos;
    float[] tan;

    Matrix matrix;

    Path touchPath;

    public CanvasView(Context context) {
        super(context);
        initMyView();
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.carx);
        bm_offsetX = bm.getWidth()/2;
        bm_offsetY = bm.getHeight()/2;

        animPath = new Path();
        mPath = new Path();


        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();

        touchPath = new Path();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw (final Canvas canvas) {

        if (animPath.isEmpty()) {
            return;
        }
        canvas.drawPath(mPath, mPaint);
        //for (int i =0 ; i<= 1000000 ; i++)
        moveCar(canvas);
    }
    protected void moveCar(Canvas canvas){
        canvas.drawPath(animPath, paint);
        matrix.reset();
        if((targetAngle- currentAngle)>stepAngle){
            currentAngle += stepAngle;
            matrix.postRotate(currentAngle, bm_offsetX, bm_offsetY);
            matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm, matrix, null);

            invalidate();
        }else if((currentAngle -targetAngle)>stepAngle){
            currentAngle -= stepAngle;
            matrix.postRotate(currentAngle, bm_offsetX, bm_offsetY);
            matrix.postTranslate(curX, curY);
            canvas.drawBitmap(bm, matrix, null);

            invalidate();
        }else{
            currentAngle =targetAngle;
            if(distance < pathLength){
                pathMeasure.getPosTan(distance, pos, tan);

                targetAngle = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
                //Log.d("tag","unghi" + targetAngle);
                matrix.postRotate(currentAngle, bm_offsetX, bm_offsetY);

                curX = pos[0]-bm_offsetX;
                curY = pos[1]-bm_offsetY;
                matrix.postTranslate(curX, curY);

                canvas.drawBitmap(bm, matrix, null);

                distance += step;

                invalidate();
            }else{
                matrix.postRotate(currentAngle, bm_offsetX, bm_offsetY);
                matrix.postTranslate(curX, curY);
                canvas.drawBitmap(bm, matrix, null);
            }
        }
    }
    public void calculateAngle(){
        setFlag=1;
    }


    public void clearCanvas(){
        touchPath.reset();
        animPath.reset();
        mPath.reset();
        invalidate();
    }

    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    private void moveTouch(float x, float y){
        float dx = Math.abs(x-mX);
        float dy = Math.abs(y-mY);

        if(dx >= TOLERANCE || dy >= TOLERANCE){
            mPath.quadTo(mX,mY,(x + mX) / 2,(y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch(action){
            case MotionEvent.ACTION_DOWN:
                touchPath.reset();
                startTouch(event.getX(),event.getY());
                touchPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(event.getX(),event.getY());
                touchPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                touchPath.lineTo(event.getX(), event.getY());
                animPath = new Path(touchPath);

                pathMeasure = new PathMeasure(animPath, false);
                pathLength = pathMeasure.getLength();

                step = 1;
                distance = 0;
                curX = 0;
                curY = 0;

                stepAngle = 1;
                currentAngle = 0;
                targetAngle = 0;

                invalidate();

                break;

        }

        return true;
    }

    }
