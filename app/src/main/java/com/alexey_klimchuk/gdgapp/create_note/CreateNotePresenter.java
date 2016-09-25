package com.alexey_klimchuk.gdgapp.create_note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.activities.MainActivity;
import com.alexey_klimchuk.gdgapp.models.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by Alexey on 24.09.2016.
 */

public class CreateNotePresenter implements CreateNoteRelations.Presenter {

    private CreateNoteRelations.View view;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance()
            .getReferenceFromUrl("gs://gdgapp-2d5ae.appspot.com").child("images");

    public CreateNotePresenter(CreateNoteActivity activty) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        view = activty;
    }

    @Override
    public void saveNote(final Note note, final Bitmap image) {
        view.showProgressDialog();
        mDatabase.child("users").child(mAuth.getCurrentUser().getEmail().replaceAll("\\.", ""))
                .child("notes").child(note.getId()).setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (image != null) {
                        saveImage(image, note.getId());
                    } else {
                        view.hideProgressDialog();
                        Intent intent = new Intent(view.getActivity(), MainActivity.class);
                        view.getActivity().startActivity(intent);
                    }
                } else {
                    view.hideProgressDialog();
                    Toast.makeText(view.getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveImage(final Bitmap image, final String noteId) {
        StorageReference imageRef = storageRef.child((new Date()).getTime() + "space.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                image.recycle();
                view.hideProgressDialog();
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.recycle();

                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String s = mAuth.getCurrentUser().getEmail().replaceAll("\\.", "");
                mDatabase.child("users").child(s)
                        .child("notes").child(noteId).child("image").setValue(downloadUrl.toString());

                view.hideProgressDialog();
                Intent intent = new Intent(view.getActivity(), MainActivity.class);
                view.getActivity().startActivity(intent);
            }
        });
    }
}
