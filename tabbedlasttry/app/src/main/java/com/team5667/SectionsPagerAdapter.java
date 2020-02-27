package com.team5667;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public  final tab[] tabs;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm,tab[] tabs) {
        super(fm);
        this.tabs=tabs;
        mContext = context;
    }
    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return tabs[position];
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position].tabText;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}