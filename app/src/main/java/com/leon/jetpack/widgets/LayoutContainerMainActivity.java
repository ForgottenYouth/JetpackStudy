package com.leon.jetpack.widgets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.leon.jetpack.R;
import com.leon.jetpack.databinding.DataBindingMainActivity;
import com.leon.jetpack.hilt.activity.HiltActivity;
import com.leon.jetpack.lifecycle.LifecycleActivity;
import com.leon.jetpack.livedata.LiveDataMainActivity;
import com.leon.jetpack.navigator.NavigatorMainActivity;
import com.leon.jetpack.paging.PagingMainActivity;
import com.leon.jetpack.room.RoomMainActivity;
import com.leon.jetpack.viewmodel.ViewModelActivity;
import com.leon.jetpack.widgets.cardview.CardViewMainActivity;
import com.leon.jetpack.widgets.constraintlayout.ConstraintLayoutMainActivity;
import com.leon.jetpack.widgets.floatingactionbutton.FloatingActionButtonMainActivity;
import com.leon.jetpack.widgets.motionlayout.MotionLayoutMainActivity;
import com.leon.jetpack.workmanager.WorkManagerMainActivity;

public class LayoutContainerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_container_main);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.cardview:
                startActivity(new Intent(this, CardViewMainActivity.class));
                break;
            case R.id.constraintlayout:
                startActivity(new Intent(this, ConstraintLayoutMainActivity.class));
                break;
            case R.id.layout_container:
                startActivity(new Intent(this, LayoutContainerMainActivity.class));
                break;
            case R.id.floatingactionbutton:
                startActivity(new Intent(this, FloatingActionButtonMainActivity.class));
                break;
            case R.id.motionlayout:
                startActivity(new Intent(this, MotionLayoutMainActivity.class));
                break;
            default:
                break;
        }
    }
}