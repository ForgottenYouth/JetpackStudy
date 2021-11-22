package com.leon.jetpack.hilt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.leon.jetpack.R;
import com.leon.jetpack.hilt.module.IBook;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;

//@AndroidEntryPoint
public class HiltActivity extends AppCompatActivity {

//    @Inject
    IBook book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hilt);

        Log.e(TAG, "onCreate: "+book.hashCode());
    }
}