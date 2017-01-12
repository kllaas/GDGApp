package com.alexey_klimchuk.gdgapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * Created by Alexey on 14.11.2016.
 */

public class ImageResizer extends AsyncTask<Void, Void, Bitmap> {

    private int size;
    private Uri uri;

    private ImageView imageView;
    private String bitmapId;

    private boolean shouldCache;

    private Context mContext;

    public ImageResizer(Context mContext, Uri uri, int size, ImageView imageView, String bitmapId, boolean shouldCache) {
        this.mContext = mContext;
        this.uri = uri;
        this.size = size;
        this.imageView = imageView;
        this.bitmapId = bitmapId;
        this.shouldCache = shouldCache;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.resizeImage(mContext, uri, size);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (shouldCache)
            CacheUtils.addBitmapToMemoryCache(bitmapId, bitmap);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            AlphaAnimation animation = new AlphaAnimation(0f, 1);
            animation.setDuration(400);
            imageView.startAnimation(animation);
            imageView.setImageBitmap(bitmap);
        }
        super.onPostExecute(bitmap);
    }
}