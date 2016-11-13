package com.alexey_klimchuk.gdgapp.notes;

import android.support.v7.widget.RecyclerView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.RecyclerAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.data.source.local.NotesLocalDataSource;
import com.alexey_klimchuk.gdgapp.data.source.remote.NotesRemoteDataSource;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;

import java.util.List;

/**
 * Created by Alexey on 24.09.2016.
 */

public class NotesPresenter implements NotesRelations.Presenter {

    private NotesRelations.View mView;

    private NotesRepository mNotesRepository;

    public NotesPresenter(NotesRelations.View view) {
        mView = view;
        mNotesRepository = NotesRepository.getInstance(NotesRemoteDataSource.getInstance(),
                NotesLocalDataSource.getInstance(mView.getActivity()), mView.getActivity());
    }

    @Override
    public void loadNotes() {
        mView.showProgressDialog();

        mNotesRepository.getNotes(new NotesDataSource.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                mView.refreshData(notes);
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
    public RecyclerView.Adapter loadAdapter(List<Note> notes) {
        return new RecyclerAdapter(notes, mView.getActivity(), mNotesRepository);
    }
}
