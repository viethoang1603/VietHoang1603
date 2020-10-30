package com.example.ondownnotecapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Picture;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class photoCaptureAPI extends AppCompatActivity implements SurfaceHolder.Callback {
    public static Camera camera;
    SurfaceView surfaceView; //
    SurfaceHolder surfaceHolder;// chia sẻ quyền sở hữu bề mặt, tương tác với khung nhìn

    android.hardware.Camera.PictureCallback jpegCallback;

    LocationManager myLM;
    TextView lblPosition;

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1000;
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1;

    private LocationManager locationManager;

    private TextView longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture_a_p_i);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();// truy xuất bằng cách gọigetholder

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Trả lại SurfaceHolder cung cấp quyền truy cập và kiểm soát bề mặt bên dưới của SurfaceView này.


        jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(String.format("/sdcard/%d.jpg", System.currentTimeMillis()));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();// theo dõi ngoại lệ,xác định phương thức gây lỗi ..... ngăn xếp
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                Log.d("Log", "Saved");
                Toast.makeText(getApplicationContext(), "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();
            }
        };
        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkPermission();


    }
// take PIC and get Location
    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, jpegCallback);

        // getLocation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null)
        {
            longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
            latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
        }
    }

/*
* Điều này được gọi ngay sau khi bề mặt được tạo lần đầu tiên.
* Việc triển khai điều này sẽ bắt đầu bất kỳ mã kết xuất nào mà họ muốn.
*  Lưu ý rằng chỉ một luồng có thể vẽ vào Surface,
* vì vậy bạn không nên vẽ vào Surface ở đây nếu kết xuất bình thường của bạn sẽ nằm trong một luồng khác.*/
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
        Camera.Parameters param;// để set các thông số cho cam
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

    /*SurfChange (Sf holder,format, with, hight)
    This is called immediately after any structural changes (format or size) have been made to the surface.
    You should at this point update the imagery in the surface.
    This method is always called at least once, after surfaceCreated(SurfaceHolder).
    Điều này được gọi ngay lập tức sau khi bất kỳ thay đổi cấu trúc (định dạng hoặc kích thước) được thực hiện trên bề mặt.
    Tại thời điểm này, bạn nên cập nhật hình ảnh trên bề mặt.
     Phương pháp này luôn được gọi ít nhất một lần, sau khi đã xử lý bề mặt (SurfaceHolder).
    * */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    /*
    * Điều này được gọi ngay lập tức trước khi một bề mặt bị phá hủy. Sau khi trở về từ cuộc gọi này,
    * bạn không nên cố gắng truy cập bề mặt này nữa. Nếu bạn có một luồng kết xuất truy cập trực tiếp vào bề mặt,
    * bạn phải đảm bảo rằng luồng đó không còn chạm vào Bề mặt trước khi quay lại từ chức năng này.*/
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

//Location
    void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener());
    }

//check permission Location
    void checkPermission() {

        String[] perm_array = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            List<String> permissions = new ArrayList<String>();

            for (int i=0;i<perm_array.length;i++)
            {
                if (checkSelfPermission(perm_array[i])!= PackageManager.PERMISSION_GRANTED)
                {
                    permissions.add(perm_array[i]);
                }
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 9999);
            } else {
                requestLocation();
            }
        } else {
            requestLocation();
        }
    }

//    public void onClick(View view)
//    {
//        @SuppressLint("MissingPermission")
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//        if(location != null)
//        {
//            longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
//            latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
//        }
//    }
//implement location cach 2
    private class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(@NonNull Location location) {

            longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
            latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }
}