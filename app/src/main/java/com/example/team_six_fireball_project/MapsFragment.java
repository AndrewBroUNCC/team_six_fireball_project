package com.example.team_six_fireball_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapsFragment extends Fragment implements AdapterView.OnItemClickListener, RecycleViewMapsAdapter.IRecycleViewMapsAdapter {

    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "demo";
    IMapsFragment mMapsFragment;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    String[] fireballSort;
    AutoCompleteTextView autoCompleteTextView;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecycleViewMapsAdapter adapter, adapter12, adapter6;
    ArrayList<FireBall> fireBallList = new ArrayList<>();
    ArrayList<FireBall> fireBallList6 = new ArrayList<>();
    ArrayList<FireBall> fireBallList12 = new ArrayList<>();
    int size;

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        getActivity().setTitle("Meteor Map");
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        //Fireball array was mad in the strings.xml file, in the value folder
        fireballSort = view.getResources().getStringArray(R.array.FireBall_Sort);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.drop_down_menu_items, fireballSort);
        autoCompleteTextView.setAdapter(arrayAdapter);



        Log.d(TAG, "onCreateView: "+arrayAdapter.getItemViewType(0));

        fireBallList = mMapsFragment.getFireBallList();
        size = fireBallList.size();

        recyclerView = view.findViewById(R.id.recycleViewMapsFrag);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewMapsAdapter(fireBallList, this);
        adapter12 = new RecycleViewMapsAdapter(fireBallList12, this);
        adapter6 = new RecycleViewMapsAdapter(fireBallList6, this);
        recyclerView.setAdapter(adapter);
        //Log.d(TAG, "onCreateView: " + fireBallList);

        //how to get what is clicked
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Log.d(TAG, "onItemClick: get string: "+adapterView.getItemAtPosition(i));
                // Log.d(TAG, "onItemClick: get position: "+ i);
                sortByStatus(recyclerView, position);
            }
        });


        return view;
    }

    public void sortByStatus(RecyclerView recyclerView2, int status){
        if (status == 0){
            //newest first
            fireBallList.sort(new Comparator<FireBall>() {
                @Override
                public int compare(FireBall fireBall, FireBall t1) {
                    return fireBall.getDate().compareTo(t1.getDate()) *-1;
                }
            });
            recyclerView.setAdapter(adapter);
        } else if(status == 1){
            //oldest first
            fireBallList.sort(new Comparator<FireBall>() {
                @Override
                public int compare(FireBall fireBall, FireBall t1) {
                    return fireBall.getDate().compareTo(t1.getDate()) * 1;
                }
            });
            recyclerView.setAdapter(adapter);
        } else if (status ==2){
            sortSixMonths();
        }else if (status == 3){
            sortTwelveMonths();
        }else if(status ==4){
            fireBallList.sort(new Comparator<FireBall>() {
                @Override
                public int compare(FireBall fireBall, FireBall t1) {

                    Double f1 = Double.parseDouble(fireBall.getImpactE());
                    Double f2 = Double.parseDouble(t1.getImpactE());
                    //Log.d(TAG, "compare: f1 "+ f1+ " f2 "+ f2);
                    if(f1>f2){
                        return -1;
                    } else if (f1<f2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }

            });
            recyclerView.setAdapter(adapter);
        }
        Log.d(TAG, "sortByStatus: fb1 "+fireBallList.size());
        Log.d(TAG, "sortByStatus: fb6 "+fireBallList6.size());
        Log.d(TAG, "sortByStatus: fb12 "+fireBallList12.size());
        Log.d(TAG, "sortByStatus: size " + size);
        adapter.notifyDataSetChanged();
    }

    public void sortTwelveMonths(){
        String date = mMapsFragment.getTwelveMonthsAgoDateMapsFrag();
        //Log.d(TAG, "sortTwo: date: "+ date);
        //Log.d(TAG, "sortTwo: date2: "+fireBallList.get(0).getDate());
        //past 6 months
        for (FireBall fireBall: fireBallList) {
            if (fireBall.getDate().compareTo(date) >= 0){
                fireBallList12.add(fireBall);
                //Log.d(TAG, "sortByStatus: fireball" + fireBall.getDate());
            }
        }

        fireBallList12.sort(new Comparator<FireBall>() {
            @Override
            public int compare(FireBall fireBall, FireBall t1) {
                return fireBall.getDate().compareTo(t1.getDate())*-1;
            }
        });
        recyclerView.setAdapter(adapter12);
    }

    public void sortSixMonths(){
        String date = mMapsFragment.getSixMonthsAgoDateMapsFrag();
        ArrayList<FireBall> temp = new ArrayList<>();
        //Log.d(TAG, "sortTwo: date: "+ date);
        //Log.d(TAG, "sortTwo: date2: "+fireBallList.get(0).getDate());
        //past 6 months
        for (FireBall fireBall: fireBallList) {
            if (fireBall.getDate().compareTo(date) >= 0){
                temp.add(fireBall);
                //Log.d(TAG, "sortByStatus: fireball" + fireBall.getDate());
            }
        }

        for (FireBall fireBallTemp:temp) {
            fireBallList6.add(fireBallTemp);
        }
        fireBallList6.sort(new Comparator<FireBall>() {
            @Override
            public int compare(FireBall fireBall, FireBall t1) {
                return fireBall.getDate().compareTo(t1.getDate())*-1;
            }
        });
        recyclerView.setAdapter(adapter6);
    }

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof MapsFragment.IMapsFragment) {
            mMapsFragment = (MapsFragment.IMapsFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    SupportMapFragment mapFragment;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Log.d(TAG, "onItemSelected: test 1 " + adapterView.getSelectedItem());
        //Log.d(TAG, "onItemSelected: test 2 "+adapterView.getItemAtPosition(i));
    }

    interface IMapsFragment{
        ArrayList<FireBall> getFireBallList();
        String getSixMonthsAgoDateMapsFrag();
        String getTwelveMonthsAgoDateMapsFrag();
        ArrayList<FireBall> getResetFireBallList();
    }
}
