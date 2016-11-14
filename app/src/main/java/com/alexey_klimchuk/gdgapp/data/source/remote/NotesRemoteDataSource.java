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
import android.net.Uri;
import android.support.annotation.NonNull;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesDataSource;
import com.alexey_klimchuk.gdgapp.utils.CustomComparator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashSet;
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

    public static void loadImage(final Note note, final LoadImageCallback imageLoadedListener) {
       /* final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl(Constants.Firebase.USERS_DB_URL).child(Constants.Firebase.IMAGES_FOLDER);

        StorageReference imagesRef = storageRef.child(note.getId() + ".jpg");
        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    note.setLocalImage(BitmapUtils.createImageFile(bmp, false));
                    imageLoadedListener.onImageLoaded(note, bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                    imageLoadedListener.onImageNotAvailable();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Loading image: ", exception.getMessage());
                imageLoadedListener.onImageNotAvailable();
            }
        });*/
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
    public void saveNote(@NonNull final Note note, final HashSet<Bitmap> bitmap, final SaveNoteCallback callback) {
        /*mDatabase.child(Constants.Firebase.USERS_FOLDER)
                .child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child(Constants.Firebase.NOTES_FOLDER)
                .child(note.getId())
                .setValue(note)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (bitmap != null) {
                                saveImage(bitmap, note.getId(), callback);
                            } else {
                                callback.onNoteSaved();
                            }
                        } else {
                            callback.onError();
                        }
                    }
                });*/
    }

    @Override
    public void editNote(@NonNull Note note, HashSet<Bitmap> image, SaveNoteCallback callback) {

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

    private void saveImage(final Bitmap image, final String noteId, final SaveNoteCallback callback) {
        StorageReference imageRef = storageRef.child(noteId + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
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
                        .setValue(downloadUrl.toString());

                callback.onNoteSaved();
            }
        });
    }
}
