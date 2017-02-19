package com.alexey_klimchuk.gdgapp.activities.settings_dialog;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.ArrayList;

/**
 * Created by alexey on 02/02/17.
 */

public class SettingsPresenter implements SettignsRelations.Presenter {

    private static final String TAG = "SettingsPres";
    //    private final NotesRepository mNotesRepository;
    private SettignsRelations.View mView;

    public SettingsPresenter(SettignsRelations.View mView) {
        this.mView = mView;
/*
        mNotesRepository = NotesRepository.getInstance(NotesRemoteDataSource.getInstance(),
                NotesLocalDataSource.getInstance(mView.getActivity()));*/
    }

    @Override
    public void loadToServer() {
       /* mView.showProgressDialog();

        mNotesRepository.getNotes(new NotesDataSource.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                notes = removeRemoteReferences(new ArrayList<>(notes));
                mNotesRepository.saveNotesRemote(new ArrayList<>(notes), mView.getActivity(), new NotesDataSource.SaveNoteCallback() {
                    @Override
                    public void onNoteSaved() {

                        Log.d(TAG, "notes saved remote, size = ");

                        mView.hideProgressDialog();
                        ToastUtils.showMessage(R.string.message_notes_saved, mView.getActivity());
                        mView.onLoadingEnd();
                    }

                    @Override
                    public void onError() {
                        handleDataNotAvailable();
                    }
                });

            }

            @Override
            public void onDataNotAvailable() {
                handleDataNotAvailable();
            }
        });*/
    }

    private ArrayList<Note> removeRemoteReferences(ArrayList<Note> notes) {
        for (Note note : notes) {
            note.setImage(new ArrayList<String>());
        }

        return notes;
    }

    @Override
    public void loadFromServer() {
        /*mView.showProgressDialog();

        mNotesRepository.getNotesFromRemoteDataSource(new NotesDataSource.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                NotesRemoteDataSource.getInstance().loadImages(new ArrayList<>(notes), 0, new NotesDataSource.LoadImageCallback() {
                    @Override
                    public void onImagesLoaded(ArrayList<Note> notes, Bitmap bitmap) {
                        mNotesRepository.saveNotes(notes, new NotesDataSource.SaveNoteCallback() {
                            @Override
                            public void onNoteSaved() {
                                mView.onLoadingEnd();
                                mView.hideProgressDialog();
                            }

                            @Override
                            public void onError() {

                            }
                        });
                    }

                    @Override
                    public void onImageNotAvailable() {
                        handleDataNotAvailable();
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                handleDataNotAvailable();
            }
        });*/
    }

    private void handleDataNotAvailable() {/*
        ToastUtils.showMessage(R.string.message_loading_failed, mView.getActivity());
        mView.hideProgressDialog();*/
    }
}
