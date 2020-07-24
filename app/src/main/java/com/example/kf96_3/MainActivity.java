package com.example.kf96_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    HomeFragment homefrag;

    // getXmlData() : 측정소의 이름을 구하는 함수로, API로부터 데이터 받아오는 함수. 반환값은 리스트 형태이며, 리스트 맨 첫번째 요소를 활용하면 된다.
    public ArrayList<String> getXmlData() throws IOException {
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>();

        String test_tmX = "244148.546388";
        String test_tmY = "412423.75772";
        String oper_ver = "1.0";
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("tmX", "UTF-8") + "=" + URLEncoder.encode("244148.546388", "UTF-8")); /*TM측정방식 X좌표*/
        urlBuilder.append("&" + URLEncoder.encode("tmY", "UTF-8") + "=" + URLEncoder.encode("412423.75772", "UTF-8")); /*TM측정방식 Y좌표*/
        urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        System.out.print(sb.toString());

        // 파싱하기
        InputStream is = url.openStream(); //url위치로 입력스트림 연결
        try {
            boolean b_item = false;
            boolean b_addr = false;
            boolean b_station = false;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("item")) {
                            b_item = true;
                        }
                        if (xpp.getName().equals("addr")) {
                            b_addr = true;
                        }
                        if (xpp.getName().equals("stationName")) {
                            b_station = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (b_item) {
                            b_item = false;
                        }
                        if (b_addr) {
                            b_addr = false;
                        }
                        if (b_station) {
                            list.add(xpp.getText());
                            b_station = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("item"))
                            break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            //
        }
        return list; // 측정소 문자열 값을 반환한다.
    }


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

        if (savedInstanceState == null) {
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
                switch (id) {
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

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                } else {
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