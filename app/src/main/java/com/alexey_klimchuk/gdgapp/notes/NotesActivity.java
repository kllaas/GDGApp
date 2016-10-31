package com.alexey_klimchuk.gdgapp.notes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.RecyclerAdapter;
import com.alexey_klimchuk.gdgapp.create_note.CreateNoteActivity;
import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends AppCompatActivity implements NotesRelations.View {

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

    private NotesRelations.Presenter mPresenter;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mPresenter = new NotesPresenter(this);
        mPresenter.loadNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshData(List<Note> notes) {
        mAdapter = new RecyclerAdapter(notes, NotesActivity.this);
        mRecyclerView.setAdapter(mAdapter);
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
    public Activity getActivity() {
        return this;
    }
}
