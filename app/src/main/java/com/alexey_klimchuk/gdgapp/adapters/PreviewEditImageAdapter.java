package com.alexey_klimchuk.gdgapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in NotesActivity.
 */
public class PreviewEditImageAdapter extends RecyclerView.Adapter<PreviewEditImageAdapter.ViewHolder>
        implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context mContext;

    public PreviewEditImageAdapter(Context context) {
        mContext = context;
    }

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

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemDismiss(p);
            }
        });

        holder.imageView.setImageBitmap(CacheUtils.tempBitmaps.getSmallImages().get(position));
    }

    /**
     * Return the counts of items
     */
    @Override
    public int getItemCount() {
        return CacheUtils.tempBitmaps.getSmallImages().size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {
        CacheUtils.tempBitmaps.removeImage(position);

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, CacheUtils.tempBitmaps.getSmallImages().size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public Button buttonDelete;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.image_view);
            buttonDelete = (Button) v.findViewById(R.id.button_remove);
        }
    }

}