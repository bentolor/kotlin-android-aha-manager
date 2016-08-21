package de.bentolor.katamanager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import de.bentolor.ahamanager.R
import java.util.*

class AhaPagerActivity : FragmentActivity() {

    private lateinit var mAhas: MutableList<Aha>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAhas = AhaLab[this].ahas

        with(ViewPager(this)) {
            id = R.id.viewPager
            setContentView(this)

            adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getCount(): Int = mAhas.size
                override fun getItem(p: Int): Fragment = AhaFragment.newInstance(mAhas[p].id)
            }

            setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(pos: Int) {
                    title = mAhas[pos].title
                }

                override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
                }

                override fun onPageScrollStateChanged(arg0: Int) {
                }
            })

            val ahaId = intent.getSerializableExtra(AhaFragment.EXTRA_AHA_ID) as UUID

            currentItem = mAhas.indexOfFirst { it.id == ahaId }
        }
    }
}