package com.example.abraham;

import com.google.android.gms.maps.model.LatLng;

public class DataKriteria {
    public String[][] getDataKriteria() {
        String[][] data = {
            // harga konsultasi
            {
                "< Rp. 25.000", // 5
                "Rp. 25.001 - Rp. 100.000", // 4
                "Rp. 100.001 - Rp. 175.000", // 3
                "Rp. 175.001 - Rp. 300.000", // 2
                "> Rp. 300.000" // 1
            },
            // harga persalinan
            {
                "< Rp. 1.000.000", // 5
                "Rp. 1.000.001 - Rp. 5.000.000", // 4
                "Rp. 5.000.001 - Rp. 10.000.000", // 3
                "Rp. 10.000.001 - Rp. 15.000.000", // 2
                "> Rp. 15.000.000" // 1
            },
            // layanan
            {
                "> 4", // 4
                "3", // 3
                "2", // 2
                "< 1" // 1
            },
            // fasilitas
            {
                "Regular Room", // 3
                "1st Class Room", // 2
                "VIP Room" // 1
            },
            // jarak
            {
                "< 5 km", // 5
                "5,001 km - 10 km", // 4
                "10,001 km - 15 km", // 3
                "15,001 km - 20 km", // 2
                "> 20 km" // 1
            },
        };
        return data;
    }

    public LatLng getLocation() {
        return new LatLng(3.578296, 98.656944);
    }
}
