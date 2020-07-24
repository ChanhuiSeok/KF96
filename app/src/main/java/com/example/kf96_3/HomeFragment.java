package com.example.kf96_3;

import android.content.Context;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class HomeFragment extends Fragment {

    TextView cur_location;
    TextView cur_dust;
    XmlPullParser xpp;

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

        cur_location = (TextView) view.findViewById(R.id.cur_location);
        cur_dust = (TextView) view.findViewById(R.id.cur_dust);
        Log.d("test","cur_location set text");
        cur_location.setText(MainActivity.current_location);
        return view;
    }
}