/**
 * FileName: FragmentFirst
 * Author: shiwenliang
 * Date: 2021/9/16 9:42
 * Description:
 */
package com.leon.jetpack.navigator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.leon.jetpack.R;

public class FragmentThird extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigator_third_layout, container, false);

        Button btn = view.findViewById(R.id.gotofirst);
        btn.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.gotofirst);
        });

         view.findViewById(R.id.back).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.backtoSecond);
        });

        return view;
    }
}
