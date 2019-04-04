package com.zzx.lib.extensions

import android.app.backup.SharedPreferencesBackupHelper
import android.content.SharedPreferences
import android.text.TextUtils
import com.zzx.lib.SkinChanger

/**
 * {@see ResourcesProvider} 外置皮肤插件路径的存储与获取
 */
private const val SKIN_PREFERENCE_PLUGIN_PATH = "skinPluginPath"

internal fun SharedPreferences.putSkinPluginPath(path: String) {
    edit().putString(SKIN_PREFERENCE_PLUGIN_PATH, path).commit()
}

internal fun SharedPreferences.getSkinPluginPath(): String? =
        getString(SKIN_PREFERENCE_PLUGIN_PATH, null)

internal fun SharedPreferences.onSkinPluginPathChange(listener: (path: String) -> Unit) {
    registerOnSharedPreferenceChangeListener {
        preferences, key ->
        if (TextUtils.equals(SKIN_PREFERENCE_PLUGIN_PATH, key)) {
            listener.invoke(preferences.getString(SKIN_PREFERENCE_PLUGIN_PATH, null))
        }
    }
}

/**
 * {@see ThemeConfigHandler} app主题配置信息的路径存储与获取
 */
private const val SKIN_PREFERENCE_CONFIG_PATH = "skinConfigPath"

internal fun SharedPreferences.putSkinThemeConfigPath(path: String) {
    edit().putString(SKIN_PREFERENCE_CONFIG_PATH, path).commit()
}

internal fun SharedPreferences.getSkinThemeConfigPath(): String? =
        getString(SKIN_PREFERENCE_CONFIG_PATH, null)

internal fun SharedPreferences.onSkinThemeConfigPathChange(listener: (path: String) -> Unit) {
    registerOnSharedPreferenceChangeListener {
        preferences, key ->
        if (TextUtils.equals(key, SKIN_PREFERENCE_CONFIG_PATH)) {
            listener.invoke(preferences.getString(SKIN_PREFERENCE_CONFIG_PATH, null))
        }
    }
}

/**
 * 皮肤更换方式的存储与获取
 */
private const val SKIN_PREFERENCE_CHANGE_TYPE = "skinChangeType"

internal fun SharedPreferences.putSkinChangeType(type: Int) {
    edit().putInt(SKIN_PREFERENCE_CHANGE_TYPE, type).commit()
}

internal fun SharedPreferences.getSKinChangeType(): Int =
        getInt(SKIN_PREFERENCE_CHANGE_TYPE, SkinChanger.SKIN_NO_CHANGE)

internal fun SharedPreferences.onSkinChangeTypeChange(listener: (type: Int) -> Unit) {
    registerOnSharedPreferenceChangeListener {
        preferences, key ->
        if (TextUtils.equals(key, SKIN_PREFERENCE_CHANGE_TYPE)) {
            listener.invoke(preferences.getInt(SKIN_PREFERENCE_CHANGE_TYPE, SkinChanger.SKIN_NO_CHANGE))
        }
    }
}

