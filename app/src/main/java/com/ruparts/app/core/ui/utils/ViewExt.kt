package com.ruparts.app.core.ui.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

fun View.paddingBelowSystemBars() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            top = systemBarsInsets.top
        )
        WindowInsetsCompat.CONSUMED
    }
}

fun View.paddingAboveSystemBars() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = view.paddingLeft + systemBarsInsets.left,
            bottom = view.paddingBottom + systemBarsInsets.bottom,
            right = view.paddingRight + systemBarsInsets.right,
        )
        WindowInsetsCompat.CONSUMED
    }
}

fun View.paddingAllSystemBars() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = systemBarsInsets.left,
            bottom = systemBarsInsets.bottom,
            right = systemBarsInsets.right,
            top = systemBarsInsets.top,
        )
        WindowInsetsCompat.CONSUMED
    }
}

fun View.alignAboveSystemBars() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<MarginLayoutParams> {
            leftMargin = leftMargin + systemBarsInsets.left
            bottomMargin = bottomMargin + systemBarsInsets.bottom
            rightMargin = rightMargin + systemBarsInsets.right
        }
        WindowInsetsCompat.CONSUMED
    }
}