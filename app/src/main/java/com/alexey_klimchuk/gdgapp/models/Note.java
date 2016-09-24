package com.alexey_klimchuk.gdgapp.models;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Alex on 22.03.2016.
 * The model of Note.
 */
public class Note {

    private String id;
    private String name;
    private String content;
    private Date date;
    private String image;
    private Mood mMood;

    /**
     * Mood states
     */
    public enum Mood {
        GOOD,
        NORMAL,
        BAD
    }

    public Note() {
    }

    public Note(String name, String content, Date date, String image, Mood mood) {
        this.id = UUID.randomUUID().toString();// Create uncial id.
        this.name = name;
        this.content = content;
        this.date = date;
        this.image = image;
        mMood = mood;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
