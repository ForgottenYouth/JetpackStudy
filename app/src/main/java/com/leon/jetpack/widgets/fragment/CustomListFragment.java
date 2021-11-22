/**
 * FileName: CustomListFragment
 * Author: shiwenliang
 * Date: 2021/11/12 14:30
 * Description:
 */
package com.leon.jetpack.widgets.fragment;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomListFragment extends ListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            data.add("leon----" + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        setListAdapter(adapter);
    }

}
