package com.example.skinchanger

import android.os.Bundle
import com.zzx.lib.SkinActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SkinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        changeColor.setOnClickListener {
            com.zzx.lib.SkinChanger.change(assets.open("skin_plugin.apk"), "skin_plugin.apk")
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
