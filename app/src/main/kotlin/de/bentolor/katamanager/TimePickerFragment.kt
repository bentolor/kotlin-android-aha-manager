package de.bentolor.katamanager

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.TimePicker
import de.bentolor.ahamanager.R
import java.util.*

class TimePickerFragment : DialogFragment() {

    private var mDate: Date? = null

    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDate = arguments.getSerializable(EXTRA_TIME) as Date

        // Create a calendar to get year,month,day
        val cal = Calendar.getInstance()
        cal.time = mDate
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        val v = activity.layoutInflater.inflate(R.layout.dialog_time, null)


        val timePicker = v.findViewById(R.id.dialog_time_timePicker) as TimePicker
        timePicker.currentHour = hour
        timePicker.currentMinute = minute

        timePicker.setOnTimeChangedListener { view, hourOfDay, selectedMinute ->
            val calendar = Calendar.getInstance()
            calendar.time = mDate
            val year = calendar.get(Calendar.YEAR)
            val monthOfYear = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

            mDate = GregorianCalendar(year, monthOfYear, dayOfMonth, hourOfDay, selectedMinute).time

            // Update argument to preserve selected value on rotation
            arguments.putSerializable(EXTRA_TIME, mDate)
        }

        return AlertDialog.Builder(activity).setView(v).setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok) { dialog, which -> sendResult(Activity.RESULT_OK) }.create()
    }

    private fun sendResult(resultCode: Int) {
        if (targetFragment == null) {
            return
        }

        val i = Intent()
        i.putExtra(EXTRA_TIME, mDate)

        targetFragment.onActivityResult(targetRequestCode, resultCode, i)
    }

    companion object {
        val EXTRA_TIME = "de.bentolor.ahamoment.time"

        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_TIME, date)

            val fragment = TimePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
