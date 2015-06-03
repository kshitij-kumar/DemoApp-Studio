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
package com.kshitij.android.demoapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.kshitij.android.demoapp.R;
import com.kshitij.android.demoapp.adapter.ListAdapter;
import com.kshitij.android.demoapp.util.ContentManager;

/**
 * Modified by kshitij.kumar on 01-06-2015.
 */
public class ViewPagerTabListViewFragment extends Fragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private static final String TAG = ViewPagerTabListViewFragment.class
            .getSimpleName();
    private int mTabId;

    public void setTabId(int tabId) {
        this.mTabId = tabId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_listview, container,
                false);

        Activity parentActivity = getActivity();
        final ObservableListView listView = (ObservableListView) view
                .findViewById(R.id.scroll);
        listView.addHeaderView(inflater.inflate(R.layout.padding, listView,
                false));
        // Set adapter of list
        listView.setAdapter(new ListAdapter(getActivity(), ContentManager
                .getInstance().getPostsForTab(mTabId)));

        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args
                        .getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(listView, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        listView.setSelection(initialPosition);
                    }
                });
            }

            // TouchInterceptionViewGroup should be a parent view other than
            // ViewPager.
            listView.setTouchInterceptionViewGroup((ViewGroup) parentActivity
                    .findViewById(R.id.root));

            listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }
        return view;
    }
}
