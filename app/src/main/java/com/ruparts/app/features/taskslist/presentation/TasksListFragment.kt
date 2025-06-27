package com.ruparts.app.features.taskslist.presentation

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.ruparts.app.R
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.features.task.presentation.TaskFragment
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TasksListScreenState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksListFragment : Fragment() {

    private val viewModel: TasksListViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpandableTaskAdapter
    private lateinit var chipGroup: ChipGroup
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var fabScanQr: com.google.android.material.floatingactionbutton.FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_taskslist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        recyclerView = view.findViewById(R.id.taskslist_recycler_view)
        adapter = ExpandableTaskAdapter(
            onTaskClick = { task ->
                findNavController().navigate(
                    TasksListFragmentDirections.actionTaskslistFragmentToTaskFragment(task)
                )
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(TaskItemDecoration(requireContext()))

        progressIndicator = view.findViewById(R.id.tasks_progress_indicator)
        
        fabScanQr = view.findViewById(R.id.fab_scan_qr)
        fabScanQr.setOnClickListener {
            findNavController().navigate(TasksListFragmentDirections.actionTaskslistFragmentToQrScanFragment())
        }

        chipGroup = view.findViewById(R.id.tasks_chip_group)
        chipGroup.findViewById<Chip>(R.id.chip_all).setOnClickListener {
            viewModel.onTaskStatusFilterChange(null)
        }
        chipGroup.findViewById<Chip>(R.id.chip_todo).setOnClickListener {
            viewModel.onTaskStatusFilterChange(TaskStatus.TODO)
        }
        chipGroup.findViewById<Chip>(R.id.chip_in_progress).setOnClickListener {
            viewModel.onTaskStatusFilterChange(TaskStatus.IN_PROGRESS)
        }
        chipGroup.findViewById<Chip>(R.id.chip_completed).setOnClickListener {
            viewModel.onTaskStatusFilterChange(TaskStatus.COMPLETED)
        }
        chipGroup.findViewById<Chip>(R.id.chip_cancelled).setOnClickListener {
            viewModel.onTaskStatusFilterChange(TaskStatus.CANCELLED)
        }

        observeScreenState()
        setupTaskUpdateListener()
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                setupSearch(menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false // We don't have any menu items that need to be handled here
            }
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
    }

    private fun observeScreenState() {
        viewModel.screenState.collectWhileStarted(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: TasksListScreenState) {
        adapter.setTaskGroups(state.groups)
        updateLoadingState(state.isLoading)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        progressIndicator.isVisible = isLoading
    }

    private fun setupTaskUpdateListener() {
        setFragmentResultListener(TaskFragment.TASK_UPDATED_REQUEST_KEY) { _, _ ->
            viewModel.refreshTasks()
        }
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
}