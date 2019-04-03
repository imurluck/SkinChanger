package com.zzx.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat

open class SkinActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, CompatFactory2(delegate))
        SkinChanger
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinChanger.release(this)
    }
}