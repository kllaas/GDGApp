package com.alexey_klimchuk.gdgapp.create_note;

import android.content.Intent;
import android.graphics.Bitmap;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.notes.NotesActivity;

/**
 * Created by Alexey on 24.09.2016.
 */

public class CreateNotePresenter implements CreateNoteRelations.Presenter {

    private CreateNoteRelations.View mView;

    private boolean oneWaySaved = false;

    private NotesRepository mNotesRepository;

    public CreateNotePresenter(CreateNoteActivity activity) {
        mView = activity;
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity),
                NotesRemoteDataSource.getInstance());
    }

    @Override
    public void saveNote(final Note note, final Bitmap image) {
        mView.showProgressDialog();
        note.setUnicalId();
        mNotesRepository.saveNote(note, new NotesDataSource.SaveNoteCallback() {
            @Override
            public void onNoteSaved() {
                if (oneWaySaved) {
                    mView.hideProgressDialog();
                    Intent intent = new Intent(mView.getActivity(), NotesActivity.class);
                    mView.getActivity().startActivity(intent);
                } else {
                    oneWaySaved = true;
                }
            }

            @Override
            public void onError() {
                mView.hideProgressDialog();
                mView.showMessage(R.string.message_loading_failed);
            }
        });
    }

}
