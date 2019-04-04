package com.zzx.lib

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log

class SkinApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SkinChanger.init(this)
    }

    companion object {
        private const val TAG = "SkinApplication"
    }
}