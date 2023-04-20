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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    int alert;
    FirebaseAuth mAuth;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;
    ExecutorService executorService;
    IMainFragment mMainFragment;
    TextView usernameTextView;
    TextView logLabelTextView;
    TextView logTitleTextView, contactUs;
    CardView forumContainer;
    CardView profileContainer;
    CardView loginContainer, logoutContainer;
    CardView interMapContainer;
    CardView graphContainer;
    CardView genInfoContainer;
    AlertDialog validate;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        usernameTextView = view.findViewById(R.id.home_username_txt);
        logLabelTextView = view.findViewById(R.id.home_log_label_text);
        logTitleTextView = view.findViewById(R.id.home_log_title_text);
        forumContainer = view.findViewById(R.id.home_forum_btn_container);
        profileContainer = view.findViewById(R.id.home_profile_btn_container);
        loginContainer = view.findViewById(R.id.home_login_btn_container2);
        logoutContainer = view.findViewById(R.id.home_logout_btn_container2);
        interMapContainer = view.findViewById(R.id.home_map_btn_container);
        graphContainer = view.findViewById(R.id.home_graph_btn_container);
        genInfoContainer = view.findViewById(R.id.home_geninfo_btn_container);
        contactUs = view.findViewById(R.id.textViewMainFragContactLink);

        validate = new AlertDialog.Builder(requireActivity()).setPositiveButton("Error", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        validate.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;

        //has to be done in main thread.
        getActivity().setTitle("Home Page");

        //take weight off main thread.
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        MainFragRunnable mainFragRunnable = new MainFragRunnable();
        executorService.execute(mainFragRunnable);

        return view;
    }

    class MainFragRunnable implements Runnable{

        @Override
        public void run() {
            //determine the display for the hello banner and login/logout button based on
            //runnable
            if (mAuth.getCurrentUser() != null){
                usernameTextView.setText("Welcome, " + mAuth.getCurrentUser().getDisplayName());
                forumContainer.setVisibility(View.VISIBLE);
                profileContainer.setVisibility(View.VISIBLE);
                loginContainer.setVisibility(View.INVISIBLE);
                logoutContainer.setVisibility(View.VISIBLE);
            }
            else {
                usernameTextView.setText("Welcome, Guest!");
                forumContainer.setVisibility(View.INVISIBLE);
                profileContainer.setVisibility(View.INVISIBLE);
                loginContainer.setVisibility(View.VISIBLE);
                logoutContainer.setVisibility(View.INVISIBLE);
            }

            //go to contactUs Fragment
            contactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new ContactUsFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });

            //onclick events for all button clicks in the home page
            forumContainer.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() != null) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new ForumsFragment())
                            .addToBackStack(null)
                            .commit();
                }else {
                    alert = 1;
                    handleAlert();
                }
            });

            profileContainer.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() != null) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .addToBackStack(null)
                            .commit();
                }else {
                    alert = 3;
                    handleAlert();
                }
            });

            loginContainer.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() == null) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new LoginFragment())
                            .addToBackStack(null)
                            .commit();
                }else {
                    alert = 2;
                    handleAlert();
                }
            });

            logoutContainer.setOnClickListener(v -> {
                if (mAuth.getCurrentUser() == null) {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new LoginFragment())
                            .addToBackStack(null)
                            .commit();
                }else {
                    alert = 2;
                    handleAlert();
                }
            });

            interMapContainer.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new MapsFragment())
                            .addToBackStack(null)
                            .commit()
            );

            graphContainer.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new GraphFragment())
                            .addToBackStack(null)
                            .commit()
            );

            genInfoContainer.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, new GeneralInfoFragment())
                            .addToBackStack(null)
                            .commit()
            );

            handleAlert();
        }
    }

    //handling alert. validation
    public void handleAlert(){
        if(alert == 1){
                    validate.setTitle("Error");
                    validate.setMessage("You must be Logged In to access Forum");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    validate.show();

                }
            });
        } else if (alert ==2){
            AlertDialog validate2 = new AlertDialog.Builder(requireActivity())
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
                                    .replace(R.id.fragment_container, new MainFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }).create();
                    validate2.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;
                    validate2.show();

        } else if (alert ==3){
            AlertDialog validate3 = new AlertDialog.Builder(requireActivity())
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
                    }).create();
            validate3.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;
            validate3.show();
        }
    }

    //interface
    interface IMainFragment{
        void mainSignOut();
    }
}