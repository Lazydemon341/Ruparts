package com.ruparts.app.features.taskslist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.ruparts.app.R
import com.ruparts.app.features.taskslist.ExpandableListAdapter
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TasksListScreenState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksListFragment : Fragment() {

    private val viewModel: TasksListViewModel by viewModels()

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: ExpandableListAdapter
    private lateinit var searchView: SearchView
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_taskslist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandableListView = view.findViewById(R.id.taskslist_expandable_list_view)
        adapter = ExpandableListAdapter(requireContext())
        expandableListView.setAdapter(adapter)

        searchView = view.findViewById(R.id.taskslist_searchview)

        tabLayout = view.findViewById(R.id.tasks_tablayout);
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE;

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(query: String): Boolean {
                viewModel.filterTasks(query)
                return true
            }
        })

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab?.position) {
                    1 -> viewModel.filterTasks(TaskStatus.TODO)
                    2 -> viewModel.filterTasks(TaskStatus.IN_PROGRESS)
                    3 -> viewModel.filterTasks(TaskStatus.COMPLETED)
                    4 -> viewModel.filterTasks(TaskStatus.CANCELLED)
                    0 -> viewModel.showAllTasks()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
            
        } )

        observeScreenState()
    }

    private fun observeScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: TasksListScreenState) {
        adapter.submitList(state.groups)
        (activity as AppCompatActivity).supportActionBar?.title = state.title
    }

}