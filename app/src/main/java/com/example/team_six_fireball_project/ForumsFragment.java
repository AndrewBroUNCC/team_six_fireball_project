package com.example.team_six_fireball_project;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ForumsFragment extends Fragment implements RecycleViewForumsAdapter.IRecycleViewForumsAdapter {

    private static final String TAG = "ForumsFragment";
    FirebaseAuth mAuth;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecycleViewForumsAdapter adapter;
    ArrayList<Forum> forumList = new ArrayList<>();
    ArrayList<Comment> commentsList = new ArrayList<>();
    String id;
    int number;

    IForumsFragment mForumsFragment;

    public ForumsFragment() {
        // Required empty public constructor
    }

    public static ForumsFragment newInstance() {
        ForumsFragment fragment = new ForumsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ForumsFragment.IForumsFragment) {
            mForumsFragment = (ForumsFragment.IForumsFragment) context;
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
        View view = inflater.inflate(R.layout.fragment_forums, container, false);

        mAuth = FirebaseAuth.getInstance();
        getActivity().setTitle("Forum Page");

        //Sorting by date
//        Collections.sort(forumList, new Comparator<Forum>() {
//            @Override
//            public int compare(Forum forum, Forum forum2) {
//                return forum.createdDate.compareTo(forum2.createdDate)*-1;
//            }
//        });

        recyclerView = view.findViewById(R.id.recycleViewForums);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewForumsAdapter(forumList, this);
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.buttonForumsNewForum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateForumFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        //TODO: run in a thread to increase speed
        getData();

        return view;
    }

    @Override
    public void deleteThisExpense(int position) {
        FirebaseFirestore dbDelete = FirebaseFirestore.getInstance();

        ArrayList<String> idDelete = new ArrayList<>();

        dbDelete.collection("Forum")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {
                        int i = 0;
                        for (QueryDocumentSnapshot document : value) {
                            if(position == i) {
                                idDelete.add((String) document.getId());
                            }
                            i++;
                        }
                        dbDelete.collection("Forum")
                                .document(idDelete.get(0))
                                .collection("comments")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot value2) {

                                        for (QueryDocumentSnapshot document: value2) {
                                            dbDelete.collection("Forum")
                                                    .document(idDelete.get(0))
                                                    .collection("comments")
                                                    .document(document.getId())
                                                    .delete();
                                            Log.d(TAG, "onSuccess: " + document.getId());
                                        }
                                        dbDelete.collection("Forum")
                                                .document(idDelete.get(0)).delete();

                                    }
                                });

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void openCommentSectionOfTopic(String tempForumID) {
        //Log.d(TAG, "openCommentSectionOfTopic: " + position);
        mForumsFragment.saveCommentArrayToMain(tempForumID);
    }

    private void getData(){
        //get the instance of FIRE STORE/////////////////GET THE DATA BASE---IMPORTANT
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //THIS IS BEST WAY TO SHOW AND UPDATE DATA IN THE ADAPTER OF THE RECYCLE/LIST VIEW- SO IMPORTANT!!!!
        db.collection("Forum")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        forumList.clear(); //CLEARS LIST SO IT DOESNT RELOAD MORE ON TOP OF OLD LIST EACH UPDATE

                        //for each loop to loop through the documents and pull out info.
                        for (QueryDocumentSnapshot document: value) {
                            //Log.d(TAG, "onEvent: getData() = " + document.getData().toString());
                            Forum forum = document.toObject(Forum.class);
                            forumList.add(forum);
                        }
                        Collections.sort(forumList, new Comparator<Forum>() {
                            @Override
                            public int compare(Forum forum, Forum forum2) {
                                return forum.createdDate.compareTo(forum2.createdDate) *-1;
                            }
                        });
                        //HOW TO TELL THE ADAPTER TO UPDATE -----------------------SUPER IMPORTANT!!-----
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    interface IForumsFragment{
        //saves list to main activity then goes to comment page
        void saveCommentArrayToMain(String tempForumID);
    }
}