package com.example.mikola.podcast.resivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.mikola.podcast.helpers.PreferenceHelper;
import com.example.mikola.podcast.services.LoadService;
import com.example.mikola.podcast.utils.Constants;

public class StartLoadServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Constants.TAG,intent.getAction());
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            boolean isOn = PreferenceHelper.getPrefIsAlarmOn(context);
            LoadService.setServiceAlarm(context,isOn);
        }
    }
}
