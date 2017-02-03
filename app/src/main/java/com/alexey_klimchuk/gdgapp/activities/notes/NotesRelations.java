package com.alexey_klimchuk.gdgapp.activities.notes;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 24.09.2016.
 */

public class NotesRelations {

    interface View {

        void refreshData(List<Note> Notes);

        void showProgressDialog();

        void hideProgressDialog();

        AppCompatActivity getActivity();

        void showEmptyListMessage(boolean visible);
    }

    interface Presenter {

        void loadNotes();

        RecyclerView.Adapter loadAdapter(List<Note> notes);

        void crateSearchDialog();

        void crateSettingsDialog();

        void searchByDate(Date date);
    }

}
