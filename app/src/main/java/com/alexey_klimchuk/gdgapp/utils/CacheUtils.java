package com.alexey_klimchuk.gdgapp.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.alexey_klimchuk.gdgapp.models.TempBitmap;

/**
 * Created by Alexey on 13.11.2016.
 */

public class CacheUtils {

    public static TempBitmap tempBitmaps = new TempBitmap();
    private static LruCache<String, Bitmap> mMemoryCache;

    public static void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public static void removeBitmapFromMemCache(String key) {
        mMemoryCache.remove(key);
    }

}
