package com.alexey_klimchuk.gdgapp.detail_note;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

/**
 * Detaild by Alexey on 24.09.2016.
 */

public class DetailNotePresenter implements DetailNoteRelations.Presenter {

    private DetailNoteRelations.View mView;

    private NotesRepository mNotesRepository;
    private String noteId;

    public DetailNotePresenter(DetailNoteRelations.View view) {
        mView = view;
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(mView.getActivity()),
                NotesRemoteDataSource.getInstance());
    }

    @Override
    public void loadNote(String id) {
        noteId = id;
        mView.showProgressDialog();

        mNotesRepository.getNote(noteId, new NotesDataSource.GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                mView.updateViews(note);
                mView.hideProgressDialog();
            }

            @Override
            public void onDataNotAvailable() {
                mView.hideProgressDialog();
                ToastUtils.showMessage(R.string.message_loading_failed, mView.getActivity());
            }
        });
    }

    @Override
    public void deleteNote() {

    }
}
