package com.example.kf96_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;

    double longitude; // 경도
    double latitude; // 위도
    public static String current_location; // 현재 위치
    // Geocoder 객체 생성
    public final Geocoder g = new Geocoder(this);

    // 위치 텍스트
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null){
            bottomNav.setItemSelected(R.id.home, true);
            fragmentManager = getSupportFragmentManager();
            HomeFragment homeFrag = new HomeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, homeFrag)
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
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

        // 위치 관리자 객체 참조
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
            //txtResult.setText("위치정보");
        }
        else{
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            assert location != null;
            String provider = location.getProvider();   // GPS
            longitude = location.getLongitude(); // 경도
            latitude = location.getLatitude();   // 위도
            double altitude = location.getAltitude();   // 고도
            current_location = null;
            List<Address> address=null;

            // 위치정보 업데이트 요청
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, // 최소시간
                    1,  // 최소거리
                    gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1,
                    1,
                    gpsLocationListener);

            System.out.println(latitude);
            System.out.println(longitude);
            try {
                address = g.getFromLocation(latitude, longitude, 1);
            } catch(IOException e){
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (address != null){
                if (address.size() == 0){
                    System.out.println("해당하는 주소가 없습니다.");
                }
                else{
                    current_location = address.get(0).getAddressLine(0).toString();
                }
            }
            else{
                System.out.println("주소값이 null입니다.");
            }
        }
    }
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            List<Address> address=null;

            try {
                address = g.getFromLocation(latitude, longitude, 10);
            } catch(IOException e){
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (address != null){
                if (address.size() == 0){
                    System.out.println("해당되는 주소 정보는 없습니다.");
                }
                else{
                    current_location = address.get(0).getAddressLine(0).toString();
                }
            }
            else{
                System.out.println("주소값이 null입니다.");
            }
        }
    };
}