package com.alexey_klimchuk.gdgapp.utils;

import com.alexey_klimchuk.gdgapp.data.Note;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Alexey on 13.11.2016.
 */

public class CustomComparator implements Comparator<Note> {

    @Override
    public int compare(Note o1, Note o2) {
        return (new Date(o2.getDate())).compareTo(new Date(o1.getDate()));
    }
}