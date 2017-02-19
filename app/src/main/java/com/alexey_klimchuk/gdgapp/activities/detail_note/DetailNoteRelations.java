package com.alexey_klimchuk.gdgapp.activities.detail_note;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.ArrayList;

/**
 * Created by Alexey on 24.09.2016.
 */

public class DetailNoteRelations {

    interface View {

        void showEditTask(@NonNull String taskId);

        void updateViews(Note note);

        void setLoadingIndicator(boolean active);

        Activity getActivity();

    }

    interface Presenter {

        void loadNote(String id);

        void deleteNote();

        DialogInterface.OnClickListener getDeleteOnClick();

        RecyclerView.Adapter getPreviewAdapter(ArrayList<String> localImage);
    }

}
