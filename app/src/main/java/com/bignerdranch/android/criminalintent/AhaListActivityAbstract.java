package com.bignerdranch.android.criminalintent;

import android.support.v4.app.Fragment;

public class AhaListActivityAbstract extends AbstractSingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new AhaListFragment();
    }

}
