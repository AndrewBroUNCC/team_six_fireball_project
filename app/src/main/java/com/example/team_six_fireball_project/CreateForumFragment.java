package com.example.team_six_fireball_project;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class CreateForumFragment extends Fragment {

    private static final String TAG = "CreateForumFragment";
    EditText editTextForumTitle, editTextForumDescription;
    FirebaseAuth mAuth;
    String date, title, creator, description, userID, forumID, creatorId;
    Calendar c;
    Button buttonCreateForumSubmit;
    TextView buttonCreateForumCancel;
    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;
    AlertDialog validate;

    public CreateForumFragment() {
        // Required empty public constructor
    }

    public static CreateForumFragment newInstance() {
        CreateForumFragment fragment = new CreateForumFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_forum, container, false);

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        requireActivity().setTitle("Forum Page");
        mAuth = FirebaseAuth.getInstance();

        editTextForumTitle = view.findViewById(R.id.editTextCreateForumTitle);
        editTextForumDescription = view.findViewById(R.id.editTextCreateForumComment);
        buttonCreateForumSubmit = view.findViewById(R.id.buttonCreateForumSubmit);
        buttonCreateForumCancel = view.findViewById(R.id.textViewCreateForumCancel);

        validate = new AlertDialog.Builder(requireActivity())
                .setPositiveButton("Ok", (dialogInterface, i) -> {

                }).create();
        validate.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;

        CreateForumMainRunnalbe createForumMainRunnalbe = new CreateForumMainRunnalbe();
        executorService.execute(createForumMainRunnalbe);

        return view;
    }

    class CreateForumMainRunnalbe implements Runnable{

        @Override
        public void run() {
            buttonCreateForumSubmit.setOnClickListener(view -> {
                CreateForumRunnable createForumRunnable = new CreateForumRunnable();
                executorService.execute(createForumRunnable);
            });

            buttonCreateForumCancel.setOnClickListener(view -> getParentFragmentManager().popBackStack());
        }
    }

    class CreateForumRunnable implements Runnable {

        @Override
        public void run() {
            String amPm;

            if (editTextForumTitle.getText().toString().isEmpty()){
                //Toast.makeText(getActivity(), getString(R.string.title_message_filler), Toast.LENGTH_SHORT).show();
                requireActivity().runOnUiThread(() -> {
                            validate.setTitle("Error");
                            validate.setMessage("The Title Field is Empty");
                    requireActivity().runOnUiThread(() -> validate.show());
                });

            } else if (editTextForumTitle.getText().toString().length() > 15) {
                requireActivity().runOnUiThread(() -> {
                            validate.setTitle("Error");
                            validate.setMessage("The Title Field must be under 15 chars");
                    requireActivity().runOnUiThread(() -> validate.show());                    });

            } else if (editTextForumDescription.getText().toString().isEmpty()){
                //Toast.makeText(getActivity(), getString(R.string.description_filler_2), Toast.LENGTH_SHORT).show();
                requireActivity().runOnUiThread(() -> {
                            validate.setTitle("Error");
                            validate.setMessage("The Comment Field is Empty");
                    requireActivity().runOnUiThread(() -> validate.show());
                });

            } else {
                ///////////////////////////////////////////////////////CURRENT DATE GETTER////////////////////////////IMPORTANT DATE
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                date = dateFormat.format(new Date());

                userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                title = editTextForumTitle.getText().toString();
                description = editTextForumDescription.getText().toString();

                //get creator and user id and sets them.
                getData();

            }
        }//end run

        private void setData(String date, String title, String creator, String description, String userID, String forumID) {
            FirebaseFirestore db2 = FirebaseFirestore.getInstance();

            HashMap<String, Object> user = new HashMap<>();
            user.put("createdDate", date);
            user.put("creator", creator);
            user.put("description", description);
            user.put("forumID", forumID);
            user.put("title", title);
            user.put("userID", userID);

            HashMap<String, Object> comment = new HashMap<>();
            comment.put("comment", description);
            comment.put("commenterID", userID);
            comment.put("commenterName", creator);
            comment.put("date", date);
            comment.put("topic", title);


            db2.collection("Forum")
                    .add(user)
                    .addOnSuccessListener(documentReference -> {

                        //updating forum with forum id
                        user.put("forumID", documentReference.getId());
                        db2.collection("Forum").document(documentReference.getId())
                                .update(user);

                        db2.collection("Forum").document(documentReference.getId())
                                .collection("comments")
                                .add(comment)
                                .addOnSuccessListener(documentReference1 -> getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, new ForumsFragment())
                                        .addToBackStack(null)
                                        .commit());
                    }).addOnFailureListener(e -> {
                        validate.setTitle("Error");
                        validate.setMessage(e.getMessage());
                        requireActivity().runOnUiThread(() -> validate.show());
                    });
        }
        private void getData(){ //get users
            //get the instance of FIRE STORE/////////////////GET THE DATA BASE---IMPORTANT
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("userList")
                    .get()
                    .addOnSuccessListener(value -> {
                        for (QueryDocumentSnapshot document : value) {
                            //Log.d(TAG, "onEvent: getData() = --------------- " + document.getData().toString());
                            User user = document.toObject(User.class);
                            //forumList.add(forum);
                            if(user.userID.equals(mAuth.getUid())){
                                creator = user.getName();
                                userID = user.getUserID();
                            }
                        }
                        setData(date, title, creator, description, userID, forumID);
                    }).addOnFailureListener(e -> {
                        validate.setTitle("Error");
                        validate.setMessage(e.getMessage());
                        requireActivity().runOnUiThread(() -> validate.show());
                    });
        }

    }
}