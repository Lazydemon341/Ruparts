package com.ruparts.app.features.task.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R

class BottomSheetImplementer(private var listener: OnItemSelectedListener?) : BottomSheetDialogFragment() {

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
            listener?.onItemSelected(impl1.text.toString())
            dismiss()
        }
        impl2.setOnClickListener {
            listener?.onItemSelected(impl2.text.toString())
            dismiss()
        }
        impl3.setOnClickListener {
            listener?.onItemSelected(impl3.text.toString())
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnItemSelectedListener): BottomSheetImplementer {
            return BottomSheetImplementer(listener)
        }
    }

}