package com.alexey_klimchuk.gdgapp.detail_note;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.Constants;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.edit_note.EditNoteActivity;
import com.alexey_klimchuk.gdgapp.utils.DateUtils;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexey on 30.10.2016.
 */

public class DetailsFragment extends Fragment implements DetailNoteRelations.View {

    private static final int REQUEST_EDIT_TASK = 1;
    private static final String TAG = "mDetails";
    @BindView(R.id.mood_icon)
    public View moodState;
    @BindView(R.id.text_view_name_details)
    public TextView noteName;
    @BindView(R.id.text_view_date_details)
    public TextView noteDate;
    @BindView(R.id.text_view_content_details)
    public TextView noteContent;
    private DetailNoteRelations.Presenter mPresenter;
    private ProgressDialog mProgressDialog;

    private String noteId;
    private View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showEditTask(noteId);
        }
    };

    public static DetailsFragment newInstance(@Nullable String taskId) {
        Bundle arguments = new Bundle();
        arguments.putString(Constants.EXTRA_NOTE_ID, taskId);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_details, container, false);
        ButterKnife.bind(this, root);
        setHasOptionsMenu(true);

        noteId = getArguments().getString(Constants.EXTRA_NOTE_ID);
        mPresenter = new DetailNotePresenter(this);
        mPresenter.loadNote(noteId);

        ((DetailNoteActivity) getActivity()).getFab().setOnClickListener(fabOnClick);

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteDialog();
                return true;
        }
        return false;
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        builder.setTitle("Delete note");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Delete", mPresenter.getDeleteOnClick());
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
    }
/*
    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }*/

    @Override
    public void showEditTask(@NonNull String noteId) {
        Intent intent = new Intent(getContext(), EditNoteActivity.class);
        intent.putExtra(Constants.ARGUMENT_EDIT_NOTE_ID, noteId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TASK) {
            // If the task was edited successfully, go back to the list.
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.loadNote(noteId);
            }
        }
    }

    @Override
    public void showProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
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
    public void updateViews(Note note) {
        setText(note);

        setMood(note);

        setImage(note);
    }

    private void setImage(Note note) {
        if (note.getLocalImage()[0] != null) {
            try {
                File imgFile = new File(note.getLocalImage()[0]);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ((DetailNoteActivity) getActivity()).getNoteImage().setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                String error = "error image loading: " + e.getMessage();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setMood(Note note) {
        GradientDrawable gd = new GradientDrawable();
        switch (note.getMood()) {
            case GOOD:
                gd.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                break;
            case NORMAL:
                gd.setColor(ContextCompat.getColor(getContext(), R.color.colorNormal));
                break;
            case BAD:
                gd.setColor(ContextCompat.getColor(getContext(), R.color.colorBad));
                break;
        }

        gd.setShape(GradientDrawable.OVAL);
        moodState.setBackground(gd);
    }

    private void setText(Note note) {
        noteName.setText(note.getName());
        noteContent.setText(note.getContent());
        noteDate.setText(DateUtils.convertDateToString(new Date(note.getDate())));
    }
}
