package com.alexey_klimchuk.gdgapp.data;

import android.content.res.Resources;

import com.alexey_klimchuk.gdgapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Alex on 22.03.2016.
 * The model of Note.
 */
public class Note {

    private String id = "";
    private String name = "";
    private String content = "";
    private long date;
    private ArrayList<String> image;
    private ArrayList<String> localImage;
    private Mood mMood;

    public Note(String id, String name, String content, Date date, ArrayList<String> image, Mood mMood) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date.getTime();
        this.image = image;
        this.mMood = mMood;
    }

    public Note(String name, String content, Date date, Mood mMood) {
        this.name = name;
        this.content = content;
        this.date = date.getTime();
        this.mMood = mMood;
    }

    public Note() {
    }

    public Note(String name, String content, Date date, ArrayList<String> image, Mood mood) {
        this.name = name;
        this.content = content;
        this.date = date.getTime();
        this.image = image;
        mMood = mood;
    }

    public Note(String id, String name, String content, Date date, ArrayList<String> image, ArrayList<String> localImage, Mood mMood) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.date = date.getTime();
        this.image = image;
        this.localImage = localImage;
        this.mMood = mMood;
    }

    public ArrayList<String> getLocalImage() {
        return localImage;
    }

    public void setLocalImage(ArrayList<String> localImage) {
        this.localImage = localImage;
    }

    public void setUnicalId() {
        this.id = UUID.randomUUID().toString();// Create uncial id.
    }

    public Mood getMood() {
        return mMood;
    }

    public void setMood(Mood mood) {
        mMood = mood;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public void setRandomId() {
        id = UUID.randomUUID().toString();
    }

    /**
     * Mood states
     */
    public enum Mood {

        GOOD,
        NORMAL,
        BAD;

        @Override
        public String toString() {
            switch (this) {
                case GOOD:
                    return Resources.getSystem().getString(R.string.mood_good);
                case NORMAL:
                    return Resources.getSystem().getString(R.string.mood_normal);
                case BAD:
                    return Resources.getSystem().getString(R.string.mood_bad);
                default:
                    return "";
            }
        }
    }

}
