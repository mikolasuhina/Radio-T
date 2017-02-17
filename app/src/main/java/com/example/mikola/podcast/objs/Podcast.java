package com.example.mikola.podcast.objs;

import android.graphics.Bitmap;

import java.util.UUID;

/**
 * Created by mikola on 21.09.2016.
 */

public class Podcast {

    private UUID id;
    private String title;
    private Bitmap image;
    private String data;
    private String sound;
    private String deck;

    private boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSound() {
        return sound;
    }

    public String getDeck() {
        return deck;
    }

    public Podcast(String title, Bitmap image, String data, String sound, String deck) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.image = image;
        this.data = data;
        this.sound = sound;
        this.deck = deck;
    }

}

