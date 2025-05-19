package com.ruparts.app.features.task.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ruparts.app.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textview.MaterialTextView
import com.ruparts.app.core.extensions.collectWhileStarted
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels()

    private lateinit var description: EditText
    private lateinit var implementer: TextView
    private lateinit var finishAtDate: EditText
    private lateinit var priorityImage: ImageView
    private lateinit var priority: MaterialTextView
    private lateinit var createdDate: TextView
    private lateinit var changedDate: TextView
    private lateinit var toolbar: Toolbar

    private lateinit var task : TaskListItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.task_toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_taskFragment_to_taskslistFragment)
        }

        (activity as? AppCompatActivity)?.supportActionBar?.hide()

            description.setText(task.description)

        when (task.priority) { //это
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
        createdDate.text = task.date

        description = view.findViewById(R.id.description_view)
        implementer = view.findViewById(R.id.implementer_view)
        finishAtDate = view.findViewById(R.id.finishAt_date_view)
        priorityImage = view.findViewById(R.id.priority_image)
        priority = view.findViewById(R.id.priority_material_tv)
        createdDate = view.findViewById(R.id.date_view_created)
        changedDate = view.findViewById(R.id.date_view_changed)



        observeScreenState()
    }



    private fun observeScreenState() { //??
        viewModel.screenState
    }

    private fun updateUI(state: TaskScreenState) {
        task = state.task
        toolbar.title = state.title
    }


}