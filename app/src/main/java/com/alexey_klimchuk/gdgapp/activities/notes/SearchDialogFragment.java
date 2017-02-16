package com.alexey_klimchuk.gdgapp.activities.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.alexey_klimchuk.gdgapp.R;
import com.alexey_klimchuk.gdgapp.data.Note;
import com.alexey_klimchuk.gdgapp.data.source.NotesRepository;
import com.alexey_klimchuk.gdgapp.utils.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 13.11.2016.
 */

public class SearchDialogFragment extends DialogFragment {

    SearchDialogListener mListener;
    MaterialCalendarView calendarView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.dialog_search, null);
        calendarView = (MaterialCalendarView) v.findViewById(R.id.calendar);

        if (NotesRepository.getCachedNotesList().size() > 0)
            calendarView.addDecorator(new EventDecorator(getActivity().getResources().getColor((R.color.colorPrimary)),
                    getCalendarDays(NotesRepository.getCachedNotesList())));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v).
                setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (calendarView.getSelectedDate() != null) {
                            mListener.onDialogPositiveClick(calendarView.getSelectedDate().getDate());
                        } else {
                            Toast.makeText(getContext(), R.string.should_select_date_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SearchDialogFragment.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    /**
     * Convert List<Note> to List<CalendarDay>
     */
    private List<CalendarDay> getCalendarDays(List<Note> cachedNotes) {
        List<CalendarDay> list = new ArrayList<>();
        for (Note n : cachedNotes) {
            list.add(new CalendarDay(new Date(n.getDate())));
        }
        return list;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NotesActivity) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface SearchDialogListener {
        void onDialogPositiveClick(Date date);
    }

}