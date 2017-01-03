package com.example.mikola.podcast;

/**
 * Created by mikola on 21.09.2016.
 */

public class Constans {
    public static final String TITLE = "title";
    public static final String IMAGE = "img";
    public static final String DECK = "description";
    public static final String ITEM = "item_podcastst";
    public static final String URL = "url";
    public static final String MEDIA_CONTENT = "media:content";
    public static final String PUB_DATA = "pubDate";
    public static final String FILE_SIZE = "fileSize";
    public static final String SRC = "src";

    public static final String dataUrl = "http://feeds.rucast.net/radio-t";

    public interface ACTION {
        public static String MAIN_ACTION = "foregroundservice.action.main";
        public static String PREV_ACTION = "foregroundservice.action.prev";
        public static String PLAY_ACTION = "foregroundservice.action.play";
        public static String NEXT_ACTION = "foregroundservice.action.next";
        public static String STARTFOREGROUND_ACTION = "foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
