package com.example.mikola.podcast;

/**
 * Created by mikola on 20.11.2016.
 */
public class Sing {
    private static Sing ourInstance = new Sing();

    public static Sing getInstance() {
        return ourInstance;
    }

    private Sing() {
    }
}
