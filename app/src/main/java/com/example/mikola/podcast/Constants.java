package com.example.mikola.podcast;

/**
 * Created by mikola on 21.09.2016.
 */

public class Constants {
    public static final String TAG = "TAG";
    public static final String TITLE = "title";
    public static final String IMAGE = "img";
    public static final String DECK = "description";
    public static final String ITEM = "item";
    public static final String URL = "url";
    public static final String MEDIA_CONTENT = "media:content";
    public static final String PUB_DATA = "pubDate";
    public static final String SRC = "src";

    public static final int SEND_DATA = 1;
    public static final int SEND_DURATION = 2;
    public static final String ACTION_TYPE = "type";
    public static final String BROADCAST_ACTION = "SEND_DATA_ACTION";

    public static final String dataUrl = "http://feeds.rucast.net/radio-t";

    public interface ACTION {
        public static String MAIN_ACTION = "foregroundservice.action.main";
        public static String PLAY_ACTION = "foregroundservice.action.icon_play";
        public static String STARTFOREGROUND_ACTION = "foregroundservice.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
