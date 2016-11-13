package com.alexey_klimchuk.gdgapp.notes;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.List;

/**
 * Created by Alexey on 24.09.2016.
 */

public class NotesRelations {

    interface View {

        void refreshData(List<Note> Notes);

        void showProgressDialog();

        void hideProgressDialog();

        Activity getActivity();
    }

    interface Presenter {

        void loadNotes();

        RecyclerView.Adapter loadAdapter(List<Note> notes);
    }

}
