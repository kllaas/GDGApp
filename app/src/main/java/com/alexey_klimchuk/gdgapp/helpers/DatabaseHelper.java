package com.alexey_klimchuk.gdgapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alex on 24.03.2016.
 * Class to help work with SQLite db.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    private static final String TAG = "mDatabaseHelper";
    private static final String DATABASE_TABLE_NAME = "notes";
    private static final String ID_COLUMN = "object_id";
    private static final String NAME_COLUMN = "name";
    private static final String CONTENT_COLUMN = "content";
    private static final String IMAGE_COLUMN = "image";
    private static final String DATE_COLUMN = "date";
    private static final String MOOD_COLUMN = "mood";

    /**
     * Script to create db with note table.
     */
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE_NAME + " (" + BaseColumns._ID + " integer primary key autoincrement, "
            + ID_COLUMN + " integer UNIQUE, "
            + NAME_COLUMN + " text, "
            + CONTENT_COLUMN + " text, "
            + MOOD_COLUMN + " integer, "
            + IMAGE_COLUMN + " text, "
            + DATE_COLUMN + " text);";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete old table and create new
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_NAME);
        // Create new table
        onCreate(db);
    }

    /**
     * Adding note to DB
     */
    public void addNoteInDB(SQLiteDatabase sqLiteDatabase, Note note) {
        ContentValues newValues = new ContentValues();

        newValues.put(ID_COLUMN, note.getId());
        newValues.put(NAME_COLUMN, note.getName());
        newValues.put(CONTENT_COLUMN, note.getContent());
        newValues.put(IMAGE_COLUMN, note.getImage());
        newValues.put(DATE_COLUMN, note.getDate());
        newValues.put(MOOD_COLUMN, note.getMood().ordinal());

        sqLiteDatabase.insert(DATABASE_TABLE_NAME, null, newValues);
    }

    /**
     * Updating note in DB
     */
    public void updateNoteInDB(SQLiteDatabase sqLiteDatabase, Note note) {
        ContentValues newValues = new ContentValues();

        newValues.put(ID_COLUMN, note.getId());
        newValues.put(NAME_COLUMN, note.getName());
        newValues.put(CONTENT_COLUMN, note.getContent());
        newValues.put(IMAGE_COLUMN, note.getImage());
        newValues.put(DATE_COLUMN, note.getDate());
        newValues.put(MOOD_COLUMN, note.getMood().ordinal());

        sqLiteDatabase.update(DATABASE_TABLE_NAME, newValues, ID_COLUMN + " = ?", new String[]{note.getId()});
    }

    /**
     * Getting note object from database by uncial id.
     */
    public Note getNoteById(SQLiteDatabase sqLiteDatabase, String id) {
        String query = "SELECT * FROM " + DATABASE_TABLE_NAME + " WHERE " + ID_COLUMN + " = '" + id + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();

        Note note = new Note();
        note.setId(cursor.getString((cursor.getColumnIndex(ID_COLUMN))));
        note.setName(cursor.getString((cursor.getColumnIndex(NAME_COLUMN))));
        note.setContent(cursor.getString((cursor.getColumnIndex(CONTENT_COLUMN))));
        note.setImage(cursor.getString((cursor.getColumnIndex(IMAGE_COLUMN))));
        note.setDate(cursor.getString(cursor.getColumnIndex(DATE_COLUMN)));
        note.setMood(Note.Mood.values()[cursor.getInt((cursor.getColumnIndex(MOOD_COLUMN)))]);

        cursor.close();
        return note;
    }

    /**
     * Getting all note objects from database.
     */
    public ArrayList<Note> getAllNotes(SQLiteDatabase sqLiteDatabase) {
        final ArrayList<Note> notes = new ArrayList<Note>();
        String query = "SELECT * " + " FROM " + DATABASE_TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getString((cursor.getColumnIndex(ID_COLUMN))));
                note.setName(cursor.getString((cursor.getColumnIndex(NAME_COLUMN))));
                note.setContent(cursor.getString((cursor.getColumnIndex(CONTENT_COLUMN))));
                note.setImage(cursor.getString((cursor.getColumnIndex(IMAGE_COLUMN))));
                note.setDate(cursor.getString(cursor.getColumnIndex(DATE_COLUMN)));
                note.setMood(Note.Mood.values()[cursor.getInt((cursor.getColumnIndex(MOOD_COLUMN)))]);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    /**
     * Deleting note object from database by uncial id.
     */
    public void deleteNote(SQLiteDatabase sqLiteDatabase, String objectId) {
        try {
            sqLiteDatabase.delete(DATABASE_TABLE_NAME, "object_id = ?", new String[]{objectId});
        } catch (Exception e) {
            Log.d(TAG, "error in deleting: " + e.getMessage());
        }
    }


    public ArrayList<Note> getNotesByDate(SQLiteDatabase sqLiteDatabase, Date date) {
        final ArrayList<Note> notes = new ArrayList<Note>();
        String query = "SELECT * " + " FROM " + DATABASE_TABLE_NAME + " WHERE " +
                DATE_COLUMN + " = '" + DateUtils.convertDateToString(date) + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(cursor.getString((cursor.getColumnIndex(ID_COLUMN))));
                note.setName(cursor.getString((cursor.getColumnIndex(NAME_COLUMN))));
                note.setContent(cursor.getString((cursor.getColumnIndex(CONTENT_COLUMN))));
                note.setImage(cursor.getString((cursor.getColumnIndex(IMAGE_COLUMN))));
                note.setDate(cursor.getString(cursor.getColumnIndex(DATE_COLUMN)));
                note.setMood(Note.Mood.values()[cursor.getInt((cursor.getColumnIndex(MOOD_COLUMN)))]);
                notes.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }
}