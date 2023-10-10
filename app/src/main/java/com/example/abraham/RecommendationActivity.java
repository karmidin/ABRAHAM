package com.example.abraham;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.StringJoiner;

public class RecommendationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private ArrayList<ClinicClass> clinicClass;
    private ClinicRankAdapter adapter;
    private double latitude = 0.0, longitude = 0.0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView = findViewById(R.id.listView);

        DataKriteria dk = new DataKriteria();
        latitude = dk.getLocation().latitude;
        longitude = dk.getLocation().longitude;

//        GpsTracker gpsTracker = new GpsTracker(getApplicationContext());
//        if(gpsTracker.canGetLocation()) {
//            latitude = gpsTracker.getLatitude();
//            longitude = gpsTracker.getLongitude();
//        }
        requestDistance();

    }

    public double[] moora(int[][] data) {
        double[] jlh_kuadrat = new double[data.length];
        for(int i = 0; i < data[0].length; i++) {
            for(int j = 0; j < data.length; j++) {
                jlh_kuadrat[i] += data[j][i]*data[j][i];
            }
            if(jlh_kuadrat[i] == 0)
                jlh_kuadrat[i] = 1.0;
            else
                jlh_kuadrat[i] = Math.sqrt(jlh_kuadrat[i]);
        }

        double[][] normalisasi = new double[data.length][data[0].length];
        for(int i = 0; i < data[0].length; i++) {
            for(int j = 0; j < data.length; j++) {
                normalisasi[j][i] = data[j][i]/jlh_kuadrat[i];
            }
        }

        double[] optimasi = new double[data.length];
        double[] percent = {0.3, 0.3, 0.2, 0.1, 0.1};
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[0].length; j++) {
                optimasi[i] += percent[j]*normalisasi[i][j];
            }
        }

        return optimasi;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    RequestQueue requestQueue;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestDistance() {
        Double getCurrLat = latitude;
        Double getCurrLng = longitude;

        Toast.makeText(getApplicationContext(), getCurrLat + "," + getCurrLng, Toast.LENGTH_SHORT).show();
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
        int[] distances = new int[nodeCount];
        int[] bobotJarak = new int[nodeCount];
        Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 1024 * 1024);
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
                                result = new JSONObject(response)
                                        .getJSONArray("rows").getJSONObject(0)
                                        .getJSONArray("elements").getJSONObject(i)
                                        .getJSONObject("distance");
                                Integer intResult = result.getInt("value");
                                distances[i] = intResult;

                                if(distances[i] > 20000)
                                    bobotJarak[i] = 1;
                                else if(distances[i] > 15000)
                                    bobotJarak[i] = 2;
                                else if(distances[i] > 10000)
                                    bobotJarak[i] = 3;
                                else if(distances[i] > 5000)
                                    bobotJarak[i] = 4;
                                else
                                    bobotJarak[i] = 5;
                                System.out.println(bobotJarak[i]);
                            }

                            FileParser fp = new FileParser(getApplicationContext());
                            ArrayList<ClinicClass> clinicClass = fp.fetch(getResources().openRawResource(R.raw.clinic));

                            int d = 1;
                            for(ClinicClass cc : clinicClass)
                                cc.setJarak(Double.valueOf(distances[d++]));

                            int[][] data = fp.getBobot(getResources().openRawResource(R.raw.clinic));
                            int[][] finalData = new int[data.length + 1][data[0].length];

                            // initialize bobot jarak
                            for(int i = 1; i < nodeCount; i++)
                                data[i - 1][4] = bobotJarak[i];

                            // move one down
                            for(int i = 1; i < finalData.length; i++) {
                                for(int j = 0; j < finalData[0].length; j++) {
                                    finalData[i][j] = data[i - 1][j];
                                }
                            }

                            int[] kIndex = getIntent().getIntArrayExtra("KRITERIA");
//                            int[] kIndex = {5, 5, 4, 3, 0};
                            // set input user in first array
                            for(int i = 0; i < finalData[0].length; i++) {
                                finalData[0][i] = kIndex[i];
                            }

                            double[] moora = moora(finalData);
                            for(int i = 1; i < moora.length; i++) {
//                                clinicClass.get(i - 1).setAlamat(finalData[i][0] + "/" + finalData[i][1] + "/" + finalData[i][2] + "/" + finalData[i][3] + "/" + finalData[i][4]);
                                clinicClass.get(i - 1).setScore((1-Math.abs((moora[0] - moora[i])/moora[0]))*100);
                            }

                            ArrayList<ClinicClass> clinicClassSorted = clinicClass;
                            clinicClassSorted.sort(new Comparator<ClinicClass>() {
                                @Override
                                public int compare(ClinicClass t0, ClinicClass t1) {
                                    return t1.getScore().compareTo(t0.getScore());
                                }
                            });

                            adapter = new ClinicRankAdapter(getApplicationContext());
                            adapter.setClinicList(clinicClassSorted);
                            listView.setAdapter(adapter);

                            ArrayList<ClinicClass> finalClinicClass = clinicClass;
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("key", finalClinicClass.get(i));
                                    bundle.putDouble("curr_lat", getCurrLat);
                                    bundle.putDouble("curr_lng", getCurrLng);
                                    bundle.putInt("dest_index", i);

                                    BottomSheetDialogFragment bottomSheetDialog = new BottomSheetFragment();
                                    bottomSheetDialog.setArguments(bundle);
                                    BottomSheetDialog bt = new BottomSheetDialog(getApplicationContext());
                                    bottomSheetDialog.show(getSupportFragmentManager(), "ModalBottomSheet");
                                }
                            });

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
}