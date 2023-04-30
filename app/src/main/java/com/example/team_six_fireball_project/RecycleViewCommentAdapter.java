package com.example.team_six_fireball_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class RecycleViewCommentAdapter extends RecyclerView.Adapter<RecycleViewCommentAdapter.UserViewHolder> {

    private static final String TAG = "RecycleViewCommentAdapt";
    IRecycleViewCommentAdapter mRecycleViewCommentAdapter;
    ArrayList<Comment> comments;

    public RecycleViewCommentAdapter(ArrayList<Comment> data, IRecycleViewCommentAdapter adapter) {
        this.comments = data;
        mRecycleViewCommentAdapter = adapter;
    }

    @NonNull
    @Override
    public RecycleViewCommentAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_chat_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewCommentAdapter.UserViewHolder holder, int position) {
        //comment has:
        Comment commentHolder = comments.get(position);
        String comment = commentHolder.getComment();
        String commenterName = commentHolder.getCommenterName();
        String topic = commentHolder.getTopic();
        String commenterID = commentHolder.getCommenterID();
        String dateTime = commentHolder.getDate();

        //Log.d(TAG, "onBindViewHolder: " + commenterName);

        holder.textViewCommenterName.setText(commenterName);
        holder.textViewComment.setText(comment);
        holder.textViewDateTime.setText(dateTime);
    }

    @Override
    public int getItemCount() {
        return this.comments.size();
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCommenterName = itemView.findViewById(R.id.textViewCommentersName);
        TextView textViewComment = itemView.findViewById(R.id.textViewComment);
        TextView textViewDateTime = itemView.findViewById(R.id.textViewDateTime);

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    interface IRecycleViewCommentAdapter{
        //this is for deleting or passing info
    }
}
