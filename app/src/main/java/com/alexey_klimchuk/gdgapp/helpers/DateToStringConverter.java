package com.alexey_klimchuk.gdgapp.helpers;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alex on 24.03.2016.
 * Class to convert Date to String (String to Date) in specify format
 */
public class DateToStringConverter {

    private static final String TAG = "mDateStringConverter";

    public static String convertDateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM, dd, yyyy ", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static Date convertStringToDate(String string) {
        DateFormat format = new SimpleDateFormat("MMMM, dd, yyyy", Locale.UK);
        try {
            return format.parse(string);
        } catch (ParseException e) {
            Log.d(TAG, "Date conversation error: " + e.getMessage());
            return new Date();
        }
    }
}
