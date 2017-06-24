package com.kumailn.prayertime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Kumail on 19-May-17.
 */

public class Alarm_Receiver extends BroadcastReceiver {

    MediaPlayer mymedia;
    private AudioManager myAudioManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("com.example.ali.gpstime", "In Receiver");

        //String get_string = intent.getExtras().getString("extra");
        //String get_string2 = intent.getExtras().getString("rr");
        //Log.e("My key is: ", get_string);

        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        //Intent service_intent = new Intent(context, RingtonePlayingService.class);
        //service_intent.putExtra("extra", get_string);
        //service_intent.putExtra("rr", get_string2);


        //context.startService(service_intent);

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
        //Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri alarmUri = Uri.parse("android.resource://" + "com.example.ali.gpstime/" + "raw/adhan");
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        //Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);

        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());

        ringtone.play();
        Log.e("alarmapp", "Ringone now playing");


        //mymedia = MediaPlayer.create(context, R.raw.sms);
        //mymedia.setAudioStreamType(AudioManager.STREAM_ALARM);


        context.startService(service_intent);
    }
}
