package com.alexey_klimchuk.gdgapp;

import android.app.Application;

import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

/**
 * Created by Alexey on 11.09.2016.
 */

public class App extends Application {

    public void onCreate() {
        super.onCreate();
        CacheUtils.initCache();
    }
}
