package com.zzx.lib

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.LayoutInflaterCompat
/**
 * 插件矿建提供的公共Activity基类，在[setContentView]后会默认更换app皮肤
 * 客户端需要更换皮肤的页面，需要继承此Activity， 或者在自己的Activity基类中参照此类进行配置
 * create by zzx
 * create at 19-4-4
 */
open class SkinActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory2(layoutInflater, CompatFactory2(delegate))
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        SkinChanger.release(this)
    }

    /**
     * 在[setContentView]方法设置contentView之后会回调此方法，此时如果有
     * 已设置的皮肤包，则应在视图显示之前更换,达到启动即更换的效果
     */
    override fun onContentChanged() {
        super.onContentChanged()
        SkinChanger.change()
    }
}