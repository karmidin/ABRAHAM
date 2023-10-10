package com.example.abraham;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetFragment newInstance(String param1, String param2) {
        BottomSheetFragment fragment = new BottomSheetFragment();
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

    private TextView nama, alamat;
    private ImageView img;
    private Button rute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);

        nama = root.findViewById(R.id.nama);
        alamat = root.findViewById(R.id.alamat);
        img = root.findViewById(R.id.foto);
        rute = root.findViewById(R.id.rute);

        Double currLat = getArguments().getDouble("curr_lat");
        Double currLng = getArguments().getDouble("curr_lng");
        ClinicClass key = getArguments().getParcelable("key");
        Double destLat = Double.valueOf(key.getLat());
        Double destLng = Double.valueOf(key.getLng());
        int destIndex = getArguments().getInt("dest_index");

        nama.setText(key.getNama());
        alamat.setText(key.getAlamat());
        int defaultPicture = getContext().getResources().getIdentifier("doctor", "drawable", getContext().getPackageName());
        if(key.getFoto().equals("unknown"))
            Glide.with(getContext()).load(defaultPicture).into(img);
        else
            Glide.with(getContext()).load(key.getFoto()).into(img);

        rute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MapsFragment.class);
                i.putExtra("CURR_LAT", currLat);
                i.putExtra("CURR_LNG", currLng);
                i.putExtra("DEST_LAT", destLat);
                i.putExtra("DEST_LNG", destLng);
                i.putExtra("DEST_INDEX", destIndex);
                startActivity(i);
            }
        });

        return root;
    }
}