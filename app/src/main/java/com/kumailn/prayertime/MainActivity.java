package com.kumailn.prayertime;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.DialogInterface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.AlertDialog;
import android.widget.Toast;
import android.provider.Settings;
import android.content.Intent;
import android.app.Dialog;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import 	java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SwipeRefreshLayout.OnRefreshListener {
    //TextView declarations
    private TextView ttv;
    private TextView fajrV;
    private TextView dhurV;
    private TextView asrV;
    private TextView maghribV;
    private TextView ishaV;
    private TextView fajrV2;
    private TextView dhurV2;
    private TextView asrV2;
    private TextView maghribV2;
    private TextView ishaV2;
    private TextView sunV;
    private TextView dayV;
    private Button checkB;

    //Location objects
    private LocationRequest locationRequest;
    private LocationManager locationManager;
    private LocationListener locationListener;

    //Longitude and Latitude variables
    private double mn1 = 0.00;
    private double mn2 = 0.00;
    //Unix timestamp
    private String mn3;

    //TextView strings
    private String timeZone;
    private String fajrTime;
    private String sunriseTime;
    private String dhurTime;
    private String asrTime;
    private String maghribTime;
    private String ishaTime;
    private String sunsetTime;
    private String unixTime;
    private String todaysDate;

    //Google locationService objects
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;

    //Permission variable
    private boolean permissionGranted = false;
    public static final String defaultMethod = "0";
    private static final int myPermission = 101;
    private static final int myPermission2 = 102;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    //number of times button clicked counter
    private int buttonClicks = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonClicks = 0;

        setContentView(R.layout.activity_main);
        //Non default toolbar creation
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //THIS LINE IS IMPORTANT - TOOK 2 HOURS TO FIND
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final ImageView iv = (ImageView) findViewById(R.id.imageView2);
        final ImageView iv2 = (ImageView) findViewById(R.id.imageView3);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRef);
        //mRecyclerView = (RecyclerView)findViewById(R.id.rec);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        checkB = (Button)findViewById(R.id.checkButton1);
        ttv = (TextView) findViewById(R.id.textView22);
        fajrV = (TextView) findViewById(R.id.fajrView);
        dhurV = (TextView) findViewById(R.id.dhurView);
        asrV = (TextView) findViewById(R.id.asrView);
        maghribV = (TextView) findViewById(R.id.maghribView);
        ishaV = (TextView) findViewById(R.id.ishaTime);
        dayV = (TextView) findViewById(R.id.todayView);
        fajrV2 = (TextView) findViewById(R.id.fajrView2);
        dhurV2 = (TextView) findViewById(R.id.dhurView2);
        asrV2 = (TextView) findViewById(R.id.asrView2);
        maghribV2 = (TextView) findViewById(R.id.maghribView2);
        ishaV2 = (TextView) findViewById(R.id.ishaTime2);




        //Old locationManager code
        /*
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListen er() {
            @Override
            public void onLocationChanged(Location location) {
                //jText.setText(Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
                mn1 = location.getLatitude();
                mn2 = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        */



        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(15000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }


        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        //OnClick button action
        checkB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                long ft = System.currentTimeMillis() + 1111;

                SimpleDateFormat sdf = new SimpleDateFormat("H");
                String str = sdf.format(new Date());
                //Toast.makeText(Main2Activity.this, str, Toast.LENGTH_SHORT).show();

                //Animations

                if(buttonClicks < 1){
                    Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
                    iv.startAnimation(animSlide);

                    Animation animSlide2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.s2);
                    iv2.startAnimation(animSlide2);
                }

                else if(buttonClicks % 2 == 0){
                    Animation animSlide4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide4);
                    iv2.startAnimation(animSlide4);

                    Animation animSlide5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide5);
                    iv.startAnimation(animSlide5);

                }
                else{
                    Animation animSlide2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide2);
                    iv.startAnimation(animSlide2);

                    Animation animSlide3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide3);
                    iv2.startAnimation(animSlide3);


                }


                while (System.currentTimeMillis() < ft) {
                    if (mn1 != 0.00) {
                        break;
                    }
                }

                String timezoneID = TimeZone.getDefault().getID();
                buttonClicks = buttonClicks + 1;
                mn3 = Long.toString(System.currentTimeMillis() / 1000);
                if(loadDat() >= 0){
                    //Toast.makeText(Main2Activity.this, "Yeah, you're definitly going to hell.", Toast.LENGTH_LONG).show();
                    //int yy = 0;
                }

                TimeZone tz1 = TimeZone.getDefault();
                int offset = tz1.getRawOffset()/1000/60/60;

                double latitude = mn1;
                double longitude = mn2;

                saveLat(Double.toString(mn1));
                saveLon(Double.toString(mn2));

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

                prayers.setTimeFormat(prayers.Time12);
                //prayers.setCalcMethod(prayers.Jafari);
                prayers.setCalcMethod(loadDat());
                prayers.setAsrJuristic(prayers.Shafii);
                prayers.setAdjustHighLats(prayers.AngleBased);
                int[] offsets = {0, 0, 0, 0, 0, 0, 0}; // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
                prayers.tune(offsets);

                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);

                ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal,
                        latitude, longitude, timezone);
                ArrayList<String> prayerNames = prayers.getTimeNames();



                fajrV.setText("Fajr:");
                dhurV.setText("Dhur:");
                asrV.setText("Asr:");
                maghribV.setText("Maghrib:");
                ishaV.setText("Isha:");

                fajrV2.setText(prayerTimes.get(0) );
                dhurV2.setText(prayerTimes.get(2));
                asrV2.setText(prayerTimes.get(3));
                maghribV2.setText(prayerTimes.get(5));
                ishaV2.setText(prayerTimes.get(6));

                if(buttonClicks <= 1){
                    AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn.setStartOffset(300);
                    fadeIn.setDuration(500);
                    AnimationSet animation = new AnimationSet(false); //change to false
                    animation.addAnimation(fadeIn);
                    fajrV.setAnimation(animation);
                    fajrV2.setAnimation(animation);

                    AlphaAnimation fadeIn2 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn2.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn2.setStartOffset(500);
                    fadeIn2.setDuration(500);
                    AnimationSet animation2 = new AnimationSet(false); //change to false
                    animation2.addAnimation(fadeIn2);
                    dhurV.setAnimation(animation2);
                    dhurV2.setAnimation(animation2);

                    AlphaAnimation fadeIn3 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn3.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn3.setStartOffset(700);
                    fadeIn3.setDuration(500);
                    AnimationSet animation3 = new AnimationSet(false); //change to false
                    animation3.addAnimation(fadeIn3);
                    asrV.setAnimation(animation3);
                    asrV2.setAnimation(animation3);

                    AlphaAnimation fadeIn4 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn4.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn4.setStartOffset(900);
                    fadeIn4.setDuration(500);
                    AnimationSet animation4 = new AnimationSet(false); //change to false
                    animation4.addAnimation(fadeIn4);
                    maghribV2.setAnimation(animation4);
                    maghribV.setAnimation(animation4);

                    AlphaAnimation fadeIn5 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn5.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn5.setStartOffset(1200);
                    fadeIn5.setDuration(500);
                    AnimationSet animation5 = new AnimationSet(false); //change to false
                    animation5.addAnimation(fadeIn5);
                    ishaV.setAnimation(animation5);
                    ishaV2.setAnimation(animation5);
                }




                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal2 = Calendar.getInstance();

                dayV.setText(dateFormat.format(cal2.getTime()));


                /*
                Log.e(prayerNames.get(0) + " - " + prayerTimes.get(0), "1");
                Log.e(prayerNames.get(1) + " - " + prayerTimes.get(1), "2");
                Log.e(prayerNames.get(2) + " - " + prayerTimes.get(2), "3");
                Log.e(prayerNames.get(5) + " - " + prayerTimes.get(5), "4");
                Log.e(prayerNames.get(6) + " - " + prayerTimes.get(6), "5");
                */

                //Toast.makeText(Main2Activity.this, (prayerNames.get(0) + " - " + prayerTimes.get(0)), Toast.LENGTH_LONG).show();

                //new jsonTask2().execute("https://maps.googleapis.com/maps/api/timezone/json?location=" + Double.toString(12.6392316)+","+ Double.toString(-8.002889200000027) +"&timestamp=" + Long.toString(System.currentTimeMillis()/1000) + "&key=AIzaSyCWGwEXTr-WnOW5YANriYkII-MohBedO9I");
                //new jsonTask2().execute("https://maps.googleapis.com/maps/api/timezone/json?location=" + Double.toString(mn1)+","+ Double.toString(mn2) +"&timestamp=1458000000&key=AIzaSyCWGwEXTr-WnOW5YANriYkII-MohBedO9I");
                //new jsonTask().execute("http://api.aladhan.com/timings/" + (mn3) + "?latitude=" + Double.toString(mn1) + "&longitude=" + Double.toString(mn2) + "&timezonestring=" + timezoneID + "&method=" + Integer.toString(loadDat()));
                //String cc = Double.toString(mn1) + "   " + Double.toString(mn2);
                //ttv.setText(cc);
                //jText.setText(Double.toString(8.99));
            }
        });


    }//OnCreate ENDS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int loadDat(){
        //loads saved settings data
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("method", defaultMethod);
        return Integer.parseInt(myMethod);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if(item.getItemId() == R.id.action_settings){
            //Toast.makeText(Main2Activity.this, "sucess", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, Main2Activity.class);
            startActivity(i);
        }
        if(item.getItemId() == R.id.action_about){
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(MainActivity.this);
            }
            builder.setTitle("About the app")
                    .setMessage("Made by Kumail Naqvi, 2017, Version 1.3, Contact me at kumailmn@gmail.com, github.com/kumailn")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }

                    })
                    //.setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();

    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, myPermission);
            }

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void save1(Double lon, Double lat){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lon", Double.toString(lon));
        editor.putString("lat", Double.toString(lat));
        editor.commit();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mn1 = location.getLatitude();
        mn2 = location.getLongitude();

        save1(mn1, mn2);
    }

    public void saveAlarm(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("alarm", meth);
        editor.commit();
    }



    public String loadAlarm(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("alarm", defaultMethod);
        return (myMethod);
    }

    public void saveLon(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lon", meth);
        editor.commit();
    }

    public void saveLat(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lat", meth);
        editor.commit();
    }
    public String loadLon(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lon", defaultMethod);
        return (myMethod);
    }

    public String loadLat(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("lat", defaultMethod);
        return (myMethod);
    }

    public void saveDaylight(boolean meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("daylight", Boolean.toString(meth));
        editor.commit();
    }

    public String loadDaylight(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("daylight", defaultMethod);
        return (myMethod);
    }

    @Override
    public void onRefresh() {
        Toast.makeText(MainActivity.this, "refreshed", Toast.LENGTH_LONG).show();
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public class jsonTask extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {

                URL myURL = new URL(params[0]);
                connection = (HttpURLConnection)myURL.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                JSONObject parentOb = new JSONObject(finalJSON);
                //JSONObject finalOb = parentOb.getJSONObject("code");

                //String fajr = parentOb.getString("status");
                //String aaa = (parentOb.getJSONArray("code")).toString();


                fajrTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Fajr");
                sunriseTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Sunrise");
                dhurTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Dhuhr");
                asrTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Asr");
                sunsetTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Sunset");
                maghribTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Maghrib");
                ishaTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Isha");
                todaysDate = parentOb.getJSONObject("data").getJSONObject("date").getString("readable");
                unixTime = parentOb.getJSONObject("data").getJSONObject("date").getString("timestamp");


                fajrTime = timeConverter(fajrTime);
                dhurTime = timeConverter(dhurTime);
                asrTime = timeConverter(asrTime);
                maghribTime = timeConverter(maghribTime);
                ishaTime = timeConverter(ishaTime);


                //return buffer.toString();
                //CharSequence cc = aaa;
                //Toast.makeText(MainActivity.this,aaa, Toast.LENGTH_LONG);
                return "";


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //jText.setText(result);
            fajrV.setText("        Fajr:             " + fajrTime );
            dhurV.setText("        Dhur:            " + dhurTime);
            asrV.setText("        Asr:              " + asrTime);
            maghribV.setText("        Maghrib:      " + maghribTime);
            ishaV.setText("        Isha:           " + ishaTime);
            dayV.setText(todaysDate);
            //sunV.setText("Sun rise/set: " + sunriseTime + "/" + sunsetTime);
            ttv.setText(TimeZone.getDefault().getID());


        }
    }
    private String ttt1;
    private String ttt2;
    private String timeConverter(String ss){
        ttt1 = ss.split(":")[0];
        ttt2 = ss.split(":")[1];

        if(Integer.parseInt(ttt1) > 11){
            ttt1 = Integer.toString(Integer.parseInt(ttt1) - 12);
            return (ttt1 + ":" + ttt2 + " PM");
        }
        else {
            return (ttt1.substring(1,2) + ":" + ttt2 + " AM");
        }

    }
    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
        catch (Exception e){

        }


    }

    public class jsonTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {


            HttpsURLConnection connection = null;
            BufferedReader reader = null;


            try {
                URL myURL = new URL(params[0]);
                connection = (HttpsURLConnection)myURL.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJSON = buffer.toString();

                JSONObject parentOb = new JSONObject(finalJSON);

                timeZone = parentOb.getString("timeZoneId");
                //sunriseTime = parentOb.getJSONObject("data").getJSONObject("timings").getString("Sunrise");

                return timeZone;


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case myPermission:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //granted
                    permissionGranted = true;

                }
                else {
                    //denied
                    permissionGranted = false;
                    Toast.makeText(getApplicationContext(), "This app needs location access to work", Toast.LENGTH_SHORT).show();
                }
                break;
            case myPermission2:
                break;
        }
    }
}
