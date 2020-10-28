package com.example.ondownnotecapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Picture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class photoCaptureAPI extends AppCompatActivity implements SurfaceHolder.Callback {
    public  static Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    android.hardware.Camera.PictureCallback jpegCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture_a_p_i);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                Log.d("Log", "Saved");
                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();
            }
        };
    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            // open the camera
            Log.d("Log", "Open Camera");
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        List<Camera.Size> sizes = param.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        param.setPreviewSize(selected.width, selected.height);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            Log.d("Log", "Cam draw");
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void refreshCamera() {
        Log.d("Log", "refreshCamera ");
        Toast.makeText(getApplicationContext(), "Refresh", Toast.LENGTH_LONG).show();
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            Log.d("Log", "preview surface ");
          //  Toast.makeText(getApplicationContext(), "preview surface", Toast.LENGTH_LONG).show();
            return;
        }
        // stop preview before making changes
        try {
            Log.d("Log", "Stop Preview ");
           // Toast.makeText(getApplicationContext(), "Stop Preview", Toast.LENGTH_LONG).show();
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            Log.d("Log", "Set Preview ");
            //Toast.makeText(getApplicationContext(), "Set Preview", Toast.LENGTH_LONG).show();
            camera.setPreviewDisplay(surfaceHolder);
            Log.d("Log", "Start");
            camera.startPreview();
        } catch (Exception e) {

        }
    }
}