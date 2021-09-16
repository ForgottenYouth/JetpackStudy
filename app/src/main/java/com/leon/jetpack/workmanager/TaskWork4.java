/**
 * FileName: TaskWork
 * Author: shiwenliang
 * Date: 2021/9/2 11:29
 * Description:
 */
package com.leon.jetpack.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TaskWork4 extends Worker {
    private static final String TAG = TaskWork4.class.getSimpleName();

    public TaskWork4(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(3000);
            Log.e(TAG, "doWork: " + TAG);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        } finally {
            Log.e(TAG, "doWork: end");
        }
        return Result.success();
    }
}
