package com.alexey_klimchuk.gdgapp.activities.detail_note;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.notes.NotesActivity;
import com.alexey_klimchuk.gdgapp.adapters.PreviewImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Detaild by Alexey on 24.09.2016.
 */

public class DetailNotePresenter implements DetailNoteRelations.Presenter {

    private DetailNoteRelations.View mView;

    private NotesRepository mNotesRepository;

    private String noteId;

    public DetailNotePresenter(DetailNoteRelations.View view) {
        mView = view;
        CacheUtils.tempBitmaps.clear();
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

    @Override
    public DialogInterface.OnClickListener getDeleteOnClick() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mView.showProgressDialog();
                mNotesRepository.deleteNote(noteId, new NotesDataSource.DeleteNoteCallback() {
                    @Override
                    public void onNoteDeleted() {
                        Intent intent = new Intent(mView.getActivity(), NotesActivity.class);
                        mView.getActivity().startActivity(intent);
                        mView.hideProgressDialog();
                    }

                    @Override
                    public void onError() {
                        mView.hideProgressDialog();
                    }
                });
            }
        };
    }

    @Override
    public RecyclerView.Adapter getPreviewAdapter(ArrayList<String> localImage) {
        if (localImage.size() != 0)
            if (!localImage.get(0).equals(""))
                CacheUtils.tempBitmaps.createFromMem(BitmapUtils.getBitmapsFromURIs(localImage, mView.getActivity(), false));

        return new PreviewImageAdapter();
    }
}
