package com.example.mikola.podcast.objs;

import android.graphics.Bitmap;

/**
 * Created by mikola on 21.09.2016.
 */

public class Description {

    private String linc;
    private String text;
    private String time;
    private Bitmap logo;


    public Description(Bitmap logo) {
        this.logo = logo;
    }

    public Description(String linc, String text, String time) {

        this.linc = linc;
        this.text = text;
        this.time = time;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

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


}
