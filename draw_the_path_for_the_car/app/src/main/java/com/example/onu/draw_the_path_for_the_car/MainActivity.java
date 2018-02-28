package com.example.onu.draw_the_path_for_the_car;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;



public class MainActivity extends AppCompatActivity {
    private CanvasView canvasView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = findViewById(R.id.canvas);
        //image = findViewById(R.id.image_start);



    }
        /*View.OnClickListener MyOnClickListener =
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onMoveImage(v,centreX,centreY);
                        canvasView.invalidate();
                    }

                };*/


    public void clearCanvas(View v){
        canvasView.clearCanvas();
    }
    public void showAngle(View v) { canvasView.calculateAngle();}

   /* public void createAngle(View v){
        canvasView.createAngles();
    }
    public void createDot(View v,double centreX,double centreY){
        canvasView.setImageStart(v,centreX,centreY  );
    }
    public void onMoveImage(View v,double centreX,double centreY) {
        createDot(v, centreX, centreY);
        //setContentView(R.layout.activity_main);
    }*/
}
