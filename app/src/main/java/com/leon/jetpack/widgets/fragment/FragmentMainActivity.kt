package com.leon.jetpack.widgets.fragment

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.leon.jetpack.R

class FragmentMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_main)
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(R.id.topcontent, TopFragment())
        beginTransaction.replace(R.id.bottomcontent, CustomListFragment())
        beginTransaction.commit()
    }

    fun onClick(view: View) {
        var customDialogFragment = CustomDialogFragment()
        customDialogFragment.show(this.supportFragmentManager, "CustomDialogFragment")
        customDialogFragment.dismiss()
    }

}