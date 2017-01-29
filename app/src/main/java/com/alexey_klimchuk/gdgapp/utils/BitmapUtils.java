package com.alexey_klimchuk.gdgapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alexey on 27.10.2016.
 */

public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    /**
     * Resizing image from gallery to increase performance.
     */
    public static Bitmap resizeImage(Context c, Uri uri, int size)
            throws FileNotFoundException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, options);

        BitmapFactory.Options finalOptions = new BitmapFactory.Options();
        finalOptions.inSampleSize = calculateInSampleSize(options, size, size);
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

    /**
     * Creating image file.
     */
    public static String createImageFile(Bitmap bmp, boolean shouldRecycle) throws IOException {
        String extr = Environment.getExternalStorageDirectory().toString();
        File mFolder = new File(extr + "/DiaryImages");
        if (!mFolder.exists()) {
            mFolder.mkdir();
        }
        File f = new File(mFolder.getAbsolutePath(), (new Date()).getTime() + "image.png");// Create with an unique name
        FileOutputStream fos = new FileOutputStream(f);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        if (shouldRecycle)
            bmp.recycle();
        return f.getAbsolutePath();
    }

    public static void deleteImageFile(String path) {
        File imgFile = new File(path);
        if (imgFile.exists()) {
            if (imgFile.delete()) {
                Log.d(TAG, "file was deleted");
            } else {
                Log.d(TAG, "file was not deleted");
            }
        }
    }

    public static ArrayList<Bitmap> getBitmapsFromURIs(String[] images, Context context, boolean shouldResize) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        for (String image : images) {
            try {
                File file = new File(image);

                if (shouldResize) {
                    bitmaps.add(BitmapUtils.resizeImage(context, Uri.fromFile(file), 256));
                } else {
                    bitmaps.add(BitmapFactory.decodeFile(file.getAbsolutePath()));
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }
}
