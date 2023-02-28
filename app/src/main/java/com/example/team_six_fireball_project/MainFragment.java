package com.example.team_six_fireball_project;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    int alert;
    FirebaseAuth mAuth;
    TextView textViewLoginShow, textViewLogout;
    IMainFragment mMainFragment;

    public MainFragment() {
        // Required empty public constructor
        alert = 0;
    }

    public MainFragment(int i) {
        alert = i;
    }




    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof MainFragment.IMainFragment) {
            mMainFragment = (MainFragment.IMainFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //testing date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        Log.d(TAG, "onCreateView: "+ (dateFormat.format(date)));
//        String date = dateFormat.format(new Date());
//        Log.d(TAG, "onCreateView: " + date);

        getActivity().setTitle("Home Page");

        if(alert == 1){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("You must be Logged In to access Forum")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        } else if (alert ==2){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Would you like to logout?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //sign out
                            mMainFragment.mainSignOut();

                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new LoginFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    })
                    .show();
        }
        mAuth = FirebaseAuth.getInstance();
        textViewLoginShow = view.findViewById(R.id.textViewMainFragmentLogin);
        textViewLogout = view.findViewById(R.id.textViewMainFragLogOut);


            if (mAuth.getCurrentUser() != null) {
                textViewLoginShow.setText(mAuth.getCurrentUser().getEmail() + " is Logged in");
            }

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //sign out
                mMainFragment.mainSignOut();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MainFragment())
                        .commit();
            }
        });

        return view;
    }

    interface IMainFragment{
        void mainSignOut();
    }
}