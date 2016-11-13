package com.alexey_klimchuk.gdgapp.create_note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.inputmethod.InputMethodManager;

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

    private NotesRepository mNotesRepository;

    public CreateNotePresenter(CreateNoteActivity activity) {
        mView = activity;
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity),
                NotesRemoteDataSource.getInstance(), mView.getActivity());
    }

    @Override
    public void saveNote(final Note note, final Bitmap image) {
        mView.showProgressDialog();
        note.setUnicalId();
        mNotesRepository.saveNote(note, image, new NotesDataSource.SaveNoteCallback() {
            @Override
            public void onNoteSaved() {
                mView.hideProgressDialog();
                Intent intent = new Intent(mView.getActivity(), NotesActivity.class);
                mView.getActivity().startActivity(intent);
                hideKeyBoard();
            }

            @Override
            public void onError() {
                mView.hideProgressDialog();
                mView.showMessage(R.string.message_loading_failed);
            }
        });
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) mView.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mView.getActivity().findViewById(android.R.id.content).getWindowToken(), 0);
    }

}
