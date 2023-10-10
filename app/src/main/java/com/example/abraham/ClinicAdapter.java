package com.example.abraham;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ClinicAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ClinicClass> clinicList = new ArrayList<>();

    public void setClinicList(ArrayList<ClinicClass> clinicList) {
        this.clinicList = clinicList;
    }

    public ClinicAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return clinicList.size();
    }

    @Override
    public Object getItem(int i) {
        return clinicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;

        if (itemView == null)
            itemView = LayoutInflater.from(context).inflate(R.layout.clinic_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(itemView);

        ClinicClass clinic = (ClinicClass) getItem(i);
        viewHolder.bind(clinic);
        return itemView;
    }

    private class ViewHolder {
        private TextView nama, alamat, jarak;
        private ImageView img;

        ViewHolder(View view) {
            nama = view.findViewById(R.id.nama);
            alamat = view.findViewById(R.id.alamat);
            jarak = view.findViewById(R.id.jarak);
            img = view.findViewById(R.id.foto);
        }

        void bind(ClinicClass clinic) {
            nama.setText(clinic.getNama());
            alamat.setText(clinic.getAlamat());
            String jarak_val = String.format("Jarak: %,.0f m", clinic.getJarak());
            jarak.setText(jarak_val.replace(",", "."));

            int defaultPicture = context.getResources().getIdentifier("doctor", "drawable", context.getPackageName());
            if(clinic.getFoto().equals("unknown"))
                Glide.with(context).load(defaultPicture).into(img);
            else
                Glide.with(context).load(clinic.getFoto()).into(img);
        }
    }
}

