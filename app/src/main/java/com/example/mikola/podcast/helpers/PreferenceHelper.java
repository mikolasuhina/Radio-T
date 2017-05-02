package com.example.mikola.podcast.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by mykola on 08.04.17.
 */

public class PreferenceHelper {
    public static final String PREF_LAST_PODCAST_TITLE = "last_podcast_title";
    public static final String PREF_IS_ALARM_ON = "is_alarm_on";

    public static String getStoredTitle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_PODCAST_TITLE, null);
    }

    public static void setStoredTitle(Context context, String title) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_PODCAST_TITLE, title).commit();
    }

    public static boolean getPrefIsAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setPrefIsAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON, isOn).commit();
    }
}
