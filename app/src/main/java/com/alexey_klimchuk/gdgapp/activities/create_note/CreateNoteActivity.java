package com.alexey_klimchuk.gdgapp.activities.create_note;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNoteActivity extends BaseActivity implements CreateNoteRelations.View {

    private static final int PICK_IMAGE_REQUEST = 1;

    @BindView(R.id.spinner)
    public Spinner spinner;

    @BindView(R.id.edit_text_name_create)
    public EditText noteName;

    @BindView(R.id.edit_text_content)
    public EditText noteContent;

    @BindView(R.id.image_view_create)
    public ImageView noteImage;

    @BindView(R.id.preview_recview)
    public RecyclerView mRecyclerView;

    private String[] spinnerValues = new String[]{"Good", "Norm", "Bad"};

    private CreateNotePresenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        presenter = new CreateNotePresenter(this, SchedulerProvider.getInstance());

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
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(CreateNoteActivity.this, R.layout.mood_item, spinnerValues);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(presenter.getImagePreviewAdapter());
    }

    /**
     * Picking image from gallery
     */
    private void pickImage() {
        if (CacheUtils.tempBitmaps.getFullSizeImages().size() < Constants.MAX_IMAGES_COUNT) {
            Intent intent = new Intent();

            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            ToastUtils.showMessage(R.string.cant_add_more_images_message, this);
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
                    Bitmap bitmap = BitmapUtils.resizeImage(CreateNoteActivity.this, result.getUri(), 600);
                    ImageView imageView = (ImageView) findViewById(R.id.image_view_create);

                    imageView.setDrawingCacheEnabled(false);
                    imageView.setImageBitmap(bitmap);

                    presenter.addImage(bitmap);
                } catch (IOException e) {
                    Toast.makeText(CreateNoteActivity.this, "Something is wrong: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
        presenter.saveNote(note);
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
    public Activity getActivity() {
        return this;
    }


}
