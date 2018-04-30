package com.rev.rtsimulation.rtsimulation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraCapture.CameraCallback {

    // Fields
    private static final String TAG = "MainActivity";
    private BaseLoaderCallback mOpenCVLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            switch (status){

                case BaseLoaderCallback.SUCCESS:
                    mOpenCVInited = true;
                    startCameraCapture();
                    break;

                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    // surface view -> image view
    private ImageView mMySurfaceView;

//    JavaCameraView javaCameraView;
//    private PortraitCameraView portraitCameraView;

    private CameraCapture mCameraCapture;
    Boolean mOpenCVInited = false;

//    private Mat rgba;
//    private Mat imgGray;
//    private Mat imgCanny;
//    private Mat hierarchy;

    List<MatOfPoint> contours;

    // static initialization of opencv library
    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mMySurfaceView = findViewById(R.id.mySurfaceView);

//        javaCameraView = findViewById(R.id.java_camera_view);
//        javaCameraView.setVisibility(SurfaceView.VISIBLE);
//        javaCameraView.setCvCameraViewListener(this);
//        javaCameraView.enableFpsMeter();

//        portraitCameraView = findViewById(R.id.portrait_camera_view);
//        portraitCameraView.setVisibility(SurfaceView.VISIBLE);
//        portraitCameraView.setCvCameraViewListener(this);
//        portraitCameraView.enableFpsMeter();

        // Requesting for camera permission manually
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Giving an explanation, if needed.
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
    }

    // to deal with when leaving the activity
    @Override
    protected void onPause() {
        super.onPause();

        // TODO: stop camera

//        if(javaCameraView != null){
//
//            javaCameraView.disableView();
//        }
//
//        if(portraitCameraView != null){
//
//            portraitCameraView.disableView();
//        }
    }

    // final call received before activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: stop camera with clean

//        if(javaCameraView != null){
//
//            javaCameraView.disableView();
//        }
//
//        if(portraitCameraView != null){
//
//            portraitCameraView.disableView();
//        }
    }

    // activity is in front of all other activities
    @Override
    protected void onResume() {
        super.onResume();

        // loads and initializes OpenCV library from within current application package
        if(OpenCVLoader.initDebug()){

            Log.d(TAG, "OpenCV Successfully Loaded!");
            mOpenCVLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {

            Log.d(TAG, "OpenCV Not Loaded!");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,
                    this, mOpenCVLoaderCallback);
        }
    }

    private void startCameraCapture(){

        // camera capture instance
        mCameraCapture = new CameraCapture();
        mCameraCapture.cameraCallback = this;

        // init basic config
        mCameraCapture.pictureFormat = ImageFormat.NV21;
        mCameraCapture.previewWidth = 480;
        mCameraCapture.previewHeight = 640;

        mCameraCapture.initCameraDevice();
        mCameraCapture.initCameraCapture();
    }

    @Override
    public void onUpdateFrame(byte[] data, int fps) {

        // rotating current surface view -> image view
        mMySurfaceView.setRotation(90);

        // or : mat = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED); // not sure it work or not

        // OpenCV primitive data types: CV_8UC1 -> 8 bits unsigned channel 1
        Mat originalFrame = new Mat(mCameraCapture.previewHeight + mCameraCapture.previewHeight/2,
                mCameraCapture.previewWidth, CvType.CV_8UC1);
        originalFrame.put(0,0, data);

        Mat rgbaFrame = new Mat(mCameraCapture.previewWidth, mCameraCapture.previewHeight, CvType.CV_8UC3);
        // convert to rgba
        Imgproc.cvtColor(originalFrame, rgbaFrame, Imgproc.COLOR_YUV2RGB_NV21);     // YUV NV21 format

        // drawing fps value
        Imgproc.putText(rgbaFrame, "FPS: " + fps, new Point(15,108), Core.FONT_HERSHEY_SIMPLEX,
                0.8, new Scalar(200, 70), 2);

        Mat grayFrame = new Mat(mCameraCapture.previewWidth, mCameraCapture.previewHeight, CvType.CV_8UC3);     // CV_8UC3 -> for gray conversion
        // convert to gray
        Imgproc.cvtColor(rgbaFrame, grayFrame, Imgproc.COLOR_RGB2GRAY);

        Mat cannyFrame = new Mat(mCameraCapture.previewWidth, mCameraCapture.previewHeight, CvType.CV_8UC3);
        // Canny edge detection
        Imgproc.Canny(grayFrame, cannyFrame, 50, 150);

        Mat hierarchy = new Mat(mCameraCapture.previewWidth, mCameraCapture.previewHeight, CvType.CV_8UC3);
        contours = new ArrayList<MatOfPoint>();

        // Finding contours
        Imgproc.findContours(cannyFrame, contours, hierarchy, Imgproc.RETR_TREE,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // matrix set to all zeros
        Mat drawing = Mat.zeros(cannyFrame.size(), CvType.CV_8UC3);
        for(int i=0; i<contours.size(); i++){

            // calculating random color codes
            Scalar color = new Scalar(Math.random()*255,
                    Math.random()*255, Math.random()*255);

            // Drawing contours
            Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy,
                    0, new Point() );
        }

        // display on view ( need run on main thread )
        updateSurfaceView(drawing);

        // release all
        originalFrame.release();
        rgbaFrame.release();
        grayFrame.release();
        cannyFrame.release();
        hierarchy.release();
        drawing.release();
    }

    // this method updates the surface view for each frame
    private void updateSurfaceView(Mat m){

        // convert mat -> bitmap
        final Bitmap outputBitmap = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(m, outputBitmap);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // sets bitmap to the surface view -> image view
                mMySurfaceView.setImageBitmap(outputBitmap);
            }
        });
    }

//    @Override
//    public void onCameraViewStarted(int width, int height) {
//
//        rgba = new Mat(height, width, CvType.CV_8UC4);
//        imgGray = new Mat(height, width, CvType.CV_8UC1);
//        imgCanny = new Mat(height, width, CvType.CV_8UC4);
//        hierarchy = new Mat();
//    }
//
//    @Override
//    public void onCameraViewStopped() {
//
//        rgba.release();
//        imgGray.release();
//        imgCanny.release();
//        hierarchy.release();
//    }
//
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//
//        rgba = inputFrame.rgba();
//        contours = new ArrayList<MatOfPoint>();
//
//        // Converting image from rgba to gray
//        Imgproc.cvtColor(rgba, imgGray, Imgproc.COLOR_RGB2GRAY);
//        // Canny edge detection
//        Imgproc.Canny(imgGray, imgCanny, 50, 150);
//
//        // Finding contours
//        Imgproc.findContours(imgCanny, contours, hierarchy, Imgproc.RETR_TREE,
//                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
//
//        Mat drawing = Mat.zeros(imgCanny.size(), CvType.CV_8UC4);
//
//        for(int i=0; i< contours.size(); i++){
//
//            Scalar color = new Scalar(Math.random()*255,
//                    Math.random()*255, Math.random()*255);
//
//            // Drawing contours
//            Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy,
//                    0, new Point() );
//        }
//
//        hierarchy.release();
////        Imgproc.drawContours(imgGray, contours, -1, new Scalar(Math.random()*255,
////                Math.random()*255, Math.random()*255)); //, 2, 8, hierarchy, 0, new Point());
//
//        return drawing;
//    }
}
