package com.tmmmt.tabanimation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.my_custom_layout.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeAppLanguage(Language.ARABIC)

        setContentView(R.layout.my_custom_layout)
        arrowTab.setSelection(1)
        arrowTab2.setSelection(0)
    }

}
