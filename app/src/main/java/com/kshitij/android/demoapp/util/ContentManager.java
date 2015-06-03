package com.kshitij.android.demoapp.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.kshitij.android.demoapp.model.Post;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kshitij.kumar on 01-06-2015.
 */

/**
 * Helper that handles the parsing and loading of data.
 */

public class ContentManager {
    private static final String TAG = ContentManager.class.getSimpleName();
    private static ContentManager mContentManager;
    private List<Post> mContent;

    public synchronized static ContentManager getInstance() {
        if (mContentManager == null) {
            mContentManager = new ContentManager();
        }
        return mContentManager;
    }

    /**
     * Parses downloaded data and populates it into ArrayList of Post object.
     *
     * @param data Raw data in String format.
     * @return List of Post object if parsing is successful, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public List<Post> parseJsonData(String data) {
        Log.d(TAG, "parseJsonData()");
        List<Post> posts = null;
        Type listType = new TypeToken<List<Post>>() {
        }.getType();

        if (!Utility.isNullOrEmpty(data)) {
            Gson gson = new Gson();
            try {
                posts = (List<Post>) gson.fromJson(data, listType);
            } catch (JsonSyntaxException e) {
                Log.e(TAG, "parseJsonData(), Exception: " + e.toString());
            }
        }
        return posts;
    }

    public List<Post> getContent() {
        return mContent;
    }

    public void setContent(List<Post> content) {
        this.mContent = content;
    }

    /**
     * Returns titles for tab. Titles corresponds to userId field in data.
     *
     * @param count Count of titles.
     * @return List of titles or null if mContent is null.
     */

    public List<Integer> getTabTitles(int count) {
        Log.d(TAG, "getTabTitles()");
        ArrayList<Integer> tabTitles = new ArrayList<>();
        if (mContent != null) {
            // Gather all userIds
            for (Post post : mContent) {
                tabTitles.add(post.getUserId());
            }
            // Make set of all unique userIds maintaining the order in which it
            // appear in feed
            Set<Integer> titles = new LinkedHashSet<>();
            titles.addAll(tabTitles);
            tabTitles.clear();
            tabTitles.addAll(titles);
            // Return only required number of tab titles
            return tabTitles.subList(0, Math.min(tabTitles.size(), count));
        }

        return tabTitles;
    }

    /**
     * Returns posts corresponding to a tab.
     *
     * @param tabId Id of the Tab which corresponds to userId field in data.
     * @return ArrayList of Post object or null if mContent is null.
     */

    public ArrayList<Post> getPostsForTab(int tabId) {
        Log.d(TAG, "getPostsForTab()");
        ArrayList<Post> posts = new ArrayList<>();
        if (mContent != null) {
            for (Post post : mContent) {
                if (post.getUserId() == tabId) {
                    posts.add(post);
                }
            }
            return posts;
        }

        return posts;
    }
}
