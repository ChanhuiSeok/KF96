package com.example.kf96_3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ismaeldivita.chipnavigation.view.HorizontalMenuItemView;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GpsService extends Service {
    public Geocoder g;
    double longitude; // 경도
    double latitude; // 위도
    public static String current_location; // 현재 위치
    public static String current_station; // 현재 측정소
    //public Mythread t1;
    public LocationManager lm;
    public static LocationListener gpsLocationListener;
    public static GpsService serviceObj = null;
    public HashMap<String, Integer> result;

    // DB에 전달하기 위한 String
    public static String db_value;

    public GpsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    void startForegroundService() {

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "channeld";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "name",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        startForeground(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service onStartCommand 호출");
        serviceObj = this;

        // null 초기화
        db_value = null;

        initGps();
        // t1 = new Mythread();
        // t1.start();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("service onDestroy 호출");
        serviceObj = null;
        // t1 = null;
        lm.removeUpdates(gpsLocationListener);
        stopSelf();

        String s1 = "미세먼지 = " + result.get("미세먼지합계") + "\n" + "초미세먼지 = " + result.get("초미세먼지합계");
        String s2 = result.get("좋음") + "@" + result.get("보통") + "@" + result.get("나쁨") + "@" + result.get("매우나쁨");

        System.out.println(s1);
        System.out.println(s2);
        // db insert

    }

    /*class Mythread extends Thread{
        public void run(){
            initGps();
        }
    }*/

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

    public HashMap<String, String> getDustXmlData(String station_name) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D"); /*Service Key*/
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
                            map.put("dataTime", xpp.getText());
                            b_dataTime = false;
                        }
                        if (b_mangName) {
                            map.put("mangName", xpp.getText());
                            b_mangName = false;
                        }
                        if (b_so2Value) {
                            map.put("so2Value", xpp.getText());
                            b_so2Value = false;
                        }
                        if (b_coValue) {
                            map.put("coValue", xpp.getText());
                            b_coValue = false;
                        }
                        if (b_o3Value) {
                            map.put("o3Value", xpp.getText());
                            b_o3Value = false;
                        }
                        if (b_no2Value) {
                            map.put("no2Value", xpp.getText());
                            b_no2Value = false;
                        }
                        if (b_pm10Value) {
                            map.put("pm10Value", xpp.getText());
                            b_pm10Value = false;
                        }
                        if (b_pm10Value24) {
                            map.put("pm10Value24", xpp.getText());
                            b_pm10Value24 = false;
                        }
                        if (b_pm25Value) {
                            map.put("pm25Value", xpp.getText());
                            b_pm25Value = false;
                        }
                        if (b_pm25Value24) {
                            map.put("pm25Value24", xpp.getText());
                            b_pm25Value24 = false;
                        }
                        if (b_khaivalue) {
                            map.put("khaiValue", xpp.getText());
                            b_khaivalue = false;
                        }
                        if (b_khaiGrade) {
                            map.put("khaiGrade", xpp.getText());
                            b_khaiGrade = false;
                        }
                        if (b_so2Grade) {
                            map.put("so2Grade", xpp.getText());
                            b_so2Grade = false;
                        }
                        if (b_coGrade) {
                            map.put("coGrade", xpp.getText());
                            b_coGrade = false;
                        }
                        if (b_o3Grade) {
                            map.put("o3Grade", xpp.getText());
                            b_o3Grade = false;
                        }
                        if (b_no2Grade) {
                            map.put("no2Grade", xpp.getText());
                            b_no2Grade = false;
                        }
                        if (b_pm10Grade) {
                            map.put("pm10Grade", xpp.getText());
                            b_pm10Grade = false;
                        }
                        if (b_pm25Grade) {
                            map.put("pm25Grade", xpp.getText());
                            b_pm25Grade = false;
                        }
                        if (b_pm10Grade1h) {
                            map.put("pm10Grade1h", xpp.getText());
                            b_pm10Grade1h = false;
                        }
                        if (b_pm25Grade1h) {
                            map.put("pm25Grade1h", xpp.getText());
                            b_pm25Grade1h = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equals("item")) {
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


    public void initGps() {

        result = new HashMap<>();
        result.put("좋음", 0);
        result.put("보통", 0);
        result.put("나쁨", 0);
        result.put("매우나쁨", 0);
        result.put("미세먼지합계", 0);
        result.put("초미세먼지합계", 0);

        // 위치 관리자 객체 참조
        System.out.println("initGps");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        g = new Geocoder(this);
        gpsLocationListener = new LocationListener() {
            public void onLocationChanged(@NotNull Location location) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
                Log.d("change", "onLocationChanged lng : " + longitude + " lat : " + latitude);
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
                GeoPoint in_pt = new GeoPoint(longitude, latitude);
                final GeoPoint tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);

                // http 통신에는 thread 가 새로 필요하다.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("test", "thread run 시작");
                        try {
                            final ArrayList<String> data = getXmlData(tm_pt.getX(), tm_pt.getY());
                            final HashMap<String, String> value = getDustXmlData(data.get(0).toString());

                            System.out.println("value" + value);
                            System.out.println(value.get("pm10Value"));
                            // pm10Value
                            String pm10Value = "" + value.get("pm10Value");
                            String pm10Grade = "" + value.get("pm10Grade");
                            String pm25Value = "" + value.get("pm25Value");
                            String pm25Grade = (String) value.get("pm25Grade");
                            System.out.println(pm10Value);
                            System.out.println(pm10Grade);
                            System.out.println(pm25Value);
                            System.out.println(pm25Grade);

                            // 카운트
                            if (Integer.parseInt(pm25Grade) == 1) {
                                result.put("좋음", result.get("좋음") + 1);
                            } else if (Integer.parseInt(pm25Grade) == 2) {
                                result.put("보통", result.get("보통") + 1);
                            } else if (Integer.parseInt(pm25Grade) == 3) {
                                result.put("나쁨", result.get("나쁨") + 1);
                            } else if (Integer.parseInt(pm25Grade) == 4) {
                                result.put("매우나쁨", result.get("매우나쁨") + 1);
                            }

                            // 합계 계산
                            result.put("미세먼지합계", result.get("미세먼지합계") + Integer.parseInt(pm10Value));
                            result.put("초미세먼지합계", result.get("초미세먼지합계") + Integer.parseInt(pm25Value));

                            MainActivity.mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HomeFragment.homeFragment.cur_dust.setText(value.get("pm10Value"));
                                    HomeFragment.homeFragment.cur_location.setText(current_location);
                                }
                            });
                            current_station = data.get(0).toString();
                            Log.d("test", "current_station : " + current_station);
                            System.out.println("current_station : " + current_station);
                            //data[0] 값이 측정소의 위치이다.

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            //txtResult.setText("위치정보");
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            assert location != null;
            System.out.println("location : " + location);
            if (location != null) {

                String provider = location.getProvider();   // GPS
                longitude = location.getLongitude(); // 경도
                latitude = location.getLatitude();   // 위도
                double altitude = location.getAltitude();   // 고도
                current_location = null;
                List<Address> address = null;

                // 위치정보 업데이트 요청
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, // 최소시간
                        1,  // 최소거리
                        gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        5000,
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
                        try {
                            final ArrayList<String> data = getXmlData(tm_pt.getX(), tm_pt.getY());
                            final HashMap<String, String> value = getDustXmlData(data.get(0).toString());

                            MainActivity.mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    HomeFragment.homeFragment.cur_dust.setText(value.get("pm10Value"));
                                    HomeFragment.homeFragment.cur_location.setText(current_location);
                                    HomeFragment.homeFragment.commonNum_textView.setText(value.get("khaiValue"));
                                    HomeFragment.homeFragment.commonAir_textView.setText(value.get("khaiGrade"));

                                }
                            });
                            current_station = data.get(0).toString();
                            Log.d("test", "current_station : " + current_station);
                            System.out.println("current_station : " + current_station);
                            //data[0] 값이 측정소의 위치이다.

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }
}

