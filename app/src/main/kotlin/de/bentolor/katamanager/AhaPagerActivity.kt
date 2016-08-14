package de.bentolor.katamanager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager

import java.util.ArrayList
import java.util.UUID

import de.bentolor.ahamanager.R

class AhaPagerActivity : FragmentActivity() {
    private var mViewPager: ViewPager? = null
    private var mAhas: ArrayList<Aha>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewPager = ViewPager(this)
        mViewPager!!.id = R.id.viewPager
        setContentView(mViewPager)

        mAhas = AhaLab.get(this).ahas

        val fm = supportFragmentManager
        mViewPager!!.adapter = object : FragmentStatePagerAdapter(fm) {
            override fun getCount(): Int {
                return mAhas!!.size
            }

            override fun getItem(p: Int): Fragment {
                val aha = mAhas!![p]
                return AhaFragment.newInstance(aha.id)
            }
        }

        mViewPager!!.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(pos: Int) {
                val aha = mAhas!![pos]
                title = aha.title
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageScrollStateChanged(arg0: Int) {
            }
        })

        val crimeId = intent.getSerializableExtra(AhaFragment.EXTRA_AHA_ID) as UUID

        for (i in mAhas!!.indices) {
            if (mAhas!![i].id == crimeId) {
                mViewPager!!.currentItem = i
                break
            }
        }
    }
}
