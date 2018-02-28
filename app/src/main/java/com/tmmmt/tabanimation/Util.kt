package com.tmmmt.tabanimation

import android.content.Context
import android.os.Build
import java.util.*

/**
 * Created by Bhuvanesh BS on 23/1/18.
 * Created for TmmmT.
 */

@Suppress("DEPRECATION")
fun Context.changeAppLanguage(language: Language) {
    val userLocale = Locale(language.code)
    Locale.setDefault(userLocale)
    val configuration = resources.configuration.apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) setLocale(userLocale)
        else locale = userLocale
    }
    createConfigurationContext(configuration)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

@Suppress("DEPRECATION")
fun Context.getCurrentLocale(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        resources.configuration.locales[0]
    else resources.configuration.locale
}

enum class Language(val code: String) {

    English("en"), ARABIC("ar"), UNDEFINED("undefined");

    companion object {
        fun getLanguage(lang: String): Language {
            Language.values().forEach { language ->
                if (lang == language.code) return language
            }
            return UNDEFINED
        }
    }
}