package com.zzx.lib

import android.util.AttributeSet
import android.view.View

data class SkinObject(
    val view: View,
    val needChangeAttrs: Set<AttrItem>,
    val attrs: AttributeSet
) {
    data class AttrItem(val attrName: String,
                        val attrValue: String,
                        val entryName: String,
                        val typeName: String)
}