package com.forever.bee.listtasksmaker.ui.detail

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.forever.bee.listtasksmaker.MainActivity
import com.forever.bee.listtasksmaker.R
import com.forever.bee.listtasksmaker.databinding.ListDetailActivityBinding
import com.forever.bee.listtasksmaker.models.TaskList
import com.forever.bee.listtasksmaker.ui.detail.ui.detail.ListDetailFragment
import com.forever.bee.listtasksmaker.ui.detail.ui.detail.ListDetailViewModel
import com.forever.bee.listtasksmaker.ui.main.MainViewModel
import com.forever.bee.listtasksmaker.ui.main.MainViewModelFactory

class ListDetailActivity : AppCompatActivity() {

    private lateinit var binding: ListDetailActivityBinding

    private lateinit var viewModel: MainViewModel



//    lateinit var list: TaskList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListDetailActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this, MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(this)))[MainViewModel::class.java]
        viewModel.list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!

        binding.addTaskButton.setOnClickListener {
            showCreateTaskDialog()
        }

//        list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!

//        title = list.name
        title = viewModel.list.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListDetailFragment.newInstance())
                .commitNow()
        }
    }

    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
            .setTitle(R.string.task_to_add)
            .setView(taskEditText)
            .setPositiveButton(R.string.add_task) {dialog, _ ->
                val task = taskEditText.text.toString()
                viewModel.addTask(task)
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onBackPressed() {
        val bundle = Bundle()
        bundle.putParcelable(MainActivity.INTENT_LIST_KEY, viewModel.list)

        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}