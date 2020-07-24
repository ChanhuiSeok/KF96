package com.example.kf96_3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    TextView cur_location;
    TextView cur_dust;
    XmlPullParser xpp;

    String key = "CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D";
    String data;

    public HomeFragment() {
        // Required empty public constructor
    }

    // API로부터 데이터 받아오기
    public String getXmlData() throws IOException {
        StringBuffer buffer = new StringBuffer();
        String test_tmX = "244148.546388";
        String test_tmY = "412423.75772";
        String oper_ver = "1.0";
        StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getNearbyMsrstnList");
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=CWiUwqUoaLsPMRKzSVdqs4QtbeSFBCsdkmhLm9wVhQZT9nJYIL8jQBR9U6uKyhGEZoQSU2v4Yeh2yijtE7JBwA%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("tmX","UTF-8") + "=" + URLEncoder.encode("244148.546388", "UTF-8")); /*TM측정방식 X좌표*/
        urlBuilder.append("&" + URLEncoder.encode("tmY","UTF-8") + "=" + URLEncoder.encode("412423.75772", "UTF-8")); /*TM측정방식 Y좌표*/
        urlBuilder.append("&" + URLEncoder.encode("ver","UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        //System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
        //System.out.println(sb.toString());
        return sb.toString();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_home, container, false);

        cur_location = (TextView) view.findViewById(R.id.cur_location);
        cur_dust = (TextView) view.findViewById(R.id.cur_dust);



        return view;
    }
}