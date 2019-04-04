package com.zzx.lib

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import com.zzx.lib.exectors.SkinExecutor
import com.zzx.lib.extensions.getCompatColor
import com.zzx.lib.extensions.getCompatDrawable
import java.io.InputStream
import java.util.*

/**
 *
 * create by zzx
 * create at 19-4-3
 */
object SkinChanger {

    /**
     * 内置颜色换肤
     */
    private const val SKIN_CHANGE_TYPE_INTERNAL = 0
    /**
     * 外置插件式换肤
     */
    private const val SKIN_CHANGE_TYPE_EXTERNAL = 1
    /**
     * 不更换皮肤
     */
    private const val SKIN_NO_CHANGE = -1

    private const val SKIN_PREFERENCE_FILE_NAME = "skin_preference"

    private const val SKIN_PREFERENCE_CHANGE_TYPE = "skinChangeType"

    private lateinit var skinPreferences: SharedPreferences

    private const val TAG = "SkinChanger"

    private lateinit var application: Application
    private val contextMap: MutableMap<Context, MutableList<SkinObject>> = hashMapOf()

    private var skinChangeType = SKIN_CHANGE_TYPE_INTERNAL
    /**
     * 插件皮肤包提供者，更换皮肤方式为外部插件方式时会使用
     */
    private val resourcesProvider = ResourcesProvider()

    /**
     * 主题配置处理器，更换皮肤方式为颜色主题方式时使用
     */
    private val themeConfigHandler = ThemeConfigHandler()

    private lateinit var skinResources: Resources
    private lateinit var skinPackageName: String

    /**
     * 收集需要更换皮肤的view， 以单个页面context分组
     */
    internal fun collectView(context: Context, skinObject: SkinObject) {
        if (contextMap[context] == null) {
            contextMap[context] = mutableListOf<SkinObject>().apply {
                add(skinObject)
            }
        } else {
            contextMap[context]?.add(skinObject)
        }
    }

    /**
     * 释放单个页面的View，此方法在[SkinActivity.onDestroy]中调用，
     * 如果客户端中的activity未继承子[SkinActivity], 则需要手动在客户端Activity中的onDestroy方法调用,
     * 避免页面无法即使回收而引起内存泄漏
     */
    fun release(context: Context) {
        contextMap[context]?.apply {
            clear()
            contextMap.remove(context)
        }
    }

    /**
     * 加载外部外部资源文件
     * [filePath] 文件路径
     */
    private fun loadResources(filePath: String) {
        val assetManager = AssetManager::class.java.newInstance()
        val addPathMethod = AssetManager::class.java.getMethod("addAssetPath",
            String::class.java)
        if (!addPathMethod.isAccessible) {
            addPathMethod.isAccessible = true
        }
        addPathMethod.invoke(assetManager, filePath)
        skinResources = Resources(assetManager, application.resources.displayMetrics,
            application.resources.configuration)
        skinPackageName = application.packageManager
            .getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES)
            .packageName
    }

    /**
     * 此方法应用在Activity启动的时候调用，框架根据皮肤切换的方式自动切换
     */
    fun change() {
        when (skinChangeType) {
            SKIN_CHANGE_TYPE_INTERNAL -> changeInternal(themeConfigHandler.getThemeConfigProperties() as Map<String, Int>)
            SKIN_CHANGE_TYPE_EXTERNAL -> changeExternal(resourcesProvider.resourcesFilePath)
            SKIN_NO_CHANGE -> Log.d(TAG, "will not change skins")
        }
    }

    /**
     * 插件式更换皮肤
     */
    fun changeExternal(filePath: String?) {
        if (TextUtils.isEmpty(filePath)) {
            Log.d(TAG, "loadResources -> resources file path is null, will not change skins")
            return
        }
        loadResources(filePath!!)
        changeViewsInMainThreadExternal()
        skinPreferences.edit().putInt(SKIN_PREFERENCE_CHANGE_TYPE, SKIN_CHANGE_TYPE_EXTERNAL).commit()
    }

    /**
     * 开始改变皮肤(插件式更换皮肤)
     * [inputStream] 皮肤包的文件流
     * [cacheFileName] 接下来的过程会将文件流缓存到Cache文件下，[cacheFileName]为缓存的名字
     */
    fun changeExternal(inputStream: InputStream, cacheFileName: String) {
        resourcesProvider.installSkinPackage(inputStream, cacheFileName) {
            changeExternal(it)
        }
    }

    fun changeInternal(themeAttrObject: ThemeAttrObject) {
        changeInternal(themeAttrObject.getConfig())
    }

    fun changeInternal(configMap: Map<String, Int>?) {
        configMap ?: return
        changeInternal(configMap, true)
    }

    private fun changeInternal(configMap: Map<String, Int>, update: Boolean) {
        if (update) {
            themeConfigHandler.updateConfig(configMap)
        }
        changeViewsInMainThreadInternal(configMap)
        skinPreferences.edit().putInt(SKIN_PREFERENCE_CHANGE_TYPE, SKIN_CHANGE_TYPE_INTERNAL).commit()
    }

    /**
     * 当将皮肤包加载完成后就在主线程中修改所有需要修改的view
     */
    private fun changeViewsInMainThreadExternal() {
        SkinExecutor.mainThread.execute {
            for (context in contextMap) {
                for (skinObject in context.value) {
                    changeSkinObjectExternal(skinObject)
                }
            }
            onChangeFinishedListener.invoke()
        }
    }

    /**
     * 更换每个View
     */
    private fun changeSkinObjectExternal(skinObject: SkinObject) {
        skinObject.apply {
            for (attr in needChangeAttrs) {
                val resourcesId = skinResources.getIdentifier(attr.entryName, attr.typeName,
                    skinPackageName
                )
                //皮肤包中没有对应的资源，则应该忽略此项属性
                if (0x0 == resourcesId) {
                    continue
                }
                when (attr.attrName) {
                    "background" -> {
                        view.background = skinResources.getCompatDrawable(view.context, resourcesId)
                    }
                    "textColor" -> {
                        (view as TextView).setTextColor(skinResources.getCompatColor(view.context, resourcesId))
                    }
                }
            }
        }
    }

    /**
     * 此方法需运行在主线程，更换所有页面
     */
    private fun changeViewsInMainThreadInternal(configMap: Map<String, Int>) {
        for (context in contextMap) {
            for (skinObject in context.value) {
                changeSkinObjectInternal(skinObject, configMap)
            }
        }
    }

    /**
     * 更换单个每个view
     */
    private fun changeSkinObjectInternal(skinObject: SkinObject, configMap: Map<String, Int>) {
        skinObject.apply {
            for (attr in needChangeAttrs) {
                when (attr.attrName) {
                    "background" -> configMap[attr.entryName]?.apply {
                        view.setBackgroundColor(this)
                    }
                    "textColor" -> configMap[attr.entryName]?.apply {
                        if (view is TextView) {
                            view.setTextColor(this)
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化信息，及创建缓存文件夹
     */
    fun init(application: Application) {
        SkinChanger.application = application
        skinPreferences = application.getSharedPreferences(SKIN_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
        skinChangeType = skinPreferences.getInt(SKIN_PREFERENCE_CHANGE_TYPE, -SKIN_NO_CHANGE)
        skinPreferences.registerOnSharedPreferenceChangeListener {
            preferences, key ->
            when (key) {
                SKIN_PREFERENCE_CHANGE_TYPE -> skinChangeType = preferences.getInt(SKIN_PREFERENCE_CHANGE_TYPE, SKIN_NO_CHANGE)
            }
        }
        resourcesProvider.config(application, skinPreferences)
        themeConfigHandler.config(application, skinPreferences)

    }

    private var onChangeFinishedListener: () -> Unit = {}
}
