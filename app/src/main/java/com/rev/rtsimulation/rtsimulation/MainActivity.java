package com.rev.rtsimulation.rtsimulation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Field
    private static final String TAG = "MainActivity";

    JavaCameraView javaCameraView;
    BaseLoaderCallback loaderCallback;
//    BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//
//            switch (status){
//
//                case BaseLoaderCallback.SUCCESS:
//
//                    javaCameraView.enableView();
//                    break;
//
//                default:
//                    super.onManagerConnected(status);
//                    break;
//            }
//        }
//    };

    Mat rgba;
    Mat imgGray;
    Mat imgCanny;

//    // opencv testing
    static {

        System.loadLibrary("opencv_java3");
//        if(OpenCVLoader.initDebug()){
//
//            Log.d(TAG, "OpenCV Successfully Loaded!");
//        } else {
//
//            Log.d(TAG, "OpenCV Not Loaded!");
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        javaCameraView = findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableView();
        javaCameraView.enableFpsMeter();

        // Requesting for camera permission manually
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Give first an explanation, if needed.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // Requesting the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                }
            }
        }

        loaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {

                switch (status){

                    case BaseLoaderCallback.SUCCESS:{
                        javaCameraView.enableView();
                    }
                        break;

                    default: {
                        super.onManagerConnected(status);
                    }
                        break;
                }
            }
        };
    }

    @Override
    protected void onPause() {

        super.onPause();

        if(javaCameraView != null){

            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if(javaCameraView != null){

            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
//        loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        if(OpenCVLoader.initDebug()){

            Log.d(TAG, "OpenCV Successfully Loaded!");
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {

            Log.d(TAG, "OpenCV Not Loaded!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,
                    this, loaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        rgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC4);
        imgCanny = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {

        rgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        rgba = inputFrame.rgba();

        // Converting to gray image from rgba
        Imgproc.cvtColor(rgba, imgGray, Imgproc.COLOR_RGB2GRAY);
        // Detecting Canny edge
//        Imgproc.Canny(imgGray, imgCanny, 50, 150);

        return imgGray;
    }
}
