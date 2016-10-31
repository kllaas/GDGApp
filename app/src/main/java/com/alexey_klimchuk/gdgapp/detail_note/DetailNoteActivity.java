package com.alexey_klimchuk.gdgapp.detail_note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.utils.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This activity work in two modes: creating and updating
 * At creating create note in db.
 * At updating - update it.
 */
public class DetailNoteActivity extends AppCompatActivity {

    @BindView(R.id.image_view_details)
    public ImageView noteImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Get the requested task id
        String noteId = getIntent().getStringExtra(Constants.EXTRA_NOTE_ID);

        DetailsFragment taskDetailFragment = (DetailsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);

        if (taskDetailFragment == null) {
            taskDetailFragment = DetailsFragment.newInstance(noteId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskDetailFragment, R.id.container);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public ImageView getNoteImage() {
        return noteImage;
    }

}
