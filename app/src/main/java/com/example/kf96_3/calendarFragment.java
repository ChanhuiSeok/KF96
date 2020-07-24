package com.example.kf96_3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

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
    TextView date_view;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public calendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment calendarFragment.
     */
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
        View view=null;//Fragment가 보여줄 View 객체를 참조할 참조변수
        view= inflater.inflate(R.layout.fragment_calendar, container, false);

        calender = (CalendarView) view.findViewById(R.id.calender); //Note this line
        date_view = (TextView) view.findViewById(R.id.date_view);
        // Add Listener in calendar
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            // In this Listener have one method and in this method we will get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Store the value of date with format in String type Variable
                // Add 1 in month because month index is start with 0
                String Date = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";

                // set this date in TextView for Display
                date_view.setText(Date);
            }
        });

        return view;
    }
}