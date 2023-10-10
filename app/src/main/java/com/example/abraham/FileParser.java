package com.example.abraham;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileParser {
    private Context context;
    private int jlhSampel = 20;
    private int jlhKriteria = 5;
    public FileParser(Context context) {
        this.context = context;
    }

    // 0 nama
    // 1 alamat
    // 2 lat
    // 3 lng
    // 4 bobot konsultasi
    // 5 bobot persalinan
    // 6 bobot layanan
    // 7 bobot fasilitas
    // 8 foto
    public ArrayList<ClinicClass> fetch(InputStream inputStream) {
        ArrayList<ClinicClass> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String txtLine;
            while ((txtLine = reader.readLine()) != null) {
                String[] row = txtLine.split(";");
                data.add(new ClinicClass(row[0], row[1], row[2], row[3], 0.0, row[8], 0.0));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading Text file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return data;
    }

    public int[][] getBobot(InputStream inputStream) {
        int[][] data = new int[jlhSampel][jlhKriteria];
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String txtLine;
            int i = 0;
            while ((txtLine = reader.readLine()) != null) {
                String[] row = txtLine.split(";");
                data[i][0] = Integer.parseInt(row[4]); // konsultasi
                data[i][1] = Integer.parseInt(row[5]); // persalinan
                data[i][2] = Integer.parseInt(row[6]); // layanan
                data[i][3] = Integer.parseInt(row[7]); // fasilitas
                data[i][4] = 0; // jarak
                i++;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading Text file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return data;
    }
}
