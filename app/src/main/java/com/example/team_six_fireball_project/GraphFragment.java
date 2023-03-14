package com.example.team_six_fireball_project;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphFragment extends Fragment {

    private static final String TAG = "demo";
    BarChart barChart;
    ArrayList<FireBall> fireBallList;
    ArrayList barArrayList;
    IGraphFragment mGraphFragment;
    SeekBar seekBar;
    TextView seekBarYear;
    String[] list = {"dummy","jan", "feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    int year, jan=0, feb=0, mar=0, apr=0, may=0, jun=0, jul=0, aug=0, sep=0, oct=0, nov=0, dec=0;
    BarDataSet barDataSet;
    BarData barData;
    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 1;



    public GraphFragment() {
    }

    public static GraphFragment newInstance() {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof GraphFragment.IGraphFragment) {
            mGraphFragment = (GraphFragment.IGraphFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        //attach
        barChart = view.findViewById(R.id.barChartGraphFrag);
        seekBar = view.findViewById(R.id.seekBarGraphFrag);
        seekBarYear = view.findViewById(R.id.textViewGraphFragYear);

        //get fireball data
        fireBallList = mGraphFragment.getFireBallDataGraph();
        //Log.d(TAG, "onCreateView: "+fireBallList.get(0));

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(1988);
        }
        int now = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now = LocalDate.now().getYear();
        } else {
            now = 2020;
        }
        seekBar.setMax(now);

        seekBar.setProgress(seekBar.getMax());
        year = seekBar.getProgress();
        getHistogramYear(year);
        seekBarYear.setText(Integer.toString(year));



        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(list));
        xAxis.setGranularity(2f);
        xAxis.setGranularityEnabled(true);

        barChart.getDescription().setEnabled(false);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarYear.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getHistogramYear(seekBar.getProgress());
            }
        });

        return view;
    }

    public void getHistogramYear(int yearForGraph){
        HistogramRunnable histogramRunnable = new HistogramRunnable(yearForGraph);
        executorService.execute(histogramRunnable);
    }

    class HistogramRunnable implements Runnable{
        int yearForGraph;
        public HistogramRunnable(int yearForGraph){
            this.yearForGraph = yearForGraph;
        };

        @Override
        public void run() {
            jan=0;
            feb=0;
            mar=0;
            apr=0;
            may=0;
            jun=0;
            jul=0;
            aug=0;
            sep=0;
            oct=0;
            nov=0;
            dec=0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

            Calendar calStartJan = Calendar.getInstance();
            calStartJan.set(yearForGraph,0,1,0,0,1);
            String strCalStartJan = dateFormat.format(calStartJan.getTime());

            Calendar calFeb = Calendar.getInstance();
            calFeb.set(yearForGraph,1,1,0,0,1);
            String strCalFeb = dateFormat.format(calFeb.getTime());

            Calendar calMar = Calendar.getInstance();
            calMar.set(yearForGraph,2,1,0,0,1);
            String strCalMar = dateFormat.format(calMar.getTime());

            Calendar calApr = Calendar.getInstance();
            calApr.set(yearForGraph,3,1,0,0,1);
            String strCalApr = dateFormat.format(calApr.getTime());

            Calendar calMay = Calendar.getInstance();
            calMay.set(yearForGraph,4,1,0,0,1);
            String strCalMay = dateFormat.format(calMay.getTime());

            Calendar calJun = Calendar.getInstance();
            calJun.set(yearForGraph,5,1,0,0,1);
            String strCalJun = dateFormat.format(calJun.getTime());

            Calendar calJul = Calendar.getInstance();
            calJul.set(yearForGraph,6,1,0,0,1);
            String strCalJul = dateFormat.format(calJul.getTime());

            Calendar calAug = Calendar.getInstance();
            calAug.set(yearForGraph,7,1,0,0,1);
            String strCalAug = dateFormat.format(calAug.getTime());

            Calendar calSep = Calendar.getInstance();
            calSep.set(yearForGraph,8,1,0,0,1);
            String strCalSep = dateFormat.format(calSep.getTime());

            Calendar calOct = Calendar.getInstance();
            calOct.set(yearForGraph,9,1,0,0,1);
            String strCalOct = dateFormat.format(calOct.getTime());

            Calendar calNov = Calendar.getInstance();
            calNov.set(yearForGraph,10,1,0,0,1);
            String strCalNov = dateFormat.format(calNov.getTime());

            Calendar calDec = Calendar.getInstance();
            calDec.set(yearForGraph,11,1,0,0,1);
            String strCalDec = dateFormat.format(calDec.getTime());

            Calendar calEnd = Calendar.getInstance();
            calEnd.set(yearForGraph+1,0,1,0,0,1);
            String strCalEnd = dateFormat.format(calEnd.getTime());

        /*
        dateFormat.format(cal.getTime());
        Log.d(TAG, "getHistogramYear: " + dateFormat.format(calEnd.getTime()));
         */
            //Log.d(TAG, "getHistogramYear: test 1: "+fireBallList.get(0).getDate());
            //Log.d(TAG, "getHistogramYear: test 2: "+strCalStartJan);
            for (FireBall fireBallTemp :fireBallList) {
                if (fireBallTemp.getDate().compareTo(strCalStartJan)>=0 && fireBallTemp.getDate().compareTo(strCalFeb)<0){
                    jan += 1;
                    //Log.d(TAG, "getHistogramYear: "+fireBallTemp.getDate());
                } else if (fireBallTemp.getDate().compareTo(strCalFeb)>=0 && fireBallTemp.getDate().compareTo(strCalMar)<0){
                    feb += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalMar)>=0 && fireBallTemp.getDate().compareTo(strCalApr)<0){
                    mar += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalApr)>=0 && fireBallTemp.getDate().compareTo(strCalMay)<0){
                    apr += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalMay)>=0 && fireBallTemp.getDate().compareTo(strCalJun)<0){
                    may += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalJun)>=0 && fireBallTemp.getDate().compareTo(strCalJul)<0){
                    jun += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalJul)>=0 && fireBallTemp.getDate().compareTo(strCalAug)<0){
                    jul += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalAug)>=0 && fireBallTemp.getDate().compareTo(strCalSep)<0){
                    aug += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalSep)>=0 && fireBallTemp.getDate().compareTo(strCalOct)<0){
                    sep += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalOct)>=0 && fireBallTemp.getDate().compareTo(strCalNov)<0){
                    oct += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalNov)>=0 && fireBallTemp.getDate().compareTo(strCalDec)<0){
                    nov += 1;
                } else if (fireBallTemp.getDate().compareTo(strCalDec)>=0 && fireBallTemp.getDate().compareTo(strCalEnd)<0){
                    dec += 1;
                }
            }
            getData();
        }
    }

    private void getData(){
       GetDataRunnable getDataRunnable2 = new GetDataRunnable();
       new Thread(getDataRunnable2).start();
    }

    class GetDataRunnable implements Runnable{

        @Override
        public void run() {
            Log.d(TAG, "getData: GOT Here");
            barArrayList = new ArrayList();
            barArrayList.add(new BarEntry(1,jan));
            barArrayList.add(new BarEntry(2,feb));
            barArrayList.add(new BarEntry(3,mar));
            barArrayList.add(new BarEntry(4,apr));
            barArrayList.add(new BarEntry(5,may));
            barArrayList.add(new BarEntry(6,jun));
            barArrayList.add(new BarEntry(7,jul));
            barArrayList.add(new BarEntry(8,aug));
            barArrayList.add(new BarEntry(9,sep));
            barArrayList.add(new BarEntry(10,oct));
            barArrayList.add(new BarEntry(11,nov));
            barArrayList.add(new BarEntry(12,dec));

            BarDataSet barDataSet = new BarDataSet(barArrayList, "Jan - Dec");
            BarData barData = new BarData(barDataSet);

            //set bar data set colors
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS); //setcolor sets one setcolors sets multiple
            //setting text color
            barDataSet.setValueTextColor(Color.BLACK);
            //setting text size
            barDataSet.setValueTextSize(12f);
            //set list
            barDataSet.setStackLabels(list);

            barChart.clear();
            if((jan+feb+mar+apr+may+jun+jul+aug+sep+oct+nov+dec) != 0) {
                barChart.setData(barData);
            }
        }
    }

    interface IGraphFragment{
        ArrayList<FireBall> getFireBallDataGraph();
    }
}