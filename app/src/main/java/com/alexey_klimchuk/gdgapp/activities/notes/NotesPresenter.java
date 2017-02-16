package com.alexey_klimchuk.gdgapp.activities.notes;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.adapters.RecyclerAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.DateUtils;
import com.alexey_klimchuk.gdgapp.utils.schedulers.BaseSchedulerProvider;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Alexey on 24.09.2016.
 */

public class NotesPresenter implements NotesRelations.Presenter {

    private final BaseSchedulerProvider mSchedulerProvider;
    private NotesRelations.View mView;
    private NotesRepository mNotesRepository;
    private CompositeSubscription mSubscriptions;

    public NotesPresenter(NotesRelations.View view, BaseSchedulerProvider schedulerProvider) {
        mView = view;
        mNotesRepository = NotesRepository.getInstance(NotesRemoteDataSource.getInstance(),
                NotesLocalDataSource.getInstance(mView.getActivity(), schedulerProvider));
        mSchedulerProvider = schedulerProvider;

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadNotes() {
        mView.showProgressDialog();

        mSubscriptions.clear();
        Subscription subscription = mNotesRepository
                .getNotes()
                .flatMap(new Func1<List<Note>, Observable<Note>>() {
                    @Override
                    public Observable<Note> call(List<Note> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::processNotes,
                        // onError
                        throwable -> mView.showEmptyListMessage(true),
                        // onCompleted
                        () -> mView.hideProgressDialog());
        mSubscriptions.add(subscription);
    }

    private void processNotes(List<Note> notes) {
        if (notes.isEmpty()) {
            mView.showEmptyListMessage(true);
        } else {
            mView.showEmptyListMessage(false);
            mView.refreshData(notes);
        }
    }

    @Override
    public RecyclerView.Adapter loadAdapter(List<Note> notes) {
        return new RecyclerAdapter(notes, mView.getActivity(), mNotesRepository);
    }

    @Override
    public void crateSearchDialog() {
        DialogFragment dialog = new SearchDialogFragment();
        dialog.show(mView.getActivity().getSupportFragmentManager(), "SearchDialogFragment");
    }

    @Override
    public void crateSettingsDialog() {/*
        SettingsDialogFragment dialog = new SettingsDialogFragment();
        dialog.show(mView.getActivity().getSupportFragmentManager(), "SettingsDialogFragment");*/

        AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivity());
        builder.setTitle("Sorry!")
                .setMessage("This function is in development ;)")
                .setNegativeButton("Ok",
                        (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void searchByDate(Date date) {
        mView.showProgressDialog();

        mSubscriptions.clear();
        Subscription subscription = mNotesRepository
                .getNotes()
                .flatMap(new Func1<List<Note>, Observable<Note>>() {
                    @Override
                    public Observable<Note> call(List<Note> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .filter(note -> isNoteBelongsDate(date, note))
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        // onNext
                        this::processNotes,
                        // onError
                        throwable -> mView.showEmptyListMessage(true),
                        // onCompleted
                        () -> mView.hideProgressDialog());
        mSubscriptions.add(subscription);
    }

    private boolean isNoteBelongsDate(Date searchDate, Note note) {
        return note.getDate() > DateUtils.getStartDayDate(searchDate) &&
                note.getDate() < DateUtils.getEndDayDate(searchDate);
    }
}
