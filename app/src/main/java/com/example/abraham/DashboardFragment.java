package com.example.abraham;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
    private Spinner konsultasi, persalinan, layanan, fasilitas, jarak;
    private Button cari;
    DataKriteria dk = new DataKriteria();
    private String[][] dataKriteria = dk.getDataKriteria();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        toolbar = root.findViewById(R.id.toolbar);
        konsultasi = root.findViewById(R.id.konsultasi);
        persalinan = root.findViewById(R.id.persalinan);
        layanan = root.findViewById(R.id.layanan);
        fasilitas = root.findViewById(R.id.fasilitas);
        jarak = root.findViewById(R.id.jarak);
        cari = root.findViewById(R.id.cari);

        toolbar.setTitle("Dashboard");

        ArrayList<String> kriteriaList = new ArrayList<>(Arrays.asList(dataKriteria[0]));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_style, kriteriaList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        konsultasi.setAdapter(spinnerArrayAdapter);

        kriteriaList = new ArrayList<>(Arrays.asList(dataKriteria[1]));
        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_style, kriteriaList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        persalinan.setAdapter(spinnerArrayAdapter);

        kriteriaList = new ArrayList<>(Arrays.asList(dataKriteria[2]));
        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_style, kriteriaList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        layanan.setAdapter(spinnerArrayAdapter);

        kriteriaList = new ArrayList<>(Arrays.asList(dataKriteria[3]));
        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_style, kriteriaList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        fasilitas.setAdapter(spinnerArrayAdapter);

        kriteriaList = new ArrayList<>(Arrays.asList(dataKriteria[4]));
        spinnerArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_style, kriteriaList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_style);
        jarak.setAdapter(spinnerArrayAdapter);

        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get kriteria
                int[] kIndex = {
                        dataKriteria[0].length - konsultasi.getSelectedItemPosition(),
                        dataKriteria[1].length - persalinan.getSelectedItemPosition(),
                        dataKriteria[2].length - layanan.getSelectedItemPosition(),
                        dataKriteria[3].length - fasilitas.getSelectedItemPosition(),
                        dataKriteria[4].length - jarak.getSelectedItemPosition()
                };

                Intent i = new Intent(getContext(), RecommendationActivity.class);
                i.putExtra("KRITERIA", kIndex);
                startActivity(i);
            }
        });
        return root;
    }
}