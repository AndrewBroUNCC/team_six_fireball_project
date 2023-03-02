package com.example.team_six_fireball_project;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RecycleViewForumsAdapter extends RecyclerView.Adapter<RecycleViewForumsAdapter.UserViewHolder> {

    final String TAG = "demo";
    FirebaseAuth mAuth;
    IRecycleViewForumsAdapter mRecycleViewForumsAdapter;
    ArrayList<Forum> forums;
    //RecycleViewExpensesFragAdapter.IRecycleViewExpensesFragAdapter mRecycleViewExpensesFragAdapter;

    public RecycleViewForumsAdapter(ArrayList<Forum> data, IRecycleViewForumsAdapter adapter) {
        //this.mRecycleViewExpensesFragAdapter = adapter;
        this.forums = data;
        mRecycleViewForumsAdapter = adapter;
        //mRecycleViewExpensesFragAdapter = adapter;

    }

    @NonNull
    @Override
    public RecycleViewForumsAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_forum_item, parent, false);
        RecycleViewForumsAdapter.UserViewHolder userViewHolder = new RecycleViewForumsAdapter.UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewForumsAdapter.UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Forum forum = forums.get(position);
        String forumId = forum.getForumID();
        String title = forum.getTitle();
        String creator = forum.getCreator();
        String date = forum.getCreatedDate();

        holder.textViewTitle.setText(title);
        holder.textViewCreator.setText(creator);
        holder.textViewDate.setText(date);

        mAuth = FirebaseAuth.getInstance();
        //Log.d(TAG, "onBindViewHolder: user LOGGED IN: id test = " + mAuth.getCurrentUser().getUid());
        //Log.d(TAG, "onBindViewHolder: user id of post = " + forum.userID);

        if (mAuth.getCurrentUser().getUid().equals(forum.userID)){
            holder.imageViewTrash.setVisibility(View.VISIBLE);
        } else{
            holder.imageViewTrash.setVisibility(View.INVISIBLE);
        }

        holder.imageViewTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: position " + position );
                mRecycleViewForumsAdapter.deleteThisExpense(forumId);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecycleViewForumsAdapter.openCommentSectionOfTopic(forum.getForumID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.forums.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        //TextView textView = itemView.findViewById(R.id.textViewSortText);
        // View sortViewContainer = itemView.findViewById(R.id.viewSortContainer);
        TextView textViewTitle = itemView.findViewById(R.id.textViewForumViewTitle);
        TextView textViewCreator = itemView.findViewById(R.id.textViewForumViewCreator);
        TextView textViewDate = itemView.findViewById(R.id.textViewForumViewDate);
        CardView imageViewTrash = itemView.findViewById(R.id.imageViewTrashDelete);

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    interface IRecycleViewForumsAdapter{
        void deleteThisExpense(String forumId);
        void openCommentSectionOfTopic(String forumID);
    }
}