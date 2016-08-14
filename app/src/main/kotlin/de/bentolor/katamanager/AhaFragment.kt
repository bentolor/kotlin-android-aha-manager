package de.bentolor.katamanager

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText

import java.util.Date
import java.util.UUID

import de.bentolor.ahamanager.R

class AhaFragment : Fragment() {

    private var mAha: Aha? = null
    private var mTitleField: EditText? = null
    private var mDateButton: Button? = null
    private var mTimeButton: Button? = null
    private var mSolvedBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ahaID = arguments.getSerializable(EXTRA_AHA_ID) as UUID

        mAha = AhaLab.get(activity).getAha(ahaID)

        setHasOptionsMenu(true)
    }

    @TargetApi(11)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_aha, container, false)

        if (NavUtils.getParentActivityName(activity) != null) {
            activity.actionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        mTitleField = v.findViewById(R.id.aha_title) as EditText
        mTitleField!!.setText(mAha!!.title)
        mTitleField!!.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {
                mAha!!.title = c.toString()
            }

            override fun beforeTextChanged(c: CharSequence, start: Int, count: Int,
                                           after: Int) {
                // left blank
            }

            override fun afterTextChanged(c: Editable) {
                // left blank
            }
        })

        mDateButton = v.findViewById(R.id.aha_date) as Button
        mDateButton!!.setOnClickListener {
            val fm = activity.supportFragmentManager
            val dialog = DatePickerFragment.newInstance(mAha!!.date)
            dialog.setTargetFragment(this@AhaFragment, REQUEST_DATE)
            dialog.show(fm, DIALOG_DATE)
        }

        mTimeButton = v.findViewById(R.id.aha_time) as Button
        mTimeButton!!.setOnClickListener {
            val fm = activity.supportFragmentManager
            val dialog = TimePickerFragment.newInstance(mAha!!.date)
            dialog.setTargetFragment(this@AhaFragment, REQUEST_TIME)
            dialog.show(fm, DIALOG_TIME)
        }

        updateDate()

        mSolvedBox = v.findViewById(R.id.aha_useful) as CheckBox
        mSolvedBox!!.isChecked = mAha!!.isUseful
        mSolvedBox!!.setOnCheckedChangeListener { buttonView, isChecked -> mAha!!.isUseful = isChecked }

        return v
    }

    private fun updateDate() {
        val d = mAha!!.date
        val c = DateFormat.format("EEEE, MMM dd, yyyy", d)
        val t = DateFormat.format("h:mm a", d)
        mDateButton!!.text = c
        mTimeButton!!.text = t
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mAha!!.date = date
            updateDate()
        } else if (requestCode == REQUEST_TIME) {
            val date = data!!.getSerializableExtra(TimePickerFragment.EXTRA_TIME) as Date
            mAha!!.date = date
            updateDate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.fragment_aha, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(activity) != null) {
                    NavUtils.navigateUpFromSameTask(activity)
                }
                return true
            }
            R.id.menu_item_delete_crime -> {
                val ahaLab = AhaLab.get(activity)
                ahaLab.deleteCrime(mAha!!)
                ahaLab.saveCrimes()
                activity.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        AhaLab.get(activity).saveCrimes()
    }

    companion object {
        val EXTRA_AHA_ID = "de.bentolor.ahamoment.aha_id"
        private val DIALOG_DATE = "date"
        private val DIALOG_TIME = "time"
        private val REQUEST_DATE = 0
        private val REQUEST_TIME = 1

        fun newInstance(ahaId: UUID): AhaFragment {
            val args = Bundle()
            args.putSerializable(EXTRA_AHA_ID, ahaId)

            val fragment = AhaFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
