package com.ruparts.app.features.task.presentation

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ruparts.app.core.task.library.UserRole

class ImplementerSpinnerAdapter(
    context: Context,
    resource: Int,
    private val implementers: List<Pair<String, String>>
) : ArrayAdapter<String>(context, resource, implementers.map { it.second }) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        formatImplementerTextView(view, position, false)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        formatImplementerTextView(view, position, true)
        return view
    }

    private fun formatImplementerTextView(
        textView: TextView,
        position: Int,
        withPadding: Boolean,
    ) {
        val implementer = implementers[position]
        val key = implementer.first

        textView.textSize = 16f

        val isUserRole = UserRole.entries.any { it.key == key }
        if (isUserRole) {
            textView.setTypeface(textView.typeface, Typeface.BOLD)
        } else {
            textView.setTypeface(null, Typeface.NORMAL)
            if (withPadding) {
                textView.setPadding(
                    20 * context.resources.displayMetrics.density.toInt(),
                    textView.paddingTop,
                    textView.paddingRight,
                    textView.paddingBottom,
                )
            }
        }
    }
}