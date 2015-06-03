/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kshitij.android.demoapp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.kshitij.android.demoapp.ui.ViewPagerTabListViewFragment;

import java.util.List;

/**
 * Modified by kshitij.kumar on 02-06-2015.
 */

/**
 * Adapter for tabs, creates a ViewPagerTabListViewFragment for each tab.
 */
public class NavigationAdapter extends CacheFragmentStatePagerAdapter {

    private List<Integer> mTabTitles;

    private int mScrollY;

    public NavigationAdapter(FragmentManager fm, List<Integer> tabTitles) {
        super(fm);
        this.mTabTitles = tabTitles;
    }

    public void setScrollY(int scrollY) {
        mScrollY = scrollY;
    }

    @Override
    protected Fragment createItem(int position) {
        Fragment f = new ViewPagerTabListViewFragment();
        if (0 < mScrollY) {
            Bundle args = new Bundle();
            args.putInt(ViewPagerTabListViewFragment.ARG_INITIAL_POSITION, 1);
            f.setArguments(args);
        }
        // Set tab Id corresponding to fragment, needed for getting posts
        if (mTabTitles != null && mTabTitles.size() > 0) {
            ((ViewPagerTabListViewFragment) f).setTabId(mTabTitles
                    .get(position));
        }
        return f;
    }

    @Override
    public int getCount() {
        if (mTabTitles != null && mTabTitles.size() > 0) {
            return mTabTitles.size();
        } else {
            return 0;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTabTitles != null && mTabTitles.size() > 0) {
            return mTabTitles.get(position).toString();
        } else {
            return "";
        }

    }
}