package com.ruparts.app.core.ui.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView

fun View.paddingBelowSystemBars(top: Int = 0) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            top = top + systemBarsInsets.top
        )
        WindowInsetsCompat.CONSUMED
    }
}

fun View.paddingAboveSystemBars(bottom: Int = 0, start: Int = 0, end: Int = 0) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            left = start + systemBarsInsets.left,
            bottom = bottom + systemBarsInsets.bottom,
            right = end + systemBarsInsets.right,
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

fun View.alignAboveSystemBars(left: Int = 0, right: Int = 0, bottom: Int = 0) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.updateLayoutParams<MarginLayoutParams> {
            leftMargin = left + systemBarsInsets.left
            bottomMargin = bottom + systemBarsInsets.bottom
            rightMargin = right + systemBarsInsets.right
        }
        WindowInsetsCompat.CONSUMED
    }
}

fun RecyclerView.enableEdgeToEdge() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val innerPadding = insets.getInsets(
            WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout()
        )
        view.updatePadding(
            left = innerPadding.left,
            right = innerPadding.right,
            bottom = innerPadding.bottom,
        )
        insets
    }
}

fun Int.dp(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()