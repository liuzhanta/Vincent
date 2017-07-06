package com.zterry.imagepicker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import static android.R.string.no;

/**
 * Desc:a Common FragmentPagerAdapter for view pager
 * Author: Terry
 * Date:2016-07-05
 */
public class CommonFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList;

    public CommonFragmentPagerAdapter(FragmentManager fm) {
        this(fm, null);
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<Fragment> mFragmentList) {
        super(fm);
        this.mFragmentList = mFragmentList;
        notifyDataSetChanged();
    }

    public void setFragments(List<Fragment> fragments) {
        this.mFragmentList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList == null ? null : mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return CommonFragmentPagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }
}
