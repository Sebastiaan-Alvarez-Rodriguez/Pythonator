package com.python.pythonator.ui.main.view;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.python.pythonator.ui.pick.PickFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String[] TAB_TITLES = new String[]{"Add", "Queue"};

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            default:
                return new PickFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        return 2;
    }
}