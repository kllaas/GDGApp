package com.alexey_klimchuk.gdgapp.activities.detail_note;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.notes.NotesActivity;
import com.alexey_klimchuk.gdgapp.adapters.PreviewImageAdapter;
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
 * Detaild by Alexey on 24.09.2016.
 */

public class DetailNotePresenter implements DetailNoteRelations.Presenter {

    private DetailNoteRelations.View mView;

    private NotesRepository mNotesRepository;

    private BaseSchedulerProvider mSchedulerProvider;

    private CompositeSubscription mSubscriptions;

    private String noteId;

    public DetailNotePresenter(DetailNoteRelations.View view, BaseSchedulerProvider provider) {
        mView = view;
        CacheUtils.tempBitmaps.clear();
        mNotesRepository = NotesRepository.getInstance(NotesLocalDataSource.getInstance(mView.getActivity(), provider),
                NotesRemoteDataSource.getInstance());

        mSchedulerProvider = provider;

        mSubscriptions = new CompositeSubscription();
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

    private void onLoadingSuccess(Note note) {
        mView.updateViews(note);
    }

    private void onLoadingFailure() {
        ToastUtils.showMessage(R.string.message_loading_failed, mView.getActivity());
    }

    @Override
    public void deleteNote() {
        mView.setLoadingIndicator(true);
        mNotesRepository.deleteNote(noteId);

        Intent intent = new Intent(mView.getActivity(), NotesActivity.class);
        mView.getActivity().startActivity(intent);
        mView.setLoadingIndicator(false);
    }

    @Override
    public DialogInterface.OnClickListener getDeleteOnClick() {
        return (dialog, which) -> deleteNote();
    }

    @Override
    public RecyclerView.Adapter getPreviewAdapter(ArrayList<String> localImage) {
        if (localImage.size() != 0)
            if (!localImage.get(0).equals(""))
                CacheUtils.tempBitmaps.createFromMem(BitmapUtils.getBitmapsFromURIs(localImage, mView.getActivity(), false));

        return new PreviewImageAdapter();
    }
}
