package com.example.team_six_fireball_project;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ContactUsFragment extends Fragment {

    CardView contactPicture;
    EditText editTextUserName, editTextUserSubject, editTextMsg;
    Button submitButton;

    public ContactUsFragment() {
    }
    public static ContactUsFragment newInstance() {
        ContactUsFragment fragment = new ContactUsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        editTextMsg = view.findViewById(R.id.editTextContactFragEmailMessage);
        editTextUserName = view.findViewById(R.id.editTextContactFragUserName);
        editTextUserSubject = view.findViewById(R.id.editTextContactFragUserSubject);
        submitButton = view.findViewById(R.id.buttonContactFragSubmit);
        contactPicture = view.findViewById(R.id.cardViewContactUsFragImage);

        crossFade();

        //code here
        getActivity().setTitle("Support Page");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            editTextUserName.setText(firebaseUser.getDisplayName());
        }



        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] supportEmail = {"abrow359@uncc.edu"};
                String message =  editTextMsg.getText().toString();
                String subject = editTextUserSubject.getText().toString();

                if (message == null || message.isEmpty()) {
                    AlertDialog validate = new AlertDialog.Builder(getActivity())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setTitle("Error").setMessage("Message is Empty.").create();
                } else if (subject == null || subject.isEmpty()){
                        AlertDialog validate = new AlertDialog.Builder(getActivity())
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).setTitle("Error").setMessage("Subject is Empty.").create();
                    } else {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, supportEmail);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose an email client"));

                }
            }
        });
        return view;
    }

    private void crossFade() {

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        contactPicture.setAlpha(0f);
        contactPicture.setVisibility(View.VISIBLE);


        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        contactPicture.animate()
                .alpha(1f)
                .setDuration(5000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contactPicture.animate()
                                .alpha(0f)
                                .setDuration(5000)
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                crossFade();
                                            }
                                        });
                    }
                });
    }

}