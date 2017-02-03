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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alexey_klimchuk.gdgapp.App;
import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CustomComparator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class NotesRemoteDataSource implements NotesDataSource {

    private static NotesRemoteDataSource INSTANCE;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl(Constants.Firebase.USERS_DB_URL).child(Constants.Firebase.IMAGES_FOLDER);
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    // Prevent direct instantiation.
    private NotesRemoteDataSource() {
    }

    public static NotesRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotesRemoteDataSource();
        }
        return INSTANCE;
    }

    public static void loadImages(final ArrayList<Note> notes, final int currentItem, final LoadImageCallback imageLoadedListener) {
        final int nextItem = currentItem + 1;

        loadImagesFromNote(notes, currentItem, 0, new LoadImageCallback() {
            @Override
            public void onImagesLoaded(ArrayList<Note> notes, Bitmap bitmap) {

                Log.d("Loading image", "note loaded: " + currentItem);

                if (nextItem == notes.size()) {
                    imageLoadedListener.onImagesLoaded(notes, bitmap);
                } else {
                    loadImages(notes, nextItem, imageLoadedListener);
                }
            }

            @Override
            public void onImageNotAvailable() {

            }
        });
    }

    private static void loadImagesFromNote(final ArrayList<Note> notes, final int noteItem, final int currentItem, final LoadImageCallback imageLoadedListener) {
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.Firebase.USERS_DB_URL).child(Constants.Firebase.IMAGES_FOLDER);

        StorageReference imagesRef = storageRef.child(notes.get(noteItem).getId() + ".jpg");

        final int nextItem = currentItem + 1;
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Log.d("Loading image", "note: " + noteItem + ", image:" + currentItem);

                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    notes.get(noteItem).getLocalImage().add(BitmapUtils.createImageFile(bmp, false));

                    if (nextItem == notes.get(noteItem).getImage().size())
                        imageLoadedListener.onImagesLoaded(notes, bmp);
                    else {
                        loadImagesFromNote(notes, noteItem, nextItem, imageLoadedListener);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading image: ", exception.getMessage());
            }
        });
    }

    /**
     * Note: {@link LoadNotesCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getNotes(final @NonNull LoadNotesCallback callback) {
        mDatabase.child(Constants.Firebase.USERS_FOLDER)
                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child(Constants.Firebase.NOTES_FOLDER).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            GenericTypeIndicator<Map<String, Note>> t = new GenericTypeIndicator<Map<String, Note>>() {
                            };
                            Map<String, Note> notes = dataSnapshot.getValue(t);
                            ArrayList list = new ArrayList<Note>(notes.values());
                            Collections.sort(list, new CustomComparator());

                            callback.onNotesLoaded(list);
                        } catch (Exception e) {
                            callback.onDataNotAvailable();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onDataNotAvailable();
                    }
                });
    }

    /**
     * Note: {@link GetNoteCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getNote(@NonNull String noteId, final @NonNull GetNoteCallback callback) {
        mDatabase.child(Constants.Firebase.USERS_FOLDER)
                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child(Constants.Firebase.NOTES_FOLDER)
                .child(noteId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Note notes = dataSnapshot.getValue(Note.class);
                        callback.onNoteLoaded(notes);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onDataNotAvailable();
                    }
                });
    }

    @Override
    public void saveNote(@NonNull final Note note, final ArrayList<Bitmap> bitmaps, final SaveNoteCallback callback) {
        mDatabase.child(Constants.Firebase.USERS_FOLDER)
                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child(Constants.Firebase.NOTES_FOLDER)
                .child(note.getId())
                .setValue(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (bitmaps != null) {
                                saveImages(0, bitmaps, note.getId(), callback);
                            } else {
                                callback.onNoteSaved();
                            }
                        } else {
                            callback.onError();
                        }
                    }
                });
    }

    @Override
    public void saveNotes(final int currentIndex, final ArrayList<Note> notes, final ArrayList<Bitmap> bitmaps, final SaveNoteCallback callback) {
        final int nextIndex = currentIndex + 1;

        mDatabase.child(Constants.Firebase.USERS_FOLDER)
                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child(Constants.Firebase.NOTES_FOLDER)
                .child(notes.get(currentIndex).getId())
                .setValue(notes.get(currentIndex))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            if (bitmaps != null) {
                                if (!notes.get(currentIndex).getLocalImage().get(0).equals("")) {
                                    saveImages(0, bitmaps, notes.get(currentIndex).getId(), new SaveNoteCallback() {
                                        @Override
                                        public void onNoteSaved() {
                                            saveNextNote(nextIndex, notes, callback);

                                            Log.d("SettingsPres", "note saved: " + currentIndex);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                                } else {
                                    saveNextNote(nextIndex, notes, callback);
                                }
                            } else {
                                callback.onNoteSaved();
                            }
                        } else {
                            callback.onError();
                        }
                    }
                });
    }

    private void saveNextNote(int nextIndex, ArrayList<Note> notes, SaveNoteCallback callback) {
        if (nextIndex == notes.size())
            callback.onNoteSaved();
        else
            saveNotes(nextIndex, notes,
                    BitmapUtils.getBitmapsFromURIs(notes.get(nextIndex).getLocalImage(),
                            App.getAppContext(), false), callback);
    }

    @Override
    public void editNote(@NonNull Note note, ArrayList<Bitmap> image, SaveNoteCallback callback) {

    }


    @Override
    public void refreshNotes() {
        // Not required because the {@link NotesRepository} handles the logic of refreshing the
        // Notes from all the available data sources.
    }

    @Override
    public void deleteAllNotes() {
    }

    @Override
    public void deleteNote(@NonNull String NoteId, DeleteNoteCallback callback) {

    }

    @Override
    public void getNotesByDate(Date date, LoadNotesCallback loadNotesCallback) {

    }

    private void saveImages(final int currentIndex, final ArrayList<Bitmap> images, final String noteId, final SaveNoteCallback callback) {
        final StorageReference imageRef = storageRef.child(noteId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        images.get(currentIndex).compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);


        final int nextIndex = currentIndex + 1;

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onError();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String s = mAuth.getCurrentUser().getEmail().replaceAll("\\.", "");

                mDatabase.child(Constants.Firebase.USERS_FOLDER)
                        .child(s)
                        .child(Constants.Firebase.NOTES_FOLDER)
                        .child(noteId)
                        .child(Constants.Firebase.IMAGES_FOLDER)
                        .child(currentIndex + "")
                        .setValue(downloadUrl.toString());

                Log.d("SettingsPres", "image saved: " + currentIndex);

                if (nextIndex == images.size())
                    callback.onNoteSaved();
                else
                    saveImages(nextIndex, images, noteId, callback);
            }
        });
    }
}
