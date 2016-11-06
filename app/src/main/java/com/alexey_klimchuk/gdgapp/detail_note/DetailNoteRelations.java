package com.alexey_klimchuk.gdgapp.detail_note;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.alexey_klimchuk.gdgapp.data.Note;

/**
 * Created by Alexey on 24.09.2016.
 */

public class DetailNoteRelations {

    interface View {

        void showEditTask(@NonNull String taskId);

        void showProgressDialog();

        void hideProgressDialog();

        void updateViews(Note note);

        Activity getActivity();

    }

    interface Presenter {

        void loadNote(String id);

        void deleteNote();

        DialogInterface.OnClickListener getDeleteOnClick();
    }

}
