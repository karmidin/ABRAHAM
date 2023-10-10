package com.example.abraham;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.abraham.databinding.ActivityMenuBinding;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MenuActivity extends AppCompatActivity {
    private AnimatedBottomBar bottom_bar;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //initilaize fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new DashboardFragment()).commit();

        bottom_bar = findViewById(R.id.bottom_bar);
        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment = null;
                switch (tab1.getId()) {
                    case R.id.dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.allClinic:
                        fragment = new AllClinicFragment();
                        break;
                    case R.id.about:
                        fragment = new AboutFragment();
                        break;
                    default:
                        fragment = new DashboardFragment();
                        break;
                }

                // refresh
                GpsTracker gpsTracker = new GpsTracker(MenuActivity.this);
                if(!gpsTracker.canGetLocation())
                    gpsTracker.showSettingsAlert();

                if(fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });
    }

}