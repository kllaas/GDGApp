package com.alexey_klimchuk.gdgapp.activities.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.alexey_klimchuk.gdgapp.BaseActivity;
import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.activities.create_note.CreateNoteActivity;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.utils.CacheUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesActivity extends BaseActivity implements NotesRelations.View, SearchDialogFragment.SearchDialogListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "recycler_state";

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.no_data_container)
    RelativeLayout mNoDataView;

    private NotesRelations.Presenter mPresenter;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
                startActivity(intent);

                CacheUtils.tempBitmaps.clear();
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
        int id = item.getItemId();

        if (id == R.id.action_search) {
            mPresenter.crateSearchDialog();
            return true;
        } else if (id == R.id.action_settings) {
            mPresenter.crateSettingsDialog();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void refreshData(List<Note> notes) {
        mAdapter = mPresenter.loadAdapter(notes);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void showEmptyListMessage(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        mNoDataView.setVisibility(visibility);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_noters) {

        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onDialogPositiveClick(Date date) {
        mPresenter.searchByDate(date);
    }

}
