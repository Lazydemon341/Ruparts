package com.ruparts.app.core.ui.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

fun View.alignBelowStatusBar() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            top = systemBarsInsets.top
        )
        WindowInsetsCompat.CONSUMED
    }
}