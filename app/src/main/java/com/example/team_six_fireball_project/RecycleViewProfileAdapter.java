package com.example.team_six_fireball_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecycleViewProfileAdapter extends RecyclerView.Adapter<RecycleViewProfileAdapter.UserViewHolder> {

    private static final String TAG = "RecycleViewCommentAdapt";
    IRecycleViewProfileAdapter mRecycleViewProfileAdapter;
    ArrayList<Forum> forums;

    public RecycleViewProfileAdapter(ArrayList<Forum> data, IRecycleViewProfileAdapter adapter) {
        this.forums = data;
        mRecycleViewProfileAdapter= adapter;
    }

    @NonNull
    @Override
    public RecycleViewProfileAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_profile_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewProfileAdapter.UserViewHolder holder, int position) {
        //comment has:
        Forum forum = forums.get(position);
        holder.textViewTitle.setText(forum.getTitle());
        holder.textViewDate.setText(forum.getCreatedDate());

        holder.trashCan.setOnClickListener(view -> mRecycleViewProfileAdapter.profileDeleteTopic(forum.getForumID()));
        holder.itemView.setOnClickListener(view -> mRecycleViewProfileAdapter.openProfileSectionOfTopic(forum.getForumID()));
    }

    @Override
    public int getItemCount() {
        return this.forums.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle = itemView.findViewById(R.id.textViewProfileRecViewTopic);
        TextView textViewDate = itemView.findViewById(R.id.textViewProfileRecViewDate);
        CardView trashCan = itemView.findViewById(R.id.viewProfileFragTrashCan);

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    interface IRecycleViewProfileAdapter{
        //this is for deleting or passing info
        void profileDeleteTopic(String forumId);
        void openProfileSectionOfTopic(String forumId);
    }
}
