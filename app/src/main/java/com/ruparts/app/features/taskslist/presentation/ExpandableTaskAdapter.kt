package com.ruparts.app.features.taskslist.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruparts.app.R
import com.ruparts.app.core.utils.formatSafely
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private const val DATE_FORMAT_PATTERN = "dd MMM yyyy"

class ExpandableTaskAdapter(private val onTaskClick: (TaskListItem) -> Unit) :
    ListAdapter<Any, RecyclerView.ViewHolder>(TaskDiffCallback()) {

    companion object {
        const val TYPE_GROUP = 0
        const val TYPE_ITEM = 1
    }

    private val originalGroups = mutableSetOf<TaskListGroup>()
    private val expandedGroups = mutableSetOf<TaskListGroup>()

    private val dateFormatter by lazy(LazyThreadSafetyMode.NONE) {
        DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN)
    }

    fun setTaskGroups(groups: List<TaskListGroup>) {
        originalGroups.clear()
        originalGroups.addAll(groups)
        expandedGroups.clear()
        expandedGroups.addAll(groups)
        updateItemList()
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TaskListGroup -> TYPE_GROUP
            is TaskListItem -> TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown view type at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_GROUP -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.expandable_list_group, parent, false)
                GroupViewHolder(view) { position -> toggleGroup(position) }
            }

            TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.expandable_list_child, parent, false)
                ItemViewHolder(
                    itemView = view,
                    dateFormatter = dateFormatter,
                    onTaskClick = onTaskClick,
                )
            }

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is TaskListGroup -> (holder as GroupViewHolder).bind(item)
            is TaskListItem -> (holder as ItemViewHolder).bind(item)
        }
    }

    private fun updateItemList() {
        val newItems = mutableListOf<Any>()
        originalGroups.forEach { group ->
            newItems.add(group)
            if (expandedGroups.contains(group)) {
                newItems.addAll(group.tasks)
            }
        }
        super.submitList(newItems)
    }

    private fun toggleGroup(position: Int) {
        if (position >= 0 && position < itemCount) {
            val item = getItem(position)
            if (item is TaskListGroup) {
                if (expandedGroups.contains(item)) {
                    expandedGroups.remove(item)
                } else {
                    expandedGroups.add(item)
                }
                notifyItemChanged(position)
                updateItemList()
            }
        }
    }

    private inner class GroupViewHolder(
        itemView: View,
        private val onGroupClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.exp_list_group_title)
        private val indicatorView: ImageView = itemView.findViewById(R.id.group_indicator)

        init {
            itemView.setOnClickListener {
                onGroupClick(adapterPosition)
            }
        }

        fun bind(group: TaskListGroup) {
            titleTextView.text = group.title

            if (expandedGroups.contains(group)) {
                indicatorView.setImageResource(R.drawable.ic_expand_more)
            } else {
                indicatorView.setImageResource(R.drawable.ic_expand_less)
            }
        }
    }

    private class ItemViewHolder(
        private val itemView: View,
        private val dateFormatter: DateTimeFormatter,
        private val onTaskClick: (TaskListItem) -> Unit,
    ) :
        RecyclerView.ViewHolder(itemView) {
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemDate: TextView = itemView.findViewById(R.id.item_date)
        private val itemNote: TextView = itemView.findViewById(R.id.item_note)
        private val itemDescription: TextView = itemView.findViewById(R.id.item_comment)
        private val layoutDate: LinearLayout = itemView.findViewById(R.id.layout_date)

        fun bind(item: TaskListItem) {
            itemName.text = item.title
            itemDescription.text = item.description

            itemDate.text = item.finishAtDate?.formatSafely(dateFormatter)

            if (itemDate.text.isNullOrEmpty()) {
                layoutDate.isVisible = false
            } else {
                layoutDate.isVisible = true
            }

            if (item.finishAtDate != null) {
                val currentDate = LocalDate.now()

                if (item.finishAtDate.isBefore(currentDate)) {
                    itemNote.isVisible = true
                    itemNote.text = "просрочено"
                    itemNote.setBackgroundResource(R.drawable.border_item_note_red)
                } else if (ChronoUnit.DAYS.between(currentDate, item.finishAtDate).toInt() == 0) {
                    itemNote.isVisible = true
                    itemNote.text = "истекает сегодня"
                    itemNote.setBackgroundResource(R.drawable.border_item_note_yellow)
                } else if (ChronoUnit.DAYS.between(currentDate, item.finishAtDate).toInt() == 1) {
                    itemNote.isVisible = true
                    itemNote.text = "осталось 2 дня"
                    itemNote.setBackgroundResource(R.drawable.border_item_note_yellow)
                } else {
                    itemNote.isVisible = false
                }
            }

            val priorityDrawable = when (item.priority) {
                TaskPriority.HIGH -> R.drawable.arrow_up
                TaskPriority.LOW -> R.drawable.arrow_down
                TaskPriority.MEDIUM -> R.drawable.equal
            }

            val drawable = ContextCompat.getDrawable(itemView.context, priorityDrawable)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            itemName.setCompoundDrawables(drawable, null, null, null)

            itemView.setOnClickListener { onTaskClick(item) }
        }

    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is TaskListGroup && newItem is TaskListGroup -> oldItem.title == newItem.title
                oldItem is TaskListItem && newItem is TaskListItem -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is TaskListGroup && newItem is TaskListGroup -> oldItem == newItem
                oldItem is TaskListItem && newItem is TaskListItem -> oldItem == newItem
                else -> false
            }
        }
    }
}
