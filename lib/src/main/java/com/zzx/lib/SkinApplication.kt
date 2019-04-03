package com.zzx.lib

import android.app.Application

class SkinApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SkinChanger.init(this)
    }
}