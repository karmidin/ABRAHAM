package com.example.abraham;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_CLINIC = "extra_clinic";
    private ClinicClass clinicClass;

    private TextView nama, alamat, lokasi;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        nama = findViewById(R.id.nama);
        alamat = findViewById(R.id.alamat);
        lokasi = findViewById(R.id.lokasi);
        img = findViewById(R.id.img);

//        clinicClass = getIntent().getParcelableExtra(EXTRA_CLINIC);
//        nama.setText(clinicClass.getNama());
//        alamat.setText(clinicClass.getAlamat());
//        lokasi.setText(clinicClass.getLat()+","+clinicClass.getLng());
//        Glide.with(this).load(clinicClass.getFoto()).into(img);

    }
}