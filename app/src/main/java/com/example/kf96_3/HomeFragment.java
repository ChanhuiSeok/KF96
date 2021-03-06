package com.example.kf96_3;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.Intent;

import android.graphics.Color;
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
import android.widget.LinearLayout;
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
    TextView cur_dust_25;
    TextView cur_dust_10;
    TextView commonAir_textView;
    TextView commonNum_textView;
    TextView ment_textView;

    private Button serviceStartBtn;
    private Button serviceFinishBtn;
    XmlPullParser xpp;
    Intent serviceIntent;
    public static HomeFragment homeFragment;
    // 위치 txt
    private TextView lcoation_txt;
    String key = "CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D";
    String data;

    LinearLayout commonAirLayout;

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

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=null; //Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_home, container, false);

        homeFragment = this;
        cur_location = (TextView) view.findViewById(R.id.cur_location);
        cur_dust_10 = (TextView) view.findViewById(R.id.pm10Text);
        cur_dust_25 = (TextView) view.findViewById(R.id.pm25Text);
        Log.d("location","current_location : "+GpsService.current_location);
        cur_location.setText(GpsService.current_location);
        cur_dust_10.setText(GpsService.current_dust);
        cur_dust_25.setText(GpsService.current_little_dust);
        serviceStartBtn = (Button)view.findViewById(R.id.startService);
        serviceFinishBtn = (Button)view.findViewById(R.id.finishService);
        commonAir_textView = (TextView) view.findViewById(R.id.commonAir_textView);
        commonNum_textView = (TextView) view.findViewById(R.id.commonNum_textView);
        commonAirLayout = (LinearLayout) view.findViewById(R.id.commonAir_layout);
        ment_textView = (TextView) view.findViewById(R.id.ment_textView);


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

        Log.d("check","check : "+GpsService.check);
        commonNum_textView.setText(GpsService.number);
        if( (commonAir_textView.getText().toString().equals("좋음")) || (GpsService.check.equals("좋음"))){
            ment_textView.setText("오늘은 날씨가 좋아요! 나들이 어떠세요?");
            commonAir_textView.setText("좋음\uD83D\uDE04");
            commonAir_textView.setTextColor(Color.parseColor("#3F51B5"));
            commonNum_textView.setTextColor(Color.parseColor("#3F51B5"));
        }
        else if ((commonAir_textView.getText().toString().equals("보통")) || (GpsService.check.equals("보통"))){
            ment_textView.setText("공기가 다소 답답하고 별로입니다. 조심하세요.");
            commonAir_textView.setText("보통\uD83D\uDE10");
            commonAir_textView.setTextColor(Color.parseColor("#F84D17"));
            commonNum_textView.setTextColor(Color.parseColor("#F84D17"));
        } else if((commonAir_textView.getText().toString().equals("나쁨")) || (GpsService.check.equals("나쁨"))) {
            commonAir_textView.setText("나쁨");
            ment_textView.setText("오늘은 꼼짝도 하지 말고 집에 계세요!");
            commonAir_textView.setText("나쁨\uD83D\uDE21");
            commonAir_textView.setTextColor(Color.parseColor("#9E2134"));
            commonNum_textView.setTextColor(Color.parseColor("#9E2134"));
        } else{
            commonAir_textView.setTextColor(Color.parseColor("#9E2134"));
            commonNum_textView.setTextColor(Color.parseColor("#9E2134"));
        }

        // 테스트용
        //
        //
        /*
        String cur_dust_str = "측정일 : "+ MainActivity.current_dust_data.get("dataTime") + "\n" +
                " 오존 농도 : " + MainActivity.current_dust_data.get("o3Value") + "\n" +
                " 미세먼지(PM10) 농도 :" + MainActivity.current_dust_data.get("pm10Value") + "\n" +
                " 초미세먼지(PM2.5) 농도 :" + MainActivity.current_dust_data.get("pm25Value") +
                " 미세먼지(PM10) 등급 : " + MainActivity.current_dust_data.get("pm10Grade");
        cur_dust.setText(cur_dust_str);
        */

        return view;
    }

}