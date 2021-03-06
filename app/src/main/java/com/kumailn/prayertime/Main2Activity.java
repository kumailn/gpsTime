package com.kumailn.prayertime;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "com.kumailn.prayertime";
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

    //Build version number
    final String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Toolbar setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        mySwitch = (Switch)findViewById(R.id.switch1);
        fajrSwitch = (Switch)findViewById(R.id.fajrSwitch);
        dhurSwitch = (Switch)findViewById(R.id.dhurSwitch);
        asrSwitch = (Switch)findViewById(R.id.asrSwitch);
        maghribSwitch = (Switch)findViewById(R.id.maghribSwitch);
        ishaSwitch = (Switch)findViewById(R.id.ishaSwitch);
        soundOnSwitch = (Switch) findViewById(R.id.alarmSoundSwitch);

        SharedPreferences sharedPreferences = getSharedPreferences("myData", MODE_PRIVATE);

        //tries to set switch to saved position
        try{mySwitch.setChecked(Boolean.valueOf(loadDaylight()));} catch (Exception e){}
        try{fajrSwitch.setChecked(Boolean.valueOf(loadSwitchAlarm("FajrAlarm")));} catch (Exception e){}
        try{dhurSwitch.setChecked(Boolean.valueOf(loadSwitchAlarm("DhurAlarm")));} catch (Exception e){}
        try{asrSwitch.setChecked(Boolean.valueOf(loadSwitchAlarm("AsrAlarm")));} catch (Exception e){}
        try{maghribSwitch.setChecked(Boolean.valueOf(loadSwitchAlarm("MaghribAlarm")));} catch (Exception e){}
        try{ishaSwitch.setChecked(Boolean.valueOf(loadSwitchAlarm("IshaAlarm")));} catch (Exception e){}
        try{soundOnSwitch.setChecked(sharedPreferences.getBoolean("soundOn", false));}catch(Exception e){}

        if (loadNumericInstance() == 1){
            mySwitch.setChecked(TimeZone.getDefault().inDaylightTime( new Date() ));
            saveDaylight(TimeZone.getDefault().inDaylightTime( new Date() ));
            Log.e(TAG, "Switch saved as on");
        }

        Log.e(String.valueOf(TimeZone.getDefault().inDaylightTime( new Date() )), "DAYLIGHTON?");

        //Saves state of switch
        soundOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(soundOnSwitch.isChecked()){
                    SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("soundOn", true).apply();
                    Log.e("Switch saved as: ", String.valueOf(sharedPreferences.getBoolean("soundOn", false)));
                }
                else{
                    SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putBoolean("soundOn", false).apply();
                }
            }
        });


        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mySwitch.isChecked()){
                    //Toast.makeText(Main2Activity.this, "works", Toast.LENGTH_SHORT).show();
                    saveDaylight(true);
                    Log.e(TAG, "Switch saved as on");
                    Log.wtf("Testing", "123");
                }
                else {
                    saveDaylight(false);
                    Log.e(TAG, "Switch saved as off");
                }
            }
        });

        fajrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(fajrSwitch.isChecked()){
                    saveSwitchAlarm("FajrAlarm", "true");
                    onAlarmSwitchClick("Fajr");
                    Log.e(TAG, "Fajr Switch saved as on");
                }
                else {
                    saveSwitchAlarm("FajrAlarm", "false");
                    try {dynamicFajrPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                    Log.e(TAG, "Switch saved as off");
                }
                Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                dtC.putExtra("Prayer", "Fajr").putExtra("Type", "Cancel");
                dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), FAJR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
            }
        });

        dhurSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(dhurSwitch.isChecked()){
                    saveSwitchAlarm("DhurAlarm", "true");
                    onAlarmSwitchClick("Dhur");
                    Log.e(TAG, "dhur Switch saved as on");
                }
                else {
                    saveSwitchAlarm("DhurAlarm", "false");
                    try {dynamicDhurPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                    Log.e(TAG, "Switch saved as off");
                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                    dtC.putExtra("Prayer", "Dhur").putExtra("Type", "Cancel");
                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), DHUR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                }
            }
        });

        asrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(asrSwitch.isChecked()){
                    saveSwitchAlarm("AsrAlarm", "true");
                    onAlarmSwitchClick("Asr");
                    Log.e(TAG, "asr Switch saved as on");
                }
                else {
                    saveSwitchAlarm("AsrAlarm", "false");
                    try {dynamicAsrPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                    Log.e(TAG, "Switch saved as off");
                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                    dtC.putExtra("Prayer", "Asr").putExtra("Type", "Cancel");
                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ASR_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                }
            }
        });

        maghribSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(maghribSwitch.isChecked()){
                    saveSwitchAlarm("MaghribAlarm", "true");
                    onAlarmSwitchClick("Maghrib");
                    Log.e(TAG, "maghrib Switch saved as on");
                }
                else {
                    saveSwitchAlarm("MaghribAlarm", "false");
                    try {dynamicMaghribPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}
                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                    dtC.putExtra("Prayer", "Maghrib").putExtra("Type", "Cancel");
                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), MAGHRIB_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);
                    Log.e(TAG, "Switch saved as off");
                }
            }
        });

        ishaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ishaSwitch.isChecked()){
                    saveSwitchAlarm("IshaAlarm", "true");
                    onAlarmSwitchClick("Isha");
                    Log.e(TAG, "isha Switch saved as true");
                }
                else {
                    saveSwitchAlarm("IshaAlarm", "false");
                    Toast.makeText(getApplicationContext(), "Isha turned off", Toast.LENGTH_SHORT).show();
                    try {dynamicIshaPendingIntent.cancel();} catch (Exception e) {e.printStackTrace();}


                    //debug purposes
                    //Test intent for debugging purposes
                    Intent dtC = new Intent(getApplicationContext(), prayerReceiver.class);
                    dtC.putExtra("Prayer", "Isha").putExtra("Type", "Cancel");
                    dynamicTestPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), ISHA_REQUEST_CODE, dtC, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), dynamicTestPendingIntent);


                    Log.e(TAG, "Switch saved as off");
                }
            }
        });

        Button offButton = (Button)findViewById(R.id.alarmOffButon);
        Button setHomeButton = (Button)findViewById(R.id.setHomeButton);

        //Alarm manager object initialization
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //Spinner object setup
        final Spinner mySpin = (Spinner)findViewById(R.id.myspin1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Main2Activity.this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpin.setAdapter(myAdapter);
        mySpin.setSelection(loadDat());
        mySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //Shia
                        save(0);
                        mySpin.setSelection(0);
                        break;
                    case 1:
                        //Karachi
                        save(1);
                        mySpin.setSelection(1);
                        break;
                    case 2:
                        //ISNA
                        save(2);
                        mySpin.setSelection(2);
                        break;
                    case 3:
                        //MWL
                        save(3);
                        mySpin.setSelection(3);
                        break;
                    case 4:
                        //Makkah
                        save(4);
                        mySpin.setSelection(4);
                        break;
                    case 5:
                        //Egypt
                        save(5);
                        mySpin.setSelection(5);
                        break;
                    case 6:
                        //Tehran
                        save(6);
                        mySpin.setSelection(6);
                        break;
                    case 7:
                        save(7);
                        mySpin.setSelection(7);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Save location of Home onClick (Raw coordinates)
        setHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHomeLocation(loadLat(), loadLon());
                Toast.makeText(Main2Activity.this, "Home location successfully saved", Toast.LENGTH_LONG).show();
                Log.e("HOME SET: ", loadHomeLocation().get(0) + " " + loadHomeLocation().get(1));
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e("MainActivity2", " now paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("MainActivity2", " now resumed");
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

    //Save alarm state
    public void saveAlarm(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alarm", meth);
        editor.commit();
    }

    public void saveSwitchAlarm(String alarmName, String state){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(alarmName, state);
        editor.commit();
    }

    public String loadSwitchAlarm(String alarmName){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString(alarmName, defaultMethod);
        return (myMethod);
    }

    //Method to load alarm state
    public String loadAlarm(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("alarm", defaultMethod);
        return (myMethod);
    }

    public void save(int meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("method", Integer.toString(meth));
        editor.commit();
    }

    //Method to save daylight savings option
    public void saveDaylight(boolean meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("daylight", Boolean.toString(meth));
        editor.commit();
    }

    public void showmap(View v){
        //  function to show location in google maps
        Uri gmmIntentUri = Uri.parse("geo:"+loadDat1());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public int loadDat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("method", defaultMethod);
        return Integer.parseInt(myMethod);
    }

    public String loadDaylight(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("daylight", defaultMethod);
        return (myMethod);
    }

    public int loadNumericInstance() {
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt("numTimes", 0);
        return myMethod;
    }

    public String loadDat1(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        String myMethod1 = sharedPreferences.getString("lat", defaultMethod);
        // Returns latitude, longitude
        return (myMethod1 + "," + myMethod);
    }

    public void saveHomeLocation(String meth, String meth2){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("homeLat", (meth));
        editor.putString("homeLon", (meth2));
        Log.e("HOMELOCATION-->", meth+meth2);
        editor.commit();
    }

    public ArrayList<String> loadHomeLocation(){
        ArrayList<String> homeL = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
        homeL.add(sharedPreferences.getString("homeLat", defaultMethod));
        homeL.add(sharedPreferences.getString("homeLon", defaultMethod));
        return (homeL);
    }

    public void SendLoagcatMail(){
        // save logcat in file
        File root = new File("/data/data/com.kumailn.prayertime/myData");
        try {
            Runtime.getRuntime().exec("logcat -f " + root.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //send file using email
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // Set type to "email"
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"kumailmn@gmail.com"};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, root.getAbsolutePath());
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "LOGCAT");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    public void aboutDialog(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(Main2Activity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(Main2Activity.this);
        }

        String nodata="<br/>&#8226; Version " + versionName + "<br/>&#8226; Made by Kumail Naqvi, 2017<br/>&#8226; kumailmn@gmail.com<br/>&#8226; github.com/kumailn<br/>";
        final SpannableString ss = new SpannableString(Html.fromHtml(nodata));
        Linkify.addLinks(ss, Linkify.ALL);

        //added a TextView
        final TextView tx1=new TextView(Main2Activity.this);
        tx1.setText(ss);
        tx1.setAutoLinkMask(RESULT_OK);
        tx1.setMovementMethod(LinkMovementMethod.getInstance());
        tx1.setTextSize(16);
        tx1.setTextColor(Color.WHITE);
        tx1.setPadding(48, 0, 0, 0);

        builder.setTitle("About the app")
                //.setMessage("Made by Kumail Naqvi, 2017, Version 1.5, Contact me at kumailmn@gmail.com, github.com/kumailn, powered by mXparser")
                //.setMessage(ss)
                .setView(tx1)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }

                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(Main2Activity.this, "Already on settings", Toast.LENGTH_SHORT).show();
        }
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case (R.id.action_about):
                aboutDialog();
        }
        return true;
    }

    public void onAlarmSwitchClick(String alarmName){
        //Initialize dynamic alarm intents

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
        Boolean myB = Boolean.valueOf(loadDaylight());
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
        if(alarmName.equals("Fajr")){
            GregorianCalendar currentCal = fajrCal;
            GregorianCalendar currentCal2 = fajrCal2;
            if(System.currentTimeMillis() < fajrCal.getTimeInMillis()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                Log.e("FajrTodaySet:",String.valueOf(fajrCal.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal.get(Calendar.MONTH)+1)  + "/" +String.valueOf(fajrCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
            else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, fajrCal2.getTimeInMillis(), dynamicFajrPendingIntent);
                }
                Log.e("FajrTomorrowSet:",String.valueOf(fajrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(fajrCal2.get(Calendar.MONTH)+1)  + "/" +String.valueOf(fajrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(fajrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(fajrCal2.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
        }

        else if(alarmName.equals("Dhur")){
            GregorianCalendar currentCal = dhurCal;
            GregorianCalendar currentCal2 = dhurCal2;
            if(System.currentTimeMillis() < dhurCal.getTimeInMillis()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                Log.e("DhurTodaySet:",String.valueOf(dhurCal.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
            else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, dhurCal2.getTimeInMillis(), dynamicDhurPendingIntent);
                }
                Log.e("DhurTomorrowSet:",String.valueOf(dhurCal2.get(Calendar.YEAR)) + "/" + String.valueOf(dhurCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(dhurCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(dhurCal2.get(Calendar.HOUR)) + ":" + String.valueOf(dhurCal2.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
        }

        else if(alarmName.equals("Asr")){
            GregorianCalendar currentCal = asrCal;
            GregorianCalendar currentCal2 = asrCal2;
            if(System.currentTimeMillis() < asrCal.getTimeInMillis()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, asrCal.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                Log.e("AsrTodaySet:",String.valueOf(asrCal.get(Calendar.YEAR)) + "/" + String.valueOf(asrCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(asrCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(asrCal.get(Calendar.HOUR)) + ":" + String.valueOf(asrCal.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
            else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, asrCal2.getTimeInMillis(), dynamicAsrPendingIntent);
                }
                Log.e("AsrTomorrowSet:",String.valueOf(asrCal2.get(Calendar.YEAR)) + "/" + String.valueOf(asrCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(asrCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(asrCal2.get(Calendar.HOUR)) + ":" + String.valueOf(asrCal2.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
        }

        else if(alarmName.equals("Maghrib")){
            GregorianCalendar currentCal = maghribCal;
            GregorianCalendar currentCal2 = maghribCal2;
            if(System.currentTimeMillis() < maghribCal.getTimeInMillis()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                Log.e("MaghribTodaySet:",String.valueOf(maghribCal.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
            else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, maghribCal2.getTimeInMillis(), dynamicMaghribPendingIntent);
                }
                Log.e("MaghribTomorrowSet:",String.valueOf(maghribCal2.get(Calendar.YEAR)) + "/" + String.valueOf(maghribCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(maghribCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(maghribCal2.get(Calendar.HOUR)) + ":" + String.valueOf(maghribCal2.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
        }


        //UPDATE DEBUG CHECK
        else if(alarmName.equals("Isha")){
            GregorianCalendar currentCal = ishaCal;
            GregorianCalendar currentCal2 = ishaCal2;

            if(System.currentTimeMillis() < ishaCal.getTimeInMillis()){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                   //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);

                    //Set in 1 min fo debug
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, dynamicIshaPendingIntent);
                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, ishaCal.getTimeInMillis(), dynamicIshaPendingIntent);
                }
                Log.e("IshaTodaySet:",String.valueOf(ishaCal.get(Calendar.YEAR)) + "/" + String.valueOf(ishaCal.get(Calendar.MONTH)+1) + "/" +String.valueOf(ishaCal.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(ishaCal.get(Calendar.HOUR)) + ":" + String.valueOf(ishaCal.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Today: " + String.valueOf(currentCal.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }
            else{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Log.e("Isha logged ", "for tomorrow android M+");
                    //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);

                    //Set in 1 min fo debug
                    alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, dynamicIshaPendingIntent);

                }
                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                    alarm_manager.setExact(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);
                }
                else{
                    alarm_manager.set(AlarmManager.RTC_WAKEUP, ishaCal2.getTimeInMillis(), dynamicIshaPendingIntent);
                }
                Log.e("IshaTomorrowSet:",String.valueOf(ishaCal2.get(Calendar.YEAR)) + "/" + String.valueOf(ishaCal2.get(Calendar.MONTH)+1) + "/" +String.valueOf(ishaCal2.get(Calendar.DAY_OF_MONTH)) + " " + String.valueOf(ishaCal2.get(Calendar.HOUR)) + ":" + String.valueOf(ishaCal2.get(Calendar.MINUTE)));
                Toast.makeText(getApplicationContext(), "Tomorrow: " + String.valueOf(currentCal2.get(Calendar.HOUR)) + ":" + String.valueOf(currentCal2.get(Calendar.MINUTE)), Toast.LENGTH_SHORT).show();
            }

        }
        else if(alarmName.equals("Test")){
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
        }


    }

    public void cancelDegubPendingIntent(Boolean b, int rq){
        if(b){
            Intent myIntent = new Intent(getApplicationContext(), Main2Activity.class);
            PendingIntent mpendingIntent = PendingIntent.getActivity(getApplicationContext(), rq, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarm_manager.cancel(mpendingIntent);
        }
    }


}