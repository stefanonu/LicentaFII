package com.example.onu.draw_the_path_for_the_car;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static com.example.onu.draw_the_path_for_the_car.R.id.activity_main;

public class MainActivity extends AppCompatActivity {
    private CanvasView canvasView;
    private double centreX;
    private double centreY;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = findViewById(R.id.canvas);
        image = findViewById(R.id.image_start);
        centreX=image.getX() + image.getWidth()  / 2;
        centreY=image.getY() + image.getHeight() / 2;

        // image.setX((float) centreX);
       // image.setY((float) centreY);

        Log.d("tag","value" + centreX);
        Log.d("tag","value" + centreY);
        image.setOnClickListener(MyOnClickListener);


    }
        View.OnClickListener MyOnClickListener =
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onMoveImage(v,centreX,centreY);
                    }

                };


    public void clearCanvas(View v){
        canvasView.clearCanvas();
    }
    public void createAngle(View v){
        canvasView.createAngles();
    }
    public void createDot(View v,double centreX,double centreY){
        canvasView.setImageStart(v,centreX,centreY  );
    }
    public void onMoveImage(View v,double centreX,double centreY){
        createDot(v,centreX,centreY);
    }
}
