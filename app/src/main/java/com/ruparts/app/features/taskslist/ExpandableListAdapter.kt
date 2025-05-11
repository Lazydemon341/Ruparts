package com.ruparts.app.features.taskslist


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ruparts.app.R
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority


class ExpandableListAdapter(private val context: Context) : BaseExpandableListAdapter() {

    private var items: List<TaskListGroup> = ArrayList()
    private val layoutInflater = LayoutInflater.from(context)

    fun submitList(newList: List<TaskListGroup>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
        return items.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return items[groupPosition].tasks.size
    }

    override fun getGroup(groupPosition: Int): TaskListGroup {
        return items[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): TaskListItem {
        return items[groupPosition].tasks[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {

        val view = if (convertView != null) {
            convertView
        } else {
            layoutInflater.inflate(R.layout.expandable_list_group, parent, false)
        }

        val textView = view.findViewById<TextView>(R.id.exp_list_group_title)
        textView.text = getGroup(groupPosition).title

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = if (convertView != null) {
            convertView
        } else {
            layoutInflater.inflate(R.layout.expandable_list_child, parent, false)
        }

        val itemPriorityImage = view.findViewById<ImageView>(R.id.item_priority)
        val itemName = view.findViewById<TextView>(R.id.item_name)
        val itemDate = view.findViewById<TextView>(R.id.item_date)
        val itemNote = view.findViewById<TextView>(R.id.item_note)
        val itemDescription = view.findViewById<TextView>(R.id.item_comment)

        val item: TaskListItem = getChild(groupPosition, childPosition)

        itemName.text = item.title
        itemDate.text = item.date
        itemDescription.text = item.description

        when (item.priority) {
            (TaskPriority.HIGH) -> itemPriorityImage.setImageResource(R.drawable.arrow_up)
            (TaskPriority.LOW) -> itemPriorityImage.setImageResource(R.drawable.arrow_down)
            else -> itemPriorityImage.setImageResource(R.drawable.equal)
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}