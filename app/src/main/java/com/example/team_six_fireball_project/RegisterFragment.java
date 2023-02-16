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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterFragment extends Fragment {

    final String TAG = "demo";
    private FirebaseAuth mAuth;
    IRegisterFragment mRegisterFragment;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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

    EditText editTextEmail, editTextPassword, editTextName;
    Button buttonRegister, buttonCancel;

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof RegisterFragment.IRegisterFragment) {
            mRegisterFragment = (RegisterFragment.IRegisterFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        getActivity().setTitle("Register");
        editTextEmail = view.findViewById(R.id.editTextRegisterPgEmail);
        editTextPassword = view.findViewById(R.id.editTextRegisterPgPassword);
        buttonRegister = view.findViewById(R.id.buttonRegisterPgRegister);
        buttonCancel = view.findViewById(R.id.buttonRegisterPgCancel);
        editTextName = view.findViewById(R.id.editTextRegFragName);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            //made a new thread to run registering
            @Override
            public void onClick(View view) {
                RegisterRunnable registerRunnable = new RegisterRunnable();
                new Thread(registerRunnable).start();
            }
        });

        return view;
    }

    class RegisterRunnable implements Runnable {

        @Override
        public void run() {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String name = editTextName.getText().toString();

            if(name.isEmpty()){
                Toast.makeText(getContext(), "Name is Empty", Toast.LENGTH_SHORT).show();
            }
            else if (email.isEmpty()) {
                Toast.makeText(getContext(), "Email is Empty", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(getContext(), "Password is Empty", Toast.LENGTH_SHORT).show();
            } else {
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    setData(name, mAuth.getCurrentUser().getUid(), email);

                                    Log.d(TAG, "onComplete: Logged in successfully");
                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new MainFragment())
                                            .commit();
                                } else {
                                    Log.d(TAG, "onComplete: Login Failed: message = " + task.getException().getMessage());

                                    //how to build an Alert Dialog
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle("Error")
                                            .setMessage(task.getException().getMessage())
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            }).show();
                                }
                            }
                        });
            }
        }
    }
    private void setData(String name, String user_id, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("name", name);
        user.put("userID", user_id);

        db.collection("userList")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "onSuccess: " + documentReference.toString());

                        mRegisterFragment.goToHomeFragment();
//                                getParentFragmentManager().beginTransaction()
//                                        .replace(R.id.fragment_container, new ForumsFragment())
//                                        .commit();
                            }
                        });
    }
    interface IRegisterFragment {
        void goToHomeFragment ();
    }
}