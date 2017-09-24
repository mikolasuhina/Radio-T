package com.mykola.podcast.helpers;

import android.content.Context;
import android.preference.PreferenceManager;


public class PreferenceHelper {
    private static final String PREF_LAST_PODCAST_TITLE = "last_podcast_title";
    private static final String PREF_IS_ALARM_ON = "is_alarm_on";

    public static String getStoredTitle(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREF_LAST_PODCAST_TITLE, null);
    }

    public static void setStoredTitle(Context context, String title) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LAST_PODCAST_TITLE, title)
                .apply();
    }

    public static boolean getPrefIsAlarmOn(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setPrefIsAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }
}
