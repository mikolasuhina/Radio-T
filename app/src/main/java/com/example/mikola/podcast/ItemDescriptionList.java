package com.example.mikola.podcast;

/**
 * Created by mikola on 21.09.2016.
 */

public class ItemDescriptionList {

    String linc;
    String text;
    String time;

    public String getLinc() {
        return linc;
    }

    public void setLinc(String linc) {
        this.linc = linc;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ItemDescriptionList(String linc, String text, String time) {

        this.linc = linc;
        this.text = text;
        this.time = time;
    }
}
