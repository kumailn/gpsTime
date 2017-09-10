package com.kumailn.prayertime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class prayerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String prayerName = intent.getStringExtra("Prayer");
        Log.e("PrayerReciever: ", prayerName);

        //Intent to start service
        Intent service_intent = new Intent(context, RingtonePlayingService.class);
        service_intent.putExtra("Prayer", prayerName);

        Log.e("PrayerReciever: ", "now launching service");
        //context.startService(service_intent);

    }
}
