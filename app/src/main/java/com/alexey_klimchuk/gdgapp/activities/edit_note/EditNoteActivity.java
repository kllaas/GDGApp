package com.alexey_klimchuk.gdgapp.activities.edit_note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.BaseActivity;
import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapters.CustomSpinnerAdapter;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.utils.BitmapUtils;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;
import com.alexey_klimchuk.gdgapp.utils.ToastUtils;
import com.alexey_klimchuk.gdgapp.utils.schedulers.SchedulerProvider;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
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
public class EditNoteActivity extends BaseActivity implements EditNoteRelations.View {

    private static final int PICK_IMAGE_REQUEST = 1;

    @BindView(R.id.spinner)
    public Spinner spinner;

    @BindView(R.id.edit_text_name_create)
    public EditText noteName;

    @BindView(R.id.edit_text_content)
    public EditText noteContent;

    @BindView(R.id.image_view_create)
    public ImageView noteImage;

    @BindView(R.id.button_create)
    public Button buttonEdit;

    @BindView(R.id.preview_recview)
    public RecyclerView mRecyclerView;

    private String[] spinnerValues;

    private Bitmap currentBitmap;

    private EditNotePresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ButterKnife.bind(this);

        spinnerValues = new String[]{
                getResources().getString(R.string.mood_good),
                getResources().getString(R.string.mood_normal),
                getResources().getString(R.string.mood_bad)};

        String noteId = getIntent().getStringExtra(Constants.ARGUMENT_EDIT_NOTE_ID);

        presenter = new EditNotePresenter(this, SchedulerProvider.getInstance());
        presenter.loadNote(noteId);

        initializeVariables();
    }

    @OnClick(R.id.fab)
    public void onClick() {
        pickImage();
    }

    /**
     * Initializing  variables
     */
    private void initializeVariables() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(EditNoteActivity.this, R.layout.mood_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        buttonEdit.setText(R.string.edit_note);
    }

    /**
     * Picking image from gallery
     */
    private void pickImage() {
        if (CacheUtils.tempBitmaps.getFullSizeImages().size() <= 5) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            ToastUtils.showMessage(getString(R.string.cant_add_more_images_message), getActivity());
        }
    }

    /**
     * Called when choose picture from gallery
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            CropImage.activity(selectedImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                try {
                    currentBitmap = BitmapUtils.resizeImage(EditNoteActivity.this, result.getUri(), 600);
                    ImageView imageView = (ImageView) findViewById(R.id.image_view_create);

                    imageView.setDrawingCacheEnabled(false);
                    imageView.setImageBitmap(currentBitmap);

                    presenter.addImage(currentBitmap);

                } catch (IOException e) {

                    Toast.makeText(EditNoteActivity.this, "Something is wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                ToastUtils.showMessage(result.getError().getMessage(), this);
            }
        }
    }

    /**
     * Creating note.
     */
    @OnClick(R.id.button_create)
    public void onClick(View view) {
        Note note = new Note(noteName.getText().toString(), noteContent.getText().toString(),
                new Date(), getMoodFromSpinner());
        presenter.updateNote(note, currentBitmap);
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
    public void updateViews(Note note) {
        setText(note);

        setMood(note);

        setImage(note);

        setPreviews(note);
    }

    private void setPreviews(Note note) {

        mRecyclerView.setAdapter(presenter.getImagePreviewAdapter(note.getLocalImage()));
    }

    private void setImage(Note note) {
        if (note.getLocalImage().get(0) != null) {
            try {

                File imgFile = new File(note.getLocalImage().get(0));
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    ((EditNoteActivity) getActivity()).getNoteImage().setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                String error = "error image loading: " + e.getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setMood(Note note) {
        switch (note.getMood()) {
            case GOOD:
                spinner.post(() -> spinner.setSelection(0));
                break;
            case NORMAL:
                spinner.post(() -> spinner.setSelection(1));
                break;
            case BAD:
                spinner.post(() -> spinner.setSelection(2));
                break;
        }
    }

    private void setText(Note note) {
        noteName.setText(note.getName());
        noteContent.setText(note.getContent());
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void saveResult() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    public ImageView getNoteImage() {
        return noteImage;
    }

}
