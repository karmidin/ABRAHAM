package com.example.abraham;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringJoiner;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        compute(googleMap);
    }

    RequestQueue requestQueue;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void compute(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.563164499646222, 98.6565861170536), 16));

        DataKriteria dk = new DataKriteria();
        Double getCurrLat = getIntent().getDoubleExtra("CURR_LAT", dk.getLocation().latitude);
        Double getCurrLng = getIntent().getDoubleExtra("CURR_LNG", dk.getLocation().longitude);
        Double getDestLat = getIntent().getDoubleExtra("DEST_LAT", dk.getLocation().latitude);
        Double getDestLng = getIntent().getDoubleExtra("DEST_LNG", dk.getLocation().longitude);

        System.out.println(getCurrLat + "," + getCurrLng);
        System.out.println(getDestLat + "," + getDestLng);

        LatLng currLocation = new LatLng(getCurrLat, getCurrLng);

        FileParser fp = new FileParser(getApplicationContext());
        ArrayList<ClinicClass> clinicClass = fp.fetch(getResources().openRawResource(R.raw.clinic));
        StringJoiner getAllCoordinates = new StringJoiner("|");

        ArrayList<String> namaClinic = new ArrayList<>();
        // CURRENT LOCATION
        namaClinic.add("CURRENT LOCATION");
        getAllCoordinates.add(currLocation.latitude + "," + currLocation.longitude);

        // CLINIC LOCATIONS
        for (ClinicClass d : clinicClass) {
            getAllCoordinates.add(d.getLat() + "," + d.getLng());
            namaClinic.add(d.getNama());
        }

        // REQUEST API
        int nodeCount = clinicClass.size() + 1;
        int[][] distances = new int[nodeCount][nodeCount];
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        BasicNetwork network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric" +
                "&mode=Driving" +
                "&origins=" + getAllCoordinates.toString() +
                "&destinations=" + getAllCoordinates.toString() +
                "&key=" + getResources().getString(R.string.api_key);

        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {
                            for (int i=0; i<nodeCount; i++) {
                                for (int j=0; j<nodeCount; j++) {
                                    result = new JSONObject(response)
                                            .getJSONArray("rows").getJSONObject(i)
                                            .getJSONArray("elements").getJSONObject(j)
                                            .getJSONObject("distance");
                                    Integer intResult = result.getInt("value");
                                    distances[i][j] = intResult;
                                }
                            }
                            LatLng origin = new LatLng(getCurrLat, getCurrLng);
                            LatLng dest = new LatLng(getDestLat, getDestLng);
                            int originIndex = 0; // 0 as curr location
                            int destIndex = getIntent().getIntExtra("DEST_INDEX", 0); // index as clinic location

                            BestFirstSearch bfs = new BestFirstSearch(21);
                            bfs.process(originIndex, destIndex, 21);

                            mMap.addMarker(new MarkerOptions().position(origin).title("ORIGIN"));
                            mMap.addMarker(new MarkerOptions().position(dest).title(getDestLat + "," + getDestLng));

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16));

                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(LatLng latLng) {
                                }
                            });

                            if (markerPoints.size() > 1) {
                                markerPoints.clear();
                                mMap.clear();
                            }

                            // Adding new item to the ArrayList
                            markerPoints.add(origin);
                            markerPoints.add(dest);

                            // Creating MarkerOptions
                            MarkerOptions options = new MarkerOptions();

                            // Setting the position of the marker
                            options.position(origin);
                            options.position(dest);

                            if (markerPoints.size() == 1) {
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            } else if (markerPoints.size() == 2) {
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            }

                            // Add new marker to the Google Map Android API V2
                            mMap.addMarker(options);


                            // Getting URL to the Google Directions API
                            String url = getDirectionsUrl(origin, dest);
                            DownloadTask downloadTask = new DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=AIzaSyA1MgLuZuyqR_OGY3ob3M52N46TDBRI_9k";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}