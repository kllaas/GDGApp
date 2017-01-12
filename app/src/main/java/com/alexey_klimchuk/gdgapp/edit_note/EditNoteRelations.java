package com.alexey_klimchuk.gdgapp.edit_note;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.HashSet;

/**
 * Created by Alexey on 24.09.2016.
 */

public class EditNoteRelations {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        void showMessage(int message);

        void showMessage(String message);

        void updateViews(Note note);

        Activity getActivity();

        void saveResult();
    }

    interface Presenter {

        void updateNote(Note note, Bitmap image);

        void loadNote(String id);

        void addImage(Bitmap bitmap);

        HashSet<Bitmap> getBitmaps();

        ItemTouchHelper getItemTouchHelper();

        RecyclerView.Adapter getImagePreviewAdapter(String[] images);
    }

}
