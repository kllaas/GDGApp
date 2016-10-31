package com.alexey_klimchuk.gdgapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;

/**
 * Created by Alexey on 27.10.2016.
 */

public class BitmapUtils {

    /**
     * Resizing image from gallery to increase performance.
     */
    public static Bitmap resizeImage(Context c, Uri uri)
            throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, options);

        BitmapFactory.Options finalOptions = new BitmapFactory.Options();
        finalOptions.inSampleSize = calculateInSampleSize(options, 240, 240);
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, finalOptions);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
