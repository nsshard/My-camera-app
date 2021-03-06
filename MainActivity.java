package com.example.cw2program;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;


import android.content.ContentValues;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;


import com.google.common.util.concurrent.ListenableFuture;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;



// Original Camera code by Faisal-FS, modified by me



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


PreviewView previewView;
    Button takepic;

    private ImageCapture imageCapture;

    private LocationManager locationManager;
    private LocationListener listener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_locate;
        TextView tv_latitude;
        TextView tv_longhitud;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        takepic = findViewById(R.id.capture);
        takepic.setOnClickListener(this);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());

        btn_locate = (Button)findViewById(R.id.btn_locate);
        tv_latitude = (TextView)findViewById(R.id.tv_latitude);
        tv_longhitud = (TextView)findViewById(R.id.tv_longhitud);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                GPSLocator gpsLocator = new GPSLocator(getApplicationContext());
                Location location = gpsLocator.GetLocation();
                if(location != null){
                    double latitude = location.getLatitude();
                    double longhitud = location.getLongitude();
                    tv_latitude.setText(String.valueOf(latitude));
                    tv_longhitud.setText(String.valueOf(longhitud));
                }
            }
        });
    }




    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(imageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }


    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.capture:
            capturePhoto(); 
            break;
        }
    }


    private void capturePhoto() {


        long timestamp =  System.currentTimeMillis();

              ContentValues contentValues = new ContentValues();
              contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
              contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");

        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(MainActivity.this, "Photo saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(MainActivity.this, "Error saving" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

}


