package com.example.team_six_fireball_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LoginFragment extends Fragment {

    //Thread Is set up and done
    private FirebaseAuth mAuth;
    final String TAG = "demo";
    ILoginFragment mLoginFragment;
    EditText editTextEmail, editTextPassword;
    Button buttonLogin, buttonRegister;

    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;

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

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof LoginFragment.ILoginFragment) {
            mLoginFragment = (LoginFragment.ILoginFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        requireActivity().setTitle("Login");
        editTextEmail = view.findViewById(R.id.editTextLoginEmail);
        editTextPassword = view.findViewById(R.id.editTextLoginPassword);
        buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonRegister = view.findViewById(R.id.buttonRegister);

        LoginRunnable logRunnable = new LoginRunnable();
        executorService.execute(logRunnable);

        return view;
    }

    class LoginRunnable implements Runnable{

        //does not need constructor. just needs to run.

        @Override
        public void run() {


            buttonLogin.setOnClickListener(view -> loginMethod());

            //goes to register page
            buttonRegister.setOnClickListener(view -> getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterFragment())
                    .addToBackStack(null)
                    .commit());

        }

        public void loginMethod(){

            AlertDialog validate = new AlertDialog.Builder(requireActivity())
                    .setPositiveButton("Ok", (dialogInterface, i) -> {

                    }).create();
            validate.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;

            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            if (email.isEmpty()) {
                //how to build an Alert Dialog
                        validate.setTitle("Error");
                        validate.setMessage("Email is Empty");
                requireActivity().runOnUiThread(validate::show);
            } else if (password.isEmpty()) {
                //how to build an Alert Dialog
                        validate.setTitle("Error");
                        validate.setMessage("Password is Empty");
                requireActivity().runOnUiThread(validate::show);
            } else {
                //calls firebase instance
                mAuth = FirebaseAuth.getInstance();
                //now to login = call V
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity(), task -> {
                            //////now to see if task is successful
                            if (task.isSuccessful()){
                                Log.d(TAG, "onComplete: Logged in successfully");
                                //how to get currentUser if login is successful. if it isnt it will be null
                                //mAuth.getCurrentUser();
                                //you can get stuff from user using = V
                                //mAuth.getCurrentUser().getDisplayName();

                                mLoginFragment.showSuccessScreenLogin(getContext());

                                //in main activity
                                mLoginFragment.loginSignIn();

                            } else {
                                validate.setTitle("Login Failed");
                                validate.setMessage(Objects.requireNonNull(task.getException()).getMessage());
                                requireActivity().runOnUiThread(validate::show);
                            }
                        });

            }
        }
    }
    interface ILoginFragment{
        void loginSignIn();
        void showSuccessScreenLogin(Context context);
    }
}