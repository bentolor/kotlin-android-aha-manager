package de.bentolor.katamanager

import android.support.v4.app.Fragment

class AhaListActivity : AbstractSingleFragmentActivity() {

    override fun createFragment(): Fragment = AhaListFragment()

}
