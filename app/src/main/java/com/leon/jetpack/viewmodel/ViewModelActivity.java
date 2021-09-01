package com.leon.jetpack.viewmodel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.leon.jetpack.R;

public class ViewModelActivity extends AppCompatActivity {

    private static final String TAG = ViewModelActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_model);
        BaseViewModel viewModel = new ViewModelProvider(this).get(BaseViewModel.class);

        Log.e(TAG, "ViewModelActivity hashcode =  " + viewModel.hashCode());
    }
}