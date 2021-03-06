package de.bentolor.ahamanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

public class AhaPagerActivity extends FragmentActivity {

    private ArrayList<Aha> mAhas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewPager mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mAhas = AhaLab.get(this).getAhas();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return mAhas.size();
            }

            @Override
            public Fragment getItem(int p) {
                Aha aha = mAhas.get(p);
                return AhaFragment.newInstance(aha.getId());
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                Aha aha = mAhas.get(pos);
                if (aha.getTitle() != null) {
                    setTitle(aha.getTitle());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        UUID ahaId = (UUID) getIntent()
                .getSerializableExtra(AhaFragment.EXTRA_AHA_ID);

        for (int i = 0; i < mAhas.size(); i++) {
            if (mAhas.get(i).getId().equals(ahaId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}