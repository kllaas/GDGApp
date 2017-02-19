package com.alexey_klimchuk.gdgapp.activities.edit_note;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapters.PreviewEditImageAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;
import com.alexey_klimchuk.gdgapp.utils.schedulers.BaseSchedulerProvider;

import java.util.ArrayList;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Alexey on 24.09.2016.
 */

public class EditNotePresenter implements EditNoteRelations.Presenter {

    private EditNoteRelations.View mView;

    private NotesRepository mNotesRepository;

    private String noteId;

    private Note mNote;

    private PreviewEditImageAdapter mPreviewAdapter;

    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeSubscription mSubscriptions;

    public EditNotePresenter(EditNoteActivity activity, BaseSchedulerProvider schedulerProvider) {
        mView = activity;
        CacheUtils.tempBitmaps.clear();
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(activity, schedulerProvider),
                NotesRemoteDataSource.getInstance());

        mSchedulerProvider = schedulerProvider;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void updateNote(final Note note, final Bitmap image) {
        mView.setLoadingIndicator(true);

        note.setLocalImage(mNote.getLocalImage());
        note.setImage(mNote.getImage());
        note.setId(noteId);

        mNotesRepository.editNote(note, CacheUtils.tempBitmaps.getFullSizeImages());

        CacheUtils.tempBitmaps.clear();
        mView.setLoadingIndicator(false);
        mView.saveResult();
    }

    @Override
    public void loadNote(String id) {
        noteId = id;
        mView.setLoadingIndicator(true);

        mSubscriptions.clear();
        Subscription subscription = mNotesRepository
                .getNote(id)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::onLoadingSuccess,
                        // onError
                        throwable -> onLoadingFailure(),
                        // onCompleted
                        () -> mView.setLoadingIndicator(false));

        mSubscriptions.add(subscription);
    }

    private void onLoadingFailure() {
        ToastUtils.showMessage(R.string.message_loading_failed, mView.getActivity());
    }

    private void onLoadingSuccess(Note note) {
        mNote = note;
        mView.updateViews(note);
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

        mPreviewAdapter = new PreviewEditImageAdapter();
        return mPreviewAdapter;
    }

}
