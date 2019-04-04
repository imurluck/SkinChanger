package com.zzx.lib

import android.app.Application

/**
 * 插件矿建提供的公共Application基类，在[onCreate]中初始化框架
 * 客户端需要更换皮肤的页面，需要继承此Application， 或者在自己的Application基类中参照此类进行配置
 * create by zzx
 * create at 19-4-4
 */
class SkinApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SkinChanger.init(this)
    }

    companion object {
        private const val TAG = "SkinApplication"
    }
}