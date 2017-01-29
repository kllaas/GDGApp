package com.alexey_klimchuk.gdgapp;

/**
 * Created by Alexey on 24.09.2016.
 */

public class Constants {

    public static final String EXTRA_NOTE_ID = "NOTE_ID";
    public static final String ARGUMENT_EDIT_NOTE_ID = "NOTE_EDIT_ID";
    public static final int PERMISSIONS_REQUEST_INTERNET = 1;
    public static final int MAX_IMAGES_COUNT = 5;
    public static String DB_URL = "https://gdgapp-2d5ae.firebaseio.com/";

    public class Firebase {
        public static final String USERS_DB_URL = "gs://gdgapp-2d5ae.appspot.com";
        public static final String IMAGES_FOLDER = "image";
        public static final String NOTES_FOLDER = "notes";
        public static final String USERS_FOLDER = "users";
        public static final String DATE_FIELD = "date";
    }
}
