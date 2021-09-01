/**
 * FileName: CustomLifecycleObserver
 * Author: shiwenliang
 * Date: 2021/8/11 9:45
 * Description: 生命周期的自定义观察者组件
 */
package com.leon.jetpack.lifecycle;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class CustomLifecycleObserver implements LifecycleObserver {

    private static final String TAG = CustomLifecycleObserver.class.getSimpleName();

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreateX() {
        Log.e(TAG, "CustomLifecycleObserver---onCreateX:");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResumeX() {
        Log.e(TAG, "CustomLifecycleObserver---onResumeX:");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStopX() {
        Log.e(TAG, "CustomLifecycleObserver---onStopX:");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestoryX() {
        Log.e(TAG, "CustomLifecycleObserver---onDestoryX:");
    }
}
