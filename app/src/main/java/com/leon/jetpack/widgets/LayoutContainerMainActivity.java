package com.leon.jetpack.widgets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.leon.jetpack.R;
import com.leon.jetpack.widgets.cardview.CardViewMainActivity;
import com.leon.jetpack.widgets.constraintlayout.ConstraintLayoutMainActivity;
import com.leon.jetpack.widgets.floatingactionbutton.FloatingActionButtonMainActivity;
import com.leon.jetpack.widgets.fragment.FragmentMainActivity;
import com.leon.jetpack.widgets.motionlayout.MotionLayoutMainActivity;

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
            case R.id.fragmentlayout:
                startActivity(new Intent(this, FragmentMainActivity.class));
                break;
            default:
                break;
        }
    }
}