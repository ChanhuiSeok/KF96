package com.example.kf96_3;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link calendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class calendarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    CalendarView calender;
    TextView date_view, date_detail_view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public calendarFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static calendarFragment newInstance(String param1, String param2) {
        calendarFragment fragment = new calendarFragment();
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
        // Inflate the layout for this fragment
        View view = null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calender = (CalendarView) view.findViewById(R.id.calender); //Note this line
        date_view = (TextView) view.findViewById(R.id.date_view);
        date_detail_view = (TextView) view.findViewById(R.id.date_detail_view);

        // Add Listener in calendar
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            DBHelper helper = new DBHelper(getActivity());
            SQLiteDatabase db = helper.getWritableDatabase();
            ArrayList<String> result = new ArrayList<String>();

            @Override
            // In this Listener have one method and in this method we will get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Store the value of date with format in String type Variable
                // Add 1 in month because month index is start with 0
                String Date;
                String c_day;
                if (dayOfMonth < 10) c_day = "0" + (dayOfMonth);
                else c_day = Integer.toString(dayOfMonth);

                if (month < 10) {
                    Date = year + "-" + "0" + (month + 1) + "-" + c_day;
                } else {
                    Date = year + "-" + (month + 1) + "-" + c_day;
                }
                // set this date in TextView for Display
                date_view.setText(Date);

                // 날짜값에 대한 DB를 불러오도록 한다. 밑의 코드는 임의로 두 컬럼만 받아오는 것이다.
                Cursor cursor = db.rawQuery("select _date, pm10Value from tb_dust where _date like '" + Date + "%'", null);
                if (cursor.moveToFirst()) {
                    result.add(cursor.getString(0));
                    result.add(cursor.getString(1));
                    date_detail_view.setText("날짜 : " + result.get(0) + "& pm10 농도 :" + result.get(1));
                } else {
                    date_detail_view.setText("데이터가 없습니다!");
                }

                // 여기서 db를 계속 닫으면 오류가 발생하므로 주석처리 한다.
                //db.close();
            }
        });

        return view;
    }
}