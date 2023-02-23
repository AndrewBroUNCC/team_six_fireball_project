package com.example.team_six_fireball_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    final String TAG = "demo";
    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        getActivity().setTitle("Login");
        editTextEmail = view.findViewById(R.id.editTextLoginEmail);
        editTextPassword = view.findViewById(R.id.editTextLoginPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRunnable logRunnable = new LoginRunnable();
                new Thread(logRunnable).start();

            }
        });

        //goes to register page
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
            }
        });

        return view;
    }

    class LoginRunnable implements Runnable{

        //does not need constructor. just needs to run.

        @Override
        public void run() {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(getContext(), "Email is Empty", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(getContext(), "Password is Empty", Toast.LENGTH_SHORT).show();
            } else {
                //calls firebase instance
                mAuth = FirebaseAuth.getInstance();
                //now to login = call V
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //////now to see if task is successful
                                if (task.isSuccessful()){
                                    Log.d(TAG, "onComplete: Logged in successfully");
                                    //how to get currentUser if login is successful. if it isnt it will be null
                                    mAuth.getCurrentUser();
                                    //you can get stuff from user using = V
                                    mAuth.getCurrentUser().getDisplayName();
                                    mAuth.getCurrentUser().getUid();
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new MainFragment())
                                            .commit();

                                } else {
                                    Log.d(TAG, "onComplete: Login Failed: message = " + task.getException().getMessage());
                                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    }
}