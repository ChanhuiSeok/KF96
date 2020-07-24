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
import java.util.List;

public class GpsService extends Service {
    public Geocoder g ;
    double longitude; // 경도
    double latitude; // 위도
    public static String current_location; // 현재 위치
    public static String current_station; // 현재 측정소
    //public Mythread t1;
    public LocationManager lm;
    public LocationListener gpsLocationListener;
    public static GpsService serviceObj = null;

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
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        startForeground(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service onStartCommand 호출");
        serviceObj = this;

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
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D"); /*Service Key*/
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



    public void initGps(){
        // 위치 관리자 객체 참조
        System.out.println("initGps");
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        g = new Geocoder(this);
        gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();
                Log.d("change","onLocationChanged lng : "+longitude+" lat : "+latitude);
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


        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.mainActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            //txtResult.setText("위치정보");
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            assert location != null;
            System.out.println("location : "+location);
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
                        ArrayList<String> data = null;
                        try {
                            data = getXmlData(tm_pt.getX(), tm_pt.getY());
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
