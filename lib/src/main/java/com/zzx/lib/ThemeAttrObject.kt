package com.zzx.lib

open class ThemeAttrObject(private val colorPrimary: Int,
                           private val colorPrimaryDark: Int,
                           private val colorPrimaryLight: Int,
                           private val colorAccent: Int,
                           private val textColorPrimary: Int,
                           private val textColorSecondary: Int,
                           private val dividerColor: Int) {

    private val configMap = mutableMapOf<String, Int>()

    fun getConfig(): MutableMap<String, Int> {
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