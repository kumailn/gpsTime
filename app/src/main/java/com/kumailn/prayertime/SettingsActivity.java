package com.kumailn.prayertime;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    final static String versionNumber = BuildConfig.VERSION_NAME;
    static SharedPreferences preferences;
    public static final String defaultMethod = "0";
    public static final int FAJR_REQUEST_CODE = 101;
    public static final int DHUR_REQUEST_CODE = 102;
    public static final int ASR_REQUEST_CODE = 103;
    public static final int MAGHRIB_REQUEST_CODE = 104;
    public static final int ISHA_REQUEST_CODE = 105;
    public static final int TEST_REQUEST_CODE = 111;
    PendingIntent dynamicFajrPendingIntent;
    PendingIntent dynamicAsrPendingIntent;
    PendingIntent dynamicDhurPendingIntent;
    PendingIntent dynamicMaghribPendingIntent;
    PendingIntent dynamicIshaPendingIntent;

    PendingIntent dynamicTestPendingIntent;
    int PLACE_PICKER_REQUEST = 1;
    PendingIntent pendingIntent;
    PendingIntent pendingIntent2;
    PendingIntent pendingIntent3;
    PendingIntent testPendingIntent1;

    AlarmManager alarm_manager;
    private Switch mySwitch;
    private Switch fajrSwitch;
    private Switch asrSwitch;
    private Switch dhurSwitch;
    private Switch maghribSwitch;
    private Switch ishaSwitch;
    private Switch soundOnSwitch;

    private SharedPreferences.OnSharedPreferenceChangeListener spChanged;

    //Build version number
    final String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getApplicationContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        // load settings fragment

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Log.e(String.valueOf(preferences.getBoolean("key_asr_switch", false)), "h");

        //Alarm manager object initialization
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @SuppressLint("ValidFragment")
    public class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref);

            //PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_main, false);

            //Programmatically bind selected value to summary
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_calculation_method)));

            // notification preference change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_calculation_method)));

            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key)));

            // feedback preference click listener
/*
            Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });
*/


            Preference mp2 = findPreference(getString(R.string.key_home_location));
            mp2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        Log.e("place", "clicked");
                        pickPlace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        Log.e("Error", "1");
                        e.printStackTrace();
                    } catch (GooglePlayServicesRepairableException e) {
                        Log.e("Error", "2");
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            //Programmatically set the version number
            Preference mp = findPreference("key_version");
            mp.setSummary(String.valueOf(versionNumber));

            spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                            try{
                                Log.e("It worked!", key + String.valueOf(sharedPreferences.getBoolean(key, false)));
                                }catch (Exception e){Log.e("SettingsActivity err", e.toString());}
                            if(key.equals(getString(R.string.key_fajr_switch))){
                                if(sharedPreferences.getBoolean(key, false)){
                                    onAlarmSwitchClick("Fajr");
                                    Log.e(TAG, "Fajr Switch saved as on");
                                }
                                else if(!sharedPreferences.getBoolean(key, false)){
                                    try {dynamicFajrPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                                    Log.e(TAG, "Fajr saved as off");
                                }
                                //Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                //dtC.putExtra("Prayer", "Fajr").putExtra("Type", "Cancel");
                                //dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), FAJR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                            }

                            else if(key.equals(getString(R.string.key_dhur_switch))){
                                if(sharedPreferences.getBoolean(key, false)){
                                    onAlarmSwitchClick("Dhur");
                                    Log.e(TAG, "dhur Switch saved as on");
                                }
                                else {
                                    try {dynamicDhurPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                                    Log.e(TAG, "Switch saved as off");
                                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                    dtC.putExtra("Prayer", "Dhur").putExtra("Type", "Cancel");
                                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), DHUR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                                }
                            }
                            else if(key.equals(getString(R.string.key_asr_switch))){
                                if(sharedPreferences.getBoolean(key, false)){
                                    onAlarmSwitchClick("Asr");
                                    Log.e(TAG, "asr Switch saved as on");
                                }
                                else {
                                    try {dynamicAsrPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                                    Log.e(TAG, "Switch saved as off");
                                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                    dtC.putExtra("Prayer", "Asr").putExtra("Type", "Cancel");
                                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ASR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                                }
                            }

                            else if(key.equals(getString(R.string.key_maghrib_switch))){
                                if(sharedPreferences.getBoolean(key, false)){
                                    onAlarmSwitchClick("Maghrib");
                                    Log.e(TAG, "maghrib Switch saved as on");
                                }
                                else {
                                    //This tries to send a cancel intent to the service.
                                    try {dynamicMaghribPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                    dtC.putExtra("Prayer", "Maghrib").putExtra("Type", "Cancel");
                                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), MAGHRIB_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                                    Log.e(TAG, "Switch saved as off");
                                }
                            }

                            else if(key.equals(getString(R.string.key_isha_switch))){
                                if(sharedPreferences.getBoolean(key, false)){
                                    onAlarmSwitchClick("Isha");
                                    Log.e(TAG, "isha Switch saved as true");
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "Isha turned off", Toast.LENGTH_SHORT).show();
                                    try {dynamicIshaPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                    dtC.putExtra("Prayer", "Isha").putExtra("Type", "Cancel");
                                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), MAGHRIB_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                                    Log.e(TAG, "Switch saved as off");
                                    //debug purposes
                                    //Test intent for debugging purposes
                                    //Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                                    //dtC.putExtra("Prayer", "Isha").putExtra("Type", "Cancel");
                                    //dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ISHA_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                                    //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                                    Log.e(TAG, "Isha saved as off");
                                }
                            }
                }
            };

            preferences.registerOnSharedPreferenceChangeListener(spChanged);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            Log.e("Prefrence: ", preference.toString() + " " + newValue.toString());

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                Log.e("List", "ENTRY: " + index);
                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


    public void pickPlace() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName() + " " + place.getLatLng());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Email client intent to send support mail
     * Appends the necessary device information to email body
     * useful when providing support
     */
    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kumailmn@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "PrayerTime Inquiry");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }

    public void onAlarmSwitchClick(String alarmName){
        //Initialize dynamic alarm intents
        SharedPreferences sp = getSharedPreferences("com.kumailn.prayertime_preferences", MODE_PRIVATE);

        Intent dynamicFajrIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        dynamicFajrIntent.putExtra("Prayer", "Fajr").putExtra("Type", "Dynamic");

        Intent dynamicDhurIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        dynamicDhurIntent.putExtra("Prayer", "Dhur").putExtra("Type", "Dynamic");

        Intent dynamicAsrIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        dynamicAsrIntent.putExtra("Prayer", "Asr").putExtra("Type", "Dynamic");

        Intent dynamicMaghribIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        dynamicMaghribIntent.putExtra("Prayer", "Maghrib").putExtra("Type", "Dynamic");

        Intent dynamicIshaIntent = new Intent(getApplicationContext(), prayerReceiver.class);
        dynamicIshaIntent.putExtra("Prayer", "Isha").putExtra("Type", "Dynamic");

        //Initialize pending intents
        dynamicFajrPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 101, dynamicFajrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dynamicDhurPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 102, dynamicDhurIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dynamicAsrPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 103, dynamicAsrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dynamicMaghribPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 104, dynamicMaghribIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        dynamicIshaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 105, dynamicIshaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
        Boolean myB = Boolean.valueOf(sp.getBoolean(getString(R.string.key_daylight_savings_switch), false));
        double timezone = offset;

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

        //Log to logCat
        Log.e("geo: " + loadLat(), loadLon());

        //Initialize Gregorian Calendars from prayertime data
        GregorianCalendar myCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, currentHour, currentMin);
        GregorianCalendar fajrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(0).split(":")[0]), Integer.parseInt(prayerTimes.get(0).split(":")[1]));
        GregorianCalendar dhurCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(2).split(":")[0]), Integer.parseInt(prayerTimes.get(2).split(":")[1]));
        GregorianCalendar asrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(3).split(":")[0]), Integer.parseInt(prayerTimes.get(3).split(":")[1]));
        GregorianCalendar maghribCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(5).split(":")[0]), Integer.parseInt(prayerTimes.get(5).split(":")[1]));
        GregorianCalendar ishaCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(6).split(":")[0]), Integer.parseInt(prayerTimes.get(6).split(":")[1]));

        //Tomorrow
        GregorianCalendar fajrCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(0).split(":")[0]), Integer.parseInt(prayerTimes2.get(0).split(":")[1]));
        GregorianCalendar asrCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(3).split(":")[0]), Integer.parseInt(prayerTimes2.get(3).split(":")[1]));
        GregorianCalendar dhurCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(2).split(":")[0]), Integer.parseInt(prayerTimes2.get(2).split(":")[1]));
        GregorianCalendar maghribCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(5).split(":")[0]), Integer.parseInt(prayerTimes2.get(5).split(":")[1]));
        GregorianCalendar ishaCal2 = new GregorianCalendar(currentYear, currentMonth - 1, currentDay + 1, Integer.parseInt(prayerTimes2.get(6).split(":")[0]), Integer.parseInt(prayerTimes2.get(6).split(":")[1]));

        //TODO: Fix API compatibility
        switch (alarmName) {
            case "Fajr": {
                GregorianCalendar currentCal = fajrCal;
                GregorianCalendar currentCal2 = fajrCal2;
                if (System.currentTimeMillis() < fajrCal.getTimeInMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                    }
                    Log.e("FajrTodaySet:", String.valueOf(fajrCal.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal.get(Calendar.MONTH) + 1) + "/" + String.valueOf(fajrCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                    }
                    Log.e("FajrTomorrowSet:", String.valueOf(fajrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal2.get(Calendar.MONTH) + 1) + "/" + String.valueOf(fajrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal2.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "Dhur": {
                GregorianCalendar currentCal = dhurCal;
                GregorianCalendar currentCal2 = dhurCal2;
                if (System.currentTimeMillis() < dhurCal.getTimeInMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                    }
                    Log.e("DhurTodaySet:", String.valueOf(dhurCal.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal.get(Calendar.MONTH) + 1) + "/" + String.valueOf(dhurCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                    }
                    Log.e("DhurTomorrowSet:", String.valueOf(dhurCal2.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal2.get(Calendar.MONTH) + 1) + "/" + String.valueOf(dhurCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal2.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal2.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "Asr": {
                GregorianCalendar currentCal = asrCal;
                GregorianCalendar currentCal2 = asrCal2;
                if (System.currentTimeMillis() < asrCal.getTimeInMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                    }
                    Log.e("AsrTodaySet:", String.valueOf(asrCal.get(Calendar.YEAR)) + "/" + String.valueOf(asrCal.get(Calendar.MONTH) + 1) + "/" + String.valueOf(asrCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(asrCal.get(Calendar.HOUR)) + ":" + String.valueOf(asrCal.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                    }
                    Log.e("AsrTomorrowSet:", String.valueOf(asrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(asrCal2.get(Calendar.MONTH) + 1) + "/" + String.valueOf(asrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(asrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(asrCal2.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "Maghrib": {
                GregorianCalendar currentCal = maghribCal;
                GregorianCalendar currentCal2 = maghribCal2;
                if (System.currentTimeMillis() < maghribCal.getTimeInMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                    }
                    Log.e("MaghribTodaySet:", String.valueOf(maghribCal.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal.get(Calendar.MONTH) + 1) + "/" + String.valueOf(maghribCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                    }
                    Log.e("MaghribTomorrowSet:", String.valueOf(maghribCal2.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal2.get(Calendar.MONTH) + 1) + "/" + String.valueOf(maghribCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal2.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal2.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                }
                break;
            }


            //UPDATE DEBUG CHECK
            case "Isha": {
                GregorianCalendar currentCal = ishaCal;
                GregorianCalendar currentCal2 = ishaCal2;

                if (System.currentTimeMillis() < ishaCal.getTimeInMillis()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);

                        //Set in 1 min fo debug
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, dynamicIshaPendingIntent);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);
                    }
                    Log.e("IshaTodaySet:", String.valueOf(ishaCal.get(Calendar.YEAR)) + "/" + String.valueOf(ishaCal.get(Calendar.MONTH) + 1) + "/" + String.valueOf(ishaCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(ishaCal.get(Calendar.HOUR)) + ":" + String.valueOf(ishaCal.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Log.e("Isha logged ", "for tomorrow android M+");
                        //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);

                        //Set in 1 min fo debug
                        alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, dynamicIshaPendingIntent);

                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        alarm_manager.setExact(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);
                    } else {
                        alarm_manager.set(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);
                    }
                    Log.e("IshaTomorrowSet:", String.valueOf(ishaCal2.get(Calendar.YEAR)) + "/" + String.valueOf(ishaCal2.get(Calendar.MONTH) + 1) + "/" + String.valueOf(ishaCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(ishaCal2.get(Calendar.HOUR)) + ":" + String.valueOf(ishaCal2.get(Calendar.MINUTE)));
                    Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case "Test":
                GregorianCalendar todayCalendar = new GregorianCalendar();
                Log.e("The current date is ", String.valueOf(todayCalendar.get(GregorianCalendar.YEAR)) + "/" +
                        String.valueOf(todaysCalendar.get(GregorianCalendar.MONTH)) + "/" + String.valueOf(todayCalendar.get(GregorianCalendar.DAY_OF_MONTH)) +
                        " " + String.valueOf(todayCalendar.get(GregorianCalendar.HOUR)) + ":" + String.valueOf(todayCalendar.get(GregorianCalendar.MINUTE)));

                //Test intent for debugging purposes
                Intent dynamicTestIntent = new Intent(getApplicationContext(), prayerReceiver.class);
                dynamicTestIntent.putExtra("Prayer", "Test").putExtra("Type", "Dynamic");

                dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 111, dynamicTestIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                long yourmilliseconds = todayCalendar.getTimeInMillis() + 60000;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd/yyyy hh:mm");
                Date resultdate = new Date(yourmilliseconds);
                String rf = sdf.format(resultdate);

                Toast.makeText(getApplicationContext(), "Set for: " + rf, Toast.LENGTH_SHORT).show();
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, todaysCalendar.getTimeInMillis() + 60000, dynamicTestPendingIntent);
                break;
        }
    }

    //Loads longitude from sharedPrefrences
    public String loadLon(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lonTwo", defaultMethod);
        return (myMethod);
    }

    //load latitude from sharedPrefrences
    public String loadLat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("latTwo", defaultMethod);
        return (myMethod);
    }

    public int loadDat(){
        //loads saved settings data
        SharedPreferences sharedPreferences = getSharedPreferences("com.kumailn.prayertimes_preferences", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt(getString(R.string.key_calculation_method), 0);
        return (myMethod);
    }
}
