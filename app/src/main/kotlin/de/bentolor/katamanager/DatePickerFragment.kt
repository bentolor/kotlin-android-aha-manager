package de.bentolor.katamanager

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import de.bentolor.ahamanager.R
import kotlinx.android.synthetic.main.dialog_date.*
import java.util.*

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

        val view = activity.layoutInflater.inflate(R.layout.dialog_date, null)

        dialog_date_datePicker.init(year, month, day) { view, y, m, dOM ->
            val calendar = Calendar.getInstance()
            calendar.time = mDate
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            // Translate picked date to Date
            mDate = GregorianCalendar(y, m, dOM, hour, minute).time

            // Update argument to preserve selected value on rotation
            arguments.putSerializable(EXTRA_DATE, mDate)
        }

        return AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok) { dialog, which -> sendResult(Activity.RESULT_OK) }
                .create()
    }

    private fun sendResult(resultCode: Int) {
        with(Intent()) {
            putExtra(EXTRA_DATE, mDate)
            targetFragment?.onActivityResult(targetRequestCode, resultCode, this)
        }
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
