package com.kshitij.android.demoapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;

import com.kshitij.android.demoapp.R;

/**
 * Created by kshitij.kumar on 01-06-2015.
 */
public class Licenses extends AppCompatActivity {
    private static final String TAG = Licenses.class.getSimpleName();
    WebView mWebView;

    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        // Set up action bar
        ActionBar aBar = getSupportActionBar();
        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setHomeButtonEnabled(true);
        aBar.setDisplayShowTitleEnabled(true);
        aBar.setTitle(getResources().getString(R.string.licenses));

        // Prepare content of license page
        String license = buildLicensePageData();

        mWebView = (WebView) findViewById(R.id.webViewLicense);
        mWebView.loadData(license, "text/html", "utf-8");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        if (mWebView != null) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume()");
        if (mWebView != null) {
            mWebView.onResume();
        }
        super.onResume();
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();

            default:

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Prepares a web page which contains list of licenses for each library.
     */
    public String buildLicensePageData() {

        String apacheSoftwareLicenseStatement = "<p>Licensed under the Apache License, Version 2.0 (the \"License\");"
                + " you may not use this file except in compliance with the License."
                + " You may obtain a copy of the License at <p>http://www.apache.org/licenses/LICENSE-2.0"
                + "<p>Unless required by applicable law or agreed to in writing, software distributed under the"
                + " License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,"
                + " either express or implied. See the License for the specific language"
                + " governing permissions and limitations under the License.";

        final StringBuilder licensesHtmlBuilder = new StringBuilder(1000);
        // Header of HTML page
        appendLicenseContainerStart(licensesHtmlBuilder);

        // Bullet point for Google Gson library
        appendLicenseBlockHeader(licensesHtmlBuilder, "Google Gson",
                "Copyright (C) 2008 Google Inc.");
        // License
        appendLicenseBlock(licensesHtmlBuilder, apacheSoftwareLicenseStatement);

        appendLicenseBlockHeader(licensesHtmlBuilder,
                "Android-ObservableScrollView",
                "Copyright 2014 Soichiro Kashima");
        appendLicenseBlock(licensesHtmlBuilder, apacheSoftwareLicenseStatement);

        appendLicenseBlockHeader(licensesHtmlBuilder, "NineOldAndroids",
                "Copyright 2012 Jake Wharton");
        appendLicenseBlock(licensesHtmlBuilder, apacheSoftwareLicenseStatement);

        appendLicenseBlockHeader(licensesHtmlBuilder, "Google I/O Android App",
                "Copyright 2014 Google Inc. All rights reserved");
        appendLicenseBlock(licensesHtmlBuilder, apacheSoftwareLicenseStatement);

        // End tags of HTML page
        appendLicenseContainerEnd(licensesHtmlBuilder);
        return licensesHtmlBuilder.toString();
    }

    /**
     * Prepares web page header, applies CSS formatting specific to license page.
     */
    private void appendLicenseContainerStart(
            final StringBuilder licensesHtmlBuilder) {
        licensesHtmlBuilder.append("<!DOCTYPE html><html><head>")
                .append("<style type=\"text/css\">")
                .append(getResources().getString(R.string.license_page_style))
                .append("</style>").append("</head><body>");
    }

    /**
     * Adds a list bullet point with title
     */

    private void appendLicenseBlockHeader(
            final StringBuilder licensesHtmlBuilder, String libraryTitle,
            String copyright) {
        licensesHtmlBuilder.append("<ul><li>").append(libraryTitle)
                .append("</li></ul>").append("<pre>").append(copyright)
                .append("<br/><br/>");
    }

    /**
     * Adds a licenses statement
     */

    private void appendLicenseBlock(final StringBuilder licensesHtmlBuilder,
                                    String licenseStatement) {
        licensesHtmlBuilder.append(licenseStatement).append("</pre>");
    }

    /**
     * Adds closing tags for HTML page
     */
    private void appendLicenseContainerEnd(
            final StringBuilder licensesHtmlBuilder) {
        licensesHtmlBuilder.append("</body></html>");
    }

}
