package com.alexey_klimchuk.gdgapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in NotesActivity.
 */
public class PreviewEditImageAdapter extends RecyclerView.Adapter<PreviewEditImageAdapter.ViewHolder> {
    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public PreviewEditImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_pager_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        int position = holder.getAdapterPosition();

        holder.buttonDelete.setOnClickListener(view -> onItemDismiss(p));

        holder.imageView.setImageBitmap(CacheUtils.tempBitmaps.getSmallImages().get(position));
    }

    /**
     * Return the counts of items
     */
    @Override
    public int getItemCount() {
        return CacheUtils.tempBitmaps.getSmallImages().size();
    }

    void onItemDismiss(int position) {
        CacheUtils.tempBitmaps.removeImage(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, CacheUtils.tempBitmaps.getSmallImages().size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.button_remove)
        Button buttonDelete;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

}