package com.alexey_klimchuk.gdgapp.edit_note;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.PreviewImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Alexey on 24.09.2016.
 */

public class EditNotePresenter implements EditNoteRelations.Presenter {

    private EditNoteRelations.View mView;

    private boolean oneWaySaved = false;

    private NotesRepository mNotesRepository;
    private HashSet<Bitmap> bitmaps = new HashSet<>();

    private String noteId;
    private Note mNote;
    private PreviewImageAdapter mPreviewAdapter;

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
        mNotesRepository.editNote(note, bitmaps, new NotesDataSource.SaveNoteCallback() {
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
                for (String path : note.getLocalImage()) {
                    File file = new File(path);
                    if (file.exists())
                        bitmaps.add(BitmapFactory.decodeFile(file.getAbsolutePath()));
                }
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
        bitmaps.add(bitmap);
    }

    @Override
    public HashSet<Bitmap> getBitmaps() {
        return bitmaps;
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
        mPreviewAdapter = new PreviewImageAdapter(mView.getActivity(), new ArrayList<String>(Arrays.asList(images)));
        return mPreviewAdapter;
    }

    private class ImageResizer extends AsyncTask<Void, Void, Bitmap> {

        private Uri uri;
        private ImageView imageView;
        private String imageId;

        ImageResizer(Uri uri, ImageView imageView, String id) {
            this.uri = uri;
            this.imageView = imageView;
            imageId = id;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapUtils.resizeImage(mView.getActivity(), uri, 240);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            CacheUtils.addBitmapToMemoryCache(imageId, bitmap);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                AlphaAnimation animation = new AlphaAnimation(0f, 1);
                animation.setDuration(400);
                imageView.startAnimation(animation);
                imageView.setImageBitmap(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }
}
