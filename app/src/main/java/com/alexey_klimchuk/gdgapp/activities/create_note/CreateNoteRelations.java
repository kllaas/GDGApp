package com.alexey_klimchuk.gdgapp.activities.create_note;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.data.Note;

/**
 * Created by Alexey on 24.09.2016.
 */

public class CreateNoteRelations {

    interface View {

        void setLoadingIndicator(boolean active);

        Activity getActivity();
    }

    interface Presenter {

        void saveNote(Note note);

        void addImage(Bitmap bitmap);

        RecyclerView.Adapter getImagePreviewAdapter();
    }
}
