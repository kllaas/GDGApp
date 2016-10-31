package com.alexey_klimchuk.gdgapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Alexey on 31.10.2016.
 */

public class ToastUtils {

    public static void showMessage(int message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
