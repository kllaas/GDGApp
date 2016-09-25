package com.alexey_klimchuk.gdgapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.Details;
import com.alexey_klimchuk.gdgapp.models.Note;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in MainActivity.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final String TAG = "mRecyclerAdapter";
    private ArrayList<Note> mNotes;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mRootView;

        public ViewHolder(View v) {
            super(v);
            mRootView = v;
        }
    }

    public RecyclerAdapter(ArrayList<Note> notes, Context context) {
        mNotes = notes;
        mContext = context;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // Create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diary_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Set element from mNotes at this position of RecyclerView
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// start Details activity after clicking on card with content of selected note
                Intent intent = new Intent(mContext, Details.class);
                intent.putExtra("id", mNotes.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        // Set up mood state
        GradientDrawable gd = new GradientDrawable();
        if (mNotes.get(position).getMood() == Note.Mood.GOOD)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        if (mNotes.get(position).getMood() == Note.Mood.NORMAL)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorNormal));
        if (mNotes.get(position).getMood() == Note.Mood.BAD)
            gd.setColor(ContextCompat.getColor(mContext, R.color.colorBad));

        gd.setShape(GradientDrawable.OVAL);
        View view = holder.mRootView.findViewById(R.id.mood_icon);
        view.setBackground(gd);

        // Set up image if exists
        if (mNotes.get(position).getImage() != null) {
            try {
                ImageView imageView = (ImageView) holder.mRootView.findViewById(R.id.image_view_item);
                File imgFile = new File(mNotes.get(position).getImage());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                Log.d(TAG, "error image setting: " + e.getMessage());
            }

        }

        ((TextView) holder.mRootView.findViewById(R.id.text_view_date)).
                setText(mNotes.get(position).getDate());

        ((TextView) holder.mRootView.findViewById(R.id.text_view_content)).
                setText(mNotes.get(position).getContent());

        ((TextView) holder.mRootView.findViewById(R.id.text_view_name)).
                setText(mNotes.get(position).getName());

    }

    /**
     * Return the counts of notes
     */
    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}