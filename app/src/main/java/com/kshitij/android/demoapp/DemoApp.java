package com.kshitij.android.demoapp;

import android.app.Application;

/**
 * Created by kshitij.kumar on 01-06-2015.
 */

/**
 * Application class.
 */
public class DemoApp extends Application {

    private static DemoApp mInstance;


    public DemoApp() {

    }

    public static DemoApp getAppInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {

        mInstance = this;
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}

