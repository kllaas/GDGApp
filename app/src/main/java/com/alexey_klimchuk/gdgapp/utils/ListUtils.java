package com.alexey_klimchuk.gdgapp.utils;

import java.util.List;

/**
 * Created by alexey on 15/01/17.
 */

public class ListUtils {

    public static String[] stringListToArray(List<String> images) {
        return images.toArray(new String[images.size()]);
    }

}
