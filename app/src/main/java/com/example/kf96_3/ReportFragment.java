package com.example.kf96_3;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFragment extends Fragment {
    LineChart lineChart;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = null;
        view = inflater.inflate(R.layout.fragment_report, container, false);

        lineChart = (LineChart) view.findViewById(R.id.chart2);
        setlineChart(0, 0, 0, 0, 1831, 2159, 0);
        // Inflate the layout for this fragment
        return view;
    }

    // 주간 데이터
    public void setlineChart(float Mon, float Tue, float Wed, float Thur, float Fri, float Sat, float Sun){

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(Mon, 0));   // 월
        entries.add(new Entry(Tue, 1));   // 화
        entries.add(new Entry(Wed, 2));   // 수
        entries.add(new Entry(Thur, 3));  // 목
        entries.add(new Entry(Fri, 4));   // 금
        entries.add(new Entry(Sat, 5));   // 토
        entries.add(new Entry(Sun, 6));   // 일
        LineDataSet dataset = new LineDataSet(entries, "value");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");
        labels.add("일");

        LineData data = new LineData(labels, dataset);
        dataset.setColor(Color.rgb(153, 0, 51));
        dataset.setCircleColor(Color.rgb(0, 0, 0));
        lineChart.setData(data);
        lineChart.animateY(1500);
    }
}