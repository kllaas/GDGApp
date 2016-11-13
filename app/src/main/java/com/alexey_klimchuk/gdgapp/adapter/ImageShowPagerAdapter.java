package com.alexey_klimchuk.gdgapp.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexey_klimchuk.gdgapp.R;
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter;
import com.alexvasilkov.gestures.views.GestureImageView;

import java.io.File;


/**
 * Created by Alexey on 13.11.2016.
 */

public class ImageShowPagerAdapter extends RecyclePagerAdapter<ImageShowPagerAdapter.MyViewHolder> {

    private final ViewPager viewPager;
    private String[] photos;

    public ImageShowPagerAdapter(ViewPager viewPager, String[] photos) {
        this.viewPager = viewPager;
        this.photos = photos;
    }

    public static GestureImageView getImage(RecyclePagerAdapter.ViewHolder holder) {
        return ((MyViewHolder) holder).image;
    }

    @Override
    public int getCount() {
        return photos.length;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup container) {
        View v = LayoutInflater.from(container.getContext())
                .inflate(R.layout.pager_item, container, false);
        MyViewHolder holder = new MyViewHolder(v);
        holder.image.getController().getSettings().setFillViewport(true).setMaxZoom(3f);
        holder.image.getController().enableScrollInViewPager(viewPager);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        File imgFile = new File(photos[position]);
        if (imgFile.exists()) {
            holder.image.setImageDrawable(Drawable.createFromPath(photos[position]));
        }

        holder.image.getController().getSettings().enableGestures();
    }

    public class MyViewHolder extends RecyclePagerAdapter.ViewHolder {
        public View mRootView;
        GestureImageView image;

        public MyViewHolder(View v) {
            super(v);
            mRootView = v;
            image = (GestureImageView) v.findViewById(R.id.image_view);
        }
    }

}