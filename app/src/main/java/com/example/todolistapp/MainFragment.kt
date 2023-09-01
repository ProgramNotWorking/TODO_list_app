package com.example.todolistapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import com.example.todolistapp.databinding.FragmentMainBinding
import com.example.todolistapp.dataclasses.Item
import com.example.todolistapp.db.TaskDatabase
import com.example.todolistapp.objects.FragmentKeys

class MainFragment : Fragment(), ListAdapter.OnLayoutClickListener {

    private lateinit var binding: FragmentMainBinding
    private val rcViewAdapter = ListAdapter(this@MainFragment)

    private var itemsList: MutableList<Item>? = null

    private lateinit var database: TaskDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = TaskDatabase(requireContext())

        binding.listRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.listRecyclerView.adapter = rcViewAdapter

        binding.tempDeletingButton.setOnClickListener {
            database.deleteData()

            if (binding.listRecyclerView.isNotEmpty()) { // thing just for some tests (remove it later)
                for (item in binding.listRecyclerView.size - 1 downTo 0) {
                    rcViewAdapter.removeItem(item)
                }
            }
        }

        openTaskFragment()

        itemsList = database.getData()
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

    private fun getResult() { // I FIX THIS SHIT (i wanna cut my head out of body)
        if (arguments?.getBoolean(FragmentKeys.IS_HAVE_RESULT) == true) {

            val receivedData = Item (
                arguments?.getString(FragmentKeys.TASK_KEY).toString(),
                arguments?.getString(FragmentKeys.TIME_KEY).toString(),
                arguments?.getString(FragmentKeys.DATE_KEY).toString()
            )

            if (arguments?.getBoolean(FragmentKeys.IS_EDIT) == true) { // check this shit
                val editableItem = Item(
                    arguments?.getString(FragmentKeys.EDITABLE_TASK).toString(),
                    arguments?.getString(FragmentKeys.EDITABLE_TIME).toString(),
                    arguments?.getString(FragmentKeys.EDITABLE_DATE).toString()
                )

                for (item in itemsList!!.indices) {
                    if (itemsList!![item].thing == editableItem.thing &&
                        itemsList!![item].time == editableItem.time &&
                        itemsList!![item].date == editableItem.date) {

                        itemsList!![item] = receivedData
                        rcViewAdapter.replaceItem(item, itemsList!![item])

                        break
                    }
                }
            } else {
                itemsList?.add(receivedData)
                rcViewAdapter.addItem(receivedData)
            }

            if (arguments?.getBoolean(FragmentKeys.IS_DELETE) == true) {
                val deletingData = Item(
                    arguments?.getString(FragmentKeys.DELETING_TASK).toString(),
                    arguments?.getString(FragmentKeys.DELETING_TIME).toString(),
                    arguments?.getString(FragmentKeys.DELETING_DATE).toString()
                )

                deleteItemFromListByData(deletingData)
                rcViewAdapter.removeItemByData(deletingData)
            }

            database.repopulateTable(itemsList)
        }
    }

    private fun displayTasks() {
        if (itemsList?.isNotEmpty() == true) {
            for (test in itemsList!!) {
                rcViewAdapter.addItem(test)
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

    override fun onLayoutClick(item: Item) { // <- may be use OnSwipeTouchListener class somehow
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
    }

    private fun deleteItemFromListByData(data: Item) {
        for (item in itemsList!!.indices) {
            if (itemsList!![item].thing == data.thing &&
                itemsList!![item].time == data.time &&
                itemsList!![item].date == data.date) {

                itemsList!!.removeAt(item)
                break
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}