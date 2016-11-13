package com.alexey_klimchuk.gdgapp.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alex on 24.03.2016.
 * Class to convert Date to String (String to Date) in specify format
 */
public class DateUtils {

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

    public static String getStartDayDate(Date searchDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(searchDate); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return String.valueOf(cal.getTime().getTime());
    }

    public static String getEndDayDate(Date searchDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(searchDate); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return String.valueOf(cal.getTime().getTime());
    }
}
