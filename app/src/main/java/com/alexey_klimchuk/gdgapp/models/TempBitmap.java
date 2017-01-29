package com.alexey_klimchuk.gdgapp.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by alexey on 15/01/17.
 */

public class TempBitmap {

    private ArrayList<Bitmap> fullSizeImages = new ArrayList<>();

    private ArrayList<Bitmap> smallImages = new ArrayList<>();

    public ArrayList<Bitmap> getFullSizeImages() {
        return fullSizeImages;
    }

    public void setFullSizeImages(ArrayList<Bitmap> fullSizeImages) {
        this.fullSizeImages = fullSizeImages;
    }

    public ArrayList<Bitmap> getSmallImages() {
        return smallImages;
    }

    public void setSmallImages(ArrayList<Bitmap> smallImages) {
        this.smallImages = smallImages;
    }

    public void removeImage(int index) {
        fullSizeImages.remove(index);

        smallImages.remove(index);
    }

    public void addImage(Bitmap bitmap) {
        fullSizeImages.add(bitmap);

        final int maxSize = 256;
        int outWidth;
        int outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        smallImages.add(Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false));
    }

    public void clear() {
        fullSizeImages.clear();
        smallImages.clear();
    }

    public void createFromMem(ArrayList<Bitmap> bitmapsFromURIs) {
        for (Bitmap item : bitmapsFromURIs) {
            addImage(item);
        }
    }
}
