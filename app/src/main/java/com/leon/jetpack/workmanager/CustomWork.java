/**
 * FileName: CustomWork
 * Author: shiwenliang
 * Date: 2021/9/2 10:35
 * Description:
 */
package com.leon.jetpack.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * TODO 自定义任务work
 * 1.必须继承Worker,需要重写doWork()方法
 * 2.通过WorkerParameters 来进行数据传递
 */
public class CustomWork extends Worker {

    public static final String TAG = CustomWork.class.getSimpleName();


    public CustomWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "doWork: start");

        //获取传递的数据
        String leon = getInputData().getString("leon");
        Log.e(TAG, "传递进来的数据: " + leon);

        //下面是简单的异步任务
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Result.failure();
        } finally {
            Log.e(TAG, "doWork: end");
        }

        //返回Activity数据
        Data data = new Data.Builder().putString("leon","task is completed").build();
        return Result.success(data);
    }
}
