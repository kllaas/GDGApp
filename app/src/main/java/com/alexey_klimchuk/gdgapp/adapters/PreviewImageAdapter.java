package com.alexey_klimchuk.gdgapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in NotesActivity.
 */
public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.ViewHolder> {

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public PreviewImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        int position = holder.getAdapterPosition();

        holder.imageView.setImageBitmap(CacheUtils.tempBitmaps.getSmallImages().get(position));
    }

    /**
     * Return the counts of items
     */
    @Override
    public int getItemCount() {
        return CacheUtils.tempBitmaps.getSmallImages().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}