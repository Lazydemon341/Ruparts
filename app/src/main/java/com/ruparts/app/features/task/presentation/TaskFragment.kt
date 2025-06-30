package com.ruparts.app.features.task.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.ruparts.app.R
import com.ruparts.app.core.ui.utils.paddingAboveSystemBars
import com.ruparts.app.core.utils.collectWhileStarted
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.task.presentation.model.TaskUiEffect
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
    private lateinit var descriptionError: TextView
    private lateinit var implementerSpinner: Spinner
    private lateinit var implementerError: TextView
    private lateinit var finishAtDate: TextView
    private lateinit var finishAtError: TextView
    private lateinit var closeBtn: ImageView
    private lateinit var priorityImage: ImageView
    private lateinit var priority: MaterialTextView
    private lateinit var priorityError: TextView
    private lateinit var createdDate: TextView
    private lateinit var changedDate: TextView
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

        title = view.findViewById(R.id.title_view)
        description = view.findViewById(R.id.description_view)
        descriptionError = view.findViewById(R.id.description_error)
        implementerSpinner = view.findViewById(R.id.implementer_spinner)
        implementerError = view.findViewById(R.id.implementer_error)
        finishAtDate = view.findViewById(R.id.finishAt_date_view)
        finishAtError = view.findViewById(R.id.finish_at_error)
        closeBtn = view.findViewById(R.id.close_btn)
        priorityImage = view.findViewById(R.id.priority_imageview)
        priority = view.findViewById(R.id.priority_material_tv)
        priorityError = view.findViewById(R.id.priority_error)
        createdDate = view.findViewById(R.id.date_view_created)
        changedDate = view.findViewById(R.id.date_view_changed)
        saveButton = view.findViewById(R.id.button_save)
        statusButton = view.findViewById(R.id.button_in_work)
        cancelButton = view.findViewById(R.id.button_cancelled)
        progressIndicator = view.findViewById(R.id.progress_indicator)

        val buttonsLayout = view.findViewById<ViewGroup>(R.id.three_buttons_layout)
        buttonsLayout.paddingAboveSystemBars()

        description.setText(viewModel.screenState.value.task.description)
        description.doOnTextChanged { text, start, before, count ->
            viewModel.setTaskDescription(text.toString())
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

        closeBtn.setOnClickListener {
            viewModel.setFinishAtDate(null)
        }

        observeScreenState()
        collectUiEffects()
    }

    private fun updateImplementerSpinner(
        implementerKey: String?,
        implementers: Map<String, String>,
    ) {
        val implementersList = implementers.toList()

        implementerSpinner.adapter = ImplementerSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            implementersList
        )

        val currentImplementerKey = implementerKey
        val position = implementersList.indexOfFirst { it.first == currentImplementerKey }
        if (position in (0..implementersList.lastIndex)) {
            implementerSpinner.setSelection(position)
        }

        implementerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position >= 0 && position < implementersList.size) {
                    val selectedImplementerKey = implementersList[position].first
                    viewModel.setTaskImplementer(selectedImplementerKey)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setTaskImplementer(null)
            }
        }
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
                    clearValidationErrors()
                    Snackbar.make(requireView(), "Задача обновлена", Snackbar.LENGTH_SHORT).show()
                    setFragmentResult(TASK_UPDATED_REQUEST_KEY, bundleOf())
                }

                is TaskUiEffect.ValidationError -> {
                    clearValidationErrors()
                    effect.errorMessages.forEach { (key, message) ->
                        when (key) {
                            "description" -> showError(descriptionError, message)
                            "implementer" -> showError(implementerError, message)
                            "finish_at" -> showError(finishAtError, message)
                            "priority" -> showError(priorityError, message)
                        }
                    }
                }

                is TaskUiEffect.TaskUpdateError -> {
                    val errorMessage = if (effect.errorMessage.isNullOrEmpty()) {
                        "Не удалось обновить задачу"
                    } else {
                        effect.errorMessage
                    }
                    Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showError(errorTextView: TextView, errorMessage: String) {
        errorTextView.text = errorMessage
        errorTextView.visibility = View.VISIBLE
    }

    private fun clearValidationErrors() {
        descriptionError.visibility = View.GONE
        implementerError.visibility = View.GONE
        finishAtError.visibility = View.GONE
        priorityError.visibility = View.GONE
    }

    private fun updateUI(state: TaskScreenState) {
        val task = state.task
        title.text = task.title

        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = state.task.type.displayName

        updateLoadingState(state.isLoading)
        updatePriority(task.priority)
        updateStatus(task.status)
        updateImplementerSpinner(state.task.implementer, state.implementers)

        finishAtDate.text = task.finishAtDate.formatSafely(dateFormatter)
        createdDate.text = task.createdAtDate.formatSafely(dateFormatter)
        changedDate.text = task.updatedAtDate.formatSafely(dateFormatter)

        if (finishAtDate.text.isNullOrEmpty()) {
            removeBorder(finishAtDate)
            closeBtn.isVisible = false
        } else {
            addBorder(finishAtDate)
            closeBtn.isVisible = true
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

    private fun updateLoadingState(isLoading: Boolean) {
        progressIndicator.isVisible = isLoading
        saveButton.isEnabled = !isLoading
        description.isEnabled = !isLoading
        implementerSpinner.isEnabled = !isLoading
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

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

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
        const val TASK_UPDATED_REQUEST_KEY = "task_updated_request_key"

        private const val DATE_FORMAT_PATTERN = "dd MMM yyyy"
    }
}