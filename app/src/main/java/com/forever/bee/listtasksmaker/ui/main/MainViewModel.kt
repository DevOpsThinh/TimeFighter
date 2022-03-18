package com.forever.bee.listtasksmaker.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.forever.bee.listtasksmaker.models.TaskList

class MainViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    lateinit var onListAdded: (() -> Unit)
    lateinit var list: TaskList
    lateinit var onTaskAdded: (() -> Unit)

    val lists: MutableList<TaskList> by lazy {
        retrieveLists()
    }


    private fun retrieveLists(): MutableList<TaskList> {
        val sharedPreferencesContents = sharedPreferences.all
        val tasksList = ArrayList<TaskList>()

        for (taskList in sharedPreferencesContents) {
            val items = ArrayList(taskList.value as HashSet<String>)
            val list = TaskList(taskList.key, items)
            tasksList.add(list)
        }
        return tasksList
    }

    fun addTask(task: String) {
        list.tasks.add(task)
        onTaskAdded.invoke()
    }

    fun saveTasksList(list: TaskList) {
        sharedPreferences.edit().putStringSet(list.name, list.tasks.toHashSet()).apply()
        lists.add(list)
        onListAdded.invoke()
    }

    fun updateList(list: TaskList) {
        sharedPreferences.edit().putStringSet(list.name, list.tasks.toHashSet()).apply()
        lists.add(list)
    }

    fun refreshLists() {
        lists.clear()
        lists.addAll(retrieveLists())
    }
}