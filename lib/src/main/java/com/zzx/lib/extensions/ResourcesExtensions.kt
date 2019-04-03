package com.zzx.lib.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
/**
 * 获取资源的工具类文件
 * create by zzx
 * create at 19-4-3
 */
/**
 * 根据resourcesId获取Color
 */
internal fun Resources.getCompatColor(context: Context, resourcesId: Int): Int {
    return if (Build.VERSION.SDK_INT >= 23) getColor(resourcesId, context.theme)
            else getColor(resourcesId)
}

/**
 * 根据resourcesId获取Drawable
 */
internal fun Resources.getCompatDrawable(context: Context, resourcesId: Int): Drawable {
    return when {
        Build.VERSION.SDK_INT >= 21 -> getDrawable(resourcesId, context.theme)
        Build.VERSION.SDK_INT >= 16 -> getDrawable(resourcesId)
        else -> {
            val tempValue = TypedValue()
            getValue(resourcesId, tempValue, false)
            val resolveId = tempValue.resourceId
            getDrawable(resolveId)
        }
    }
}