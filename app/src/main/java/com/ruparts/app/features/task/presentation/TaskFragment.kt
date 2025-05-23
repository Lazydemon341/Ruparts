package com.ruparts.app.features.task.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.ruparts.app.R
import com.ruparts.app.core.extensions.collectWhileStarted
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskPriority
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var title: TextView
    private lateinit var description: EditText
    private lateinit var implementer: TextView
    private lateinit var finishAtDate: EditText
    private lateinit var priorityImage: ImageView
    private lateinit var priority: MaterialTextView
    private lateinit var createdDate: TextView
    private lateinit var changedDate: TextView
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setTask(requireNotNull(arguments?.getParcelable(ARG_TASK_KEY)))

        toolbar = view.findViewById(R.id.task_toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        title = view.findViewById(R.id.title_view)
        description = view.findViewById(R.id.description_view)
        implementer = view.findViewById(R.id.implementer_view)
        finishAtDate = view.findViewById(R.id.finishAt_date_view)
        priorityImage = view.findViewById(R.id.priority_imageview)
        priority = view.findViewById(R.id.priority_material_tv)
        createdDate = view.findViewById(R.id.date_view_created)
        changedDate = view.findViewById(R.id.date_view_changed)

        description.doOnTextChanged { text, start, before, count ->
            viewModel.setTaskDescription(text.toString())
        }

        implementer.setOnClickListener {
            showBottomSheetImplementer()
        }

        priority.setOnClickListener {
            showBottomSheetPriority()
        }

        finishAtDate.setOnClickListener {
            showDatePickerDialog()
        }

        observeScreenState()
    }

    private fun showBottomSheetImplementer() {
        val sheet = BottomSheetImplementer.newInstance(object : OnImplementerSelectedListener {
            override fun onItemSelected(implementer: TaskImplementer) {
                viewModel.setTaskImplementer(implementer)
            }
        })
        sheet.show(childFragmentManager, "MyBottomSheet")
    }

    private fun showBottomSheetPriority() {
        val sheet = BottomSheetPriority.newInstance(object : OnPrioritySelectedListener {
            override fun onPrioritySelected(item: TaskPriority) {
                viewModel.setTaskPriority(item)
            }
        })
        sheet.show(childFragmentManager, "MyBottomSheet")
    }

    private fun observeScreenState() {
        viewModel.screenState.collectWhileStarted(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: TaskScreenState) {
        val task = state.task
        title.text = task.title

        description.setText(task.description)

//        toolbar.setSubtitle(title.text)
//        toolbar.setSubtitleTextColor(R.color.white)

        when (task.priority) {
            TaskPriority.HIGH -> {
                priorityImage.setImageResource(R.drawable.arrow_up)
                priority.text = "Высокий"
            }

            TaskPriority.LOW -> {
                priorityImage.setImageResource(R.drawable.arrow_down)
                priority.text = "Низкий"
            }

            TaskPriority.MEDIUM -> {
                priorityImage.setImageResource(R.drawable.equal)
                priority.text = "Средний"
            }
        }
        createdDate.text = task.createdAtDate

        implementer.text = when (task.implementer) {
            TaskImplementer.USER -> "Работник склада"
            TaskImplementer.PURCHASES_MANAGER -> "Администратор"
            TaskImplementer.STOREKEEPER -> "Кладовщик"
            TaskImplementer.UNKNOWN -> ""
        }

        finishAtDate.setText(task.finishAtDate)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val sdf = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }.time
                val formattedDate = sdf.format(selectedDate)
                viewModel.setFinishAtDate(formattedDate)
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    companion object {
        const val ARG_TASK_KEY = "task"
    }
}