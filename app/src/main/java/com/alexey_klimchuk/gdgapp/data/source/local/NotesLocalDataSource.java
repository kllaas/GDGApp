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
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.StringArrayBoxing;
import com.alexey_klimchuk.gdgapp.utils.schedulers.BaseSchedulerProvider;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

import static com.alexey_klimchuk.gdgapp.data.source.local.NotesPersistenceContract.NoteEntry;
import static com.alexey_klimchuk.gdgapp.utils.StringArrayBoxing.convertArrayToString;


/**
 * Concrete implementation of a data source as a db.
 */
public class NotesLocalDataSource implements NotesDataSource {

    private static NotesLocalDataSource INSTANCE;

    private final BriteDatabase mDatabaseHelper;

    private Func1<Cursor, Note> mTaskMapperFunction;

    private NotesLocalDataSource(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        NotesDbHelper mDbHelper = new NotesDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(mDbHelper, schedulerProvider.io());
        mTaskMapperFunction = this::getNote;
    }

    public static NotesLocalDataSource getInstance(@NonNull Context context, @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new NotesLocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Note>> getNotes() {
        String[] projection = {
                NoteEntry.IMAGE_LOCAL_COLUMN,
                NoteEntry.COLUMN_NAME_ENTRY_ID,
                NoteEntry.NAME_COLUMN,
                NoteEntry.CONTENT_COLUMN,
                NoteEntry.IMAGE_COLUMN,
                NoteEntry.DATE_COLUMN,
                NoteEntry.MOOD_COLUMN
        };

        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), NoteEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(NoteEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction);
    }

    private Note getNote(Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(NoteEntry.COLUMN_NAME_ENTRY_ID));
        String name = c.getString(c.getColumnIndexOrThrow(NoteEntry.NAME_COLUMN));
        String content =
                c.getString(c.getColumnIndexOrThrow(NoteEntry.CONTENT_COLUMN));
        String image =
                c.getString(c.getColumnIndexOrThrow(NoteEntry.IMAGE_COLUMN));
        String imageLocal =
                c.getString(c.getColumnIndexOrThrow(NoteEntry.IMAGE_LOCAL_COLUMN));
        String date =
                c.getString(c.getColumnIndexOrThrow(NoteEntry.DATE_COLUMN));
        Note.Mood mood =
                Note.Mood.values()[c.getInt((c.getColumnIndex(NoteEntry.MOOD_COLUMN)))];

        Note note = new Note(itemId, name, content, new Date(Long.valueOf(date)), StringArrayBoxing.convertStringToArray(image), mood);

        note.setLocalImage(StringArrayBoxing.convertStringToArray(imageLocal));
        return note;
    }

    @Override
    public Observable<Note> getNote(@NonNull String noteId) {

        String[] projection = {
                NoteEntry.IMAGE_LOCAL_COLUMN,
                NoteEntry.COLUMN_NAME_ENTRY_ID,
                NoteEntry.NAME_COLUMN,
                NoteEntry.CONTENT_COLUMN,
                NoteEntry.IMAGE_COLUMN,
                NoteEntry.DATE_COLUMN,
                NoteEntry.MOOD_COLUMN
        };

        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection), NoteEntry.TABLE_NAME, NoteEntry.COLUMN_NAME_ENTRY_ID);
        return mDatabaseHelper.createQuery(NoteEntry.TABLE_NAME, sql, noteId)
                .mapToOneOrDefault(mTaskMapperFunction, null);
    }

    @Override
    public void editNote(@NonNull Note note, ArrayList<Bitmap> images) {
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NAME_ENTRY_ID, note.getId());
        values.put(NoteEntry.NAME_COLUMN, note.getName());
        values.put(NoteEntry.CONTENT_COLUMN, note.getContent());
        values.put(NoteEntry.IMAGE_COLUMN, convertArrayToString(note.getImage()));
        values.put(NoteEntry.DATE_COLUMN, note.getDate());
        values.put(NoteEntry.MOOD_COLUMN, note.getMood().ordinal());

        values.put(NoteEntry.IMAGE_LOCAL_COLUMN, convertArrayToString(note.getLocalImage()));
        //to use new image in RecyclerView
        CacheUtils.removeBitmapFromMemCache(note.getId());

        String selection = NoteEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {note.getId()};
        mDatabaseHelper.update(NoteEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void saveNote(@NonNull Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_NAME_ENTRY_ID, note.getId());
        values.put(NoteEntry.NAME_COLUMN, note.getName());
        values.put(NoteEntry.CONTENT_COLUMN, note.getContent());
        values.put(NoteEntry.IMAGE_COLUMN, convertArrayToString(note.getImage()));
        values.put(NoteEntry.DATE_COLUMN, note.getDate());
        values.put(NoteEntry.MOOD_COLUMN, note.getMood().ordinal());

        values.put(NoteEntry.IMAGE_LOCAL_COLUMN, convertArrayToString(note.getLocalImage()));

        mDatabaseHelper.insert(NoteEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void deleteAllNotes() {
        mDatabaseHelper.delete(NoteEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteNote(@NonNull String noteId) {
        String selection = NoteEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {noteId};
        mDatabaseHelper.delete(NoteEntry.TABLE_NAME, selection, selectionArgs);
    }

    /*@Override
    public Observable<List<Note>> getNotesByDate(Date searchDate) {
        String[] projection = {
                NoteEntry.IMAGE_LOCAL_COLUMN,
                NoteEntry.COLUMN_NAME_ENTRY_ID,
                NoteEntry.NAME_COLUMN,
                NoteEntry.CONTENT_COLUMN,
                NoteEntry.IMAGE_COLUMN,
                NoteEntry.DATE_COLUMN,
                NoteEntry.MOOD_COLUMN
        };

        String selection = NoteEntry.DATE_COLUMN + " > " + DateUtils.getStartDayDate(searchDate) +
                " AND " + NoteEntry.DATE_COLUMN + " < " + DateUtils.getEndDayDate(searchDate);

        String sql = String.format("SELECT %s FROM %s WHERE %s", TextUtils.join(",", projection), NoteEntry.TABLE_NAME, selection);
        return mDatabaseHelper.createQuery(NoteEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction);
    }*/
}
