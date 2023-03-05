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
import java.util.List;

public class MapsFragment extends Fragment implements RecycleViewMapsAdapter.IRecycleViewMapsAdapter {

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
    RecycleViewMapsAdapter adapter;
    ArrayList<FireBall> fireBallList = new ArrayList<>();

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
    public View onCreateView( LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


        getActivity().setTitle("Meteor Map");
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);
        //Fireball array was mad in the strings.xml file, in the value folder
        fireballSort = view.getResources().getStringArray(R.array.FireBall_Sort);
        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.drop_down_menu_items, fireballSort);
        autoCompleteTextView.setAdapter(arrayAdapter);


        fireBallList = mMapsFragment.getFireBallList();
        recyclerView = view.findViewById(R.id.recycleViewMapsFrag);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewMapsAdapter(fireBallList, this);
        recyclerView.setAdapter(adapter);
        //Log.d(TAG, "onCreateView: " + fireBallList);
        return view;
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

    interface IMapsFragment{
        ArrayList<FireBall> getFireBallList();
    }
}
