package com.leon.jetpack.camerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.Identifier;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.leon.jetpack.R;
import com.leon.jetpack.utils.PermissionUtils;

import java.security.Permission;
import java.util.concurrent.ExecutionException;

public class CameraXMainActivity extends AppCompatActivity {

    PreviewView previewView;
    Preview preview;
    ListenableFuture<ProcessCameraProvider> providerListenableFuture;
    CameraSelector cameraSelector;
    ProcessCameraProvider processCameraProvider;

    /**
     * TODO 配置全屏
     */
    void configFullScreen() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_x_main);
        previewView = findViewById(R.id.previewView);

        PermissionUtils.with(this).requestPermissions(new String[]{Manifest.permission.CAMERA},
                new PermissionUtils.PermissionGrantedCallBack() {
                    @Override
                    public void onGranted() {
                        startCamera();
                    }

                    @Override
                    public void onGrantedError(String error) {

                    }
                });
    }

    private void startCamera() {
        providerListenableFuture = ProcessCameraProvider.getInstance(this);
        providerListenableFuture.addListener(() -> {
            try {
                processCameraProvider = providerListenableFuture.get();
                bindPreview();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview() {
        preview = new Preview.Builder().build();
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.PERMISSION_REQUEST) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (!isAllGranted) {
                finish();
            } else {
                startCamera();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void onClick(View view) {
        if (view.getId() == R.id.close) {
            this.finish();
        } else if (view.getId() == R.id.switch_camera) {
            processCameraProvider.unbind(preview);
            cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
            processCameraProvider.bindToLifecycle(this, cameraSelector, preview);
        }
    }
}