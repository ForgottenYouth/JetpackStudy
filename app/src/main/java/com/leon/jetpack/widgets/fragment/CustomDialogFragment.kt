/**
 * FileName: CustomDialogFragment
 * Author: shiwenliang
 * Date: 2021/11/12 11:33
 * Description:
 */
package com.leon.jetpack.widgets.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.leon.jetpack.R

class CustomDialogFragment:DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_dialogfragment_layout,container,false)
    }
}