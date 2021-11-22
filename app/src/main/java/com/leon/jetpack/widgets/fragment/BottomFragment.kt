/**
 * FileName: TopFragment
 * Author: shiwenliang
 * Date: 2021/10/19 11:04
 * Description:
 */
package com.leon.jetpack.widgets.fragment

import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.leon.jetpack.R

class BottomFragment : Fragment() {
    val TAG: String = BottomFragment::class.java.simpleName
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "onAttach()")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "onCreateView()")
        return return inflater.inflate(R.layout.bottom_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e(TAG, "onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Log.e(TAG, "onStart()")
        super.onStart()
    }

    override fun onResume() {
        Log.e(TAG, "onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.e(TAG, "onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.e(TAG, "onStop()")
        super.onStop()
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy()")
        super.onDestroy()
    }

    override fun onDestroyView() {
        Log.e(TAG, "onDestroyView()")
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.e(TAG, "onDetach()")
        super.onDetach()
    }
}