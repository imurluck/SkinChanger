package com.zzx.lib

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater.Factory2
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.zzx.lib.SkinObject.AttrItem
/**
 * 自定义的Factory2类,用来判断收集需要修改皮肤的view
 * [delegate] 代理，不影响系统的逻辑，用之来真正的创建view
 * create by zzx
 * create at 19-4-3
 */
class CompatFactory2(private val delegate: AppCompatDelegate): Factory2 {

    override fun onCreateView(name: String?, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(parent: View?, name: String?, context: Context, attrs: AttributeSet): View? {
        val view = delegate.createView(parent, name, context, attrs)
        if (!attrs.getAttributeBooleanValue(
                APP_NAMESPACE,
                SKIN_ENABLED_TAG, false)) {
            return view
        }
        val needChangeAttrs = mutableSetOf<AttrItem>()
        for (i in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(i)
            val attrValue = attrs.getAttributeValue(i)
            //不保存view的id属性
            if (TextUtils.equals(attrName, ID_TAG)) {
                continue
            }
            //保存view值为int类型的属性， 都是需要修改的
            if (attrValue.startsWith("@")) {
                val id = attrValue.substring(1).toInt()
                val entryName = context.resources.getResourceEntryName(id)
                val typeName = context.resources.getResourceTypeName(id)
                needChangeAttrs.add(AttrItem(attrName, attrValue, entryName, typeName))
            }
            SkinChanger.collectView(context, SkinObject(view, needChangeAttrs, attrs))
        }
        return view
    }

    companion object {

        private const val TAG = "CompatFactory2"
        private const val SKIN_ENABLED_TAG = "skin_enable"
        private const val ID_TAG = "id"
        private const val APP_NAMESPACE = "http://schemas.android.com/apk/res-auto"
    }
}