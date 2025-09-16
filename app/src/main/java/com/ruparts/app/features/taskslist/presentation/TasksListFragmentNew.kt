package com.ruparts.app.features.taskslist.presentation

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.ruparts.app.R
import com.ruparts.app.core.ui.theme.RupartsTheme
import com.ruparts.app.features.task.presentation.TaskFragment
import com.ruparts.app.features.taskslist.presentation.model.TaskListScreenEffect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksListFragmentNew : Fragment() {

    private val viewModel: TasksListViewModelNew by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LaunchedEffect(Unit) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is TaskListScreenEffect.NavigateToItemDetails -> {
                                findNavController().navigate(
                                    TasksListFragmentNewDirections.actionTaskslistFragmentToTaskFragment(effect.item)
                                )
                            }
                        }
                    }
                }

                val state by viewModel.state.collectAsState()
                RupartsTheme {
                    TaskListContent(
                        state = state,
                        onEvent = viewModel::handleEvent,
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        setupMenu()
        setupTaskUpdateListener()
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.task_list_menu, menu)
                setupSearch(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearch(menu: Menu) {
        val searchItem = menu.findItem(R.id.search_bar)
        val searchView = searchItem?.actionView as SearchView

        styleSearchView(searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.onSearchQueryChange(query)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    viewModel.onSearchQueryChange(query)
                    return true
                }
                return false
            }
        })
    }

    private fun styleSearchView(searchView: SearchView) {
        searchView.queryHint = getString(R.string.tasklist_search_hint)
        val searchFrame = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchFrame.background = GradientDrawable().apply {
            cornerRadius = resources.getDimension(R.dimen.search_corner_radius)
            setColor(ContextCompat.getColor(requireContext(), R.color.white_40))
        }
    }

    private fun setupTaskUpdateListener() {
        setFragmentResultListener(TaskFragment.TASK_UPDATED_REQUEST_KEY) { _, _ ->
            viewModel.refreshTasks()
        }
    }
}