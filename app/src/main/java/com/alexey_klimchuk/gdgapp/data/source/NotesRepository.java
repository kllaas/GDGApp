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

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CustomComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation to load Notes from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class NotesRepository implements NotesDataSource {

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
    private Context context;

    // Prevent direct instantiation.
    private NotesRepository(@NonNull NotesDataSource notesRemoteDataSource,
                            @NonNull NotesDataSource notesLocalDataSource, Context context) {
        mNotesRemoteDataSource = notesRemoteDataSource;
        mNotesLocalDataSource = notesLocalDataSource;
        this.context = context;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param notesRemoteDataSource the backend data source
     * @param notesLocalDataSource  the device storage data source
     * @return the {@link NotesRepository} instance
     */
    public static NotesRepository getInstance(NotesDataSource notesRemoteDataSource,
                                              NotesDataSource notesLocalDataSource, Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotesRepository(notesRemoteDataSource, notesLocalDataSource, context);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(NotesDataSource, NotesDataSource, Context)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    public static List<Note> getCachedNotes() {
        return new ArrayList<>(mCachedNotes.values());
    }

    /**
     * Gets Notes from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadNotesCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getNotes(@NonNull final LoadNotesCallback callback) {

        // Respond immediately with cache if available and not dirty
        if (mCachedNotes != null && !mCacheIsDirty) {
            callback.onNotesLoaded(new ArrayList<>(mCachedNotes.values()));
            return;
        }

        // Query the local storage if available. If not, query the network.
        mNotesLocalDataSource.getNotes(new LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                Collections.sort(notes, new CustomComparator());
                refreshCache(notes);
                callback.onNotesLoaded(notes);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveNote(@NonNull final Note note, final Bitmap image,
                         final SaveNoteCallback callback) {
        if (image != null) {
            try {
                BitmapUtils.deleteImageFile(note.getLocalImage());
                note.setLocalImage(BitmapUtils.createImageFile(image, true));
            } catch (Exception ignored) {
            }
        }

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        ArrayList<Note> notes = new ArrayList<Note>(mCachedNotes.values());
        notes.add(0, note);
        refreshCache(notes);

        mNotesLocalDataSource.saveNote(note, image, callback);
    }

    @Override
    public void editNote(@NonNull Note note, Bitmap image, SaveNoteCallback callback) {
        if (image != null) {
            try {
                BitmapUtils.deleteImageFile(note.getLocalImage());
                note.setLocalImage(BitmapUtils.createImageFile(image, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mNotesLocalDataSource.editNote(note, image, callback);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.put(note.getId(), note);
        callback.onNoteSaved();
    }

    /**
     * Gets Notes from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link LoadNotesCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getNote(@NonNull final String noteId, @NonNull final GetNoteCallback callback) {

        final Note cachedNote = getNoteWithId(noteId);

        // Respond immediately with cache if available
        if (cachedNote != null) {
            callback.onNoteLoaded(cachedNote);
            return;
        }

        // Load from server/persisted if needed.

        // Is the Note in the local data source? If not, query the network.
        mNotesLocalDataSource.getNote(noteId, new GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                callback.onNoteLoaded(note);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void refreshNotes() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllNotes() {
        mNotesRemoteDataSource.deleteAllNotes();
        mNotesLocalDataSource.deleteAllNotes();

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        }
        mCachedNotes.clear();
    }

    @Override
    public void deleteNote(@NonNull String NoteId, DeleteNoteCallback callback) {
        mNotesLocalDataSource.deleteNote(NoteId, callback);

        if (mCachedNotes == null) {
            mCachedNotes = new LinkedHashMap<>();
        } else {
            mCachedNotes.remove(NoteId);
        }
    }

    @Override
    public void getNotesByDate(Date date, final LoadNotesCallback callback) {
        mNotesLocalDataSource.getNotesByDate(date, new LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                Collections.sort(notes, new CustomComparator());
                callback.onNotesLoaded(notes);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void getNotesFromRemoteDataSource(@NonNull final LoadNotesCallback callback) {
        mNotesRemoteDataSource.getNotes(new LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
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
            mNotesLocalDataSource.saveNote(note, null, null);
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
}
