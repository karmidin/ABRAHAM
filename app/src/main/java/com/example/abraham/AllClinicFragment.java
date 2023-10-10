package com.example.abraham;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllClinicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllClinicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AllClinicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllClinicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllClinicFragment newInstance(String param1, String param2) {
        AllClinicFragment fragment = new AllClinicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Toolbar toolbar;
    private ListView listView;
    private ArrayList<ClinicClass> clinicClass;
    private ClinicAdapter adapter;

    private double latitude = 0.0, longitude = 0.0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_all_clinic, container, false);

        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setTitle("Daftar Rumah Sakit / Klinik");

        listView = root.findViewById(R.id.listView);

        DataKriteria dk = new DataKriteria();
        latitude = dk.getLocation().latitude;
        longitude = dk.getLocation().longitude;

//        GpsTracker gpsTracker = new GpsTracker(getContext());
//        if(gpsTracker.canGetLocation()) {
//            latitude = gpsTracker.getLatitude();
//            longitude = gpsTracker.getLongitude();
//        }

        requestDistance();

        return root;
    }

    RequestQueue requestQueue;
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestDistance() {
        Double getCurrLat = latitude;
        Double getCurrLng = longitude;

        Toast.makeText(getContext(), getCurrLat + "," + getCurrLng, Toast.LENGTH_SHORT).show();
        LatLng currLocation = new LatLng(getCurrLat, getCurrLng);

        FileParser fp = new FileParser(getContext());
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
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
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
                            }

                            ArrayList<ClinicClass> clinicClass = new ArrayList<>();
                            clinicClass = fp.fetch(getResources().openRawResource(R.raw.clinic));
                            int i = 1;
                            for(ClinicClass cc : clinicClass)
                                cc.setJarak(Double.valueOf(distances[i++]));

                            adapter = new ClinicAdapter(getContext());
                            adapter.setClinicList(clinicClass);
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
                                    BottomSheetDialog bt = new BottomSheetDialog(getContext());
                                    bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "ModalBottomSheet");
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