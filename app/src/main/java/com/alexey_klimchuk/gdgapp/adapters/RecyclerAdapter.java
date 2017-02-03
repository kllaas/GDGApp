package com.alexey_klimchuk.gdgapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.detail_note.DetailNoteActivity;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.DateUtils;
import com.alexey_klimchuk.gdgapp.utils.ImageResizer;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 22.03.2016.
 * Adapter for RecyclerView in NotesActivity.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private static final String TAG = "mRecyclerAdapter";
    private List<Note> mNotes;
    private Context mContext;
    private NotesRepository mRepository;

    public RecyclerAdapter(List<Note> notes, Context context, NotesRepository repository) {
        mNotes = notes;
        mContext = context;
        mRepository = repository;
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

        return new ViewHolder(v);
    }

    /**
     * Set element from mNotes at this position of RecyclerView
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int p) {
        int position = holder.getAdapterPosition();

        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {// start Details activity after clicking on card with content of selected note
                Intent intent = new Intent(mContext, DetailNoteActivity.class);
                intent.putExtra(Constants.EXTRA_NOTE_ID, mNotes.get(holder.getAdapterPosition()).getId());
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

        // Set up image part
        final ImageView imageView = (ImageView) holder.mRootView.findViewById(R.id.image_view_item);
        setImage(position, imageView);

        ((TextView) holder.mRootView.findViewById(R.id.text_view_date)).
                setText(DateUtils.convertDateToString(new Date(mNotes.get(position).getDate())));

        ((TextView) holder.mRootView.findViewById(R.id.text_view_content)).
                setText(mNotes.get(position).getContent());

        ((TextView) holder.mRootView.findViewById(R.id.text_view_name))
                .setText(mNotes.get(position).getName());
    }


    private void setImage(final int position, final ImageView imageView) {
        try {
            if (mNotes.get(position).getLocalImage().size() != 0) {
                if (!mNotes.get(position).getLocalImage().get(0).equals("")) {
                    if ((CacheUtils.getBitmapFromMemCache(mNotes.get(position).getId()) == null)) {

                        imageView.setImageBitmap(null);
                        File imgFile = new File(mNotes.get(position).getLocalImage().get(0));//get first image

                        if (imgFile.exists()) {
                            new ImageResizer(mContext, Uri.fromFile((imgFile)), 240, imageView, mNotes.get(position).getId(), true).execute();
                        }

                    } else {
                        imageView.setImageBitmap(CacheUtils.getBitmapFromMemCache(mNotes.get(position).getId()));
                    }
                } else {
                    setDefaultImage(imageView);
                }
            } else {
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            Log.d(TAG, "error image setting: " + e.getMessage());
        }
    }

    private void setDefaultImage(ImageView imageView) {
        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.background_card));
    }

    /**
     * Return the counts of notes
     */
    @Override
    public int getItemCount() {
        return mNotes.size();
    }

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
}