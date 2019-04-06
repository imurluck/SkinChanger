package com.zzx.lib

import android.app.Application
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.zzx.lib.extensions.getSkinThemeConfigPath
import com.zzx.lib.extensions.onSkinThemeConfigPathChange
import com.zzx.lib.extensions.putSkinThemeConfigPath
import java.io.*
import java.lang.Exception
import java.util.*

/**
 * 动态改变和获取主题颜色，主题属性参考配置文件{@see themeConfig.properties}
 */
internal class ThemeConfigHandler {

    private lateinit var skinPreferences: SharedPreferences
    private lateinit var application: Application

    private lateinit var skinConfigDir: File

    private var skinConfigFilePath: String? = null

    internal fun config(application: Application, skinPreferences: SharedPreferences) {
        this.skinPreferences = skinPreferences
        this.application = application
        val skinConfigDirPath = application.cacheDir.absolutePath + File.separator +
                SKIN_CONFIG_DIR
        skinConfigDir = File(skinConfigDirPath)
        if (!skinConfigDir.exists()) {
            skinConfigDir.mkdirs()
        }
        skinConfigFilePath = skinPreferences.getSkinThemeConfigPath()
        skinPreferences.onSkinThemeConfigPathChange {
            skinConfigFilePath = it
        }
    }

    internal fun <T: ThemeAttrObject> getThemeConfig(clazz: Class<T>): T? {
        skinConfigFilePath?.apply {
            var br: BufferedReader? = null
            try {
                br = BufferedReader(FileReader(this))
                var json = ""
                while (br.readLine()?.apply {
                        json += this
                    } != null)
                    return Gson().fromJson(json, clazz)
            } catch (e: Exception) {
                Log.e(TAG, "getThemeConfig -> ${e.message}")
            } finally {
                br?.close()
            }

        }
        Log.e(TAG, "getThemeConfig -> skinConfigFilePath is null")
        return null
    }

    internal fun updateConfig(themeAttrObject: ThemeAttrObject) {
        if (TextUtils.isEmpty(skinConfigFilePath)) {
            skinConfigFilePath = skinConfigDir.absolutePath + File.separator + themeAttrObject.name + ".json"
        }
        val configFile = File(skinConfigFilePath)
        //如果此前没有次配置文件，则创建一个新的配置文件
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        val fos = FileOutputStream(configFile)
        fos.write(Gson().toJson(themeAttrObject).toByteArray(Charsets.UTF_8))
        skinPreferences.putSkinThemeConfigPath(skinConfigFilePath!!)
        fos.close()
    }

    companion object {

        private const val TAG = "ThemeConfigHandler"

        private const val SKIN_CONFIG_DIR = "SkinConfigDir"
    }
}