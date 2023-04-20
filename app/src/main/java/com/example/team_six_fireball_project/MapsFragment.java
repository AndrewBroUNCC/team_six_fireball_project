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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;

import okhttp3.OkHttpClient;

public class MapsFragment extends Fragment implements AdapterView.OnItemClickListener, RecycleViewMapsAdapter.IRecycleViewMapsAdapter {

    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "demo";
    IMapsFragment mMapsFragment;
    GoogleMap googleMap;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        //map markers get placed
        @Override
        public void onMapReady(GoogleMap googleMapReady) {

            googleMap = googleMapReady;
            setMapMarkers(googleMap, fireBallList);
//            //==how to create a marker==
//            LatLng sydney = new LatLng(-34, 151);
//            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//
//            //how to moveCamera==
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
        fireBallList = mMapsFragment.getFireBallList();

        setMapMarkers(googleMap, fireBallList);
//        callback = new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {
//
//                googleMap.clear();
//                LatLng ballMaker = new LatLng(0,0);
//                int count = 0;
//
//                for (FireBall fireballMarker: fireBallList) {
//                    if (fireballMarker.getLat() != "null" || fireballMarker.getLon() != "null") {
//                        count +=1;
//                        //==how to create a marker==
//                        ballMaker = new LatLng(Double.parseDouble(fireballMarker.getLat()), Double.parseDouble(fireballMarker.getLon()));
//                        googleMap.addMarker(new MarkerOptions().position(ballMaker).title("FireBall: "+count+" Impact Power: " + fireballMarker.getImpactE()));
//                    }
//                }
//
//                //how to moveCamera==
//                googleMap.getUiSettings().setZoomControlsEnabled(true);
//                //googleMap.moveCamera(CameraUpdateFactory.zoomTo(0));
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ballMaker, 4));
//                /*animate
//                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 5000, null);
//                 */
//            }
//        };

        Log.d(TAG, "onCreateView: "+arrayAdapter.getItemViewType(0));

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
        //to reload full list
        if (status== 0 || status == 1 || status == 4){
            setMapMarkers(googleMap,fireBallList);
        }
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
        adapter.notifyDataSetChanged();
    }

    public void sortTwelveMonths(){
        fireBallList12.clear();
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
        setMapMarkers(googleMap, fireBallList12);
    }

    public void sortSixMonths(){
        fireBallList6.clear();
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
        setMapMarkers(googleMap, fireBallList6);
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

    //call and pass fireball list to clear map and reload with new list and sets markers
    public void setMapMarkers(GoogleMap googleMap, ArrayList<FireBall> FireballsForMarkers) {
//        callback = new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {

                if(googleMap != null) {
                    googleMap.clear();
                    LatLng ballMaker = new LatLng(0, 0);
                    int count = 0;

                    for (FireBall fireballMarker : FireballsForMarkers) {
                        if (fireballMarker.getLat() != "null" || fireballMarker.getLon() != "null") {
                            count += 1;
                            //==how to create a marker==
                            ballMaker = new LatLng(Double.parseDouble(fireballMarker.getLat()), Double.parseDouble(fireballMarker.getLon()));
                            googleMap.addMarker(new MarkerOptions().position(ballMaker).title("FireBall- Lat:" + fireballMarker.getLat()+" Lon: "+ fireballMarker.getLon() + " Impact Power: " + fireballMarker.getImpactE()));
                        }
                    }
                    //==how to moveCamera==
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    //googleMap.moveCamera(CameraUpdateFactory.zoomTo(0));
                    if (count == 1) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ballMaker, 4));
                    }
                /*  ==animate==
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(16), 5000, null);
                 */
                    //  }
                    //};
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

    //this is where the fireball data from the recycle view gets passed back to maps fragment.
    @Override
    public void goToFireBallOnMap(FireBall fireBallFromMap) {
        ArrayList<FireBall> mapList = new ArrayList<>();
        mapList.add(fireBallFromMap);
        setMapMarkers(googleMap, mapList);
    }

    interface IMapsFragment{
        ArrayList<FireBall> getFireBallList();
        String getSixMonthsAgoDateMapsFrag();
        String getTwelveMonthsAgoDateMapsFrag();
    }
}
