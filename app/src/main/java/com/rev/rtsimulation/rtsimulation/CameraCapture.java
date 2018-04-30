package com.rev.rtsimulation.rtsimulation;

/**
 * Created by Revee on 4/30/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.graphics.SurfaceTexture;
import android.media.ExifInterface;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.sql.Time;

/**
 * Created by Revee on 4/27/2018.
 */

public class CameraCapture {

    private final static int MAX_FPS = 60;
    private final static int TEXT_ID = 49;
    private final static int InitCamera = 0;
    private final static int StartCamera = 1;
    private final static int StopCamera = 2;

    // --------------------------------------------------
    // camera variable
    // --------------------------------------------------
    public SurfaceTexture mSurfaceTexture;
    private Camera mCamera;
    public int pictureFormat = ImageFormat.NV21;    // image format
    public int previewWidth = 480;
    public int previewHeight = 640;
    public CameraCallback cameraCallback = null;

    public Boolean mCameraInitialized = false;
    public Boolean mCameraUsing = false;

    private int mLastFPS = 0;
    private int mFrameCounter = 0;
    private long mLastNanoTime = 0;

    // this is our looper handler to capture camera frame
    private Looper mServiceLooper;
    public CameraServiceHandler mServiceHandler;

    public CameraCapture() {

    }

    // this method initializes camera device (without parameter)
    public void initCameraDevice(){
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("camera_service_thread", Process.THREAD_PRIORITY_LOWEST);
        thread.start();

        // Get the HandlerThread's Looper and use it for new Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new CameraServiceHandler(mServiceLooper, this);
    }

    // this method initializes camera device (with parameter)
    public void initCameraDevice(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        initCameraDevice();
    }

    // Check if this device has a camera
    // need use this to check first if you want to app run on other device
    public boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    // we init camera when receive command from other thread
    private Boolean initCamera() {

        if(mCameraInitialized) return false;

        int camID = 0;      // normal back camera = 0
        try {
            mCamera = Camera.open(camID);       // attempt to get a Camera instance

            // doesn't work
//            setCameraDisplayOrientation(activity, camID, mCamera);

        }
        catch (Exception e){
            e.printStackTrace();
            // camera is not available (in use or does not exist)
            //TODO: callback here

            mCameraInitialized = false;
            return false;
        }

        // setting up all parameters for camera
        Parameters params = mCamera.getParameters();
        params.setPreviewFormat(pictureFormat);
        params.setPreviewSize(previewWidth, previewHeight);
        mCamera.setParameters(params);

        if (mSurfaceTexture == null){
            mSurfaceTexture = new SurfaceTexture(TEXT_ID);
        }

        // doesn't work either
//        mCamera.setDisplayOrientation(90);

        mCameraInitialized = true;
        return true;
    }

//    private static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
//
//        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
//        android.hardware.Camera.getCameraInfo(cameraId, info);
//
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//
//        int result;
//
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//
//            result = (info.orientation + degrees) % 360;
//            result = (360 - result) % 360;  // compensate the mirror
//        } else {                            // back-facing
//
//            result = (info.orientation - degrees + 360) % 360;
//        }
//
//        camera.setDisplayOrientation(result);
//    }

//    private void setCameraOrientation(final String path) {
//
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapSrc.bm1, 480,
//                640, true);
//        Bitmap rotatedBitmap = null;
//
//        try {
//            ExifInterface ei = new ExifInterface(path);
//
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            Matrix matrix = new Matrix();
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotatedBitmap = rotateImage(scaledBitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotatedBitmap = rotateImage(scaledBitmap, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotatedBitmap = rotateImage(scaledBitmap, 270);
//                    break;
//                case ExifInterface.ORIENTATION_NORMAL:
//
//                default:
//                    rotatedBitmap = scaledBitmap;
//                    break;
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//
//        bitmapSrc.bm1 = null;
//    }
//
//    public Bitmap rotateImage(Bitmap source, float angle) {
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }

    // function to find camera front id
    private int findFrontFacingCameraId() {

        int camera_id = -1;

        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i=0; i<numberOfCameras; i++) {

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camera_id = i;
                break;
            }
        }
        return camera_id;
    }

    // starts the camera
    private void startCamera() {
        if(!mCameraInitialized) return;
        if(mCameraUsing) return;

        try {

            // mek sure if it is threadsafe
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
            mCameraUsing = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // stops the camera
    private void stopCamera(Boolean withClean) {
        if(!mCameraInitialized) return;
        if(!mCameraUsing) return;

        mCamera.stopPreview();
        mCameraUsing = false;

        if(withClean) {

            mCameraInitialized = false;
            mCamera.release();
            mCamera = null;

            try {

                mServiceLooper.getThread().join();
                mServiceLooper = null;
                mServiceHandler = null;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // made up callback for per preview frame
    private PreviewCallback mPreviewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            // update current fps
            final int fps = updateFPS();

            if(cameraCallback != null){
                cameraCallback.onUpdateFrame(data, fps);
            }
        }
    };

    // calculates and update frame rate
    private int updateFPS() {
        if(mLastNanoTime == 0) mLastNanoTime = System.nanoTime();

        mFrameCounter++;

        if (mFrameCounter >= MAX_FPS) {

            mLastFPS = (int)(mFrameCounter * 1e9 / (System.nanoTime() - mLastNanoTime));
            mFrameCounter = 0;
            mLastNanoTime = System.nanoTime();

            Log.d("Camera FPS", "FPS:" + mLastFPS);
        }

        return mLastFPS;
    }

    // sends service handler message
    void initCameraCapture(){
        Message msg = mServiceHandler.obtainMessage();
        msg.what = CameraCapture.InitCamera;
        mServiceHandler.sendMessage(msg);
    }

    void startCameraCapture(){
        Message msg = mServiceHandler.obtainMessage();
        msg.what = CameraCapture.StartCamera;
        mServiceHandler.sendMessage(msg);
    }

    void stopCameraCapture(Boolean withClean) {
        Message msg = mServiceHandler.obtainMessage();
        msg.what = CameraCapture.StopCamera;
        msg.arg1 = withClean ? 1 : 0;
        mServiceHandler.sendMessage(msg);
    }

    // inner service handler class
    private final class CameraServiceHandler extends Handler {
        // use weak reference so when handler is release that reference will be cast to nil.
        // if you not. camera capture can't free because that reference till count.
        // that will case memory leak
        WeakReference<CameraCapture> mReference;

        public CameraServiceHandler(Looper looper, CameraCapture referent) {
            super(looper);
            mReference = new WeakReference<CameraCapture>(referent);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraCapture cc = mReference.get();

            switch (msg.what) {
                case CameraCapture.InitCamera:
                    if(cc.initCamera()){
                        cc.startCamera();
                    }
                    break;
                case CameraCapture.StartCamera:

                    break;
                case CameraCapture.StopCamera:
                    int withClean = msg.arg1;
                    cc.stopCamera(withClean == 0 ? false : true);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    }

    // callback interface
    public interface CameraCallback {

        void onUpdateFrame(byte[] data, int fps);
    }
}