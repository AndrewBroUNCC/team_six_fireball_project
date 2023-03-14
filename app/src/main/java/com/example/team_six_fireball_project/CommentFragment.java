package com.example.team_six_fireball_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommentFragment extends Fragment implements  RecycleViewCommentAdapter.IRecycleViewCommentAdapter {

    private static final String TAG = "CommentFragment";
    FirebaseAuth mAuth;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecycleViewCommentAdapter adapter;
    String commentTitle =  "", forumID;
    CardView homeButton;
    ArrayList<Comment> commentsList = new ArrayList<>();
    TextView textViewTitle;
    ICommentFragment mCommentFragment;
    ExecutorService executorService;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;

    public static CommentFragment newInstance() {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof CommentFragment.ICommentFragment) {
            mCommentFragment = (CommentFragment.ICommentFragment) context;
        } else {
            throw new RuntimeException(context.toString());
        }
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
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        //gets list from main activity.
        //get forumID
        forumID = mCommentFragment.getForumID();
        getActivity().setTitle("Comment Page");
        homeButton = view.findViewById(R.id.viewCommentFragHomeButton);

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        recyclerView = view.findViewById(R.id.recycleViewComments);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewCommentAdapter(commentsList, this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.buttonCommentFragNewComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateCommentFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentFragment.commentToHome();
            }
        });

        textViewTitle = view.findViewById(R.id.textViewCommentFragTopicTitle);

        //implemented Thread to speed up application.
        GetDataRunnable getData = new GetDataRunnable(textViewTitle);
        executorService.execute(getData);

        //now runs in thread above
        //getData(textViewTitle);
        return view;
    }

    class GetDataRunnable implements Runnable{
        TextView textViewTitle;
        public GetDataRunnable(TextView textViewTitle){
            this.textViewTitle = textViewTitle;
        }

        @Override
        public void run() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Forum").document(forumID)
                    .collection("comments")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            commentsList.clear();
                            for (QueryDocumentSnapshot document: value) {
                                //Log.d(TAG, "onEvent: getData() = " + document.getData().toString());
                                Comment comment = document.toObject(Comment.class);
                                //Log.d(TAG, "onEvent: " + comment.commenterName);
                                commentsList.add(comment);
                                commentTitle = comment.getTopic();
                            }
                            Collections.sort(commentsList, new Comparator<Comment>() {
                                @Override
                                public int compare(Comment comment, Comment comment2) {
                                    return comment.date.compareTo(comment2.date)*-1;
                                }
                            });

                            //Log.d(TAG, "compare: list= " + commentsList);
                            textViewTitle.setText(commentTitle);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    //moved to a thread.
//    private void getData(TextView textViewTitle){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Forum").document(forumID)
//                .collection("comments")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                        commentsList.clear();
//                        for (QueryDocumentSnapshot document: value) {
//                            //Log.d(TAG, "onEvent: getData() = " + document.getData().toString());
//                            Comment comment = document.toObject(Comment.class);
//                            //Log.d(TAG, "onEvent: " + comment.commenterName);
//                            commentsList.add(comment);
//                            commentTitle = comment.getTopic();
//                        }
//                        Collections.sort(commentsList, new Comparator<Comment>() {
//                            @Override
//                            public int compare(Comment comment, Comment comment2) {
//                                return comment.date.compareTo(comment2.date)*-1;
//                            }
//                        });
//                        textViewTitle.setText(commentTitle);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//    }

    interface ICommentFragment{
        String  getForumID();
        void commentToHome();
    }
}