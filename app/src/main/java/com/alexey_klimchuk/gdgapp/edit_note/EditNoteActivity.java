package com.alexey_klimchuk.gdgapp.edit_note;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.helpers.DatabaseHelper;
import com.alexey_klimchuk.gdgapp.helpers.DateUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This activity work in two modes: creating and updating
 * At creating create note in db.
 * At updating - update it.
 */
public class EditNoteActivity extends AppCompatActivity implements EditNoteRelations.View {

    private static final int PICK_IMAGE_REQUEST = 1;
    @BindView(R.id.button_create)
    public
    Button editButton;
    private Spinner spinner;
    private String[] spinnerValues;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private EditText noteName;
    private EditText noteContent;
    private ImageView noteImage;
    private Note updatingNote;
    private Bitmap currentBitmap;

    private EditNotePresenter presenter;
    private ProgressDialog mProgressDialog;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        presenter = new EditNotePresenter(this);

        initializeVariables();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        pickImage();
    }

    /**
     * Initializing  variables
     */
    private void initializeVariables() {
        ButterKnife.bind(this);

        databaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();// Get id of Note in update mode
        updatingNote = databaseHelper.getNoteById(sqLiteDatabase, extras.getString("id"));

        noteName = (EditText) findViewById(R.id.edit_text_name_create);
        noteContent = (EditText) findViewById(R.id.edit_text_content);
        noteImage = (ImageView) findViewById(R.id.image_view_create);
        spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a custom spinner layout
        spinnerValues = getResources().getStringArray(R.array.mood_variants);
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(EditNoteActivity.this, R.layout.mood_item, spinnerValues);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);// Apply the adapter to the spinner

        editButton.setText(R.string.edit_note);
        noteName.setText(updatingNote.getName());
        noteContent.setText(updatingNote.getContent());
        if (!updatingNote.getImage().equals("")) {
            //TODO: show picture with Picasso.
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
                currentBitmap = resizeImage(EditNoteActivity.this, selectedImageUri);
                noteImage = (ImageView) findViewById(R.id.image_view_create);
                noteImage.setDrawingCacheEnabled(false); // clear drawing cache

                noteImage.setImageBitmap(currentBitmap);
            } catch (IOException e) {
                Toast.makeText(EditNoteActivity.this, "Something is wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creating or updating note.
     * In create mode: if image was not picked set null to Note.image.
     * In update mode: if image was not picked set image from updatingNote to Note.image
     */
    @OnClick(R.id.button_create)
    public void onClick(View view) {
        Note note = new Note(noteName.getText().toString(), noteContent.getText().toString(),
                DateUtils.convertDateToString(new Date()), getMoodFromSpinner());
        presenter.saveNote(note, currentBitmap);
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

    @Override
    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(getString(R.string.message_loading));
        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        mProgressDialog.cancel();
    }

    @Override
    public void showMessage(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
