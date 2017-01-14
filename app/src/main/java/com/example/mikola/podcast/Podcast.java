package com.example.mikola.podcast;

import android.graphics.Bitmap;

/**
 * Created by mikola on 21.09.2016.
 */

public class Podcast {


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;
    private String title;
    private Bitmap image;
    private String data;
    private String sound;
    private String deck;
    private String urlImage;

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


    public Podcast(String title, Bitmap image, String data, String sound, String deck, String urlImage) {
        this.title = title;
        this.image = image;
        this.data = data;
        this.sound = sound;
        this.deck = deck;

        this.urlImage = urlImage;
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



    public String getUrlImage() {
        return urlImage;
    }

    public Podcast(String title, Bitmap image, String data) {
        this.title = title;
        this.image = image;
        this.data = data;
    }
}

