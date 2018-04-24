package com.rev.rtsimulation.rtsimulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    // opencv testing
    private static final String TAG = "MainActivity";

    static {

        if(OpenCVLoader.initDebug()){

            Log.d(TAG, "OpenCV Successfully Loaded!");
        } else {

            Log.d(TAG, "OpenCV Not Loaded!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
