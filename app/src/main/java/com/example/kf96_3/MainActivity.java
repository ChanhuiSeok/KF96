package com.example.kf96_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null){
            bottomNav.setItemSelected(R.id.calendar, true);
            fragmentManager = getSupportFragmentManager();
            calendarFragment calendarFrag = new calendarFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarFrag)
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.calendar:
                        fragment = new calendarFragment();
                        break;
                    case R.id.report:
                        fragment = new ReportFragment();
                        break;
                    case R.id.setting:
                        fragment = new SettingFragment();
                        break;
                }

                if (fragment != null){
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }else {
                    Log.e(TAG, "Error in creating fragment");
                }
            }
        });
    }
}