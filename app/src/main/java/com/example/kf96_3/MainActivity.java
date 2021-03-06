package com.example.kf96_3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    HomeFragment homefrag;
    public static MainActivity mainActivity;

    double longitude; // 경도
    double latitude; // 위도
    public static String current_location; // 현재 위치
    // Geocoder 객체 생성
    public final Geocoder g = new Geocoder(this);
    // 위치 텍스트
    private TextView txtResult;
    public static String current_station; // 현재 측정소
    public static HashMap<String, String> current_dust_data = new HashMap<String, String>(); // 현재 미세먼지값 딕셔너리

    // getDustXmlData(): getStationXmlData() 함수로 받아온 측정소 값을 기준으로 미세먼지 정보를 받아오는 함수
    // Map 형태이므로 항목 값을 가져오고 싶을 경우 get("키값")으로 접근할 수 있다.
    public HashMap<String, String> getDustXmlData(String station_name) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=zO%2FALX92DLcoHKJkPghBFL%2FX9Uv00qqrvM9rVGH7n60Wz0k9hlNpPiNwMLDndeechzzKWHExU2kJ8zXL%2FaUJRw%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(station_name, "UTF-8")); /*측정소 이름*/
        urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간 (하루 : DAILY, 한달 : MONTH, 3달 : 3MONTH)*/
        urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.3", "UTF-8")); /*버전별 상세 결과 참고문서 참조*/
        URL url = new URL(urlBuilder.toString());
        System.out.println("url = " + url);
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

        HashMap<String, String> map = new HashMap<String, String>();
        // 파싱하기
        InputStream is = url.openStream(); //url위치로 입력스트림 연결
        try {
            boolean b_dataTime = false;
            boolean b_mangName = false;
            boolean b_so2Value = false;
            boolean b_coValue = false;
            boolean b_o3Value = false;
            boolean b_no2Value = false;
            boolean b_pm10Value = false;
            boolean b_pm10Value24 = false;
            boolean b_pm25Value = false;
            boolean b_pm25Value24 = false;
            boolean b_khaivalue = false;
            boolean b_khaiGrade = false;
            boolean b_so2Grade = false;
            boolean b_coGrade = false;
            boolean b_o3Grade = false;
            boolean b_no2Grade = false;
            boolean b_pm10Grade = false;
            boolean b_pm25Grade = false;
            boolean b_pm10Grade1h = false;
            boolean b_pm25Grade1h = false;
            boolean b_item = true;

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();
            boolean cur_flag = false;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals("item")) {
                            b_item = true;
                        }
                        if (xpp.getName().equals("dataTime")) {
                            b_dataTime = true;
                        }
                        if (xpp.getName().equals("mangName")) {
                            b_mangName = true;
                        }
                        if (xpp.getName().equals("so2Value")) {
                            b_so2Value = true;
                        }
                        if (xpp.getName().equals("o3Value")) {
                            b_o3Value = true;
                        }
                        if (xpp.getName().equals("no2Value")) {
                            b_no2Value = true;
                        }
                        if (xpp.getName().equals("pm10Value")) {
                            b_pm10Value = true;
                        }
                        if (xpp.getName().equals("pm10Value24")) {
                            b_pm10Value24 = true;
                        }
                        if (xpp.getName().equals("pm25Value")) {
                            b_pm25Value = true;
                        }
                        if (xpp.getName().equals("pm25Value24")) {
                            b_pm25Value24 = true;
                        }
                        if (xpp.getName().equals("khaiValue")) {
                            b_khaivalue = true;
                        }
                        if (xpp.getName().equals("khaiGrade")) {
                            b_khaiGrade = true;
                        }
                        if (xpp.getName().equals("so2Grade")) {
                            b_so2Grade = true;
                        }
                        if (xpp.getName().equals("coGrade")) {
                            b_coGrade = true;
                        }
                        if (xpp.getName().equals("o3Grade")) {
                            b_o3Grade = true;
                        }
                        if (xpp.getName().equals("no2Grade")) {
                            b_no2Grade = true;
                        }
                        if (xpp.getName().equals("pm10Grade")) {
                            b_pm10Grade = true;
                        }
                        if (xpp.getName().equals("pm25Grade")) {
                            b_pm25Grade = true;
                        }
                        if (xpp.getName().equals("pm10Grade1h")) {
                            b_pm10Grade1h = true;
                        }
                        if (xpp.getName().equals("pm25Grade1h")) {
                            b_pm25Grade1h = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (b_item) {
                            b_item = false;
                        }
                        if (b_dataTime) {
                            map.put("dataTime",xpp.getText());
                            b_dataTime = false;
                        }
                        if (b_mangName) {
                            map.put("mangName",xpp.getText());
                            b_mangName = false;
                        }
                        if (b_so2Value) {
                            if(xpp.getText().equals("-"))
                                map.put("so2Value","0");
                            else
                                map.put("so2Value",xpp.getText());
                            b_so2Value = false;
                        }
                        if (b_coValue) {
                            if(xpp.getText().equals("-"))
                                map.put("coValue","0");
                            else
                                map.put("coValue",xpp.getText());
                            b_coValue = false;
                        }
                        if (b_o3Value) {
                            if(xpp.getText().equals("-"))
                                map.put("o3Value","0");
                            else
                                map.put("o3Value",xpp.getText());
                            b_o3Value = false;
                        }
                        if (b_no2Value) {
                            if(xpp.getText().equals("-"))
                                map.put("no2Value","0");
                            else
                                map.put("no2Value",xpp.getText());
                            b_no2Value = false;
                        }
                        if (b_pm10Value) {
                            if(xpp.getText().equals("-"))
                                map.put("pm10Value","0");
                            else
                                map.put("pm10Value",xpp.getText());
                            b_pm10Value = false;
                        }
                        if (b_pm10Value24) {
                            if(xpp.getText().equals("-"))
                                map.put("pm10Value24","0");
                            else
                                map.put("pm10Value24",xpp.getText());
                            b_pm10Value24 = false;
                        }
                        if (b_pm25Value) {
                            if(xpp.getText().equals("-"))
                                map.put("pm25Value","0");
                            else
                                map.put("pm25Value",xpp.getText());
                            b_pm25Value = false;
                        }
                        if (b_pm25Value24) {
                            if(xpp.getText().equals("-"))
                                map.put("pm25Value24","0");
                            else
                                map.put("pm25Value24",xpp.getText());
                            b_pm25Value24 = false;
                        }
                        if (b_khaivalue) {
                            if(xpp.getText().equals("-"))
                                map.put("khaiValue","0");
                            else
                                map.put("khaiValue",xpp.getText());
                            b_khaivalue = false;
                        }
                        if (b_khaiGrade) {
                            map.put("khaiGrade",xpp.getText());
                            b_khaiGrade = false;
                        }
                        if (b_so2Grade) {
                            map.put("so2Grade",xpp.getText());
                            b_so2Grade = false;
                        }
                        if (b_coGrade) {
                            map.put("coGrade",xpp.getText());
                            b_coGrade = false;
                        }
                        if (b_o3Grade) {
                            map.put("o3Grade",xpp.getText());
                            b_o3Grade = false;
                        }
                        if (b_no2Grade) {
                            map.put("no2Grade",xpp.getText());
                            b_no2Grade = false;
                        }
                        if (b_pm10Grade) {
                            map.put("pm10Grade",xpp.getText());
                            b_pm10Grade = false;
                        }
                        if (b_pm25Grade) {
                            map.put("pm25Grade",xpp.getText());
                            b_pm25Grade = false;
                        }
                        if (b_pm10Grade1h) {
                            map.put("pm10Grade1h",xpp.getText());
                            b_pm10Grade1h = false;
                        }
                        if (b_pm25Grade1h) {
                            map.put("pm25Grade1h",xpp.getText());
                            b_pm25Grade1h = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("item")){
                            cur_flag = true;
                            break;
                        }
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                if (cur_flag == true) break;
                else eventType = xpp.next();
            }

        } catch (Exception e) {
            //
        }
        return map; // 측정소 문자열 값을 반환한다.
    }

    // getXmlData() : 측정소의 이름을 구하는 함수로, API로부터 데이터 받아오는 함수. 반환값은 리스트 형태이며, 리스트 맨 첫번째 요소를 활용하면 된다.
    public ArrayList<String> getXmlData(double temp_tmX, double temp_tmY) throws IOException {
        Log.d("test", "lng : " + temp_tmX + " lat : " + temp_tmY);
        StringBuffer buffer = new StringBuffer();
        ArrayList<String> list = new ArrayList<String>();

        String test_tmX = Double.toString(temp_tmX);
        String test_tmY = Double.toString(temp_tmY);
        String oper_ver = "1.0";
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=zO%2FALX92DLcoHKJkPghBFL%2FX9Uv00qqrvM9rVGH7n60Wz0k9hlNpPiNwMLDndeechzzKWHExU2kJ8zXL%2FaUJRw%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("tmX", "UTF-8") + "=" + URLEncoder.encode(test_tmX, "UTF-8")); /*TM측정방식 X좌표*/
        urlBuilder.append("&" + URLEncoder.encode("tmY", "UTF-8") + "=" + URLEncoder.encode(test_tmY, "UTF-8")); /*TM측정방식 Y좌표*/
        //urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8"));

        Log.d("test", "url : " + urlBuilder.toString());
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_nav);
        mainActivity = this;

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
        // 데이터베이스 객체 및 DB에 넣는 코드 부분/////////////////////
        /*
        DBHelper helper = new DBHelper(this);
        SQLiteDatabase db = helper.getInstance(this).getWritableDatabase();
        // 밑의 insert는 임의로 넣은 것이며, 주기적으로 데이터를 DB에 넣는 부분을 서비스 등으로 돌려야 함.
        try {
            db.execSQL("insert into tb_dust (_date, pm10Value) values (?,?)",
                    new String[]{"2020-07-24 23:00", "10"});
        }catch(Exception e){
            System.out.println("이미 데이터가 있습니다");
        }
        db.close();*/
    }

    public  void initGps(){
        // 위치 관리자 객체 참조
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    0);
            //txtResult.setText("위치정보");
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            assert location != null;
            if (location != null) {
                String provider = location.getProvider();   // GPS
                longitude = location.getLongitude(); // 경도
                latitude = location.getLatitude();   // 위도
                double altitude = location.getAltitude();   // 고도
                current_location = null;
                List<Address> address = null;

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
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                }
                if (address != null) {
                    if (address.size() == 0) {
                        System.out.println("해당하는 주소가 없습니다.");
                    } else {
                        current_location = address.get(0).getAddressLine(0).toString();
                    }
                } else {
                    System.out.println("주소값이 null입니다.");
                }

                Log.d("test", "lng : " + longitude + "lat : " + latitude);
                GeoPoint in_pt = new GeoPoint(longitude, latitude);
                final GeoPoint tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);

                // http 통신에는 thread 가 새로 필요하다.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test", "thread run 시작");
                        ArrayList<String> data = null;
                        try {
                            data = getXmlData(tm_pt.getX(), tm_pt.getY());
                            current_station = data.get(0).toString();
                            current_dust_data = getDustXmlData(current_station);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }



    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            Log.d("change","onLocationChanged");
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();

            List<Address> address = null;

            try {
                address = g.getFromLocation(latitude, longitude, 10);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
            }
            if (address != null) {
                if (address.size() == 0) {
                    System.out.println("해당되는 주소 정보는 없습니다.");
                } else {
                    current_location = address.get(0).getAddressLine(0).toString();
                }
            } else {
                System.out.println("주소값이 null입니다.");
            }
        }
    };
}
