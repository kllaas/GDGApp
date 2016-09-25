package com.alexey_klimchuk.gdgapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.adapter.RecyclerAdapter;
import com.alexey_klimchuk.gdgapp.create_note.CreateNoteActivity;
import com.alexey_klimchuk.gdgapp.helpers.DatabaseHelper;
import com.alexey_klimchuk.gdgapp.models.Note;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Note> mNotes = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // initialize db
        databaseHelper = new DatabaseHelper(this, "mydatabase.db", null, 1);
        sqLiteDatabase = databaseHelper.getWritableDatabase();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadNotesFromDB();
        // specify an adapter
        mAdapter = new RecyclerAdapter(mNotes, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Loading notes from SQLite db.
     */
    private void loadNotesFromDB() {
        mNotes = databaseHelper.getAllNotes(sqLiteDatabase);
        Collections.reverse(mNotes); // Reverse ArrayList to display new items in top
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
            createDialogSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createDialogSearch() {
        LayoutInflater inflater = getLayoutInflater();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = inflater.inflate(R.layout.dialog_calendar, null);
        final MaterialCalendarView calendarView = (MaterialCalendarView) dialogView.findViewById(R.id.calendar);
        calendarView.setMaximumDate(new Date());
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.isBefore(new CalendarDay(new Date((new Date()).getTime() - 2592000)));
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_card));
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (calendarView.getSelectedDate() != null) {
                    searchByDate(calendarView.getSelectedDate().getDate());
                }
            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.setView(dialogView);

        b.show();
    }

    private void searchByDate(Date date) {
        mNotes = databaseHelper.getNotesByDate(sqLiteDatabase, date);
        Collections.reverse(mNotes);
        mAdapter = new RecyclerAdapter(mNotes, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }
}
