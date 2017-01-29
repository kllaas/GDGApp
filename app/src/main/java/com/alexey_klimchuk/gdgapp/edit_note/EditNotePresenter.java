package com.alexey_klimchuk.gdgapp.edit_note;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.PreviewEditImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

import java.util.HashSet;

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

    public EditNotePresenter(EditNoteActivity activity) {
        mView = activity;
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity),
                NotesRemoteDataSource.getInstance(), mView.getActivity());
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
    public HashSet<Bitmap> getBitmaps() {
        return null;
    }

    @Override
    public ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mPreviewAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public RecyclerView.Adapter getImagePreviewAdapter(String[] images) {
        CacheUtils.tempBitmaps.createFromMem(BitmapUtils.getBitmapsFromURIs(images, mView.getActivity(), false));

        mPreviewAdapter = new PreviewEditImageAdapter(mView.getActivity());
        return mPreviewAdapter;
    }

}
