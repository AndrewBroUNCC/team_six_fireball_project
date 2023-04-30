package com.example.team_six_fireball_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateCommentFragment extends Fragment {

    ICreateCommentFragment mCreateCommentFragment;
    TextView textViewMultiLine;
    Button buttonCreate, buttonCancel;
    FirebaseAuth mAuth;
    String forumID = "";

    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;
    AlertDialog validate;

    //FOR CREATING A COMMENT. (order is important)
    String comment;
    String commenterName;
    String topic;
    String commenterID;
    String date;

    public CreateCommentFragment() {
        // Required empty public constructor
    }

    public static CreateCommentFragment newInstance() {
        CreateCommentFragment fragment = new CreateCommentFragment();
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

        if (context instanceof CreateCommentFragment.ICreateCommentFragment) {
            mCreateCommentFragment = (CreateCommentFragment.ICreateCommentFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_comment, container, false);

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        textViewMultiLine = view.findViewById(R.id.editTextCreateCommentFragComment);
        buttonCreate = view.findViewById(R.id.buttonCreateCommentFragCreateComment);
        buttonCancel = view.findViewById(R.id.buttonCreateCommentFragCancel);
        mAuth = FirebaseAuth.getInstance();

        validate = new AlertDialog.Builder(requireActivity()).setPositiveButton("Ok", (dialogInterface, i) -> {

        }).create();
        validate.getWindow().getAttributes().windowAnimations = R.style.AnimationSlide;

        CreateCommentRunnable createCommentRunnable = new CreateCommentRunnable();
        executorService.execute(createCommentRunnable);

        return view;
    }

    class CreateCommentRunnable implements Runnable{

        @Override
        public void run() {

            buttonCreate.setOnClickListener(view -> {
                if (textViewMultiLine.getText().toString().isEmpty()){
                    validate.setTitle("Error");
                    validate.setMessage("The Comment Field is Empty");
                    requireActivity().runOnUiThread(() -> validate.show());

                } else if(textViewMultiLine.getText().toString().length() > 100){
                    validate.setTitle("Error");
                    validate.setMessage("The Comment Field limit is 100");
                    requireActivity().runOnUiThread(() -> validate.show());
                }
                else {
                    forumID = mCreateCommentFragment.getCreateForumID();
                    CreateRunnable runnable = new CreateRunnable();
                    executorService.execute(runnable);
                }
            });

            buttonCancel.setOnClickListener(view -> getParentFragmentManager().popBackStack());
//----------------------stop runnable---------------------------------------
        }
    }

    class CreateRunnable implements Runnable{


        public CreateRunnable() {
        }

        @Override
        public void run() {
            comment = textViewMultiLine.getText().toString();
            commenterID = mAuth.getUid();
            getData();
        }

        private void getData(){ //get users

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("userList")
                    .get()
                    .addOnSuccessListener(value -> {
                        for (QueryDocumentSnapshot document : value) {
                            User user = document.toObject(User.class);
                            if(user.userID.equals(mAuth.getUid())){
                                commenterName = user.getName();
                            }
                        }

                        db.collection("Forum").document(forumID)
                                .get()
                                .addOnSuccessListener(value1 -> {
                                    Forum forum = value1.toObject(Forum.class);
                                    assert forum != null;
                                    topic = forum.getDescription();
                                    setData();
                                });
                    });
        }

        private void setData() {
            FirebaseFirestore db2 = FirebaseFirestore.getInstance();

            date = mCreateCommentFragment.getCreateDate();

            HashMap<String, Object> setComment = new HashMap<>();
            setComment.put("comment", comment);
            setComment.put("commenterID", commenterID);
            setComment.put("commenterName", commenterName);
            setComment.put("date", date);
            setComment.put("topic", topic);

            db2.collection("Forum").document(forumID)
                    .collection("comments")
                    .add(setComment)
                    .addOnSuccessListener(documentReference -> getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new CommentFragment())
                            .commit());

        }

    }

    interface ICreateCommentFragment {
        String getCreateForumID();
        String getCreateDate();
    }
}