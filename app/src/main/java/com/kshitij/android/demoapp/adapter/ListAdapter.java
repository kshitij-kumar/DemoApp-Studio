package com.kshitij.android.demoapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kshitij.android.demoapp.R;
import com.kshitij.android.demoapp.model.Post;

import java.util.ArrayList;

/**
 * Created by kshitij.kumar on 02-06-2015.
 */

/**
 * Adapter for content of each tab. Populates UI of ViewPagerTabListViewFragment.
 */
public class ListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Post> mPosts;

    public ListAdapter(Context context, ArrayList<Post> posts) {
        mInflater = LayoutInflater.from(context);
        mPosts = posts;
    }

    @Override
    public int getCount() {
        if (mPosts.size() > 0) {
            return mPosts.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        if (convertView == null) {

            // inflate the layout
            convertView = mInflater.inflate(R.layout.list_item, parent, false);

            // set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.mTitle = (TextView) convertView
                    .findViewById(R.id.tvHeadline);
            viewHolder.mMessage = (TextView) convertView
                    .findViewById(R.id.tvByline);

            // store the holder with the view.
            convertView.setTag(viewHolder);

        } else {
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        // Post item based on the position
        Post post = mPosts.get(position);

        // assign values if the object is not null
        if (post != null) {
            // get the TextView(s) from the ViewHolder and then set the text
            viewHolder.mTitle.setText(post.getTitle());
            viewHolder.mMessage.setText(post.getBody());
        }

        return convertView;
    }

    static class ViewHolderItem {
        TextView mTitle;
        TextView mMessage;
    }
}