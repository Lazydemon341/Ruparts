package com.ruparts.app.features.task.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R
import com.ruparts.app.features.taskslist.model.TaskPriority

class BottomSheetPriority(private var listener: OnPrioritySelectedListener?) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_task_priority, container, false)

        val high = view.findViewById<TextView>(R.id.high_priority)
        val medium = view.findViewById<TextView>(R.id.medium_priority)
        val low = view.findViewById<TextView>(R.id.low_priority)

        high.setOnClickListener {
            listener?.onPrioritySelected(TaskPriority.HIGH)
            dismiss()
        }
        medium.setOnClickListener {
            listener?.onPrioritySelected(TaskPriority.MEDIUM)
            dismiss()
        }

        low.setOnClickListener {
            listener?.onPrioritySelected(TaskPriority.LOW)
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnPrioritySelectedListener): BottomSheetPriority {
            return BottomSheetPriority(listener)
        }
    }

}