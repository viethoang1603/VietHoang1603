package com.example.ondownnotecapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ondownnotecapture.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class TrangChu extends AppCompatActivity implements GestureDetector.OnGestureListener {
    private float x1,x2,y1,y2;
    private static  int MIN_DISTANCE=150;
    private GestureDetector detector;
    private int mTextnodeID;
    public static int count = 0;
    public String file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);
        TextView tv=(TextView) findViewById(R.id.tvtest);
        Intent it=getIntent();
        String value=it.getStringExtra("userid");
        Toast.makeText(this,""+value+"",Toast.LENGTH_LONG).show();
        detector=new GestureDetector(TrangChu.this,this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1=event.getX();
                y1=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2=event.getX();
                y2=event.getY();
                float valueX=x2-x1;
                float valueY=y2-y1;

                if(Math.abs(valueX)>MIN_DISTANCE){
                    if(x2>x1){
                        Toast.makeText(this,"Right",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this,"left",Toast.LENGTH_LONG).show();

                    }
                }
                //Hoang onDown_Note photoCapture
                else if(Math.abs(valueY)>MIN_DISTANCE)
                {
                    if(y2>y1){

                        Toast.makeText(this,"bottom",Toast.LENGTH_LONG).show();
                        //  onDownPhotoCapture();
                        Intent onDown= new Intent(TrangChu.this,photoCaptureAPI.class);
                        startActivity(onDown);
                    }
                    else {
                        Toast.makeText(this,"top",Toast.LENGTH_LONG).show();
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {


        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
    private  void LunchTakePhotoIntent(){
        if(mTextnodeID==-1){
            //save curren Note to get ID of Note
            // saveNote();
        }
        Intent takePictureIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }



}