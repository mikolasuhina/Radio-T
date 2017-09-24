package com.mykola.podcast.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mykola.podcast.helpers.PreferenceHelper;
import com.mykola.podcast.services.NotificationService;

public class StartLoadServiceReceiver extends BroadcastReceiver {

    private  final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            boolean isOn = PreferenceHelper.getPrefIsAlarmOn(context);
            NotificationService.setServiceAlarm(context,isOn);
        }
    }
}
