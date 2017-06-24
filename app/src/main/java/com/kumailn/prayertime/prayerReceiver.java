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
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        /*Uri alarmUri = Uri.parse("android.resource://" + "com.kumailn.prayertime/" + "raw/adhan");
        Log.e("IN", "BROADCASTRECIEVER");
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        ringtone.play();*/
        Log.e("alarmapp", "Ringone now playing");
        context.startService(service_intent);

    }
}
