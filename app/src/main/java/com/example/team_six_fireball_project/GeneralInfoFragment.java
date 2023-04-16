package com.example.team_six_fireball_project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class GeneralInfoFragment extends Fragment {


    IGeneralInfoFragment mGeneralInfoFragment;

    public GeneralInfoFragment() {
    }


    public static GeneralInfoFragment newInstance() {
        GeneralInfoFragment fragment = new GeneralInfoFragment();
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


    //need this for interface to work.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GeneralInfoFragment.IGeneralInfoFragment) {
            mGeneralInfoFragment = (GeneralInfoFragment.IGeneralInfoFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general_info, container, false);
        getActivity().setTitle("Information");

        /* Example block for wanting an onclick event (Ex. click on text, button, image,e tc..) */
        TextView testTextView = view.findViewById(R.id.textViewTest);
        testTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGeneralInfoFragment.generalInfoAlert("Leaving FireBorn","Would you like to proceed to the Website?","https://www.theguardian.com/science/2013/nov/06/chelyabinsk-meteor-russia");
            }
        });


        Button button_Recent_Fireball_Activity = view.findViewById(R.id.button_Recent_Fireball_Activity);
        button_Recent_Fireball_Activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGeneralInfoFragment.generalInfoAlert("Leaving FireBorn","Would you like to proceed to the Website?","https://fireball.imo.net/members/imo_view/browse_events");
            }
        });

        Button button_report = view.findViewById(R.id.button_report);

        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGeneralInfoFragment.generalInfoAlert("Leaving FireBorn","Would you like to proceed to the Website?","https://ams.imo.net/members/imo/report_intro/");
            }
        });

        Button button_faq = view.findViewById(R.id.button_faq);
        button_faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGeneralInfoFragment.generalInfoAlert("Leaving FireBorn","Would you like to proceed to the Website?","https://www.amsmeteors.org/fireballs/faqf/");
            }
        });

        return view;
    }

interface IGeneralInfoFragment{
        void generalInfoAlert(String title, String message, String url);
}
}