package com.zzx.lib

import android.app.Application
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import java.io.*
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
        skinConfigFilePath = skinPreferences.getString(SKIN_PREFERENCE_CONFIG_PATH, null)
        skinPreferences.registerOnSharedPreferenceChangeListener {
            preferences, key ->
            if (TextUtils.equals(key, SKIN_PREFERENCE_CONFIG_PATH)) {
                skinConfigFilePath = preferences.getString(SKIN_PREFERENCE_CONFIG_PATH, null)
            }
        }
    }

    private var configProperties: Properties? = null

    internal fun getThemeConfigProperties(): Properties {
        val properties = Properties()
        val isr = InputStreamReader(FileInputStream(File(skinConfigFilePath)), "utf-8")
        return properties.apply {
            load(isr)
            configProperties = this
            isr.close()
        }
    }

    internal fun updateConfig(configMap: Map<String, Int>) {
        if (TextUtils.isEmpty(skinConfigFilePath)) {
            skinConfigFilePath = skinConfigDir.absolutePath + File.separator + DEFAULT_CONFIG_NAME
        }
        val configFile = File(skinConfigFilePath)
        //如果此前没有次配置文件，则创建一个新的配置文件
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        if (configProperties == null) {
            configProperties = getThemeConfigProperties()
        }
        configProperties?.apply {
            val keySet = configMap.keys
            for (key in keySet) {
                this[key] = configMap[key].toString()
            }
            val fos = FileOutputStream(skinConfigFilePath)
            store(fos, null)
            fos.close()
        }
    }

    companion object {
        private const val DEFAULT_CONFIG_NAME = "themeConfig.properties"

        private const val TAG = "ThemeConfigHandler"

        private const val SKIN_PREFERENCE_CONFIG_PATH = "skinConfigPath"

        private const val SKIN_CONFIG_DIR = "SkinConfigDir"
    }
}