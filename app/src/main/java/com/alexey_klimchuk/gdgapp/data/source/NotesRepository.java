/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alexey_klimchuk.gdgapp.data.source;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * Concrete implementation to load Notes from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class NotesRepository {

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    static Map<String, Note> mCachedNotes;
    private static NotesRepository INSTANCE = null;
    private final NotesDataSource mNotesRemoteDataSource;
    private final NotesDataSource mNotesLocalDataSource;
    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private NotesRepository(@NonNull NotesDataSource notesRemoteDataSource,
                            @NonNull NotesDataSource notesLocalDataSource) {
        mNotesRemoteDataSource = notesRemoteDataSource;
        mNotesLocalDataSource = notesLocalDataSource;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param notesRemoteDataSource the backend data source
     * @param notesLocalDataSource  the device storage data source
     * @return the {@link NotesRepository} instance
     */
    public static NotesRepository getInstance(NotesDataSource notesRemoteDataSource,
                                              NotesDataSource notesLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new NotesRepository(notesRemoteDataSource, notesLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(NotesDataSource, NotesDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    public static List<Note> getCachedNotesList() {
        if (mCachedNotes != null)
            return new ArrayList<>(mCachedNotes.values());
        else return new ArrayList<>();
    }

    public static Map<String, Note> getCachedNotesMap() {
        return mCachedNotes;
    }

    public Observable<List<Note>> getNotes() {
        if (mCachedNotes != null && !mCacheIsDirty) {
            return Observable.from(mCachedNotes.values()).toList();
        } else if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }

        Observable<List<Note>> localTasks = getAndCacheLocalNotes();

        return localTasks
                .filter(tasks -> !tasks.isEmpty())
                .first();
    }

    private Observable<List<Note>> getAndCacheLocalNotes() {
        return mNotesLocalDataSource.getNotes()
                .flatMap(new Func1<List<Note>, Observable<List<Note>>>() {
                    @Override
                    public Observable<List<Note>> call(List<Note> tasks) {
                        return Observable.from(tasks)
                                .doOnNext(task -> mCachedNotes.put(task.getId(), task))
                                .toList();
                    }
                });
    }

    public void saveNotes(ArrayList<Note> notes) {
        for (Note note : notes) {
            saveNote(note, null);
        }
    }

    public void saveNote(@NonNull final Note note, final ArrayList<Bitmap> images) {
        if (images != null) {
            ArrayList<String> localImages = new ArrayList<>();

            for (Bitmap bitmap : images) {
                try {
                    localImages.add(BitmapUtils.createImageFile(bitmap, true));
                } catch (Exception ignored) {
                }
            }

            note.setLocalImage(localImages);
        }

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }

        ArrayList<Note> notes = new ArrayList<Note>(mCachedNotes.values());
        notes.add(0, note);
        refreshCache(notes);

        mNotesLocalDataSource.saveNote(note);
    }

    public void editNote(@NonNull Note note, ArrayList<Bitmap> images) {
        //Delete old images
        for (int i = 0; i < note.getLocalImage().size(); i++) {
            if (note.getLocalImage().get(i) != null) {
                BitmapUtils.deleteImageFile(note.getLocalImage().get(i));
            }
        }

        //Add new images
        ArrayList<String> localImages = new ArrayList<>();
        if (images != null) {
            for (Bitmap bitmap : images) {
                try {
                    localImages.add(BitmapUtils.createImageFile(bitmap, true));

                } catch (Exception ignored) {
                }
            }
        }

        note.setLocalImage(localImages);

        mNotesLocalDataSource.editNote(note, images);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.put(note.getId(), note);
    }

    public Observable<Note> getNote(@NonNull final String noteId) {

        final Note cachedNote = getNoteWithId(noteId);

        // Respond immediately with cache if available
        if (cachedNote != null) {
            return Observable.just(cachedNote);
        }

        return mNotesLocalDataSource.getNote(noteId);
    }

    public void refreshNotes() {
        mCacheIsDirty = true;
    }

    public void deleteAllNotes() {
        mNotesLocalDataSource.deleteAllNotes();

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.clear();
    }

    public void deleteNote(@NonNull String NoteId) {
        mNotesLocalDataSource.deleteNote(NoteId);

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        } else {
            mCachedNotes.remove(NoteId);
        }
    }

    private void refreshCache(List<Note> notes) {
        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.clear();
        for (Note note : notes) {
            mCachedNotes.put(note.getId(), note);
        }
        mCacheIsDirty = false;
    }

    private void refreshLocalDataSource(List<Note> notes) {
        mNotesLocalDataSource.deleteAllNotes();
        for (Note note : notes) {
            mNotesLocalDataSource.saveNote(note);
        }
    }

    @Nullable
    private Note getNoteWithId(@NonNull String id) {
        if (mCachedNotes == null || mCachedNotes.isEmpty()) {
            return null;
        } else {
            return mCachedNotes.get(id);
        }
    }
/*
    public void saveNotesRemote(final ArrayList<Note> notes, final Context context, final SaveNoteCallback callback) {
        mNotesRemoteDataSource.deleteAllNotes(new DeleteNoteCallback() {
            @Override
            public void onNoteDeleted() {
                mNotesRemoteDataSource.saveNotes(0, notes, BitmapUtils.getBitmapsFromURIs(notes.get(0).getLocalImage(), context, false), callback);
            }

            @Override
            public void onError() {
                callback.onError();
            }
        });
    }

    public void getNotesFromRemoteDataSource() {
        mNotesRemoteDataSource.getNotes(new LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                notes = removeLocalReferences(new ArrayList<>(notes));
                refreshCache(notes);
                refreshLocalDataSource(notes);
                callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private ArrayList<Note> removeLocalReferences(ArrayList<Note> notes) {
        for (Note note : notes) {
            note.setLocalImage(new ArrayList<String>());
        }

        return notes;
    }
    */
}

