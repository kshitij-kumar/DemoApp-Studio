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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.kshitij.android.demoapp.R;
import com.kshitij.android.demoapp.adapter.NavigationAdapter;
import com.kshitij.android.demoapp.model.Post;
import com.kshitij.android.demoapp.net.Network;
import com.kshitij.android.demoapp.util.ContentManager;
import com.kshitij.android.demoapp.util.Utility;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.IOException;
import java.util.List;

/**
 * Modified by kshitij.kumar on 01-06-2015.
 */

public class ViewPagerTabListViewActivity extends AppCompatActivity implements
        ObservableScrollViewCallbacks {
    public static final String TAG = ViewPagerTabListViewActivity.class
            .getSimpleName();
    // URL of the server for feed
    private static final String URL = "http://jsonplaceholder.typicode.com/posts";
    private ProgressDialog mProgressDialog;
    private View mHeaderView;
    private View mToolbarView;
    private int mBaseTranslationY;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private DataDownloaderTask mDataDownloaderTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Download data if network is available, abort otherwise
        if (Network.isNetworkAvailable(this)) {
            mDataDownloaderTask = new DataDownloaderTask();
            mDataDownloaderTask.execute(URL);
        } else {
            showErrorMessage();
        }
    }

    /**
     * Displays content of the app after data has been downloaded
     */

    private void showContentView() {
        setContentView(R.layout.activity_viewpagertab);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mHeaderView = findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView,
                getResources().getDimension(R.dimen.toolbar_elevation));
        mToolbarView = findViewById(R.id.toolbar);

        // Get titles of tabs
        List<Integer> tabTitles = ContentManager.getInstance().getTabTitles(5);
        if (tabTitles == null || tabTitles.size() <= 0) {
            showErrorMessage();
            return;
        }

        // Set adapter for tabs
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager(),
                tabTitles);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);

        // Set sliding layout

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator,
                android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(
                R.color.accent));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);

        // When the page is selected, other fragments' scrollY should be
        // adjusted
        // according to the toolbar status(shown/hidden)
        slidingTabLayout
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i2) {
                    }

                    @Override
                    public void onPageSelected(int i) {
                        propagateToolbarState(toolbarIsShown());
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {
                    }
                });

        propagateToolbarState(toolbarIsShown());
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            float currentHeaderTranslationY = ViewHelper
                    .getTranslationY(mHeaderView);
            if (firstScroll) {
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(
                    -(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }

        int toolbarHeight = mToolbarView.getHeight();
        final ObservableListView listView = (ObservableListView) view
                .findViewById(R.id.scroll);
        if (listView == null) {
            return;
        }
        int scrollY = listView.getCurrentScrollY();
        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar
            // should be adjusted
            if (toolbarIsShown() || toolbarIsHidden()) {
                // Toolbar is completely moved, so just keep its state
                // and propagate it to other pages
                propagateToolbarState(toolbarIsShown());
            } else {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu()");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_licenses) {
            startActivity(new Intent(this, Licenses.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Activity is destroying, cancel any background tasks which may process UI
        if (mDataDownloaderTask != null) {
            mDataDownloaderTask.cancel(true);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDataDownloaderTask != null) {
            mDataDownloaderTask.cancel(true);
        }
        super.onBackPressed();
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
    }

    private void propagateToolbarState(boolean isShown) {
        int toolbarHeight = mToolbarView.getHeight();

        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(isShown ? 0 : toolbarHeight);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            Fragment f = mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            ObservableListView listView = (ObservableListView) view
                    .findViewById(R.id.scroll);
            if (isShown) {
                // Scroll up
                if (0 < listView.getCurrentScrollY()) {
                    listView.setSelection(0);
                }
            } else {
                // Scroll down (to hide padding)
                if (listView.getCurrentScrollY() < toolbarHeight) {
                    listView.setSelection(1);
                }
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView
                .getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0)
                    .setDuration(200).start();
        }
        propagateToolbarState(true);
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView)
                    .translationY(-toolbarHeight).setDuration(200).start();
        }
        propagateToolbarState(false);
    }

    private void dissmissLoadingProgress() {
        Log.d(TAG, "dissmissLoadingProgress()");
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void showLoadingProgress() {
        Log.d(TAG, "showLoadingProgress()");
        dissmissLoadingProgress();
        mProgressDialog = ProgressDialog.show(this, "", "Loading...", true,
                false);
    }

    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage()");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connection_error))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mDataDownloaderTask != null) {
                            mDataDownloaderTask.cancel(true);
                        }
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * AsyncTask to download data off the UI thread
     *
     * @param URL of the remote server.
     */
    private class DataDownloaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "DataDownloaderTask, onPreExecute()");
            // Show progress indicator
            showLoadingProgress();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            // Download data
            try {
                return Network.getResponseAsString(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "DataDownloaderTask, doInBackground(), Exception: "
                        + e.toString());
                return getResources().getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "DataDownloaderTask, onPostExecute()");
            // Hide progress indicator
            dissmissLoadingProgress();
            List<Post> posts = null;
            ContentManager cm = ContentManager.getInstance();
            if (!Utility.isNullOrEmpty(result)) {
                if (result.equalsIgnoreCase(getResources().getString(
                        R.string.connection_error))) {
                    // Download unsuccessful, show error message
                    showErrorMessage();
                } else {
                    // Download successful, parse data
                    posts = cm.parseJsonData(result);
                }
            }
            if (posts != null && posts.size() > 0) {
                // Data has been downloaded, populate it in ContentManager
                cm.setContent(posts);
                // Populate UI of App
                showContentView();
            } else {
                // Data couldn't be downloaded or parsed, show error message
                showErrorMessage();
            }
        }
    }

}
