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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    final String TAG = "demo";
    private FirebaseAuth mAuth;
    IRegisterFragment mRegisterFragment;
    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;

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

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
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
                        .addToBackStack(null)
                        .commit();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            //made a new thread to run registering
            @Override
            public void onClick(View view) {
                RegisterRunnable registerRunnable = new RegisterRunnable();
                executorService.execute(registerRunnable);
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
                //how to build an Alert Dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Name is Empty")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
            else if (email.isEmpty()) {
                //how to build an Alert Dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Email is Empty")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            } else if (password.isEmpty()) {
                //how to build an Alert Dialog
                new AlertDialog.Builder(getActivity())
                        .setTitle("Error")
                        .setMessage("Password is Empty")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            } else {
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    setData(name, mAuth.getCurrentUser().getUid(), email);

                                    //HOW TO UPDATE USER PROFILE
//                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                                            .setDisplayName("Jane Q. User")
//                                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
//                                            .build();
//
//                                    user.updateProfile(profileUpdates)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Log.d(TAG, "User profile updated.");
//                                                    }
//                                                }
//                                            });

                                    //UPDATE user display name on Auth user side
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();
                                    user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            //Log.d(TAG, "onComplete: User has been registered successfully");
                                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                                    .replace(R.id.fragment_container, new MainFragment())
                                                                    .addToBackStack(null)
                                                                    .commit();
                                                        }
                                                    });
                                } else {
                                    //Log.d(TAG, "onComplete: Login Failed: message = " + task.getException().getMessage());

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

        private void setData(String name, String user_id, String email) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String joinDate = mRegisterFragment.getCurrentDateRegisterFrag();

            HashMap<String, Object> user = new HashMap<>();
            user.put("email", email);
            user.put("joinDate", joinDate);
            user.put("name", name);
            user.put("uri", null);
            user.put("userID", user_id);

            db.collection("userList")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                    }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            mRegisterFragment.goToHomeFragment();
                        }
                    });
        }
    }

    interface IRegisterFragment {
        void goToHomeFragment ();
        String getCurrentDateRegisterFrag();
    }
}