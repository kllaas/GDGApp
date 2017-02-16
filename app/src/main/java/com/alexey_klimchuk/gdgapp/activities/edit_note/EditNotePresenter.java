package com.alexey_klimchuk.gdgapp.activities.edit_note;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapters.PreviewEditImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;
import com.alexey_klimchuk.gdgapp.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;

/**
 * Created by Alexey on 24.09.2016.
 */

public class EditNotePresenter implements EditNoteRelations.Presenter {

    private EditNoteRelations.View mView;

    private boolean oneWaySaved = false;

    private NotesRepository mNotesRepository;

    private String noteId;
    private Note mNote;
    private PreviewEditImageAdapter mPreviewAdapter;

    public EditNotePresenter(EditNoteActivity activity, BaseSchedulerProvider schedulerProvider) {
        mView = activity;
        CacheUtils.tempBitmaps.clear();
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity),
                NotesRemoteDataSource.getInstance());
    }

    @Override
    public void updateNote(final Note note, final Bitmap image) {
        mView.showProgressDialog();

        note.setLocalImage(mNote.getLocalImage());
        note.setImage(mNote.getImage());
        note.setId(noteId);

        mNotesRepository.editNote(note, CacheUtils.tempBitmaps.getFullSizeImages(), new NotesDataSource.SaveNoteCallback() {
            @Override
            public void onNoteSaved() {
                if (oneWaySaved) {
                    CacheUtils.tempBitmaps.clear();
                    mView.hideProgressDialog();
                    mView.saveResult();
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

    @Override
    public void loadNote(String id) {
        noteId = id;
        mView.showProgressDialog();

        mNotesRepository.getNote(noteId, new NotesDataSource.GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                mNote = note;
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
    public void addImage(Bitmap bitmap) {
        CacheUtils.tempBitmaps.addImage(bitmap);
        mPreviewAdapter.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.Adapter getImagePreviewAdapter(ArrayList<String> images) {
        if (images.size() != 0)
            if (!images.get(0).equals(""))
                CacheUtils.tempBitmaps.createFromMem(BitmapUtils.getBitmapsFromURIs(images, mView.getActivity(), false));

        mPreviewAdapter = new PreviewEditImageAdapter(mView.getActivity());
        return mPreviewAdapter;
    }

}
