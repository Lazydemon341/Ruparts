package com.ruparts.app.features.taskslist


import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import com.ruparts.app.features.taskslist.model.TaskListGroup


class ExpandableListAdapter : BaseExpandableListAdapter() {


    private var items: List<TaskListGroup> = ArrayList()

    fun submitList(newList: List<TaskListGroup>) {
        items = newList
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
        TODO("Not yet implemented")
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        TODO("Not yet implemented")
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}