package com.ruparts.app.features.task.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val impl1 = view.findViewById<TextView>(R.id.implementer1)
        val impl2 = view.findViewById<TextView>(R.id.implementer2)
        val impl3 = view.findViewById<TextView>(R.id.implementer3)

        impl1.setOnClickListener {
            listener?.onItemSelected(TaskImplementer.PURCHASES_MANAGER)
            dismiss()
        }
        impl2.setOnClickListener {
            listener?.onItemSelected(TaskImplementer.STOREKEEPER)
            dismiss()
        }
        impl3.setOnClickListener {
            listener?.onItemSelected(TaskImplementer.USER)
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnImplementerSelectedListener): BottomSheetImplementer {
            return BottomSheetImplementer(listener)
        }
    }

}