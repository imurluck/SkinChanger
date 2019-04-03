package com.zzx.lib
import android.view.View
/**
 * 保存view及其需要修改的属性
 * create by zzx
 * create at 19-4-3
 */
data class SkinObject(
    val view: View,
    val needChangeAttrs: Set<AttrItem>
) {
    data class AttrItem(val attrName: String,
                        val attrValue: String,
                        val entryName: String,
                        val typeName: String)
}