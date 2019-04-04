package com.zzx.lib
/**
 * 此类是数据类,保存app主题颜色，用于App内部的主题更换方式(非皮肤插件化式更换)
 * 此类定义了MaterialDesign风格的基本颜色名称,如需添加新的颜色，需继承此类,并重写[getConfig]方法
 * create by zzx
 * create at 19-4-4
 */
open class ThemeAttrObject(private val colorPrimary: Int,
                           private val colorPrimaryDark: Int,
                           private val colorPrimaryLight: Int,
                           private val colorAccent: Int,
                           private val textColorPrimary: Int,
                           private val textColorSecondary: Int,
                           private val dividerColor: Int) {

    private val configMap = mutableMapOf<String, Int>()

    open fun getConfig(): MutableMap<String, Int> {
        configMap.clear()
        return configMap.apply {
            put(COLOR_PRIMARY, colorPrimary)
            put(COLOR_PRIMARY_DARK, colorPrimaryDark)
            put(COLOR_PRIMARY_LIGHT, colorPrimaryLight)
            put(COLOR_ACCENT, colorAccent)
            put(TEXT_COLOR_PRIMARY, textColorPrimary)
            put(TEXT_COLOR_SECONDARY, textColorSecondary)
            put(DIVIDER_COLOR, dividerColor)
        }
    }

    companion object {
        const val COLOR_PRIMARY = "colorPrimary"
        const val COLOR_PRIMARY_DARK = "colorPrimaryDark"
        const val COLOR_PRIMARY_LIGHT = "colorPrimaryLight"
        const val COLOR_ACCENT = "colorAccent"
        const val TEXT_COLOR_PRIMARY = "textColorPrimary"
        const val TEXT_COLOR_SECONDARY = "textColorSecondary"
        const val DIVIDER_COLOR = "dividerColor"
    }
}