package com.kumailn.prayertime;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
    public static final String defaultMethod = "0";
    AlarmManager alarm_manager;
    PendingIntent pendingIntent;

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
        String prayerName = intent.getStringExtra("Prayer");

        //Initalize date format
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/HH/mm");
        Calendar cal2 = Calendar.getInstance();
        String myday = (dateFormat.format(cal2.getTime()));

        //Current Time variables
        int currentDay = Integer.parseInt(myday.split("/")[0]);
        int currentMonth = Integer.parseInt(myday.split("/")[1]);
        int currentYear = Integer.parseInt(myday.split("/")[2]);
        int currentHour = Integer.parseInt(myday.split("/")[3]);
        int currentMin = Integer.parseInt(myday.split("/")[4]);

        TimeZone tz1 = TimeZone.getDefault();
        int offset = tz1.getRawOffset()/1000/60/60;

        //Load longitute and latitute from saved data
        double latitude = Double.parseDouble(loadLat());
        double longitude = Double.parseDouble(loadLon());

        //Load daylight-savings
        Boolean myB = Boolean.valueOf(loadDaylight());
        double timezone = offset;

        saveServiceStartNumber(loadServiceStartNumber() + 1);

        //Adjust timezone based on daylight savings
        if(myB == null){
            timezone = offset;
        }
        else if(myB == false){
            timezone = offset;
        }
        else{
            timezone = timezone + 1;
        }

        //Initialize praytime object
        PrayTime prayers = new PrayTime();
        prayers.setTimeFormat(prayers.Time24);
        prayers.setCalcMethod(loadDat());
        prayers.setAsrJuristic(prayers.Shafii);
        prayers.setAdjustHighLats(prayers.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
        prayers.tune(offsets);

        Date now = new Date();
        Calendar todaysCalendar = Calendar.getInstance();
        Calendar tomorrowCalendar = Calendar.getInstance();
        tomorrowCalendar.set(currentYear, currentMonth - 1, currentDay + 1);
        todaysCalendar.setTime(now);

        //List of prayertimes for today
        ArrayList<String> prayerTimes = prayers.getPrayerTimes(todaysCalendar, latitude, longitude, timezone);
        //Prayer times for tomorrow
        ArrayList<String> prayerTimes2 = prayers.getPrayerTimes(tomorrowCalendar, latitude, longitude, timezone);
        //0 = Fajr, 1 = Sunrise, 2 = Dhur, 3 = Asr, 4 = Sunset, 5 = Maghrib, 6 = Isha
        ArrayList<String> prayerNames = prayers.getTimeNames();
        //Initialize Gregorian Calendars from prayertime data
        GregorianCalendar myCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, currentHour, currentMin);
        GregorianCalendar fajrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(0).split(":")[0]), Integer.parseInt(prayerTimes.get(0).split(":")[1]));
        GregorianCalendar dhurCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(2).split(":")[0]), Integer.parseInt(prayerTimes.get(2).split(":")[1]));
        GregorianCalendar asrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(3).split(":")[0]), Integer.parseInt(prayerTimes.get(3).split(":")[1]));
        GregorianCalendar maghribCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(5).split(":")[0]), Integer.parseInt(prayerTimes.get(5).split(":")[1]));
        GregorianCalendar ishaCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(6).split(":")[0]), Integer.parseInt(prayerTimes.get(6).split(":")[1]));
        //Tomorrow
        GregorianCalendar fajrCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(0).split(":")[0]), Integer.parseInt(prayerTimes2.get(0).split(":")[1]));
        GregorianCalendar dhurCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(2).split(":")[0]), Integer.parseInt(prayerTimes2.get(2).split(":")[1]));
        GregorianCalendar asrCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes.get(3).split(":")[0]), Integer.parseInt(prayerTimes.get(3).split(":")[1]));
        GregorianCalendar maghribCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(5).split(":")[0]), Integer.parseInt(prayerTimes2.get(5).split(":")[1]));
        GregorianCalendar ishaCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes.get(6).split(":")[0]), Integer.parseInt(prayerTimes.get(6).split(":")[1]));

        //Initialize URI location of audio file
        //Uri alarmUri = Uri.parse("android.resource://" + "com.kumailn.prayertime/" + "raw/sms");
        Uri alarmUri = Uri.parse("android.resource://" + "com.kumailn.prayertime/" + "raw/adhan7");
        //Error catch if uri is null, set audio to default
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        //Initialize ringtone from uri location
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        //Set ringtone type to alarm
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build());
        }
        else{
            ringtone.setStreamType(AudioManager.STREAM_ALARM);
        }

        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent nexTimeIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        nexTimeIntent.putExtra("Prayer", prayerName);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, nexTimeIntent, 0);

        if(prayerName.equals("Fajr")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(),  pendingIntent);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(),  pendingIntent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(),  pendingIntent);
            }
            Log.e("ServiceFajrSet:",String.valueOf(fajrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(fajrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal2.get(Calendar.MINUTE)));
        }

        else if(prayerName.equals("Asr")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(),  pendingIntent);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(),  pendingIntent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(),  pendingIntent);
            }
            Log.e("ServiceAsrSet:",String.valueOf(asrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(asrCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(asrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(asrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(asrCal2.get(Calendar.MINUTE)));
        }

        else if (prayerName.equals("Dhur")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(),  pendingIntent);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(),  pendingIntent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(),  pendingIntent);
            }
            Log.e("ServiceDhurSet:",String.valueOf(dhurCal2.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal2.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal2.get(Calendar.MINUTE)));
        }
        else if(prayerName.equals("Maghrib")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(),  pendingIntent);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(),  pendingIntent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(),  pendingIntent);
            }
            Log.e("ServiceDhurSet:",String.valueOf(maghribCal2.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal2.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal2.get(Calendar.MINUTE)));
        }
        else if(prayerName.equals("Isha")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(),  pendingIntent);
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(),  pendingIntent);
            }
            else{
                alarm_manager.set(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(),  pendingIntent);
            }
            Log.e("ServiceIshaSet:",String.valueOf(ishaCal2.get(Calendar.YEAR)) + "/" + String.valueOf(ishaCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(ishaCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(ishaCal2.get(Calendar.HOUR)) + ":" + String.valueOf(ishaCal2.get(Calendar.MINUTE)));
        }

        if(ringtone.isPlaying()){
            ringtone.stop();
        }
        else {
            //LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            //googleApiClient.connect();
            Log.e("ringtoneService: ", "In Ringtone Service");
            //Log.e(String.valueOf(mn1), String.valueOf(mn2) + "GOOGLE");
            //Log.e("com.example.ali.gpstime", "STARTING SOUND NOW");

            ringtone.play();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent_main = new Intent(this.getApplicationContext(), Main2Activity.class);

            PendingIntent pendingIntentMain = PendingIntent.getActivity(this, 0, intent_main, PendingIntent.FLAG_CANCEL_CURRENT);
            Notification notificationPopup = new Notification.Builder(this).setContentTitle("It's " + prayerName + " time!").setContentText("Click here")
                    .setContentIntent(pendingIntentMain).setAutoCancel(true).setSmallIcon(R.drawable.mosque).setPriority(Notification.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL).build();
            notificationManager.notify(0, notificationPopup);
        }

        return START_STICKY;
    }

    public int loadDat(){
        //Method to load calculation method
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("method", defaultMethod);
        return Integer.parseInt(myMethod);
    }
    public String loadLon(){
        //Method to load longitute data
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        return (myMethod);
    }

    public String loadLat(){
        //Method to load latitute data
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
        return (myMethod);
    }

    public void saveAlarm(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alarm", meth);
        editor.commit();
    }

    public String loadDaylight(){
        //Method to load daylight savings state
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("daylight", defaultMethod);
        return (myMethod);
    }

    public String loadAlarm(){
        //Method to load current Alarm state
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("alarm", defaultMethod);
        return (myMethod);
    }

    public void saveAlarmTimeDebug(String alarmName, String setTime){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("debugTimes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(alarmName + String.valueOf(loadServiceStartNumber()), setTime);
        editor.commit();
    }

    public void saveServiceStartNumber(int meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("serviceNumber", meth);
        editor.commit();
    }

    public int loadServiceStartNumber() {
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt("serviceNumber", 0);
        return myMethod;
    }



}
