package com.ruparts.app.features.menu.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ruparts.app.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by viewModels()

    private lateinit var tasksButton: Button
    private lateinit var placementButton: Button
    private lateinit var workWithProductButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: tasksButton = findViewById(...)

        tasksButton = view.findViewById(R.id.tasks)
        placementButton = view.findViewById(R.id.placement)
        workWithProductButton = view.findViewById(R.id.work_with_product)

        tasksButton.setOnClickListener {

        }

        placementButton.setOnClickListener {

        }

        workWithProductButton.setOnClickListener {

        }
    }

}
