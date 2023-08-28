package com.example.todolistapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todolistapp.databinding.FragmentMainBinding
import com.example.todolistapp.dataclasses.Item
import com.example.todolistapp.db.TaskDatabase
import com.example.todolistapp.objects.FragmentKeys

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val rcViewAdapter = ListAdapter(this@MainFragment)

    private var itemsList: MutableList<Item>? = null

    private var item = Item("", "", "")

    private lateinit var database: TaskDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = TaskDatabase(requireContext())

        binding.listRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.listRecyclerView.adapter = rcViewAdapter

        openTaskFragment()

        displayTasks()

        getResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getResult() { // NAH I am tired tbh
        if (arguments?.getBoolean(FragmentKeys.IS_HAVE_RESULT) == true) {
            if (arguments?.getBoolean(FragmentKeys.IS_EDIT) == true) {
                val editableItem = Item(
                    arguments?.getString(FragmentKeys.TASK_KEY).toString(),
                    arguments?.getString(FragmentKeys.TIME_KEY).toString(),
                    arguments?.getString(FragmentKeys.DATE_KEY).toString()
                )
                for (item in itemsList!!.indices) {
                    if (itemsList!![item].thing == arguments?.getString(FragmentKeys.EDITABLE_TASK).toString()) {
                        itemsList!![item] = editableItem
                    }
                }
            } else {
                item.thing = arguments?.getString(FragmentKeys.TASK_KEY).toString()
                item.time = arguments?.getString(FragmentKeys.TIME_KEY).toString()
                item.date = arguments?.getString(FragmentKeys.DATE_KEY).toString()

                itemsList?.add(item)
            }

            database.insertTask(item)
            rcViewAdapter.addItem(item)
        }
    }

    private fun displayTasks() = with(binding) {
        itemsList = database.getData()

        if (listRecyclerView.isNotEmpty()) {
            for (item in listRecyclerView.size - 1 downTo 0) {
                rcViewAdapter.removeItem(item)
            }
        }

        if (itemsList?.isNotEmpty() == true) {
            for (item in itemsList!!) {
                rcViewAdapter.addItem(item)
            }
        }
    }

    private fun openTaskFragment() = with(binding) {
        addButton.setOnClickListener {
            val taskFragment = TaskFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager
                .beginTransaction()
                .replace(R.id.mainHolder, taskFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun onLayoutClick(item: Item) { // <- may be use OnSwipeTouchListener class somehow
        val bundle = Bundle()
        bundle.putString(FragmentKeys.TASK_KEY, item.thing)
        bundle.putString(FragmentKeys.TIME_KEY, item.time)
        bundle.putString(FragmentKeys.DATE_KEY, item.date)
        bundle.putBoolean(FragmentKeys.IS_EDIT, true)

        val taskFragment = TaskFragment()
        taskFragment.arguments = bundle

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainHolder, taskFragment)
            .addToBackStack(null)
            .commit()

        // deleting task here (or editing i guess)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}