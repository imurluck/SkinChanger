package com.zzx.lib

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
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
/**
 *
 * create by zzx
 * create at 19-4-3
 */
object SkinChanger {

    private const val TAG = "SkinChanger"

    private lateinit var application: Application
    private val contextMap: MutableMap<Context, MutableList<SkinObject>> = hashMapOf()

    private val resourcesProvider = ResourcesProvider()

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

    fun change(filePath: String? = resourcesProvider.resourcesFilePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.d(TAG, "loadResources -> resources file path is null, will not change skins")
            return
        }
        loadResources(filePath!!)
        changeViewsInMainThread()
    }

    /**
     * 开始改变皮肤
     * [inputStream] 皮肤包的文件流
     * [cacheFileName] 接下来的过程会将文件流缓存到Cache文件下，[cacheFileName]为缓存的名字
     */
    fun change(inputStream: InputStream, cacheFileName: String) {
        resourcesProvider.installSkinPackage(inputStream, cacheFileName) {
            loadResources(it)
            changeViewsInMainThread()
        }
    }

    /**
     * 当将皮肤包加载完成后就在主线程中修改所有需要修改的view
     */
    private fun changeViewsInMainThread() {
        SkinExecutor.mainThread.execute {
            for (context in contextMap) {
                for (skinObject in context.value) {
                    changeSkinObject(skinObject)
                }
            }
            onChangeFinishedListener.invoke()
        }
    }

    /**
     * 更换每个页面的View
     */
    private fun changeSkinObject(skinObject: SkinObject) {
        skinObject.apply {
            for (attr in skinObject.needChangeAttrs) {
                val resourcesId = skinResources.getIdentifier(attr.entryName, attr.typeName,
                    skinPackageName
                )
                if (-1 == resourcesId) {
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
     * 初始化信息，及创建缓存文件夹
     */
    fun init(application: Application) {
        SkinChanger.application = application
        resourcesProvider.config(application)
    }

    private var onChangeFinishedListener: () -> Unit = {}
}
