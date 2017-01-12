package com.alexey_klimchuk.gdgapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.ImageResizer;

import java.io.File;
import java.util.List;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in NotesActivity.
 */
public class PreviewImageAdapter extends RecyclerView.Adapter<PreviewImageAdapter.ViewHolder>
        implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private List<String> images;
    private Context mContext;

    public PreviewImageAdapter(Context context, List<String> images) {
        mContext = context;
        this.images = images;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public PreviewImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // Create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_pager_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int p) {
        int position = holder.getAdapterPosition();

        File imgFile = new File(images.get(position));
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemDismiss(p);
            }
        });

        if (imgFile.exists()) {
            new ImageResizer(mContext, Uri.fromFile((imgFile)), 128, holder.imageView, null, false).execute();
        }
    }

    public void removeItem(int position) {
        images.remove(position);
        this.notifyItemRemoved(position);
    }

    /**
     * Return the counts of items
     */
    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {
        images.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, images.size());
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
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