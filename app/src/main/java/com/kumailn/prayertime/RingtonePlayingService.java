package com.kumailn.prayertime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import android.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.nearby.messages.Strategy;

import java.security.Provider;

/**
 * Created by Kumail Naqvi on 19-May-17.
 */

public class RingtonePlayingService extends Service  {
    Double mn1;
    Double mn2;
    MediaPlayer mymedia;
    int start_id;
    boolean isRunning;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        return;
    }



    public int onStartCommand(Intent intent, int flags, int startId){


        Uri alarmUri = Uri.parse("android.resource://" + "com.kumailn.prayertime/" + "raw/adhan");
        Log.e("IN", "BROADCASTRECIEVER");
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());

        if(ringtone.isPlaying()){
            ringtone.stop();
        }
        else {
            //LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);


            //googleApiClient.connect();

            Log.e("com.example.ali.gpstime", "In Ringtone Service");

            //String state = intent.getExtras().getString("extra");
            //String state2 = intent.getExtras().getString("rr");

            Log.e(String.valueOf(mn1), String.valueOf(mn2) + "GOOGLE");

            //Log.e("com.example.ali.gpstime", "STARTING SOUND NOW");


            ringtone.play();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent_main = new Intent(this.getApplicationContext(), Main2Activity.class);

            PendingIntent pendingIntentMain = PendingIntent.getActivity(this, 0, intent_main, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notificationPopup = new Notification.Builder(this).setContentTitle("Alarm is ON!").setContentText("Click here")
                    .setContentIntent(pendingIntentMain).setAutoCancel(true).setSmallIcon(R.drawable.mosque).setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL).build();
            notificationManager.notify(0, notificationPopup);
        }



        return START_STICKY;
    }
}
