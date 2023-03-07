package com.example.team_six_fireball_project;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.FirebaseAuthCredentialsProvider;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProfileFragment extends Fragment implements RecycleViewProfileAdapter.IRecycleViewProfileAdapter {

    private static final String TAG = "Demo";
    FirebaseAuth mAuth;
    User user;
    //this for the adapter
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecycleViewProfileAdapter adapter;
    //
    IProfileFragment mProfileFragment;

    ImageView profilePic;
    CardView homeButton;
    Button updateButton;
    TextView nameHolder, emailHolder, dateHolder, topicCount;
    ArrayList<Forum> forumList = new ArrayList<>();

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //need this for interface to work
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ProfileFragment.IProfileFragment) {
            mProfileFragment = (ProfileFragment.IProfileFragment) context;
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        profilePic = view.findViewById(R.id.imageViewProfileFragProfileImage);
        updateButton = view.findViewById(R.id.buttonProfileFragUpdate);
        homeButton = view.findViewById(R.id.viewProfileFragHomeButton);
        topicCount = view.findViewById(R.id.textViewProfileTopicNumber);
        nameHolder = view.findViewById(R.id.textViewProfileFragName);
        emailHolder = view.findViewById(R.id.textViewProfileFragEmail);
        dateHolder = view.findViewById(R.id.textViewProfileFragJoinDate);

        //recycleView stuff
        recyclerView = view.findViewById(R.id.recyclerViewProfile);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewProfileAdapter(forumList, this);
        recyclerView.setAdapter(adapter);
        //

        mProfileFragment.updateProfilePic();


        //D/Demo: Url Path: /document/image:31
        //Log.d(TAG, "Url Path: "+ mAuth.getCurrentUser().getPhotoUrl().getPath());

        //run in a thread to increase speed
        ProfileRunnable profileRunnable = new ProfileRunnable();
        new Thread(profileRunnable).start();

        Uri uri = mAuth.getCurrentUser().getPhotoUrl();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        //profilePic.setImageURI(intent.getData());

        return view;
    }


    @Override
    public void profileDeleteTopic(String forumId) {
        FirebaseFirestore dbDelete = FirebaseFirestore.getInstance();

        dbDelete.collection("Forum")
                .document(forumId)
                .collection("comments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot value) {

                        for (QueryDocumentSnapshot document: value) {
                            dbDelete.collection("Forum")
                                    .document(forumId)
                                    .collection("comments")
                                    .document(document.getId())
                                    .delete();
                        }
                        dbDelete.collection("Forum")
                                .document(forumId).delete();
                    }

                });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void openProfileSectionOfTopic(String tempForumID) {
        mProfileFragment.saveProfileArrayToMain(tempForumID);
    }

    class ProfileRunnable implements Runnable{
        @Override
        public void run()
        {
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d(TAG, "onClick: TEST");
                    mProfileFragment.profileToHome();
                }
            });
            getData();

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProfileFragment.openUpdatePopUp();
                }
            });
        }
        
        private void getData() {

            FirebaseUser currentUser = mAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ArrayList<User> userReturn = new ArrayList<>();

            db.collection("userList")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                User userDoc = document.toObject(User.class);
                                if (currentUser.getUid().compareTo(userDoc.getUserID()) == 0) {
                                    userReturn.add(userDoc);
                                    user = userReturn.get(0);
                                    break;
                                }
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {


                            String userName = mAuth.getCurrentUser().getDisplayName();
                            String userEmail = mAuth.getCurrentUser().getEmail();
                            String userJoinDate = user.getJoinDate();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nameHolder.setText(userName);
                                    emailHolder.setText(userEmail);
                                    dateHolder.setText(userJoinDate);
                                }
                            });


                            //get current user's id
                            String userId = mAuth.getCurrentUser().getUid();
                            //THIS IS BEST WAY TO SHOW AND UPDATE DATA IN THE ADAPTER OF THE RECYCLE/LIST VIEW- SO IMPORTANT!!!!
                            db.collection("Forum")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            forumList.clear(); //CLEARS LIST SO IT DOESNT RELOAD MORE ON TOP OF OLD LIST EACH UPDATE

                                            //for each loop to loop through the documents and pull out info.
                                            for (QueryDocumentSnapshot document : value) {
                                                //Log.d(TAG, "onEvent: getData() = " + document.getData().toString());
                                                Forum forum = document.toObject(Forum.class);
                                                if (forum.getUserID().compareTo(userId) == 0) {
                                                    forumList.add(forum);
                                                }
                                            }
                                            Log.d(TAG, "onEvent: " + forumList);
                                            Collections.sort(forumList, new Comparator<Forum>() {
                                                @Override
                                                public int compare(Forum forum, Forum forum2) {
                                                    return forum.createdDate.compareTo(forum2.createdDate) * -1;
                                                }
                                            });
                                            int count = adapter.getItemCount();

                                            if(count > 99){
                                                topicCount.setText("99+");
                                            } else {
                                                topicCount.setText(Integer.toString(count));
                                            }
                                            //HOW TO TELL THE ADAPTER TO UPDATE -----------------------SUPER IMPORTANT!!-----
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    });
        }
    }

    interface IProfileFragment {
        void saveProfileArrayToMain(String tempForumID);
        void profileToHome();
        void openUpdatePopUp();
        void updateProfilePic();
    }
}