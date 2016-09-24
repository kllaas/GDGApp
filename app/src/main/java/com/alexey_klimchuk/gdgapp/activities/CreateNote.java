package com.alexey_klimchuk.gdgapp.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.CustomSpinnerAdapter;
import com.alexey_klimchuk.gdgapp.helpers.DatabaseHelper;
import com.alexey_klimchuk.gdgapp.models.Note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * This activity work in two modes: creating and updating
 * At creating create note in db.
 * At updating - update it.
 */
public class CreateNote extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Spinner spinner;
    private String[] spinnerValues;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private EditText noteName;
    private EditText noteContent;
    private ImageView noteImage;
    private Note updatingNote = null;
    private Bitmap currentBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        initializeVariables();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Initializing  variables
     */
    private void initializeVariables() {
        databaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();// Get id of Note in update mode
        if (extras != null) {
            updatingNote = databaseHelper.getNoteById(sqLiteDatabase, extras.getString("id"));
        }

        noteName = (EditText) findViewById(R.id.edit_text_name_create);
        noteContent = (EditText) findViewById(R.id.edit_text_content);
        noteImage = (ImageView) findViewById(R.id.image_view_create);
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        spinnerValues = getResources().getStringArray(R.array.mood_variants);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(CreateNote.this, R.layout.mood_item, spinnerValues);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);// Apply the adapter to the spinner

        if (updatingNote != null) { //If activity in update mode
            Button createButton = (Button) findViewById(R.id.button_create);
            createButton.setText(R.string.edit_note);
            noteName.setText(updatingNote.getName());
            noteContent.setText(updatingNote.getContent());
            if (updatingNote.getImage() != null) {
                File imgFile = new File(updatingNote.getImage());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    noteImage.setImageBitmap(myBitmap);
                }
            }
        }
    }

    /**
     * Picking image from gallery
     */
    private void pickImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Called when choose picture from gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                currentBitmap = resizeImage(CreateNote.this, selectedImageUri);
                ImageView imageView = (ImageView) findViewById(R.id.image_view_create);

                if (imageView != null) {
                    imageView.setImageBitmap(currentBitmap);
                } else {
                    Toast.makeText(CreateNote.this, "Something is wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(CreateNote.this, "Something is wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creating or updating note.
     * In create mode: if image was not picked set null to Note.image.
     * In update mode: if image was not picked set image from updatingNote to Note.image
     */
    public void onClick(View view) {
        if (currentBitmap != null) {
            try {
                Note note = new Note(noteName.getText().toString(), noteContent.getText().toString(),
                        new Date(), createImageFile(), getMoodFromSpinner());

                if (updatingNote == null) {// if activity in update mode update note.
                    databaseHelper.addNoteInDB(sqLiteDatabase, note);
                } else {
                    note.setDate(updatingNote.getDate());
                    note.setId(updatingNote.getId());
                    databaseHelper.updateNoteInDB(sqLiteDatabase, note);
                }
            } catch (IOException e) {
                Toast.makeText(CreateNote.this, "Error note saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Note note = new Note(noteName.getText().toString(), noteContent.getText().toString(),
                    new Date(), null, getMoodFromSpinner());

            if (updatingNote == null) {// If activity in update mode update note.
                databaseHelper.addNoteInDB(sqLiteDatabase, note);
            } else {
                note.setImage(updatingNote.getImage());
                note.setId(updatingNote.getId());
                databaseHelper.updateNoteInDB(sqLiteDatabase, note);
            }
        }

        Intent intent = new Intent(CreateNote.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Creating image file.
     */
    private String createImageFile() throws IOException {
        String extr = Environment.getExternalStorageDirectory().toString();
        File mFolder = new File(extr + "/DiaryImages");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        File f = new File(mFolder.getAbsolutePath(), (new Date()).getTime() + "image.png");// Create with an unique name
        FileOutputStream fos = new FileOutputStream(f);
        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        currentBitmap.recycle();
        return f.getAbsolutePath();
    }

    /**
     * Getting mood state from spinner.
     */
    private Note.Mood getMoodFromSpinner() {
        String item = spinner.getSelectedItem().toString();
        if (item.equals(spinnerValues[0])) {
            return Note.Mood.GOOD;
        }
        if (item.equals(spinnerValues[1])) {
            return Note.Mood.NORMAL;
        } else return Note.Mood.BAD;
    }

    /**
     * Resizing image from gallery to increase performance.
     */
    public static Bitmap resizeImage(Context c, Uri uri)
            throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, options);

        BitmapFactory.Options finalOptions = new BitmapFactory.Options();
        finalOptions.inSampleSize = calculateInSampleSize(options, 240, 240);
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, finalOptions);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
