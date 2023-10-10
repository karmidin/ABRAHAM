package com.example.abraham;

import android.os.Parcel;
import android.os.Parcelable;

public class ClinicClass implements Parcelable {
    private String nama, alamat, lat, lng, foto;
    private Double jarak, score;

    public ClinicClass(String nama, String alamat, String lat, String lng, Double jarak, String foto, Double score) {
        this.nama = nama;
        this.alamat = alamat;
        this.lat = lat;
        this.lng = lng;
        this.jarak = jarak;
        this.foto = foto;
        this.score = score;
    }

    protected ClinicClass(Parcel in) {
        nama = in.readString();
        alamat = in.readString();
        lat = in.readString();
        lng = in.readString();
        foto = in.readString();
    }

    public static final Creator<ClinicClass> CREATOR = new Creator<ClinicClass>() {
        @Override
        public ClinicClass createFromParcel(Parcel in) {
            return new ClinicClass(in);
        }

        @Override
        public ClinicClass[] newArray(int size) {
            return new ClinicClass[size];
        }
    };

    public String getNama() {
        return nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public Double getJarak() {
        return jarak;
    }

    public void setJarak(Double jarak) {
        this.jarak = jarak;
    }

    public String getFoto() {
        return foto;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.nama);
        parcel.writeString(this.alamat);
        parcel.writeString(this.lat);
        parcel.writeString(this.lng);
        parcel.writeString(this.foto);
    }
}
