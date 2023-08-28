package com.example.todolistapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.todolistapp.databinding.FragmentTaskBinding
import com.example.todolistapp.dataclasses.Item
import com.example.todolistapp.objects.FragmentKeys
import java.util.Calendar

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var timeTextDefault: String
    private lateinit var dateTextDefault: String

    private var resultData = Item("", "", "")
    private val editableData = Item("", "", "")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timeTextDefault = binding.setTimeButton.text.toString()
        dateTextDefault = binding.setDateButton.text.toString()

        onTouchCloseKeyboard()

        setSaveListener()
        setDateListener()
        setTimeListener()

        getEditableTask()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = TaskFragment()
    }

    private fun getEditableTask() = with(binding) {
        if (arguments?.getBoolean(FragmentKeys.IS_EDIT) == true) {
            enterTaskPlainTextVew.setText(arguments?.getString(FragmentKeys.TASK_KEY))
            setTimeButton.text = arguments?.getString(FragmentKeys.TIME_KEY)
            setDateButton.text = arguments?.getString(FragmentKeys.DATE_KEY)

            editableData.thing = enterTaskPlainTextVew.text.toString()
            editableData.time = setTimeButton.text.toString()
            editableData.date = setDateButton.text.toString()
        }
    }

    private fun setSaveListener() = with(binding) {
        saveButton.setOnClickListener {
            if (checkValidationOfEnteredData()) {
                resultData = sendDataToMainFragment()

                val bundle = Bundle()
                bundle.putString(FragmentKeys.TASK_KEY, resultData.thing)
                bundle.putString(FragmentKeys.TIME_KEY, resultData.time)
                bundle.putString(FragmentKeys.DATE_KEY, resultData.date)
                bundle.putBoolean(FragmentKeys.IS_HAVE_RESULT, true)

                if (editableData.thing.isNotEmpty()) {
                    bundle.putBoolean(FragmentKeys.IS_EDIT, true)
                    bundle.putString(FragmentKeys.EDITABLE_TASK, resultData.thing)
                } else
                    bundle.putBoolean(FragmentKeys.IS_EDIT, false)

                val mainFragment = MainFragment()
                mainFragment.arguments = bundle

                parentFragmentManager
                    .beginTransaction()
                    .replace(R.id.mainHolder, mainFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun setTimeListener() = with(binding) {
        setTimeButton.setOnClickListener {
            val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                setTimeButton.text = time
            }, 0, 0, true)
            timePickerDialog.window?.setBackgroundDrawableResource(
                R.color.medium_grey
            )

            timePickerDialog.show()

            val positiveButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
            val negativeButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            negativeButton.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
        }
    }

    private fun sendDataToMainFragment() : Item = with(binding) {
        val item = Item(
            enterTaskPlainTextVew.text.toString(),
            setTimeButton.text.toString(),
            setDateButton.text.toString()
        )

        if (item.time == timeTextDefault)
            item.time = SetNAData.NOTHING
        if (item.date == dateTextDefault)
            item.date = SetNAData.NOTHING

        return item
    }

    private fun setDateListener() = with(binding) {
        setDateButton.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val selectedDate = "$day/${month + 1}/$year"

                if (selectedDate.isNotEmpty()) {
                    setDateButton.text = selectedDate
                }
            }

            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), dateSetListener, year, month, day)
            datePickerDialog.show()
        }
    }

    private fun checkValidationOfEnteredData(): Boolean = with(binding) {
        val item = Item(
            enterTaskPlainTextVew.text.toString(),
            setTimeButton.text.toString(),
            setDateButton.text.toString()
        )

        if (item.thing.isEmpty()) {
            Toast.makeText(
                requireContext(),
                R.string.need_enter_task,
                Toast.LENGTH_SHORT
            ).show()

            return false
        } else return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.taskFragmentHolder.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.taskFragmentHolder.windowToken, 0)
            }

            true
        }
    }

}

object SetNAData {
    const val NOTHING = "N/A"
}