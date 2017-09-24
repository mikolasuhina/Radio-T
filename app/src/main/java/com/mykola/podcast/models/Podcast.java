package com.mykola.podcast.models;

public class Podcast {

    private String title;
    private String image;
    private String date;
    private String sound;
    private String description;
    private String link;

    public Podcast(String title, String image, String date, String sound, String description,String link) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.sound = sound;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getSound() {
        return sound;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Podcast))
            return false;

        if (title.equals(((Podcast) obj).getTitle()))
            return true;

            return super.equals(obj);
    }
}

