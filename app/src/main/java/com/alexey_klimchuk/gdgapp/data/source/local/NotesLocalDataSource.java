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

package com.alexey_klimchuk.gdgapp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;

import java.util.ArrayList;
import java.util.List;

import static com.alexey_klimchuk.gdgapp.data.source.local.NotesPersistenceContract.NoteEntry;


/**
 * Concrete implementation of a data source as a db.
 */
public class NotesLocalDataSource implements NotesDataSource {

    private static NotesLocalDataSource INSTANCE;

    private NotesDbHelper mDbHelper;

    // Prevent direct instantiation.
    private NotesLocalDataSource(@NonNull Context context) {
        mDbHelper = new NotesDbHelper(context);
    }

    public static NotesLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotesLocalDataSource(context);
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadNotesCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getNotes(@NonNull LoadNotesCallback callback) {
        List<Note> Notes = new ArrayList<Note>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        db.beginTransaction();

        String[] projection = {
                NoteEntry.COLUMN_NAME_ENTRY_ID,
                NoteEntry.NAME_COLUMN,
                NoteEntry.CONTENT_COLUMN,
                NoteEntry.IMAGE_COLUMN,
                NoteEntry.DATE_COLUMN,
                NoteEntry.MOOD_COLUMN
        };

        Cursor c = db.query(
                NoteEntry.TABLE_NAME, projection, null, null, null, null, null);

        int i = 0;
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_ENTRY_ID));
                String name = c.getString(c.getColumnIndexOrThrow(NoteEntry.NAME_COLUMN));
                String content =
                        c.getString(c.getColumnIndexOrThrow(NoteEntry.CONTENT_COLUMN));
                String image =
                        c.getString(c.getColumnIndexOrThrow(NoteEntry.IMAGE_COLUMN));
                String date =
                        c.getString(c.getColumnIndexOrThrow(NoteEntry.DATE_COLUMN));
                Note.Mood mood =
                        Note.Mood.values()[c.getInt((c.getColumnIndex(NoteEntry.MOOD_COLUMN)))];
                Note note = new Note(itemId, name, content, date, image, mood);
                Notes.add(note);
                i++;
            }
        }

        Log.d("LocalDataStorage", "loaded notes = " + i);
        if (c != null) {
            c.close();
        }

        db.endTransaction();
        db.close();

        if (Notes.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable();
        } else {
            callback.onNotesLoaded(Notes);
        }

    }

    /**
     * Note: {@link GetNoteCallback#onDataNotAvailable()} is fired if the {@link Note} isn't
     * found.
     */
    @Override
    public void getNote(@NonNull String NoteId, @NonNull GetNoteCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                NoteEntry.COLUMN_NAME_ENTRY_ID,
                NoteEntry.NAME_COLUMN,
                NoteEntry.CONTENT_COLUMN,
                NoteEntry.IMAGE_COLUMN,
                NoteEntry.DATE_COLUMN,
                NoteEntry.MOOD_COLUMN
        };

        String selection = NoteEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {NoteId};

        Cursor c = db.query(
                NoteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Note note = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_ENTRY_ID));
            String name = c.getString(c.getColumnIndexOrThrow(NoteEntry.NAME_COLUMN));
            String content =
                    c.getString(c.getColumnIndexOrThrow(NoteEntry.CONTENT_COLUMN));
            String image =
                    c.getString(c.getColumnIndexOrThrow(NoteEntry.IMAGE_COLUMN));
            String date =
                    c.getString(c.getColumnIndexOrThrow(NoteEntry.DATE_COLUMN));
            Note.Mood mood =
                    Note.Mood.values()[c.getInt((c.getColumnIndex(NoteEntry.MOOD_COLUMN)))];
            note = new Note(itemId, name, content, date, image, mood);
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (note != null) {
            callback.onNoteLoaded(note);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void editNote(@NonNull Note Note) {

    }

    @Override
    public void saveNote(@NonNull Note Note, SaveNoteCallback callback) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NAME_ENTRY_ID, Note.getId());
        values.put(NoteEntry.NAME_COLUMN, Note.getContent());
        values.put(NoteEntry.CONTENT_COLUMN, Note.getContent());
        values.put(NoteEntry.IMAGE_COLUMN, Note.getImage());
        values.put(NoteEntry.DATE_COLUMN, Note.getDate());
        values.put(NoteEntry.MOOD_COLUMN, Note.getMood().ordinal());

        db.insert(NoteEntry.TABLE_NAME, null, values);

        db.close();

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
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(NoteEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteNote(@NonNull String NoteId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = NoteEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {NoteId};

        db.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
