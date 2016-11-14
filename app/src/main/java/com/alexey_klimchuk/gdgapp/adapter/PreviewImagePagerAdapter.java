package com.alexey_klimchuk.gdgapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexey_klimchuk.gdgapp.R;

import java.util.List;


/**
 * Created by Alexey on 13.11.2016.
 */

public class PreviewImagePagerAdapter extends PagerAdapter {

    private List<String> images;
    private Context mContext;

    public PreviewImagePagerAdapter(Context context, List<String> pages) {
        this.images = pages;
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.pager_item, collection, false);
        /*ImageView imageView = (ImageView) v.findViewById(R.id.image_view);
        File imgFile = new File(mNotes.get(position).getLocalImage());
        if (imgFile.exists()) {
            new ImageResizer(Uri.fromFile((imgFile)), imageView, position, mNotes, mContext).execute();
        }*/
        collection.addView(v);
        return v;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}