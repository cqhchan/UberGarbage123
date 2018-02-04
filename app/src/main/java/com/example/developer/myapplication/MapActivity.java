package com.example.developer.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.Circle;
import com.tomtom.online.sdk.map.CircleBuilder;
import com.tomtom.online.sdk.map.Icon;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.RouteQuery;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.*;
import io.reactivex.schedulers.Schedulers;

public class MapActivity extends AppCompatActivity {


    TomtomMap tomtomMap;
    Button findRoutesButton;
    EditText distanceInput, capacityInput;
    ArrayList<Request> requestsArray = new ArrayList<>();
    RouteQuery routeQuery;
    protected Scheduler networkScheduler = Schedulers.from(Executors.newFixedThreadPool(4));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(onMapReadyCallback);

        Drawable bg = new ColorDrawable(getColor(R.color.colorUberGreen));
        this.getSupportActionBar().setBackgroundDrawable(bg);

        findRoutesButton = findViewById(R.id.findRoutesButton);
        distanceInput = findViewById(R.id.distanceEditText);
        capacityInput = findViewById(R.id.capacityEditText);

        distanceInput.setEnabled(false);
        distanceInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {




            }
        });

        distanceInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    new Thread(new buildMarkers(requestsArray)).start();

                    if (tomtomMap.getUserLocation() != null && !distanceInput.getText().toString().isEmpty()) {



                        tomtomMap.getOverlaySettings().removeOverlays();

                        final Circle circle = CircleBuilder.create()
                                .fill(true)
                                .radius(Double.parseDouble(distanceInput.getText().toString()))
                                .position(new LatLng(tomtomMap.getUserLocation()))
                                .color(Color.RED)
                                .opacity(0.5f)
                                .build();
                        tomtomMap.getOverlaySettings().addOverlay(circle);

                    }
                    return true;
                }
                return false;
            }
        });



        RoutingApi routePlannerAPI = OnlineRoutingApi.create(getApplicationContext());

        findRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<LatLng> arraylatLng = new ArrayList<LatLng>();

                    Double lat =  37.7877483;
                    Double lon = -122.3961359;
                    LatLng latLng = new LatLng(lat,lon);
                    arraylatLng.add(latLng);

                    lat = 37.7878260;
                    lon = -122.3956977;

                    latLng = new LatLng(lat,lon);
                    arraylatLng.add(latLng);

                lat = 37.7884563;
                lon = -122.3975541;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);

                lat = 37.7867804;
                lon = -122.3968135;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);


                lat = 37.7864198;

                lon = -122.4062197;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);


                lat = 37.7880646;
                lon = -122.4049119;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);


                lat = 37.7895540;
                lon =   -122.4038509;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);


                lat = 37.7952984;
                lon = -122.3983159;

                latLng = new LatLng(lat,lon);
                arraylatLng.add(latLng);





                displayRoute(arraylatLng);
            }
        });
    }

    public void displayRoute(List<LatLng> route) {

        tomtomMap.getOverlaySettings().removeOverlays();
        tomtomMap.clearRoute();
        tomtomMap.getMarkerSettings().removeMarkers();

        for (LatLng latLng : route){

            tomtomMap.addMarker(new MarkerBuilder(latLng));

        }
        route.add(0, new LatLng(tomtomMap.getUserLocation().getLatitude(),tomtomMap.getUserLocation().getLongitude()));
        Drawable dr = getApplicationContext().getResources().getDrawable( R.drawable.flat_design_single_05_2016_ecology);

        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//
        Drawable startIcon = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 200, 200, true));
        RouteBuilder routeBuilder = new RouteBuilder(route)
                .isActive(true)
                .startIcon(new Icon("testing 123", startIcon));
        final Route mapRoute = tomtomMap.addRoute(routeBuilder);
        tomtomMap.displayRoutesOverview();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        tomtomMap.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private OnMapReadyCallback onMapReadyCallback =
            new OnMapReadyCallback() {
                @Override
                public void onMapReady(TomtomMap map) {
                    //Map is ready here
                    tomtomMap = map;
                    tomtomMap.setMyLocationEnabled(true);
                    distanceInput.setEnabled(true);
                    sendPost();


                }
            };




    public void getRoute() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {


//                    tomtomMap.getUserLocation().getLatitude();

                    URL url = new URL("http://18.144.35.17/road_finder.php?lat=" + tomtomMap.getUserLocation().getLatitude() +
                            "&lon=" +tomtomMap.getUserLocation().getLongitude()+ "&max=3000");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-length", "0");
                    conn.setUseCaches(false);
                    Log.d("testing " , 1 +" ");



                    conn.connect();
                    Log.d("testing " , 2 +" ");

                    int status = conn.getResponseCode();
                    Log.d("testing " , 3 +" ");

                    switch (status) {
                        case 200:
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb2 = new StringBuilder();
                            String line;
                            while ((line = br2.readLine()) != null) {
                                sb2.append(line + "\n");
                            }
                            Log.d("testing " , sb2.toString());

//                             String[] parts = sb2.toString().split("]|\\[");
//
//                            String parsedString = parts[1];
//
//                            String [] newParts = parsedString.split("\\}|\\{");
//
//
//                            for (String object : newParts) {
//
//
//                                if (!object.equals(",")) {
//
//                                    object = "{" + object + "}";
//                                    Gson g = new Gson();
//                                    Request p = g.fromJson(object, Request.class);
//
//
//                                    if ( p.lat != null && !p.lat.isEmpty() && p.lon != null && !p.lon.isEmpty()) {
//
//                                        requestsArray.add(p);
////
////                                        Log.d("Test request ID Created", "" + p.id);
////                                        Log.d("Test request Lat Created", "" + p.lat);
////                                        Log.d("Test request Lon Created", "" + p.lon);
//
//                                    }
//                                }
//                            }

//                            br2.close();



                            break;
                        case 201:
                            Log.d("Test", "success");

                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line2;
                            while ((line2 = br.readLine()) != null) {
                                sb.append(line2 + "\n");
                            }
                            br.close();
                            Log.d("Test", sb.toString());
                            break;
                        default:
                            Log.d("TEST", " " + status);


                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://18.144.35.17/api.php/garbage_request?transform=1");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-length", "0");
                    conn.setUseCaches(false);



                    conn.connect();

                    int status = conn.getResponseCode();

                    switch (status) {
                        case 200:
                            BufferedReader br2 = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb2 = new StringBuilder();
                            String line;
                            while ((line = br2.readLine()) != null) {
                                sb2.append(line + "\n");
                            }


                            String[] parts = sb2.toString().split("]|\\[");

                            String parsedString = parts[1];

                            String [] newParts = parsedString.split("\\}|\\{");


                            for (String object : newParts) {


                                if (!object.equals(",")) {

                                    object = "{" + object + "}";
                                    Gson g = new Gson();
                                    Request p = g.fromJson(object, Request.class);


                                    if ( p.lat != null && !p.lat.isEmpty() && p.lon != null && !p.lon.isEmpty()) {

                                        requestsArray.add(p);
//
//                                        Log.d("Test request ID Created", "" + p.id);
//                                        Log.d("Test request Lat Created", "" + p.lat);
//                                        Log.d("Test request Lon Created", "" + p.lon);

                                    }
                                }
                            }

                            br2.close();



                            break;
                        case 201:
                            Log.d("Test", "success");

                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder sb = new StringBuilder();
                            String line2;
                            while ((line2 = br.readLine()) != null) {
                                sb.append(line2 + "\n");
                            }
                            br.close();
                            Log.d("Test", sb.toString());
                            break;
                        default:
                            Log.d("TEST", " " + status);


                    }

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    public class buildMarkers implements Runnable {

        ArrayList<Request> requestsArrayList;
     buildMarkers (ArrayList<Request>  requestsArrayList) {

        this.requestsArrayList = requestsArrayList;

    }

        @Override
        public void run() {

            tomtomMap.getMarkerSettings().removeMarkers();
            int range = 20000;
            if (distanceInput.getText().toString() != null && !distanceInput.getText().toString().equals("0")){
                range = Integer.parseInt(distanceInput.getText().toString());
            }

            //Position, decimal degrees
            Double latcurrent = tomtomMap.getUserLocation().getLatitude();
            Double loncurrent = tomtomMap.getUserLocation().getLongitude();

            //Earth’s radius, sphere
            double R = 6378137;

            //offsets in meters


            //Coordinate offsets in radians
            double dLat = range/R;
            double dLon = range/(R*Math.cos(Math.PI*latcurrent/180));

            //OffsetPosition, decimal degrees
            Double latMax = latcurrent + dLat * 180/Math.PI;
            Double latMin  = latcurrent - dLat * 180/Math.PI;

            Double lonMax = loncurrent + dLon * 180/Math.PI;
            Double lonMin = loncurrent - dLon * 180/Math.PI;

            for (Request request : requestsArrayList) {
                Log.d("Test request ID Created", "" + request.id);
                Log.d("Test request Lat Created", "" + request.lat);
                Log.d("Test request Lon Created", "" + request.lon);

                Double lat = Double.parseDouble(request.lat);
                Double lon = Double.parseDouble(request.lon);




                if ((lat<latMax && lat> latMin)&&( lon < lonMax && lon > lonMin) ) {

                    LatLng latLng = new LatLng(lat, lon);
                    MarkerBuilder marker = new MarkerBuilder(latLng);

                    tomtomMap.addMarker(marker);
                }


            }

        }

    }







    }
