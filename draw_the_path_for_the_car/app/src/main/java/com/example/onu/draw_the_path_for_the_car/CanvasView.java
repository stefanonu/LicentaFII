package com.example.onu.draw_the_path_for_the_car;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

/**
 * Created by Onu on 2/6/2018.
 */

public class CanvasView extends View  {
    public  int width;
    public int heigh;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private float mX,mY;
    private static final float TOLERANCE = 5;
    Context context;
    private int counter=0;
    private ArrayList<Point> myPoints = new ArrayList<>();
    private Point originPoint;
    private boolean flag = true;


    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath,mPaint);
    }

    private void startTouch(float x, float y){
        mPath.moveTo(x,y);
        mX = x;
        mY = y;
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
    public void clearCanvas(){
        mPath.reset();
        counter=0;
        invalidate();
        myPoints = new ArrayList<>();

    }
    private void upTouch() {
        mPath.lineTo(mX, mY);
    }

    public void setImageStart(final View view,double centreX,double centreY){

        final float xMij = (float) centreX;
        final float yMij = (float) centreY;

        ObjectAnimator objectAnimator =
                ObjectAnimator.ofFloat(view, view.X,
                        View.Y, mPath);
        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Point X = new Point(Math.round(x),Math.round(y));
        if(flag) {
            originPoint = X;
            flag = false;
        }
        else {
            myPoints.add(X);
        }
        //Log.d("coordonatesTag","Values " + Float.toString(x) + " , " + Float.toString(y));
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startTouch(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    public double angle(Point originPoint,Point targetPoint) {
        double theta = Math.atan2(targetPoint.y - originPoint.y,targetPoint.x-originPoint.x);
        theta += Math.PI/2.0;
        double angle = Math.toDegrees(theta);
        return angle;
    }
    public void createAngles()
    {
        for (int i =0;i< myPoints.size()-1;i++) {
            Log.d("tag", "Ungle : " + angle(originPoint, myPoints.get(i)));
        }
    }

}
