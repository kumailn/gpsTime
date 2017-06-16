package com.kumailn.prayertime;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class Main2Activity extends AppCompatActivity {
    public static final String TAG = "com.example.ali.gpstime";
    public static final String defaultMethod = "0";
    PendingIntent pendingIntent;
    PendingIntent pendingIntent2;
    PendingIntent pendingIntent3;
    PendingIntent pendingIntent4;
    PendingIntent pendingIntent5;
    PendingIntent fajr2Pending;
    PendingIntent fajr3Pending;
    PendingIntent fajr4Pending;
    PendingIntent fajr5Pending;
    PendingIntent fajr6Pending;
    PendingIntent fajr7Pending;
    PendingIntent dhur2Pending;
    PendingIntent dhur3Pending;
    PendingIntent dhur4Pending;
    PendingIntent dhur5Pending;
    PendingIntent dhur6Pending;
    PendingIntent dhur7Pending;
    PendingIntent isha2Pending;
    PendingIntent isha3Pending;
    PendingIntent isha4Pending;
    PendingIntent isha5Pending;
    PendingIntent isha6Pending;
    PendingIntent isha7Pending;





    AlarmManager alarm_manager;
    private Switch mySwitch;

    public static boolean aSent = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Toolbar setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        mySwitch = (Switch)findViewById(R.id.switch1);

        try{
            //tries to set switch to saved position
            mySwitch.setChecked(Boolean.valueOf(loadDaylight()));
        }
        catch (Exception e){

        }
        //Saves state of switch
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mySwitch.isChecked()){
                    //Toast.makeText(Main2Activity.this, "works", Toast.LENGTH_SHORT).show();
                    saveDaylight(true);
                    Log.e(TAG, "Switch saved as on");
                }
                else {
                    saveDaylight(false);
                    Log.e(TAG, "Switch saved as off");
                }
            }
        });

        Button aB = (Button)findViewById(R.id.alarmButton);
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

        setHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHomeLocation(loadLat(), loadLon());
                Toast.makeText(Main2Activity.this, "Home location successfully saved", Toast.LENGTH_LONG).show();
                Log.i("HOME SET: ", loadHomeLocation().get(0) + " " + loadHomeLocation().get(1));
            }
        });
        //Alarm On onclick listner
        aB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String alarmState = loadAlarm();
                if(alarmState == "on"){
                    return;
                }
                else if(alarmState == null){
                    return;
                }
                Intent i2 = new Intent(Main2Activity.this, Alarm_Reciever.class);
                i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                i2.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                Intent i3 = new Intent(Main2Activity.this, Alarm_Reciever.class);
                pendingIntent = PendingIntent.getBroadcast(Main2Activity.this, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);

                pendingIntent2 = PendingIntent.getBroadcast(Main2Activity.this, 1, i3, PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent3 = PendingIntent.getBroadcast(Main2Activity.this, 2, i2, 0);
                pendingIntent4 = PendingIntent.getBroadcast(Main2Activity.this, 3, i2, 0);
                pendingIntent5 = PendingIntent.getBroadcast(Main2Activity.this, 4, i2, 0);
                fajr2Pending = PendingIntent.getBroadcast(Main2Activity.this, 5, i2, 0);
                fajr3Pending = PendingIntent.getBroadcast(Main2Activity.this, 6, i2, 0);
                fajr4Pending = PendingIntent.getBroadcast(Main2Activity.this, 7, i2, 0);
                fajr5Pending = PendingIntent.getBroadcast(Main2Activity.this, 8, i2, 0);
                fajr6Pending = PendingIntent.getBroadcast(Main2Activity.this, 9, i2, 0);
                fajr7Pending = PendingIntent.getBroadcast(Main2Activity.this, 10, i2, 0);
                dhur2Pending = PendingIntent.getBroadcast(Main2Activity.this, 11, i2, 0);
                dhur3Pending = PendingIntent.getBroadcast(Main2Activity.this, 12, i2, 0);
                dhur4Pending = PendingIntent.getBroadcast(Main2Activity.this, 13, i2, 0);
                dhur5Pending = PendingIntent.getBroadcast(Main2Activity.this, 14, i2, 0);
                dhur6Pending = PendingIntent.getBroadcast(Main2Activity.this, 15, i2, 0);
                dhur7Pending = PendingIntent.getBroadcast(Main2Activity.this, 16, i2, 0);
                isha2Pending = PendingIntent.getBroadcast(Main2Activity.this, 17, i2, 0);
                isha3Pending = PendingIntent.getBroadcast(Main2Activity.this, 18, i2, 0);
                isha4Pending = PendingIntent.getBroadcast(Main2Activity.this, 19, i2, 0);
                isha5Pending = PendingIntent.getBroadcast(Main2Activity.this, 20, i2, 0);
                isha6Pending = PendingIntent.getBroadcast(Main2Activity.this, 21, i2, 0);
                isha7Pending = PendingIntent.getBroadcast(Main2Activity.this, 22, i2, 0);

                //i2.putExtra("extra", "alarm on");
                //i2.putExtra("rr", "yes");
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


                double latitude = Double.parseDouble(loadLon());
                double longitude = Double.parseDouble(loadLat());


                Boolean myB = Boolean.valueOf(loadDaylight());
                double timezone = offset;

                if(myB == null){
                    timezone = offset;
                }
                else if(myB == false){
                    timezone = offset;
                }
                else{
                    timezone = timezone + 1;
                }


                PrayTime prayers = new PrayTime();

                prayers.setTimeFormat(prayers.Time24);
                prayers.setCalcMethod(loadDat());
                prayers.setAsrJuristic(prayers.Shafii);
                prayers.setAdjustHighLats(prayers.AngleBased);
                int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                Calendar cal22 = Calendar.getInstance();
                cal22.set(currentYear, currentMonth - 1, currentDay + 1);

                Calendar cal3 = Calendar.getInstance();
                cal3.set(currentYear, currentMonth - 1, currentDay + 2);

                Calendar cal4 = Calendar.getInstance();
                cal4.set(currentYear, currentMonth - 1, currentDay + 3);

                Calendar cal5 = Calendar.getInstance();
                cal5.set(currentYear, currentMonth - 1, currentDay + 4);

                Calendar cal6 = Calendar.getInstance();
                cal6.set(currentYear, currentMonth - 1, currentDay + 5);

                Calendar cal7 = Calendar.getInstance();
                cal7.set(currentYear, currentMonth - 1, currentDay + 6);

                cal.setTime(now);

                //List of prayertimes for today
                ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                        latitude, longitude, timezone);
                //Prayer times for tomorrow
                ArrayList<String> prayerTimes2 = prayers.getPrayerTimes(cal22,
                        latitude, longitude, timezone);
                //Prayer times for 2 days from now
                ArrayList<String> prayerTimes3 = prayers.getPrayerTimes(cal3,
                        latitude, longitude, timezone);
                //Three days from now
                ArrayList<String> prayerTimes4 = prayers.getPrayerTimes(cal4,
                        latitude, longitude, timezone);
                //Four days from now
                ArrayList<String> prayerTimes5 = prayers.getPrayerTimes(cal5,
                        latitude, longitude, timezone);
                //Five days from now
                ArrayList<String> prayerTimes6 = prayers.getPrayerTimes(cal6,
                        latitude, longitude, timezone);
                //A week from now
                ArrayList<String> prayerTimes7 = prayers.getPrayerTimes(cal7,
                        latitude, longitude, timezone);


                ArrayList<String> prayerNames = prayers.getTimeNames();


                Log.e("geo: " + loadLat(), loadLon());
                Log.e(prayerTimes.get(6) + "today", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes2.get(6) + "tom", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes3.get(6) + "tom2", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes4.get(6) + "tom3", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes5.get(6) + "tom4", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes6.get(6) + "tom5", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes7.get(6) + "tom6", Integer.toString(loadDat()) + prayerNames.get(6));
                Log.e(prayerTimes.get(6) + "tom7", Integer.toString(loadDat()) + prayerNames.get(6));
                //Log.e(prayerTimes.get(5), Integer.toString(loadDat()) + prayerNames.get(5));
                //Log.e(prayerTimes.get(4), Integer.toString(loadDat()) + prayerNames.get(4));
                //Log.e(prayerTimes.get(3), Integer.toString(loadDat()) + prayerNames.get(3));
                //Log.e(prayerTimes.get(2), Integer.toString(loadDat()) + prayerNames.get(2));
                //Log.e(prayerTimes.get(1), Integer.toString(loadDat()) + prayerNames.get(1));
                //Log.e(prayerTimes.get(0), Integer.toString(loadDat()) + prayerNames.get(0));


                GregorianCalendar myCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, currentHour, currentMin);
                GregorianCalendar fajrCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(0).split(":")[0]), Integer.parseInt(prayerTimes.get(0).split(":")[1]));
                GregorianCalendar dhurCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(2).split(":")[0]), Integer.parseInt(prayerTimes.get(2).split(":")[1]));
                GregorianCalendar maghribCal = new GregorianCalendar(currentYear, currentMonth - 1, currentDay, Integer.parseInt(prayerTimes.get(5).split(":")[0]), Integer.parseInt(prayerTimes.get(5).split(":")[1]));
                Log.e("Alarm is SET", prayerTimes.get(5).split(":")[0] + ":" + prayerTimes.get(5).split(":")[1]);
                Log.e("Fajr is SET", prayerTimes.get(0).split(":")[0] + ":" + prayerTimes.get(0).split(":")[1]);
                Log.e("dhur is SET", prayerTimes.get(2).split(":")[0] + ":" + prayerTimes.get(2).split(":")[1]);
                long millis = myCal.getTimeInMillis();


                //Log.e(loadLat(), loadLon());
                CharSequence a = Long.toString(myCal.getTimeInMillis()) + " " + Long.toString(System.currentTimeMillis());
                if(currentHour < Integer.parseInt(prayerTimes.get(2).split(":")[0])){
                    //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, dhurCal.getTimeInMillis(), pendingIntent3);
                    //Log.e(prayerTimes.get(2), Integer.toString(loadDat()) + prayerNames.get(2) + " SET");
                }
                //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, fajrCal.getTimeInMillis(), pendingIntent);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, maghribCal.getTimeInMillis(), pendingIntent2);
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent2);
                //alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

                Toast.makeText(Main2Activity.this,a, Toast.LENGTH_LONG).show();


                //alarm_manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                //alarm_manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000 , pendingIntent);
                //alarm_manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60, pendingIntent);

                //PendingIntent alarmIntent = PendingIntent.getBroadcast(Main2Activity.this, 0, i2, PendingIntent.FLAG_UPDATE_CURRENT);
                //alarm_manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+25000, pendingIntent);

                saveAlarm("on");
                Toast.makeText(Main2Activity.this, "Adhans set for today", Toast.LENGTH_SHORT).show();
            }
        });


        //Alarm off onClick listener
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pendingIntent == null){
                    return;
                }
                alarm_manager.cancel(pendingIntent);
                alarm_manager.cancel(pendingIntent2);
                alarm_manager.cancel(pendingIntent3);
                saveAlarm("off");
                Toast.makeText(Main2Activity.this, "Adhans Off", Toast.LENGTH_SHORT).show();

                //i2.putExtra("extra", "alarm off");
                //sendBroadcast(i2);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public String loadLon(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        return (myMethod);
    }

    //load latitude data
    public String loadLat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
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

        //Intent myi = new Intent(Main2Activity.this, Main4Activity.class);
        //startActivity(myi);
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

    public String loadDat1(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        String myMethod1 = sharedPreferences.getString("lat", defaultMethod);
        return (myMethod + "," + myMethod1);
    }

    public void saveHomeLocation(String meth, String meth2){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("homeLat", (meth));
        editor.putString("homeLon", (meth2));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if(item.getItemId() == R.id.action_settings){
            Toast.makeText(Main2Activity.this, "Already on settings", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId() == R.id.action_about){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(Main2Activity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(Main2Activity.this);
            }
            builder.setTitle("About the app")
                    .setMessage("Made by Kumail Naqvi, 2017, Version 1.3, Contact me at kumailmn@gmail.com")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }

                    })
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}