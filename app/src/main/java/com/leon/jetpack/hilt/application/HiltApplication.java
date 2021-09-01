/**
 * FileName: HiltApplication
 * Author: shiwenliang
 * Date: 2021/8/11 16:45
 * Description:
 */
package com.leon.jetpack.hilt.application;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class HiltApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
