package com.example.team_six_fireball_project;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphFragment extends Fragment {

    private static final String TAG = "demo";
    BarChart barChart;
    ArrayList<FireBall> fireBallList;
    ArrayList barArrayList;
    IGraphFragment mGraphFragment;
    SeekBar seekBar;
    TextView seekBarYear, meteorCount;
    String[] list = {"dummy","jan", "feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    int year, jan=0, feb=0, mar=0, apr=0, may=0, jun=0, jul=0, aug=0, sep=0, oct=0, nov=0, dec=0;
    int northAmerica=0, southAmerica=0, asia=0, africa=0, americas=0, antarctica=0, europe=0, australia=0;
    PieChart pieChart;
    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 1;
    Button pieButton, histButton, heatMapButton;


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
        pieChart = view.findViewById(R.id.pieChart_view);
        barChart = view.findViewById(R.id.barChartGraphFrag);
        seekBar = view.findViewById(R.id.seekBarGraphFrag);
        seekBarYear = view.findViewById(R.id.textViewGraphFragYear);
        pieButton = view.findViewById(R.id.buttonGraphFragPieChart);
        histButton = view.findViewById(R.id.buttonGraphFragHIstogram);
        heatMapButton = view.findViewById(R.id.buttonGraphHeatMapTable);
        meteorCount = view.findViewById(R.id.textViewMeteorCount);

        //get fireball data
        fireBallList = mGraphFragment.getFireBallDataGraph();
        //Log.d(TAG, "onCreateView: "+fireBallList.get(0));

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        GraphRunnable graphRunnable = new GraphRunnable();
        executorService.execute(graphRunnable);

        return view;
    }

    class GraphRunnable implements Runnable{

        @Override
        public void run() {
            //thread start//
            sortFireBallListForPieChart();

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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBarYear.setText(Integer.toString(year));
                }
            });



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

            //Buttons to swap back and forth from pie to hist//
            histButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pieChart.setVisibility(View.INVISIBLE);
                    barChart.setVisibility(View.VISIBLE);
                    seekBarYear.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    meteorCount.setVisibility(View.VISIBLE);
                    histButton.setBackgroundColor(getActivity().getColor(R.color.button_yellow_grey2));
                    pieButton.setBackgroundColor(getActivity().getColor(R.color.yellow_button_header));
                    histButton.setClickable(false);
                    pieButton.setClickable(true);
                }
            });
            pieButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seekBarYear.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    pieChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.INVISIBLE);
                    meteorCount.setVisibility(View.INVISIBLE);
                    histButton.setBackgroundColor(getActivity().getColor(R.color.yellow_button_header));
                    pieButton.setBackgroundColor(getActivity().getColor(R.color.button_yellow_grey2));
                    pieButton.setClickable(false);
                    histButton.setClickable(true);
                    //Pie Chart//
                    sortFireBallListForPieChart();
                    showPieChart();
                    //end pie chart//
                }
            });
            heatMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGraphFragment.graphFragAlertUrl("Leaving FireBorn","Would you like to proceed to the Website?", "https://public.tableau.com/app/profile/anders.pierson/viz/FireBornVisualization/Dashboard1?publish=yes");
                }
            });
            //end button swap from pie to hist, hist to pie//
            //thread stop//
        }
    }

    public void sortFireBallListForPieChart(){
        int nullCount = 0;
        for (FireBall fireBall: fireBallList) {
            if (fireBall.getLat() != "null" || fireBall.getLon() != "null" || fireBall.getLatDir() != "null" || fireBall.getLonDir()!="null") {
               // Log.d(TAG, "sortFireBallListForPieChart: " + fireBall.getLat());
                double lat;
                double lon;
//                double lat = 0;
//                double lon = 0;
                String latDir = fireBall.getLatDir();
                String lonDir = fireBall.getLonDir();
                Double laat = Double.parseDouble(fireBall.getLat());
                Double lonn = Double.parseDouble(fireBall.getLon());

                if (latDir.compareTo("S") == 0 && laat > 0){
                    lat = 0 - Double.parseDouble(fireBall.getLat());
                }else{
                    lat = Double.parseDouble(fireBall.getLat());
                }

                if(lonDir.compareTo("W")==0 && lonn > 0){
                    lon = 0 - Double.parseDouble(fireBall.getLon());
                }else {
                    lon = Double.parseDouble(fireBall.getLon());
                }

//        [lat, log] -lat is south, -long is west
                if (lat <= 37 && lat >= -35 && lon <= 51 && lon >= -16) {
                    africa += 1;
                } else if (lat <= 77 && lat >= 7 && lon <= -38 && lon >= -179) {
                    northAmerica += 1;
                }else if (lat <= 16 && lat >= -56 && lon <= -25 && lon >= -85) {
                    southAmerica += 1;
                } else if (lat <= -60 && lat >= -90 && lon >= -171 && lon <= 160) {
                    antarctica += 1;
                } else if (lat <= 72 && lat >= 34 && lon <= 45 && lon >= -25) {
                    europe += 1;
                } else if (lat <= -1 && lat >= -59 && lon <= 179 && lon >= 90) {
                    australia += 1;
                } else if (lat <= 77 && lat >= 1 && lon >= 35 && lon <= 170) {
                    asia += 1;
                }
            } else {
                nullCount+=1;
            }
        }

    }

    private void showPieChart(){

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "type";

        //initializing data
        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Asia",asia);
        typeAmountMap.put("Africa",africa);
        typeAmountMap.put("North America",northAmerica);
        typeAmountMap.put("South America",southAmerica);
        typeAmountMap.put("Antarctica",antarctica);
        typeAmountMap.put("Europe",europe);
        typeAmountMap.put("Australia",australia);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));

        //input data and fit data into pie chart entry
        for(String type: typeAmountMap.keySet()){
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
        //setting text size of the value
        pieDataSet.setValueTextSize(18f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);

        pieChart.getDescription().setText("Number of Fireballs Per Continent");
        pieChart.getDescription().setTextSize(10f);
        pieChart.setData(pieData);
        //pieChart.setDrawHoleEnabled(false);
        pieChart.invalidate();
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
            GetDataRunnable getDataRunnable2 = new GetDataRunnable(yearForGraph);
            new Thread(getDataRunnable2).start();
        }
    }

    class GetDataRunnable implements Runnable{
        int yearForCount;
        public GetDataRunnable(int yearForGraph){
            this.yearForCount = yearForGraph;
        };

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


            int count = (jan+ feb+ mar+ apr+ may+ jun+ jul+ aug+ sep+ oct+ nov+ dec);

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

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    meteorCount.setText("FireBall count is " + count + " for " +yearForCount);
                }
            });
        }
    }

    interface IGraphFragment{
        ArrayList<FireBall> getFireBallDataGraph();
        void graphFragAlertUrl(String title, String message, String url);
    }
}