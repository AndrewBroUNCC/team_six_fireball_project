package com.example.team_six_fireball_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    int alert;
    FirebaseAuth mAuth;
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
        mAuth = FirebaseAuth.getInstance();
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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //determines the display for the hello banner and login/logout button based on
        //if the user is logged in or not
        TextView usernameTextView = view.findViewById(R.id.home_username_txt);
        TextView logLabelTextView = view.findViewById(R.id.home_log_label_text);
        TextView logTitleTextView = view.findViewById(R.id.home_log_title_text);
        if (mAuth.getCurrentUser() != null){
            usernameTextView.setText("Welcome, " + mAuth.getCurrentUser().getDisplayName());
            logLabelTextView.setText("");
            logTitleTextView.setText("Logout");
        }
        else {
            usernameTextView.setText("Welcome, Guest!");
            logLabelTextView.setText("Login or Register");
            logTitleTextView.setText("Login");
        }

        //onclick events for all button clicks in the home page
        CardView forumContainer = view.findViewById(R.id.home_forum_btn_container);
        forumContainer.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ForumsFragment())
                        .addToBackStack(null)
                        .commit();
            }else {
                alert = 1;
                handleAlert();
            }
        });

        CardView profileContainer = view.findViewById(R.id.home_profile_btn_container);
        profileContainer.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }else {
                alert = 3;
                handleAlert();
            }
        });

        CardView loginContainer = view.findViewById(R.id.home_login_btn_container);
        loginContainer.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .addToBackStack(null)
                        .commit();
            }else {
                alert = 2;
                handleAlert();
            }
        });

        CardView interMapContainer = view.findViewById(R.id.home_map_btn_container);
        interMapContainer.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapsFragment())
                        .addToBackStack(null)
                        .commit()
        );

        CardView graphContainer = view.findViewById(R.id.home_graph_btn_container);
        graphContainer.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GraphFragment())
                        .addToBackStack(null)
                        .commit()
        );

        CardView genInfoContainer = view.findViewById(R.id.home_geninfo_btn_container);
        genInfoContainer.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GeneralInfoFragment())
                        .addToBackStack(null)
                        .commit()
            );

        //testing date
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        Log.d(TAG, "onCreateView: "+ (dateFormat.format(date)));
//        String date = dateFormat.format(new Date());
//        Log.d(TAG, "onCreateView: " + date);

        getActivity().setTitle("Home Page");

        handleAlert();

        return view;
    }

    interface IMainFragment{
        void mainSignOut();
    }

    public void handleAlert(){
        if(alert == 1){
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Error")
                    .setMessage("You must be Logged In to access Forum")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        } else if (alert ==2){
            new AlertDialog.Builder(requireActivity())
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
        } else if (alert ==3){
            new AlertDialog.Builder(requireActivity())
                    .setTitle("Must be logged in")
                    .setMessage("Would you like to go to login screen?")
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
    }
}