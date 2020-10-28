package com.example.ondownnotecapture;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public Button Capture;
    public  Button CaptureAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Capture=findViewById(R.id.btnCapture);
        Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent capture= new Intent(MainActivity.this,photoCapture.class);
                startActivity(capture);
            }
        });

        CaptureAPI=findViewById(R.id.btnCaptureAPI);
        CaptureAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent captureAPI = new Intent(MainActivity.this,photoCaptureAPI.class);
                startActivity(captureAPI);
            }
        });
    }
}
