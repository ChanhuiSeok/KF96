package com.example.kf96_3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;

public class Graph extends AppCompatActivity {


    public static void setBarChart() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        setBarChart(30, 20, 3, 4);
        setlineChart(1, 2, 4, 7, 5, 6, 7);
    }

    // 일일 데이터
    public void setBarChart(int good, int normal, int bad, int very_bad){
        BarChart chart;
        chart = (BarChart) findViewById(R.id.chart);

        chart.clearChart();
        chart.addBar(new BarModel("좋음", good, Color.rgb(0, 102, 204)));
        chart.addBar(new BarModel("보통", normal, Color.rgb(102, 204, 0)));
        chart.addBar(new BarModel("나쁨", bad, Color.rgb(255, 102, 0)));
        chart.addBar(new BarModel("매우나쁨", very_bad, Color.rgb(255, 80, 80)));

        chart.startAnimation();
    }

    // 주간 데이터
    public void setlineChart(float Mon, float Tue, float Wed, float Thur, float Fri, float Sat, float Sun){
        LineChart lineChart;
        lineChart = (LineChart) findViewById(R.id.chart2);

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
        dataset.setColor(Color.rgb(153, 0, 051));
        dataset.setCircleColor(Color.rgb(0, 0, 0));
        lineChart.setData(data);
        lineChart.animateY(1500);
    }
}