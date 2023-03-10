package com.example.team_six_fireball_project;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphFragment extends Fragment {

    BarChart barChart;
    ArrayList barArrayList;
    IGraphFragment mGraphFragment;


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

        barChart = view.findViewById(R.id.barChartGraphFrag);

        String[] list = {"dummy","jan", "feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        getData();

        BarDataSet barDataSet = new BarDataSet(barArrayList, "Jan - Dec");
        BarData barData = new BarData(barDataSet);

        barChart.setData(barData);
        //set bar data set colors
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS); //setcolor sets one setcolors sets multiple
        //setting text color
        barDataSet.setValueTextColor(Color.BLACK);
        //setting text size
        barDataSet.setValueTextSize(16f);
        //set list
        barDataSet.setStackLabels(list);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(list));
        xAxis.setGranularity(2f);
        xAxis.setGranularityEnabled(true);

        barChart.getDescription().setEnabled(false);

        return view;
    }

    private void getData(){
        barArrayList = new ArrayList();
        barArrayList.add(new BarEntry(1,5));
        barArrayList.add(new BarEntry(2,10));
        barArrayList.add(new BarEntry(3,20));
        barArrayList.add(new BarEntry(4,30));
        barArrayList.add(new BarEntry(5,40));
        barArrayList.add(new BarEntry(6,50));
        barArrayList.add(new BarEntry(7,10));
        barArrayList.add(new BarEntry(8,20));
        barArrayList.add(new BarEntry(9,30));
        barArrayList.add(new BarEntry(10,40));
        barArrayList.add(new BarEntry(11,50));
        barArrayList.add(new BarEntry(12,10));
    }

    interface IGraphFragment{
        ArrayList<FireBall> getFireBallDataGraph();
    }
}