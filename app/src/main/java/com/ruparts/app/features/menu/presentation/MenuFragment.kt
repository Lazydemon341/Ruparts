package com.ruparts.app.features.menu.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ruparts.app.R
import com.ruparts.app.core.extensions.collectWhileStarted
import com.ruparts.app.features.menu.presentation.model.MenuUiEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment() {

    private val viewModel: MenuViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var tasksButton: Button
    private lateinit var placementButton: Button
    private lateinit var workWithProductButton: Button
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        collectUiEffects()
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up toolbar
        toolbar = view.findViewById(R.id.menu_toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        tasksButton = view.findViewById(R.id.tasks)
        placementButton = view.findViewById(R.id.placement)
        workWithProductButton = view.findViewById(R.id.work_with_product)
        logoutButton = view.findViewById(R.id.logout_button)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        tasksButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_taskslistFragment)
        }

        placementButton.setOnClickListener {
            // TODO
        }

        workWithProductButton.setOnClickListener {
            // TODO
        }

        logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun collectUiEffects() {
        viewModel.uiEffect.collectWhileStarted(viewLifecycleOwner) { effect ->
            when (effect) {
                is MenuUiEffect.NavigateToAuth -> {
                    findNavController().navigate(R.id.action_menuFragment_to_authFragment)
                }
            }
        }
    }
}
