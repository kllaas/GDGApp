package com.alexey_klimchuk.gdgapp.edit_note;

import android.app.Activity;
import android.graphics.Bitmap;

import com.alexey_klimchuk.gdgapp.data.Note;

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
    }

}
