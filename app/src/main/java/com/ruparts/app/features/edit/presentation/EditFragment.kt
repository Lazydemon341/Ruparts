package com.ruparts.app.features.edit.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.ruparts.app.core.ui.theme.RupartsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RupartsTheme {
                    EditScreen()
                }
            }
        }
    }
}