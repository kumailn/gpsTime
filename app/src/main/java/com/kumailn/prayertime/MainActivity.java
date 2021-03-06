package com.kumailn.prayertime;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.nearby.messages.Strategy;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import 	java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SwipeRefreshLayout.OnRefreshListener {
    public static String locationReturnStatement = "a";

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
    RequestQueue requestQueue;
    private TextView addressText;

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
    public static int locationViewerInt = 0;

    //Build version
    final String versionName = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        buttonClicks = 0;

        Stetho.initializeWithDefaults(this);

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
        addressText = (TextView)findViewById(R.id.addressTextView);

        saveNumericInstance(loadNumericInstance() + 1);
        if(loadNumericInstance() == 1){
            saveDaylight(TimeZone.getDefault().inDaylightTime( new Date() ));
            Log.e("MainActivity", "Switch saved as on");
        }

        Log.e("Num Times Opened: ", String.valueOf(loadNumericInstance()));
        if(loadNumericInstance() == 0){
            SharedPreferences sharedPreferences = getSharedPreferences("com.kumailn.prayertimes_preferences", MODE_PRIVATE);
            //sharedPreferences.edit().putBoolean("key_calculation_method", 0);
        }


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

        checkB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                return true;
            }
        });

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

                saveLatitude(Double.toString(mn1));
                saveLongitude(Double.toString(mn2));

                Boolean myB = Boolean.valueOf(loadDaylight());
                double timezone = offset;
                Log.e("Daylight savings:::", String.valueOf(myB));

                if(myB == null) timezone = offset;
                else if(!myB)timezone = offset;
                else timezone = timezone + 1;

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

                ArrayList<String> prayerTimes = prayers.getPrayerTimes(cal, latitude, longitude, timezone);
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

                addressText.setText(getLocationJSON(latitude, longitude));

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

                    AlphaAnimation fadeIn6 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn6.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn6.setStartOffset(1400);
                    fadeIn6.setDuration(500);
                    AnimationSet animation6 = new AnimationSet(false); //change to false
                    animation6.addAnimation(fadeIn6);
                    addressText.setAnimation(animation6);
                }

                if (!(mn1 == 0.0 && mn2 == 0.0) && locationViewerInt == 1) {
                    AlphaAnimation fadeIn6 = new AlphaAnimation(0.0f , 1.0f ) ;
                    fadeIn6.setInterpolator(new AccelerateInterpolator()); //and this
                    fadeIn6.setStartOffset(1400);
                    fadeIn6.setDuration(500);
                    AnimationSet animation6 = new AnimationSet(false); //change to false
                    animation6.addAnimation(fadeIn6);
                    addressText.setAnimation(animation6);
                }

                // Calendar with today's date
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal2 = Calendar.getInstance();

                // https://stackoverflow.com/questions/9001231/how-to-detect-day-light-saving-in-java
                // 0 = False, Else = True
                int daylightSavingCheck = cal2.get(Calendar.DST_OFFSET);

                Log.e("DAYLIGHT:", String.valueOf(daylightSavingCheck));

                dayV.setText(dateFormat.format(cal2.getTime()));

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public int loadDat(){
        //loads saved settings data
        SharedPreferences sharedPreferences = getSharedPreferences("com.kumailn.prayertimes_preferences", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt(getString(R.string.key_calculation_method), 0);
        Log.e("Calc method: ", String.valueOf(myMethod));
        return (myMethod);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Configures menu (toolbar) button options
        if(item.getItemId() == R.id.action_settings){
            //Toast.makeText(Main2Activity.this, "sucess", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, SettingsActivity.class);
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

    public String getLocationJSON(double lat, double lon){
        final String[] myStringOne = new String[1];
        final String[] address = new String[2];
        final List<String> strs = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);
        String jsonURL2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=43.5183,-79.8531&key=AIzaSyDBjz8G41V2MUToug2aWJ3TLxqVUnZAog4";
        String jsonURL3 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=42.2199983,-108.4569983&key=AIzaSyDBjz8G41V2MUToug2aWJ3TLxqVUnZAog4";
        String jsonURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(lat) + "," + String.valueOf(lon) + "&key=AIzaSyDBjz8G41V2MUToug2aWJ3TLxqVUnZAog4";
        Log.e(String.valueOf(lat),String.valueOf(lon));
        Log.e(jsonURL, "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.locationViewerInt += 1;
                            //JSONArray jsonArray = response.getJSONArray("name");
                            String aaa = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                            address[0] = aaa;
                            myStringOne[0] = aaa;
                            strs.add("ABCD");
                            MainActivity.locationReturnStatement = aaa;
                            //Toast.makeText(MainActivity.this, "JSON WORKS", Toast.LENGTH_SHORT).show();
                            Log.e("JSONVOLLEY", aaa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        strs.add("f");
        requestQueue.add(jsonObjectRequest);
        Log.e("JSON", String.valueOf(strs.get(0)));
        if (!MainActivity.locationReturnStatement.equals("a")){
            return MainActivity.locationReturnStatement;
        }
        return "";


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

    public void saveLongitude(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lonTwo", meth);
        editor.commit();
    }

    public void saveLatitude(String meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latTwo", meth);
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
        SharedPreferences sharedPreferences = getSharedPreferences("com.kumailn.prayertime_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.key_daylight_savings_switch), (meth));
        editor.commit();
    }

    public void saveNumericInstance(int meth){
        //Local data storage
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numTimes", meth);
        editor.commit();
    }

    public int loadNumericInstance(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt("numTimes", 0);
        return myMethod;
    }

    public boolean loadDaylight(){
        SharedPreferences sharedPreferences = getSharedPreferences("com.kumailn.prayertime_preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("key_daylight_savings_switch", false);
    }

    @Override
    public void onRefresh() {
        Toast.makeText(MainActivity.this, "refreshed", Toast.LENGTH_LONG).show();
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        Log.e("MainActivity", " now resumed");
        if(googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MainActivity", " now paused");
        try{
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
        catch (Exception e){

        }
    }

    public void aboutDialog(){

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }

        String nodata="<br/>&#8226; Version " + versionName + "<br/>&#8226; Made by Kumail Naqvi, 2017<br/>&#8226; kumailmn@gmail.com<br/>&#8226; github.com/kumailn<br/>&#8226";
        final SpannableString ss = new SpannableString(Html.fromHtml(nodata));
        Linkify.addLinks(ss, Linkify.ALL);

        //added a TextView
        final TextView tx1=new TextView(MainActivity.this);
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
