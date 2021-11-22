package com.leon.jetpack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.leon.jetpack.camerax.CameraXMainActivity;
import com.leon.jetpack.widgets.LayoutContainerMainActivity;
import com.leon.jetpack.widgets.cardview.CardViewMainActivity;
import com.leon.jetpack.widgets.constraintlayout.ConstraintLayoutMainActivity;
import com.leon.jetpack.databinding.DataBindingMainActivity;
import com.leon.jetpack.hilt.activity.HiltActivity;
import com.leon.jetpack.lifecycle.LifecycleActivity;
import com.leon.jetpack.livedata.LiveDataMainActivity;
import com.leon.jetpack.navigator.NavigatorMainActivity;
import com.leon.jetpack.paging.PagingMainActivity;
import com.leon.jetpack.room.RoomMainActivity;
import com.leon.jetpack.viewmodel.BaseViewModel;
import com.leon.jetpack.viewmodel.ViewModelActivity;
import com.leon.jetpack.workmanager.WorkManagerMainActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseViewModel viewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        Log.e(TAG, "MainActivity hashcode =  " + viewModel.hashCode());
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.lifecycle:
                startActivity(new Intent(this, LifecycleActivity.class));
                break;
            case R.id.hilt:
                startActivity(new Intent(this, HiltActivity.class));
                break;
            case R.id.databinding:
                startActivity(new Intent(this, DataBindingMainActivity.class));
                break;
            case R.id.viewmodel:
                startActivity(new Intent(this, ViewModelActivity.class));
                break;
            case R.id.livedata:
                startActivity(new Intent(this, LiveDataMainActivity.class));
                break;
            case R.id.room:
                startActivity(new Intent(this, RoomMainActivity.class));
                break;
            case R.id.workmanager:
                startActivity(new Intent(this, WorkManagerMainActivity.class));
                break;
            case R.id.paging:
                startActivity(new Intent(this, PagingMainActivity.class));
                break;
            case R.id.navigator:
                startActivity(new Intent(this, NavigatorMainActivity.class));
                break;
            case R.id.cardview:
                startActivity(new Intent(this, CardViewMainActivity.class));
                break;
            case R.id.constraintlayout:
                startActivity(new Intent(this, ConstraintLayoutMainActivity.class));
                break;
            case R.id.layout_container:
                startActivity(new Intent(this, LayoutContainerMainActivity.class));
                break;
            case R.id.camera:
                startActivity(new Intent(this, CameraXMainActivity.class));
                break;
            default:
                break;
        }
    }
}