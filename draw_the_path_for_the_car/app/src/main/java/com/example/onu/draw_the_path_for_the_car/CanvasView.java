package com.example.onu.draw_the_path_for_the_car;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
    private ArrayList<Point> startPoints = new ArrayList<>();
    private ArrayList<Point> finalPoints = new ArrayList<>();
    private Point originPoint;
    private boolean flag = true;
    private boolean flagAutocomplete = true;


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
        canvas.drawPath(mPath, mPaint);
    }

    private void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        Point startPoint = new Point((int) mX, (int) mY);
        startPoints.add(startPoint);
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
        flag = true;
        invalidate();
        myPoints = new ArrayList<>();
        startPoints.clear();
        finalPoints.clear();

    }
    private void upTouch() {
        mPath.lineTo(mX, mY);
        Point finalPoint = new Point((int) mX, (int) mY);
        finalPoints.add(finalPoint);
    }

    public void setImageStart(final View view,double centreX,double centreY){
        Set<Point> hashSetStartPoints = new HashSet<>();
        Set<Point> hashSetFinalPoints = new HashSet<>();
        hashSetStartPoints.addAll(startPoints);
        hashSetFinalPoints.addAll(finalPoints);
        startPoints.clear();
        finalPoints.clear();
        startPoints.addAll(hashSetStartPoints);
        finalPoints.addAll(hashSetFinalPoints);


        if (startPoints.size() == 1 && finalPoints.size() == 1)
        {
            Log.d("tag","value : " + " nothing to complete");
        }
        else
        {
            Log.d("tag","value : " + startPoints.toString() + " size : " + startPoints.size() + "\n" +
            finalPoints.toString() + " size : " + finalPoints.size());

            if(startPoints.size() > finalPoints.size())
            {
                ArrayList<Integer> indexes = new ArrayList<>();
                for (Point stPoint :
                        startPoints) {
                    for (Point fnPoint:
                         finalPoints) {
                        if (fnPoint.x == stPoint.x && fnPoint.y == stPoint.y) {
                            indexes.add(startPoints.indexOf(stPoint));
                            continue;
                        }
                    }

                }
                for (int i=indexes.size()-1; i>=0; i--) {
                    startPoints.remove(startPoints.get(indexes.get(i)));
                }

                indexes.clear();
            }
            if(startPoints.size() < finalPoints.size())
            {
                ArrayList<Integer> indexes = new ArrayList<>();
                for (Point fnPoint :
                        finalPoints) {
                    for (Point stPoint:
                            startPoints) {
                        if (fnPoint.x == stPoint.x && fnPoint.y == stPoint.y) {
                            indexes.add(finalPoints.indexOf(fnPoint));
                        }
                    }
                }
                for (int i=indexes.size()-1; i>=0; i--) {
                    finalPoints.remove(finalPoints.get(indexes.get(i)));
                }
                indexes.clear();
            }

            if(startPoints.size() == finalPoints.size())
            {
                ArrayList<Integer> indexesFn = new ArrayList<>();
                ArrayList<Integer> indexesSt = new ArrayList<>();
                for (Point fnPoint :
                        finalPoints) {
                    for (Point stPoint:
                            startPoints) {
                        if (fnPoint.x == stPoint.x && fnPoint.y == stPoint.y) {
                            indexesSt.add(startPoints.indexOf(stPoint));
                            indexesFn.add(finalPoints.indexOf(fnPoint));
                        }
                    }
                }
 //               Log.d("sepparator" , "------------------------");
   //             Log.d("tag","value : " + indexesSt.toString() + " size : " + startPoints.size() + "\n" +
     //                   indexesFn.toString() + " size : " + finalPoints.size());

                for (int i=indexesSt.size()-1; i>=0; i--) {
                    startPoints.remove(startPoints.get(indexesSt.get(i)));
                }

                for (int i=indexesFn.size()-1; i>=0; i--) {
                    finalPoints.remove(finalPoints.get(indexesFn.get(i)));
                }

                indexesFn.clear();
                indexesSt.clear();
            }

            autoCompleteThePath(startPoints,finalPoints);
            invalidate();

           Log.d("tag","value : " + startPoints.toString() +" SIZE: " + startPoints.size() + "\n" + finalPoints.toString() + " SIZE: " + finalPoints.size());
           Log.d("tag","value " + "i have to create " + ((startPoints.size()-1+finalPoints.size()-1)/2));
        }

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
        Log.d("tag","Origin Point : " + originPoint);
        for (int i =0;i< myPoints.size()-1;i++) {
            Log.d("tag", "Unghi : " + angle(originPoint, myPoints.get(i)));
        }
    }

    public void autoCompleteThePath(ArrayList<Point> startPoints,ArrayList<Point> finalPoints){

        for (int indexSt = 1; indexSt <= startPoints.size() - 1; indexSt++) {
            mPaint.setColor(Color.RED);
            mPath.quadTo(finalPoints.get(indexSt - 1).x,finalPoints.get(indexSt - 1).y,startPoints.get(indexSt).x,startPoints.get(indexSt).y);//startPoints.get(indexSt).x,startPoints.get(indexSt).y,finalPoints.get(indexSt).x,finalPoints.get(indexSt).y, mPaint);
            mPaint.setColor(Color.BLACK);
            invalidate();
            //Path newPath = new Path();
            //mPath.reset();
           // mPath.moveTo(finalPoints.get(indexSt - 1).x,finalPoints.get(indexSt - 1).y);
           // mPath.lineTo(startPoints.get(indexSt).x,startPoints.get(indexSt).y);
            mCanvas.drawPath(mPath,mPaint);
        }
        mPaint.setColor(Color.BLACK);
    }

}
