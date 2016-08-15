package de.bentolor.katamanager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils.getParentActivityName
import android.support.v4.app.NavUtils.navigateUpFromSameTask
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat.format
import android.view.*
import android.widget.Button
import android.widget.TextView
import de.bentolor.ahamanager.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.UI
import java.util.*

class AhaFragmentAnko : Fragment() {

    private var mAha: Aha = Aha()
    private var mDateButton: Button? = null
    private var mTimeButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ahaID = arguments.getSerializable(EXTRA_AHA_ID) as UUID

        mAha = AhaLab[activity].getAha(ahaID) ?: Aha()

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (getParentActivityName(activity) != null) {
            activity.actionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        return UI {
            verticalLayout {
                lparams { width = matchParent; height = matchParent }

                applyRecursively { it.lparams { width = matchParent; height = wrapContent } }

                textView {
                    textResource = R.string.aha_title_label
                    // Anko: no style support: style="?android:listSeparatorTextViewStyle"
                }

                editText {
                    setText(mAha.title, TextView.BufferType.EDITABLE)
                    hintResource = R.string.aha_title_hint
                    textChangedListener {
                        object : TextWatcher {
                            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {
                                mAha.title = c.toString()
                            }

                            override fun beforeTextChanged(c: CharSequence, start: Int, count: Int, after: Int) {
                            }

                            override fun afterTextChanged(c: Editable) {
                            }
                        }
                    }
                }
                textView {
                    textResource = R.string.aha_details_label
                }

                mDateButton = button {
                    onClick {
                        val dialog = DatePickerFragment.newInstance(mAha.date)
                        dialog.setTargetFragment(this@AhaFragmentAnko, REQUEST_DATE)
                        dialog.show(activity.supportFragmentManager, DIALOG_DATE)
                    }
                }.lparams {
                    width = matchParent; height = wrapContent
                    leftMargin = dip(16); rightMargin = dip(16)
                }

                mTimeButton = button {
                    onClick {
                        val dialog = TimePickerFragment.newInstance(mAha.date)
                        dialog.setTargetFragment(this@AhaFragmentAnko, REQUEST_TIME)
                        dialog.show(activity.supportFragmentManager, DIALOG_TIME)
                    }
                }.lparams {
                    width = matchParent; height = wrapContent
                    leftMargin = dip(16); rightMargin = dip(16)
                }

                checkBox {
                    lparams { leftMargin = dip(16); rightMargin = dip(16) }
                    isChecked = mAha.isUseful
                    onCheckedChange { buttonView, isChecked -> mAha.isUseful = isChecked }
                }
            }

            updateDate()
        }.view

    }

    private fun updateDate() {
        mDateButton?.text = format("EEEE, MMM dd, yyyy", mAha.date)
        mTimeButton?.text = format("h:mm a", mAha.date)
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
