package de.bentolor.katamanager

import android.R.id
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v4.app.NavUtils.getParentActivityName
import android.support.v4.app.NavUtils.navigateUpFromSameTask
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.text.format.DateFormat.format
import android.view.*
import de.bentolor.ahamanager.R
import kotlinx.android.synthetic.main.fragment_aha.*
import java.util.*

class AhaFragment : Fragment() {

    private var mAha: Aha = Aha()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ahaID = arguments.getSerializable(EXTRA_AHA_ID) as UUID

        mAha = AhaLab.get(activity).getAha(ahaID) ?: Aha()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_aha, container, false)

        if (getParentActivityName(activity) != null) {
            activity.actionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        return v
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        aha_title.setText(mAha.title)
        aha_title.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {
                mAha.title = c.toString()
            }

            override fun beforeTextChanged(c: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(c: Editable) {
            }
        })

        aha_date.setOnClickListener {
            val dialog = DatePickerFragment.newInstance(mAha.date)
            dialog.setTargetFragment(this@AhaFragment, REQUEST_DATE)
            dialog.show(activity.supportFragmentManager, DIALOG_DATE)
        }

        aha_time.setOnClickListener {
            val dialog = TimePickerFragment.newInstance(mAha.date)
            dialog.setTargetFragment(this@AhaFragment, REQUEST_TIME)
            dialog.show(activity.supportFragmentManager, DIALOG_TIME)
        }

        updateDate()

        aha_useful.isChecked = mAha.isUseful
        aha_useful.setOnCheckedChangeListener { buttonView, isChecked -> mAha.isUseful = isChecked }

    }

    private fun updateDate() {
        aha_date.text = format("EEEE, MMM dd, yyyy", mAha.date)
        aha_time.text = format("h:mm a", mAha.date)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            mAha.date = date
            updateDate()
        } else if (requestCode == REQUEST_TIME) {
            val date = data!!.getSerializableExtra(TimePickerFragment.EXTRA_TIME) as Date
            mAha.date = date
            updateDate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.fragment_aha, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.home -> {
                if (getParentActivityName(activity) != null) {
                    navigateUpFromSameTask(activity)
                }
                return true
            }
            R.id.menu_item_delete_crime -> {
                with(AhaLab[activity]) {
                    deleteAha(mAha)
                    saveAhas()
                }
                activity.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        AhaLab[activity].saveAhas()
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
