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

package com.alexey_klimchuk.gdgapp.data.source.remote;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class NotesRemoteDataSource implements NotesDataSource {

    private static final int SERVICE_LATENCY_IN_MILLIS = 0;
    private final static Map<String, Note> NOTES_SERVICE_DATA;
    private static NotesRemoteDataSource INSTANCE;

    static {
        NOTES_SERVICE_DATA = new LinkedHashMap<>(2);
    }

    // Prevent direct instantiation.
    private NotesRemoteDataSource() {
    }

    public static NotesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotesRemoteDataSource();
        }
        return INSTANCE;
    }

    /*private static void addNote(String title, String description) {
        Note newNote = new Note(title, description);
        NOTES_SERVICE_DATA.put(newNote.getId(), newNote);
    }*/

    /**
     * Note: {@link LoadNotesCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getNotes(final @NonNull LoadNotesCallback callback) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onNotesLoaded(new ArrayList<Note>(NOTES_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    /**
     * Note: {@link GetNoteCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getNote(@NonNull String noteId, final @NonNull GetNoteCallback callback) {
        final Note Note = NOTES_SERVICE_DATA.get(noteId);

        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onNoteLoaded(Note);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveNote(@NonNull Note note, Bitmap bitmap, SaveNoteCallback callback) {
        NOTES_SERVICE_DATA.put(note.getId(), note);

        if (callback != null)
            callback.onNoteSaved();
    }

    @Override
    public void editNote(@NonNull Note note, Bitmap image, SaveNoteCallback callback) {
        NOTES_SERVICE_DATA.remove(note.getId());
        NOTES_SERVICE_DATA.put(note.getId(), note);
        if (callback != null)
            callback.onNoteSaved();
    }


    @Override
    public void refreshNotes() {
        // Not required because the {@link NotesRepository} handles the logic of refreshing the
        // Notes from all the available data sources.
    }

    @Override
    public void deleteAllNotes() {
        NOTES_SERVICE_DATA.clear();
    }

    @Override
    public void deleteNote(@NonNull String NoteId) {
        NOTES_SERVICE_DATA.remove(NoteId);
    }
}
