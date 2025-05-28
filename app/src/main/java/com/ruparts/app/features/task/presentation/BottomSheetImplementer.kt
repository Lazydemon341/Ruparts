package com.ruparts.app.features.task.presentation

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R
import com.ruparts.app.features.taskslist.model.TaskImplementer

class BottomSheetImplementer(private var listener: OnImplementerSelectedListener?) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_task_implementer, container, false)
        val implementersContainer = view.findViewById<LinearLayout>(R.id.implementers_container)

        // Create and add TextView for each implementer role
        addImplementerOption(implementersContainer, TaskImplementer.USER, getImplementerDisplayName(TaskImplementer.USER))
        addImplementerOption(implementersContainer, TaskImplementer.SUPPLIER, getImplementerDisplayName(TaskImplementer.SUPPLIER))
        addImplementerOption(implementersContainer, TaskImplementer.HEAD_OF_WAREHOUSE, getImplementerDisplayName(TaskImplementer.HEAD_OF_WAREHOUSE))
        addImplementerOption(implementersContainer, TaskImplementer.STOREKEEPER, getImplementerDisplayName(TaskImplementer.STOREKEEPER))
        addImplementerOption(implementersContainer, TaskImplementer.LOGISTICS_CONTROL, getImplementerDisplayName(TaskImplementer.LOGISTICS_CONTROL))
        addImplementerOption(implementersContainer, TaskImplementer.PURCHASES_MANAGER, getImplementerDisplayName(TaskImplementer.PURCHASES_MANAGER))
        addImplementerOption(implementersContainer, TaskImplementer.FLAWS_PROCESSING_MANAGER, getImplementerDisplayName(TaskImplementer.FLAWS_PROCESSING_MANAGER))

        return view
    }

    private fun addImplementerOption(container: LinearLayout, implementer: TaskImplementer, displayName: String) {
        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.implementer_item_height)
            )
            gravity = Gravity.CENTER_VERTICAL
            setPadding(resources.getDimensionPixelSize(R.dimen.implementer_item_padding_left), 0, 0, 0)
            text = displayName
            textSize = 16f
            setTypeface(typeface, Typeface.NORMAL)
            setOnClickListener {
                listener?.onItemSelected(implementer)
                dismiss()
            }
        }
        container.addView(textView)
    }
    
    private fun getImplementerDisplayName(implementer: TaskImplementer): String {
        return when (implementer) {
            TaskImplementer.USER -> "Пользователь"
            TaskImplementer.SUPPLIER -> "Поставщик"
            TaskImplementer.HEAD_OF_WAREHOUSE -> "Руководитель склада"
            TaskImplementer.STOREKEEPER -> "Кладовщик"
            TaskImplementer.LOGISTICS_CONTROL -> "Контроль логистики"
            TaskImplementer.PURCHASES_MANAGER -> "Менеджер по закупкам"
            TaskImplementer.FLAWS_PROCESSING_MANAGER -> "Менеджер по обработке брака"
            TaskImplementer.UNKNOWN -> "Неизвестно"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnImplementerSelectedListener): BottomSheetImplementer {
            return BottomSheetImplementer(listener)
        }
    }

}