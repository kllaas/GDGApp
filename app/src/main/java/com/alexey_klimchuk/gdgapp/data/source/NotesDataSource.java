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

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Main entry point for accessing Notes data.
 * <p>
 * For simplicity, only loadNotes() and getNote() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new Note is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface NotesDataSource {

    void getNotes(@NonNull LoadNotesCallback callback);

    void getNote(@NonNull String NoteId, @NonNull GetNoteCallback callback);

    void saveNote(@NonNull Note note, HashSet<Bitmap> images, SaveNoteCallback callback);

    void editNote(@NonNull Note note, HashSet<Bitmap> image, SaveNoteCallback callback);

    void refreshNotes();

    void deleteAllNotes();

    void deleteNote(@NonNull String NoteId, DeleteNoteCallback callback);

    void getNotesByDate(Date date, LoadNotesCallback loadNotesCallback);

    interface LoadNotesCallback {

        void onNotesLoaded(List<Note> Notes);

        void onDataNotAvailable();
    }

    interface LoadImageCallback {

        void onImageLoaded(Note note, Bitmap bitmap);

        void onImageNotAvailable();
    }

    interface GetNoteCallback {

        void onNoteLoaded(Note note);

        void onDataNotAvailable();
    }

    interface SaveNoteCallback {

        void onNoteSaved();

        void onError();
    }

    interface DeleteNoteCallback {

        void onNoteDeleted();

        void onError();
    }
}
