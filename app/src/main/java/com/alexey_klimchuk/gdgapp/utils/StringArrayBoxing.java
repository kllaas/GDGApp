package com.alexey_klimchuk.gdgapp.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by alexey on 16/02/17.
 */

public class StringArrayBoxing {

    private static String strSeparator = "__,__";

    public static String convertArrayToString(ArrayList<String> array) {
        if (array == null) {
            return "";
        }
        String str = "";
        for (int i = 0; i < array.size(); i++) {
            str = str + array.get(i);
            // Do not append comma a the end of last element
            if (i < array.size() - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

    public static ArrayList<String> convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return new ArrayList<String>(Arrays.asList(arr));
    }

}
