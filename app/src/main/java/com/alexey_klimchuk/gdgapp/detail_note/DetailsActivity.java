package com.alexey_klimchuk.gdgapp.detail_note;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.data.Note;

import butterknife.BindView;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "mDetails";

    @BindView(R.id.mood_icon)
    public View moodState;
    @BindView(R.id.text_view_name_details)
    public TextView noteName;
    @BindView(R.id.text_view_date_details)
    public TextView noteDate;
    @BindView(R.id.text_view_content_details)
    public TextView noteContent;
    @BindView(R.id.image_view_details)
    public ImageView noteImage;

    private Note mNote;
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // Set content from note object to views
        fillViews();

        FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fab);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editNote();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

   *//**
     * Start Activity to edit note.
     * Put in extras id of current note.
     *//*
    private void editNote() {
        Intent intent = new Intent(DetailsActivity.this, EditNoteActivity.class);
        intent.putExtra("id", mNote.getId());
        startActivity(intent);
    }

    *//**
     * Set data from note object to views.
     *//*
    private void fillViews() {
        Bundle extras = getIntent().getExtras();// Get id of Note from NotesActivity
        mNote = databaseHelper.getNoteById(sqLiteDatabase, extras.getString("id"));// Get note from db

        noteName.setText(mNote.getName());
        noteContent.setText(mNote.getContent());
        noteDate.setText(mNote.getDate());

        GradientDrawable gd = new GradientDrawable();// Set mood
        if (mNote.getMood() == Note.Mood.GOOD)
            gd.setColor(ContextCompat.getColor(DetailsActivity.this, R.color.colorPrimary));
        if (mNote.getMood() == Note.Mood.NORMAL)
            gd.setColor(ContextCompat.getColor(DetailsActivity.this, R.color.colorNormal));
        if (mNote.getMood() == Note.Mood.BAD)
            gd.setColor(ContextCompat.getColor(DetailsActivity.this, R.color.colorBad));
        gd.setShape(GradientDrawable.OVAL);
        moodState.setBackground(gd);

        if (mNote.getImage() != null) {
            try {
                File imgFile = new File(mNote.getImage());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                String error = "error image loading: " + e.getMessage();
                Toast.makeText(DetailsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            createDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    *//**
     * Create dialog to delete note.
     *//*
    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Delete note");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseHelper.deleteNote(sqLiteDatabase, mNote.getId());
                if (mNote.getImage() != null) { // If note has imageView
                    deleteImageFile();
                }
                // Go to NotesActivity after deleting
                Intent intent = new Intent(DetailsActivity.this, NotesActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    *//**
     * Delete imageView file.
     *//*
    private void deleteImageFile() {
        File imgFile = new File(mNote.getImage());
        if (imgFile.exists()) {
            if (imgFile.delete()) {
                Log.d(TAG, "file was deleted");
            } else {
                Log.d(TAG, "file was not deleted");
            }
        }
    }*/
}
