package de.bentolor.ahamanager;

import android.support.v4.app.Fragment;

public class AhaListActivity extends AbstractSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AhaListFragment();
    }

}
