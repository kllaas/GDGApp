package com.alexey_klimchuk.gdgapp.activities.create_note;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.notes.NotesActivity;
import com.alexey_klimchuk.gdgapp.adapters.PreviewEditImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

import java.util.UUID;

/**
 * Created by Alexey on 24.09.2016.
 */

public class CreateNotePresenter implements CreateNoteRelations.Presenter {

    private CreateNoteRelations.View mView;

    private NotesRepository mNotesRepository;
    private PreviewEditImageAdapter mPreviewAdapter;

    private String noteId;

    public CreateNotePresenter(CreateNoteActivity activity) {
        mView = activity;
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity),
                NotesRemoteDataSource.getInstance(), mView.getActivity());
        noteId = UUID.randomUUID().toString();
    }

    @Override
    public void saveNote(final Note note) {
        mView.showProgressDialog();

        note.setId(noteId);

        mNotesRepository.saveNote(note, CacheUtils.tempBitmaps.getFullSizeImages(), new NotesDataSource.SaveNoteCallback() {
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

    @Override
    public void addImage(Bitmap bitmap) {
        CacheUtils.tempBitmaps.addImage(bitmap);
        mPreviewAdapter.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.Adapter getImagePreviewAdapter() {
        mPreviewAdapter = new PreviewEditImageAdapter(mView.getActivity());
        return mPreviewAdapter;
    }

}
