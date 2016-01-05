package com.example.rho_eojin1.nearestfriend;

/**
 * Created by shinjaemin on 2016. 1. 5..
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context currContext;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context cContext) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.currContext = cContext;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
            case 1:
                TabFragment3 tab3 = new TabFragment3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}