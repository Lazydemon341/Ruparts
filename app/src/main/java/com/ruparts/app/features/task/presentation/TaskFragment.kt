package com.ruparts.app.features.task.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.ruparts.app.R
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.task.presentation.model.TaskUiEffect
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var title: TextView
    private lateinit var description: EditText
    private lateinit var implementer: TextView
    private lateinit var finishAtDate: TextView
    private lateinit var priorityImage: ImageView
    private lateinit var priority: MaterialTextView
    private lateinit var createdDate: TextView
    private lateinit var changedDate: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var saveButton: Button
    private lateinit var statusButton: MaterialButton
    private lateinit var cancelButton: Button
    private lateinit var progressIndicator: CircularProgressIndicator

    private val dateFormatter by lazy(LazyThreadSafetyMode.NONE) {
        DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)
    }

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

        toolbar.setSubtitle(viewModel.screenState.value.task.type.displayName)
        toolbar.setSubtitleTextColor(resources.getColor(R.color.white))

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        title = view.findViewById(R.id.title_view)
        description = view.findViewById(R.id.description_view)
        implementer = view.findViewById(R.id.implementer_view)
        finishAtDate = view.findViewById(R.id.finishAt_date_view)
        priorityImage = view.findViewById(R.id.priority_imageview)
        priority = view.findViewById(R.id.priority_material_tv)
        createdDate = view.findViewById(R.id.date_view_created)
        changedDate = view.findViewById(R.id.date_view_changed)
        saveButton = view.findViewById(R.id.button_save)
        statusButton = view.findViewById(R.id.button_in_work)
        cancelButton = view.findViewById(R.id.button_cancelled)
        progressIndicator = view.findViewById(R.id.progress_indicator)

        description.setText(viewModel.screenState.value.task.description)
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

        saveButton.setOnClickListener {
            viewModel.updateTask()
        }

        observeScreenState()
        collectUiEffects()
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

    private fun collectUiEffects() {
        viewModel.uiEffect.collectWhileStarted(viewLifecycleOwner) { effect ->
            when (effect) {
                is TaskUiEffect.TaskUpdateSuccess -> {
                    Snackbar.make(requireView(), "Задача обновлена", Snackbar.LENGTH_SHORT).show()
                    setFragmentResult(TASK_UPDATED_REQUEST_KEY, bundleOf())
                }

                is TaskUiEffect.TaskUpdateError -> {
                    Snackbar.make(
                        requireView(),
                        "Не удалось обновить задачу",
                        Snackbar.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    private fun updateUI(state: TaskScreenState) {
        val task = state.task
        title.text = task.title

        updateLoadingState(state.isLoading)
        updatePriority(task.priority)
        updateStatus(task.status)
        updateImplementer(task.implementer)

        createdDate.text = task.createdAtDate.formatSafely(dateFormatter)
        changedDate.text = task.updatedAtDate.formatSafely(dateFormatter)
        finishAtDate.text = task.finishAtDate.formatSafely(dateFormatter)
        if (finishAtDate.text.isNullOrEmpty()) {
            removeBorder(finishAtDate)
        } else {
            addBorder(finishAtDate)
        }
    }

    private fun updatePriority(taskPriority: TaskPriority) {
        when (taskPriority) {
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
    }

    private fun updateStatus(taskStatus: TaskStatus) {
        when (taskStatus) {
            TaskStatus.TODO -> {
                statusButton.text = "В работу"
                showEnabledState(statusButton, R.drawable.play_arrow)
                statusButton.setOnClickListener {
                    viewModel.changeTaskStatus(TaskStatus.IN_PROGRESS)
                }

                cancelButton.isVisible = true
                cancelButton.setOnClickListener {
                    viewModel.changeTaskStatus(TaskStatus.CANCELLED)
                }
            }

            TaskStatus.IN_PROGRESS -> {
                statusButton.text = "Закрыть"
                showEnabledState(statusButton, R.drawable.baseline_close_24)
                statusButton.setOnClickListener {
                    viewModel.changeTaskStatus(TaskStatus.COMPLETED)
                }

                cancelButton.isVisible = true
                cancelButton.setOnClickListener {
                    viewModel.changeTaskStatus(TaskStatus.CANCELLED)
                }
            }

            TaskStatus.COMPLETED -> {
                statusButton.text = "Завершена"
                showDisabledState(statusButton)

                cancelButton.isVisible = false
                cancelButton.setOnClickListener(null)
            }

            TaskStatus.CANCELLED -> {
                statusButton.text = "Отменена"
                showDisabledState(statusButton)

                cancelButton.isVisible = false
                cancelButton.setOnClickListener(null)
            }
        }
    }

    private fun updateImplementer(taskImplementer: TaskImplementer) {
        implementer.text = when (taskImplementer) {
            TaskImplementer.USER -> "Пользователь"
            TaskImplementer.SUPPLIER -> "Поставщик"
            TaskImplementer.HEAD_OF_WAREHOUSE -> "Руководитель склада"
            TaskImplementer.STOREKEEPER -> "Кладовщик"
            TaskImplementer.LOGISTICS_CONTROL -> "Контроль логистики"
            TaskImplementer.PURCHASES_MANAGER -> "Менеджер по закупкам"
            TaskImplementer.FLAWS_PROCESSING_MANAGER -> "Менеджер по обработке брака"
            TaskImplementer.UNKNOWN -> ""
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        progressIndicator.isVisible = isLoading
        saveButton.isEnabled = !isLoading
        description.isEnabled = !isLoading
        implementer.isEnabled = !isLoading
        priority.isEnabled = !isLoading
        finishAtDate.isEnabled = !isLoading
    }

    private fun showDatePickerDialog() {
        val currentDate = viewModel.screenState.value.task.finishAtDate
            ?: LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue - 1 // DatePickerDialog uses 0-based months
        val dayOfMonth = currentDate.dayOfMonth

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.setFinishAtDate(selectedDate)
            },
            year, month, dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun showEnabledState(button: MaterialButton, @DrawableRes iconRes: Int) {
        button.isEnabled = true
        button.setIconResource(iconRes)
        button.setBackgroundColor(resources.getColor(R.color.light_purple, null))
        button.setTextColor(resources.getColor(R.color.black, null))
        button.iconTint = resources.getColorStateList(R.color.black, null)
    }

    private fun showDisabledState(button: MaterialButton) {
        button.isEnabled = false
        button.setIconResource(0)
        button.setBackgroundColor(resources.getColor(R.color.gray, null))
        button.setTextColor(resources.getColor(R.color.white, null))
        button.setOnClickListener(null)
    }

    private fun removeBorder(view: View) {
        view.background = null
    }

    private fun addBorder(view: View) {
        view.setBackgroundResource(R.drawable.border_rectangle_radius5dp)
    }

    companion object {
        const val ARG_TASK_KEY = "task"
        const val TASK_UPDATED_REQUEST_KEY = "task_updated_request_key"

        private const val DATE_FORMAT_PATTERN = "dd MMM yyyy"
    }
}