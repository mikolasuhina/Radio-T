package com.example.mikola.podcast;

import android.graphics.Bitmap;

/**
 * Created by mikola on 21.09.2016.
 */

public class PodcastItem {


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
    private int lSound;
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

    public int getlSound() {
        return lSound;
    }

    public void setlSound(int lSound) {
        this.lSound = lSound;
    }

    public PodcastItem(String title, Bitmap image, String data, String sound, String deck, int lSound, String urlImage) {
        this.title = title;
        this.image = image;
        this.data = data;
        this.sound = sound;
        this.deck = deck;
        this.lSound = lSound;
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

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getDeck() {
        return deck;
    }

    public void setDeck(String deck) {
        this.deck = deck;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public PodcastItem(String title, Bitmap image, String data) {
        this.title = title;
        this.image = image;
        this.data = data;
    }
}

