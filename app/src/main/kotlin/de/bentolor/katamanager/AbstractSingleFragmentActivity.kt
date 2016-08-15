package de.bentolor.katamanager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import de.bentolor.ahamanager.R

abstract class AbstractSingleFragmentActivity : FragmentActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment == null) {
            fragment = createFragment()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit()
        }

    }

}
