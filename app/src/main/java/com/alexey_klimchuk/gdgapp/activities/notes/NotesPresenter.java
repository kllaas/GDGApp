package com.alexey_klimchuk.gdgapp.activities.notes;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.adapters.RecyclerAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 24.09.2016.
 */

public class NotesPresenter implements NotesRelations.Presenter {

    private NotesRelations.View mView;

    private NotesRepository mNotesRepository;

    public NotesPresenter(NotesRelations.View view) {
        mView = view;
        mNotesRepository = NotesRepository.getInstance(NotesRemoteDataSource.getInstance(),
                NotesLocalDataSource.getInstance(mView.getActivity()));
    }

    @Override
    public void loadNotes() {
        mView.showProgressDialog();

        mNotesRepository.getNotes(new NotesDataSource.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                mView.refreshData(notes);
                mView.hideProgressDialog();
                mView.showEmptyListMessage(false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.hideProgressDialog();
                mView.refreshData(new ArrayList<Note>());
                mView.showEmptyListMessage(true);
            }
        });
    }

    @Override
    public RecyclerView.Adapter loadAdapter(List<Note> notes) {
        return new RecyclerAdapter(notes, mView.getActivity(), mNotesRepository);
    }

    @Override
    public void crateSearchDialog() {
        DialogFragment dialog = new SearchDialogFragment();
        dialog.show(mView.getActivity().getSupportFragmentManager(), "SearchDialogFragment");
    }

    @Override
    public void crateSettingsDialog() {/*
        SettingsDialogFragment dialog = new SettingsDialogFragment();
        dialog.show(mView.getActivity().getSupportFragmentManager(), "SettingsDialogFragment");*/

        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivity());
        builder.setTitle("Sorry!")
                .setMessage("This function is in development ;)")
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void searchByDate(Date date) {
        mView.showProgressDialog();
        mNotesRepository.getNotesByDate(date, new NotesDataSource.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                mView.refreshData(notes);
                mView.hideProgressDialog();
                mView.showEmptyListMessage(false);
            }

            @Override
            public void onDataNotAvailable() {
                mView.hideProgressDialog();
                mView.refreshData(new ArrayList<Note>());
                mView.showEmptyListMessage(true);
            }
        });
    }
}
