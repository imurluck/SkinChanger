package com.zzx.lib

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.zzx.lib.exectors.SkinExecutor
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
/**
 * 用于安装皮肤包，包括assets文件夹下的皮肤包，或者从网络上获取皮肤包，
 * 提供给[SkinChanger]
 * create by zzx
 * create at 19-4-3
 */
class ResourcesProvider {

    private lateinit var skinCacheDir: File

    private lateinit var application: Application

    private lateinit var skinPreferences: SharedPreferences

    var resourcesFilePath: String? = null
    /**
     * 初始化配置, 初始化缓存目录，和以设置的皮肤文件路径
     */
    internal fun config(application: Application) {
        this.application = application
        val skinCacheDirPath = application.cacheDir.absolutePath +
                File.separator + SKIN_CACHE_DIR
        skinCacheDir = File(skinCacheDirPath)
        if (!skinCacheDir.exists()) {
            skinCacheDir.mkdirs()
        }
        skinPreferences = application.getSharedPreferences(SKIN_PREFERENCE_FILE_NAME, MODE_PRIVATE)
        resourcesFilePath = skinPreferences.getString(SKIN_PREFERENCE_NAME, null)
        skinPreferences.registerOnSharedPreferenceChangeListener {
            preferences, key ->
            when (key) {
                SKIN_PREFERENCE_NAME -> resourcesFilePath = preferences.getString(SKIN_PREFERENCE_NAME, null)
            }
        }
    }

    /**
     * 复制文件流的皮肤包到Cache目录中
     * [inputStream] 皮肤包的文件流
     * [cacheFileName] 接下来的过程会将文件流缓存到Cache文件下，[cacheFileName]为缓存的名字
     * [onInstallFinished] 资源安装完成后回调
     */
    internal fun installSkinPackage(inputStream: InputStream, cacheFileName: String,
                                    onInstallFinished: (fileName: String) -> Unit) {
        SkinExecutor.diskIO.execute {
            val cacheFile = File(skinCacheDir, cacheFileName)
            if (cacheFile.exists()) {
                cacheFile.delete()
            }
            val fos = FileOutputStream(cacheFile)
            val buff = ByteArray(1024)
            var len: Int
            while (inputStream.read(buff).apply { len = this } != -1) {
                fos.write(buff, 0, len)
            }
            inputStream.close()
            fos.close()
            skinPreferences.edit().putString(SKIN_PREFERENCE_NAME, cacheFile.absolutePath).commit()
            onInstallFinished.invoke(cacheFile.absolutePath)
        }
    }

    companion object {
        private const val SKIN_CACHE_DIR = "SkinCacheDir"

        private const val SKIN_PREFERENCE_FILE_NAME = "skin_preference"

        private const val SKIN_PREFERENCE_NAME = "skinFilePath"
    }
}