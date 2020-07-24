package com.example.kf96_3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Intent;

import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    TextView cur_location;
    TextView cur_dust;
    private Button serviceStartBtn;
    private Button serviceFinishBtn;
    XmlPullParser xpp;
    Intent serviceIntent;
    public static HomeFragment homeFragment;
    // 위치 txt
    private TextView lcoation_txt;
    String key = "CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D";
    String data;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate" + MainActivity.current_location);
        // cur_location.setText(MainActivity.current_location);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null; //Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_home, container, false);

        homeFragment = this;
        cur_location = (TextView) view.findViewById(R.id.cur_location);
        cur_dust = (TextView) view.findViewById(R.id.cur_dust);
        serviceStartBtn = (Button)view.findViewById(R.id.startService);
        serviceFinishBtn = (Button)view.findViewById(R.id.finishService);

        serviceStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Btn","startBtn");
                if(GpsService.serviceObj == null) {
                    //GpsService.serviceFlag = 1;
                    serviceIntent = new Intent(getActivity(), GpsService.class);
                    if (Build.VERSION.SDK_INT >= 26) {
                        System.out.println("26이상");
                        getContext().startForegroundService(serviceIntent);
                    } else {
                        System.out.println("26이하");
                        getContext().startService(serviceIntent);
                    }
                }
            }
        });

        serviceFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Btn","finishBtn");
                if(GpsService.serviceObj != null){
                    System.out.println("serviceintent not null");
                    GpsService.serviceObj.onDestroy();
                }

            }
        });

        Log.d("test","cur_location set text");
        cur_location.setText(MainActivity.current_location);

        // 테스트용
        String cur_dust_str = "측정일 : "+ MainActivity.current_dust_data.get("dataTime") + "\n" +
                " 오존 농도 : " + MainActivity.current_dust_data.get("o3Value") + "\n" +
                " 미세먼지(PM10) 농도 :" + MainActivity.current_dust_data.get("pm10Value") + "\n" +
                " 초미세먼지(PM2.5) 농도 :" + MainActivity.current_dust_data.get("pm25Value") +
                " 미세먼지(PM10) 등급 : " + MainActivity.current_dust_data.get("pm10Grade");
        cur_dust.setText(cur_dust_str);

        return view;
    }

}