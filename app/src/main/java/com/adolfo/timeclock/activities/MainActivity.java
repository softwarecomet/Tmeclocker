package com.adolfo.timeclock.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adolfo.timeclock.R;
import com.adolfo.timeclock.cam.CameraSurfaceView;
import com.adolfo.timeclock.cam.FaceDetectionCamera;
import com.adolfo.timeclock.cam.FrontCameraRetriever;
import com.adolfo.timeclock.utils.LoadingDialog;
import com.adolfo.timeclock.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FrontCameraRetriever.Listener, FaceDetectionCamera.Listener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private RelativeLayout rel_menu_view;
    private FrameLayout cameraPreview;
    private SurfaceView cameraSurface;
    private ImageView img_camera, img_confirm;
    private TextView tv_identify, tv_date, tv_time;
    private LoadingDialog loadingDialog;
    private static CountDownTimer countDownTimer;
    private boolean isScanning = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {
            setupCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initView() {
        rel_menu_view = findViewById(R.id.rel_menu_view);
        cameraPreview = findViewById(R.id.cameraPreview);
        img_camera = findViewById(R.id.img_camera);
        img_confirm = findViewById(R.id.img_confirm);
        tv_identify = findViewById(R.id.tv_identify);
        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);

        ImageView img_menu = findViewById(R.id.img_menu);
        TextView tv_configuration = findViewById(R.id.tv_configuration);
        TextView tv_signout = findViewById(R.id.tv_signout);

        rel_menu_view.setOnClickListener(this);
        tv_configuration.setOnClickListener(this);
        tv_signout.setOnClickListener(this);
        img_menu.setOnClickListener(this);
    }

    private void setupCamera() {
        isScanning = true;
        cameraPreview.setVisibility(View.VISIBLE);
        img_camera.setVisibility(View.GONE);
        tv_identify.setVisibility(View.VISIBLE);
        img_confirm.setVisibility(View.GONE);

        FrontCameraRetriever.retrieveFor(this);
    }

    private void takePhoto() {
        SurfaceView view = cameraSurface;
        final Bitmap bitmap = Bitmap.createBitmap(cameraSurface.getWidth(), cameraSurface.getHeight(),
                Bitmap.Config.ARGB_8888);
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();

        PixelCopy.request(view, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
            @Override
            public void onPixelCopyFinished(int copyResult) {
                if (copyResult == PixelCopy.SUCCESS) {
                    Log.e(TAG, bitmap.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isScanning) {
                                img_camera.setImageBitmap(bitmap);

                                isScanning = false;
                                cameraPreview.setVisibility(View.GONE);
                                img_camera.setVisibility(View.VISIBLE);
                                tv_identify.setVisibility(View.GONE);
                                img_confirm.setVisibility(View.VISIBLE);

                                loadingDialog = new LoadingDialog(MainActivity.this, false);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDialog.hide();
                                        setupCamera();
                                    }
                                }, 5000);
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "Failed to take the photo from camera");
                }
                handlerThread.quitSafely();
            }
        }, new Handler(handlerThread.getLooper()));
    }


    private void showDateTime() {
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                countDownTimer.cancel();
                Date currentTime = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
                @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
                tv_date.setText(dateFormat.format(currentTime));
                tv_time.setText(timeFormat.format(currentTime));
                showDateTime();
            }
        }.start();
    }


    @Override
    public void onLoaded(FaceDetectionCamera camera) {
        cameraSurface = new CameraSurfaceView(this, camera, this);
        cameraPreview.addView(cameraSurface);
    }

    @Override
    public void onFailedToLoadFaceDetectionCamera() {
        Log.d(TAG, "onFailedToLoadFaceDetectionCamera");
    }

    @Override
    public void onFaceDetected() {
        takePhoto();
    }

    @Override
    public void onFaceTimedOut() {
        Log.d(TAG, "onFaceTimedOut");
    }

    @Override
    public void onFaceDetectionNonRecoverableError() {
        Log.d(TAG, "onFaceDetectionNonRecoverableError");
    }

    @Override
    protected void onResume() {
        super.onResume();
        rel_menu_view.setVisibility(View.GONE);
        showDateTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_configuration :
                Log.d(TAG, "Configuration");
                startActivity(new Intent(this, ConfigurationActivity.class));
                break;
            case R.id.tv_signout:
                Log.d(TAG, "Log out");
                Preferences.setValue(this, Preferences.isLogin, false);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.img_menu:
                Log.d(TAG, "Show the menu");
                rel_menu_view.setVisibility(View.VISIBLE);
                break;
            case R.id.rel_menu_view:
                Log.d(TAG, "Hide the menu");
                rel_menu_view.setVisibility(View.GONE);
                break;
        }
    }
}
