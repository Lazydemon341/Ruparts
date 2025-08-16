package com.ruparts.app.core.utils

import android.content.Context
import android.content.res.Configuration

fun Context.getQuantityStringForRuLocale(resId: Int, quantity: Int, vararg formatArgs: Any?): String {
    val config = Configuration(resources.configuration)
    config.setLocale(LOCALE_RUSSIAN)
    val localizedContext = createConfigurationContext(config)
    return localizedContext.resources.getQuantityString(resId, quantity, *formatArgs)
}