package com.mykola.podcast.models;

public class Description {

    private String linc;
    private String text;
    private String time;


    public Description(String linc, String text, String time) {
        this.linc = linc;
        this.text = text;
        this.time = time;
    }

    public String getLinc() {
        return linc;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

}
