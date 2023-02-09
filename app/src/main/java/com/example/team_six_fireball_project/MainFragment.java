package com.example.team_six_fireball_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainFragment extends Fragment {

    FirebaseAuth mAuth;
    TextView textViewLoginShow, textViewLogout;
    public MainFragment() {
        // Required empty public constructor
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        getActivity().setTitle("Home Page");

        mAuth = FirebaseAuth.getInstance();
        textViewLoginShow = view.findViewById(R.id.textViewMainFragmentLogin);
        textViewLogout = view.findViewById(R.id.textViewMainFragLogOut);


            if (mAuth.getCurrentUser() != null) {
                textViewLoginShow.setText(mAuth.getCurrentUser().getEmail() + " is Logged in");
            }

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MainFragment())
                        .commit();
            }
        });

        return view;
    }
}