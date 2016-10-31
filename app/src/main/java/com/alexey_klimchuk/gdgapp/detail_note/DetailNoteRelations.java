package com.alexey_klimchuk.gdgapp.detail_note;

import android.app.Activity;

import com.alexey_klimchuk.gdgapp.data.Note;

/**
 * Created by Alexey on 24.09.2016.
 */

public class DetailNoteRelations {

    interface View {

        void showProgressDialog();

        void hideProgressDialog();

        void updateViews(Note note);

        Activity getActivity();

    }

    interface Presenter {

        void loadNote(String id);

        void deleteNote();

    }

}
