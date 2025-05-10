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

    override fun getGroup(groupPosition: Int): Any {
        return items[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
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

        val view = layoutInflater.inflate(R.layout.expandable_list_group, parent, false)

        var textView = view.findViewById<TextView>(R.id.exp_list_group_title)

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = layoutInflater.inflate(R.layout.expandable_list_child, parent, false)

        var priorityImage = view.findViewById<ImageView>(R.id.item_priority)
        var itemName = view.findViewById<TextView>(R.id.item_name)
        var itemDate = view.findViewById<TextView>(R.id.item_date)
        var itemNote = view.findViewById<TextView>(R.id.item_note)
        var itemDescription = view.findViewById<TextView>(R.id.item_comment)

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}