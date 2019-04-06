package com.example.skinchanger

import android.graphics.Color
import android.os.Bundle
import com.zzx.lib.SkinActivity
import com.zzx.lib.SkinChanger
import com.zzx.lib.ThemeAttrObject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : SkinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        println("colorPrimaryDark" + Color.parseColor("#00796B"))
//        println("colorPrimaryLight" + Color.parseColor("#B2DFDB"))
//        println("colorPrimary" + Color.parseColor("#009688"))
//        println("colorAccent" + Color.parseColor("#FF4081"))
//        println("textColorPrimary" + Color.parseColor("#212121"))
//        println("textColorSecondary" + Color.parseColor("#757575"))
//        println("dividerColor" + Color.parseColor("#BDBDBD"))
        changeColor.setOnClickListener {
//            SkinChanger.changeExternal(assets.open("skin_plugin.apk"), "skin_plugin.apk")

            SkinChanger.changeInternal(
                ThemeAttrObject(
                Color.parseColor("#009688"),
                Color.parseColor("#00796B"),
                Color.parseColor("#B2DFDB"),
                Color.parseColor("#FF4081"),
                Color.parseColor("#212121"),
                Color.parseColor("#757575"),
                Color.parseColor("#BDBDBD")
            )
            )
        }
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
