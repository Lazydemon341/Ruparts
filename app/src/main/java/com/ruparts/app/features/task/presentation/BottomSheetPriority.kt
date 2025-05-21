package com.ruparts.app.features.task.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruparts.app.R

class BottomSheetPriority(private var listener: OnItemSelectedListener?) : BottomSheetDialogFragment() {

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
            listener?.onItemSelected(high.text.toString())
            dismiss()
        }
        medium.setOnClickListener {
            listener?.onItemSelected(medium.text.toString())
            dismiss()
        }

        low.setOnClickListener {
            listener?.onItemSelected(low.text.toString())
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnItemSelectedListener): BottomSheetPriority {
            return BottomSheetPriority(listener)
        }
    }

}