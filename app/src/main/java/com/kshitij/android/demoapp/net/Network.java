package com.kshitij.android.demoapp.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.kshitij.android.demoapp.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kshitij.kumar on 01-06-2015.
 */
public class Network {
    private static final String TAG = Network.class.getSimpleName();

    /**
     * Checks if device is connected to any network
     *
     * @param context Context in which App is running.
     * @return True if connected, false otherwise.
     */

    public static boolean isNetworkAvailable(Context context) {
        Log.d(TAG, "checking network connectivity");

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Connects to server using URL class. Downloads data as InputStream and converts into String representation.
     *
     * @param destUrl URL of the remote server.
     * @return String representation of downloaded data or null if failed.
     */

    public static String getResponseAsString(String destUrl) throws IOException {
        Log.d(TAG, "getResponseAsString(), Url = " + destUrl);
        InputStream is = null;

        try {
            URL url = new URL(destUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "getResponseAsString(), The response code is: "
                    + responseCode);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = Utility.convertToString(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
