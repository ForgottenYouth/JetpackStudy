package com.leon.jetpack.databinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.leon.jetpack.R;

public class DataBindingMainActivity extends AppCompatActivity {

    ActivityDataBindingMainBinding dataBindingMainBinding;

    DataBean dataBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBindingMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding_main);

        dataBean = new DataBean();
        dataBean.setResultDesc("hello DataBinding");
        dataBindingMainBinding.setDataSource(dataBean);
    }

    public void onClick(View view) {
        dataBean.setResultDesc("hello DataBinding changed");
    }
}