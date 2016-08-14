package de.bentolor.katamanager

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener

import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

import de.bentolor.ahamanager.R

class DatePickerFragment : DialogFragment() {

    private var mDate: Date? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDate = arguments.getSerializable(EXTRA_DATE) as Date

        // Create a calendar to get year,month,day
        val cal = Calendar.getInstance()
        cal.time = mDate
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val v = activity.layoutInflater.inflate(R.layout.dialog_date, null)


        val datePicker = v.findViewById(R.id.dialog_date_datePicker) as DatePicker
        datePicker.init(year, month, day) { view, selectedYear, monthOfYear, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.time = mDate
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            // Translate picked date to Date
            mDate = GregorianCalendar(selectedYear, monthOfYear, dayOfMonth, hour, minute).time

            // Update argument to preserve selected value on rotation
            arguments.putSerializable(EXTRA_DATE, mDate)
        }

        return AlertDialog.Builder(activity).setView(v).setTitle(R.string.date_picker_title).setPositiveButton(android.R.string.ok) { dialog, which -> sendResult(Activity.RESULT_OK) }.create()
    }

    private fun sendResult(resultCode: Int) {
        if (targetFragment == null) {
            return
        }

        val i = Intent()
        i.putExtra(EXTRA_DATE, mDate)

        targetFragment.onActivityResult(targetRequestCode, resultCode, i)
    }

    companion object {
        val EXTRA_DATE = "de.bentolor.ahamoment.date"

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_DATE, date)

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
