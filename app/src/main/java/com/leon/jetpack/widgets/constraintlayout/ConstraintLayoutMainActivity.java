package com.leon.jetpack.widgets.constraintlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Placeholder;

import android.os.Bundle;
import android.view.View;

import com.leon.jetpack.R;

public class ConstraintLayoutMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout_main);

    }


    private void StudyConstraintSet() {
        ConstraintSet set = new ConstraintSet();//创建一个对象
        set.clone(this, R.layout.activity_constraint_layout_main);//获取布局中的约束


        ConstraintLayout constraintLayout = findViewById(R.id.root);
        set.applyTo(constraintLayout);
    }
}