package com.leon.jetpack.workmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.util.Log;

import com.leon.jetpack.R;

import java.util.concurrent.TimeUnit;

public class WorkManagerMainActivity extends AppCompatActivity {
    public static final String TAG = WorkManagerMainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager_main);

        constraintTaskSimple();
    }

    /**
     * TODO 任务的约束条件
     */
    void constraintTaskSimple() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)//联网中
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)//充电中
                .setRequiresDeviceIdle(true)//空闲时
                .build();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TaskWork.class)
                .setConstraints(constraints).build();

        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }

    //简单实用
    void simpleUsed() {
        OneTimeWorkRequest oneTimeWorkRequest = OneTimeWorkRequest.from(CustomWork.class);
        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }

    //数据传递
    void simpleUsed2() {

        Data data = new Data.Builder().putString("leon", "I am from WorkManagerMainActivity").build();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(CustomWork.class)
                .setInputData(data).build();

        //获取任务返回的数据
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(this, workInfo ->
                {
                    if (workInfo.getState().isFinished()) {
                        String backleon = workInfo.getOutputData().getString("leon");
                        Log.e(TAG, "任务返回的数据：" + backleon);
                    }
                }
        );

        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    }

    //多任务
    void mutilTaskSimple() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TaskWork.class).build();
        OneTimeWorkRequest oneTimeWorkRequest2 = new OneTimeWorkRequest.Builder(TaskWork2.class).build();
        OneTimeWorkRequest oneTimeWorkRequest3 = new OneTimeWorkRequest.Builder(TaskWork3.class).build();
        OneTimeWorkRequest oneTimeWorkRequest4 = new OneTimeWorkRequest.Builder(TaskWork4.class).build();

        WorkManager.getInstance(this)
                .beginWith(oneTimeWorkRequest)
                .then(oneTimeWorkRequest2)
                .then(oneTimeWorkRequest3)
                .then(oneTimeWorkRequest4)
                .enqueue();
    }


    /**
     * 重复任务
     * 重复任务不能小于15分钟，如果小于15分钟重复一次，就会使用默认的15分钟
     */

    void repeatTaskSimple() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TaskWork.class, 15, TimeUnit.MINUTES).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodicWorkRequest.getId()).observe(this, workInfo -> {
            Log.e(TAG, "任务的执行状态：" + workInfo.getState().name());
        });

        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    }
}