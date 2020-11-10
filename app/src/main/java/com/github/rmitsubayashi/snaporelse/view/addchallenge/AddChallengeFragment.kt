package com.github.rmitsubayashi.snaporelse.view.addchallenge

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import ca.antonious.materialdaypicker.MaterialDayPicker
import com.github.rmitsubayashi.snaporelse.R
import com.github.rmitsubayashi.snaporelse.model.entity.PhotoFrequency
import com.github.rmitsubayashi.snaporelse.view.util.EventObserver
import com.github.rmitsubayashi.snaporelse.view.util.showToast
import kotlinx.android.synthetic.main.fragment_add_challenge.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class AddChallengeFragment : Fragment() {
    private val viewModel: AddChallengeViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupClickListeners()
        setupViewBinding()

    }

    private fun setupNavigation() {
        viewModel.addedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddChallengeFragmentDirections.actionAddChallengeFragmentToChallengeListFragment()
            findNavController().navigate(action)
        })
    }

    private fun setupClickListeners() {
        val onDateSetListener = DatePickerDialog.OnDateSetListener {
            _, year, month, day ->
            // month is 0-11, not 1-12
            viewModel.setEndDate(LocalDate.of(year, month+1, day))
        }

        button_choose_end_date.setOnClickListener {
            val presetDate = viewModel.endDate.value ?: LocalDate.now()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                onDateSetListener,
                presetDate.year,
                // this is 0-11, not 1-12
                presetDate.monthValue-1,
                presetDate.dayOfMonth
            )
            datePickerDialog.show()
        }

        switch_hardcore.setOnCheckedChangeListener { _, checked ->
            viewModel.setIsHardCore(checked)
        }

        val onTimeSetListener = TimePickerDialog.OnTimeSetListener {
                _, hour, minutes ->
                viewModel.setReminder(LocalTime.of(hour, minutes))

        }
        switch_reminder.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    onTimeSetListener,
                    18,
                    0,
                    true
                )
                timePickerDialog.setOnCancelListener { switch_reminder.isChecked = false }
                timePickerDialog.show()
            } else {
                viewModel.setReminder(null)
            }
        }

        edittext_title.addTextChangedListener(
            object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    viewModel.setTitle(s.toString())
                }
            }
        )

        edittext_cheat_days.addTextChangedListener(
            object: TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    val intVal: Int? = if (s.toString() == "") null else Integer.parseInt(s.toString())
                    viewModel.setCheatDays(intVal)
                }
            }
        )

        button_add_challenge.setOnClickListener {
            viewModel.add()
        }

        day_picker.daySelectionChangedListener = object: MaterialDayPicker.DaySelectionChangedListener {
            override fun onDaySelectionChanged(selectedDays: List<MaterialDayPicker.Weekday>) {
                if (selectedDays.isEmpty()) {
                    viewModel.setDayOfDeadline(null)
                } else {
                    // there will only be one item in the list
                    val dayOfWeek = when (selectedDays[0]) {
                        MaterialDayPicker.Weekday.SUNDAY -> DayOfWeek.SUNDAY
                        MaterialDayPicker.Weekday.MONDAY -> DayOfWeek.MONDAY
                        MaterialDayPicker.Weekday.TUESDAY -> DayOfWeek.TUESDAY
                        MaterialDayPicker.Weekday.WEDNESDAY -> DayOfWeek.WEDNESDAY
                        MaterialDayPicker.Weekday.THURSDAY -> DayOfWeek.THURSDAY
                        MaterialDayPicker.Weekday.FRIDAY -> DayOfWeek.FRIDAY
                        MaterialDayPicker.Weekday.SATURDAY -> DayOfWeek.SATURDAY
                    }
                    viewModel.setDayOfDeadline(dayOfWeek)
                }
            }
        }

        populatePictureFrequencySpinner()
    }

    private fun setupViewBinding() {
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver {
            requireContext().showToast(it)
        })
        viewModel.endDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            label_chosen_end_date.text = it.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        })
        viewModel.notificationTime.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                label_reminder_value.text = ""
            } else {
                label_reminder_value.text =
                    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(it)
            }
        })
    }

    private fun populatePictureFrequencySpinner() {
        val spinnerItems = PhotoFrequency.values()
        val spinnerItemStrings = spinnerItems.map { it.stringVal }
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, spinnerItemStrings)
        spinner_pic_frequency.adapter = spinnerAdapter
        spinner_pic_frequency.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spinnerItems[position]
                viewModel.setPictureFrequency(selectedItem)

                val notificationVisibility = if (selectedItem == PhotoFrequency.WHENEVER) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                label_reminder.visibility = notificationVisibility
                label_reminder_description.visibility = notificationVisibility
                label_reminder_value.visibility = notificationVisibility
                switch_reminder.visibility = notificationVisibility

                val dayPickerVisibility = if (selectedItem == PhotoFrequency.EVERY_WEEK) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                label_deadline_day.visibility = dayPickerVisibility
                day_picker.visibility = dayPickerVisibility
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setPictureFrequency(null)
            }
        }
    }
}